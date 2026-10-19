package cardplayingsimulation;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Provides static methods for creating and writing to files and directories.
 * @author Thomas Russell and Finley Jones.
 */

class OutputHandler {
    /**
     * Wipes all files from a given directory.
     * @param path String, the path to the dir.
     */
    static void wipeDir(String path) {
        File dir = new File(path);

        for (File file : dir.listFiles()) {
            file.delete();
        }
    }

    /**
     * Creates a text file of the given name & path.
     * @param path String, the path of the .txt file.
     */
    static void createTextFile(String path) {
        File textFile = new File(path);
        try {
            if (textFile.createNewFile()) {
                //System.out.println("File created: " + textFile.getName());
            } else {
                //System.out.println("File already exists: " + textFile.getName());
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    /**
     * Writes a given text to a given .txt file.
     * @param path String, the path of the .txt file.
     * @param text String, the text to be written into the file.
     * @param addNewLine boolean, whether to add a \n after the given entry.
     */
    static void writeToTextFile(String path, String text, boolean addNewLine) {
        try (FileWriter writer = new FileWriter(path,true)) {
            if (addNewLine) {
                text += "\n";
            }
            
            writer.write(text);
        } catch (IOException e) {
            System.err.println(e);
        }
    }
}
