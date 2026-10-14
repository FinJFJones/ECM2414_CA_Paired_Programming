import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class OutputHandler {
    public static void wipeDir(String path) {
        File dir = new File(path);

        for (File file : dir.listFiles()) {
            file.delete();
        }
    }

    public static void createTextFile(String path) {
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

    public static void writeToTextFile(String path, String text, boolean addNewLine) {
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
