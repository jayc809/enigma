package enigma;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Machine class.
 *  @author Jay Chiang
 */
public class MachineTest {

    private Machine machine;

    public void setupIdentity() {
        Permutation abc = new Permutation("", new Alphabet("ABC"));
        Rotor r4 = new MovingRotor("r4", abc, "C");
        Rotor r3 = new MovingRotor("r3", abc, "C");
        Rotor r2 = new MovingRotor("r2", abc, "C");
        Rotor r1 = new FixedRotor("r1", abc);
        ArrayList<Rotor> rotors = new ArrayList<>();
        rotors.add(r4);
        rotors.add(r3);
        rotors.add(r2);
        rotors.add(r1);
        machine = new Machine(new Alphabet("ABC"), 4, 3, rotors);
        machine.setPlugboard(abc);
        machine.insertRotors(new String[] {"r1", "r2", "r3", "r4"});
        machine.setRotors("AAA");
    }

    @Test
    public void testMachineIdentity() {
        setupIdentity();
        String[] compareString =
            new String[] {
                "AAAB",  "AAAC",  "AABA",  "AABB",
                "AABC",  "AACA", "ABAB",  "ABAC",
                "ABBA",  "ABBB",  "ABBC",  "ABCA",
                "ACAB",  "ACAC",  "ACBA",  "ACBB",
                "ACBC",  "ACCA", "AAAB"
            };
        for (int i = 0; i < 19; i += 1) {
            machine.convert(0);
            assertEquals(compareString[i], machine.getSetupString());
        }

        setupIdentity();
        assertEquals("ABCABC", machine.convert("ABC ABC"));

    }

    public void setupExample() {
        String tmp = "(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)";
        Permutation rIPerm = new Permutation(tmp, UPPER);
        Rotor rI = new MovingRotor("I", rIPerm, "Q");
        tmp = "(AEPLIYWCOXMRFZBSTGJQNH) (DV) (KU)";
        Permutation rIVPerm = new Permutation(tmp, UPPER);
        Rotor rIV = new MovingRotor("IV", rIVPerm, "J");
        tmp = "(ABDHPEJT) (CFLVMZOYQIRWUKXSG) (N)";
        Permutation rIIIPerm = new Permutation(tmp, UPPER);
        Rotor rIII = new MovingRotor("III", rIIIPerm, "V");
        tmp = "(ALBEVFCYODJWUGNMQTZSKPR) (HIX)";
        Permutation rBetaPerm = new Permutation(tmp, UPPER);
        Rotor rBeta = new FixedRotor("Beta", rBetaPerm);
        tmp = "(AE) (BN) (CK) (DQ)(FU) (GY) (HW) (IJ) (LO) (MP) (RX) (SZ) (TV)";
        Permutation rBPerm = new Permutation(tmp, UPPER);
        Rotor rB = new Reflector("B", rBPerm);
        ArrayList<Rotor> rotors = new ArrayList<>();
        rotors.add(rI);
        rotors.add(rIV);
        rotors.add(rIII);
        rotors.add(rBeta);
        rotors.add(rB);
        machine = new Machine(UPPER, 5, 3, rotors);
        machine.setPlugboard(new Permutation("(YF) (ZH)", UPPER));
        machine.insertRotors(new String[] {"B", "Beta", "III", "IV", "I"});
        machine.setRotors("AXLE");
    }

    @Test
    public void testMachineExample() {
        setupExample();
        assertEquals("Z", machine.convert("Y"));
        assertEquals("AAXLF", machine.getSetupString());

        setupExample();
        assertEquals("Y", machine.convert("Z"));

        setupExample();
        for (int i = 0; i < 13; i += 1) {
            machine.convert(0);
        }
        assertEquals("AAXMR", machine.getSetupString());
        for (int i = 0; i < 597; i += 1) {
            machine.convert(0);
        }
        assertEquals("AAXIQ", machine.getSetupString());
        machine.convert(0);
        assertEquals("AAXJR", machine.getSetupString());
        machine.convert(0);
        assertEquals("AAYKS", machine.getSetupString());
    }

}
