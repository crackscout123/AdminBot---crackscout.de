package de.crackscout.Commands.TrollCommands;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;

import de.crackscout.AdminBot.Main;
import de.crackscout.Managers.AuthManager;
import de.crackscout.Managers.Debug;

public class Troll 	/* Example Class */  {

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
					// Only react to private messages not sent by the query itself
					if (e.getTargetMode() != TextMessageTargetMode.SERVER && e.getInvokerId() != clientId) {
						String message = e.getMessage().toLowerCase();
						
						ClientInfo client = api.getClientByUId(e.getInvokerUniqueId());
						if(!AuthManager.auth(client)) {
							Debug.err("auth denied!");
							return;
						}

						/* 
						 * Command syntax:
						 *  1. !trollmove <clientName/id> <amount> <duration>
						 *  2. !trollkick <clientName/id> 
						 *  3. !troll### where ### is the function and the syntax is defined as above
						 */
		
						if (message.equals("!troll")) {
							// functionality to be added in the future
							api.sendPrivateMessage(e.getInvokerId(), "");
							
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
 * @date 23.53.2026 - 05:21:54
 *
 */