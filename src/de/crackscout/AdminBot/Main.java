package de.crackscout.AdminBot;


import java.util.logging.Logger;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;

import de.crackscout.Collectors.AfkCollector;
import de.crackscout.Collectors.KickCollector;
import de.crackscout.Commands.Clear;
import de.crackscout.Commands.KickMe;
import de.crackscout.Commands.Ping;
import de.crackscout.Commands.Stats;
import de.crackscout.Commands.Stay;
import de.crackscout.Events.Connections;
import de.crackscout.Events.Disconnect;
import de.crackscout.Events.StackedEvents;
import de.crackscout.Logging.Logging;
import de.crackscout.Managers.AuthManager;
import de.crackscout.Managers.Debug;
import de.crackscout.Managers.WordFilterManager;


public class Main {

	public static TS3Api api;
	
	private static String hostname, username, password;
	private static Integer serverID;
	private static String[] credentials;
	public static Boolean debug = false;


		
	private static Thread collectorProzess, kickCollector;
	private static final Logger LOGGER = Logger.getLogger( Logging.class.getName() );

	
	// java adminbot.jar "hostname" "serverID" "username:password" -debug
	

	

	
	public static void main(String[] args) {	
		
		registerLogging();
		
		Debug.info("Bot loading.");
		
	    // Fetch start arguments
	    for(int i = 0; i < args.length; i++) {
	    	if(args.length >= 3) {
	    		hostname = args[0];
	    		serverID = Integer.parseInt(args[1]);
	    		credentials = args[2].split(":"); //credentials[0] = user | credentials[1] = auth
	    		username = credentials[0];
	    		password = credentials[1];
	    	
	    		
	    		if(args.length == 4) {
	    			if(args[3].contains("debug")) {
	    				debug = true;
	    			}
	    		} 
	    	}
	    	
	    }
		final TS3Config config = new TS3Config();
		config.setHost(hostname);
		config.setEnableCommunicationsLogging(debug);

		final TS3Query query = new TS3Query(config);
		query.connect();

		api = query.getApi();
		api.login(username, password);
		api.selectVirtualServerById(serverID);
		api.setNickname("AdminBot dev");
		
		
		registerWordFilter();
		Debug.info("WordFilter: registerd.");
		
		registerLogging();
		Debug.info("Logging: registerd.");
		
		registerCollectors();
		Debug.info("Collectors: registerd.");
		
		registerCommands();
		Debug.info("Commands: registerd.");
		
		registerAuth();
		Debug.info("Auth: registerd.");
		
		Debug.info("Bot loaded.");
		
	}


	private static void registerWordFilter() {
		WordFilterManager.createDefaults();
	}
	
	
	private static void registerAuth() {
		AuthManager.createDefaults();
		AuthManager.readKeys();
	}
	
	private static void registerLogging() {
		Logging.createDefaults();
	  	Logging logg = new Logging();
	  	logg.setup();
	}
	
	
	private static void registerCommands() {
		KickMe.load();
		Ping.load();
		Stay.load();
		Clear.load();
		Stats.load();
		
		//events
		Disconnect.load();
		Connections.load();
		StackedEvents.load();
		
		
		Debug.info("Commands & Events loaded.");
	}


	private static void registerCollectors() {
		collectorProzess = new Thread(new AfkCollector(api));
	    collectorProzess.start();
	    kickCollector = new Thread(new KickCollector(api));
	    kickCollector.start();
	    
		Debug.info("Collectors loaded.");
	}

		
	/**
	 * TODO:
	 * 
	 * check for new teamspeak-api (java-query) DONE
	 * 
	 * migrate default functions from crackys-ts-bot & TrollBot DONE
	 * (AfkCollector.java, AntikAFK.java, de.crackscout.utils.**) DONE 
	 * 
	 * de.crackscout.Logging.* <- rework
	 * 
	 * improve performance particular on the subjects messaging (query->client) DONE
	 * & Client collection (client->query) DONE
	 * 
	 * remove unused functions DONE
	 * 
	 * clean up util's, debugging & the Log DONE (not the log)
	 * 
	 * 
	 */
	
}


/** 
 *
 * @author Joel Rzepka - crackscout.de
 *
 * @date 27.03.2023 - 02:19:52
 *
 */