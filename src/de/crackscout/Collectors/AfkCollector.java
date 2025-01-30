package de.crackscout.Collectors;

import java.util.ArrayList;
import java.util.List;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import de.crackscout.Managers.Utils;

public class AfkCollector implements Runnable {


    private final TS3Api api;
    private int sleep = 60*1000; //sleep between collections in seconds x 1000
    private int afkChannelId = 24;
    private int musicChannelId = 22;
    private int maxIdleTime = 600*1000; // time in seconds x 1000
	public static ArrayList<Integer> whitelistedUsers = Utils.whitelistedUsers;
    
    public AfkCollector(TS3Api api) {
        this.api = api;
    }
    
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				System.out.println("Encountered an interrupted exception while sleeping, shutting down collection service... \n Dumping error:");
				e.printStackTrace();
                return;
			}
			
			List<Client> clients = api.getClients();
			System.out.println("Trying to move and evaluate clients...");
			for (Client client : clients) {
				if(!whitelistedUsers.contains(client.getId())) {
					moveClient(client);
				}
			}
		}
	}
	
	public void moveClient(Client client) {
		try {		
	        if (client.getId() != api.whoAmI().getId() && client.getChannelId() != afkChannelId && client.getChannelId() != musicChannelId && client.getIdleTime() > maxIdleTime) {
	        	api.moveClient(client.getId(), afkChannelId);
	            api.sendPrivateMessage(client.getId(), "Du wurdest in den AFK-Channel verschoben!");
	        }
		} catch (Exception e) {
			System.out.println("Failed to fetch client details, dumping error info: " + e.getMessage());
		}
	}
}

/** 
 *
 * @author Joel Rzepka - crackscout.de
 *
 * @date 27.03.2023 - 13:37:06
 *
 */