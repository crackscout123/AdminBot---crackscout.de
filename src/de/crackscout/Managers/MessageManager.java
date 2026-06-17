package de.crackscout.Managers;


public class MessageManager {

	public static String file = "messages.properties";

	public static void createDefaults() {
		if(!ConfigManager.checkForDefault(file)) {
			    // messages.properties
                ConfigManager.saveProp("afk.moved", "Du wurdest in den AFK-Channel verschoben!", file);
                ConfigManager.saveProp("wordfilter.kick", "Blacklisted name! Please change it!", file);
                ConfigManager.saveProp("stay.added", "Du wurdest zur Whitelist hinzugefügt.",   file);
                ConfigManager.saveProp("stay.removed", "Du wurdest von der Whitelist entfernt.", file);  
                ConfigManager.saveProp("trollmove.no.target", "Kein Zielclient gesetzt. Bitte einen Client angeben.", file);
                ConfigManager.saveProp("trollmove.ignored", "Der Zielclient befindet sich in einem ignorierten Channel/Gruppe.", file);

		}
	}


    

}
