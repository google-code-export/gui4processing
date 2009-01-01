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

	/** Only GPanels can have children - set to true in GPanel ctors */
	protected boolean childrenPremitted = false;

	/** Text value associated with component */
	protected String text = "";

	/** Top left position of component in pixels (relative to parent or absolute if parent is null) */
	protected int x, y;

	/** Width and height of component in pixels for drawing background */
	protected int width, height;

	protected boolean visible;

	/**
	 * Prevent uninitialised instantiation
	 */
	protected GComponent() { }

	/**
	 * This constructor should be called by the ctor of any child class e.g.
	 * GPanel, GLabel etc.
	 * 
	 * Only create the GScheme the first time it is called.
	 * 
	 * @param theApplet
	 * @param colorScheme
	 * @param fontScheme
	 */
	public GComponent(PApplet theApplet, int x, int y){
		app = theApplet;
		this.x = x;
		this.y = y;
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
	 * 
	 * @param component
	 * @return
	 */
	public boolean addComponent(GComponent component){
		return false;
	}

	/**
	 * Determines whether the position ax, ay is over this component
	 * @return
	 */
	public boolean isOver(int ax, int ay){
		System.out.println("GComponent isMouseOver " + this);
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
		d.x += x;
		d.y += y;
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
