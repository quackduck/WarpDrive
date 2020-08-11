import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Comparator;

/**
* This is a work in progress
* MacOS will be supported first.
* Inspired by rupa/z, autojump, zsh-z, and z.lua. Moving to fish meant I couldn't use rupa/z anymore.(Edit: there is actually a fish z from jethrokuan/z. I will still continue to work on this)
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
            String match = "";
            for (int i = 0; i < args.length; i++) {
                match += args[0];
            }
            match = matchPattern(match);
            addPath(match);
            System.out.println(match); // cd <match of args[0]>
        }
    }

    public static String matchPattern(String pattern) {
        ArrayList<String> finalists = new ArrayList<>();
        pattern = pattern.trim();
        String[] parsedPattern = pattern.split(" ");
        int hits = 0;
        if (!dataFileRead) {
            readFromDataFile();
        }
        ArrayList<String> parsedDataline = new ArrayList<>();
        String candidatePath = "";
        for (String dataline : lines) {
            hits = 0;
            parseDataline(parsedDataline, dataline);
            candidatePath = parsedDataline.get(0);
            for (int i = 0; i < parsedPattern.length; i++) {
                if (i == parsedPattern.length - 1) {
                    String[] parsedCandidatePath = candidatePath.split("/");
                    if (parsedCandidatePath.length == 0) {
                        parsedCandidatePath = new String[1];
                    }
                    parsedCandidatePath[0] = "/";
                    if (parsedCandidatePath[parsedCandidatePath.length - 1].contains(parsedPattern[parsedPattern.length - 1])) {
                        hits++;
                    }
                } else if (candidatePath.contains(parsedPattern[i])) {
                    hits++;
                }
            }
            if (hits == parsedPattern.length) {
                finalists.add(dataline);
            }
        }
        if (finalists.size() == 1) {
            return finalists.get(0);
        } else if (finalists.size() > 1) {
            finalists.sort(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    double firstRank = rank(o1);
                    double secondRank = rank(o2);
                    return Double.compare(firstRank, secondRank);
                }
            });
            return parseDataline(finalists.get(0), 0);
        }
        return pattern;
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
                parsed.clear();
                line = lines.get(i);
                if (line.contains(path)) {
                    parseDataline(parsed, line);
                    if (parsed.get(0).length() != path.length()) {
                        continue;
                    }
                    parsed.add(1, Integer.toString(Integer.parseInt(parsed.get(1))+1));
                    parsed.remove(2);
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
                line = line.trim();
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

    private static void parseDataline(ArrayList<String> parsed, String line) {
        String[] parsedArr = line.split("\\|"); // the two slashes escape the pipe character used as a delimiter
        parsed.clear();
        for (String s : parsedArr) {
            parsed.add(s);
        }
        while (parsed.size() > 3) {
            parsed.add(0, parsed.get(0) + parsed.get(1));
            parsed.remove(1);
        }
        //return parsed;
    }
    private static ArrayList<String> parseDataline(String line) {
        ArrayList<String> parsed = new ArrayList<>();
        String[] parsedArr = line.split("\\|"); // the two slashes escape the pipe character used as a delimiter
        parsed.clear();
        for (String s : parsedArr) {
            parsed.add(s);
        }
        while (parsed.size() > 3) {
            parsed.add(0, parsed.get(0) + parsed.get(1));
            parsed.remove(1);
        }
        return parsed;
    }

    private static String parseDataline(String line, int i) {
        ArrayList<String> parsed = new ArrayList<>();
        String[] parsedArr = line.split("\\|"); // the two slashes escape the pipe character used as a delimiter
        parsed.clear();
        for (String s : parsedArr) {
            parsed.add(s);
        }
        while (parsed.size() > 3) {
            parsed.add(0, parsed.get(0) + parsed.get(1));
            parsed.remove(1);
        }
        return parsed.get(i);
    }

    private static double rank(String dataline) {
        ArrayList<String> parsed = new ArrayList<>();
        parseDataline(parsed, dataline);
        long frequency = Long.parseLong(parsed.get(0));
        long time = Math.round((double)System.currentTimeMillis()/(double)1000) - Long.parseLong(parsed.get(2));
        return 0;
    }
}
