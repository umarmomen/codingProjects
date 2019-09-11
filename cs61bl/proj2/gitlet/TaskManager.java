package gitlet;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Date;
import java.util.Arrays;

public class TaskManager {

    /**
     * Only runs the commands
     */

    /**
     * Create the folders, create initial commit, create the first branch
     * Blobs folder: contains all the blobs, each in a single file, file name is HashCode,
     * file content is serialized code
     * Commits Folder: contains all the commits, each in a single file, file name is HashCode,
     * file content is serialized code
     * Branches Folder: contains all the Branches, each in a single file, file name is HashCode,
     * file content is serialized code
     * StagingArea Foler: contains two separate folders: addedAndModifiedFiles, removedFiles
     * addedAndModifiedFiles: reference of filename -> serialized code
     * removedFiles: reference of filename ->s serialized code
     * HeadPointerFile: contains only the name of the branch
     */
    public static void init() throws CommitReadWriteException, BranchReadWriteException {
        //Create the folders
        File gitletDir = new File(Main.WKDR, ".gitlet");
        gitletDir.mkdir();
        File blobsDir = new File(gitletDir, "blobs");
        blobsDir.mkdir();
        File stagingAreaDir = new File(gitletDir, "stagingArea");
        stagingAreaDir.mkdir();
        File addedFiles = new File(stagingAreaDir, "addedFiles");
        addedFiles.mkdir();
        File modifiedFiles = new File(stagingAreaDir, "modifiedFiles");
        modifiedFiles.mkdir();
        File removedFiles = new File(stagingAreaDir, "removedFiles");
        removedFiles.mkdir();
        File commitDir = new File(gitletDir, "commits");
        commitDir.mkdir();
        File branchDir = new File(gitletDir, "branches");
        branchDir.mkdir();
        File headPointerFile = new File(gitletDir, "headPointerFile");
        // set up initial commit
        Commit initialCommit = new Commit(null, "initial commit", new Date(),
                new TreeMap<>());
        // write the commit
        CommitIO.write(initialCommit, Main.WKDR);
        // write the initial branch
        String initialCommitCode = initialCommit.getSha();
        BranchIO.write("master", initialCommitCode, initialCommitCode, Main.WKDR);
        // write the headPointer
        BranchIO.writeHeadPointer("master", headPointerFile);
    }

    //add command

    public static void add(String... args) {
        String fileName = args[1];
        StagingAreaIO.add(fileName, Main.WKDR);
    }

    //commit command
    public static void commit(String... args) {
        String msg = args[1];
        if (msg.equals("")) {
            System.out.println("Please enter a commit message.");
            return;
        }
        if (StagingAreaIO.ifStagingAreaEmpty(Main.WKDR)) {
            System.out.println("No changes added to the commit");
            return;
        }
        File gitlet = new File(Main.WKDR, ".gitlet");
        File stagingArea = new File(gitlet, "stagingArea");
        File newFiles = new File(stagingArea, "addedFiles");
        try {
            String branchName = BranchIO.readHeadPointer(Main.WKDR);
            String currentCommitCode = BranchIO.readCommitCode(branchName, Main.WKDR);
            Commit currentCommit = CommitIO.read(Main.WKDR, currentCommitCode);
            // copy the originally tracked files map
            SortedMap<String, String> trackedFiles = new TreeMap<>(currentCommit.getContents());
            // get the name of the files that are needed to be removed
            File[] removedFileNames = StagingAreaIO.readRemovedFiles(Main.WKDR);
            for (File f : removedFileNames) {
                // remove the deletedFiles
                trackedFiles.remove(f.getName());
            }
            // get the files that are needed to be added
            File[] addedFiles = newFiles.listFiles();
            // add them and write them to the blobs, and delete them afterwards
            for (File f : addedFiles) {
                byte[] contents = Utils.readContents(f);
                trackedFiles.put(f.getName(), Utils.sha1(contents));
                CommitIO.writeFiles(Main.WKDR, contents, Utils.sha1(contents));
                f.delete();
            }
            // clean the removedFIles
            StagingAreaIO.cleanRemovedFiles(Main.WKDR);
            // create the new commit
            Commit newCommit = new Commit(currentCommitCode,
                    msg,
                    new Date(),
                    trackedFiles);
            // write it in the commits folder
            CommitIO.write(newCommit, Main.WKDR);
            // change the current branch's commitCode to the one of current's commit
            String newCommitCode = newCommit.getSha();
            String rootCode = BranchIO.readSplitPointCode(branchName, Main.WKDR);
            // update the branch pointer
            BranchIO.write(branchName, newCommitCode, rootCode, Main.WKDR);


        } catch (BranchReadWriteException | CommitReadWriteException be) {
            be.printStackTrace();
        }
    }

