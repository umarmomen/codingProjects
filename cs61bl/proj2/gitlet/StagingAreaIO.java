package gitlet;

import java.io.IOException;
import java.io.File;
import java.util.SortedMap;

public class StagingAreaIO {

    /**
     * copy the file into the added staging area
     *
     * @param fileName  the name of the file to be copied
     * @param workingDR the working directory
     */
    public static void add(String fileName, File workingDR) {
        if (!StagingAreaIO.ifInRemovedFiles(fileName, workingDR)) {
            if (StagingAreaIO.checkIfValidToAdd(fileName, workingDR)) {
                File gitlet = new File(workingDR, ".gitlet");
                File stagingArea = new File(gitlet, "stagingArea");
                // operatingArea is if added or removed folder
                File operatingArea = new File(stagingArea, "addedFiles");
                // operatingFile is the file object
                File operatingFile = new File(workingDR, fileName);
                byte[] content = Utils.readContents(operatingFile);
                //create a file with the identical name
                File createdFile = new File(operatingArea, fileName);
                try {
                    createdFile.createNewFile();
                    //fill the content with the original file
                    Utils.writeContents(createdFile, content);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else { // if it's in the removed files then just remove the mark
            File[] originallyRemoved = StagingAreaIO.readRemovedFiles(workingDR);
            for (File f : originallyRemoved) {
                if (f.getName().equals(fileName)) {
                    // restore the file into WKDR
                    StagingAreaIO.unremoveFile(f, workingDR);
                }
            }
        }
    }

    /**
     * Copy the file in removedFiles back to WKDR
     *
     * @param removedFile the file
     * @param workingDr   the working DR
     */
    public static void unremoveFile(File removedFile, File workingDr) {
        byte[] content = Utils.readContents(removedFile);
        File restoredFile = new File(workingDr, removedFile.getName());
        try {
            restoredFile.createNewFile();
            // fill in the content
            Utils.writeContents(restoredFile, content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        removedFile.delete();
    }

    public static void cleanRemovedFiles(File workingDR) {
        File gitlet = new File(workingDR, ".gitlet");
        File stagingArea = new File(gitlet, "stagingArea");
        File removeFiles = new File(stagingArea, "removedFiles");
        if (removeFiles.isDirectory()) {
            for (File f : removeFiles.listFiles()) {
                f.delete();
            }
        }
    }

    /**
     * @param fileName  the name of the file
     * @param workingDR
     * @return if the file is tracked and it is not modified, or if the file is not
     * in working directory, then return false
     * otherwise return true
     */
    public static boolean checkIfValidToAdd(String fileName, File workingDR) {
        // workingFile is the newly modified file
        File workingFile = new File(workingDR, fileName);
        if (workingFile.exists()) { // check if the file exists in working Directory
            // get the map of tracked files
            try {
                String commitCode = BranchIO.readCommitCode(BranchIO.readHeadPointer(workingDR),
                        workingDR);
                Commit previousCommit = CommitIO.read(workingDR, commitCode);
                SortedMap<String, String> trackedFiles = previousCommit.getContents();
                if (trackedFiles.keySet().contains(fileName)) { // the file is tracked
                    String blobCode = trackedFiles.get(fileName);
                    String newBlobcODE = Utils.sha1(Utils.readContents(workingFile));
                    if (blobCode.equals(newBlobcODE)) {
                        return false;
                    }
                }
            } catch (CommitReadWriteException e) {
                e.printStackTrace();
            } catch (BranchReadWriteException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("File does not exist.");
            return false;
        }
        return true;
    }


    /**
     * if in staging area:
     * 1.yes, delete
     * 2.no, continue
     * if tracked:
     * 1. no, print message
     * 2. yes, copy the file to deleted
     *
     * @param fileName
     * @param workingDR
     */
    public static void remove(String fileName, File workingDR) {
        File gitlet = new File(workingDR, ".gitlet");
        File stagingArea = new File(gitlet, "stagingArea");
        File addedFiles = new File(stagingArea, "addedFiles");
        // get the trackedFiles
        SortedMap<String, String> trackedFiles = null;
        try {
            String currentCommitSha = BranchIO.readCommitCode(BranchIO.readHeadPointer(workingDR),
                    workingDR);
            Commit currentCommit = CommitIO.read(workingDR, currentCommitSha);
            trackedFiles = currentCommit.getContents();
        } catch (BranchReadWriteException e) {
            e.printStackTrace();
        } catch (CommitReadWriteException e) {
            e.printStackTrace();
        }
        //if tracked
        if (trackedFiles.keySet().contains(fileName)) {
            String shaFileName = trackedFiles.get(fileName);
            byte[] content = BlobIO.deserializeBlob(shaFileName, workingDR);
            File removedFiles = new File(stagingArea, "removedFiles");
            File rmCopyFile = new File(removedFiles, fileName);
            try {
                rmCopyFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // mark in the removedFiles folder
            Utils.writeContents(rmCopyFile, content);

            //if in WKDR
            if ((new File(workingDR, fileName)).exists()) {
                (new File(workingDR, fileName)).delete();
            }

            if (StagingAreaIO.ifInAddedStagingArea(fileName, workingDR)) {
                File theFile = new File(addedFiles, fileName);
                theFile.delete(); // remove the file from staging area
            }

        } else if (StagingAreaIO.ifInAddedStagingArea(fileName, workingDR)) {
            // if not tracked but is staged, delete from staging area
            File theFile = new File(addedFiles, fileName);
            theFile.delete(); // remove the file from staging area
        } else {
            System.out.println("No reason to remove the file.");
        }

    }

    /**
     * @param fileName  the name of checked file
     * @param workingDR the working irectory
     * @return if the file exists in the addedFilesDirectory
     */
    public static boolean ifInAddedStagingArea(String fileName, File workingDR) {
        File gitlet = new File(workingDR, ".gitlet");
        File stagingArea = new File(gitlet, "stagingArea");
        File addedFiles = new File(stagingArea, "addedFiles");
        File theFile = new File(addedFiles, fileName);
        return theFile.exists();
    }

    /**
     * @param fileName  the name of the file
     * @param workingDR
     * @return if the file is staged for removal
     */
    public static boolean ifInRemovedFiles(String fileName, File workingDR) {
        File[] removed = StagingAreaIO.readRemovedFiles(workingDR);
        for (File f : removed) {
            if (f.getName().equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param workingDR the working directory
     * @return an array of files that are staged for removal
     */
    public static File[] readRemovedFiles(File workingDR) {
        File gitlet = new File(workingDR, ".gitlet");
        File stagingArea = new File(gitlet, "stagingArea");
        File removedFiles = new File(stagingArea, "removedFiles");
        return removedFiles.listFiles();
    }

    /**
     * @param workingDR the working directory
     * @return if theres anything in addedFiles ot removedFiles
     */
    public static boolean ifStagingAreaEmpty(File workingDR) {
        File gitlet = new File(workingDR, ".gitlet");
        File stagingArea = new File(gitlet, "stagingArea");
        File addedFiles = new File(stagingArea, "addedFiles");
        if ((addedFiles.list().length == 0)
                && (StagingAreaIO.readRemovedFiles(workingDR).length == 0)) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
    }
}
