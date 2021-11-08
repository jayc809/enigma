package enigma;

import java.util.Collection;
import java.util.ArrayList;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Jay Chiang
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        if (1 < numRotors) {
            _numRotors = numRotors;
        } else {
            throw new EnigmaException("numRotors not > 1");
        }
        if (0 <= pawls && pawls < numRotors) {
            _pawls = pawls;
        } else {
            throw new EnigmaException("numPawls not >= 0 and < numRotors");
        }
        _availableRotors = new ArrayList<>(allRotors);
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _rotors = new ArrayList<>();
        for (int i = 0; i < rotors.length; i += 1) {
            for (int j = 0; j < _availableRotors.size(); j += 1) {
                Rotor currRotor = _availableRotors.get(j);
                if (rotors[i].equals(currRotor.name())) {
                    currRotor.set(0);
                    _rotors.add(currRotor);
                }
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (!(setting.length() == _numRotors - 1)) {
            throw new EnigmaException("bad input");
        }
        for (int i = 0; i < setting.length(); i += 1) {
            _rotors.get(i + 1).set(setting.charAt(i));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        ArrayList<Integer> rotatingRotors = new ArrayList<>();
        if (_rotors.size() > 2) {
            for (int i = _rotors.size() - 1; i > 1; i -= 1) {
                Rotor currRotor = _rotors.get(i);
                Rotor nextRotor = _rotors.get(i - 1);
                if (currRotor.atNotch() && nextRotor.rotates()) {
                    if (!rotatingRotors.contains(i)) {
                        rotatingRotors.add(i);
                    }
                    if (!rotatingRotors.contains(i - 1)) {
                        rotatingRotors.add(i - 1);
                    }
                } else if (i == _rotors.size() - 1) {
                    rotatingRotors.add(i);
                }
            }
        } else if (_rotors.size() == 2) {
            rotatingRotors.add(1);
        }
        for (int i = 0; i < rotatingRotors.size(); i += 1) {
            _rotors.get(rotatingRotors.get(i)).advance();
        }

        setupString = "";
        for (int k = 0; k < _rotors.size(); k += 1) {
            setupString += _alphabet.toChar(_rotors.get(k)._position);
        }

        int input = _plugboard.permute(c);
        for (int i = _rotors.size() - 1; i > 0; i -= 1) {
            input = _rotors.get(i).convertForward(input);
        }
        input = _rotors.get(0).convertForward(input);
        for (int i = 1; i < _rotors.size(); i += 1) {
            input = _rotors.get(i).convertBackward(input);
        }
        return _plugboard.permute(input);
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String outputMsg = "";
        for (int i = 0; i < msg.length(); i += 1) {
            char ch = msg.charAt(i);
            if (_alphabet.contains(ch)) {
                outputMsg += _alphabet.toChar(convert(_alphabet.toInt(ch)));
            }
        }
        return outputMsg;
    }

    /** Return AVAILABLEROTORS. */
    public ArrayList<Rotor> getAvailableRotors() {
        return _availableRotors;
    }

    /** Return ROTORS. */
    public ArrayList<Rotor> getRotors() {
        return _rotors;
    }

    /** returns SETUPSTRING. */
    public String getSetupString() {
        return setupString;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** number of rotors. */
    private int _numRotors;
    /** number of pawls. */
    private int _pawls;
    /** list of available rotors. */
    private ArrayList<Rotor> _availableRotors;
    /** list of current rotors. */
    private ArrayList<Rotor> _rotors;
    /** permutation of plugboard. */
    private Permutation _plugboard;
    /** string of current setting. */
    private String setupString;
}
