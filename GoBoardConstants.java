/**
 * Constants for the Game of Life, adapted to fit on
 * a Go board.
 */
public interface GoBoardConstants {
    
    /**
     * The index of the top row
     */
    public static final int FIRST_ROW = 1;
    /**
     * The index of the bottom row
     */
    public static final int LAST_ROW = 19;
    /**
     * The total number of rows in the board
     */
    public static final int ROW_COUNT = LAST_ROW - FIRST_ROW + 1;

    /**
     * The index of the left-most column
     */
    public static final int FIRST_COLUMN = 1;
    /**
     * The index of the right-most column
     */
    public static final int LAST_COLUMN = 19;
    /**
     * The total number of columns in the board
     */
    public static final int COLUMN_COUNT = LAST_COLUMN - FIRST_COLUMN + 1;
    
}