    //rm command
    public static void rm(String... args) {
        String fileName = args[1];
        StagingAreaIO.remove(fileName, Main.WKDR);
    }

    //log command
    public static void log() throws BranchReadWriteException, CommitReadWriteException {
        String currentBranchName = BranchIO.readHeadPointer(Main.WKDR);
        String curentCommitCode = BranchIO.readCommitCode(currentBranchName, Main.WKDR);
        Commit currentCommit = CommitIO.read(Main.WKDR, curentCommitCode);
        while (currentCommit.getParent() != null) {
            System.out.println("===");
            System.out.println("Commit " + currentCommit.getSha());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");
            String strDate = sdf.format(currentCommit.getCommitDate());
            System.out.println(strDate);
            System.out.println(currentCommit.getMessage());
            System.out.println();
            currentCommit = CommitIO.read(Main.WKDR, currentCommit.getParent());
        }
        System.out.println("===");
        System.out.println("Commit " + currentCommit.getSha());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");
        String strDate = sdf.format(currentCommit.getCommitDate());
        System.out.println(strDate);
        System.out.println(currentCommit.getMessage());
        System.out.println();
    }

    //global log command
    public static void globalLog() throws CommitReadWriteException {
        File gitlet = new File(Main.WKDR, ".gitlet");
        File commits = new File(gitlet, "commits");
        String[] commitFileList = commits.list();
        for (String fname : commitFileList) {
            Commit current = CommitIO.read(Main.WKDR, fname);
            String currentCommitCode = current.getSha();
            System.out.println("===");
            System.out.println("Commit " + currentCommitCode);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:MM:ss");
            String strDate = sdf.format(current.getCommitDate());
            System.out.println(strDate);
            System.out.println(current.getMessage());
            System.out.println();
        }
    }

    //find command
    public static void find(String... args) throws BranchReadWriteException,
            CommitReadWriteException {
        boolean exists = false;
        File gitdir = new File(Main.WKDR, ".gitlet");
        String message = args[1];
        File commits = new File(gitdir, "commits");
        String[] directoryListing = commits.list();
        if (directoryListing != null) {
            for (String child : directoryListing) {
                Commit temp = CommitIO.read(Main.WKDR, child);
                if (temp.getMessage().equals(message)) {
                    System.out.println(temp.getSha());
                    exists = true;
                }
            }
        }
        if (!exists) {
            System.out.print("Found no commit with that message.");
        }

    }

    //status command
    public static void status() throws BranchReadWriteException, CommitReadWriteException {
        String headBranchName = BranchIO.readHeadPointer(Main.WKDR);
        File gitlet = new File(Main.WKDR, ".gitlet");
        File branches = new File(gitlet, "branches");
        String[] branchNames = branches.list();
        System.out.println("=== Branches ===");
        System.out.print("*");
        System.out.println(headBranchName);
        Arrays.stream(branchNames)
                .filter(x -> !x.equals(headBranchName))
                .sorted((x1, x2) -> x1.compareTo(x2))
                .forEach(x -> System.out.println(x));
        System.out.println();
        File stageArea = new File(gitlet, "stagingArea");
        File added = new File(stageArea, "addedFiles");
        String[] addedFiles = added.list();
        System.out.println("=== Staged Files ===");
        Arrays.stream(addedFiles)
                .sorted((x1, x2) -> x1.compareTo(x2))
                .forEach(x -> System.out.println(x));
        System.out.println();
        File[] rmFiles = StagingAreaIO.readRemovedFiles(Main.WKDR);
        ArrayList<File> removed = new ArrayList<>(Arrays.asList(rmFiles));
        System.out.println("=== Removed Files ===");
        removed.stream()
                .sorted((x1, x2) -> (x1.getName()).compareTo(x2.getName()))
                .forEach(x -> System.out.println(x.getName()));
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
    }

