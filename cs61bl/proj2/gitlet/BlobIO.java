package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;


public class BlobIO {
    /**
     * @param fileHashCode the file name in the blob, which is the content
     * @param workingDR    the working directory
     * @return the byte[] of the file
     */
    public static byte[] deserializeBlob(String fileHashCode, File workingDR) {
        File gitlet = new File(workingDR, ".gitlet");
        File blobs = new File(gitlet, "blobs");
        File blobFile = new File(blobs, fileHashCode);
        byte[] content = null;
        try {
            FileInputStream fileIn = new FileInputStream(blobFile);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            content = (byte[]) in.readObject();
            in.close();
            fileIn.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        } catch (java.lang.ClassNotFoundException e) {
            e.printStackTrace();
        }
        return content;
    }
}
