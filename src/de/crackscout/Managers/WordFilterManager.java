package de.crackscout.Managers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

public class WordFilterManager {

	// Initialize variables
	public static Client sender;
	public static Client target;
	public static String blacklistFileName = "blacklisted_words.app";
	public static Boolean enabled = Boolean.parseBoolean(
		ConfigManager.loadProp("wordfilter.enabled", "config.properties") != null ? ConfigManager.loadProp("wordfilter.enabled", "config.properties") : "true"
	);
	
	public static List<String> words = readWords();
	public static void createDefaults() {
		if(!ConfigManager.checkForDefault(blacklistFileName)) {
			//@TODO add example words to the blacklist file
			try {	
				Files.write(Paths.get("AdminBot/" + blacklistFileName), List.of(
					"# Normales Wort-Matching",
					"badword",
					"# Regex-Matching",
					"regex:f+[u\\*ü0]+c+k+"
				));
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	
	public static boolean check(String toBeChecked) {
		if (!enabled || toBeChecked == null) {
			return false;
		}

		for (int i = 0; i < words.size(); i++) {
			String entry = String.valueOf(words.get(i)).trim();
			if (entry.isEmpty()) {
				continue;
			}
			if (entry.startsWith("regex:")) {
				String regex = entry.substring(6).trim();
				if (!regex.isEmpty() &&
					Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(toBeChecked).find()) {
					return true;
				}
			} else {
				String regex = "(?i)\\b" + Pattern.quote(entry) + "\\b";
				if (Pattern.compile(regex).matcher(toBeChecked).find()) {
					return true;
				}
			}
		}

		return false;
	}
	
	public static List<String> readWords() {
		try {
			List<String> allLines = Files.readAllLines(Paths.get("AdminBot/" + blacklistFileName));
			allLines.remove(0);
			allLines.removeIf(line -> line.startsWith("#"));
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