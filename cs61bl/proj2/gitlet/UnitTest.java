package gitlet;

import ucb.junit.textui;
import org.junit.Test;



/**
 * The suite of all JUnit tests for the gitlet package.
 *
 * @author
 */
public class UnitTest {

    /**
     * Run the JUnit tests in the loa package. Add xxxTest.class entries to
     * the arguments of runClasses to run other JUnit tests.
     */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
    }

    /**
     * A dummy test to avoid complaint.
     */
    @Test
    public void placeholderTest() {
    }

    @Test
    public void testInit() {
        String[] args = new String[]{"init"};
        Main.main(args);
    }

    @Test
    public void testAdd1() {
        String[] args = new String[]{"add", "g.txt"};
        Main.main(args);

    }

    @Test
    public void testAdd2() {
        String[] args = new String[]{"add", "f.txt"};
        Main.main(args);

    }

    @Test
    public void testCommit1() {
        String[] args = new String[]{"commit", "added gf"};
        Main.main(args);
    }

    @Test
    public void testBranch() {
        String[] args = new String[]{"branch", "other"};
        Main.main(args);
    }

    @Test
    public void testAdd3() {
        String[] args = new String[]{"add", "h.txt"};
        Main.main(args);

    }

    @Test
    public void testRemove1() {
        String[] args = new String[]{"rm", "g.txt"};
        Main.main(args);
    }


    @Test
    public void testCommit2() {
        String[] args = new String[]{"commit", "Add h.txt and removed g.txt"};
        Main.main(args);
    }

    @Test
    public void testCheckOut1() {
        String[] args = new String[]{"checkout", "other"};
        Main.main(args);
    }

    @Test
    public void testRemovef() {
        String[] args = new String[]{"rm", "f.txt"};
        Main.main(args);
    }

    @Test
    public void testCommit() {
        String[] args = new String[]{"commit", "--"};
        Main.main(args);
    }

    @Test
    public void testAddk() {
        String[] args = new String[]{"add", "k.txt"};
        Main.main(args);

    }

    @Test
    public void testCommit3() {
        String[] args = new String[]{"commit", "Add k.txt and removed f.txt"};
        Main.main(args);
    }

    @Test
    public void testCheckOut() {
        String[] args = new String[]{"checkout", "master"};
        Main.main(args);
    }

    @Test
    public void testMerge() {
        String[] args = new String[]{"merge", "other"};
        Main.main(args);
    }

    @Test
    public void testLog() {
        String[] args = new String[]{"log"};
        Main.main(args);
    }

    @Test
    public void testStatus() {
        String[] args = new String[]{"status"};
        Main.main(args);
    }

///////////////
    ///////////////

    ///////////////////

    @Test
    public void test1() {
        String[] args = new String[]{"init"};
        Main.main(args);
    }

    @Test
    public void test2() {
        String[] args = new String[]{"add", "f.txt"};
        Main.main(args);
    }

    @Test
    public void test3() {
        String[] args = new String[]{"add", "f.txt"};
        Main.main(args);
    }

    @Test
    public void test4() {
        String[] args = new String[]{"add", "f.txt"};
        Main.main(args);
    }

    @Test
    public void test5() {
        String[] args = new String[]{"add", "f.txt"};
        Main.main(args);
    }

    @Test
    public void test6() {
        String[] args = new String[]{"add", "f.txt"};
        Main.main(args);
    }

    @Test
    public void test7() {
        String[] args = new String[]{"init"};
        Main.main(args);
    }

    @Test
    public void test8() {
        String[] args = new String[]{"init"};
        Main.main(args);
    }

    @Test
    public void test9() {
        String[] args = new String[]{"init"};
        Main.main(args);
    }

    @Test
    public void test10() {
        String[] args = new String[]{"init"};
        Main.main(args);
    }

    @Test
    public void test11() {
        String[] args = new String[]{"init"};
        Main.main(args);
    }

    @Test
    public void test12() {
        String[] args = new String[]{"init"};
        Main.main(args);
    }

    @Test
    public void test13() {
        String[] args = new String[]{"init"};
        Main.main(args);
    }

    @Test
    public void test14() {
        String[] args = new String[]{"init"};
        Main.main(args);
    }


}


