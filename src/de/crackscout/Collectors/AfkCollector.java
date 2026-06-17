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
	public static ArrayList<Integer> whitelistedUsers = Utils.whitelistedUsers;

	public static String msg_afk_moved = ConfigManager.loadProp("afk.moved", "messages.properties");

	public static int[] ignoredChannels = ConfigManager.loadIntArray("afk.ignored.channels", "config.properties");
	public static int[] ignoredGroups = ConfigManager.loadIntArray("afk.ignored.groups", "config.properties");
	
	public static int afkChannelId = ConfigManager.loadInt("afk.afk.channel", "config.properties");
	public static int sleepTime = ConfigManager.loadInt("afk.sleep.ms", "config.properties");
	public static int maxIdleTime = ConfigManager.loadInt("afk.max.idle.ms", "config.properties");

    public AfkCollector(TS3Api api) {
        this.api = api;
    }
    
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				System.out.println("Encountered an interrupted exception while sleeping, shutting down collection service... \n Dumping error:");
				e.printStackTrace();
                return;
			}
			
			List<Client> clients = api.getClients();
			System.out.println("Trying to move and evaluate clients... \n DEBUG: ignored channels: " + Arrays.toString(ignoredChannels));
			for (Client client : clients) {
				if(!whitelistedUsers.contains(client.getId())) {
					moveClient(client);
				}
			}
		}
	}
	
	public void moveClient(Client client) {
		try {		
	        if (client.getId() != api.whoAmI().getId() && client.getChannelId() != afkChannelId && Arrays.stream(ignoredChannels).anyMatch(c -> c != client.getChannelId()) && client.getIdleTime() > maxIdleTime) {
				if (Arrays.stream(client.getServerGroups()).anyMatch(g -> Arrays.stream(ignoredGroups).anyMatch(ignored -> ignored == g))) {
					return;
				}
	        	api.moveClient(client.getId(), afkChannelId);
				api.sendPrivateMessage(client.getId(), msg_afk_moved);
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