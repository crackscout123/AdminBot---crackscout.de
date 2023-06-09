package de.crackscout.Events;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.ChannelCreateEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelDeletedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelDescriptionEditedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelEditedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelPasswordChangedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.PrivilegeKeyUsedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ServerEditedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3Listener;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import de.crackscout.AdminBot.Main;
import de.crackscout.Managers.WordFilterManager;

public class Connections {
	
	static TS3Api api = Main.api;
	   
	public static void load(){
		
		api.registerAllEvents();
		api.addTS3Listeners(new TS3Listener() {

			
			@Override
			public void onClientJoin(ClientJoinEvent e) {
				
				Client client = api.getClientByUId(e.getUniqueClientIdentifier());
				WordFilterManager.check(client.getNickname());
				if(WordFilterManager.check(client.getNickname())) {
					api.kickClientFromServer("Blacklisted name! Please change it!", client);
				}
				// check for 'bad-names'

			}
			
			@Override
			public void onTextMessage(TextMessageEvent e) {
				// ...
			}

			@Override
			public void onServerEdit(ServerEditedEvent e) {
				// ...
			}

			@Override
			public void onClientMoved(ClientMovedEvent e) {
				// ...
			}

			@Override
			public void onClientLeave(ClientLeaveEvent e) {
				// ...
			}

			@Override
			public void onChannelEdit(ChannelEditedEvent e) {
				// ...
			}

			@Override
			public void onChannelDescriptionChanged(ChannelDescriptionEditedEvent e) {
				// ...
			}

			@Override
			public void onChannelCreate(ChannelCreateEvent e) {
				// ...
			}

			@Override
			public void onChannelDeleted(ChannelDeletedEvent e) {
				// ...
			}

			@Override
			public void onChannelMoved(ChannelMovedEvent e) {
				// ...
			}

			@Override
			public void onChannelPasswordChanged(ChannelPasswordChangedEvent e) {
				// ...
			}

			@Override
			public void onPrivilegeKeyUsed(PrivilegeKeyUsedEvent e) {
				// ...
			}
		});
	}
}



/** 
 *
 * @author Joel Rzepka - crackscout.de
 *
 * @date 09.06.2023 - 23:32:48
 *
 */