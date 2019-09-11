package gitlet;


import java.io.File;

public class TaskDelegate {

    /**
     * Only checks if the args is valid.
     * Print message if not valid
     */

    public static void initHelp(String[] args) throws CommitReadWriteException,
            BranchReadWriteException {
        if (args.length != 1) {
            System.out.println("Incorrect operands.");
            return;
        }
        if ((new File(".gitlet")).exists()) {
            System.out.println("A gitlet version-control system already "
                    + "exists in the current directory.");
            return;
        }
        TaskManager.init();
    }

    public static void addHelp(String[] args) throws CommitReadWriteException,
            BranchReadWriteException {
        if (!(new File(".gitlet")).exists()) {
            System.out.println("Not in an initialized gitlet directory.");
            return;
        }
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            return;
        }
        TaskManager.add(args);
    }

    public static void commitHelp(String[] args) throws CommitReadWriteException,
            BranchReadWriteException {
        if (!(new File(".gitlet")).exists()) {
            System.out.println("Not in an initialized gitlet directory.");
            return;
        }
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            return;
        }
        TaskManager.commit(args);
    }

    public static void rmHelp(String[] args) throws CommitReadWriteException,
            BranchReadWriteException {
        if (!(new File(".gitlet")).exists()) {
            System.out.println("Not in an initialized gitlet directory.");
            return;
        }
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            return;
        }
        TaskManager.rm(args);
    }

    public static void logHelp(String[] args) throws CommitReadWriteException,
            BranchReadWriteException {
        if (!(new File(".gitlet")).exists()) {
            System.out.println("Not in an initialized gitlet directory.");
            return;
        }
        if (args.length != 1) {
            System.out.println("Incorrect operands.");
            return;
        }
        TaskManager.log();
    }

    public static void globalLogHelp(String[] args) throws CommitReadWriteException,
            BranchReadWriteException {
        if (!(new File(".gitlet")).exists()) {
            System.out.println("Not in an initialized gitlet directory.");
            return;
        }
        if (args.length != 1) {
            System.out.println("Incorrect operands.");
            return;
        }
        TaskManager.globalLog();
    }

    public static void findHelp(String[] args) throws CommitReadWriteException,
            BranchReadWriteException {
        if (!(new File(".gitlet")).exists()) {
            System.out.println("Not in an initialized gitlet directory.");
            return;
        }
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            return;
        }
        TaskManager.find(args);

    }

    public static void statusHelp(String[] args) throws CommitReadWriteException,
            BranchReadWriteException {
        if (!(new File(".gitlet")).exists()) {
            System.out.println("Not in an initialized gitlet directory.");
            return;
        }
        if (args.length != 1) {
            System.out.println("Incorrect operands.");
            return;
        }
        TaskManager.status();
    }

    public static void checkoutHelp(String[] args) throws CommitReadWriteException,
            BranchReadWriteException {
        if (!(new File(".gitlet")).exists()) {
            System.out.println("Not in an initialized gitlet directory.");
            return;
        }
        if (!(args.length == 2 || args.length == 3 || args.length == 4)) {
            System.out.println("Incorrect operands.");
            return;
        }
        TaskManager.checkOut(args);
    }
    public static void branchHelp(String[] args) throws CommitReadWriteException,
            BranchReadWriteException {
        if (!(new File(".gitlet")).exists()) {
            System.out.println("Not in an initialized gitlet directory.");
            return;
        }
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            return;
        }
        TaskManager.branch(args);
    }

    public static void rmBranchHelp(String[] args) throws CommitReadWriteException,
            BranchReadWriteException {
        if (!(new File(".gitlet")).exists()) {
            System.out.println("Not in an initialized gitlet directory.");
            return;
        }
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            return;
        }
        TaskManager.removeBranch(args);
    }

    public static void resetHelp(String[] args) throws CommitReadWriteException,
            BranchReadWriteException {
        if (!(new File(".gitlet")).exists()) {
            System.out.println("Not in an initialized gitlet directory.");
            return;
        }
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            return;
        }
        TaskManager.reset(args);
    }

    public static void mergeHelp(String[] args) throws CommitReadWriteException,
            BranchReadWriteException {
        if (!(new File(".gitlet")).exists()) {
            System.out.println("Not in an initialized gitlet directory.");
            return;
        }
        if (args.length != 2) {
            System.out.println("Incorrect operands.");
            return;
        }
        TaskManager.merge(args);
    }

    public static void helpHelp(String[] args) throws CommitReadWriteException,
            BranchReadWriteException {
        System.out.print("init      Creates a new gitlet version-control system "
                + "in the current directory");
        System.out.println("add [filename]       Adds a copy of the file as it currently "
                + "exists to the staging area");
        System.out.println("commit [message]      Saves a snapshot of certain files "
                + "in the current commit and staging area");
        System.out.println("rm [filename]      Untrack the file");
        System.out.println("log      Starting at the current head commit, display "
                + "information about each commit backwards ");
        System.out.println("global-log     Like log, except displays information about "
                + "all commits ever made..");
        System.out.println("find [commit message]      Prints out the ids of all commits "
                + "that have the given commit message");
        System.out.println("status       Displays what branches currently exist, and "
                + "marks the current branch with a *");
        System.out.println("checkout       Checkout is a kind of general command that "
                + "can do a few "
                + "different things depending on what its arguments are. ");
        System.out.println("branch [branch name]      Creates a new branch with "
                + "the given name");
        System.out.println("rm-branch [branch name]     Deletes the branch with "
                + "the given name.");
        System.out.println("reset [commit id]       Checks out all the files tracked "
                + "by the given commit.");
        System.out.println("merge [branch name]     Merges files from the given branch"
                + " into the current branch..");
    }




    public static void checkArgAndRun(String[] args) throws CommitReadWriteException,
            BranchReadWriteException {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            throw new IllegalArgumentException("Please enter a command.");
        }
        String command = args[0].toLowerCase();
        switch (command) {
            case "init":
                TaskDelegate.initHelp(args);
                break;
            case "add":
                TaskDelegate.addHelp(args);
                break;
            case "commit":
                TaskDelegate.commitHelp(args);
                break;
            case "rm":
                TaskDelegate.rmHelp(args);
                break;
            case "log":
                logHelp(args);
                break;
            case "global-log":
                TaskDelegate.globalLogHelp(args);
                break;
            case "find":
                TaskDelegate.findHelp(args);
                break;
            case "status":
                TaskDelegate.statusHelp(args);
                break;
            case "checkout":
                checkoutHelp(args);
                break;
            case "branch":
                TaskDelegate.branchHelp(args);
                break;
            case "rm-branch":
                rmBranchHelp(args);
                break;
            case "reset":
                resetHelp(args);
                break;
            case "merge":
                mergeHelp(args);
                break;
            case "help":
                helpHelp(args);
                break;
            default:
                System.out.println("No command with that name exists.");
        }
    }


}
