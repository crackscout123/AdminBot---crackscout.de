package de.crackscout.Managers;

import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.crackscout.AdminBot.Main;

public class Debug {
	
	public static boolean debug = Main.debug;
	
	public static void info(String msg) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		if(debug) {
		    Logger.getLogger("SomeName").log(Level.INFO, msg);
		}
		System.out.println(timestamp + " |INFO| " + msg);
	}
	
	public static void err(String msg) {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		if(debug) {
		    Logger.getLogger("AnotherName").log(Level.WARNING, msg);
		}
		System.err.println(timestamp + " |ERR| " + msg);
	}
	
}


/**
 * @author Joel Rzepka - crackscout123.de
 *
 * @date 11.11.2021 - 00:17:14
 *
 */