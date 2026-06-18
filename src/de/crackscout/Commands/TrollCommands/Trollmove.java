package de.crackscout.Commands.TrollCommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;

import de.crackscout.AdminBot.Main;
import de.crackscout.Managers.AuthManager;
import de.crackscout.Managers.ConfigManager;
import de.crackscout.Managers.Debug;

public class Trollmove {

        // note to myself: this command is still in development and not fully functional yet, but the basic structure is there. The command syntax will be as follows:
        // !trollmove <clientName/id> <amount/duration>
        // if the second argument is an integer, the client will be moved back and forth between channels the specified amount of times. If the second argument is a time indicator (e.g. 10s, 5m, 1h), 
        // the client will be moved back and forth between channels for the specified duration. The command will ignore clients in certain channels, groups or with certain IDs to prevent abuse. 
        // The command will also check if the target client is currently in a channel before attempting to move them.
        // example command: !trollmove JohnDoe 10s

        // currently not moving the target client, but the command is being parsed and the target client is being identified correctly. The moving part has to be fixed in the future.

        static TS3Api api = Main.api;
        
        static List<Channel> validChannels = new ArrayList<>(); // list of channels the client can be moved to

        
		public static String msg_notarget = ConfigManager.loadProp("trollmove.no.target", "messages.properties");
		public static String msg_ignored = ConfigManager.loadProp("trollmove.ignored", "messages.properties");

        static int[] ignoredChannelIds = ConfigManager.loadIntArray("trollmove.ignored.channels", "config.properties");
        static int[] ignoredClientIds = ConfigManager.loadIntArray("trollmove.ignored.clients", "config.properties");
        static int[] ignoredGroupIds = ConfigManager.loadIntArray("trollmove.ignored.groups", "config.properties");

