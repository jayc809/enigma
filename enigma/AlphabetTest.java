package enigma;

import org.junit.Test;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Alphabet class.
 *  @author Jay Chiang
 */

public class AlphabetTest {

    @Test
    public void runTest() {
        Alphabet alphabet = new Alphabet("abcd");
        assertEquals(4, alphabet.size());
        assertEquals(true, alphabet.contains('a'));
        assertEquals(false, alphabet.contains('e'));
        assertEquals('c', alphabet.toChar(2));
        assertEquals(1, alphabet.toInt('b'));
        assertEquals(2, alphabet.toInt(alphabet.toChar(2)));
    }

}
