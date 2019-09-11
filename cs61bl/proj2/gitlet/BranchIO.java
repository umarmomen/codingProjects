package gitlet;

import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;

public class BranchIO {
    public static final String GITLET = ".gitlet";
    public static final String BRANCH = "branches";

    public static void write(String branchName, String commitHashCode,
                             String rootHashCode, File workingDR)
            throws BranchReadWriteException {
        File gitdir = new File(workingDR, GITLET);
        File branchdir = new File(gitdir, BRANCH);
        File outFile = new File(branchdir, branchName);
        try {
            FileOutputStream fos = new FileOutputStream(outFile);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(commitHashCode);
            bw.newLine();
            bw.write(rootHashCode);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteBranch(String branchName, File workingDR) {
        File gitdir = new File(workingDR, GITLET);
        File branchdir = new File(gitdir, BRANCH);
        File branchFile = new File(branchdir, branchName);
        branchFile.delete();
    }

    public static String readCommitCode(String branchName, File workingDR)
            throws BranchReadWriteException {
        String commitHashCode;
        try {
            File gitdir = new File(workingDR, GITLET);
            File commitdir = new File(gitdir, BRANCH);
            File branchFile = new File(commitdir, branchName);
            FileInputStream fileIn = new FileInputStream(branchFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fileIn));
            commitHashCode = br.readLine();
            fileIn.close();
            br.close();
        } catch (IOException i) {
            i.printStackTrace();
            throw new BranchReadWriteException(i);
        }
        return commitHashCode;
    }

    public static String readSplitPointCode(String branchName, File workingDR)
            throws BranchReadWriteException {
        String rootHashCode;
        try {
            File gitdir = new File(workingDR, GITLET);
            File commitdir = new File(gitdir, BRANCH);
            File branchFile = new File(commitdir, branchName);
            FileInputStream fileIn = new FileInputStream(branchFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fileIn));
            br.readLine();
            rootHashCode = br.readLine();
            fileIn.close();
            br.close();
        } catch (IOException i) {
            i.printStackTrace();
            throw new BranchReadWriteException(i);
        }
        return rootHashCode;
    }

    public static void writeHeadPointer(String branchName, File headPointerFile) {

        try {
            FileOutputStream fos = new FileOutputStream(headPointerFile);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(branchName);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readHeadPointer(File workingDR) throws BranchReadWriteException {
        String branchName;
        try {
            File gitdir = new File(workingDR, GITLET);
            File branchFile = new File(gitdir, "headPointerFile");
            FileInputStream fileIn = new FileInputStream(branchFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(fileIn));
            branchName = br.readLine();
            fileIn.close();
            br.close();
        } catch (IOException i) {
            i.printStackTrace();
            throw new BranchReadWriteException(i);
        }
        return branchName;
    }

    public static void main(String[] args) throws BranchReadWriteException {
        BranchIO.deleteBranch("umar", Main.WKDR);
    }
}