    //checkout command
    public static void checkoutHelp1(String... args) throws BranchReadWriteException,
            CommitReadWriteException {
        if (!args[1].equals("--")) {
            System.out.println("Incorrect operands.");
            return;
        }
        String filename = args[2];
        File workingFile = new File(Main.WKDR, filename);
        if (workingFile.exists()) {
            workingFile.delete();
        }
        String headBranchName = BranchIO.readHeadPointer(Main.WKDR);
        String headCommitCode = BranchIO.readCommitCode(headBranchName, Main.WKDR);
        Commit headCommit = CommitIO.read(Main.WKDR, headCommitCode);
        SortedMap<String, String> fileMap = headCommit.getContents();
        if (!fileMap.containsKey(filename)) {
            System.out.println("File does not exist in that commit.");
            return;
        } else {
            String fileSHA = headCommit.getContents().get(filename);
            byte[] contents = BlobIO.deserializeBlob(fileSHA, Main.WKDR);
            Utils.writeContents(workingFile, contents);
        }
    }

    public static void checkoutHelp2(String... args) throws BranchReadWriteException,
            CommitReadWriteException {
        if (!args[2].equals("--")) {
            System.out.println("Incorrect operands.");
            return;
        }
        String origCommitCode = args[1];
        String filename = args[3];
        File workingFile = new File(Main.WKDR, filename);
//            if (workingFile.exists()) {
//                workingFile.delete();
//            }
        File gitlet = new File(Main.WKDR, ".gitlet");
        File commits = new File(gitlet, "commits");
//            File commitFromCode = new File(commits, commitCode);
//            if (!commitFromCode.exists()){
//                System.out.println("No commit with that id exists.");
//                return;
//            }
        String commitCode = null;
        String[] commitCodes = commits.list();
        boolean exits = false;
        for (String name : commitCodes) {
            if (name.substring(0, origCommitCode.length()).equals(origCommitCode)) {
                commitCode = name;
                exits = true;
                break;
            }
        }
        if (!exits) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit refCommit = CommitIO.read(Main.WKDR, commitCode);
        SortedMap<String, String> fileMap = refCommit.getContents();
        if (!fileMap.containsKey(filename)) {
            System.out.println("File does not exist in that commit.");
            return;
        } else {
            String fileSHA = refCommit.getContents().get(filename);
            byte[] contents = BlobIO.deserializeBlob(fileSHA, Main.WKDR);
            Utils.writeContents(workingFile, contents);
        }

    }

