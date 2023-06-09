package de.crackscout.Managers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

public class WordFilterManager {

	// Initialize variables
	public static Client sender;
	public static Client target;
	public static String file = "blacklisted_words.app";
	public static Boolean enabled = Boolean.parseBoolean(ConfigManager.loadProp("enabled", file));
		
	public static List<String> words = readWords();
	
	public static void createDefaults() {
		if(!ConfigManager.checkForDefault(file)) {
			ConfigManager.saveProp("enabled", "true", file);
		}
	}
	
	
	// BIG TODO HERE !!! I may use regex patterns and matchers;
	
	public static boolean check(String toBeChecked) {
		if(enabled){ 
//	    for (String word : words) {
//	    	Debug.info("1");
//	    	if(toBeChecked.contains(word) ) {
//		    	Debug.info("2");
//	    		return true;
//	    		}
//	    	}  
		    for (int i = 0; i < words.size(); i++) {
		    	System.out.println("USER: " + toBeChecked + " LIST: " + words.get(i));
		    	if(toBeChecked.matches(words.get(i)) ) {
		    		System.out.println("matched");
		    		return true;
		    	}
		    }  
		}
		return false;
	}
	
	public static List<String> readWords() { // words are phrased line by line
		try {
			List<String> allLines = Files.readAllLines(Paths.get("AdminBot/"+file));
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
 * @date 10.06.2023 - 00:09:19
 *
 */