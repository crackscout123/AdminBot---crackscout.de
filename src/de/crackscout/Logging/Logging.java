package de.crackscout.Logging;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Logging {

    private static final String LOG_DIR = "logs";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private static Logging instance;

    private Logger logger;
    private FileHandler fileHandler;
    private String currentDay;

    private Logging() {
        new File(LOG_DIR).mkdirs();
        rotate();
    }

    public static synchronized Logging getInstance() {
        if (instance == null) instance = new Logging();
        return instance;
    }

    /** Gibt den Logger zurück – rotiert automatisch wenn der Tag gewechselt hat */
    public Logger getLogger() {
        String today = DATE_FORMAT.format(new Date());
        if (!today.equals(currentDay)) rotate();
        return logger;
    }

    private void rotate() {
        currentDay = DATE_FORMAT.format(new Date());

        if (logger != null && fileHandler != null) {
            logger.removeHandler(fileHandler);
            fileHandler.close();
        }

        logger = Logger.getLogger("AdminBot");
        logger.setUseParentHandlers(false); // kein doppelter stdout-Output

        try {
            String path = LOG_DIR + File.separator + currentDay + ".log";
            fileHandler = new FileHandler(path, true); // append=true → Neustart überschreibt nicht
            fileHandler.setFormatter(new Formatter() {
                private final SimpleDateFormat ts = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                @Override
                public String format(LogRecord r) {
                    return String.format("[%s] [%-7s] %s%n",
                        ts.format(new Date(r.getMillis())),
                        r.getLevel().getName(),
                        r.getMessage());
                }
            });
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("[Logging] Konnte Log-Datei nicht öffnen: " + e.getMessage());
        }
    }

    /** Im Shutdown Hook aufrufen für sauberes Flush */
    public void shutdown() {
        if (fileHandler != null) {
            fileHandler.flush();
            fileHandler.close();
        }
    }
}


/** 
 *
 * @author Joel Rzepka - crackscout.de
 *
 * @date 11.07.2026 - 09:58:49
 *
 */