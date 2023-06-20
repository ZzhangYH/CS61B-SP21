package gitlet;

import static gitlet.Utils.*;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Yuhan Zhang
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *
     *  <COMMAND> <OPERAND1> <OPERAND2> ...
     *  init -- Creates a new Gitlet version-control system in the current directory.
     *          This system will automatically start with one initial commit.
     *          It will have a single branch: master, which initially points to this initial commit,
     *          and master will be the current branch.
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
                // TODO: handle the `add [filename]` command
                break;
            // TODO: FILL THE REST IN
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
