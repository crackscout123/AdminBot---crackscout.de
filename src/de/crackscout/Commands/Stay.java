package de.crackscout.Commands;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import de.crackscout.AdminBot.Main;
import de.crackscout.Managers.AuthManager;
import de.crackscout.Managers.Debug;
import de.crackscout.Managers.Utils;

public class Stay {

	   static TS3Api api = Main.api;
	   
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
					// Only react to channel messages not sent by the query itself
					if (e.getTargetMode() != TextMessageTargetMode.SERVER && e.getInvokerId() != clientId) {
						
						Client client = api.getClientByUId(e.getInvokerUniqueId());
						if(!AuthManager.auth(client)) {
							Debug.err("auth denied!");
							return;
						}
						
						String message = e.getMessage().toLowerCase();
						Integer clientId = e.getInvokerId();
						if (message.equals("!stay")) {
							if(Utils.whitelistedUsers.contains(clientId)) {
								Utils.whitelistedUsers.remove(clientId);
								api.sendPrivateMessage(e.getInvokerId(), "removed from whitelist");
								Debug.info("removed " +  e.getInvokerName() + " from whitelist");
							}else {
								Utils.whitelistedUsers.add(clientId);
								api.sendPrivateMessage(e.getInvokerId(), "added to whitelist");
								Debug.info("added " +  e.getInvokerName() + " to whitelist");
							}
						} 
					}
				}
			});
		}
	}


/** 
 *
 * @author Joel Rzepka - crackscout.de
 *
 * @date 27.03.2023 - 14:24:19
 *
 */