    public static void checkoutHelp3(String... args) throws BranchReadWriteException,
            CommitReadWriteException {
        String argBranchName = args[1];
        String headBranchName = BranchIO.readHeadPointer(Main.WKDR);
        File gitlet = new File(Main.WKDR, ".gitlet");
        File branches = new File(gitlet, "branches");
        File branch = new File(branches, argBranchName);
        if (!branch.exists()) {
            System.out.println("No such branch exists.");
            return;
        } else if (argBranchName.equals(headBranchName)) {
            System.out.println("No need to checkout the current branch.");
            return;
        } else {
            String currentCommitCode = BranchIO.readCommitCode(
                    BranchIO.readHeadPointer(Main.WKDR), Main.WKDR);
            Commit currentCommit = CommitIO.read(Main.WKDR, currentCommitCode);
            SortedMap<String, String> currentFiles = currentCommit.getContents();
            String branchCommitCode = BranchIO.readCommitCode(argBranchName, Main.WKDR);
            Commit branchCommit = CommitIO.read(Main.WKDR, branchCommitCode);
            SortedMap<String, String> branchFiles = branchCommit.getContents();
            String[] wkDRFiles = Main.WKDR.list();
            List<String> untracked = Arrays.stream(wkDRFiles)
                    .filter(x -> !x.equals(".gitlet"))
                    .filter(x -> !x.equals(".idea"))
                    .filter(x -> !x.equals("gitlet"))
                    .filter(x -> !x.equals("out"))
                    .filter(x -> !x.equals("testing"))
                    .filter(x -> !x.equals("src"))
                    .filter(x -> !x.equals("runner.py"))
                    .filter(x -> !x.equals(".gitignore"))
                    .filter(x -> !x.equals("proj2.iml"))
                    .filter(x -> !x.equals(".DS_Store"))
                    .collect(Collectors.toList());
            ArrayList<String> removeTemp = new ArrayList<>();
            for (String fileName : untracked) {
                if (currentFiles.containsKey(fileName)) {
                    removeTemp.add(fileName);
                } else {
                    if (branchFiles.containsKey(fileName)) {
                        String workingCode = Utils.sha1(Utils.readContents(
                                new File(Main.WKDR, fileName)));
                        String branchFileCode = branchFiles.get(fileName);
                        if (workingCode.equals(branchFileCode)) {
                            removeTemp.add(fileName);
                        }
                    } else {
                        removeTemp.add(fileName);
                    }
                }
            }
            for (String fileName : removeTemp) {
                untracked.remove(fileName);
            }
            if (untracked.size() > 0) {
                System.out.println("There is an untracked file in the way; delete it or "
                        + "add it first.");
                return;
            }
            File headPointerFile = new File(gitlet, "headPointerFile");
            BranchIO.writeHeadPointer(argBranchName, headPointerFile);
            for (String key : branchFiles.keySet()) {
                File check = new File(Main.WKDR, key);
                if (check.exists()) {
                    check.delete();
                }
                String fileSHA = branchFiles.get(key);
                byte[] contents = BlobIO.deserializeBlob(fileSHA, Main.WKDR);
                Utils.writeContents(check, contents);
            }

            for (String file : currentFiles.keySet()) {
                File workingFile = new File(Main.WKDR, file);
                if (!branchFiles.containsKey(file)) {
                    workingFile.delete();
                }
            }
        }
    }

    public static void checkOut(String... args) throws BranchReadWriteException,
            CommitReadWriteException {
        if (args.length == 3) {
            TaskManager.checkoutHelp1(args);
        } else if (args.length == 4) {
            TaskManager.checkoutHelp2(args);
        } else if (args.length == 2) {
            TaskManager.checkoutHelp3(args);
        }
    }


