package de.crackscout.Commands;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;

import de.crackscout.AdminBot.Main;
import de.crackscout.Managers.AuthManager;
import de.crackscout.Managers.Utils;

public class Stats {
	
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
		
						if (message.equals("!stats")) {
							int kickSize = Utils.kickMeList.size();
							int whitelistSize = Utils.whitelistedUsers.size();
							int allowedSize = AuthManager.readKeys().size();
							
							boolean auth = AuthManager.ignoreAuth;
							boolean debug = Main.debug;
							
							
							api.sendPrivateMessage(e.getInvokerId(), 
								  "\nSTATS FOR DEBUG - DEBUG: " + ((debug == false) ? "INAKTIV" : "AKTIV")
								+ "\nArray<> whitelistedUsers has a total size of: " +whitelistSize+"."
								+ "\nArray<> kickMeList has a total size of: " +kickSize+"."
								+ "\n Use !clear to empty those."
								+ "\n"
								+ "\nAUTH-SYSTEM: " + ((auth == true) ? "INAKTIV" : "AKTIV")
								+ "\nAllowed identitys: " + allowedSize
								+ "\n");
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
 * @date 08.04.2023 - 04:55:52
 *
 */