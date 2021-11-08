package enigma;

import org.junit.Test;
import static org.junit.Assert.*;
import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Rotor class.
 *  @author Jay Chiang
 */

public class RotorTest {

    @Test
    public void testExample() {
        Permutation perm5 = new Permutation(
                "(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)", UPPER);
        Rotor rotor5 = new Rotor("rotor5", perm5);
        int setting = perm5.alphabet().toInt('F');
        rotor5.set(setting);
        int curr = rotor5.convertForward(setting);
        assertEquals("F -> I", perm5.alphabet().toInt('I'), curr);

        Permutation perm4 = new Permutation(
                "(AEPLIYWCOXMRFZBSTGJQNH) (DV) (KU)", UPPER);
        Rotor rotor4 = new Rotor("rotor4", perm4);
        rotor4.set(perm4.alphabet().toInt('L'));
        curr = rotor4.convertForward(curr);
        assertEquals("I -> V", perm4.alphabet().toInt('V'), curr);

        Permutation perm3 = new Permutation(
                "(ABDHPEJT) (CFLVMZOYQIRWUKXSG) (N)", UPPER);
        Rotor rotor3 = new Rotor("rotor3", perm3);
        rotor3.set(perm3.alphabet().toInt('X'));
        curr = rotor3.convertForward(curr);
        assertEquals("V -> J", perm3.alphabet().toInt('J'), curr);
    }

}