    public static void resetHelp(String... args) throws BranchReadWriteException,
            CommitReadWriteException {
        String argBranchName = args[1];
        String headBranchName = BranchIO.readHeadPointer(Main.WKDR);
        File gitlet = new File(Main.WKDR, ".gitlet");
        File branches = new File(gitlet, "branches");
        File branch = new File(branches, argBranchName);
        if (!branch.exists()) {
            return;
        } else if (argBranchName.equals(headBranchName)) {
            return;
        } else {
            String currentCommitCode = BranchIO.readCommitCode(
                    BranchIO.readHeadPointer(Main.WKDR), Main.WKDR);
            Commit currentCommit = CommitIO.read(Main.WKDR, currentCommitCode);
            SortedMap<String, String> currentFiles = currentCommit.getContents();
            String branchCommitCode = BranchIO.readCommitCode(argBranchName, Main.WKDR);
            Commit branchCommit = CommitIO.read(Main.WKDR, branchCommitCode);
            SortedMap<String, String> branchFiles = branchCommit.getContents();
            String[] wkDRFiles = Main.WKDR.list();
            List<String> untracked = Arrays.stream(wkDRFiles)
                    .filter(x -> !x.equals(".gitlet"))
                    .filter(x -> !x.equals(".idea"))
                    .filter(x -> !x.equals("gitlet"))
                    .filter(x -> !x.equals("out"))
                    .filter(x -> !x.equals("testing"))
                    .filter(x -> !x.equals("src"))
                    .filter(x -> !x.equals("runner.py"))
                    .filter(x -> !x.equals(".gitignore"))
                    .filter(x -> !x.equals("proj2.iml"))
                    .filter(x -> !x.equals(".DS_Store"))
                    .collect(Collectors.toList());
            ArrayList<String> removeTemp = new ArrayList<>();
            for (String fileName : untracked) {
                if (currentFiles.containsKey(fileName)) {
                    removeTemp.add(fileName);
                } else {
                    if (branchFiles.containsKey(fileName)) {
                        String workingCode = Utils.sha1(Utils.readContents(
                                new File(Main.WKDR, fileName)));
                        String branchFileCode = branchFiles.get(fileName);
                        if (workingCode.equals(branchFileCode)) {
                            removeTemp.add(fileName);
                        }
                    } else {
                        removeTemp.add(fileName);
                    }
                }
            }
            for (String fileName : removeTemp) {
                untracked.remove(fileName);
            }
            if (untracked.size() > 0) {
                return;
            }
            File headPointerFile = new File(gitlet, "headPointerFile");
            BranchIO.writeHeadPointer(argBranchName, headPointerFile);
            for (String key : branchFiles.keySet()) {
                File check = new File(Main.WKDR, key);
                if (check.exists()) {
                    check.delete();
                }
                String fileSHA = branchFiles.get(key);
                byte[] contents = BlobIO.deserializeBlob(fileSHA, Main.WKDR);
                Utils.writeContents(check, contents);
            }
            for (String file : currentFiles.keySet()) {
                File workingFile = new File(Main.WKDR, file);
                if (!branchFiles.containsKey(file)) {
                    workingFile.delete();
                }
            }

        }
    }

    //branch command
    public static void branch(String... args) {
        File gitdir = new File(Main.WKDR, ".gitlet");
        String branchName = args[1];
        File branches = new File(gitdir, "branches");
        if (!(new File(branches, branchName)).exists()) {
            try {
                //get the name of active branch
                String activeBranchName = BranchIO.readHeadPointer(Main.WKDR);
                // get the hashcode of the active commit
                String activeCommitCode = BranchIO.readCommitCode(activeBranchName,
                        Main.WKDR);
                // write another branch pointing to the same commit
                BranchIO.write(branchName, activeCommitCode, activeCommitCode, Main.WKDR);
                BranchIO.write(activeBranchName, activeCommitCode, activeCommitCode,
                        Main.WKDR);
            } catch (BranchReadWriteException be) {
                be.printStackTrace();
            }
        } else {
            System.out.println("A branch with that name already exists.");
        }
    }

    //rm-branch command
    public static void removeBranch(String... args) {
        File gitdir = new File(Main.WKDR, ".gitlet");
        String branchName = args[1];
        File branches = new File(gitdir, "branches");
        if ((new File(branches, branchName)).exists()) {
            try {
                String activeBranchName = BranchIO.readHeadPointer(Main.WKDR);
                // check if the intended-to-be-removed branch is the active branch
                if (branchName.equals(activeBranchName)) {
                    System.out.println("Cannot remove the current branch.");
                }
                // now it's safe to delete
                BranchIO.deleteBranch(branchName, Main.WKDR);
            } catch (BranchReadWriteException be) {
                be.printStackTrace();
            }
        } else {
            System.out.println("A branch with that name does not exist.");
        }
    }

    //reset command
    public static void reset(String... args) throws BranchReadWriteException,
            CommitReadWriteException {
        File gitlet = new File(Main.WKDR, ".gitlet");
        File commits = new File(gitlet, "commits");
        File thisCommit = new File(commits, args[1]);
        if (!thisCommit.exists()) {
            System.out.println("No commit with that id exists.");
        } else {
            File branches = new File(gitlet, "branches");
            BranchIO.write("resetBranch", args[1], args[1], Main.WKDR);
            TaskManager.resetHelp(new String[]{"checkout", "resetBranch"});
            File master = new File(branches, "master");
            if (master.exists()) {
                BranchIO.deleteBranch("master", Main.WKDR);
            }
            BranchIO.write("master", args[1], args[1], Main.WKDR);
            TaskManager.checkOut(new String[]{"checkout", "master"});
            BranchIO.deleteBranch("resetBranch", Main.WKDR);
            File stageArea = new File(gitlet, "stagingArea");
            File added = new File(stageArea, "addedFiles");
            Arrays.stream(added.list())
                    .map(x -> new File(added, x))
                    .forEach(x -> x.delete());
        }
    }

