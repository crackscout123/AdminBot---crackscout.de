package de.crackscout.AdminBot;

import de.crackscout.Logging.Logging;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;

import de.crackscout.Collectors.AfkCollector;
import de.crackscout.Collectors.KickCollector;

import de.crackscout.Commands.Clear;
import de.crackscout.Commands.Help;
import de.crackscout.Commands.KickMe;
import de.crackscout.Commands.Ping;
import de.crackscout.Commands.Stats;
import de.crackscout.Commands.Stay;
import de.crackscout.Commands.TrollCommands.Trollmove;

import de.crackscout.Events.WordFilterEvents;
import de.crackscout.Events.Disconnect;
import de.crackscout.Events.StackedEvents;

import de.crackscout.Managers.AuthManager;
import de.crackscout.Managers.ConfigManager;
import de.crackscout.Managers.Debug;
import de.crackscout.Managers.MessageManager;
import de.crackscout.Managers.SettingsManager;
import de.crackscout.Managers.WordFilterManager;


public class Main {

	public static TS3Api api;
	
	private static String hostname, username, password;
	private static Integer serverID;
	private static String[] credentials;
	public static Boolean debug = false;

	private static Thread afkCollector, kickCollector;
	// private static final Logger LOGGER = Logger.getLogger( Logging.class.getName() ); //@TODO rework logging, currently it just prints to console, but it should write to a file and have different log levels (info, warning, error)

	// Fallback only — actual value is loaded from config.properties after createDefaults() in main()
	public static String bot_nickname = "AdminBot starting...";

	
	// java adminbot.jar "hostname" "serverID" "username:password" -debug
	
	public static void main(String[] args) {	
		
		//registerLogging();
		
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
		api.setNickname(bot_nickname); // Set default nickname before loading settings, in case config.properties doesn't exist yet

		// Load settings first so config.properties exists before reading bot_nickname
		registerSettingsManager();
		Debug.info("SettingsManager: registerd.");

		// Now safely read bot_nickname from config (file is guaranteed to exist after createDefaults())
		String configured = ConfigManager.loadProp("bot.nickname", "config.properties");
		if (configured != null) bot_nickname = configured;
		api.setNickname(bot_nickname);
		

		registerWordFilter();
		Debug.info("WordFilter: registerd.");

		registerMessageManager();
		Debug.info("MessageManager: registerd.");
				
		registerLogging();
		Debug.info("Logging: registerd.");
		
		registerCollectors();
		Debug.info("Collectors: registerd.");
		
		registerCommands();
		Debug.info("Commands: registerd.");
		
		registerAuth();
		Debug.info("Auth: registerd.");
		
		Debug.info("Bot loaded.");
		System.out.println("done."); //for pterodactyl install script
		
	}


	private static void registerSettingsManager() {
		SettingsManager.createDefaults();
	}


	private static void registerMessageManager() {
		MessageManager.createDefaults();
	}


	private static void registerWordFilter() {
		WordFilterManager.createDefaults();
	}
	
	
	private static void registerAuth() {
		AuthManager.createDefaults();
		AuthManager.readKeys();
	}
	

	private static void registerLogging() {
		Logging.getInstance();
	}
	
	
	private static void registerCommands() {
		KickMe.load();
		Ping.load();
		Stay.load();
		Clear.load();
		Stats.load();
		Help.load();
		
		//events
		Disconnect.load();
		WordFilterEvents.load();
		StackedEvents.load();

		//troll commands
		Trollmove.load();
		
		
		Debug.info("Commands & Events loaded.");
	}


	private static void registerCollectors() {
		afkCollector = new Thread(new AfkCollector(api));
	    afkCollector.start();
	    kickCollector = new Thread(new KickCollector(api));
	    kickCollector.start();
	    registerShutdownHook();
		Debug.info("Collectors loaded.");
	}

	private static void registerShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			Debug.info("Shutdown hook triggered. Stopping collectors...");
			if (afkCollector != null && afkCollector.isAlive()) {
				afkCollector.interrupt();
				Debug.info("AfkCollector stopped.");
			}
			if (kickCollector != null && kickCollector.isAlive()) {
				kickCollector.interrupt();
				Debug.info("KickCollector stopped.");
			}
			try {
				Thread.sleep(1000); // Wait for collectors to stop
				api.unregisterAllEvents();
				api.logout();
				Thread.sleep(500); // Wait for logout to complete
				Logging.getInstance().shutdown();
			} catch (Exception e) {
				Debug.err("Shutdown hook interrupted while waiting for collectors to stop. Error: \n"+ e.getMessage());
			}
		}));
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