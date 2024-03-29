package game2048;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.Observable;


/** The state of a game of 2048.
 *  @author Yuhan Zhang
 */
public class Model extends Observable {
    /** Current contents of the board. */
    private Board board;
    /** Current score. */
    private int score;
    /** Maximum score so far.  Updated when game ends. */
    private int maxScore;
    /** True iff game is ended. */
    private boolean gameOver;

    /* Coordinate System: column C, row R of the board (where row 0,
     * column 0 is the lower-left corner of the board) will correspond
     * to board.tile(c, r).  Be careful! It works like (x, y) coordinates.
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = maxScore = 0;
        gameOver = false;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (row, col) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score, int maxScore, boolean gameOver) {
        int size = rawValues.length;
        board = new Board(rawValues, score);
        this.score = score;
        this.maxScore = maxScore;
        this.gameOver = gameOver;
    }

    /** Return the current Tile at (COL, ROW), where 0 <= ROW < size(),
     *  0 <= COL < size(). Returns null if there is no tile there.
     *  Used for testing. Should be deprecated and removed.
     *  */
    public Tile tile(int col, int row) {
        return board.tile(col, row);
    }

    /** Return the number of squares on one side of the board.
     *  Used for testing. Should be deprecated and removed. */
    public int size() {
        return board.size();
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        checkGameOver();
        if (gameOver) {
            maxScore = Math.max(score, maxScore);
        }
        return gameOver;
    }

    /** Return the current score. */
    public int score() {
        return score;
    }