        public static Map<Integer, Thread> activeTrollMoves = new HashMap<>(); // map to keep track of active trollmove threads for each client

	   
		public static void load(){
			// Get our own client ID by running the "whoami" command
			int clientId = api.whoAmI().getId();
		
			// Listen to chat in the channel the query is currently in
			// As we never changed the channel, this will be the default channel of the server
			api.registerEvent(TS3EventType.TEXT_PRIVATE, 0);
		
			// Register the event listener
			api.addTS3Listeners(new TS3EventAdapter() {
		
				@Override
				public void onTextMessage(TextMessageEvent e) {
					// Only react to private messages not sent by the query itself
					if (e.getTargetMode() != TextMessageTargetMode.SERVER && e.getInvokerId() != clientId) {
						String message = e.getMessage().toLowerCase();
                        String[] args = message.split(" ");
						
						ClientInfo client = api.getClientByUId(e.getInvokerUniqueId());
						if(!AuthManager.auth(client)) {
							Debug.err("auth denied!");
							return;
						}
						/* 
						 * Command syntax:
						 *  1. !trollmove <clientName/id> <amount/duration>
						 */
		
						if (message.equals("!trollmove")) {
                            api.sendPrivateMessage(e.getInvokerId(), "Usage: !trollmove <clientName/id> <amount/duration>");
                        } else if (message.startsWith("!trollmove ")) {
                            if(args.length < 3) {
                                api.sendPrivateMessage(e.getInvokerId(), "Usage: !trollmove <clientName/id> <amount/duration>");
                                return;
                            }

                            Client targetClient; // now local instead of static                           
                            if(!isInteger(args[1])) {
                                targetClient = api.getClientByNameExact(args[1], true);
                            } else {
                                targetClient = api.getClientInfo(Integer.parseInt(args[1]));
                            }

                            if(targetClient == null) {
                                api.sendPrivateMessage(e.getInvokerId(), msg_notarget);
                                return;
                            }

/*                            if(targetClient.getId() == clientId) {
                                api.sendPrivateMessage(e.getInvokerId(), "You cannot move yourself.");
                                return;
                            }
*/
                            if(isIgnored(targetClient)) {
                                api.sendPrivateMessage(e.getInvokerId(), msg_ignored);
                                return;
                            }
                            if(targetClient.getChannelId() == 0) {
                                api.sendPrivateMessage(e.getInvokerId(), "The target client is not in a channel.");
                                return;
                            }


							
							api.sendPrivateMessage(e.getInvokerId(), "INFO: target client identified as " + targetClient.getNickname() + " with ID " + targetClient.getId()); //removeme
							if(isInteger(args[2])) {
                                int amount = Integer.parseInt(args[2]);
                                validadChannlIds(targetClient.getChannelId(), api.getChannels());
                                for(int i = 0; i < amount; i++) {
                                    moveClientToRandomChannel(targetClient);     
                            }
                            } else {
                                validadChannlIds(targetClient.getChannelId(), api.getChannels()); 
                                // read out the time indicator and parse the duration in seconds
                                int duration = Integer.parseInt(args[2].substring(0, args[2].length() - 1));
                                if(args[2].endsWith("s")) {
                                    duration = Integer.parseInt(args[2].substring(0, args[2].length() - 1));
                                } else if(args[2].endsWith("m")) {
                                    duration = Integer.parseInt(args[2].substring(0, args[2].length() - 1)) * 60;
                                } else if(args[2].endsWith("h")) {
                                    duration = Integer.parseInt(args[2].substring(0, args[2].length() - 1)) * 3600;
                                }
                                final int durationSeconds = duration;
                                new Thread(() -> {
                                    activeTrollMoves.put(targetClient.getId(), Thread.currentThread());
                                    long endTime = System.currentTimeMillis() + durationSeconds * 1000L;
                                    while(System.currentTimeMillis() < endTime) {
                                        moveClientToRandomChannel(targetClient);                                    
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException ex) {
                                            Thread.currentThread().interrupt();
                                            activeTrollMoves.remove(targetClient.getId());
                                            break;
                                        }
                                    }
                                }).start();
                            }
						} 
					}
				}

                private void moveClientToRandomChannel(Client targetClient) {
                     if (!validChannels.isEmpty()) {
                         validChannels.removeIf(channel -> channel.getId() == targetClient.getChannelId()); // remove the current channel from the list of valid channels
                         Channel randomChannel = validChannels.get(new Random().nextInt(validChannels.size()));
                         System.out.println("INFO: Moving client " + targetClient.getNickname() + " to channel " + randomChannel.getName()); //removeme
                         api.moveClient(targetClient.getId(), randomChannel.getId());
                         // i some how still get already member of channel error, 
                         // even though the target channel is removed from the list of valid channels. 
                         // and there multiple checks troughout the whole class to just check for this simple thing, 
                         // maybe the channel list is not updated after moving the client? need to check that in the future.
                         // generally speaking its working most of the time, but sometimes it tries to move the client to the same channel, which results in an error. need to fix that in the future.
                         // gonna push this as it is for now, since the basic functionality is there 
                     }
                }

                private void validadChannlIds(int currentChannelId, List<Channel> channels) {
                     validChannels.clear();
                    for (Channel channel : channels) {
                         String name = channel.getName();
                        boolean isSpacer = name.matches("^\\[(?:[rcl]?spacer\\d*)\\].*");
                         if (!isSpacer && channel.getId() != currentChannelId) {
                             validChannels.add(channel);
                         }
                     }
                }

                public boolean isInteger(String s) {
                        try { 
                            Integer.parseInt(s); 
                        } catch(NumberFormatException e) { 
                            return false; 
                        } catch(NullPointerException e) {
                            return false;
                        }
                        // only got here if we didn't return false
                        return true;
                }
                private boolean isIgnored(Client targetClient) {
                    for(int channelId : ignoredChannelIds) {
                        if(targetClient.getChannelId() == channelId) {
                            return true;
                        }
                    }
                    for(int clientId : ignoredClientIds) {
                        if(targetClient.getId() == clientId) {
                            return true;
                        }
                    }
                    for(int groupId : ignoredGroupIds) {
                        for(int clientGroupId : targetClient.getServerGroups()) {
                            if(clientGroupId == groupId) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
			});
		}
	}




/** 
 *
 * @author Joel Rzepka - crackscout.de
 *
 * @date 23.53.2026 - 05:21:54
 *
 */