package scripts.api.files;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Spencer on 8/9/2016.
 */
public class FileHelper {

    /**
     * Returns a boolean if the file has been created or exists
     * @param path Path to the file you wish to create
     * @return if the file is created or exists.
     */
    public static boolean createFile(Path path) {
        if (fileExists(path)) return true;

        try {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns a boolean that says if the file exists at the given path.
     * @param path Path to the file
     * @return if the file exists at the path given
     */
    public static boolean fileExists(Path path) {
        return Files.exists(path);
    }

    /**
     * Returns a boolean that says if the file exists at the given path.
     * @param path Path to the file
     * @return if the file exists at the path given
     */
    public static boolean fileExists(String stringPath) {
        return fileExists(Paths.get(stringPath));
    }

    /**
     * Returns a boolean that says if the file exists at the given path.
     * @param path Path to the file
     * @return if the file exists at the path given
     */
    public static boolean fileExists(File path) {
        return fileExists(Paths.get(path.toURI()));
    }

    /**
     * Returns a boolean that says if the write to the given file was successful
     * @param file Path to the file you wish to write to.
     * @param data Data to add to the file.
     * @param append If the data should be appended to the file
     * @return if the writing was successful
     */
    public static boolean writeFile(Path file, String data, boolean append) {
        FileWriter fw;
        try {
            fw = new FileWriter(file.toFile(), append);
            fw.write(data);
            fw.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retuns a String that contains the contents of the file.
     * @param path Path to the file
     * @return a String with the data from the file.
     * @throws IOException
     */
    public static String readFile(Path path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path.toFile()));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }

            return sb.toString();
        } catch(IOException e) {
            throw e;
        } finally {
            br.close();
        }
    }
}
