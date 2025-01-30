package de.crackscout.Managers;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.crackscout.Logging.Logging;


public class Debug {
	
	private static final Logger LOGGER = Logger.getLogger(Logging.class.getName() );
	
	public static boolean debug = true;
	
	public static void info(String msg) {
		if(debug) {
			LOGGER.log(Level.INFO, msg.toString(), msg);
		    //Logger.getLogger("SomeName").log(Level.INFO, msg);
		}
	}
	
	public static void err(String msg) {
		if(debug) {
			LOGGER.log(Level.SEVERE, msg.toString(), msg);  
		}
	}
	
}


/**
 * @author Joel Rzepka - crackscout123.de
 *
 * @date 11.11.2021 - 00:17:14
 *
 */