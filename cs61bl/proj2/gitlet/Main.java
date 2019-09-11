package gitlet;


import java.io.File;

/**
 * Driver class for Gitlet, the tiny stupid version-control system.
 *
 * @author
 */
public class Main {


    /**
     * Only one instance variable: a GitRepo object REPO
     * 1. Take in the command as ARGS
     * 2. Check if there is a existing repo.store, if so let REPO point to it,
     *   if not let REPO = null
     * 3. Pass REPO and ARGS into a TaskDelegate object TD
     * 4. After being checked by TD, we then pass ARGS and REPO to a TaskManager object TM
     * 5. After TM run the commands, we write REPO to disk
     */

    /**
     * the .txt file at proj2/gitlet/blobStorage stores the blobs,
     *  which are the serialization codes
     */

    /**
     * Usage: java gitlet.Main ARGS, where ARGS contains
     * <COMMAND> <OPERAND> ....
     */


    public static final File WKDR = new File(System.getProperty("user.dir"));

    public static void main(String... args) {
        try {
            TaskDelegate.checkArgAndRun(args);
        } catch (BranchReadWriteException e) {
            e.printStackTrace();
        } catch (CommitReadWriteException e) {
            e.printStackTrace();
        }

    }
}
