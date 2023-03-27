package de.crackscout.Logging;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class MyFormatter extends Formatter{
	
    @Override
    public String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();
        sb.append(record.getLevel()).append(':');
        sb.append(record.getMessage()).append('\n');
        return sb.toString();
    } 
	
}


/** 
 *
 * @author Joel Rzepka - crackscout.de
 *
 * @date 22.03.2023 - 23:57:29
 *
 */