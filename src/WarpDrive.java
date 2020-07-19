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

    public static void main(String[] args) {
//        ArrayList<String> argsList = new ArrayList<>();
        boolean noMatch = false;
        for (int i = 0; i < args.length; i++) {
            if (!args[0].equalsIgnoreCase("--add")) {
                matchPattern(args[0]);
            }
            if (args[i].equalsIgnoreCase("--add")) {
                if (args.length == (i+1)) {
                    System.err.println("No argument for option: --add");
                    System.exit(4);
                }
                for (int j = 0; j < args.length - i; j++) {
                    addPath(args[i + j]);
                }
            }
        }


//        addPath("/Users/ishan/Desktop");
//        if (argsList.contains("--add")) {
//            addPath(argsList.get(argsList.lastIndexOf("--add") + 1));
//        }
//        addPath("/Users/ishan/Desktop/GitHub");
    }

    public static void matchPattern(String pattern) {

    }

    public static void addPath(String path) {
        File dir = new File(path);
        //System.out.println(data.getPath());
        if (!dir.exists()) {
            return;
        }
        readFromDataFile();
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
        } catch (Exception ex) {
            System.err.println("Error while trying to read or write to datafile(~/.WarpDriveData)");
            ex.printStackTrace();
            System.exit(2);
        }

    }

//    public static String normalizedPath(String path) {
//        try {
//            return new File(path).getCanonicalPath();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
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
                System.exit(1);
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
            e.printStackTrace();
            System.exit(2);
        }
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
        }

    }
}
