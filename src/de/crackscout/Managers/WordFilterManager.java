package de.crackscout.Managers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

public class WordFilterManager {

	// Initialize variables
	public static Client sender;
	public static Client target;
	public static String blacklistFileName = "blacklisted_words.app";
	public static Boolean enabled = Boolean.parseBoolean(
		ConfigManager.loadProp("wordfilter.enabled", "config.properties") != null ? ConfigManager.loadProp("wordfilter.enabled", "config.properties") : "true"
	);
	
	public static List<String> words = readWords() != null ? readWords() : new ArrayList<>();	
	public static void createDefaults() {
		if(!ConfigManager.checkForDefault(blacklistFileName)) {
			//@TODO add example words to the blacklist file
			try {	
				Files.write(Paths.get("AdminBot/" + blacklistFileName), List.of(
					"enabled=true",
					"# This is the blacklist file. Add words you want to filter out line by line.",
					"# Lines starting with # are ignored and can be used for comments.",
					"example1",
					"example2",
					"example3"
				));
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	//@TODO regex support and better word matching (currently it just checks if the message contains the blacklisted word, which can lead to false positives);
	
	public static boolean check(String toBeChecked) {
		if(enabled){ 
			for (int i = 0; i < words.size(); i++) {
				System.out.println("USER: " + toBeChecked + " LIST: " + words.get(i));
				if(toBeChecked.contains(words.get(i))) {
					System.out.println("matched");
					return true;
				}
			}  
		}
		return false;
	}
	
	public static List<String> readWords() { // words are phrased line by line
		try {
			List<String> allLines = Files.readAllLines(Paths.get("AdminBot/" + blacklistFileName));
			allLines.remove(0); // remove first line (enabled=true/false)
			allLines.removeIf(line -> line.startsWith("#")); // remove comments
			return allLines;

		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<>();
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