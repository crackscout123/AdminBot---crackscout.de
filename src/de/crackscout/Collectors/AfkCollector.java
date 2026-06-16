package de.crackscout.Collectors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import de.crackscout.Managers.ConfigManager;
import de.crackscout.Managers.Utils;

public class AfkCollector implements Runnable {


    private final TS3Api api;
    private int sleep = 60*1000; //sleep between collections in seconds x 1000 (milliseconds)
    private int afkChannelId = 19;
	private int silentGroupId = 18;
    private int ignoredChannelIds[] = {20, 31, 11, 12, 26, 13}; //IDs of channels that should be ignored by the collector
    private int maxIdleTime = 600*1000; //time in milliseconds (seconds x 1000)
	public static ArrayList<Integer> whitelistedUsers = Utils.whitelistedUsers;

	public static String msg_afk_moved = ConfigManager.loadProp("afk.moved", "messages.properties");
    
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
	        if (client.getId() != api.whoAmI().getId() && client.getChannelId() != afkChannelId && Arrays.stream(ignoredChannelIds).anyMatch(c -> c != client.getChannelId()) && client.getIdleTime() > maxIdleTime) {
				if (Arrays.stream(client.getServerGroups()).anyMatch(g -> g == silentGroupId)) {
					return;
				}
	        	api.moveClient(client.getId(), afkChannelId);
				api.sendPrivateMessage(client.getId(), msg_afk_moved);
	            //api.sendPrivateMessage(client.getId(), "Du wurdest in den AFK-Channel verschoben!");
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