package areolsen.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
* ReadFile helper file.
 */
public class ReadFile {
    /**
    * Reads a text file as list of strings
    * @param filename filename of text file.
    * @return list of strings of file contents
 */
    public static List<String> readLines(String filename) {
        try {
            File file = new File(filename);
            Scanner sc = new Scanner(file);
            List<String> list = new ArrayList<>();
            while (sc.hasNextLine()) {
                list.add(sc.nextLine());
            }
            sc.close();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
    * Reads a text file as one string.
    * @param filename filename of text file.
    * @return String of file contents.
 */
    public static String read(String filename) {
        try {
            File file = new File(filename);
            Scanner sc = new Scanner(file);
            StringBuilder builder = new StringBuilder();
            while (sc.hasNextLine()) {
                builder.append(sc.nextLine());
            }
            sc.close();
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
