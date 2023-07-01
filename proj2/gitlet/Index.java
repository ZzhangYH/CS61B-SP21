package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static gitlet.Utils.*;
import static gitlet.Repository.*;

/**
 * Represents the staging area of the gitlet repository.
 * @author Yuhan Zhang
 */
public class Index implements Serializable {

    /** Map of the Blobs staged. */
    private HashMap<File, Blob> staged;

    /** Default constructor, initialize the instance variables. */
    public Index() {
        staged = new HashMap<File, Blob>();
    }

    /** Updates and writes to the staging area. */
    public void save() {
        writeObject(INDEX, this);
    }

    /** Clears the staging area. */
    public void clear() {
        staged = new HashMap<File, Blob>();
        save();
    }

    /** Adds the specified file into staged area. */
    public void add(String fileName, File file) {
        if (isModified(file)) {
            staged.put(file, new Blob(fileName, file));
        }
        save();
    }

    /** Checks whether the specified file is modified under that in the last commit. */
    public boolean isModified(File file) {
        Blob b = readObject(getCurrentBranch(), Branch.class).getCommit().getBlob(file);
        if (b == null) {
            return true;
        }
        byte[] oldFile = b.getContents();
        byte[] newFile = readContents(file);
        return Arrays.equals(oldFile, newFile);
    }

    /** Returns the map of the staged blobs. */
    public HashMap<File, Blob> getStaged() {
        return this.staged;
    }

    /** Returns the status of the current repository as a String. */
    @Override
    public String toString() {
        StringBuilder status = new StringBuilder();

        status.append("=== Branches ===\n*");
        String current = readObject(getCurrentBranch(), Branch.class).getName();
        status.append(current).append("\n");
        List<String> all = plainFilenamesIn(REFS_DIR);
        for (String b : all) {
            if (!b.equals(current)) {
                status.append(b).append("\n");
            }
        }

        status.append("\n=== Staged Files ===\n");
        for (Blob b : staged.values()) {
            status.append(b.getName()).append("\n");
        }

        status.append("\n=== Removed Files ===\n");

        status.append("\n=== Modifications Not Staged for Commit ===\n");

        status.append("\n=== Untracked Files ===\n");

        status.append("\n");
        return status.toString();
    }

}
