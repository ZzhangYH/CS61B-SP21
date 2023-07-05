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
        writeContents(b.getLogFile(), this.toString() + "\n" + readContentsAsString(b.getLogFile()));
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

    /** Returns the blob of the specified file tracked by the specified UID (or abbreviation). */
    public static Blob findBlob(String commitID, File file) {
        String id1 = commitID.substring(0, 2);
        String id2 = commitID.substring(2);
        File dir = join(OBJECTS_DIR, id1);
        // Checks whether the folder of commit exists.
        if (!dir.exists()) {
            exit("No commit with that id exists.");
        }
        // Checks whether the exact commit UID exists.
        List<String> fullIDs = plainFilenamesIn(dir);
        if (fullIDs == null) {
            exit("No commit with that id exists.");
        }
        String fullID = null;
        for (String id : fullIDs) {
            if (id.startsWith(id2)) {
                fullID = id;
                break;
            }
        }
        if (fullID == null) {
            exit("No commit with that id exists.");
        }
        // Checks whether the file exists in the specified commit.
        File f = join(OBJECTS_DIR, id1, fullID);
        Blob b = readObject(f, Commit.class).getBlob(file);
        if (b == null) {
            exit("File does not exist in that commit.");
        }
        return b;
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
