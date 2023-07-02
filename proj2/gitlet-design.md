# Gitlet Design Document

**Name**: Yuhan Zhang

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

1. `private final HashMap<File, Blob> staged` Map of the files staged.
2. `private final HashMap<File, Blob> tracked` Map of the files tracked.
3. `private final HashMap<File, Blob> removed` Map of the files removed.

`Default Constructor` Initializes the `staged`, `tracked`, and `removed` maps.

### Branch

This class represents a `Branch` in our gitlet repository. Each branch has a `name` and a relative `path` which is in the form of `refs/heads/<branch-name>`. Additionally, to keep track of commits, it has a `commit` field holding the latest commit ever made by the user.

`refs/heads/<branch-name>`, which is the path of a `Branch` exists under two directories: the root directory `.gitlet` and the log directory `.gitlet/logs`. Files for writing `Branch` objects will be created *when calling the constructor*.

#### Fields

1. `private final String name`: Name of the branch.
2. `private final File path`: **Relative** path of the branch.
3. `private Commit commit`: Latest commit of the branch.

`Constructor` Sets the **name** and **path** of the branch, creates the corresponding files, and sets the latest commit according to `.gitlet/HEAD` *if applicable*.

### Commit

This class represents a `Commit` in our gitlet repository. Each commit is **identified by its SHA-1 id**, which includes the *essential composition of a commit*: file references of its files, parent reference, log message, and commit time.

`Commit` objects are stored under `.gitlet/objects/<##>/<~commit-uid>` where `##` is the **first two digits** of the commit UID and **the rest** are the name of the object file. Its relative files are created and written when actually committing the commits by calling `public void commit()`.

#### Fields

1. `private HashMap<File, Blob> blobs` Map of file to the Blob contents of the commit.
2. `private final String message` Message of the commit.
3. `private final Date date` Timestamp of the commit.
4. `private Commit parent` Not implemented yet.
5. `private String UID` 40-character SHA-1 id of the commit.

`Default Constructor`: Sets the ***initial commit*** with **message** `initial commit`, **timestamp** of `00:00:00 UTC, Thursday, 1 January 1970`, **parent** of `null`, and **empty blobs**.

`Constructor` Sets the **message**, **time**, **parent** which *is the last commit*, and copies all blobs from the parent.

### Blob

A blob holds the detailed information of a file in the repository. It helps commits to keep track of **different** and **various versions** of files, which is *core of our version control system*. Mostly a `Blob` will be stored in form of `HashMap<File, Blob>` where the path to the file will be the key, lying under the staging area and tracked by all commits.

#### Fields

1. `private final String name` Name of this blob, relative path to the `CWD`.
2. `private final File path` Absolute path to the file of this blob.
3. `private final byte[] contents` Contents in the file of this blob.

`Constructor` Sets the **name** and **path** of the blob taken from the parameter and **reads the file contents**.

All instance variables are all `final` because once the `Blob` is generated there won't be changes to it. ***Any modifications*** to the file contents will generate a **new** `Blob` to be tracked.

## Algorithms

The following shows algorithms of each command in our `Gitlet` version control system, and the validation of user inputs with the corresponding commands are done by the `Main` class:
- **If a user doesn’t input any arguments**, the program exits with message `Please enter a command.`
- **If a user inputs a command that doesn’t exist**, the program exits with message `No command with that name exists.`
- **If a user inputs a command with the wrong number or format of operands**, the program exits with message `Incorrect operands.`
- **If a user inputs a command that requires being in an initialized Gitlet working directory** (i.e., one containing a .gitlet subdirectory), **but is not in such a directory**, the program exits with message `Not in an initialized Gitlet directory.`

### init

The `init` command starts by attempting to set up the persistence of the repository, creating the folders needed for our gitlet system. **If there is already a gitlet repository existing in the current working directory**, the program exits with error message `A Gitlet version-control system already exists in the current directory.`

Then the system will create a default branch `master` in the repository, and commit an `initial commit` with no files into the new `master` branch.

### add

`add` command takes a `file name` as the second command line argument, adding a copy of the file as it currently exists to the staging area. **If the file does not exist**, print the error message `File does not exist.` and exit without changing anything. Firstly, the program checks *whether the current working version of the file is **identical to that in the last commit***, if true it will not be staged and will be removed it if it is already there. When the file is **eligible to be added**, it will be passed to `staged` and `tracked` area so that the `Index` object will be updated and saved accordingly.

### commit

The command starts by checking
- **If no files have been staged for commit**, abort and print `No changes added to the commit.`
- **If the commit have a non-blank message**, abort and print `Please enter a commit message.`

When committing a `commit` by calling `public void commit()`, the program will create the file holding the `commit` object, generate the **SHA-1** `UID` according to *file references*, *parent reference*, *log message*, and *commit time*, and **clear the staging area**. Then, the program saves this `commit` under `.gitlet/objects` identified by its `UID`, finds the current working branch with `.gitlet/HEAD`, updates the commit into it, and appends the commit details to the `log` file of the branch.

According to the real `git`, the commit objects are *stored under the subfolder of their first two digits of their* `UID` so that it makes searching for commits ***faster*** (the complexity of naive pointer representation will be linear in the number of objects)

> *Not finished yet, lots more commands to integrate.*

### log

The `log` command displays information about each commit, starting at the current head commit, **backwards along the commit tree** until the `initial commit`, following the first parent commit links, ignoring any second parents found in merge commits. Since each commit writes to the `log` file of the working branch, the program just finds the current working branch with `.gitlet/HEAD` and prints out everything written in it.

### status

`status` command displays **what branches currently exist**, and marks the current branch with a `*`. It also displays **what files have been staged for *addition* or *removal***. The message to be printed of the repository can be accessed by `Index.toString()` method which reads and formats all information needed and listed in *lexicographic order*, using Java string-comparison.

### checkout

There are 3 possible use cases of the `checkout` command and the arguments passed in are checked by the `Main` class.

1. `java gitlet.Main checkout -- [file name]`
2. `java gitlet.Main checkout [commit id] -- [file name]`

The first two cases are implemented together. The program will consider the `[command id]` of the first usage with latest commit of the current working branch. Then, it will start finding the `blob` of *the specified file under the specified commit* where there are two failure cases (see below). The `commit id` specified should be either a 40-character **full-long** `UID` or an **abbreviation** which should be no less than 6 characters.
- **If the file does not exist in the previous commit**, abort and print `File does not exist in that commit.`
- **If no commit with the given id exists**, abort and print `No commit with that id exists.`

After finding the corresponding blob, ***the current existing file will be overwritten*** by the contents stored in the blob, thus done with checking-out a file.

3. `java gitlet.Main checkout [branch name]`

> *Checking out a certain branch is not implemented yet.*

## Persistence

Structure of the `.gitlet` repository *(temporarily built until now)*:

```
.gitlet/
├── objects/
|   ├── <##>/    # First two digits of the commit UID
|   |   ├── <~commit-id>       # Commit object,
|   |   └── ...                # excluding the first two digits
|   └── ...
├── refs/
|   └── heads/
|       ├── <branch-name>      # Branch object
|       └── ...
├── logs/
|   └── refs/
|       └── heads/
|           ├── <branch-name>  # Full commit logs of that branch
|           └── ...
├── index        # Staging area, holds the Index object
└── HEAD         # Path to the current branch - refs/heads/<branch-name>
```
