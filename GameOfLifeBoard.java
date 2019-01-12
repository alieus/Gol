package csc143.gol;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

/**
 * A class that creats a graphical output component for the Game of Life It
 * extends JPanel.
 *
 * @author Alieu Sanneh
 * @version PA7: Game of Life, Serialization and Final Submission: Challenge
 */
public class GameOfLifeBoard extends JPanel implements Observer {
	
	// field for the GameOfLife object
	private MyGameOfLife game;

	// User controlled values, that are passed to controller
	private boolean running = false;
	private int gensPerMin = 120;
	
	// Config params for MRU file
	private String CONFIG_FILE = "mru_config.txt";
	private int MAX_MRU_ITEMS = 4;
	
	// to store the string paths of the files
	ArrayList<String> mruFiles = new ArrayList<>(); 
	
	// Jpanel menu bar reference,reloaded them in between 
	JMenuBar menuBar;
	JMenu menu;
	

	/**
	 * Constructor for the class.
	 * 
	 * @param gol The GameOfLife object
	 */
	public GameOfLifeBoard(MyGameOfLife gol) {
		game = gol;

		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(545, 610));

		// read the MRU file lists at the start
		readMruFile();
		
		// Draw the UI by checking the states of the cells
		addMouseListener(new MouseAdapter() {

			/**
			 * The method from MouseListener
			 * @param e The action that triggered this handler
			 */
			@Override
			public void mousePressed(MouseEvent e) {
				// divide by 25 to get the position of the cell.
				int row = e.getY() / 25;
				int col = e.getX() / 25;

				// checks if the click is within the grid
				if (row >= 1 && row <= 19 && col >= 1 && col <= 19) {
					// just an extra functionality I decided to add
					// System.out.println("Clicked " + row + ", " + col);

					// Toggle the state of the clicked cell
					if (game.getCellState(row, col) == GameOfLife.ALIVE) {
						game.setCellState(row, col, GameOfLife.DEAD);
					} else {
						game.setCellState(row, col, GameOfLife.ALIVE);
					}
				}
			}

			/**
			 * The method from MouseListener
			 * @param e The action that triggered this handler
			 */
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});


		JPanel toolbar = new JPanel();
		toolbar.add(createFirstToolBarRow(), BorderLayout.CENTER);
		toolbar.setPreferredSize(new Dimension(545, 70));

		// Create the menu bar.
		menuBar = new JMenuBar();
		menu = new JMenu("File");
		menuBar.add(menu);
		
		// this will populate menu items in File menu
		populateFileMenu();
		
		add(menuBar, BorderLayout.NORTH);

