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

    /** Message of this commit. */
    private final String message;
    /** Date of this commit. */
    private final Date date;
    /** Parent of this commit. */
    private Commit parent;
    /** Map of file tracked by this commit. */
    private HashMap<File, Blob> blobs;
    /** UID of this commit. */
    private String UID;

    /** Default constructor, initializes the very first commit of the repo. */
    public Commit() {
        this.message = "initial commit";
        this.date = new Date(0);
        this.parent = null;
        this.blobs = new HashMap<File, Blob>();
    }

    /** Constructor of a normal commit. */
    public Commit(String message) {
        this.message = message;
        this.date = new Date();
        this.parent = readObject(getCurrentBranch(), Branch.class).getCommit();
        this.blobs = this.parent.getBlobs();
    }

    /** Commit and write to the logs. */
    public void commit() {
        Index idx = readObject(INDEX, Index.class);
        if (idx.getStaged().size() == 0 && this.parent != null) {
            exit("No changes added to the commit.");
        } else {
            blobs.putAll(idx.getStaged());
            idx.clear();
        }
        this.setUID();
        try {
            getPath().createNewFile();
            writeObject(getPath(), this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Branch b = readObject(getCurrentBranch(), Branch.class);
        b.setCommit(this);
        writeContents(b.getLogFile(), this.toString() + "\n" +
                readContentsAsString(b.getLogFile()));
    }

    /** Generates and sets the UID of the commit. */
    public void setUID() {
        ArrayList<Object> vals = new ArrayList<Object>();
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

    /** Return the absolute path to the objs. */
    public File getPath() {
        return join(OBJECTS_DIR, this.UID);
    }

    /** Returns the map of blobs of the commit. */
    public HashMap<File, Blob> getBlobs() {
        return this.blobs;
    }

    /** Returns the Blob of the file specified. */
    public Blob getBlob(File file) {
        return blobs.get(file);
    }

    /** Return the commit as a log of String. */
    @Override
    public String toString() {
        SimpleDateFormat d = new SimpleDateFormat("E MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);
        return "===\ncommit " + UID + "\n" + "Date: " + d.format(date) + "\n" + message + "\n";
    }

}
