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
import java.util.HashSet;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PFont;

/**
 * The base class for all GUI components
 * 
 * @author Peter Lager
 *
 */
@SuppressWarnings("unchecked")
public class GComponent implements PConstants, Comparable  {

	/**
	 * INTERNAL USE ONLY
	 * This holds a reference to the GComponent that currently has the
	 * focus.
	 * A component looses focus when another component takes focus with the
	 * takeFocus() method. The takeFocus method should use focusIsWith.looseFocus()
	 * before setting its value to the new component 
	 */
	protected static GComponent focusIsWith; // READ ONLY

	/**
	 * INTERNAL USE ONLY
	 * Keeps track of the component the mouse is over so the mouse
	 * cursor can be changed if we wish.
	 */
	protected static GComponent cursorIsOver;
	
	/*
	 * INTERNAL USE ONLY
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

	/**
	 * A list of child GComponents added to this component
	 * Created and used by GPanel and GCombo classes
	 */
	protected HashSet<GComponent> children;

	/** The object to handle the event */
	protected Object eventHandlerObject = null;
	/** The method in eventHandlerObject to execute */
	protected Method eventHandler = null;

	/** Text value associated with component */
	protected String text = "";
	protected int textWidth;
	protected int textAlign = GAlign.LEFT;
	protected int alignX = 0;

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
		G4P.app = theApplet;
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
	 * clear this selection when they loose focus. Also components
	 * like GCombo that comprise other G4P components need additional
	 * work
	 */

	/**
	 * Give the focus to this component but only after allowing the 
	 * current component with focus to release it gracefully
	 */
	protected void takeFocus(){
		if(focusIsWith != null && focusIsWith != this)
			focusIsWith.looseFocus(this);
		focusIsWith = this;
	}

	/**
	 * For most components there is nothing to do when they loose focus.
	 * Override this method in classes that need to do something when
	 * they loose focus eg TextField
	 */
	protected void looseFocus(GComponent grabber){
		if(cursorIsOver == this)
			cursorIsOver = null;
		focusIsWith = null;
	}

	/**
	 * Determines whether this component is to have focus or not
	 * @param focus
	 */
	public void setFocus(boolean focus){
		if(focus)
			takeFocus();
		else
			looseFocus(null);
	}

	/**
	 * Does this component have focus
	 * 
	 * @return
	 */
	public boolean hasFocus(){
		return (this == focusIsWith);
	}

	/**
	 * Get a the object (if any) that currently has focus
	 * @return 
	 */
	public static GComponent getFocusObject(){
		return focusIsWith;
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
	 * Add a GUI component to this GComponent at the position specified by
	 * component being added. If transparency has been applied to the 
	 * GComponent then the same level will be applied to the component.
	 * Unregister the component for drawing this is managed by the 
	 * GComponent draw method to preserve z-ordering
	 * 
	 * @return always true
	 */
	public boolean add(GComponent component){
		if(component == null || children.contains(component)){
			if(G4P.messages)
				System.out.println("Either the component doesn't exist or has already been added to this panel");
			return false;
		} else {
			component.parent = this;
			children.add(component);
			app.unregisterDraw(component);
			if(localColor.getAlpha() < 255)
				component.setAlpha(localColor.getAlpha());
			return true;
		}
	}

	/**
	 * Remove a GUI component from this component
	 * 
	 * @param component
	 */
	public void remove(GComponent component){
		children.remove(component);
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
	 * Sets the local color scheme
	 * @param schemeNo
	 */
	public void setColorScheme(int schemeNo){
		localColor = GCScheme.getColor(app, schemeNo);
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
		calcAlignX();
	}

	/**
	 * @param text the text to set with alignment
	 */
	public void setText(String text, int align) {
		this.text = text;
		textAlign = align;
		app.textFont(localFont, localFont.size);
		textWidth = (int) app.textWidth(text);
		calcAlignX();
	}

	/**
	 * Set the text alignment inside the box
	 * @param align
	 */
	public void setTextAlign(int align){
		textAlign = align;
		calcAlignX();
	}

	/**
	 * Override in child classes
	 * @param fontname
	 * @param fontsize
	 */
	public void setFont(String fontname, int fontsize){
	}
	
	/**
	 * Calculate text X position based on text alignment
	 */
	protected void calcAlignX(){
		switch(textAlign){
		case GAlign.LEFT:
			alignX = border + PADH;
			break;
		case GAlign.RIGHT:
			alignX = width - textWidth - border - PADH;
			break;
		case GAlign.CENTER:
			alignX = (width - textWidth)/2;
			break;
		}
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
	 * Sets the x position of a component
	 * @param x
 	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Sets the x position of a component
	 * @param x
 	 */
	public void setY(int y) {
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
			looseFocus(null);
		this.visible = visible;
	}

	/**
	 * The user can add a border by specifying it's thickness
	 * a value of 0 means no border (this is the default)
	 * @param border width in pixels
	 */
	public void setBorder(int border){
		this.border = border;
		calcAlignX();
	}

	/**
	 * Get the border width
	 * @return
	 */
	public int getBorder(){
		return border;
	}

	/**
	 * Determines wheher to show tha back color or not.
	 * Only applies to some components
	 * @param opaque
	 */
	public void setOpaque(boolean opaque){
		this.opaque = opaque;
	}

	/**
	 * Find out if the component is opaque
	 * @return
	 */
	public boolean getOpaque(){
		return opaque;
	}

	/**
	 * Controls the transparency of this component
	 * 0 = fully transparent
	 * 255 = fully opaque
	 * 
	 * @param alpha
	 */
	public void setAlpha(int alpha){
		localColor.setAlpha(alpha);
	}

	/**
	 * How transparent / opaque is this component
	 * @return 0 (transparent) 255 (opaque)
	 */
	public int getAlpha(){
		return localColor.getAlpha();
	}


	public int compareTo(Object o) {
		return new Integer(this.hashCode()).compareTo(new Integer(o.hashCode()));
	}


} // end of class
