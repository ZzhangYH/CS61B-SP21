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

    /** Map of the files staged. */
    private final HashMap<File, Blob> staged;
    /** Map of the files tracked. */
    private final HashMap<File, Blob> tracked;
    /** Map of the files removed. */
    private final HashMap<File, Blob> removed;

    /** Default constructor, initialize the instance variables. */
    public Index() {
        staged = new HashMap<File, Blob>();
        tracked = new HashMap<File, Blob>();
        removed = new HashMap<File, Blob>();
    }

    /** Updates and writes to the staging area. */
    public void save() {
        writeObject(INDEX, this);
    }

    /** Clears the staging area. */
    public void clear() {
        staged.clear();
        removed.clear();
        save();
    }

    /** Adds the specified file into staged area. */
    public void add(String fileName, File file) {
        if (isModified(file)) {
            Blob b = new Blob(fileName, file);
            staged.put(file, b);
            tracked.put(file, b);
        } else {
            staged.remove(file);
        }
        save();
    }

    /** Checks whether the specified file is modified under that in the last commit. */
    public boolean isModified(File file) {
        Blob b = getCurrentCommit().getBlob(file);
        if (b == null) {
            return true;
        }
        byte[] oldFile = b.getContents();
        byte[] newFile = readContents(file);
        return !Arrays.equals(oldFile, newFile);
    }

    /** Returns the map of the staged blobs. */
    public HashMap<File, Blob> getStaged() {
        return this.staged;
    }

    /** Returns the map of the tracked blobs. */
    public HashMap<File, Blob> getTracked() {
        return this.tracked;
    }

    /** Returns the map of the removed blobs. */
    public HashMap<File, Blob> getRemoved() {
        return this.removed;
    }

    /** Converts the map of the files into arrays in lexicographic order. */
    public String[] sort(HashMap<File, Blob> map) {
        String[] str = new String[map.size()];
        int idx = 0;
        for (Blob b : map.values()) {
            str[idx] = b.getName();
            idx += 1;
        }
        Arrays.sort(str);
        return str;
    }

    /** Returns the status of the current repository as a String. */
    @Override
    public String toString() {
        StringBuilder status = new StringBuilder();

        status.append("=== Branches ===\n");
        String current = getCurrentBranch().getName();
        List<String> all = plainFilenamesIn(join(REFS_DIR, "heads"));
        for (String b : all) {
            if (b.equals(current)) {
                status.append("*");
            }
            status.append(b).append("\n");
        }

        status.append("\n=== Staged Files ===\n");
        String[] stagedStr = sort(staged);
        for (String s : stagedStr) {
            status.append(s).append("\n");
        }

        status.append("\n=== Removed Files ===\n");

        status.append("\n=== Modifications Not Staged for Commit ===\n");

        status.append("\n=== Untracked Files ===\n");

        status.append("\n");
        return status.toString();
    }

}
