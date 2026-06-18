package de.crackscout.Managers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

public class ConfigManager {

	public static int[] loadIntArray(String key, String file) {
		String value = loadProp(key, file);
		if (value != null) {
			String[] parts = value.split(",");
			int[] result = new int[parts.length];
			for (int i = 0; i < parts.length; i++) {
				try {
					result[i] = Integer.parseInt(parts[i].trim());
				} catch (NumberFormatException e) {
					e.printStackTrace();
					return new int[0]; // Return empty array on error
				}
			}
			return result;
		}
		return new int[0]; // Return empty array if key not found
	}

	public static void saveProp(String key, String value, String file) {
		Properties p = new Properties();
		File f = new File("AdminBot/" + file);
		// Bestehende Keys laden, dann neuen hinzufügen
		if (f.exists()) {
			try { p.load(new FileInputStream(f)); } catch (IOException ignored) {}
		}
		p.setProperty(key, value);
		saveSortedProperties(p, f);

	}
		
	private static void saveSortedProperties(Properties props, File file) {
		TreeMap<String, String> sorted = new TreeMap<>();

		for (String key : props.stringPropertyNames()) {
			sorted.put(key, props.getProperty(key));
		}

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			for (Map.Entry<String, String> entry : sorted.entrySet()) {
				writer.write(entry.getKey() + "=" + entry.getValue());
				writer.newLine();
			}
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
			System.out.println(file + " created.");
			return false;
		}
		return true;
	}

	public static int loadInt(String key, String file) {
		String value = loadProp(key, file);
		if(value != null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return 0; // Return default value on error
			}
		}
		return 0; // Return default value if key not found
	}
}



/** 
 *
 * @author Joel Rzepka - crackscout.de
 *
 * @date 27.03.2023 - 13:01:01
 *
 */