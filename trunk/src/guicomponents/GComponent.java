package guicomponents;

import java.awt.Dimension;
import java.awt.Point;

import processing.core.PApplet;

public class GComponent {

	/**
	 * If this is null then no GUI component has the focus, 
	 * otherwise it references the object that has the focus.
	 * It must be set to null on mouse released event.
	 */
	protected static GComponent gcWithFocus;
	
	/** The GUI scheme (color and font to be used) by the GUI */
	protected static GScheme gscheme;
	
	/** This must be set by the constructor */
	protected PApplet app;
	
	/** Link to the parent panel (if null then it is topmost panel) */
	protected GComponent parent = null;

	/** Only GPanels can have children - set to true in GPanel ctors */
	protected boolean childrenPremitted = false;
	
	/** Text value associated with component */
	protected String text = "";
	
	/** Top left position of component in pixels (relative to parent or absolute if parent is null) */
	protected int x, y;
	
	/** Width and height of component in pixels for drawing background */
	protected int width, height;
	

	/**
	 * This method should be called by the ctor of any child class e.g.
	 * GPanel, GLabel etc.
	 * 
	 * Only create the GScheme the first time it is called.
	 * 
	 * @param theApplet
	 * @param colorScheme
	 * @param fontScheme
	 */
	public GComponent(PApplet theApplet, int colorScheme, int fontScheme, int x, int y){
		app = theApplet;
		if(gscheme == null)
			gscheme = GScheme.getScheme(app, colorScheme, fontScheme);
		this.x = x;
		this.y = y;
	}
	
	/**
	 * This method should be called when this component is added to 
	 * a GPanel
	 */
	private void drawWithParent(){
		app.unregisterDraw(this);
		app.unregisterMouseEvent(this);		
	}
	
	/**
	 * Determines whether the position ax, ay is over this component
	 * @return
	 */
	public boolean isOver(int ax, int ay){
		return false;
	}
	
	/** 
	 * calculate the absolute top left position of this component 
	 * 
	 * @param d
	 */
	public void calcAbsPosition(Point d){
		if(parent != null)
			parent.calcAbsPosition(d);
		else {
			d.x += x;
			d.y += y;
		}
	}
	
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @param width the width to set
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	
} // end of class
