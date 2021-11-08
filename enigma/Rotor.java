package enigma;

import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Jay Chiang
 */
class Rotor {

    /** A rotor named NAME whose permutation is given by PERM. */
    Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        _position = 0;
    }

    /** Return my name. */
    String name() {
        return _name;
    }

    /** Return my alphabet. */
    Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    int size() {
        return _permutation.size();
    }

    /** Return true iff I have a ratchet and can move. */
    boolean rotates() {
        return false;
    }

    /** Return true iff I reflect. */
    boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    int setting() {
        return _permutation.wrap(_position);
    }

    /** Set setting() to POSN.  */
    void set(int posn) {
        _position = posn;
    }

    /** Set setting() to character CPOSN. */
    void set(char cposn) {
        _position = _permutation.alphabet().toInt(cposn);
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    int convertForward(int p) {
        return _permutation.wrap(
                _permutation.permute(p + _position - _ringAdjustment)
                        - _position + _ringAdjustment);
    }

    /** Return the conversion of E (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    int convertBackward(int e) {
        return _permutation.wrap(
                _permutation.invert(e + _position - _ringAdjustment)
                        - _position + _ringAdjustment);
    }

    /** Returns true iff I am positioned to allow the rotor to my left
     *  to advance. */
    boolean atNotch() {
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    void advance() {

    }

    /** Return POSITION. */
    public int getPosition() {
        return _position;
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** sets the ring adjustment using ADJUSTMENT. */
    public void setRingAdjustment(int adjustment) {
        _ringAdjustment = adjustment;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;

    /** the position of the rotor. */
    protected int _position;

    /** ring adjustments. */
    protected int _ringAdjustment = 0;

}
