package de.crackscout.Managers;

import java.util.logging.Level;
import java.util.logging.Logger;


public class Debug {
	
	public static boolean debug = true;
	
	public static void info(String msg) {
		if(debug) {
		    Logger.getLogger("SomeName").log(Level.INFO, msg);
		}
	}
	
	public static void err(String msg) {
		if(debug) {
		    Logger.getLogger("AnotherName").log(Level.WARNING, msg);
		}
	}
	
}


/**
 * @author Joel Rzepka - crackscout123.de
 *
 * @date 11.11.2021 - 00:17:14
 *
 */