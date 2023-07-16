package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static gitlet.Utils.*;
import static gitlet.Repository.*;

/**
 * Represents a gitlet remote object.
 * @author Yuhan Zhang
 */
public class Remote implements Serializable {

    /** Name of this remote. */
    private final String name;
    /** Relative path to this remote. */
    private final File path;
    /** Absolute path to the repository (.gitlet) of this remote. */
    private final File directory;

    /** Constructor of a remote. */
    public Remote(String name, File directory) {
        this.name = name;
        this.path = join("refs", "remotes", this.name);
        this.directory = directory;
        try {
            // Checks if a remote with the given name already exists.
            if (!getRefFile().createNewFile()) {
                exit("A remote with that name already exists.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        save();
    }

    /** Updates and writes to the Remote object. */
    public void save() {
        writeObject(this.getRefFile(), this);
    }

    /** Saves the given login information under the given remote name. */
    public static void addRemote(String remoteName, String dirString) {
        File dirPath = join(dirString.replace("/", File.separator));
        new Remote(remoteName, dirPath);
    }

    /** Remove information associated with the given remote name. */
    public static void rmRemote(String remoteName) {
        Remote r = Remote.find(remoteName);
        if (r.getRefFile().exists()) {
            r.getRefFile().delete();
        }
    }

    /** Attempts to append the current branch's commits to the end of the given branch
     *  at the given remote. */
    public static void push(String remoteName, String remoteBranchName) {
        Remote remote = find(remoteName);
        Branch remoteBranch = remote.findBranch(remoteBranchName);
        Set<Commit> localCommits = getCurrentBranch().getAllCommits();
        // If the remote branch's head is not in the history of the current local head.
        Set<String> commitIds = new HashSet<>();
        for (Commit c : localCommits) {
            commitIds.add(c.getUID());
        }
        if (!commitIds.contains(remoteBranch.getCommit().getUID())) {
            exit("Please pull down remote changes before pushing.");
        }
        // Saves the commits and updates the remote branch
        for (Commit c : localCommits) {
            c.saveTo(remote.directory);
        }
        remoteBranch.resetCommit(getCurrentCommit());
    }

    /** Brings down commits from the remote repository into the local repository. */
    public static void fetch(String remoteName, String remoteBranchName) {
        String localBranchName = remoteName + "/" + remoteBranchName;
        Branch remoteBranch = find(remoteName).findBranch(remoteBranchName);
        Branch localBranch = Branch.find(localBranchName, 0);
        if (localBranch == null) {
            localBranch = new Branch(localBranchName);
        }
        // Saves the commits and updates the local branch
        Set<Commit> commits = remoteBranch.getAllCommits();
        for (Commit c : commits) {
            c.save();
        }
        localBranch.resetCommit(remoteBranch.getCommit());
    }

    /** Fetches the specified remote branch and merges that fetch into the current branch. */
    public static void pull(String remoteName, String remoteBranchName) {
        fetch(remoteName, remoteBranchName);
        merge(remoteName + "/" + remoteBranchName);
    }

    /** Finds all the existing remotes and returns them as a set. */
    public static Set<Remote> findAll() {
        File dir = join(REFS_DIR, "remotes");
        List<String> remoteNames = plainFilenamesIn(dir);
        Set<Remote> remotes = new HashSet<>();
        if (remoteNames != null) {
            for (String name : remoteNames) {
                remotes.add(readObject(join(dir, name), Remote.class));
            }
        }
        return remotes;
    }

    /** Returns the Remote object of the specified remote name. */
    public static Remote find(String remoteName) {
        Remote remote = null;
        for (Remote b : findAll()) {
            if (remoteName.equals(b.name)) {
                remote = b;
                break;
            }
        }
        // If no remote with that name exists.
        if (remote == null) {
            exit("A remote with that name does not exist.");
        }
        return remote;
    }

    /** Finds the specified Branch object from the remote. */
    public Branch findBranch(String remoteBranchName) {
        File dir = join(directory, "refs", "heads");
        // If the remote .gitlet directory does not exist.
        if (!dir.exists()) {
            exit("Remote directory not found.");
        }
        List<String> branchNames = plainFilenamesIn(dir);
        Branch branch = null;
        if (branchNames != null) {
            for (String name : branchNames) {
                if (remoteBranchName.equals(name)) {
                    branch = readObject(join(dir, name), Branch.class);
                }
            }
        }
        // If the remote repository does not have the given branch name.
        if (branch == null) {
            exit("That remote does not have that branch.");
        }
        return branch;
    }

    /** Return the absolute path to the refs. */
    public File getRefFile() {
        return join(GITLET_DIR, this.path);
    }

}
