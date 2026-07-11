package de.crackscout.Commands;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;

import de.crackscout.AdminBot.Main;
import de.crackscout.Managers.ConfigManager;
import de.crackscout.Managers.MessageManager;

public class Help {

    static TS3Api api = Main.api;
   
	public static String msg_help = MessageManager.helpText();
   
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
				if (e.getTargetMode() != TextMessageTargetMode.SERVER && e.getInvokerId() != clientId) {
					String message = e.getMessage().toLowerCase();

					if (message.equals("!help")) {
						api.sendPrivateMessage(e.getInvokerId(), msg_help);
					}
				}
			}
		});
	}
}
