package de.crackscout.Managers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;


public class AuthManager {
	// Initialize variables
	public static Client sender;
	public static Client target;
	public static String file = "auth.app";
	public static Boolean ignoreAuth = Boolean.parseBoolean(ConfigManager.loadProp("ignoreAuth", file));
		
	public static void createDefaults() {
		if(!ConfigManager.checkForDefault(file)) {
			ConfigManager.saveProp("ignoreAuth", "false", file);
		}
	}
	
	public static boolean auth(Client client) {
		if(!ignoreAuth){
			if(readKeys().contains(client.getUniqueIdentifier())) {
				return true;
			} else {
				return false;
			}
		}else {
			return true;
		}
	}
	
	public static List<String> readKeys() {
		try {
			List<String> allLines = Files.readAllLines(Paths.get("AdminBot/auth.app"));
			allLines.remove(0);
			allLines.remove(0);
			return allLines;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}


/** 
 *
 * @author Joel Rzepka - crackscout.de
 *
 * @date 27.03.2023 - 14:31:31
 *
 */