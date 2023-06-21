package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static gitlet.Utils.*;
import static gitlet.Repository.*;

/** Represents a gitlet commit object.
 *
 *  @author Yuhan Zhang
 */
public class Commit implements Serializable {

    /** UID of this commit. */
    private final String UID;
    /** Message of this commit. */
    private final String message;
    /** Date of this commit. */
    private final Date date;
    /** Parent of this commit. */
    private Commit parent;

    /** Default constructor, initializes the very first commit of the repo. */
    public Commit() {
        this.message = "initial commit";
        this.date = new Date(0);
        this.parent = null;
        this.UID = sha1(null + message + date);
    }

    /** Constructor of a normal commit. */
    public Commit(String message, Commit parent) {
        this.message = message;
        this.date = new Date();
        this.parent = parent;
        this.UID = sha1(parent + message + date);
    }

    /** Commit and write to the logs. */
    public void commit() {
        try {
            this.getPath().createNewFile();
            writeObject(this.getPath(), this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Branch b = readObject(HEAD, Branch.class);
        b.setCommit(this);
        writeContents(b.getLogFile(), this.toString() +
                readContentsAsString(b.getLogFile()));
    }

    /** Return the relative path of the commit. */
    public File getPath() {
        return join(LOGS_DIR, this.UID);
    }

    /** Return the commit as a log of String. */
    @Override
    public String toString() {
        SimpleDateFormat d = new SimpleDateFormat("E MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH);
        return "===\ncommit " + this.UID + "\n" +
                "Date: " + d.format(this.date) + "\n" +
                this.message + "\n";
    }

}
