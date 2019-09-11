package gitlet;


public class CommitReadWriteException extends Exception {
    public CommitReadWriteException(Exception ex) {
        super(ex);
    }
}