    public static void merge(String... args) throws BranchReadWriteException {
        if (!isValidToMerge(args)) {
            return;
        }
        try {
            boolean ifConflict = false;
            String branchName = args[1];
            String currentBranchName = BranchIO.readHeadPointer(Main.WKDR);
            String branchCommitCode = BranchIO.readCommitCode(branchName, Main.WKDR);
            String currentCommitCode = BranchIO.readCommitCode(currentBranchName, Main.WKDR);
            String splitCode = BranchIO.readSplitPointCode(currentBranchName, Main.WKDR);
            Commit branchCommit = CommitIO.read(Main.WKDR, branchCommitCode);
            Commit currentCommit = CommitIO.read(Main.WKDR, currentCommitCode);
            Commit splitCommit = CommitIO.read(Main.WKDR, splitCode);
            SortedMap<String, String> branchFiles = branchCommit.getContents();
            SortedMap<String, String> currentFiles = currentCommit.getContents();
            SortedMap<String, String> splitFiles = splitCommit.getContents();
            for (String branchFileName : branchFiles.keySet()) {
                File workingFile = new File(Main.WKDR, branchFileName);
                if (!currentFiles.containsKey(branchFileName)) {
                    if (workingFile.exists()) {
                        String blobCode = branchFiles.get(branchFileName);
                        String workingCode = Utils.sha1(Utils.readContents(workingFile));
                        if (!blobCode.equals(workingCode)) {
                            System.out.println("There is an untracked file in the way; "
                                    + "delete it or add it first.");
                            return;
                        }
                    }
                }
            }
            Commit temp = currentCommit;
            while (temp.getParent() != null) {
                if (temp.getParent().equals(branchCommitCode)) {
                    System.out.println("Given branch is an ancestor of the current branch.");
                    return;
                }
                temp = CommitIO.read(Main.WKDR, temp.getParent());
            }
            if (currentCommitCode.equals(splitCode)) {
                BranchIO.write(branchName, branchCommitCode, branchCommitCode, Main.WKDR);
                BranchIO.write(currentBranchName, branchCommitCode, branchCommitCode,
                        Main.WKDR);
                System.out.println("Current branch fast-forwarded.");
                return;
            }
            for (String branchFileName : branchFiles.keySet()) {
                if (splitFiles.containsKey(branchFileName)
                        && currentFiles.containsKey(branchFileName)) {
                    if ((!(splitFiles.get(branchFileName).equals(branchFiles.get(branchFileName))))
                            && (splitFiles.get(branchFileName).equals(
                                    currentFiles.get(branchFileName)))) {
                        String[] checkOutArgs =
                                new String[]{"checkout", branchCommitCode, "--", branchFileName};
                        TaskManager.checkOut(checkOutArgs);
                        StagingAreaIO.add(branchFileName, Main.WKDR);
                    }
                }
            }
            for (String branchFileName : branchFiles.keySet()) {
                if ((!splitFiles.containsKey(branchFileName))
                        && (!currentFiles.containsKey(branchFileName))) {
                    String[] checkOutArgs =
                            new String[]{"checkout", branchCommitCode, "--", branchFileName};
                    TaskManager.checkOut(checkOutArgs);
                    StagingAreaIO.add(branchFileName, Main.WKDR);
                }
            }
            for (String splitFileName : splitFiles.keySet()) {
                if (currentFiles.containsKey(splitFileName)) {
                    if (splitFiles.get(splitFileName).equals(currentFiles.get(splitFileName))) {
                        String[] rmArgs = new String[]{"rm", splitFileName};
                        TaskManager.rm(rmArgs);
                    }
                }
            }
            ifConflict = combineConflictPresentInAll(branchFiles, currentFiles,
                    splitFiles, ifConflict);
            ifConflict = combineConflictPresentAtSplitAndOneOfBranch(branchFiles, currentFiles,
                    splitFiles, ifConflict);
            ifConflict = combineConflictNotPresentAtSplit(branchFiles, currentFiles, splitFiles,
                    ifConflict);
            if (!ifConflict) {
                String[] commitArgs = new String[]{"commit", "Merged " + currentBranchName
                        + " with " + branchName + "."};
                TaskManager.commit(commitArgs);
                return;
            } else {
                System.out.println("Encountered a merge conflict.");
            }
        } catch (CommitReadWriteException e) {
            e.printStackTrace();
        } catch (BranchReadWriteException e) {
            e.printStackTrace();
        }
    }

