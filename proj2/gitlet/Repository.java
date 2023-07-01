package gitlet;

import java.io.File;

import static gitlet.Utils.*;

/**
 * Represents a gitlet repository.
 * @author Yuhan Zhang
 */
public class Repository {

    /* Directories */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The object directory. */
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    /** The reference directory. */
    public static final File REFS_DIR = join(GITLET_DIR, "refs");
    /** The log directory. */
    public static final File LOGS_DIR = join(GITLET_DIR, "logs");

    /* Files */

    /** The reference to the current branch file. */
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    /** The staging area. */
    public static final File INDEX = join(GITLET_DIR, "index");

    /* Methods */

    /** Creates a new Gitlet version-control system in the current directory. */
    public static void initialize() {
        // Abort when there is already a gitlet repository in the current directory.
        if (GITLET_DIR.exists()) {
            exit("A Gitlet version-control system already exists in the current directory.");
        }

        // Initialize the directories.
        GITLET_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        REFS_DIR.mkdir();
        LOGS_DIR.mkdir();
        join(REFS_DIR, "heads").mkdir();
        join(LOGS_DIR, "refs").mkdir();
        join(LOGS_DIR, "refs", "heads").mkdir();

        // Create default branch.
        Branch master = new Branch("master");
        writeContents(HEAD, master.getPath().toString());
        writeObject(INDEX, new Index());

        // Create initial commit.
        Commit init = new Commit();
        init.commit();
    }

    /** Adds a copy of the file as it currently exists to the staging area. */
    public static void addFile(String fileName) {
        // File does not exist.
        File file = join(CWD, fileName);
        if (!file.exists()) {
            exit("File does not exist.");
        }
        Index idx = readObject(INDEX, Index.class);
        idx.add(fileName, file);
    }

    /** Adds a copy of the file as it currently exists to the staging area. */
    public static void commit(String message) {
        // Commit must have a non-blank message.
        if (message.length() == 0) {
            exit("Please enter a commit message.");
        }
        Commit c = new Commit(message);
        c.commit();
    }

    /** Starting at the current head commit, display each commit backwards until the initial commit. */
    public static void log() {
        Branch b = readObject(getCurrentBranch(), Branch.class);
        System.out.print(readContentsAsString(b.getLogFile()));
    }

    /** Displays what branches currently exist, (marking the current branch with a *) and
     *  what files have been staged for addition or removal. */
    public static void status() {
        System.out.print(readObject(INDEX, Index.class).toString());
    }

    /** Returns the object of the current working branch. */
    public static File getCurrentBranch() {
        return join(GITLET_DIR, readContentsAsString(HEAD));
    }

}
