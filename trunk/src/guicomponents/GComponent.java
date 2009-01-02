package guicomponents;

import java.awt.Point;
import java.awt.event.MouseEvent;

import processing.core.PApplet;

public class GComponent implements GConstants {

	/**
	 * If this is null then no GUI component has the mouse focus, 
	 * otherwise it references the object that has the focus.
	 * It must be set to null on mouse released event.
	 */
	public static GComponent mouseFocusOn;

	/**
	 * If this is null then no GUI component has the text focus, 
	 * otherwise it references the object that has the focus.
	 * It must be set to null on mouse released event.
	 */
	public static GComponent keyFocusOn;

	/** 
	 * The GUI scheme (color and font to be used globally) by the GUI 
	 * unless overridden by a local value.
	 */
	protected static GScheme globalGScheme;

	/** 
	 * The GUI scheme (color and font to be used) by this
	 * component. 
	 */
	protected GScheme localGScheme;

	/** This must be set by the constructor */
	protected PApplet app;

	/** Link to the parent panel (if null then it is topmost panel) */
	protected GComponent parent = null;

	/** Text value associated with component */
	protected String text = "";

	/** Top left position of component in pixels (relative to parent or absolute if parent is null) */
	protected int x, y;

	/** Width and height of component in pixels for drawing background */
	protected int width, height;

	/** Minimum width and height of component in pixels based on child components */
	protected int minWidth = 20, minHeight = 20;
	
	/** Maximum width and height of component in pixels based on child components */
	protected int maxWidth = 200, maxHeight = 200;

	protected boolean visible = true;

	/**
	 * Prevent uninitialised instantiation
	 */
	protected GComponent() { }

	/**
	 * This constructor should be called by all constructors  
	 * of any child class e.g. GPanel, GLabel etc.
	 * 
	 * Only create the GScheme the first time it is called.
	 * 
	 * @param theApplet
	 * @param x
	 * @param y
	 */
	public GComponent(PApplet theApplet, int x, int y){
		app = theApplet;
		if(globalGScheme == null)
			globalGScheme = GScheme.getScheme(theApplet, 0, 0);
		localGScheme = globalGScheme;
		maxWidth = (int) 0.95f * app.getWidth();
		maxHeight = (int) 0.95f * app.getHeight();
		this.x = x;
		this.y = y;
	}

	/**
	 * This constructor should be called by all constructors  
	 * of any child class e.g. GPanel, GLabel etc.
	 * 
	 * Create a local  color/font scheme and use for global if that 
	 * has not been defined yet.
	 * 
	 * @param theApplet
	 * @param x
	 * @param y
	 * @param colorScheme
	 * @param fontScheme
	 */
	public GComponent(PApplet theApplet, int x, int y, int colorScheme, int fontScheme ){
		app = theApplet;
		localGScheme = GScheme.getScheme(theApplet, colorScheme, fontScheme);
		if(globalGScheme == null)
			globalGScheme = localGScheme;
		maxWidth = (int) 0.95f * app.getWidth();
		maxHeight = (int) 0.95f * app.getHeight();
		this.x = x;
		this.y = y;
	}

	/**
	 * This constructor should be called by all constructors  
	 * of any child class e.g. GPanel, GLabel etc.
	 * 
	 * Create a local color/font scheme and use for global if that 
	 * has not been defined yet.
	 * 
	 * @param theApplet
	 * @param x
	 * @param y
	 * @param colorScheme
	 */
	public GComponent(PApplet theApplet, int x, int y, int colorScheme){
		app = theApplet;
		localGScheme = GScheme.getScheme(theApplet, colorScheme, 0);
		if(globalGScheme == null)
			globalGScheme = localGScheme;
		maxWidth = (int) 0.95f * app.getWidth();
		maxHeight = (int) 0.95f * app.getHeight();
		this.x = x;
		this.y = y;
	}

	/**
	 * Override in child classes
	 */
	public void pre(){
	}

	/**
	 * Override in child classes
	 */
	public void draw(){
	}

	/**
	 * Override in child classes.
	 * Every object will execute this method when an event
	 * is to be processed.
	 */
	public void mouseEvent(MouseEvent event){
	}

	/**
	 * This is the default implementation for all GUI components except
	 * GPanel since we can use panels to group data.
	 * 
	 * @param component to be added (ignored)
	 * @return false always
	 */
	public boolean addComponent(GComponent component){
		return false;
	}

	/**
	 * Determines whether the position ax, ay is over this component.
	 * This is the default implementation and assumes the component
	 * is a rectangle where x & y is the top-left corner and the size
	 * is defined by width and height.
	 * Override this method where necessary in child classes e.g. GPanel 
	 * 
	 * @param ax mouse x position
	 * @param ay mouse y position
	 * @return true if mouse is over the component else false
	 */
	public boolean isOver(int ax, int ay){
		Point p = new Point(0,0);
		calcAbsPosition(p);
		if(ax >= p.x && ax <= p.x + width && ay >= p.y && ay <= p.y + height)
			return true;
		else 
			return false;
	}

	/**
	 * Determines whether the position ax, ay is over this rectangle.
	 * where x & y is the top-left corner and the size is defined by 
	 * width and height.
	 * The x,y values are adjusted for any parent component's position.
	 * Override this method where necessary in child classes e.g. GPanel 
	 * 
	 * @param ax mouse x position
	 * @param ay mouse y position
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return true if mouse is over the rectangle else false
	 */
	public boolean isOver(int ax, int ay, int x, int y, int width, int height){
		Point p = new Point(x,y);
		calcAbsPosition(p);
		if(ax >= p.x && ax <= p.x + width && ay >= p.y && ay <= p.y + height)
			return true;
		else 
			return false;
	}

	/** 
	 * This method will calculate the absolute top left position of this 
	 * component taking into account any ancestors. 
	 * 
	 * @param d
	 */
	public void calcAbsPosition(Point d){
		if(parent != null)
			parent.calcAbsPosition(d);
		d.x += x;
		d.y += y;
	}

	/**
	 * Constrain a value to stay within a range.
	 * 
	 * @param v the value to constrain
	 * @param min range minimum value
	 * @param max range maximum value
	 * @return the constrained value
	 */
	public int constrain(int v, int min, int max){
		if(v < min) return min;
		if(v > max) return max;
		return v;
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

	/**
	 * @return the component's visibility
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * @param visible the visibility to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

} // end of class
