package de.crackscout.Collectors;

import java.util.ArrayList;
import java.util.List;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import de.crackscout.Managers.Utils;


public class KickCollector implements Runnable {
	
    private final TS3Api api;
    private int sleep = 1*1000; //sleep between collections in seconds (milliseconds x 1000)
    //public static ArrayList<Integer> whitelistedUsers = new ArrayList<>();
	public static ArrayList<Integer> whitelistedUsers = Utils.whitelistedUsers;
	public static ArrayList<Integer> kickMeList = Utils.kickMeList;

    public KickCollector(TS3Api api) {
        this.api = api;
    }
	
	@Override
	public void run() {
		while(true) {
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				System.out.println("Encountered an interrupted exception while sleeping, shutting down collection service... \n Dumping error:");
				e.printStackTrace();
				return;
			}
			
			List<Client> clients = api.getClients();
			for (Client client : clients) {
				if(!whitelistedUsers.contains(client.getId())) {
					kickClients(client);
				}
			}
		}
		
	}
	
	public void kickClients(Client client) {
		if(kickMeList.contains(client.getId())) {
			api.kickClientFromServer(client);
		}
	}
	

}


/** 
 *
 * @author Joel Rzepka - crackscout.de
 *
 * @date 27.03.2023 - 13:44:59
 *
 */