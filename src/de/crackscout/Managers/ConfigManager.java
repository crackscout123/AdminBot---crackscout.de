package de.crackscout.Managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
	
	public static void saveProp(String key, String value, String file) {
		Properties p = new Properties();
		File f = new File("AdminBot/" + file);
		// Bestehende Keys laden, dann neuen hinzufügen
		if (f.exists()) {
			try { p.load(new FileInputStream(f)); } catch (IOException ignored) {}
		}
		try {
			p.setProperty(key, value);
			p.store(new FileOutputStream(f), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String loadProp(String key, String file) {
		Properties p = new Properties();
		try {
			p.load(new FileInputStream("AdminBot/" + file));
			return p.getProperty(key);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void createFunctionsFolder() {
		File folder = new File("AdminBot");
		if(!folder.exists()) {
			folder.mkdirs();
		}
		
	}
	
	public static boolean checkForDefault(String file) {
		File path = new File("AdminBot/" + file);
		if(!path.exists()) {
			path.getParentFile().mkdirs();
			System.out.println("created.");
			return false;
		}
		return true;
	}
}



/** 
 *
 * @author Joel Rzepka - crackscout.de
 *
 * @date 27.03.2023 - 13:01:01
 *
 */