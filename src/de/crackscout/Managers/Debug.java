package de.crackscout.Managers;

import java.util.logging.Level;
import java.util.logging.Logger;
import de.crackscout.Logging.Logging;

public class Debug {

    public static boolean debug = true;

    private static Logger logger() {
        return Logging.getInstance().getLogger();
    }

    public static void info(String msg) {
        System.out.println("[INFO]  " + msg);   // weiterhin auf stdout
        logger().log(Level.INFO, msg);
    }

    public static void warn(String msg) {
        System.out.println("[WARN]  " + msg);
        logger().log(Level.WARNING, msg);
    }

    public static void err(String msg) {
        System.err.println("[ERROR] " + msg);
        logger().log(Level.SEVERE, msg);
    }
}


/** 
 *
 * @author Joel Rzepka - crackscout.de
 *
 * @date 11.07.2026 - 09:58:55
 *
 */