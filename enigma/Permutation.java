package enigma;

import java.util.ArrayList;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Jay Chiang
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = new ArrayList<>();
        _unmappedChars = new ArrayList<>();
        String cycleString = "";
        ArrayList<Character> usedChars = new ArrayList<>();
        for (int i = 0; i < cycles.length(); i += 1) {
            char ch = cycles.charAt(i);
            if (ch == '(') {
                cycleString = "";
            } else if (ch == ')') {
                addCycle(cycleString);
            } else if (_alphabet.contains(ch)) {
                cycleString += ch;
                usedChars.add(ch);
            }
        }
        for (int i = 0; i < _alphabet.size(); i += 1) {
            if (!usedChars.contains(_alphabet.toChar(i))) {
                _unmappedChars.add(_alphabet.toChar(i));
            }
        }

    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        ArrayList<Character> cycleList = new ArrayList<>();
        for (int i = 0; i < cycle.length(); i += 1) {
            cycleList.add(cycle.charAt(i));
        }
        _cycles.add(cycleList);
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        if (p == -1) {
            return p;
        }
        char p2 = _alphabet.toChar(wrap(p));
        return _alphabet.toInt(permute(p2));
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        if (c == -1) {
            return c;
        }
        char c2 = _alphabet.toChar(wrap(c));
        return _alphabet.toInt(invert(c2));
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        if (_unmappedChars.contains(p)) {
            return p;
        } else {
            for (int i = 0; i < _cycles.size(); i += 1) {
                ArrayList<Character> currCycle = _cycles.get(i);
                for (int j = 0; j < currCycle.size(); j += 1) {
                    if (p == currCycle.get(j)) {
                        if (j == currCycle.size() - 1) {
                            return currCycle.get(0);
                        } else {
                            return currCycle.get(j + 1);
                        }
                    }
                }
            }
        }
        return ' ';
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        if (_unmappedChars.contains(c)) {
            return c;
        } else {
            for (int i = 0; i < _cycles.size(); i += 1) {
                ArrayList<Character> currCycle = _cycles.get(i);
                for (int j = 0; j < currCycle.size(); j += 1) {
                    if (c == currCycle.get(j)) {
                        if (j == 0) {
                            return currCycle.get(currCycle.size() - 1);
                        } else {
                            return currCycle.get(j - 1);
                        }
                    }
                }
            }
        }
        return ' ';
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        return _unmappedChars.size() == 0;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** list of cycles. */
    private ArrayList<ArrayList> _cycles;
    /** list of unmapped characters. */
    private ArrayList<Character> _unmappedChars;
}
