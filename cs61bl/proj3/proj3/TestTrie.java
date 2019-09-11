import ucb.junit.textui;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import static org.junit.Assert.*;

/** The suite of all JUnit tests for the Trie class.
 *  @author
 */
public class TestTrie {

    @Test
    public void testPartialTrie() {
        Trie t = new Trie();
        t.insert("will", 1);
        t.insert("william", 2.0);
        t.insert("williams", 3.3);
        assertTrue(t.find("w", false));
        assertTrue(t.find("wi", false));
        assertTrue(t.find("will", false));
        assertTrue(t.find("will", true));
        assertEquals(t.getTopMatch("will"), "williams");
    }


    @Test
    public void testNewLanguageTrie() {
        NewLanguageTrie t = new NewLanguageTrie();
        t.insert("will");
        t.insert("william");
        t.insert("williams");
        assertTrue(t.find("w", false));
        assertTrue(t.find("wi", false));
        assertTrue(t.find("will", false));
        assertTrue(t.find("will", true));
    }

    @Test
    public void testNLTrieSort() {
        NewLanguageTrie tr = new NewLanguageTrie();
        try {
            BufferedReader br
                = new BufferedReader(new InputStreamReader(new FileInputStream("wiktionary.txt")));
            int numLines = Integer.valueOf(br.readLine());
            String[] terms = new String[numLines];
            for (int i = 0; i < numLines; i++) {
                String[] line = br.readLine().split("\t");
                String t = line[1];
                terms[i] = t;
            }
            for (String t : terms) {
                tr.insert(t);
            }
            assertEquals(tr.sortedWords().get(1), "American");
            assertEquals(tr.sortedWords().get(7), "abandoning");
            assertEquals(tr.sortedWords().get(0), "America");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /** Run the JUnit tests above. */
    public static void main(String[] ignored) {
        textui.runClasses(TestTrie.class);
    }

}
