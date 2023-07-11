package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;
import static gitlet.Repository.*;

/**
 * Represents a gitlet branch object.
 * @author Yuhan Zhang
 */
public class Branch implements Serializable {

    /** Name of this branch. */
    private final String name;
    /** Relative path to this branch. */
    private final File path;
    /** Latest commit of this branch. */
    private Commit commit;

    /** Constructor of a branch. */
    public Branch(String name) {
        this.name = name;
        this.path = join("refs", "heads", this.name);
        try {
            // Checks if a branch with the given name already exists.
            if (!getRefFile().createNewFile() || !getLogFile().createNewFile()) {
                exit("A branch with that name already exists.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        save();
    }

    /** Updates and writes to the Branch object. */
    public void save() {
        writeObject(this.getRefFile(), this);
    }

    /** Deletes the branch references. */
    public void remove() {
        if (getRefFile().exists()) {
            getRefFile().delete();
        }
        if (getLogFile().exists()) {
            getLogFile().delete();
        }
    }

    /** Synchronizes the branch with the specified branch history. */
    public void sync(Branch branch) {
        this.commit = branch.getCommit();
        writeContents(this.getLogFile(), readContents(branch.getLogFile()));
        save();
    }

    /** Updates the branch with the latest commit and save it. */
    public void setCommit(Commit commit) {
        this.commit = commit;
        String id = commit.getUID();
        String log = readContentsAsString(getLogFile());
        // Write to the log.
        if (!log.contains(id)) {
            // New commit.
            writeContents(getLogFile(),
                    commit.toString() + "\n" + readContentsAsString(getLogFile()));
        } else {
            // Reset commit.
            writeContents(getLogFile(),
                    "===\ncommit " + log.substring(log.indexOf(id)));
        }
        save();
    }

    /** Finds all the existing branches and returns them as a set. */
    public static Set<Branch> findAll() {
        File dir = join(REFS_DIR, "heads");
        List<String> branchNames = plainFilenamesIn(dir);
        Set<Branch> branches = new HashSet<>();
        if (branchNames != null) {
            for (String name : branchNames) {
                branches.add(readObject(join(dir, name), Branch.class));
            }
        }
        return branches;
    }

    /** Returns the Branch object of the specified branch name. */
    public static Branch find(String branchName, int key) {
        Branch branch = null;
        for (Branch b : findAll()) {
            if (branchName.equals(b.getName())) {
                branch = b;
                break;
            }
        }
        // If no branch with that name exists.
        if (branch == null && key == 1) {
            exit("No such branch exists.");
        } else if (branch == null && key == 2) {
            exit("A branch with that name does not exist.");
        }
        return branch;
    }

    /** Return the name of the branch. */
    public String getName() {
        return this.name;
    }

    /** Return the relative path of the branch. */
    public File getPath() {
        return this.path;
    }

    /** Return the absolute path to the refs. */
    public File getRefFile() {
        return join(GITLET_DIR, this.path);
    }

    /** Return the absolute path to the logs. */
    public File getLogFile() {
        return join(LOGS_DIR, this.path);
    }

    /** Return the latest commit of the branch. */
    public Commit getCommit() {
        return this.commit;
    }

}
