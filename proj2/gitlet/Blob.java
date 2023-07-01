package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;
import static gitlet.Repository.*;

/**
 * Represents a blob tracking each file in the repository.
 * @author Yuhan Zhang
 */
public class Blob implements Serializable {

    /** Name of this blob, relative path to the CWD */
    private final String name;
    /** Absolute path to the file of this blob. */
    private final File path;
    /** Contents in the file of this blob. */
    private byte[] contents;

    /** Constructor, sets the name and path, reads the contents from file. */
    public Blob(String name, File path) {
        this.name = name;
        this.path = path;
        readContents();
    }

    /** Reads the contents from the file. */
    public void readContents() {
        this.contents = Utils.readContents(this.path);
    }

    /** Returns the name of the blob. */
    public String getName() {
        return this.name;
    }

    /** Returns the path to the file. */
    public File getPath() {
        return this.path;
    }

    /** Returns the contents of the file. */
    public byte[] getContents() {
        return this.contents;
    }

}