    /** Return the current maximum game score (updated at end of game). */
    public int maxScore() {
        return maxScore;
    }

    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        gameOver = false;
        board.clear();
        setChanged();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
        checkGameOver();
        setChanged();
    }

    /** Tilt the board toward SIDE. Return true iff this changes the board.
     * 1. If two Tile objects are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     * */
    public boolean tilt(Side side) {
        boolean changed;
        changed = false;

        // Change Viewing perspective according to the given side
        // so that only need to write code for one direction.
        board.setViewingPerspective(side);

        // Implementation of the up direction (Side.NORTH):

        // Break the board into columns.
        Tile[] tiles = findAllTiles(board);
        Tile[][] tile = new Tile[board.size()][board.size()];
        int count = 0;
        for (int i = 0; i < board.size(); i++) {
            for (int j = 0; j < board.size(); j++) {
                tile[i][j] = tiles[count++];
            }
        }

        // Iterate all columns and get the moves for each column.
        for (int i = tile.length - 1; i >= 0; i--) {
            int[] move = movesForCol(tile[i]);
            // Iterate all tiles in the column.
            for (int j = tile[i].length - 1; j >= 0; j--) {
                // Only focus on valid (non-empty) tiles.
                if (tile[i][j] != null) {
                    // Conduct the move.
                    board.move(i, j + move[j], tile[i][j]);
                    // Set changed to true if move != 0.
                    if (move[j] != 0) {
                        changed = true;
                    }
                }
            }
        }

        // Restore viewing perspective.
        board.setViewingPerspective(Side.NORTH);

        checkGameOver();
        if (changed) {
            setChanged();
        }
        return changed;
    }

    /** Given a column of tiles, return an array of ints
     *  containing the relative move step for each tile.
     *  */
    public int[] movesForCol(Tile[] col) {
        // Get the values of each tile in the given column.
        int[] val = new int[col.length];
        int count = 0;
        for (Tile t : col) {
            if (t != null) {
                val[count++] = t.value();
            } else {
                val[count++] = 0;
            }
        }

        // Calculate the moves for each tile:
        int[] move = new int[col.length];

        // Organize the tiles, remove 0s in the middle,
        // i.e. push all valid tile to the top without merge.
        for (int i = 0; i < val.length; i++) {
            int numOfZero = 0;
            for (int j = i + 1; j < val.length; j++) {
                if (val[j] == 0) {
                    numOfZero++;
                }
            }
            move[i] = numOfZero;
        }

        // Look for merges.
        for (int i = val.length - 1; i >= 0; i--) {
            // Skip empty spaces
            if (val[i] == 0) {
                continue;
            }
            // Check if the next valid tile has the same value.
            for (int j = i - 1; j >= 0; j--) {
                if (val[i] == val[j]) {
                    // Clear the value for later iterations.
                    val[j] = 0;
                    // Inc all moves from 0 to j for merge.
                    for (int k = 0; k <= j; k++) {
                        move[k]++;
                    }
                    // Add corresponding score.
                    score += val[i] * 2;
                    break;
                }
            }
        }

        // Return the list of moves for each tile in the column.
        return move;
    }

    /** Checks if the game is over and sets the gameOver variable
     *  appropriately.
     */
    private void checkGameOver() {
        gameOver = checkGameOver(board);
    }

    /** Determine whether game is over. */
    private static boolean checkGameOver(Board b) {
        return maxTileExists(b) || !atLeastOneMoveExists(b);
    }

    /** Check if the given tile is valid and has the same value. */
    public static boolean sameValue(Tile t, int val) {
        return t != null && t.value() == val;
    }

    /** A helper method to get all tiles from the board,
     *  and return them as a list of tiles.
     */
    public static Tile[] findAllTiles(Board b) {
        ArrayList<Tile> tList = new ArrayList<>();
        // Traverse the board and append all the tiles.
        int boardSize = b.size();
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                tList.add(b.tile(i, j));
            }
        }
        // Convert to array and return.
        Tile[] ts = new Tile[tList.size()];
        tList.toArray(ts);
        return ts;
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public static boolean emptySpaceExists(Board b) {
        // Traverse all tiles on the board to check if any of them is empty.
        Tile[] allTiles = findAllTiles(b);
        for (Tile t : allTiles) {
            if (t == null) {
                return true;
            }
        }
        // No empty space, return false.
        return false;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public static boolean maxTileExists(Board b) {
        // Traverse all tiles on the board to check if MAX_PIECE exists.
        Tile[] allTiles = findAllTiles(b);
        for (Tile t : allTiles) {
            if (sameValue(t, MAX_PIECE)) {
                return true;
            }
        }
        // MAX_PIECE not found, return false.
        return false;
    }

    /** Returns true if the given tile has a neighbor with the same value,
     *  on the given board.
     */
    public static boolean sameValueAdjacentExists(Board b, Tile t) {
        // Down
        if (t.row() - 1 >= 0) {
            Tile ts = b.tile(t.col(), t.row() - 1);
            if (sameValue(t, ts.value())) {
                return true;
            }
        }
        // Up
        if (t.row() + 1 < b.size()) {
            Tile ts = b.tile(t.col(), t.row() + 1);
            if (sameValue(t, ts.value())) {
                return true;
            }
        }
        // Right
        if (t.col() - 1 >= 0) {
            Tile ts = b.tile(t.col() - 1, t.row());
            if (sameValue(t, ts.value())) {
                return true;
            }
        }
        // Left
        if (t.col() + 1 < b.size()) {
            Tile ts = b.tile(t.col() + 1, t.row());
            if (sameValue(t, ts.value())) {
                return true;
            }
        }
        // None of the neighbors matched.
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public static boolean atLeastOneMoveExists(Board b) {
        // 1. At least one empty space on the board.
        if (emptySpaceExists(b)) {
            return true;
        }

        // 2. Two adjacent tiles with the same value.
        Tile[] allTiles = findAllTiles(b);
        for (Tile t : allTiles) {
            if (sameValueAdjacentExists(b, t)) {
                return true;
            }
        }

        // No valid move exists.
        return false;
    }


    @Override
     /** Returns the model as a string, used for debugging. */
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int row = size() - 1; row >= 0; row -= 1) {
            for (int col = 0; col < size(); col += 1) {
                if (tile(col, row) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(col, row).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (max: %d) (game is %s) %n", score(), maxScore(), over);
        return out.toString();
    }

    @Override
    /** Returns whether two models are equal. */
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (getClass() != o.getClass()) {
            return false;
        } else {
            return toString().equals(o.toString());
        }
    }

    @Override
    /** Returns hash code of Model’s string. */
    public int hashCode() {
        return toString().hashCode();
    }
}