		// add the toolbar to the frame
		add(toolbar, BorderLayout.SOUTH);
	}
	
	/*
	 * This method creates the top row of the toolbar
	 * StartAnimation Button
	 * Generations Controls
	 */
	private JPanel createFirstToolBarRow() {

		// create the animationBtn button and add an actionlistener to it
		JButton animationBtn = new JButton("Start Animation");
		animationBtn.addActionListener(new ActionListener() {

			/**
			 * The method from ActionListener
			 * @param e The action that triggered this handler
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("Start Animation")) {
					animationBtn.setText("Stop Animation");
					running = true;
				} else {
					animationBtn.setText("Start Animation");
					running = false;
				}
			}
		});

		Font f = new Font("Helvetica", Font.PLAIN, 18);
		JLabel genLabel = new JLabel("Generations per minute:");
		JTextField generations = new JTextField("" + gensPerMin);
		generations.setFont(f);
		generations.addActionListener(new ActionListener() {
         /**
			 * The method from ActionListener
			 * @param e The action that triggered this handler
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				String value = generations.getText();
				
				try {
					int gen = Integer.parseInt(value);
					
					// if user sets an invalid value
					if (gen < 60 || gen > 500) {
						JOptionPane.showMessageDialog(GameOfLifeBoard.this, "input value is out of bound.", "Error",
								JOptionPane.ERROR_MESSAGE);
					} else {
						gensPerMin = gen;
					}

				} catch (Exception ex) {
					JOptionPane.showMessageDialog(GameOfLifeBoard.this, "input value is not an integer", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
				
				// update label
				generations.setText("" + gensPerMin);
			}
		});

		JButton incrGenerations = new JButton("+");
		incrGenerations.setFont(f);
		JButton decrGenerations = new JButton("-");
		decrGenerations.setFont(f);

		ActionListener genBtnChange = new ActionListener() {
			/**
			 * The method from ActionListener
			 * @param e The action that triggered this handler
			 */      
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean increment = e.getActionCommand().equals("+");

				String value = generations.getText();
				try {
					int gen = Integer.parseInt(value);

					if (increment) {
						gen += 20;
						if (gen > 500) {
							Toolkit.getDefaultToolkit().beep();
						} else {
							gensPerMin = gen;
						}
					} else {
						gen -= 20;
						if (gen < 60) {
							Toolkit.getDefaultToolkit().beep();
						} else {
							gensPerMin = gen;
						}
					}
				} catch (Exception ex) {
					// if user input is not a number
					JOptionPane.showMessageDialog(GameOfLifeBoard.this, "input value is not an integer", "Error",
							JOptionPane.ERROR_MESSAGE);
				}
				generations.setText("" + gensPerMin);
			}
		};
		incrGenerations.addActionListener(genBtnChange);
		decrGenerations.addActionListener(genBtnChange);
		
		// add components to the panel
		JPanel panel = new JPanel();
		panel.add(animationBtn);
		panel.add(genLabel);
		panel.add(generations);
		panel.add(incrGenerations);
		panel.add(decrGenerations);
		
		return panel;
	}

	/*
	 * This method adds the menu items into fileMenu
	 */
	void populateFileMenu() {
		JMenuItem menuItem;

		// a group of JMenuItems
		menuItem = new JMenuItem("Open ...");
		menuItem.addActionListener(new ActionListener() {
			/**
			 * The method from ActionListener
			 * @param e The action that triggered this handler
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				
				// add .gol files filter
				addFilter(fc);
				
				int returnVal = fc.showOpenDialog(GameOfLifeBoard.this);

				// if user has accepted to open a file
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					
					try {
						// try to open the file
						game.fileOpen(fc.getSelectedFile());
						
						// we need to add this recently opened file to the MRU list
						String path = fc.getSelectedFile().getAbsolutePath();
						
						// if it contains, remove it from list and add to the top
						if(mruFiles.contains(path)) {
							mruFiles.remove(path);
						}
						
						// move this new item to the top
						mruFiles.add(0, path);
						
						if(mruFiles.size() > MAX_MRU_ITEMS) {
							mruFiles.remove(mruFiles.size()-1);
						}
						
						// reload menu options.. so that MRU list is updated
						menu.removeAll();
						populateFileMenu();
						
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(GameOfLifeBoard.this, "GOLBoard: Error occurred in opening the selected file.", "Error",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					System.out.println("User cancelled file choose operation");
				}
			}
		});
		menu.add(menuItem);

		menuItem = new JMenuItem("Save ...");
		menuItem.addActionListener(new ActionListener() {
			/**
			 * The method from ActionListener
			 * @param e The action that triggered this handler
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				addFilter(fc);
				int returnVal = fc.showSaveDialog(GameOfLifeBoard.this);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					
					// if the file already exists, ask user if they wants to override
					if (file.exists()) {
						returnVal = JOptionPane.showConfirmDialog(GameOfLifeBoard.this, "Overwrite file: " + file.getName(),
								"Confirm file overwrite", JOptionPane.YES_NO_OPTION);
						
						// if user accepts to overwrite
						if (returnVal == JOptionPane.YES_OPTION) {
							try {
								game.fileSave(file);
							} catch (IOException e1) {
								JOptionPane.showMessageDialog(GameOfLifeBoard.this, "GOLBoard: Error occurred in saving the selected file.",
										"Error",
										JOptionPane.ERROR_MESSAGE);
							}
						}
						
					} else {
						try {
							game.fileSave(file);
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(GameOfLifeBoard.this, "GOLBoard: Error occurred in saving the selected file.",
									"Error",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				} else {
					System.out.println("User cancelled file choose operation");
				}
			}
		});
		menu.add(menuItem);

		menu.addSeparator();
		
		// if there are MRU file lists, add them into the menu
		if(!mruFiles.isEmpty()) {
			for(String mruFile: mruFiles) {
				menuItem = new JMenuItem(mruFile);
				menuItem.addActionListener(new ActionListener() {
  			      /**
			       * The method from ActionListener
			       * @param e The action that triggered this handler
			       */
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							game.fileOpen(mruFile);

							// add file to the top of MRU list
							mruFiles.remove(mruFile);
							mruFiles.add(0, mruFile);
							
							// reload menu options.. so that MRU list is updated
							menu.removeAll();
							populateFileMenu();
							
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(GameOfLifeBoard.this, mruFile + " doesn't exist.", "Error",
									JOptionPane.ERROR_MESSAGE);
							
							// remove the file as it doesn't exist
							mruFiles.remove(mruFile);
							
							// reload menu options.. so that MRU list is updated
							menu.removeAll();
							populateFileMenu();
						}
					}
				});
				menu.add(menuItem);
			}
			
			menu.addSeparator();
		}

		menuItem = new JMenuItem("Clear Board");
		menuItem.addActionListener(new ActionListener() {
			/**
			 * The method from ActionListener
			 * @param e The action that triggered this handler
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				game.clearBoard();
			}
		});
		menu.add(menuItem);

		menuItem = new JMenuItem("Exit Game of Life");
		menuItem.addActionListener(new ActionListener() {
			/**
			 * The method from ActionListener
			 * @param e The action that triggered this handler
			 */
			@Override
			public void actionPerformed(ActionEvent e) {
				GameOfLifeBoard.this.saveMRUList();
				System.exit(0);
			}
		});
		menu.add(menuItem);
	}

	/*
	 * This method adds the fileFilter to display only the 
	 * .gol extensions file when jfilechooser pops up the 
	 * window.
	 */
	private void addFilter(JFileChooser jc) {
		jc.setFileFilter(new FileFilter() {

			public String getDescription() {
				return "Game of Life files (.gol)";
			}

			/*
			 * tells what kind of files to accept
			 */
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				} else {
					String filename = f.getName().toLowerCase();
					return filename.endsWith(".gol");
				}
			}
		});
	}

	/**
	 * This is the method that gets called whenever observable object notifies the
	 * observers.
    *
    * @param o  The observable object.
    * @param arg An argument passed to the notifyObservers method
	 */
	@Override
	public void update(Observable o, Object arg) {
		this.repaint();
	}

	/**
	 * The necessary method. This method renders the component.
	 *
	 * @param g The Graphics object use to render
	 */
	@Override
	protected void paintComponent(Graphics g) {
		// paint the underlying component
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		g.setColor(Color.BLACK);
		g2.setStroke(new BasicStroke(1));

		// create cells of 25x25
		int width = 25;
		int height = 25;
		for (int x = 1; x <= 19; x++) {
			for (int y = 1; y <= 19; y++) {
				int state = game.getCellState(y, x);

				g.setColor(Color.BLACK);
				g.drawRect(x * width, y * height, width, height);

				// create 15-pixels diameter circles to rep.. LIVE cells
				if (state == MyGameOfLife.ALIVE) {
					g.fillOval(x * width + 2, y * height + 2, 21, 21);
				}
			}
		}

	}
	/**
	 * A getter method to get the state of running
	 * 
	 * @return The State of running
	 */
	public boolean isRunning() {
		return running;
	}
   
	/** 
	 * A getter method for the generations per minute
	 * 
	 * @return The generations per minute
	 */	
	public int getGensPerMin() {
		return gensPerMin;
	}
	
   /** 
	 * this method reads the file for MRU files configuration
	 */
	@SuppressWarnings("unchecked")
	void readMruFile() {
		try {
			FileInputStream fis = new FileInputStream(CONFIG_FILE);
			ObjectInputStream iis = new ObjectInputStream(fis);
			mruFiles = (ArrayList<String>) iis.readObject();
			iis.close();
		} catch (ClassNotFoundException e) {
      
		} catch (IOException e) {
		}
	}

	/** 
    * This method saves the files present in the arraylist of MRU list
    */
	public void saveMRUList() {
		try {
			FileOutputStream fos = new FileOutputStream(CONFIG_FILE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(mruFiles);
			oos.close();
		} catch (IOException e) {
		}
	}
}
