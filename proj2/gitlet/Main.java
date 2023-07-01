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
     */
    public static void main(String[] args) {
        // No input arguments.
        if (args.length == 0) {
            exit("Please enter a command.");
        }

        String cmd = args[0];
        switch(cmd) {
            case "init":
                Repository.initialize();
                validate(args, 1);
                break;
            case "add":
                validate(args, 2);
                Repository.addFile(args[1]);
                break;
            case "commit":
                validate(args, 2);
                Repository.commit(args[1]);
                break;
            case "log":
                validate(args, 1);
                Repository.log();
                break;
            case "status":
                validate(args, 1);
                Repository.status();
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

}
