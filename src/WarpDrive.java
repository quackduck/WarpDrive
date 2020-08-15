import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.*;

/**
* This is a work in progress
* MacOS will be supported first.
* Inspired by rupa/z, autojump, zsh-z, and z.lua. Moving to fish meant I couldn't use rupa/z anymore.(Edit: there is actually a fish z from jethrokuan/z. I will still continue to work on this)
* I had to install lua just so I could use a program similar to rupa/z
*/
public class WarpDrive {

    private static final File data = new File(System.getProperty("user.home") + "/.WarpDriveData");
    private static final ArrayList<String> lines = new ArrayList<>();
    private static boolean dataFileRead = false;
    private static boolean debug = false;

    public static void main(String[] args) {
        //System.err.println(System.currentTimeMillis());
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
            return;
        }

        if (args[0].equalsIgnoreCase("--list") || args[0].equalsIgnoreCase("--ls") || args[0].equalsIgnoreCase("-l")) {
            printDirsAndPoints();
            System.out.println("."); // cd .
            //System.err.println(System.currentTimeMillis());
            return;
        }

        String match = "";
        for (String arg : args) {
            match += arg + " ";
        }
        match = matchPattern(match);
        addPath(match);
        System.out.println(match); // cd <match of args[0]>
    }

    public static void printDirsAndPoints() {
        readFromDataFile();
        ArrayList<String> parsedDataline = new ArrayList<>();
        lines.sort((o1, o2) -> {
            double firstRank = points(o1);
            double secondRank = points(o2);
            return Double.compare(secondRank, firstRank);
        });
        System.err.println("Points\tDirectory");
        for (String line: lines) {
            parseDataline(parsedDataline, line);
            System.err.println(points(line) + "\t" + parsedDataline.get(0));
        }
        writeToDataFile();
    }

    public static String matchPattern(String pattern) {
        ArrayList<String> finalists = new ArrayList<>();
        pattern = pattern.trim();
        String[] parsedPattern = pattern.split(" ");
        int hits;
        if (!dataFileRead) {
            readFromDataFile();
        }
        ArrayList<String> parsedDataline = new ArrayList<>();
        String candidatePath;
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
        if (finalists.size() >= 1) {
            finalists.sort((o1, o2) -> {
                double firstRank = points(o1);
                double secondRank = points(o2);
                return (int) (secondRank - firstRank);
            });
            //System.err.println("finalists: " + finalists);
            return parseDataline(finalists.get(0), 0);
        }
        return pattern;
    }

    public static void addPath(String path) {
        File dir = new File(path);
        if (!dir.isDirectory()) {
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
        return new File(path).isDirectory();
    }

    private static void readFromDataFile() {
        lines.clear();
        String line;
        FileReader fr = null;
        if (!data.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
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
                if (!line.isBlank() && pathExists(parseDataline(line, 0))) {
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
        Collections.addAll(parsed, parsedArr);
        while (parsed.size() > 3) {
            parsed.add(0, parsed.get(0) + parsed.get(1));
            parsed.remove(1);
        }
        //return parsed;
    }
    private static ArrayList<String> parseDataline(String line) {
        String[] parsedArr = line.split("\\|"); // the two slashes escape the pipe character used as a delimiter
        ArrayList<String> parsed = new ArrayList<>(Arrays.asList(parsedArr));
        while (parsed.size() > 3) {
            parsed.add(0, parsed.get(0) + parsed.get(1));
            parsed.remove(1);
        }
        return parsed;
    }

    private static String parseDataline(String line, int i) {
        String[] parsedArr = line.split("\\|"); // the two slashes escape the pipe character used as a delimiter
        ArrayList<String> parsed = new ArrayList<>(Arrays.asList(parsedArr));
        while (parsed.size() > 3) {
            parsed.add(0, parsed.get(0) + parsed.get(1));
            parsed.remove(1);
        }
        return parsed.get(i);
    }

    private static double points(String dataline) {
        ArrayList<String> parsed = new ArrayList<>();
        parseDataline(parsed, dataline);
        int frequency = Integer.parseInt(parsed.get(1));
        int time = (int)(Math.round((double)System.currentTimeMillis()/(double)1000) - Long.parseLong(parsed.get(2)));
        double result;
        if (time <= 60) { // last minute
            result = frequency * 16;
        } else if (time <= 1800) { // last half hour
            result = frequency * 8;
        } else if (time <= 3600) { // last hour
            result = frequency * 4;
        } else if (time <= (3600*12)) { // last half day
            result = frequency * 2;
        } else if (time <= (3600*24)*7) { // last week
            result = frequency;
        } else if (time <= (3600*24)*7*2) {
            result = frequency * 0.5;
        } else {
            result = frequency * 0.25;
        }
        return (double)Math.round(result*100)/(double)100;
    }
}
