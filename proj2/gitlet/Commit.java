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
    private final Commit parent;
    /** Merged parent of this commit. */
    private final Commit mergeParent;
    /** UID of this commit. */
    private String UID;

    /** Default constructor, initializes the very first commit of the repository. */
    public Commit() {
        this.message = "initial commit";
        this.date = new Date(0);
        this.parent = null;
        this.mergeParent = null;
    }

    /** Constructor of a normal commit. */
    public Commit(String message) {
        this.message = message;
        this.date = new Date();
        this.parent = getCurrentCommit();
        this.mergeParent = null;
        this.blobs.putAll(parent.blobs);
    }

    /** Constructor of a merge commit. */
    public Commit(Branch current, Branch other) {
        this.message = String.format("Merged %s into %s.", other.getName(), current.getName());
        this.date = new Date();
        this.parent = current.getCommit();
        this.mergeParent = other.getCommit();
        this.blobs.putAll(getIndex().getStaged());
    }

    /** Creates and writes to the Commit object. */
    public void save() {
        try {
            if (!getPathFolder().exists()) {
                getPathFolder().mkdir();
            }
            getPathFile().createNewFile();
            writeObject(getPathFile(), this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Creates and writes the Commit object to the specified repository. */
    public void saveTo(File dir) {
        File pathFolder = join(dir, UID.substring(0, 2));
        File pathFile = join(pathFolder, UID.substring(2));
        try {
            if (!pathFolder.exists()) {
                pathFolder.mkdir();
            }
            pathFile.createNewFile();
            writeObject(pathFile, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /** Commits and writes to the logs. */
    public void commit() {
        // Checks the staging area unless initial or merge commit.
        if (parent != null && mergeParent == null) {
            checkCommit();
        }
        setUID();
        save();
        Branch b = getCurrentBranch();
        b.setCommit(this);
        writeContents(b.getLogFile(),
                this.toString() + "\n" + readContentsAsString(b.getLogFile()));
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
            if (c.message.equals(message)) {
                ids.add(c.getUID());
            }
        }
        if (ids.size() == 0) {
            exit("Found no commit with that message.");
        }
        return ids;
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

    /** Returns the parent of the commit. */
    public Commit getParent() {
        return this.parent;
    }

    /** Returns the merged (second) parent of the commit. */
    public Commit getMergeParent() {
        return this.mergeParent;
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
        StringBuilder log = new StringBuilder();
        log.append("===\ncommit ").append(UID).append("\n");
        if (mergeParent != null) {
            log.append("Merge: ");
            log.append(parent.UID, 0, 7).append(" ");
            log.append(mergeParent.UID, 0, 7).append("\n");
        }
        log.append("Date: ").append(d.format(date)).append("\n").append(message).append("\n");
        return log.toString();
    }

}
