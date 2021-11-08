package enigma;

import java.util.ArrayList;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Jay Chiang
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = new ArrayList<>();
        for (int i = 0; i < notches.length(); i += 1) {
            _notches.add(permutation().alphabet().toInt(notches.charAt(i)));
        }
        super.set(0);
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean atNotch() {
        return _notches.contains(_position);
    }

    @Override
    void advance() {
        if (_position == alphabet().size() - 1) {
            _position = 0;
        } else {
            _position += 1;
        }
    }

    /** list of notches. */
    private ArrayList<Integer> _notches;
}
