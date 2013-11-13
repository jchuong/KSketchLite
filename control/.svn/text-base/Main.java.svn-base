package control;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import view.DrawPad;

public class Main {
    static DrawPad pad;
	static JToolBar menu;
	static JToolBar bottomBar;
	static JButton drawButton;
	static JButton eraseButton;
	static JButton selectButton;
	static JButton clearButton;
	static JButton colorButton;
	static JColorChooser colorChooser;
	
	static JSlider timeSlider;
	static JButton playButton;
	static JButton pauseButton;
	static JButton addFrameButton;
	
	static Timer animationTimer;
	
	public static int MAX_TIME = 900;
	
	static ActionListener tick = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			int currentTick = timeSlider.getValue();
			if (currentTick == MAX_TIME) {
				animationTimer.stop();
			} else {
				currentTick++;
				timeSlider.setValue(currentTick);
				pad.setTime(currentTick);
			}
		}
	};
	
	static void increaseFrames() {
		timeSlider.setMaximum(timeSlider.getMaximum() + 1);
	}
	
	/**
	 * Initializes the tool bar, adds buttons and listeners 
	 */
	public static void initializeToolbar() {
		menu = new JToolBar();
		drawButton = new JButton("Draw");
		eraseButton = new JButton("Erase");
		selectButton = new JButton("Select");
		clearButton = new JButton("Clear");
		colorButton = new JButton("Colour");
		colorChooser = new JColorChooser(Color.BLACK);
		menu.add(drawButton);
		menu.add(eraseButton);
		menu.add(selectButton);
		menu.add(clearButton);
		menu.add(colorButton);
		
		colorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				colorChooser.setColor(JColorChooser.showDialog(new JPanel(), "Colour Picker", Color.BLACK));
			}
		});
		
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				animationTimer.stop();
				pad.clearScreen();
			}
		});
		
		drawButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				animationTimer.stop();
				pad.setMode(DrawPad.MODE.DRAW);
			}
		});
		
		selectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				animationTimer.stop();
				pad.setMode(DrawPad.MODE.SELECT);
			}
		});
		
		eraseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				animationTimer.stop();
				pad.setMode(DrawPad.MODE.ERASE);
			}
		});
		
		menu.setFloatable(false);
	}
	
	public static Color getColor() {
		return colorChooser.getColor();
	}
	
	public static void initializeBottomBar() {
		bottomBar = new JToolBar();
		bottomBar.setFloatable(false);
		
		playButton = new JButton("Play");
		pauseButton = new JButton("Pause");
		addFrameButton = new JButton("Add Frame");
		
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				animationTimer.start();
			}
		});
		
		pauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				animationTimer.stop();
			}
		});
		
		addFrameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				animationTimer.stop();
				pad.setTime(timeSlider.getValue());
				pad.addFrame();
				Main.increaseFrames();
			}
		});
		
        timeSlider = new JSlider(0, MAX_TIME, 0);
        timeSlider.setMajorTickSpacing(10);
        timeSlider.setPaintTicks(true);
        timeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (timeSlider.getValueIsAdjusting()) {
					int time = timeSlider.getValue();
					pad.setTime(time);
				}
			}
		});
        
        bottomBar.add(playButton);
        bottomBar.add(pauseButton);
        bottomBar.add(timeSlider);
        bottomBar.add(addFrameButton);
	}
	
	private static void initializeTimer() {
		animationTimer = new Timer(50, tick);
		animationTimer.stop();
	}
	
	public static void startTimer() {
		if (!animationTimer.isRunning()) {
			animationTimer.start();
		}
	}
	
	public static void stopTimer() {
		animationTimer.stop();
	}
	
	public static void main(String[] args) {
		// Set up window
		JFrame frame = new JFrame("Assignment 3");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
		
        pad = new DrawPad();
        initializeTimer();
        initializeToolbar();
        initializeBottomBar();
        
        Container container = frame.getContentPane();
		container.setLayout(new BorderLayout());
		container.add(pad, BorderLayout.CENTER);
		container.add(menu, BorderLayout.NORTH);
		container.add(bottomBar, BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);			// Set frame centered 
        frame.setVisible(true);
	}
}