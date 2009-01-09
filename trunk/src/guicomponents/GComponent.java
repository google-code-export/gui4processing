/*
  Part of the GUI for Processing library 
  	http://gui4processing.lagers.org.uk
	http://code.google.com/p/gui4processing/
	
  Copyright (c) 2008-09 Peter Lager

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
 */

package guicomponents;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;

import processing.core.PApplet;

public class GComponent implements GUI {

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

	public static GColor globalColor;
	public static GColor localColor;
	
	public static GFont globalFont;
	public static GFont localFont;
	
	public final static int PADH = 4;
	public final static int PADV = 1;
	
	/** 
	 * The GUI scheme (color and font to be used) by this
	 * component. 
	 */
	//protected GScheme localGScheme;

	/** This must be set by the constructor */
	protected PApplet app;

	/** Link to the parent panel (if null then it is topmost panel) */
	protected GComponent parent = null;

	protected Method eventHandler = null;
	protected Object eventHandlerObject = null;
	
	/** Unique ID for every component */
	protected String id;
	
	/** Text value associated with component */
	private String text = "";
	protected int textWidth;
	protected int textAlign = GUI.LEFT;
	
	/** Top left position of component in pixels (relative to parent or absolute if parent is null) */
	protected int x, y;

	/** Width and height of component in pixels for drawing background */
	protected int width, height;

	/** Minimum width and height of component in pixels based on child components */
	protected int minWidth = 20, minHeight = 20;
	
	/** Maximum width and height of component in pixels based on child components */
	protected int maxWidth = 200, maxHeight = 200;

	protected boolean visible = true;

	protected int border = 0;

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
		if(globalColor == null)
			globalColor = GColor.getColor(theApplet);
		localColor = globalColor;
		if(globalFont == null)
			globalFont = GFont.getFont(theApplet);
		localFont = globalFont;
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
	 * @param cScheme
	 * @param fScheme
	 */
	public GComponent(PApplet theApplet, int x, int y, GColor cScheme, GFont fScheme ){
		app = theApplet;
		localColor = cScheme;
		localFont = fScheme;
		if(globalColor == null)
			globalColor = localColor;
		if(globalFont == null)
			globalFont = localFont;
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
	 * @param cScheme
	 */
	public GComponent(PApplet theApplet, int x, int y, GColor cScheme){
		app = theApplet;
		localColor = cScheme;
		if(globalColor == null)
			globalColor = localColor;
		this.x = x;
		this.y = y;
	}

	/**
	 * 
	 * @return the unique ID for this component
	 */
	public String getID(){
		return id;
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
	 * Attempt to fire an event for this component
	 */
	protected void fireEvent(){
		if(eventHandler != null){
			try {
				eventHandler.invoke(eventHandlerObject, new Object[] { this });
			} catch (Exception e) {
				System.out.println("Disabling " + eventHandler.getName() + " due to an error");
				eventHandler = null;
				eventHandlerObject = null;
			}
		}		
	}

	/**
	 * This is the default implementation for all GUI components except
	 * GPanel since we can use panels to group data.
	 * 
	 * @param component to be added (ignored)
	 * @return false always
	 */
	public boolean add(GComponent component){
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
	 * @param text use this function to set the text so that the
	 * text length is calculated
	 */
	public void setText(String text) {
		this.text = text;
		app.textFont(localFont.gpFont, localFont.gpFontSize);
		textWidth = (int) app.textWidth(text); 
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text, int align) {
		this.text = text;
		textAlign = align;
		app.textFont(localFont.gpFont, localFont.gpFontSize);
		textWidth = (int) app.textWidth(text); 
	}

	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
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

	/**
	 * The user can add a border by specifying it's thickness
	 * a value of 0 means no border (this is the default)
	 * @param border width in pixels
	 */
	public void setBorder(int border){
		this.border = border;
	}

} // end of class
