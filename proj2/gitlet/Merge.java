package gitlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static gitlet.Utils.*;
import static gitlet.Repository.*;

/**
 * Deals with merge command exclusively.
 * @author Yuhan Zhang
 */
public class Merge {

    /** Merge command runner method. */
    public static void merge(Branch current, Branch other) {
        // Finds and checks the split commit.
        Commit currentCommit = current.getCommit();
        Commit otherCommit = other.getCommit();
        Commit splitCommit = Commit.findSplitPoint(currentCommit, otherCommit);
        if (splitCommit.getUID().equals(otherCommit.getUID())) {
            exit("Given branch is an ancestor of the current branch.");
        }
        if (splitCommit.getUID().equals(currentCommit.getUID())) {
            checkoutBranch(other.getName());
            exit("Current branch fast-forwarded.");
        }

        // Required maps for the merge operation.
        Map<File, Blob> currentFiles = currentCommit.getBlobs();
        Map<File, Blob> otherFiles = otherCommit.getBlobs();
        Map<File, Blob> splitFiles = splitCommit.getBlobs();
        Map<File, Blob> mergedFiles = mergeFiles(currentFiles, otherFiles, splitFiles);

        // Updates the staging area, commits the merge, and updates all files.
        getIndex().merge(mergedFiles);
        Commit mergeCommit = new Commit(current, other);
        mergeCommit.commit();
        reset(mergeCommit.getUID());
    }

    /** Merges the files in two branches. */
    private static Map<File, Blob> mergeFiles(Map<File, Blob> current,
                                              Map<File, Blob> other,
                                              Map<File, Blob> split) {
        // All files map.
        Map<File, Blob> all = new HashMap<>();
        all.putAll(current);
        all.putAll(other);
        // Merged files map.
        Map<File, Blob> merged = new HashMap<>();
        // Checked files set.
        Set<File> checked = new HashSet<>();

        for (Blob b : all.values()) {
            // Records and checks file consistency.
            File f = b.getPath();
            if (checked.contains(f)) {
                continue;
            }
            checked.add(f);

            // Blob that is not present in the split commit.
            if (!b.isIn(split)) {
                // Puts the blob in merged
                // except that it is modified in both current and other in different ways.
                if (!(b.isIn(current) && b.isIn(other) && b.isModifiedIn(current, other))) {
                    merged.put(f, b);
                    continue;
                }
            }

            // Blob that is in the split commit.
            if (b.isIn(split)) {
                // Skips the blob: Unmodified in current but not present in other.
                if (b.isIn(current) && !b.isIn(other) && !b.isModifiedIn(split, current)) {
                    continue;
                }
                // Skips the blob: Unmodified in other but not present in current.
                if (!b.isIn(current) && b.isIn(other) && !b.isModifiedIn(split, other)) {
                    continue;
                }
                // Puts the blob in merged if modified in both current and other the same way.
                if (b.isIn(current) && b.isIn(other) && !b.isModifiedIn(current, other)) {
                    merged.put(f, b);
                    continue;
                }
            }

            // Remaining blob encounters merge conflict.
            merged.put(f, mergeConflict(b, current, other));
        }

        return merged;
    }

    /** Handles the merge conflict and returns the merged blob. */
    private static Blob mergeConflict(Blob blob, Map<File, Blob> current, Map<File, Blob> other) {
        // Gets the blob contents (empty byte array if null).
        Blob currentBlob = current.get(blob.getPath());
        Blob otherBlob = other.get(blob.getPath());
        byte[] currentContents = currentBlob == null ? new byte[0] : currentBlob.getContents();
        byte[] otherContents = otherBlob == null ? new byte[0] : otherBlob.getContents();

        // Formats the conflict.
        byte[] head = "<<<<<<< HEAD\n".getBytes(StandardCharsets.UTF_8);
        byte[] body = "=======\n".getBytes(StandardCharsets.UTF_8);
        byte[] feet = ">>>>>>>\n".getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream str = new ByteArrayOutputStream( );
        try {
            str.write(head);
            str.write(currentContents);
            str.write(body);
            str.write(otherContents);
            str.write(feet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] mergeContents = str.toByteArray();

        // Prints conflict message and returns the merged blob.
        System.out.println("Encountered a merge conflict.");
        return new Blob(blob.getName(), blob.getPath(), mergeContents);
    }

}
