import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Observable;

import javax.swing.JOptionPane;

/**
 * A class that prints out generations of Game Of Life. It implements
 * GameOfLife.
 *
 * @author Alieu Sanneh
 * @version PA7: Game of Life, Serialization and Final Submission: Challenge
 */
public class MyGameOfLife extends Observable implements GameOfLife,

		GoBoardConstants {
	/*
	 * CheckerBoardConstants { //
	 */

	/**
    * field for transitional BIRTH;
    */
	public static final int BIRTH = -1;
	/**
    * field for transitional LONELISNESS
    */  
	public static final int LONELINESS = -2;
	/**
    * field for transitional OVERCROWDING
    */ 
	public static final int OVERCROWDING = -3;

	// array to hold the cells
	private int[][] world;

	/**
	 * The parameterless constructor for the class. It initializes the board.
	 */
	public MyGameOfLife() {
		world = new int[ROW_COUNT][COLUMN_COUNT];

		// save a starting point configuration into
		// file start.gol
		// once done, revert cell positions back
		setStartingState();
		try {
			fileSave("start.gol");
		} catch (IOException e) {
			
		}
		clearBoard();
	}
   
	/**
	 * The method that sets the starting state
	 */
	public void setStartingState() {
		// The GameOfLifeBoard will begin with all the cells dead.
		for (int x = FIRST_ROW; x < FIRST_ROW + ROW_COUNT; x++) {
			for (int y = FIRST_COLUMN; y < FIRST_COLUMN + COLUMN_COUNT; y++) {
				this.setCellState(x, y, GameOfLife.DEAD);
			}
		}

		// the two alive cells in the upper left
		this.setCellState(3, 4, GameOfLife.ALIVE);
		this.setCellState(4, 4, GameOfLife.ALIVE);
		// the block in the upper right
		this.setCellState(3, 13, GameOfLife.ALIVE);
		this.setCellState(3, 14, GameOfLife.ALIVE);
		this.setCellState(4, 13, GameOfLife.ALIVE);
		this.setCellState(4, 14, GameOfLife.ALIVE);
		// the beehive in the center
		this.setCellState(8, 7, GameOfLife.ALIVE);
		this.setCellState(8, 8, GameOfLife.ALIVE);
		this.setCellState(9, 6, GameOfLife.ALIVE);
		this.setCellState(9, 9, GameOfLife.ALIVE);
		this.setCellState(10, 7, GameOfLife.ALIVE);
		this.setCellState(10, 8, GameOfLife.ALIVE);
		// the glider on the lower left
		this.setCellState(15, 6, GameOfLife.ALIVE);
		this.setCellState(16, 4, GameOfLife.ALIVE);
		this.setCellState(16, 6, GameOfLife.ALIVE);
		this.setCellState(17, 5, GameOfLife.ALIVE);
		this.setCellState(17, 6, GameOfLife.ALIVE);
		// the blinker in the lower right
		this.setCellState(13, 13, GameOfLife.ALIVE);
		this.setCellState(13, 14, GameOfLife.ALIVE);
		this.setCellState(13, 15, GameOfLife.ALIVE);

		notifyOurChange();
	}

	/**
	 * Gets the current state of a given cell.
	 *
	 * @param row The row number of the given cell, 1 - 19
	 * @param col The column number of the given cell, 1 - 19
	 * @return The current state of the given cell
	 * @throws IllegalArgumentException If row/column value invalid
	 */
	@Override
	public int getCellState(int row, int col) {
		if (row < FIRST_ROW || row > FIRST_ROW + ROW_COUNT || col < FIRST_COLUMN || col > FIRST_COLUMN + COLUMN_COUNT) {
			throw new IllegalArgumentException("invalid row or column value. Value: (" + row + "," + col + ")");
		}
		return world[row - FIRST_ROW][col - FIRST_COLUMN];

	}

	/**
	 * Sets the state of the given cell to the given state value.
	 * 
	 * @param row The row number of the given cell, 1 - 19
	 * @param col The column number of the given cell, 1 - 19
	 * @param state The new state for the given cell.
	 * @throws IllegalArgumentException If row/column value invalid
	 */
	@Override
	public void setCellState(int row, int col, int state) {
		if (row < FIRST_ROW || row > FIRST_ROW + ROW_COUNT || col < FIRST_COLUMN || col > FIRST_COLUMN + COLUMN_COUNT) {
			throw new IllegalArgumentException("invalid row or column value. Value: (" + row + "," + col + ")");
		}
		world[row - FIRST_ROW][col - FIRST_COLUMN] = state;
		notifyOurChange();
	}

	/**
	 * Creates an ASCII representation of the current board state.
	 * It uses StringBuilder().
	 * @return The ASCII representation of the current board state.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int x = FIRST_ROW; x < FIRST_ROW + ROW_COUNT; x++) {
			for (int y = FIRST_COLUMN; y < FIRST_COLUMN + COLUMN_COUNT; y++) {
				// if the cell is alive, put 'o', else '.'
				sb.append(String.format("%c ", world[x - FIRST_ROW][y - FIRST_COLUMN] == ALIVE ? 'O' : '.'));
			}
			// Separate the rows with a new line character ('\n').
			sb.append("\n");
		}
		return sb.toString();
	}

	/**
	 * This helper method checks if the cell is alive or not.
    *
	 * @param cellRow The row number of the given cell, 1 - 19
	 * @param cellColumn The column number of the given cell, 1 - 19
	 *
	 * @return The state of the cell(DEAD or Alive?)
	 */
	private boolean isCellAlive(int cellRow, int cellColumn) {
		// if it is a valid cell
		if (cellRow < ROW_COUNT && cellRow >= 0 && cellColumn < COLUMN_COUNT && cellColumn >= 0) {
			if (world[cellRow][cellColumn] == ALIVE || world[cellRow][cellColumn] == LONELINESS
					|| world[cellRow][cellColumn] == OVERCROWDING) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This helper method checks for living neighbors.
	 *
	 * @param row The row number of the given cell, 1 - 19
	 * @param col The column number of the given cell, 1 - 19
	 *
	 * @return The number of living neighbors
	 */
	private int numNeighborsAlive(int row, int col) {
		int activeNeighbours = 0;

		if (isCellAlive(row - 1, col - 1))
			activeNeighbours++;
		if (isCellAlive(row - 1, col))
			activeNeighbours++;
		if (isCellAlive(row - 1, col + 1))
			activeNeighbours++;

		if (isCellAlive(row, col - 1))
			activeNeighbours++;
		if (isCellAlive(row, col + 1))
			activeNeighbours++;

		if (isCellAlive(row + 1, col - 1))
			activeNeighbours++;
		if (isCellAlive(row + 1, col))
			activeNeighbours++;
		if (isCellAlive(row + 1, col + 1))
			activeNeighbours++;

		return activeNeighbours;
	}

	/**
	 * This helper method checks if cell will be alive in next generation or not
	 *
	 * @param numLivingNeighbors The number of living neighbors
	 * @param cellCurrentlyLiving The current state of the cell
	 *
	 * @return The state of the cell in the next gen.
	 */
	private int isCellLivingInNextGeneration(int numLivingNeighbors, int cellCurrentlyLiving) {
		// If a live cell has two (2) or three (3) live neighbors, it remains alive.
		if (cellCurrentlyLiving == ALIVE) {
			if (numLivingNeighbors == 3 || numLivingNeighbors == 2) {
				return ALIVE;
			} else if (numLivingNeighbors < 2) {
				return LONELINESS;
			} else {
				return OVERCROWDING;
			}
		} else {
			// If a dead cell has exactly three (3) live neighbors, it becomes alive.
			if (numLivingNeighbors == 3) {
				return BIRTH;
			} else {
				return DEAD;
			}
		}
	}

	/**
	 * Calcuates the state of the Game of Life booard for the next generation and
	 * updates the board state.
	 */
	@Override
	public void nextGeneration() {

		// cells get transitional states only at start
		for (int i = 0; i < world.length; i++) {
			for (int j = 0; j < world[i].length; j++) {
				world[i][j] = isCellLivingInNextGeneration(numNeighborsAlive(i, j), world[i][j]);
			}
		}

		// change transitional states to permanent states only at last step
		for (int i = 0; i < world.length; i++) {
			for (int j = 0; j < world[i].length; j++) {
				if (world[i][j] == LONELINESS) {
					world[i][j] = DEAD;
				}
				if (world[i][j] == OVERCROWDING) {
					world[i][j] = DEAD;
				}
				if (world[i][j] == BIRTH) {
					world[i][j] = ALIVE;
				}
			}
		}

		notifyOurChange();
	}
	/**
	 * The method to clear the board
	 */
	public void clearBoard() {
		// set all the cells to DEAD
		for (int x = FIRST_ROW; x < FIRST_ROW + ROW_COUNT; x++) {
			for (int y = FIRST_COLUMN; y < FIRST_COLUMN + COLUMN_COUNT; y++) {
				this.setCellState(x, y, GameOfLife.DEAD);
			}
		}
		notifyOurChange();
	}
   
	/**
	 * The helper notifier method
	 */
	private void notifyOurChange() {
		setChanged();
		notifyObservers(this);
	}
	/**
	 * The method to open files (minimal level).
    * @param filename The name of the file
    * @throws java.io.IOException If file not found
	 */
	public void fileOpen(String filename) throws java.io.IOException {
		FileInputStream fis = new FileInputStream(filename);
		ObjectInputStream iis = new ObjectInputStream(fis);
		try {
			world = (int[][]) iis.readObject();
			notifyOurChange();
		} catch (ClassNotFoundException e) {
		
		}
		iis.close();
	};
	/**
	 * The method to save the files(minimal level)
    * @param filename The name of the file to be save
    * @throws java.io.IOException If file not saved
	 */
	public void fileSave(String filename) throws java.io.IOException {
		FileOutputStream fos = new FileOutputStream(filename);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(world);
		oos.close();
	};
	/**
	 * The method to open the files
    * @param file The file to open
    * @throws java.io.IOException If file not found
	 */
	public void fileOpen(java.io.File file) throws java.io.IOException {
		FileInputStream fis = new FileInputStream(file);
		ObjectInputStream iis = new ObjectInputStream(fis);
		try {
			world = (int[][]) iis.readObject();
			notifyOurChange();
		} catch (ClassNotFoundException e) {
		
		}
		iis.close();
	}
	/**
	 * The method to save the .gol files 
    * @param file The file being saved
    * @throws java.io.IOException If file not saved
	 */
	public void fileSave(File file) throws java.io.IOException {
		System.out.println(file.getName());
		System.out.println(file.getParent());
		System.out.println(file.getAbsolutePath());
		if (!file.getName().endsWith(".gol")) {
		    file = new File(file.getParent() + File.separator + file.getName() + ".gol");
		}
		
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(world);
		oos.close();
	}
}
