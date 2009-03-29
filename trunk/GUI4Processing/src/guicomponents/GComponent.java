/*
  Part of the GUI for Processing library 
  	http://gui-for-processing.lagers.org.uk
	http://code.google.com/p/gui-for-processing/
	
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
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;

import processing.core.PApplet;
import processing.core.PFont;

/**
 * The base class for all GUI components
 * 
 * @author Peter Lager
 *
 */
@SuppressWarnings("unchecked")
public class GComponent implements Comparable  {

	/**
	 * This holds a reference to the GComponent that currently has the
	 * focus.
	 * It is made protected for performance reasons but should be treated
	 * as a READ ONLY attribute.
	 * A component looses focus when another component takes focus with the
	 * takeFocus() method. The takeFocus method should use focusIsWith.looseFocus()
	 * before setting its value to the new component 
	 */
	protected static GComponent focusIsWith; // READ ONLY
	
	/*
	 * Used to track mouse required by GButton, GCheckbox, GHorzSlider
	 * GVertSlider, GPanel classes
	 */
	protected int mdx = Integer.MAX_VALUE, mdy = Integer.MAX_VALUE;

	public static GCScheme globalColor;
	public GCScheme localColor;
	
	public static PFont globalFont;
	public PFont localFont;
	
	/*
	 * Padding around fonts
	 */
	protected final static int PADH = 4;
	protected final static int PADV = 2;
	
	/** This must be set by the constructor */
	protected static PApplet app;

	/** Link to the parent panel (if null then it is topmost panel) */
	protected GComponent parent = null;

	protected Method eventHandler = null;
	protected Object eventHandlerObject = null;
	
	/** Text value associated with component */
	protected String text = "";
	protected int textWidth;
	protected int textAlign = GTAlign.LEFT;
	
	/** Top left position of component in pixels (relative to parent or absolute if parent is null) */
	protected int x, y;

	/** Width and height of component in pixels for drawing background */
	protected int width, height;

	/** Minimum width and height of component in pixels based on child components */
	protected int minWidth = 20, minHeight = 20;
	
	/** Maximum width and height of component in pixels based on child components */
	protected int maxWidth = 200, maxHeight = 200;

	protected boolean visible = true;

	/** The border width for this component : default value is 0 */
	protected int border = 0;

	// Whether to show background or not 
	protected boolean opaque = true;
	
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
			globalColor = GCScheme.getColor(theApplet);
		localColor = new GCScheme(globalColor);
		if(globalFont == null)
			globalFont = GFont.getDefaultFont(theApplet);
		localFont = globalFont;
		this.x = x;
		this.y = y;
		G4P.addComponent(this);
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
	 * @param colors to be used for this component (local)
	 * @param font
	 */
	public GComponent(PApplet theApplet, int x, int y, GCScheme colors, PFont font ){
		app = theApplet;
		localColor = colors;
		localFont = font;
		if(globalColor == null)
			globalColor = new GCScheme(localColor);
		if(globalFont == null)
			globalFont = localFont;
		this.x = x;
		this.y = y;
		G4P.addComponent(this);
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
	 * @param colors
	 */
	public GComponent(PApplet theApplet, int x, int y, GCScheme colors){
		app = theApplet;
		localColor = colors;
		if(globalColor == null)
			globalColor = new GCScheme(localColor);
		if(globalFont == null)
			globalFont = GFont.getDefaultFont(theApplet);
		localFont = globalFont;
		this.x = x;
		this.y = y;
		G4P.addComponent(this);
	}

	/**
	 * Get the PApplet object
	 * @return
	 */
	public PApplet getPApplet(){
		return GComponent.app;
	}

	/*
	 * The following methods are related to handling focus.
	 * Most components can loose focus without affecting their state
	 * but TextComponents that support mouse text selection need to 
	 * clear this selection when they loose focus.
	 */
	
	/**
	 * Give the focus to this component but only after allowing the 
	 * current component with focus to release it gracefully.
	 */
	protected void takeFocus(){
		if(focusIsWith != null)
			focusIsWith.looseFocus();
		focusIsWith = this;
	}
	
	/**
	 * For most components there is nothing to do when they loose focus.
	 * Override this method in classes that need to do something when
	 * they loose focus e.g. TextField
	 */
	protected void looseFocus(){
		focusIsWith = null;
	}

	/**
	 * Determines whether this component is to have focus or not.
	 * @param focus
	 */
	public void setFocus(boolean focus){
		if(focus)
			takeFocus();
		else
			looseFocus();
	}
	
	/**
	 * Used by some components on the MOUSE_RELEASED event 
	 * @param x
	 * @param y
	 * @return
	 */
	protected boolean mouseHasMoved(int x, int y){
		return (mdx != x || mdy != y);
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
	 * Override in child classes
	 * @param event
	 */
	public void keyPressed(KeyEvent event){
	}
	
	/**
	 * Attempt to fire an event for this component
	 */
	protected void fireEvent(){
		if(eventHandler != null){
			try {
				eventHandler.invoke(eventHandlerObject, new Object[] { this });
			} catch (Exception e) {
				System.out.println("Disabling " + eventHandler.getName() + " due to an unknown error");
				eventHandler = null;
				eventHandlerObject = null;
			}
		}		
	}

	/**
	 * This is the default implementation for all GUI components except
	 * GPanel since we can use panels to group components.
	 * 
	 * @param component to be added (ignored)
	 * @return false always
	 */
	public boolean add(GComponent component){
		return false;
	}

	/**
	 * This method is used to register this object with PApplet so it can process
	 * events appropriate for that class.
	 * It should be called from all child class ctors.
	 * 
	 * @param draw
	 * @param mouse
	 * @param pre
	 * @param key
	 */
	protected void registerAutos_DMPK(boolean draw, boolean mouse, boolean pre, boolean key){
		// if auto draw has been disabled then do not register for draw()
		if(draw && G4P.isAutoDrawOn())
			app.registerDraw(this);
		if(mouse)
			app.registerMouseEvent(this);
		if(pre)
			app.registerPre(this);
		if(key)
			app.registerKeyEvent(this);
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
	 * @return the parent
	 */
	public GComponent getParent() {
		return parent;
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
		app.textFont(localFont, localFont.size);
		textWidth = (int) app.textWidth(text); 
	}

	/**
	 * @param text the text to set with alignment
	 */
	public void setText(String text, int align) {
		this.text = text;
		textAlign = align;
		app.textFont(localFont, localFont.size);
		textWidth = (int) app.textWidth(text); 
	}

	/**
	 * Sets the position of a component
	 * @param x
	 * @param y
	 */
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
		// If we are making it invisible and it has focus give up the focus
		if(!visible && focusIsWith == this)
			looseFocus();
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

	/**
	 * Determines wheher to show tha back color or not.
	 * Only applies to some components
	 * @param opaque
	 */
	public void setOpaque(boolean opaque){
		this.opaque = opaque;
	}

	public int compareTo(Object o) {
		return new Integer(this.hashCode()).compareTo(new Integer(o.hashCode()));
	}

} // end of class