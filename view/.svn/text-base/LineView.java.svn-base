package view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JComponent;
import model.LineModel;

public class LineView extends JComponent {
	LineModel model;
	private int currentTime;
	private boolean selected;
	private Color color;

	public LineView() {
		model = new LineModel();
		selected = false;
	}
	
	/**
	 * Draws the line with the data points from the model
	 * 
	 * @param g Graphics object to be painted on
	 */
	public void myRepaint(Graphics g) {
		Point begin = model.getPoint(currentTime, 0);
		if (begin == null) {
			return;
		}
		
		for (int i = 1; i < model.getArraySize(currentTime); ++i) {
			Point end = model.getPoint(currentTime, i);
			if (selected) {
				g.setColor(Color.BLUE);
			} else {
				g.setColor(color);
			}
			g.drawLine((int)begin.getX(), (int)begin.getY(), (int)end.getX(), (int)end.getY());
			begin = end;
		}
		g.setColor(Color.BLACK);
	}
	
	public void setTime(int time) {
		this.currentTime = time;
	}
	
	public void addPoint(int x, int y, int time) {
		model.addPoint(x, y, time);
	}
	
	/**
	 * Returns a point at a given time
	 * @param time
	 * @return
	 */
	public Point getPoint(int time, int i) {
		return model.getPoint(time, i);
	}
	
	/**
	 * Get the size of the array of points at a
	 * specific time
	 * @param time
	 * @return size
	 */
	public int getArraySize(int time) {
		return model.getArraySize(time);
	}
	
	public int getLatestTime(int time) {
		return model.getLatestTime(time);
	}
	
	public void deleteFrames(int time) {
		// Remove all frames after this time, inclusive
		model.deleteFrame(time);
	}
	
	public boolean isEmpty() {
		return model.isEmpty();
	}
	
	public void addFrame() {
		model.addFrame(currentTime);
	}
	
	public void setSelected(boolean isSelected) {
		selected = isSelected;
	}
	
	public boolean getSelected() {
		return selected;
	}
	
	public boolean timeExists(int time) {
		return model.containsTime(time);
	}
	
	public void setColor(Color c) {
		color = c;
	}
}
