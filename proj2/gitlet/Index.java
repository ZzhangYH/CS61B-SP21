package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;
import static gitlet.Repository.*;

/**
 * Represents the staging area of the gitlet repository.
 * @author Yuhan Zhang
 */
public class Index implements Serializable {

    /** Map of the files staged. */
    private final Map<File, Blob> staged;
    /** Map of the files removed. */
    private final Map<File, Blob> removed;

    /** Default constructor, initializes the instance variables. */
    public Index() {
        staged = new HashMap<File, Blob>();
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
        } else {
            staged.remove(file);
        }
        removed.remove(file);
        save();
    }

    /** Unstages the file and removes it if eligible. */
    public void remove(String fileName, File file) {
        boolean tracked = getCurrentCommit().isTracked(file);
        if (!isStaged(file) && !tracked) {
            exit("No reason to remove the file.");
        }
        staged.remove(file);
        if (tracked) {
            removed.put(file, new Blob(fileName, file));
            if (file.exists()) {
                file.delete();
            }
        }
        save();
    }

    /** Checks whether the specified file is modified under that in the last commit. */
    public boolean isModified(File file) {
        Blob b;
        if (isStaged(file)) {
            b = staged.get(file);
        } else {
            b = getCurrentCommit().getBlob(file);
        }
        if (b == null) {
            return true;
        }
        byte[] oldFile = b.getContents();
        byte[] newFile = readContents(file);
        return !Arrays.equals(oldFile, newFile);
    }

    /** Returns whether the specified file is staged for addition. */
    public boolean isStaged(File file) {
        return staged.containsKey(file);
    }

    /** Returns whether the specified file is staged for removal. */
    public boolean isRemoved(File file) {
        return removed.containsKey(file);
    }

    /** Returns the map of the staged blobs. */
    public Map<File, Blob> getStaged() {
        return this.staged;
    }

    /** Returns the map of the removed blobs. */
    public Map<File, Blob> getRemoved() {
        return this.removed;
    }

    /** Converts the map of the files into arrays in lexicographic order. */
    public String[] sort(Map<File, Blob> map) {
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
        List<String> branches = plainFilenamesIn(join(REFS_DIR, "heads"));
        if (branches != null) {
            for (String s : branches) {
                if (s.equals(current)) {
                    status.append("*");
                }
                status.append(s).append("\n");
            }
        }

        status.append("\n=== Staged Files ===\n");
        for (String s : sort(staged)) {
            status.append(s).append("\n");
        }

        status.append("\n=== Removed Files ===\n");
        for (String s : sort(removed)) {
            status.append(s).append("\n");
        }

        Map<File, Blob> tracked = getCurrentCommit().getBlobs();

        status.append("\n=== Modifications Not Staged for Commit ===\n");
        Set<String> modifiedSet = new HashSet<String>();
        for (File f : tracked.keySet()) {
            if (!f.exists() && !isRemoved(f)) {
                modifiedSet.add(tracked.get(f).getName() + " (deleted)");
            } else if (f.exists() && !isStaged(f) && isModified(f)) {
                modifiedSet.add(tracked.get(f).getName() + " (modified)");
            }
        }
        for (File f : staged.keySet()) {
            if (!f.exists()) {
                modifiedSet.add(staged.get(f).getName() + " (deleted)");
            } else if (f.exists() && isModified(f)) {
                modifiedSet.add(staged.get(f).getName() + " (modified)");
            }
        }
        String[] modifiedArray = new String[modifiedSet.size()];
        modifiedSet.toArray(modifiedArray);
        Arrays.sort(modifiedArray);
        for (String s : modifiedArray) {
            status.append(s).append("\n");
        }

        status.append("\n=== Untracked Files ===\n");
        List<String> cwdFiles = plainFilenamesIn(CWD);
        if (cwdFiles != null) {
            for (String s : cwdFiles) {
                File f = join(CWD, s);
                if (!isStaged(f) && !tracked.containsKey(f)) {
                    status.append(s).append("\n");
                }
            }
        }

        return status.append("\n").toString();
    }

}
