package de.crackscout.Events;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;

import de.crackscout.AdminBot.Main;

public class Disconnect {
	   
	static TS3Api api = Main.api;
	   
	public static void load(){
	api.registerAllEvents();
	
	api.registerEvent(TS3EventType.SERVER, 0);
	
	api.addTS3Listeners(new TS3EventAdapter() {

		@Override
		public void onClientLeave(ClientLeaveEvent e) {
			// Utils.whitelistedUsers.remove(client.getId());
			/*
			 * TODO:
			 *  fix: everytime collecting afk users also check for dead-id's in the Array and remove!
			 */
			
			
		}




	
	
	});}
}


/** 
 *
 * @author Joel Rzepka - crackscout.de
 *
 * @date 27.03.2023 - 14:57:41
 *
 */