    private static boolean isValidToMerge(String... args) throws BranchReadWriteException {
        if (!StagingAreaIO.ifStagingAreaEmpty(Main.WKDR)) {
            System.out.println("You have uncommitted changes.");
            return false;
        }
        File gitlet = new File(Main.WKDR, ".gitlet");
        File branches = new File(gitlet, "branches");
        File br = new File(branches, args[1]);
        if (!br.exists()) {
            System.out.println("A branch with that name does not exist.");
            return false;
        }
        if (BranchIO.readHeadPointer(Main.WKDR).equals(args[1])) {
            System.out.println("Cannot merge a branch with itself.");
            return false;
        }
        return true;
    }

    private static boolean combineConflictPresentInAll(
            SortedMap<String, String> branchFiles,
            SortedMap<String, String> currentFiles,
            SortedMap<String, String> splitFiles,
            boolean ifConflict) {
        for (String splitFileName : splitFiles.keySet()) {

            File workingFile = new File(Main.WKDR, splitFileName);
            if (currentFiles.containsKey(splitFileName)
                    && branchFiles.containsKey(splitFileName)) {
                if ((!splitFiles.get(splitFileName).equals(currentFiles.get(splitFileName)))
                        && (!splitFiles.get(splitFileName).equals(
                        branchFiles.get(splitFileName)))
                        && (!branchFiles.get(splitFileName).equals(
                        currentFiles.get(splitFileName)))) {
                    byte[] header = "<<<<<<< HEAD\n".getBytes();
                    byte[] currentContent = BlobIO.deserializeBlob(
                            currentFiles.get(splitFileName), Main.WKDR);
                    byte[] middle = "=======\n".getBytes();
                    byte[] branchContent = BlobIO.deserializeBlob(
                            branchFiles.get(splitFileName), Main.WKDR);
                    byte[] end = (">>>>>>>\n").getBytes();

                    byte[] newContent = new byte[header.length
                            + currentContent.length
                            + middle.length
                            + branchContent.length
                            + end.length];
                    System.arraycopy(header, 0, newContent, 0, header.length);
                    System.arraycopy(currentContent, 0, newContent,
                            header.length, currentContent.length);
                    System.arraycopy(middle, 0, newContent, header.length
                            + currentContent.length, middle.length);
                    System.arraycopy(branchContent, 0, newContent, header.length
                            + currentContent.length
                            + middle.length, branchContent.length);
                    System.arraycopy(end, 0, newContent, header.length
                            + currentContent.length
                            + middle.length
                            + branchContent.length, end.length);
                    Utils.writeContents(workingFile, newContent);
                    ifConflict = true;
                }
            }
        }
        return ifConflict;
    }

