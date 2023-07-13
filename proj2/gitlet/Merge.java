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

    public static void merge(Branch current, Branch other) {
        // Check the split commit.
        Commit split = Commit.findSplitPoint(current.getCommit(), other.getCommit());
        if (split.equals(other.getCommit())) {
            exit("Given branch is an ancestor of the current branch.");
        }
        if (split.equals(current.getCommit())) {
            checkoutBranch(other.getName());
            exit("Current branch fast-forwarded.");
        }

        // All maps of files needed to check.
        Map<File, Blob> splitFile = split.getBlobs();
        Map<File, Blob> currentFiles = current.getCommit().getBlobs();
        Map<File, Blob> otherFiles = other.getCommit().getBlobs();

        // Final version of files after merge.
        Map<File, Blob> merged = new HashMap<>();

        

    }


}
