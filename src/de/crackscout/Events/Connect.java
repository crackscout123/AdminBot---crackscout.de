package de.crackscout.Events;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;

import de.crackscout.AdminBot.Main;
import de.crackscout.Collectors.WasserCollector;

public class Connect {

    static TS3Api api = Main.api;

    public static void load() {
        api.registerAllEvents();
        api.registerEvent(TS3EventType.SERVER);

        api.addTS3Listeners(new TS3EventAdapter() {
            @Override
            public void onClientJoin(ClientJoinEvent e) {
				ClientInfo client = api.getClientByUId(e.getUniqueClientIdentifier());
                WasserCollector.onClientJoined(e.getClientId(), client.getChannelId());
            }

            @Override
            public void onClientMoved(ClientMovedEvent e) {
                WasserCollector.onClientMoved(e.getClientId(), e.getTargetChannelId());
            }

            @Override
            public void onClientLeave(ClientLeaveEvent e) {
                WasserCollector.onClientLeft(e.getClientId());
            }
        });
    }
}


/** 
 *
 * @author Joel Rzepka - crackscout.de
 *
 * @date 27.03.2023 - 14:57:41
 *
 */