    private static boolean combineConflictPresentAtSplitAndOneOfBranch(
            SortedMap<String, String> branchFiles,
            SortedMap<String, String> currentFiles,
            SortedMap<String, String> splitFiles,
            boolean ifConflict) {
        for (String splitFileName : splitFiles.keySet()) {
            File workingFile = new File(Main.WKDR, splitFileName);
            if (currentFiles.containsKey(splitFileName)
                    && (!branchFiles.containsKey(splitFileName))) {
                if (workingFile.exists() && (!currentFiles.get(splitFileName).equals(
                        splitFiles.get(splitFileName)))) {
                    byte[] header = "<<<<<<< HEAD\n".getBytes();
                    byte[] currentContent = BlobIO.deserializeBlob(
                            currentFiles.get(splitFileName), Main.WKDR);
                    byte[] middle = "=======\n".getBytes();
                    byte[] end = (">>>>>>>\n").getBytes();
                    byte[] newContent = new byte[header.length
                            + currentContent.length
                            + middle.length
                            + end.length];
                    System.arraycopy(header, 0, newContent, 0, header.length);
                    System.arraycopy(currentContent, 0, newContent, header.length,
                            currentContent.length);
                    System.arraycopy(middle, 0, newContent, header.length
                            + currentContent.length, middle.length);
                    System.arraycopy(end, 0, newContent, header.length
                            + currentContent.length + middle.length, end.length);
                    Utils.writeContents(workingFile, newContent);
                    ifConflict = true;
                }
            }
            if ((!currentFiles.containsKey(splitFileName))
                    && branchFiles.containsKey(splitFileName)) {
                if (workingFile.exists() && (!branchFiles.get(splitFileName).equals(
                        splitFiles.get(splitFileName)))) {
                    byte[] header = "<<<<<<< HEAD\n".getBytes();
                    byte[] middle = "=======\n".getBytes();
                    byte[] branchContent = BlobIO.deserializeBlob(branchFiles.get(
                            splitFileName), Main.WKDR);
                    byte[] end = (">>>>>>>\n").getBytes();
                    byte[] newContent = new byte[header.length
                            + middle.length
                            + branchContent.length
                            + end.length];
                    System.arraycopy(header, 0, newContent, 0, header.length);
                    System.arraycopy(middle, 0, newContent, header.length,
                            middle.length);
                    System.arraycopy(branchContent, 0, newContent,
                            header.length + middle.length, branchContent.length);
                    System.arraycopy(end, 0, newContent, header.length
                            + middle.length + branchContent.length, end.length);
                    Utils.writeContents(workingFile, newContent);
                    ifConflict = true;
                }
            }
        }
        return ifConflict;
    }
    private static boolean combineConflictNotPresentAtSplit(SortedMap<String, String> branchFiles,
                                                     SortedMap<String, String> currentFiles,
                                                     SortedMap<String, String> splitFiles,
                                                     boolean ifConflict) {
        for (String branchFileName : branchFiles.keySet()) {
            File workingFile = new File(Main.WKDR, branchFileName);
            if (currentFiles.containsKey(branchFileName)
                    && (!splitFiles.containsKey(branchFileName))
                    && (!currentFiles.get(branchFileName).equals(
                    branchFiles.get(branchFileName)))) {
                byte[] header = "<<<<<<< HEAD\n".getBytes();
                byte[] currentContent = BlobIO.deserializeBlob(
                        currentFiles.get(branchFileName), Main.WKDR);
                byte[] middle = "=======\n".getBytes();
                byte[] branchContent = BlobIO.deserializeBlob(
                        branchFiles.get(branchFileName), Main.WKDR);
                byte[] end = (">>>>>>>\n").getBytes();
                byte[] newContent = new byte[header.length
                        + currentContent.length
                        + middle.length
                        + branchContent.length
                        + end.length];
                System.arraycopy(header, 0, newContent, 0, header.length);
                System.arraycopy(currentContent, 0, newContent, header.length,
                        currentContent.length);
                System.arraycopy(middle, 0, newContent, header.length
                        + currentContent.length, middle.length);
                System.arraycopy(branchContent, 0, newContent, header.length
                        + currentContent.length
                        + middle.length, branchContent.length);
                System.arraycopy(end, 0, newContent, header.length
                        + currentContent.length
                        + middle.length
                        + branchContent.length, end.length);
                Utils.writeContents(workingFile, newContent);

                ifConflict = true;
            }
        }
        return ifConflict;
    }

    public static void main(String[] args) {
    }
}
