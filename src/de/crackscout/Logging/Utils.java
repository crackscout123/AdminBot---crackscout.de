package de.crackscout.Logging;

import java.io.File;

public class Utils {
	
    public static File getUniqueFileName(String folderName, String searchedFilename) {
        int num = 1;
        String extension = getExtension(searchedFilename);
        String filename = searchedFilename.substring(0, searchedFilename.lastIndexOf("."));
        File file = new File(folderName, searchedFilename);
        while (file.exists()) {
            searchedFilename = filename + "(" + (num++) + ")" + extension;
            file = new File(folderName, searchedFilename);
        }
        return file;
    }
    
    private static String getExtension(String name) { return name.substring(name.lastIndexOf(".")); }
}


/** 
 *
 * @author Joel Rzepka - crackscout.de
 *
 * @date 22.03.2023 - 23:52:12
 *
 */