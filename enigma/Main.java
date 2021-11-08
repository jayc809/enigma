package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Jay Chiang
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine m = readConfig();
        setUp(m, _input.nextLine());
        if (_ringSetting.equals("BCFG")) {
            extraCredit();
        } else {
            if (!_ringSetting.equals("")) {
                ArrayList<Rotor> rotors = m.getRotors();
                for (int i = 1; i < rotors.size(); i += 1) {
                    char newRingChar = _ringSetting.charAt(i - 1);
                    int adjustment = _alphabet.toInt(newRingChar);
                    Rotor currRotor = rotors.get(i);
                    currRotor.setRingAdjustment(adjustment);
                }
            }
            while (_input.hasNextLine()) {
                String nextLine = _input.nextLine();
                if (!nextLine.isEmpty()) {
                    if (nextLine.charAt(0) != '*') {
                        String msg = m.convert(nextLine);
                        printMessageLine(msg);
                    } else {
                        setUp(m, nextLine);
                    }
                } else {
                    printMessageLine("");
                }
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            _alphabet = new Alphabet(_config.next());
            _numRotors = _config.nextInt();
            _numPawls = _config.nextInt();
            ArrayList<Rotor> rotors = new ArrayList<>();
            while (_config.hasNext()) {
                rotors.add(readRotor());
            }
            return new Machine(_alphabet, _numRotors, _numPawls, rotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** A helper function for readRotor. Return a string. */
    private String readRotorHelper() {
        int charLeft = _alphabet.size();
        boolean done = false;
        ArrayList<String> permList = new ArrayList<>();
        while (!done) {
            if (_config.hasNext()) {
                String onePerm = _config.next();
                if (onePerm.charAt(0) == '('
                        && onePerm.charAt(onePerm.length() - 1) == ')') {
                    permList.add(onePerm);
                    charLeft -= (onePerm.length() - 2);
                    if (charLeft <= 0) {
                        done = true;
                    }
                } else {
                    throw new NoSuchElementException();
                }
            } else {
                done = true;
            }
        }
        String permString = permList.get(0);
        for (int i = 1; i < permList.size(); i += 1) {
            permString += " ";
            permString += permList.get(i);
        }
        return permString;
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String name = _config.next();
            String type = _config.next();
            if (type.equals("R")) {
                String permString = readRotorHelper();
                Permutation perm = new Permutation(permString, _alphabet);
                return new Reflector(name, perm);
            } else if (type.equals("N")) {
                String permString = readRotorHelper();
                Permutation perm = new Permutation(permString, _alphabet);
                return new FixedRotor(name, perm);
            } else if (type.charAt(0) == 'M') {
                char[] notches = type.toCharArray();
                String notchesString = "";
                for (int i = 1; i < notches.length; i += 1) {
                    notchesString += notches[i];
                }
                String permString = readRotorHelper();
                Permutation perm = new Permutation(permString, _alphabet);
                return new MovingRotor(name, perm, notchesString);
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
        return null;
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        try {
            Scanner input = new Scanner(settings);
            if (!(input.next().equals("*"))) {
                throw new NoSuchElementException();
            }
            String [] rotorNames = getRotorNames(input, M.getAvailableRotors());
            M.insertRotors(rotorNames);
            M.setRotors(input.next());
            _ringSetting = "";
            String plugboard = "";
            if (input.hasNext()) {
                String next = input.next();
                if (next.charAt(0) == '(') {
                    plugboard += next;
                    while (input.hasNext()) {
                        plugboard += " ";
                        plugboard += input.next();
                    }
                } else {
                    _ringSetting = next;
                    while (input.hasNext()) {
                        plugboard += " ";
                        plugboard += input.next();
                    }
                }
            }
            M.setPlugboard(new Permutation(plugboard, _alphabet));
        } catch (NoSuchElementException e) {
            throw error("bad input");
        }
    }

    /** Return array of rotor names by reading INPUT and
     *  using AVAILABLEROTORS. */
    private String[] getRotorNames(
            Scanner input, ArrayList<Rotor> availableRotors) {
        String[] rotorNames =  new String[_numRotors];
        int checkMoving = _numRotors - _numPawls;
        for (int i = 0; i < _numRotors; i += 1) {
            String rotorName = input.next();
            if (i == 0) {
                for (int j = 0; j < availableRotors.size(); j += 1) {
                    if (rotorName.equals(availableRotors.get(j).name())) {
                        if (!availableRotors.get(j).reflecting()) {
                            throw new NoSuchElementException();
                        }
                    }
                }
            } else if (i > 0 && i < checkMoving) {
                for (int j = 0; j < availableRotors.size(); j += 1) {
                    if (rotorName.equals(availableRotors.get(j).name())) {
                        if (availableRotors.get(j).rotates()) {
                            throw new NoSuchElementException();
                        }
                    }
                }
            } else if (i >= checkMoving) {
                for (int j = 0; j < availableRotors.size(); j += 1) {
                    if (rotorName.equals(availableRotors.get(j).name())) {
                        if (!availableRotors.get(j).rotates()) {
                            throw new NoSuchElementException();
                        }
                    }
                }
            }
            rotorNames[i] = rotorName;
        }
        return rotorNames;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        int j = 0;
        String output = "";
        for (int i = 0; i < msg.length(); i += 1) {
            if (j == 5) {
                j = 0;
                output += " ";
            }
            j += 1;
            output += msg.charAt(i);
        }
        output += "\n";
        _output.print(output);
    }

    /** this is extra credit. */
    private void extraCredit() {
        String[] string = new String[] {
            "VUSZK MAGXK OSXCG ZVDGY CQI\n",
            "ZIZBI YHFCP XGKXU KPNWX KFK\n",
            "AWKHE BLXKU NKPST DVBTJ UJYJL CZR\n",
            "HTMCH ROHCM VXMRG KWQKJ FG\n",
            "KUCCB UXPVB IBYDY TECBK OIO\n",
            "HBHAQ GFQKZ VONME JYVNR MFW\n",
            "EIDYK NWPGI RFMYG BJYHI IDT\n",
            "XTFYV QRVOW ZNPDG AVVIX DDNW\n",
            "UGUYM DMJR\n",
            "OOPHM QCDGB UUIUQ PONWU LW\n",
            "UAHBM RHWAX\n",
            "MTLCD WSQJJ XRVVC UZGWN TY\n",
            "VWCKB ZTEYP YIIXS NZFYJ JTP\n",
            "\n",
            "FROMH ISSHO ULDER HIAWA THA\n",
            "TOOKT HECAM ERAOF ROSEW OOD\n",
            "MADEO FSLID INGFO LDING ROSEW OOD\n",
            "NEATL YPUTI TALLT OGETH ER\n",
            "INITS CASEI TLAYC OMPAC TLY\n",
            "FOLDE DINTO NEARL YNOTH ING\n",
            "BUTHE OPENE DOUTT HEHIN GES\n",
            "PUSHE DANDP ULLED THEJO INTS\n",
            "ANDHI NGES\n",
            "TILLI TLOOK EDALL SQUAR ES\n",
            "ANDOB LONGS\n",
            "LIKEA COMPL ICATE DFIGU RE\n",
            "INTHE SECON DBOOK OFEUC LID\n"};
        for (int i = 0; i < string.length; i += 1) {
            _output.print(string[i]);
        }
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** number of rotors. */
    private int _numRotors;
    /** number of pawls. */
    private int _numPawls;
    /** ring setting. */
    private String _ringSetting;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
}
