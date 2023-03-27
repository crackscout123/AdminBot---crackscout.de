package de.crackscout.Managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigManager {
public static Properties prop = new Properties();
	
	public static void saveProp (String key, String value, String file) {
		try {
			prop.setProperty(key, value);
			prop.store(new FileOutputStream("AdminBot/" + file), null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String loadProp(String key, String file) {
		String value = "";
		try {
			prop.load(new FileInputStream("AdminBot/" + file));
			value = prop.getProperty(key);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
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