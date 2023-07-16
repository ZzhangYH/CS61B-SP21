# Gitlet Design Document

**Name:** Yuhan Zhang

**Contents:**

<table>
  <thead>
    <tr>
      <th>Category</th>
      <th>Classes and Data Structures</th>
      <th>Command Algorithms</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Local</td>
      <td>
        <a href="#main">Main.class</a><br>
        <a href="#repository">Repository.class</a><br>
        <a href="#index">Index.class</a><br>
        <a href="#merge">Merge.class</a><br>
        <a href="#branch">Branch.class</a><br>
        <a href="#commit">Commit.class</a><br>
        <a href="#blob">Blob.class</a>
      </td>
      <td>
        <a href="#init">init</a><br>
        <a href="#add">add</a><br>
        <a href="#rm">rm</a><br>
        <a href="#commit-1">commit</a><br>
        <a href="#log">log</a><br>
        <a href="#global-log">global-log</a><br>
        <a href="#find">find</a><br>
        <a href="#status">status</a><br>
        <a href="#checkout">checkout</a><br>
        <a href="#branch-1">branch</a><br>
        <a href="#rm-branch">rm-branch</a><br>
        <a href="#reset">reset</a><br>
        <a href="#merge-1">merge</a>
      </td>
    </tr>
    <tr>
      <td>Remote</td>
      <td>
        <a href="#remote">Remote.class</a>
      </td>
      <td>
        <a href="#add-remote">add-remote</a><br>
        <a href="#rm-remote">rm-remote</a><br>
        <a href="#push">push</a><br>
        <a href="#fetch">fetch</a><br>
        <a href="#pull">pull</a>
      </td>
    </tr>
    <tr>
      <td colspan="3">
        <br><b>Persistence:</b> <a href="#persistence">Gitlet repository structure</a><br><br>
      </td>
    </tr>
  </tbody>
</table>

## Classes and Data Structures

### Main

This is the driver class of our `Gitlet` version control system. It takes in arguments from the command line and based on the command (the first element of the `args` array) calls the corresponding command in the gitlet `Repository` which will actually execute the logic of the command. It also **validates the arguments and the existence of the repository** based on the command to ensure that enough arguments were passed in.

### Repository

This is where the main logic of our program will live. The class mainly deals with **high-level structure** of the gitlet repository itself, *but not the detailed implementation of a single branch or commit*. It is also the container of all the gitlet command methods which are called by the `Main` class.

