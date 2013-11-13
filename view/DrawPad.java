package view;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import control.Main;

public class DrawPad extends JPanel {
	
	public enum MODE {
		DRAW, ERASE, SELECT
	}
	
	private MODE currentMode;
	private int oldX, oldY, currentX, currentY;
	Graphics2D graphics;
	Image image;
	public int time;
	public int previousRepaint;
	private ArrayList<LineView> lines;
	private LineView currentLine;
	private LineView eraseLine;
	private LineView selectionLine;
	private Polygon selectionPoly;
	private ArrayList<LineView> selectedLines;
	
	public DrawPad() {
		currentMode = MODE.DRAW;
		time = 0;
		previousRepaint = -1;
		lines = new ArrayList<LineView>();
		currentLine = null;
		eraseLine = null;
		selectionLine = null;
		selectionPoly = null;
		selectedLines = new ArrayList<LineView>();
		
		this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				oldX = e.getX();
				oldY = e.getY();
			}
			
			public void mouseReleased(MouseEvent e) {
				// Clear the current line for the next one
				currentLine = null;
				eraseLine = null;
				selectionLine = null;
				if (selectionPoly != null) {
					calculateSelection();
					selectionPoly = null;
				}
				forceRepaint();
			}
		});
		
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				currentX = e.getX();
				currentY = e.getY();
				if (currentMode == MODE.DRAW) {
					Main.stopTimer();
					if (currentLine == null) {
						currentLine = new LineView();
						Color c = Main.getColor();
						currentLine.setTime(time);
						currentLine.setColor(c);
						lines.add(currentLine);
						currentLine.addPoint(oldX, oldY, time);
						currentLine.myRepaint(graphics);
					}
				
					currentLine.addPoint(currentX, currentY, time);
				} else if (currentMode == MODE.ERASE) {
					Main.stopTimer();
					// Check if the line drawn by cursor intersects any existing line
					if (eraseLine == null) {
						eraseLine = new LineView();
						eraseLine.addPoint(oldX, oldY, time);
					}
					eraseLine.addPoint(currentX, currentY, time);
					deleteLine(oldX, oldY, currentX, currentY);
				} else {
					if (e.isShiftDown()) {
						Main.startTimer();
						animateLines(oldX, oldY, currentX, currentY);
					} else {
						Main.stopTimer();
						// Selection
						if (selectionPoly == null) {
							selectionPoly = new Polygon();
							selectionPoly.addPoint(oldX, oldY);
						}
						if (selectionLine == null) {
							selectionLine = new LineView();
							selectionLine.addPoint(oldX, oldY, time);
						}
						selectionPoly.addPoint(currentX, currentY);
						selectionLine.addPoint(currentX, currentY, time);
					}
				}
				oldX = currentX;
				oldY = currentY;
				forceRepaint();
			}
		});
	}
	
	private void deselectLines() {
		for (int i = 0; i < lines.size(); i++) {
			lines.get(i).setSelected(false);
		}
		selectedLines.clear();
		forceRepaint();
	}
	
	/**
	 * Animate the selected lines.
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	private void animateLines(int x1, int y1, int x2, int y2) {
		int dx = x2 - x1;
		int dy = y2 - y1;
		// Loop through selected lines, move them by dx, dy
		for (int i = 0; i < selectedLines.size(); i++) {
			if (selectedLines.get(i).timeExists(time)) {
				for (int j = 0; j < selectedLines.get(i).getArraySize(time); j++) {
					Point p = selectedLines.get(i).getPoint(time, j);
					if (p != null) {
						p.x = p.x + dx;
						p.y = p.y + dy;
					}
				}
			} else {
				for (int j = 0; j < selectedLines.get(i).getArraySize(time - 1); j++) {
					Point p = selectedLines.get(i).getPoint(time - 1, j);
					if (p != null) {
						selectedLines.get(i).addPoint((int)p.getX() + dx, (int)p.getY() + dy, time);
					}
				}
			}
		}
	}
	
	/**
	 * Returns true if one line model intersects a given line
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	private LineView getIntersectingLine(int x1, int y1, int x2, int y2) {
		for (int i = 0; i < lines.size(); i++) {
			Point p1 = lines.get(i).getPoint(time, 0);
			Point p2 = null;
			for (int j = 1; j < lines.get(i).getArraySize(time); j++) {
				p2 = lines.get(i).getPoint(time, j);
				int x3 = (int)p1.getX();
				int y3 = (int)p1.getY();
				int x4 = (int)p2.getX();
				int y4 = (int)p2.getY();
				if (Line2D.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4)) {
					return lines.get(i);
				}
				p1 = p2;
			}
		}
		return null;
	}
	
	private void deleteLine(int x1, int y1, int x2, int y2) {
		LineView line = getIntersectingLine(x1, y1, x2, y2);
		
		if (line != null) {
			line.deleteFrames(time);
		}
	}
	
	private void calculateSelection() {
		if (selectionPoly != null) {
			for (int i = 0; i < lines.size(); i++) {
				LineView line = lines.get(i);
				if (lineSelected(line)) {
					line.setSelected(true);
					selectedLines.add(line);
				}
			}
		}
	}
	
	private boolean lineSelected(LineView line) {
		for (int j = 0; j < line.getArraySize(time); j++) {
			if (!selectionPoly.contains(line.getPoint(time, j))) {
				return false;
			}
		}
		return true;
	}
	
	public void forceRepaint() {
		previousRepaint = -1;
		repaint();
	}
	
	private void drawEraseLine() {
		graphics.setStroke(new BasicStroke(2));
		graphics.setColor(Color.RED);
		eraseLine.myRepaint(graphics);
		graphics.setColor(Color.BLACK);
	}
	
	private void drawSelectionLine() {
		graphics.setColor(Color.GREEN);
		graphics.drawPolygon(selectionPoly);
		selectionLine.myRepaint(graphics);
		graphics.setColor(Color.BLACK);
	}
	
	public void paintComponent(Graphics g) {
		if (image == null) {
			image = createImage(getSize().width, getSize().height);
			graphics = (Graphics2D) image.getGraphics();
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			graphics.setStroke(new BasicStroke(2));
		}
		// Iterate through all the lines views to draw the line
		if (previousRepaint != time) {
			clear();
			for (int i = 0; i < lines.size(); i++) {
				lines.get(i).setTime(time);
				lines.get(i).myRepaint(graphics);
			}
			
			if (eraseLine != null) {
				drawEraseLine();
			}
			if (selectionPoly != null && selectionLine != null) {
				drawSelectionLine();
			}
			
			previousRepaint = time;
		}
		
		g.drawImage(image, 0, 0, null);
	}
	
	/**
	 * Fill the graphics with white
	 */
	public void clear() {
		graphics.setPaint(Color.white);
		graphics.fillRect(0, 0, getSize().width, getSize().height);
		graphics.setPaint(Color.black);
	}
	
	/**
	 * Clears all the lines at the current time point onwards.
	 */
	public void clearScreen() {
		// Clone current screen to one less time frame
		// Delete all points starting from this time, to the end of time.
		for (int i = 0; i < lines.size(); i++) {
			lines.get(i).deleteFrames(time);
			if (lines.get(i).isEmpty()) {
				lines.remove(i);
				i--;	//Size of lines is reduced and items are shifted over
			}
		}
		forceRepaint();
	}
	
	public void setMode(MODE mode) {
		currentMode = mode;
		deselectLines();
	}
	
	public MODE getMode() {
		return currentMode;
	}
	
	public void setTime(int time) {
		this.time = time;
		repaint();
	}
	
	/**
	 * Adds extra frames by shifting all line animations over
	 */
	public void addFrame() {
		for (int i = 0; i < lines.size(); i++) {
			lines.get(i).addFrame();
		}
	}
	
}
