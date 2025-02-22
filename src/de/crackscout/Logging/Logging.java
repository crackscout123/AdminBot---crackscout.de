package de.crackscout.Logging;

import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import de.crackscout.Managers.ConfigManager;
import de.crackscout.Managers.TimeHandler;

public class Logging {

	public static String file = "logs/init";
		
	public static void createDefaults() { if(!ConfigManager.checkForDefault(file)) {} }
	
	public void setup() {

	  	Logger logger = Logger.getLogger("MyLog");  
	    FileHandler fh;  
	    
	    try {  

	        // This block configure the logger with handler and formatter  
	    	String filepath = Utils.getUniqueFileName("AdminBot/logs", TimeHandler.sdf3.format(new Date())+".log").toString();
	        fh = new FileHandler(filepath, true);  
	        //fh.setFormatter(new MyFormatter());  
	        logger.addHandler(fh);
	        
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);  

	        // the following statement is used to log any messages  
	        logger.info("Begin of the log (" + TimeHandler.sdf1.format(new Date()) + ")");  
	        logger.setUseParentHandlers(false);

	    } catch (SecurityException e) {  
	        logger.log(Level.SEVERE, e.toString(), e);  
	    } catch (IOException e) {  
	        logger.log(Level.SEVERE, e.toString(), e);  
	    }   
	}
}


/** 
 *
 * @author Joel Rzepka - crackscout.de
 *
 * @date 22.03.2023 - 23:53:49
 *
 */