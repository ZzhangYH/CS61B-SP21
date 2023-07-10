package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

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
            this.getRefFile().createNewFile();
            this.getLogFile().createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        writeObject(this.getRefFile(), this);
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
        writeObject(this.getRefFile(), this);
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
