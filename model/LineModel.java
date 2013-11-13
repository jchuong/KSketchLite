package model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * This is the model for Lines to be drawn on the screen.
 * It holds the points and the time it exists.
 * Points should be added as the mouse is dragged, and stop recording when
 * mouse button is released.
 */

public class LineModel {
	// Array of points and time
	TreeMap<Integer, ArrayList<Point>> points;
	
	public LineModel() {
		points = new TreeMap<Integer, ArrayList<Point>>();
	}
	
	/*
	 * Add a point to the line, at a certain time frame
	 */
	public void addPoint(int x, int y, int time) {
		Point newPoint = new Point(x, y);
		//Check if the time is already existing
		if (points.containsKey(time)) {
			points.get(time).add(newPoint);
		} else {
			points.put(time, new ArrayList<Point>());
			points.get(time).add(newPoint);
		}
	}
	
	/**
	 * Returns the next latest time that exists
	 */
	public int getLatestTime (int time) {
		//Iterate the map with descending keys, find the first number <= to given time
		for(Map.Entry<Integer, ArrayList<Point>> e : points.descendingMap().entrySet()) {
			if (e.getKey() <= time) {
				return e.getKey();
			}
		}
		return 0;
	}
	
	/**
	 * Get the size of the array for a certain time frame.
	 * If that size doesn't exist, return the closest previous time.
	 * If no points exist, then returns 0.
	 * @param time
	 */
	public int getArraySize(int time) {
		int myTime = time;
		
		if (points.isEmpty()) {
			return 0;
		}
		
		// Get the next newest time.
		if (!points.containsKey(time)) {
			myTime = getLatestTime(time);
		}
		
		if (points.get(myTime) == null) {
			return 0;
		}
		return points.get(myTime).size();
	}
	
	/**
	 * Returns the ith point at a given time
	 * @param time
	 */
	public Point getPoint(int time, int i) {
		int myTime = time;
		if (!points.containsKey(time)) {
			myTime = getLatestTime(time);
		}
		
		// Check if any point exists at an earlier point in time
		if (myTime <= 0 && points.get(myTime) == null) {
			return null;
		}
		
		// Found a dummy, return nothing.
		if (points.get(myTime) == null) {
			return null;
		}
		
		return points.get(myTime).get(i);
	}
	
	/**
	 * Wrapper for containsKey.  Returns
	 * true if the model has given time stamp
	 * @param time
	 * @return
	 */
	public boolean containsTime(int time) {
		return points.containsKey(time);
	}
	
	/**
	 * Deletes all lines from a given time onwards
	 */
	public void deleteFrame(int time) {
		// If there exists a frame right before this one, just delete
		// Otherwise we have to clone the frame given how the data structure works
		if (!points.containsKey(time - 1)) {
			points.put(time - 1, points.get(getLatestTime(time)));
		}
		
		Iterator<Map.Entry<Integer, ArrayList<Point>>> i = points.entrySet().iterator();
		while (i.hasNext()) {
			Map.Entry<Integer, ArrayList<Point>> entry = i.next();
			if (entry.getKey() >= time) {
				i.remove();
			}
		}
		points.put(time, null); // Dummy, such that later frames don't copy the earlier frames
	}
	
	public boolean isEmpty() {
		return points.isEmpty();
	}
	
	public void addFrame(int time) {
		//Iterate through the largest time, increase the frame time by one
		for(Map.Entry<Integer, ArrayList<Point>> e : points.descendingMap().entrySet()) {
			if (e.getKey() >= time) {
				points.put(e.getKey() + 1, e.getValue());
				points.remove(e.getKey());
			}
		}
	}
}
