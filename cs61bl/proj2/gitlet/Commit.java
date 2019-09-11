package gitlet;

import java.io.Serializable;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;


public class Commit implements Serializable {

    /**
     * Create commit objects based on the commands.
     * Store the blobs into the blobStorage file
     */

    /**
     * Instead of pointing at the commit object, PARENT is the SHA-1 code of the parent commit
     * This avoids redundant serialization when adding a new commit
     */
    private String parent;

    /**
     * MESSAGE is the log message
     */
    private String message;

    /**
     * Date of the commit
     */
    private Date commitDate;

    /**
     * A mapping of file names to the SHA-1's of their blobs.
     */
    private SortedMap<String, String> contents = new TreeMap<>();

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCommitDate() {
        return commitDate;
    }

    public void setCommitDate(Date commitDate) {
        this.commitDate = commitDate;
    }

    public SortedMap<String, String> getContents() {
        return contents;
    }

    public void setContents(SortedMap<String, String> contents) {
        this.contents = contents;
    }


    public String getSha() {
        return Utils.sha1((parent == null ? "" : parent),
                message,
                commitDate.toString(),
                contents.toString());
    }

//    @Override
//    public boolean equals(Object that) {
//        if (that == this) {
//            return true;
//        }
//        if (!(that instanceof Commit)) {
//            return false;
//        }
//        if (this.parent.equals(((Commit) that).parent)
//                && this.commitDate.equals(((Commit) that).commitDate)
//                && this.message.equals(((Commit) that).message)
//                && this.contents.equals(((Commit) that).contents)) {
//            return true;
//        }
//        return false;
//    }

    @Override
    public String toString() {
        return "parent=" + parent
                + ", message=" + message
                + ", commitDate=" + (commitDate == null ? "" : commitDate.toString())
                + ", contents=" + (contents == null ? "" : contents.toString());
    }

    public Commit(String parent, String message, Date commitDate,
                  SortedMap<String, String> contents) {
        this.parent = parent;
        this.message = message;
        this.commitDate = commitDate;
        this.contents = contents;
    }
}
