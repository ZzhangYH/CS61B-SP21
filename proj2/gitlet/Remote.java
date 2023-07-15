package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static gitlet.Utils.*;
import static gitlet.Repository.*;

public class Remote implements Serializable {

    /** Name of this remote. */
    private final String name;
    /** Relative path to this remote. */
    private final File path;
    /** Absolute path to the remote directory. */
    private final File directory;
    /** Latest commit of this remote. */
    private Commit commit;

    /** Constructor of a remote. */
    public Remote(String name, File directory) {
        this.name = name;
        this.path = join("refs", "remotes", this.name);
        this.directory = directory;
        try {
            // Checks if a remote with the given name already exists.
            if (!getRefFile().createNewFile() || !getLogFile().createNewFile()) {
                exit("A remote with that name already exists.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        save();
    }

    /** Updates and writes to the Remote object. */
    public void save() {
        writeObject(this.getRefFile(), this);
    }

    /** Deletes the remote references. */
    public void remove() {
        if (getRefFile().exists()) {
            getRefFile().delete();
        }
        if (getLogFile().exists()) {
            getLogFile().delete();
        }
    }

    /** Finds all the existing remotes and returns them as a set. */
    public static Set<Remote> findAll() {
        File dir = join(REFS_DIR, "remotes");
        List<String> remoteNames = plainFilenamesIn(dir);
        Set<Remote> remotes = new HashSet<>();
        if (remoteNames != null) {
            for (String name : remoteNames) {
                remotes.add(readObject(join(dir, name), Remote.class));
            }
        }
        return remotes;
    }

    /** Returns the Remote object of the specified remote name. */
    public static Remote find(String remoteName) {
        Remote remote = null;
        for (Remote b : findAll()) {
            if (remoteName.equals(b.getName())) {
                remote = b;
                break;
            }
        }
        // If no remote with that name exists.
        if (remote == null) {
            exit("A remote with that name does not exist.");
        }
        return remote;
    }

    /** Return the name of the remote. */
    public String getName() {
        return this.name;
    }

    /** Return the absolute path to the refs. */
    public File getRefFile() {
        return join(GITLET_DIR, this.path);
    }

    /** Return the absolute path to the logs. */
    public File getLogFile() {
        return join(LOGS_DIR, this.path);
    }

    /** Returns the directory of the remote. */
    public File getDirectory() {
        return this.directory;
    }

    /** Returns the latest commit of the remote. */
    public Commit getCommit() {
        return this.commit;
    }

}
