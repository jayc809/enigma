package enigma;

import java.util.ArrayList;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Jay Chiang
 */
class Alphabet {

    /** A new alphabet containing CHARS. The K-th character has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        _set = new ArrayList<Character>();
        for (int i = 0; i < chars.length(); i += 1) {
            if (!_set.contains(chars.charAt(i))) {
                _set.add(chars.charAt(i));
            }
        }
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _set.size();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        return _set.contains(ch);
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        return _set.get(index);
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        return _set.indexOf(ch);
    }

    /** Stores chars of an instance.*/
    private ArrayList<Character> _set;

}
