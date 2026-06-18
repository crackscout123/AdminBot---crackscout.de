package de.crackscout.Events;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import de.crackscout.AdminBot.Main;
import de.crackscout.Commands.TrollCommands.Trollmove;
import de.crackscout.Managers.ConfigManager;
import de.crackscout.Managers.WordFilterManager;

public class WordFilterEvents {
	
	static TS3Api api = Main.api;

	public static String msg_wordfilter_kick = ConfigManager.loadProp("wordfilter.kick", "messages.properties");
	   
	public static void load(){
		
		api.registerEvent(TS3EventType.SERVER);

		api.addTS3Listeners(new TS3EventAdapter() {
				
			@Override
			public void onClientJoin(ClientJoinEvent e) {
				Client client = api.getClientByUId(e.getUniqueClientIdentifier());
			 	if(WordFilterManager.check(client.getNickname())) {
					api.kickClientFromServer(msg_wordfilter_kick, client);
				}
			}			

			@Override
			public void onClientLeave(ClientLeaveEvent e) {				
				if(Trollmove.activeTrollMoves.containsKey(e.getClientId())) {
					Thread t = Trollmove.activeTrollMoves.get(e.getClientId());
					if(t != null && t.isAlive()) {
						t.interrupt();
					}
					Trollmove.activeTrollMoves.remove(e.getClientId());
				}
			}
		});

		// Zusätzliches Polling, um Nickname-Änderungen zu erfassen, da es kein Event dafür gibt
		Thread wordFilterPolling = new Thread(() -> {
			while (true) {
				try {
					for (Client client : api.getClients()) {
    					if (client.getId() == api.whoAmI().getId()) continue; // Bot überspringen
						if (WordFilterManager.check(client.getNickname())) {
							api.kickClientFromServer(msg_wordfilter_kick, client.getId());
						}
					}
					Thread.sleep(30000); // alle 30 Sekunden @TODO anpassbar machen
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		wordFilterPolling.setName("WordFilter-Polling");
		wordFilterPolling.setDaemon(true); 
		wordFilterPolling.start();
	}
}



/** 
 *
 * @author Joel Rzepka - crackscout.de
 *
 * @date 09.06.2023 - 23:32:48
 *
 */