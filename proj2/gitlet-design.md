# Gitlet Design Document

**Name**: Yuhan Zhang

## Classes and Data Structures

### Main

This is the driver class of our `Gitlet` version control system. It takes in arguments from the command line and based on the command (the first element of the `args` array) calls the corresponding command in the gitlet `Repository` which will actually execute the logic of the command. It also **validates the arguments and the existence of the repository** based on the command to ensure that enough arguments were passed in.

### Repository

This is where the main logic of our program will live. The class mainly deals with **high-level structure** of the gitlet repository itself, *but not the detailed implementation of a single branch or commit*. It is also the container of all the gitlet command methods which are called by the `Main` class.

Refer to the internal structure of `.gitlet` directory at the [Persistence](#persistence) part.

#### Fields

1. `public static final File CWD = new File(System.getProperty("user.dir"))` The current wording directory.
2. `public static final File GITLET_DIR = join(CWD, ".gitlet")` The hidden `.gitlet` directory of our version control system, it holds everything needed to set up Gitlet.
3. `public static final File OBJECTS_DIR = join(GITLET_DIR, "objects")` Not implemented yet.
4. `public static final File REFS_DIR = join(GITLET_DIR, "refs")` The directory holding all references to branches related files.
5. `public static final File LOGS_DIR = join(GITLET_DIR, "logs")` The directory holding the commit logs based on the structure of branches and commit related files.
6. `public static final File HEAD = join(GITLET_DIR, "HEAD")` File under our repository root directory, contains the relative path to the current working branch `refs/heads/<branch-name>`.
7. `public static final File INDEX = join(GITLET_DIR, "index")` Not implemented yet.

Everything within this class shall be `static` since there is no need to instantiate a `Repository` object, and we can directly call and refer to its variables and methods.

### Branch

This class represents a `Branch` in our gitlet repository. Each branch has a `name` and a relative `path` which is in the form of `refs/heads/<branch-name>`. Additionally, to keep track of commits, it has a `commit` field holding the latest commit ever made by the user.

`refs/heads/<branch-name>`, which is the path of a `Branch` exists under two directories: the root directory `.gitlet` and the log directory `.gitlet/logs`. Files for writing `Branch` objects will be created *when calling the constructor*.

#### Fields

1. `private final String name`: Name of the branch.
2. `private final File path`: **Relative** path of the branch.
3. `private final File refFile`: **Absolute** path to the ref file of the branch.
4. `private final File logFile`: **Absolute** path to the log file of the branch.
5. `private Commit commit`: Latest commit of the branch.

`Constructor`: Sets the **name** and **path** of the branch, creates the corresponding files, and sets the latest commit according to `.gitlet/HEAD` *if applicable*.

### Commit

This class represents a `Commit` in our gitlet repository. Each commit is **identified by its SHA-1 id**, which includes the *essential composition of a commit*: file references of its files, parent reference, log message, and commit time.

`Commit` objects are stored under `.gitlet/logs/<commit-uid>`. Its relative files are created and written when actually committing the commits by calling `public void commit()`.

#### Fields

1. `UID [Final]`: 40-character SHA-1 id of the commit.
2. `message [Final]`: Message of the commit.
3. `date [Final]`: Timestamp of the commit.
4. `parent`: Not implemented yet.

`Default Constructor`: Sets the ***initial commit*** with **message** `initial commit`, **timestamp** of `00:00:00 UTC, Thursday, 1 January 1970`, **parent** of `null`, and finally generates the `UID`.

`Constructor`: Sets the **message**, **time**, and **parent** using the parameters, and generates the `UID`.

## Algorithms

The following shows algorithms of each command in our `Gitlet` version control system, and the validation of user inputs with the corresponding commands are done by the `Main` class:

- **If a user doesn’t input any arguments**, the program exits with message `Please enter a command.`
- **If a user inputs a command that doesn’t exist**, the program exits with message `No command with that name exists.`
- **If a user inputs a command with the wrong number or format of operands**, the program exits with message `Incorrect operands.`
- **If a user inputs a command that requires being in an initialized Gitlet working directory** (i.e., one containing a .gitlet subdirectory), **but is not in such a directory**, the program exits with message `Not in an initialized Gitlet directory.`

### init

The `init` command starts by attempting to set up the persistence of the repository, creating the folders needed for our gitlet system. **If there is already a gitlet repository existing in the current working directory**, the program exits with error message `A Gitlet version-control system already exists in the current directory.`

Then the system will create a default branch `master` in the repository, and commit an `initial commit` with no files into the new `master` branch.

### commit

**Not fully implemented yet.**

When committing a `commit` by calling `public void commit()`, the program will create the file holding the `commit` object, find the current working branch with `.gitlet/HEAD`, update the commit into it, and append the commit details to the `log` file of the branch.

### log

The `log` command displays information about each commit, starting at the current head commit, **backwards along the commit tree** until the `initial commit`, following the first parent commit links, ignoring any second parents found in merge commits. Since each commit writes to the `log` file of the working branch, the program just finds the current working branch with `.gitlet/HEAD` and prints out everything written in it.

## Persistence

Structure of the `.gitlet` repository *(temporarily built until now)*:

```
.gitlet/
├── objects/  # Not implemented yet
├── refs/
|   └── heads/
|       ├── <branch-name>      # Branch object
|       └── ...
├── logs/
|   ├── refs/
|   |   └── heads/
|   |       ├── <branch-name>  # Full commit logs of that branch
|   |       └── ...
|   ├── <commit-uid>           # Commit object
|   └── ...
├── index     # Not implemented yet
└── HEAD      # Path to the current branch - refs/heads/<branch-name>
```
