package gitlet;

import static gitlet.Utils.*;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 * @author Yuhan Zhang
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     *
     *  init --
     *      Creates a new Gitlet version-control system in the current directory.
     *      This system will automatically start with one initial commit.
     *      It will have a single branch: master, which initially points to this initial commit,
     *      and master will be the current branch.
     *
     *  add <file name> --
     *      Adds a copy of the file as it currently exists to the staging area. Staging an
     *      already-staged file overwrites the previous entry in the staging area with
     *      the new contents. The staging area should be somewhere in .gitlet.
     *      If the current working version of the file is identical to the version in the
     *      current commit, do not stage it to be added, and remove it from the staging area
     *      if it is already there.
     *
     *  rm <file name> --
     *      Unstage the file if it is currently staged for addition. If the file is tracked in
     *      the current commit, stage it for removal and remove the file from the working directory
     *      if the user has not already done so.
     *
     *  commit <message> --
     *      Saves a snapshot of tracked files in the current commit and staging area so they can
     *      be restored at a later time, creating a new commit. The commit is said to be tracking
     *      the saved files. By default, each commit’s snapshot of files will be exactly
     *      the same as its parent commit’s snapshot of files; it will keep versions of files
     *      exactly as they are, and not update them.
     *
     *  log --
     *      Starting at the current head commit, display information about each commit
     *      backwards along the commit tree until the initial commit, following the first parent
     *      commit links, ignoring any second parents found in merge commits.
     *
     *  global-log --
     *      Like log, except displays information about all commits ever made. The order of the
     *      commits does not matter.
     *
     *  find <commit message> --
     *      Prints out the ids of all commits that have the given commit message, one per line.
     *      If there are multiple such commits, it prints the ids out on separate lines.
     *
     *  status --
     *      Displays what branches currently exist, and marks the current branch with a *. Also
     *      displays what files have been staged for addition or removal. Entries should be listed
     *      in lexicographic order, using the Java string-comparison order.
     *
     *  checkout --
     *      -- <file name>
     *          Takes the version of the file as it exists in the head commit and puts it in the
     *          working directory, overwriting the version of the file that’s already there.
     *          The new version of the file is not staged.
     *      <commit id> -- <file name>
     *          Takes the version of the file as it exists in the commit with the given id, and
     *          puts it in the working directory, overwriting the version of the file that’s
     *          already there. The new version of the file is not staged.
     *      <branch name>
     *          Takes all files in the commit at the head of the given branch, and puts them in the
     *          working directory, overwriting the versions of the files that are already there
     *          if they exist. Also, at the end of this command, the given branch will now be
     *          considered the current branch.
     *
     *  branch <branch name> --
     *      Creates a new branch with the given name, and points it at the current head commit.
     *      This command does NOT immediately switch to the newly created branch.
     *
     *  rm-branch <branch name> --
     *      Deletes the branch with the given name. This only means to delete the pointer
     *      associated with the branch; it does not mean to delete all commits that were created
     *      under the branch, or anything like that.
     *
     *  reset <commit id> --
     *      Checks out all the files tracked by the given commit. Removes tracked files that are
     *      not present in that commit. Also moves the current branch’s head to that commit node.
     *
     */
    public static void main(String[] args) {
        // No input arguments.
        if (args.length == 0) {
            exit("Please enter a command.");
        }

        String cmd = args[0];
        switch (cmd) {
            case "init":
                Repository.initialize();
                validate(args, 1);
                break;
            case "add":
                validate(args, 2);
                Repository.addFile(args[1]);
                break;
            case "rm":
                validate(args, 2);
                Repository.rmFile(args[1]);
                break;
            case "commit":
                validate(args, 2);
                Repository.commit(args[1]);
                break;
            case "log":
                validate(args, 1);
                Repository.log();
                break;
            case "global-log":
                validate(args, 1);
                Repository.globalLog();
                break;
            case "find":
                validate(args, 2);
                Repository.find(args[1]);
                break;
            case "status":
                validate(args, 1);
                Repository.status();
                break;
            case "checkout":
                checkout(args);
                break;
            case "branch":
                validate(args, 2);
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                validate(args, 2);
                Repository.rmBranch(args[1]);
                break;
            case "reset":
                validate(args, 2);
                Repository.reset(args[1]);
                break;
            default:
                exit("No command with that name exists.");
        }
    }

    /** Checks whether the command line args are valid. */
    private static void validate(String[] args, int n) {
        // Gitlet working directory is not initialized.
        if (!Repository.GITLET_DIR.exists()) {
            exit("Not in an initialized Gitlet directory.");
        }
        // Check the number of args.
        if (args.length != n) {
            exit("Incorrect operands.");
        }
    }

    /** Checker for the checkout command. */
    private static void checkout(String[] args) {
        if (args.length == 2) {
            Repository.checkoutBranch(args[1]);
        } else if (args.length == 3 && args[1].equals("--")) {
            Repository.checkoutFile(null, args[2]);
        } else if (args.length == 4 && args[2].equals("--")) {
            Repository.checkoutFile(args[1], args[3]);
        } else {
            exit("Incorrect operands.");
        }
    }

}
