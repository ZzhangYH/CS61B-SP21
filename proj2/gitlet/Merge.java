package gitlet;

import java.io.File;
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
        Commit split = Commit.findSplitPoint(currentCommit, otherCommit);
        if (split.equals(otherCommit)) {
            exit("Given branch is an ancestor of the current branch.");
        }
        if (split.equals(currentCommit)) {
            checkoutBranch(other.getName());
            exit("Current branch fast-forwarded.");
        }

        Map<File, Blob> currentFiles = current.getCommit().getBlobs();
        Map<File, Blob> otherFiles = other.getCommit().getBlobs();
        Map<File, Blob> splitFile = split.getBlobs();

        // Final version of files after merge.
        Map<File, Blob> merged = mergeFiles(currentFiles, otherFiles, splitFile);
        getIndex().merge(merged);

        // Commits the merge and updates all files.
        Commit mergeCommit = new Commit(current, other);
        mergeCommit.commit();
        for (File f : currentFiles.keySet()) {
            f.delete();
        }
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

        // Iterates through all the blobs.
        for (Blob b : all.values()) {

            // Records and checks file consistency.
            File f = b.getPath();
            if (checked.contains(f)) {
                continue;
            }
            checked.add(f);

            /* --- Only one occurrence --- */
            // Blob is only in current and not in other.
            if (!b.isIn(split) && b.isIn(current) && !b.isIn(other)) {
//                System.out.println(b.getName() + " is only in current.");
                merged.put(b.getPath(), b);
                continue;
            }
            // Blob is only in other and not in current.
            if (!b.isIn(split) && !b.isIn(current) && b.isIn(other)) {
//                System.out.println(b.getName() + " is only in other.");
                merged.put(b.getPath(), b);
                continue;
            }
            /* --- Only one occurrence --- */

            /* --- Two occurrences --- */
            // Blob is unmodified in current but not present in other.
            if (!b.isIn(other) && !b.isModifiedIn(split, current)) {
//                System.out.println(b.getName() + " is unmodified in current.");
                continue;
            }
            // Blob is unmodified in other but not present in current.
            if (!b.isIn(current) && !b.isModifiedIn(split, other)) {
//                System.out.println(b.getName() + " is unmodified in other.");
                continue;
            }
            /* --- Two occurrences --- */

            /* --- Three occurrences --- */
            // Blob is NOT modified in both current and other.
            if (!(b.isModifiedIn(split, current) && b.isModifiedIn(split, other))) {
                merged.put(b.getPath(), b);
                continue;
            }
            // Blob is modified in both current and other.
            else {
                Blob currentBlob = current.get(b.getPath());
                Blob otherBlob = other.get(b.getPath());
                // Modified in the same way.
                if (currentBlob.equals(otherBlob)) {
                    merged.put(b.getPath(), b);
                    continue;
                }
                // Merge conflict.
                else {
                    exit("Encountered a merge conflict.");
                }
            }
            /* --- Three occurrences --- */

        } // End of for loop.

        return merged;
    }

}
