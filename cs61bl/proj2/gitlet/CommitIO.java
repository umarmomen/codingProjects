package gitlet;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.ArrayList;

public class CommitIO {
    public static final String GITLET = ".gitlet";
    public static final String COMMIT = "commits";

    /**
     * write the commit into the commit folder as a file
     *
     * @param c         the given commit
     * @param workingDR the working directory
     * @throws CommitReadWriteException
     */
    public static void write(Commit c, File workingDR) throws CommitReadWriteException {
        File gitdir = new File(workingDR, GITLET);
        File commitdir = new File(gitdir, COMMIT);
        File outFile = new File(commitdir, c.getSha());
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(c);
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new CommitReadWriteException(ex);
        }
    }

    /**
     * @param workingDR the working directory
     * @param hashCode  the commit file name, which is the sha-code
     * @return the commit deserialized from the commit folder
     * @throws CommitReadWriteException
     */
    public static Commit read(File workingDR, String hashCode) throws CommitReadWriteException {
        Commit c;
        try {
            File gitdir = new File(workingDR, GITLET);
            File commitdir = new File(gitdir, COMMIT);
            File commitFile = new File(commitdir, hashCode);
            FileInputStream fileIn = new FileInputStream(commitFile);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            c = (Commit) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException i) {
            i.printStackTrace();
            throw new CommitReadWriteException(i);
        }
        return c;
    }


    /**
     * @param workingDR the working directory
     * @return an arraylist of all the commits ever created
     */
    public static ArrayList<Commit> getAllCommits(File workingDR) {
        File gitlet = new File(workingDR, ".gitlet");
        File commits = new File(gitlet, "commits");
        ArrayList<Commit> commitList = new ArrayList<>();
        if (commits.isDirectory()) {
            String[] commitFileList = commits.list();
            for (String fname : commitFileList) {
                try {
                    commitList.add(CommitIO.read(workingDR, fname));
                } catch (CommitReadWriteException e) {
                    e.printStackTrace();
                }
            }
        }
        return commitList;
    }

    /**
     * taking in a byte[] and a file name and write them in the blobs directory
     *
     * @param workingDR the working directory
     * @param content   the byte[] that will be serialized
     * @param sha       the sha-code, which will be the future file name
     */
    public static void writeFiles(File workingDR, byte[] content, String sha) {

        FileOutputStream fos = null;
        try {
            File gitlet = new File(workingDR, ".gitlet");
            File blobs = new File(gitlet, "blobs");
            File inFile = new File(blobs, sha);
            fos = new FileOutputStream(inFile);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(content);
            out.close();
            fos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

}
