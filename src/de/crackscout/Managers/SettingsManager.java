package de.crackscout.Managers;

public class SettingsManager {

	public static String file = "config.properties";

	public static void createDefaults() {
		if(!ConfigManager.checkForDefault(file)) {
			    // messages.properties
                ConfigManager.saveProp("bot.nickname", "AdminBot", file);
                ConfigManager.saveProp("bot.debug", "false", file);

                ConfigManager.saveProp("afk.ignored.groups", "18,", file);
                ConfigManager.saveProp("afk.sleep.ms", "60000", file);
                ConfigManager.saveProp("afk.max.idle.ms", "600000", file);
                ConfigManager.saveProp("afk.afk.channel", "19", file);
                ConfigManager.saveProp("afk.ignored.channels", "20,31,11,12,26,13,19,20", file);

                ConfigManager.saveProp("wordfilter.enabled", "true", file);

                ConfigManager.saveProp("auth.ignore", "false", file);

                ConfigManager.saveProp("trollmove.ignored.channels", "1,2,3", file);
                ConfigManager.saveProp("trollmove.ignored.clients", "4,5,6", file);
                ConfigManager.saveProp("trollmove.ignored.groups", "7,8,9", file);

		}
	}
}
