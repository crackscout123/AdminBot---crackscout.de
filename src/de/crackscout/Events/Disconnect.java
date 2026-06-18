package de.crackscout.Events;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;

import de.crackscout.AdminBot.Main;
import de.crackscout.Managers.Utils;

public class Disconnect {
	   
	static TS3Api api = Main.api;
	   
	public static void load(){
	api.registerAllEvents();
	
	api.registerEvent(TS3EventType.SERVER);
	
	api.addTS3Listeners(new TS3EventAdapter() {

		@Override
		public void onClientLeave(ClientLeaveEvent e) {
			Utils.whitelistedUsers.remove((Integer) e.getClientId());
    		Utils.kickMeList.remove((Integer) e.getClientId());
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