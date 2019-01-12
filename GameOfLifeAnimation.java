package csc143.gol;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;
/**
 * A class that creates animations for GOL
 *
 * @author Alieu Sanneh
 * @version PA7: Game of Life, Serialization and Final Submission: Challenge
 */
public class GameOfLifeAnimation implements Runnable {

	// model for the game
	private MyGameOfLife model;
	
	// view for the game
	private GameOfLifeBoard view;
	
	// number of generations that user wants
	private int gensPerMin;

	// this will be changed based on board state
	private boolean animationRunning;
	
	// this is used to share state of game between background
	// thread. When this is false, we will stop
	volatile static boolean stop = false;
   
   /**
    * The constructor for the class
    * It initializes the model and view instances
    */	
	public GameOfLifeAnimation() {
		this.model  = new MyGameOfLife();
		this.view = new GameOfLifeBoard(model);
		
		// add the observer in view
		model.addObserver(view);
		
		// default values
		gensPerMin = 120;
		animationRunning = false;
	}
   
	/** 
	 * The method from runnable
    * It describes the actions the thread will perform.
	 */
	@Override
	public void run() {
		
		while(!stop) {
			
			// get the animation states and speed from view
			animationRunning = view.isRunning();
			gensPerMin = view.getGensPerMin();

			// calculate how much time 
			int interval = 1000 * 60 / gensPerMin;
			
			if(interval != 0) {
				try {
					Thread.sleep(interval);
				} catch (InterruptedException e) {
					
				}
			}
			
			// call next generation if animation is running
			if(animationRunning) {
				model.nextGeneration();
			}
		}
	}

	

	/**
	 * The application method that runs the program
	 *
	 * @param args The command-line arguments
	 */
	public static void main(String args[]) {
		JFrame frame = new JFrame("Game Of Life");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocation(25, 25);
		
		GameOfLifeAnimation ani = new GameOfLifeAnimation();
		JPanel spacer = new JPanel();
		spacer.add(ani.view);
		frame.add(spacer);
		
		// read command line arguments.
		System.out.println(Arrays.toString(args));
		if(args.length > 0) {
			try {
				ani.model.fileOpen(args[0]);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		// start the thread
		Thread t = new Thread(ani);
		t.start();
		
		// set start size
		frame.pack();
		frame.setSize(600, 670);
		frame.setVisible(true);
		
		// to cleanup the thread.
		frame.addWindowListener(new WindowAdapter() {
      
      	/**
	        * The method from WindowListerner
	        * Invoked when the user attempts to close the 
           * window from the window's system menu
           *
	        * @param e The action the triggered the event
	        */
		    @Override
		    public void windowClosing(WindowEvent e) {
		    	GameOfLifeAnimation.stop = true;
		    	
		    	// when closing the app, save the list
		    	ani.view.saveMRUList();
		    }
		});
	}
}
