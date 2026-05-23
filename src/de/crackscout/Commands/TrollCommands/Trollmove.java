package de.crackscout.Commands.TrollCommands;

import java.util.List;

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
        
        static int IGNORED_CHANNEL_IDS[] = {1, 2, 3}; // example channel IDs to ignore
        static int IGNORED_CLIENT_IDS[] = {4, 5, 6}; // example client IDs to ignore
        static int IGNORED_GROUP_IDS[] = {7, 8, 9}; // example group IDs to ignore
        static Client targetClient; // the client to be moved

	   
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
                            if(targetClient == null) {
                                api.sendPrivateMessage(e.getInvokerId(), "No target client set. Please specify a client to move.");
                                return;
                            }
/*                            if(targetClient.getId() == clientId) {
                                api.sendPrivateMessage(e.getInvokerId(), "You cannot move yourself.");
                                return;
                            }
*/
                            if(isIgnored(targetClient)) {
                                api.sendPrivateMessage(e.getInvokerId(), "The target client is in an ignored channel, group, or is an ignored client.");
                                return;
                            }
                            if(targetClient.getChannelId() == 0) {
                                api.sendPrivateMessage(e.getInvokerId(), "The target client is not in a channel.");
                                return;
                            }
                            
                            if(!isInteger(args[1])) {
                                targetClient = api.getClientByNameExact(args[1], true);
                            } else {
                                targetClient = api.getClientInfo(Integer.parseInt(args[1]));
                            }
							
							api.sendPrivateMessage(e.getInvokerId(), "");
							if(isInteger(args[2])) {
                                int amount = Integer.parseInt(args[2]);
                                for(int i = 0; i < amount; i++) {
                                    api.moveClient(targetClient.getId(), targetClient.getChannelId());
                                }
                            } else {
                                // read out the time indicator and parse the duration in seconds
                                int duration = Integer.parseInt(args[2]);
                                if(args[2].endsWith("s")) {
                                    duration = Integer.parseInt(args[2].substring(0, args[2].length() - 1));
                                } else if(args[2].endsWith("m")) {
                                    duration = Integer.parseInt(args[2].substring(0, args[2].length() - 1)) * 60;
                                } else if(args[2].endsWith("h")) {
                                    duration = Integer.parseInt(args[2].substring(0, args[2].length() - 1)) * 3600;
                                }
                                final int durationSeconds = duration;
                                new Thread(() -> {
                                    long endTime = System.currentTimeMillis() + durationSeconds * 1000L;
                                    while(System.currentTimeMillis() < endTime) {
                                        // move the client to a random channel that is not a spacer every second
                                         int currentChannelId = targetClient.getChannelId();
                                         List<Channel> channels = api.getChannels();
                                         List<Channel> validChannels = new java.util.ArrayList<>();
                                         for (Channel channel : channels) {
                                             String name = channel.getName();
                                            boolean isSpacer = name.matches(".^\\[(?:([rcl])?spacer(\\d+))\\](.*)$");
                                             if (!isSpacer && channel.getId() != currentChannelId) {
                                                 validChannels.add(channel);
                                             }
                                         }
                                         if (!validChannels.isEmpty()) {
                                             Channel randomChannel = validChannels.get(new java.util.Random().nextInt(validChannels.size()));
                                             api.moveClient(targetClient.getId(), randomChannel.getId());
                                         }
                                        
                                         
                                        try {
                                            Thread.sleep(1000);
                                        } catch (InterruptedException ex) {
                                            Thread.currentThread().interrupt();
                                        }
                                    }
                                }).start();
                            }
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
                    for(int channelId : IGNORED_CHANNEL_IDS) {
                        if(targetClient.getChannelId() == channelId) {
                            return true;
                        }
                    }
                    for(int clientId : IGNORED_CLIENT_IDS) {
                        if(targetClient.getId() == clientId) {
                            return true;
                        }
                    }
                    for(int groupId : IGNORED_GROUP_IDS) {
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