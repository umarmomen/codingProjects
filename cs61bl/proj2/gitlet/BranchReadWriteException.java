package gitlet;

public class BranchReadWriteException extends Exception {
    public BranchReadWriteException(Exception e) {
        super(e);
    }
}