Refer to the internal structure of `.gitlet` directory at the [Persistence](#persistence) part.

#### Fields

1. `public static final File CWD` The current wording directory.
2. `public static final File GITLET_DIR` The hidden `.gitlet` directory of our version control system, it holds everything needed to set up Gitlet.
3. `public static final File OBJECTS_DIR` Not implemented yet.
4. `public static final File REFS_DIR` The directory holding all references to branches related files.
5. `public static final File LOGS_DIR` The directory holding the commit logs based on the structure of branches and commit related files.
6. `public static final File HEAD` File under our repository root directory, contains the relative path to the current working branch `refs/heads/<branch-name>`.
7. `public static final File INDEX` File under our repository root directory, which is the staging area, holds the `Index` object.

Everything within this class shall be `static` since there is no need to instantiate a `Repository` object, and we can directly call and refer to its variables and methods.

### Index

This is the staging area of the gitlet repository. It holds `maps` of **staged**, **tracked**, and **removed** *files to their blob contents* where the `add`, `rm`, `commits`, `status` commands refer. The `Index` object is stored in `index` file under the root directory and will be updated promptly after change(s) are made.

#### Fields

1. `private final Map<File, Blob> staged` Map of the files staged.
2. `private final Map<File, Blob> removed` Map of the files removed.

`Default Constructor` Initializes the `staged` and `removed` maps.

### Merge

The class handles the single `merge` command,  codes are just too long to fit either `Repository` class or `Branch` class, Therefore, I makes it an individual class. `Merge` has no fields or objects, only several *static methods* to deal with merge conflict, `DFS`, etc.

### Remote

The class is the driver class for all remote commands. Our `Main` method calls the `Repository` class before, now all remote commands go into this class. It can not only be initialized as `Remote` objects to keep track of different remotes Gitlet recognizes, but it also contains numerous *static **methods*** to directly deal with the commands: **add-remote**, **rm-remote**, **push**, **fetch**, and **pull**. Each `Remote` object is written (saved) under `.gitlet/refs/remotes/<remote-name>`.

#### Fields

1. `private final String name` Name of this remote.
2. `private final File path` **Relative** path to this remote.
3. `private final File directory` **Absolute** path to the repository (.gitlet) of this remote.

`Constructor` Creates the required folders & files and saves the `Remote` object with **name**, **path**, and recorded **directory path**.

### Branch

This class represents a `Branch` in our gitlet repository. Each branch has a `name` and a relative `path` which is in the form of `refs/heads/<branch-name>`. Additionally, to keep track of commits, it has a `commit` field holding the latest commit ever made by the user.

`refs/heads/<branch-name>`, which is the path of a `Branch` exists under two directories: the root directory `.gitlet` and the log directory `.gitlet/logs`. Files for writing `Branch` objects will be created *when calling the constructor*.

#### Fields

1. `private final String name` Name of the branch.
2. `private final File path` **Relative** path of the branch.
3. `private final File directory` **Absolute** path to the repository (.gitlet) of this branch.
4. `private Commit commit` Latest commit of the branch.

`Constructor` Sets the **name** and **path** of the branch as well as records the repository directory, creates the corresponding files, and sets the latest commit according to `.gitlet/HEAD` *if applicable*. The `directory` field exists since when our Gitlet goes remote we will encounter multiple repos, and the recorded directory can simply help us get the exact (absolute) path to the corresponding repository.

### Commit

This class represents a `Commit` in our gitlet repository. Each commit is **identified by its SHA-1 id**, which includes the *essential composition of a commit*: file references of its files, parent reference, log message, and commit time.

`Commit` objects are stored under `.gitlet/objects/<##>/<~commit-uid>` where `##` is the **first two digits** of the commit UID and **the rest** are the name of the object file. Its relative files are created and written when actually committing the commits by calling `public void commit()`.

#### Fields

1. `private Map<File, Blob> blobs` Map of file to the Blob contents of the commit.
2. `private final String message` Message of the commit.
3. `private final Date date` Timestamp of the commit.
4. `private Commit parent` Parent of the commit.
5. `private final Commit mergeParent` Merged parent of the commit.
6. `private String UID` 40-character SHA-1 id of the commit.

`Default Constructor`: Sets the ***initial commit*** with **message** `initial commit`, **timestamp** of `00:00:00 UTC, Thursday, 1 January 1970`, **parent** of `null`, and **empty blobs**.

`Normal Constructor` Sets the **message**, **time**, **parent** which *is the last commit*, and copies all blobs from the parent.

`Merge Constructor` Takes in two `Branch` objects, sets the `parent` and `mergeParent` accordingly and commit message to be `Merged [given branch name] into [current branch name].` Then takes everything staged for addition in the staging area to be in the `blobs`.

### Blob

A blob holds the detailed information of a file in the repository. It helps commits to keep track of **different** and **various versions** of files, which is *core of our version control system*. Mostly a `Blob` will be stored in form of `HashMap<File, Blob>` where the path to the file will be the key, lying under the staging area and tracked by all commits.

#### Fields

1. `private final String name` Name of this blob, relative path to the `CWD`.
2. `private final File path` Absolute path to the file of this blob.
3. `private final byte[] contents` Contents in the file of this blob.

`Default Constructor` Sets the **name** and **path** of the blob taken from the parameter and **reads the file contents** (`contents` will be `null` is the file does not exist).

`Merge Constructor` Sets **name** and **path** alike the default one, but it sets the **contents** our program specifies *(merge conflicts handling)* instead of reading from file.

All instance variables are all `final` because once the `Blob` is generated there won't be changes to it. ***Any modifications*** to the file contents will generate a **new** `Blob` to be tracked.

## Command Algorithms

The following shows algorithms of each command in our `Gitlet` version control system, and the validation of user inputs with the corresponding commands are done by the `Main` class:
- **If a user doesn’t input any arguments**, the program exits with message `Please enter a command.`
- **If a user inputs a command that doesn’t exist**, the program exits with message `No command with that name exists.`
- **If a user inputs a command with the wrong number or format of operands**, the program exits with message `Incorrect operands.`
- **If a user inputs a command that requires being in an initialized Gitlet working directory** (i.e., one containing a .gitlet subdirectory), **but is not in such a directory**, the program exits with message `Not in an initialized Gitlet directory.`

### init

The `init` command starts by attempting to set up the persistence of the repository, creating the folders needed for our gitlet system. **If there is already a gitlet repository existing in the current working directory**, the program exits with error message `A Gitlet version-control system already exists in the current directory.`

Then the system will create a default branch `master` in the repository, and commit an `initial commit` with no files into the new `master` branch.

### add

`add` command takes a `file name` as the second command line argument, adding a copy of the file as it currently exists to the staging area. **If the file does not exist**, print the error message `File does not exist.` and exit without changing anything. Firstly, the program checks *whether the current working version of the file is **identical to that in the last commit***, if true it will not be staged and will be removed it if it is already there. When the file is **eligible to be added**, it will be passed to `staged` area and the `Index` object will be updated and saved accordingly.

### rm

**If the file is neither staged nor tracked by the head commit, (or even does not exist)**, exit with message `No reason to remove the file.`

The `rm` command is quite straightforward:
1. It removes the file taken as `args[1]` from the `staged` area if it exists.
2. If the file is ***tracked*** in the current commit, 
   - it will be added to `removed` so that it is staged for removal.
   - it will be removed from the working directory if the user has not already done so.
3. Update the staging area (`Index` object).

### commit

The command starts by checking
- **If the commit has a blank message**, abort and print `Please enter a commit message.`
- **If no files have been staged for commit**, abort and print `No changes added to the commit.`

It then updates the `blobs` of the commit, **adding** those *new-staged* ones, **overwriting** the *modified* ones, and **removing** those that have been *stage for removal*.

After all things are done, the program will create the file holding the `commit` object, generate the **SHA-1** `UID` according to *file references*, *parent reference*, *log message*, and *commit time*, and **clear the staging area**. Finally, the program saves this `commit` under `.gitlet/objects` identified by its `UID`, finds the current working branch with `.gitlet/HEAD`, updates the commit into it, and appends the commit details to the `log` file of the branch.

According to the real `git`, the commit objects are *stored under the subfolder of their first two digits of their* `UID` so that it makes searching for commits ***faster*** (the complexity of naive pointer representation will be linear in the number of objects), checkout the [Persistence](#persistence) part!

### log

The `log` command displays information about each commit, starting at the current head commit, **backwards along the commit tree** until the `initial commit`, following the first parent commit links, ignoring any second parents found in merge commits. Since each commit writes to the `log` file of the working branch, the program just finds the current working branch with `.gitlet/HEAD` and prints out everything written in it.

### global-log

This command is similar to `log` except it displays information about ***all commits ever made***. The order of the commits does not matter. Therefore, the program just traverse all subdirectories under `.gitlet/objects` and get all the `Commit` objects ever stored. Print each of their information out using the overridden `toSting()` method.

### find

Finding a commit is going through **all commits ever made**, checks whether the commit message (log) is the same with the one searched and prints out the ***ids*** of them *one per line*. **If no such commit exists**, prints the error message `Found no commit with that message.`

### status

`status` command displays **what branches currently exist**, and marks the current branch with a `*`. It also displays **what files have been staged for *addition* or *removal***. The message to be printed of the repository can be accessed by calling `toString()` method in the `Index` class which reads and formats all information needed and listed in *lexicographic order*, using Java string-comparison.

### checkout

There are 3 possible use cases of the `checkout` command and the arguments passed in are checked by the `Main` class.

1. `java gitlet.Main checkout -- [file name]`
2. `java gitlet.Main checkout [commit id] -- [file name]`

The first two cases are implemented together. The program will consider the `[command id]` of the first usage with latest commit of the current working branch. Then, it will start finding the `blob` of *the specified file under the specified commit* where there are two failure cases (see below). The `commit id` specified should be either a 40-character **full-long** `UID` or an **abbreviation** which should be no less than 6 characters.
- **If the file does not exist in the previous commit**, abort and print `File does not exist in that commit.`
- **If no commit with the given id exists**, abort and print `No commit with that id exists.`

After finding the corresponding blob, ***the current existing file will be overwritten*** by the contents stored in the blob, thus done with checking-out a file.

3. `java gitlet.Main checkout [branch name]`

For this command, the program firstly finds the `Branch` object with the specified name given. Then **deletes all file tracked by the current commit** *before switching the new branch*, **checks-out the new branch** by overwriting the `HEAD` file, and in the end calls the `reset` command *(also clears the staging area for us)* to **restore all files** in the latest commit of the new branch.

Following checks are performed before any files are touched:
- **If no branch with that name exists**, print `No such branch exists.`
- **If that branch is the current branch**, print `No need to checkout the current branch.`
- **If a working file is untracked in the current branch and would be overwritten by the checkout**, print `There is an untracked file in the way; delete it, or add and commit it first.`

### branch

This command simply creates a new `Branch` object with the given name, and *synchronizes it with the current branch (including commits and logs)* using `sync(Branch branch)` method.
- **If a branch with the given name already exists**, print `A branch with that name already exists.`

### rm-branch

The `rm-branch` command is also simple. Conducts the following failure tests and finds the desired `Branch` object. Then just ***delete*** the related `ref` & `log` files, and, phew, done!
- **If a branch with the given name does not exist**, print `A branch with that name does not exist.`
- **If you try to remove the branch you’re currently on**, print `Cannot remove the current branch.`

### reset

The `reset` command utilizes the `Commit.find(String commitID)` method to find the desired `Commit` object, during which **if no commit with the given id exists**, the program exits with `No commit with that id exists.`; then it checks whether there is **untracked file(s) under the current branch** which would be dangerous if overwritten, also exits with `There is an untracked file in the way; delete it, or add and commit it first.`

As long as all checks are done, ***iterate through all blobs*** under the `Commit` object we've got, and perform `overwrite` methods on each of them, just like that in `checkout`. At the end, moves the current branch's head to that commit node and clear the staging area. *When moving the head pointer, the existing log file is also modified correspondingly.* As soon as the `commit` field of the branch is updated, we traverse through the history of this commit following the direct (first) parent until the initial commit and rewrite the log file.

### merge

This `merge` command is a lot more complicated than the previous one. Initially gets the `Branch` object specified and checks:
- **If attempting to merge a branch with itself**, print `Cannot merge a branch with itself.`
- **If there are staged additions or removals present**, print `You have uncommitted changes.`
- **If a branch with the given name does not exist**, print `A branch with that name does not exist.`

Then calls `Merge.merge(Branch current, Branch other)`, which belongs to a class exclusively designed for the `merge` command. The first thing we need to do is to find the *split commit* of the two branches: ***latest common ancestor***. Since our commit history will be very complicated, we need to get a **detailed map** of it. What I do is to utilize *Depth First Search* `DFS` to recursively traverse through the both latest commits and records each commit as well as its ***DEPTH** (very important, this is how we get to know whether the split point we find is the latest one!)*. 

```java
private static void traceBackCommit(Commit commit, Map<String, Integer> map, int depth) {
    // Base case
    if (commit == null) {
        return;
    }
    // Records commit id along with depth
    map.put(commit.getUID(), depth);
    // Where recursion happening...
    traceBackCommit(commit.getParent(), map, depth + 1);
    traceBackCommit(commit.getMergeParent(), map, depth + 1);
}
```

After getting the two commit maps with depth recorded, the program iterates over each key `commit id` in the **current-branch** commit map, and see whether the **other-branch** commit map *contains* it, and takes ***the one with the smallest depth*** to become the split commit we choose. With the split commit, we again need to conduct some checking:
- **If the *split point* is the same commit as the given branch**, then we do nothing, the merge is complete with the message `Given branch is an ancestor of the current branch.`
- **If the *split point* is the current branch**, then the effect is to check-out the given branch with the message `Current branch fast-forwarded.`

Now we are starting to actually merge the files in two branches. The seven rules can be simplified into the following. Strongly recommended to check out this [Merge Example video](https://www.youtube.com/watch?v=JR3OYCMv9b4&t=929s), everything is explained in detail!
1. Not present in `split` nor in `other` but in `current` -> `current`
2. Not present in `split` nor in `current` but in `other` -> `other`
3. Unmodified in `current` but not present in `other` -> remove
4. Unmodified in `other` but not present in `head` -> remove
5. Modified in `other` but not in `current` -> `other`
6. Modified in `current` but not in `head` -> `current`
7. Modified in both `current` and `other`,
   1. in the same way -> `current`/`other` (they are the same)
   2. in different ways -> ***CONFLICT**, refer to [merge spec](https://sp21.datastructur.es/materials/proj/proj2/proj2#merge) for how to format merge conflicts*

I follow the guidance as well when going through every file, see [here](gitlet/Merge.java#L60). Therefore, we have got a ***merged*** version of `Map<File, Blob>`. This map is **forced to replace the *"staged for addition"*** part in the staging area so that the new merge commit will track them during which also checks **if untracked file(s) in the current commit would be overwritten** and print `There is an untracked file in the way; delete it, or add and commit it first.`

Finally! Make a merge commit and use the `reset` command to help write those files!

### add-remote

Adding a remote is simply instantiating a *new* `Remote` object. Be aware that the separator `/` passed passed in must be **replaced by** `Java.io.File.separator` to adapt to both *Windows* and *Unix* systems. An addition check is conducted: **if a remote with the given name already exists**, print the error message `A remote with that name already exists.`, but we do NOT have to check whetehr the user name and server information are legit.

### rm-remote

Two steps to remove a remote:
1. Check **if a remote with the given name does not exist**, print the error message `A remote with that name does not exist.`
2. Find the `Remote` object and discard its reference file.

### push

A `push` is to append the current branch's commits to the end of the given branch at the given remote. However, the prerequisite is to ensure that the **remote branch's head is in the history of the current local head** *(the local branch contains some commits in the future of the remote branch)*. Hence check
- **If the remote** `.gitlet` **directory does not exist**, print `Remote directory not found.`
- **If the remote branch's head is not in the history of the current local head**, print `Please pull down remote changes before pushing.`

What I deal with commits is to coarsely **write all commits** in the current branch's history *to the remote repository*. Then we have got the **remote** `Branch` object, resets the it with the current head so that its `commit` field and log file is updated.

### fetch

`fetch` does things reversely compared to `push`. It brings down commits from the remote Gitlet repository into the local Gitlet repository. When fetching, the program automatically generates **a new branch in the local repo** called `<remote-name>/<remote-branch-name>` *(if one is not already created)* to store the remote branch history. Following that, it copies **all commits** from the given branch in the remote repository, and resets the local branch `<remote-name>/<remote-branch-name>` with the remote branch head so that its `commit` field and log file is updated.

Some checks:
- **If the remote** `.gitlet` **directory does not exist**, print `Remote directory not found.`
- **If the remote Gitlet repository does not have the given branch name**, print `That remote does not have that branch.`

### pull

`pull` is actually `fetch` and `merge` the **local branch *created by a remote*** as in the specification, and the checks are also the same.

## Persistence

Structure of a `.gitlet` repository:

```
.gitlet/
├── objects/
|   ├── <##>/    # First two digits of the commit UID
|   |   ├── <~commit-id>       # Commit object,
|   |   └── ...                # excluding the first two digits
|   └── ...
├── refs/
|   ├── heads/
|   |   ├── <branch-name>      # Branch object
|   |   └── ...
|   └── remotes/
|       ├── <remote-name>      # Remote object
|       └── ...
├── logs/
|   └── refs/
|       └── heads/
|           ├── <branch-name>  # Full commit logs of that branch
|           └── ...
├── index        # Staging area, holds the Index object
└── HEAD         # Path to the current branch - refs/heads/<branch-name>
```
