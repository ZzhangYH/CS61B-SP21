package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import static gitlet.Utils.*;
import static gitlet.Repository.*;

/** Represents a gitlet branch object.
 *
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
            this.getRefPath().createNewFile();
            this.getLogPath().createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Return the name of the branch. */
    public String getName() {
        return this.name;
    }

    /** Return the relative path of the branch. */
    public File getPath() {
        return this.path;
    }

    /** Return the relative path to the refs. */
    public File getRefPath() {
        return join(GITLET_DIR, this.path);
    }

    /** Return the relative path to the logs. */
    public File getLogPath() {
        return join(LOGS_DIR, this.path);
    }

}
