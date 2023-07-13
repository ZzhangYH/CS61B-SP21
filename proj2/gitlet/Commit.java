package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;
import static gitlet.Repository.*;

/**
 * Represents a gitlet commit object.
 * @author Yuhan Zhang
 */
public class Commit implements Serializable {

    /** Map of file tracked by this commit. */
    private final Map<File, Blob> blobs = new HashMap<File, Blob>();
    /** Message of this commit. */
    private final String message;
    /** Date of this commit. */
    private final Date date;
    /** Parent of this commit. */
    private Commit parent;
    /** UID of this commit. */
    private String UID;

    /** Default constructor, initializes the very first commit of the repository. */
    public Commit() {
        this.message = "initial commit";
        this.date = new Date(0);
        this.parent = null;
    }

    /** Constructor of a normal commit. */
    public Commit(String message) {
        this.message = message;
        this.date = new Date();
        this.parent = getCurrentCommit();
        this.blobs.putAll(this.parent.getBlobs());
    }

    /** Generates and sets the UID of the commit. */
    public void setUID() {
        List<Object> vals = new ArrayList<Object>();
        Set<File> files = blobs.keySet();
        for (File f : files) {
            vals.add(f.toString());
        }
        if (parent != null) {
            vals.add(parent.toString());
        }
        vals.add(message);
        vals.add(date.toString());
        this.UID = sha1(vals);
    }

    /** Commits and writes to the logs. */
    public void commit() {
        // Checks the staging area unless the initial commit.
        if (parent != null) {
            checkCommit();
        }
        // Generates UID.
        setUID();
        // Writes the commit object.
        try {
            File folder = getPathFolder();
            if (!folder.exists()) {
                folder.mkdir();
            }
            getPathFile().createNewFile();
            writeObject(getPathFile(), this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Update the current working branch.
        Branch b = getCurrentBranch();
        b.setCommit(this);
    }

    /** Checks the staging area to validate commit eligibility. */
    public void checkCommit() {
        Index idx = getIndex();
        Map<File, Blob> staged = idx.getStaged();
        Map<File, Blob> removed = idx.getRemoved();
        if (staged.isEmpty() && removed.isEmpty()) {
            exit("No changes added to the commit.");
        }
        for (File f : removed.keySet()) {
            blobs.remove(f);
        }
        blobs.putAll(idx.getStaged());
        idx.clear();
    }

    /** Finds all commits ever made and returns them as a set. */
    public static Set<Commit> findAll() {
        List<String> dirs = subDirNamesIn(OBJECTS_DIR);
        Set<Commit> commits = new HashSet<Commit>();
        if (dirs != null) {
            for (String dir : dirs) {
                File subDir = join(OBJECTS_DIR, dir);
                List<String> files = plainFilenamesIn(subDir);
                if (files == null) {
                    continue;
                }
                for (String file: files) {
                    commits.add(readObject(join(subDir, file), Commit.class));
                }
            }
        }
        return commits;
    }

    /** Returns the Commit object of the specified UID (or abbreviation). */
    public static Commit find(String commitID) {
        Commit commit = null;
        for (Commit c : findAll()) {
            if (c.getUID().startsWith(commitID)) {
                commit = c;
                break;
            }
        }
        // If no commit with the given id exists.
        if (commit == null) {
            exit("No commit with that id exists.");
        }
        return commit;
    }

    /** Finds the ids of all commits with the specified message and returns as a set. */
    public static Set<String> findId(String message) {
        Set<Commit> commits = findAll();
        Set<String> ids = new HashSet<String>();
        for (Commit c : commits) {
            if (c.getMessage().equals(message)) {
                ids.add(c.getUID());
            }
        }
        if (ids.size() == 0) {
            exit("Found no commit with that message.");
        }
        return ids;
    }

    /** Traces back two commits and find the splitting point where they diverge. */
    public static Commit findSplitPoint(Commit current, Commit other) {
        Commit currentTemp = current;
        while (true) {
            Commit otherTemp = other;
            while (otherTemp != null) {
                if (currentTemp.equals(otherTemp)) {
                    return currentTemp;
                }
                otherTemp = otherTemp.getParent();
            }
            if (currentTemp.getParent() != null) {
                currentTemp = currentTemp.getParent();
            } else {
                return currentTemp;
            }
        }
    }

    /** Deletes all the files (not the blobs) tracked by the commit.
     *  The parameter is for checking whether any untracked files would be overwritten. */
    public void deleteTrackedFiles(Commit commit) {
        getIndex().checkUntracked(commit);
        for (Blob b : blobs.values()) {
            b.delete();
        }
    }

    /** Overwrites all the files (not the blobs) tracked by the commit. */
    public void overwriteTrackedFiles() {
        for (Blob b : blobs.values()) {
            b.overwrite();
        }
    }

    /** Returns whether the specified file is tracked by the commit. */
    public boolean isTracked(File file) {
        return this.blobs.containsKey(file);
    }

    /** Returns the Blob of the file specified. */
    public Blob getBlob(File file) {
        return blobs.get(file);
    }

    /** Returns the map of blobs of the commit. */
    public Map<File, Blob> getBlobs() {
        return this.blobs;
    }

    /** Returns the message of the commit. */
    public String getMessage() {
        return this.message;
    }

    /** Returns the parent of the commit. */
    public Commit getParent() {
        return this.parent;
    }

    /** Returns the UID of the commit. */
    public String getUID() {
        return this.UID;
    }

    /** Returns the path to the objs subfolder. */
    public File getPathFolder() {
        return join(OBJECTS_DIR, this.UID.substring(0, 2));
    }

    /** Returns the path to the file holding the commit object. */
    public File getPathFile() {
        return join(getPathFolder(), this.UID.substring(2));
    }

    /** Returns the commit as a log of String. */
    @Override
    public String toString() {
        SimpleDateFormat d = new SimpleDateFormat("E MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);
        return "===\ncommit " + UID + "\n" + "Date: " + d.format(date) + "\n" + message + "\n";
    }

}
