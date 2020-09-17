import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class WarpDrive {

    // TODO: Bash support

    private static final File data = new File(System.getProperty("user.home") + "/.WarpDrive/WarpDriveData.txt");
    private static final ArrayList<String> lines = new ArrayList<>();
    private static final ArrayList<ArrayList<String>> linesWithPoints = new ArrayList<>();
    private static final Comparator<ArrayList<String>> sortByPointsDescending = (o1, o2) -> {
        double firstRank = Double.parseDouble(o1.get(3));
        double secondRank = Double.parseDouble(o2.get(3));
        return Double.compare(secondRank, firstRank);
    };
    private static boolean dataFileRead = false;

    public static void main(String[] args) {
        if (args.length == 0) {
            // don't print anything so cd gets no args
            System.exit(0);
        }

        if (args[0].equals("-")) {
            System.out.println("-");
            return;
        }

        if (args[0].equals("--help") || args[0].equals("-h")) {
            String n = System.lineSeparator();
            System.out.println("WarpDrive - Warp across directories" + n +
                    "Usage: wd [<option>] [<pattern> ...] " + n +
                    "Options:" + n +
                    "   --ls, -s                   Run ls after warping to a directory" + n +
                    "   --add, -a <path> ...       Add paths to be tracked. Paths are automatically added when visited" + n +
                    "   --remove, -r <path> ...    Remove paths so they are no longer tracked" + n +
                    "   --list, -l                 List tracked paths and their points, sorted by most" + n +
                    "   --update                   Update WarpDrive to the latest commit" + n +
                    "   --help, -h                 Print this help message" + n +
                    "   --version, -v              Print the version of WarpDrive you have" + n +
                    "   --check, -c                Check if a newer version is available" + n +
                    "Examples:" + n +
                    "   wd" + n +
                    "   wd dir-in-pwd" + n +
                    "   wd dir-that-was-visited-before" + n +
                    "   wd grand-parent-dir parent-dir child-dir" + n +
                    "   wd parent-dir grand-parent-dir child-dir" + n +
                    "   wd a-part-of-the-name-of-some-dir" + n +
                    "   wd /absolute/path/to/somewhere" + n +
                    "   wd -s run-ls-after-warping" + n +
                    "   wd --add dir-to-add" + n +
                    "   wd --remove dir-to-remove" + n +
                    "Note:" + n +
                    "   To go to the home directory don't specify any arguments, i.e. use just `wd` (like cd)" + n +
                    "   When specifying multiple patterns, order doesn't matter except for the last pattern given" + n +
                    "      i.e. WarpDrive will always take you to a directory whose name matches the last pattern" + n +
                    "   If <pattern> is specified after an option, <pattern> will be ignored unless the option is -s" + n +
                    "   No options can be combined (you can't use any two options at the same time)" + n +
                    "   Any output seen is on stderr" + n +
                    n +
                    "Refer to https://github.com/quackduck/WarpDrive for more information");
            System.exit(10);
            return;
        }

        if (args[0].equals("--add") || args[0].equals("-a")) {
            if (args.length == 1) {
                System.err.println("No argument for option: --add");
                System.exit(10);
            }

            for (int j = 1; j < args.length; j++) {
                addPath(args[j]);
            }
            System.exit(10);
            return;
        }

        if (args[0].equals("--remove") || args[0].equals("-r")) {
            if (args.length == 1) {
                System.err.println("No argument for option: --remove");
                System.exit(10);
            }

            for (int j = 1; j < args.length; j++) {
                removePath(args[j]);
            }
            System.exit(10); // cd .
        }

        if (args[0].equals("--list") || args[0].equals("-l")) {
            printDirsAndPoints();
            System.exit(10);
            // System.err.println(System.currentTimeMillis());
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
        linesWithPoints.sort(sortByPointsDescending);
        System.err.println("Points\tDirectory");
        for (ArrayList<String> parsedDataline : linesWithPoints) {
            System.out.println(parsedDataline.get(3) + "\t" + parsedDataline.get(0));
        }
        writeToDataFile();
    }

    public static String matchPattern(String pattern) {
        ArrayList<ArrayList<String>> finalists = new ArrayList<>();
        pattern = pattern.trim();
        String[] parsedPattern = pattern.split(" ");
        int hits;
        if (!dataFileRead) {
            readFromDataFile();
        }
        String candidatePath;
        for (ArrayList<String> parsedDataline : linesWithPoints) {
            hits = 0;
            candidatePath = parsedDataline.get(0);
            for (int i = 0; i < parsedPattern.length; i++) {
                if (i == parsedPattern.length - 1) {
                    String[] parsedCandidatePath = candidatePath.split("/");
                    if (parsedCandidatePath.length == 0) {
                        parsedCandidatePath = new String[1];
                    }
                    parsedCandidatePath[0] = "/";
                    if (parsedCandidatePath[parsedCandidatePath.length - 1].toLowerCase()
                            .contains(parsedPattern[parsedPattern.length - 1].toLowerCase())) {
                        hits++;
                    }
                } else if (candidatePath.toLowerCase().contains(parsedPattern[i].toLowerCase())) {
                    hits++;
                }
            }
            if (hits == parsedPattern.length) {
                finalists.add(parsedDataline);
            }
        }
        if (finalists.size() >= 1) {
            finalists.sort(sortByPointsDescending);
            return finalists.get(0).get(0);
        }
        return pattern;
    }

    public static void addPath(String path) {
        File dir = new File(path);
        if (!dir.isDirectory()) {
            return;
        }
        try {
            if (dir.getCanonicalPath().equals(System.getProperty("user.home"))) {
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!dataFileRead) {
            readFromDataFile();
        }
        try {
            path = dir.getCanonicalPath();
            boolean dirWasFound = false;
            for (int i = 0, linesSize = linesWithPoints.size(); i < linesSize; i++) {
                if (linesWithPoints.get(i).get(0).equals(path)) {
                    dirWasFound = true;
                    linesWithPoints.get(i).set(1, String.valueOf(Integer.parseInt(linesWithPoints.get(i).get(1)) + 1));
                    linesWithPoints.get(i).set(2,
                            String.valueOf(Math.round((double) System.currentTimeMillis() / 1000.0)));
                    linesWithPoints.get(i).set(3, String.valueOf(points(linesWithPoints.get(i))));
                    break;
                }
            }
            if (!dirWasFound) {
                ArrayList<String> newLine = new ArrayList<>();
                newLine.add(path);
                newLine.add("1");
                newLine.add(String.valueOf(Math.round((double) System.currentTimeMillis() / 1000.0)));
                newLine.add(String.valueOf(points(newLine)));
                linesWithPoints.add(newLine);
            }
            writeToDataFile();
        } catch (Exception e) {
            System.err.println(
                    "Error while trying to add a path (This has probably happened because of bad code @quackduck wrote)");
            e.printStackTrace();
            System.exit(2);
        }

    }

    public static void removePath(String path) {
        File dir = new File(path);
        if (!dataFileRead) {
            readFromDataFile();
        }
        if (!dir.exists()) {
            return;
        }
        try {
            path = dir.getCanonicalPath();
            for (int i = 0, linesSize = linesWithPoints.size(); i < linesSize; i++) {
                if (linesWithPoints.get(i).get(0).equalsIgnoreCase(path)) {
                    linesWithPoints.remove(i);
                    break;
                }
            }
            writeToDataFile();
        } catch (Exception e) {
            System.err.println(
                    "Error while trying to remove a path (This has probably happened because of bad code @quackduck wrote)");
            e.printStackTrace();
            System.exit(2);
        }

    }

    private static boolean pathExists(String path) {
        return new File(path).isDirectory();
    }

    private static void readFromDataFile() {
        lines.clear();
        String line;
        FileReader fr = null;
        if (!data.exists()) {
            try {
                // noinspection ResultOfMethodCallIgnored
                data.getParentFile().mkdirs();
                data.createNewFile();
            } catch (IOException e) {
                System.err.println("Could not create the datafile");
                e.printStackTrace();
                System.exit(3);
            }
        }
        try {
            fr = new FileReader(data);
        } catch (FileNotFoundException ignored) {
        }
        BufferedReader br = new BufferedReader(fr);
        try {
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.trim().isEmpty() && pathExists(parseDataline(line, 0))) {
                    ArrayList<String> parsedLine = new ArrayList<>();
                    parseDataline(parsedLine, line);
                    parsedLine.add(Double.toString(points(line)));
                    if (Double.parseDouble(parsedLine.get(3)) >= 1) {
                        linesWithPoints.add(parsedLine);
                    }
                }
            }
            fr.close();
            br.close();
        } catch (IOException e) {
            System.err.println("Could not read from datafile or close resources attached");
            e.printStackTrace();
            System.exit(4);
        }
        dataFileRead = true;
    }

    private static void writeToDataFile() {
        try {
            FileWriter fw = new FileWriter(data);
            BufferedWriter out = new BufferedWriter(fw);
            for (ArrayList<String> parsed : linesWithPoints) {
                out.write(unParseDataline(parsed) + System.lineSeparator());
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println("Could not write to datafile or close resources attached");
            e.printStackTrace();
            System.exit(4);
        }

    }

    private static void parseDataline(ArrayList<String> parsed, String line) {
        String[] parsedArr = line.split("\\|"); // the two slashes escape the pipe character used as a delimiter
        parsed.clear();
        Collections.addAll(parsed, parsedArr);
        while (parsed.size() > 3) {
            parsed.add(0, parsed.get(0) + "|" + parsed.get(1));
            parsed.remove(1);
            parsed.remove(1);
        }
        // return parsed;
    }

    private static ArrayList<String> parseDataline(String line) {
        ArrayList<String> parsed = new ArrayList<>();
        parseDataline(parsed, line);
        return parsed;
    }

    private static String parseDataline(String line, int i) {
        return parseDataline(line).get(i);
    }

    private static String unParseDataline(ArrayList<String> parsed) {
        return parsed.get(0) + "|" + parsed.get(1) + "|" + parsed.get(2);
    }

    private static double points(String dataline) {
        ArrayList<String> parsed = new ArrayList<>();
        parseDataline(parsed, dataline);
        return points(parsed);
    }

    private static double points(ArrayList<String> parsed) {
        int frequency = Integer.parseInt(parsed.get(1));
        int time = (int) (Math.round((double) System.currentTimeMillis() / (double) 1000)
                - Long.parseLong(parsed.get(2)));
        double result;
        if (time <= 60) { // last minute
            result = frequency * 16;
        } else if (time <= 1800) { // last half hour
            result = frequency * 8;
        } else if (time <= 3600) { // last hour
            result = frequency * 4;
        } else if (time <= (3600 * 12)) { // last half day
            result = frequency * 2;
        } else if (time <= (3600 * 24) * 7) { // last week
            result = frequency;
        } else if (time <= (3600 * 24) * 7 * 2) { // last fortnight
            result = frequency * 0.5;
        } else if (time <= (3600 * 24) * 7 * 4 * 2) { // approx last two months
            result = frequency * 0.25;
        } else {
            result = 0;
        }
        return (double) Math.round(result * 100) / (double) 100;
    }
}
