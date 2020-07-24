import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;

/**
* This is a work in progress
* MacOS will be supported first.
* Inspired by rupa/z, autojump, zsh-z, and z.lua. Moving to fish meant I couldn't use rupa/z anymore.
* I had to install lua just so I could use a program similar to rupa/z
*/
public class WarpDrive {

    private static File data = new File(System.getProperty("user.home") + "/.WarpDriveData");
    private static ArrayList<String> lines = new ArrayList<>();
    private static boolean dataFileRead = false;
    private static boolean debug = false;

    public static void main(String[] args) {
        if (args.length == 0) {
            //don't print anything so cd gets no args
            System.exit(0);
        }

        if (args[0].equalsIgnoreCase("--add")) {
            if (args.length == 1) {
                System.err.println("No argument for option: --add");
                System.exit(1);
            }

            for (int j = 1; j < args.length; j++) {
                addPath(args[j]);
            }
            System.out.println("."); // cd .
        } else {
            String match = matchPattern(args[0]);
            addPath(match);
            System.out.println(match); // cd <match of args[0]>
        }
    }

    public static String matchPattern(String pattern) {
        return pattern;  // just for now
    }

    public static void addPath(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            return;
        }
        if (!dataFileRead) {
            readFromDataFile();
        }
        try {
            path = dir.getCanonicalPath();
            ArrayList<String> parsed = new ArrayList<>();
            boolean dirWasFound = false;
            String line;
            for (int i = 0, linesSize = lines.size(); i < linesSize; i++) {
                line = lines.get(i);
                if (line.contains(path)) {
                    String[] parsedArr = line.split("\\|"); // the two slashes escape the pipe character used as a delimiter
                    for (int j = 0; j < parsedArr.length; j++) {
                        parsed.add(parsedArr[j]);
                    }
                    while (parsed.size() > 3) {
                        parsed.remove(0);
                        parsed.add(0, parsed.get(0) + parsed.get(1));
                    }
                    if (parsed.get(0).length() != path.length()) {
                        parsed.clear();
                        continue;
                    }
                    parsed.remove(1);
                    parsed.add(1, Integer.toString(Integer.parseInt(parsedArr[1]) + 1));
                    line = parsed.get(0) + "|" + parsed.get(1) + "|" + Math.round((double) System.currentTimeMillis() / 1000.0);
                    dirWasFound = true;
                    lines.remove(i);
                    lines.add(i, line);
                    break;
                }
            }
            if (!dirWasFound) {
                lines.add(dir.getCanonicalPath() + "|1|" + Math.round((double)System.currentTimeMillis()/1000.0));
            }
            writeToDataFile();
        } catch (Exception e) {
            System.err.println("Error while trying to read or write to datafile(~/.WarpDriveData)");
            if (debug) {
                e.printStackTrace();
            }
            System.exit(2);
        }

    }

    public static boolean pathExists(String path) {
        return new File(path).exists();
    }

    private static void readFromDataFile() {
        lines.clear();
        String line = "";
        FileReader fr = null;
        if (data.exists()) {
            try {
                data.createNewFile();
                fr = new FileReader(data);
            } catch (IOException e) {
                System.err.println("Couldn't make the datafile");
                e.printStackTrace();
                System.exit(3);
            }
        }
        BufferedReader br = new BufferedReader(fr);
        try {
            while ((line = br.readLine()) != null) {
                if (!line.isBlank()) {
                    lines.add(line);
                }
            }
            fr.close();
            br.close();
        }
        catch(IOException e){
            System.err.println("Could not read from datafile or close resources attached");
            if (debug) {
                e.printStackTrace();
            }
            System.exit(4);
        }
        dataFileRead = true;
    }

    private static void writeToDataFile() {
        try {
            FileWriter fw = new FileWriter(data);
            BufferedWriter out = new BufferedWriter(fw);
            for (String s : lines) {
                out.write(s + System.lineSeparator());
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println("Could not write to datafile or close resources attached");
            if (debug) {
                e.printStackTrace();
            }
            System.exit(4);
        }

    }

    private static double rank() {
        return 0;
    }
}
