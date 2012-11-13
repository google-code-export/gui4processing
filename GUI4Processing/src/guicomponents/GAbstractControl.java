/*
  Part of the GUI for Processing library 
  	http://www.lagers.org.uk/g4p/index.html
	http://gui4processing.googlecode.com/svn/trunk/

  Copyright (c) 2008-12 Peter Lager

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

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PGraphicsJava2D;
import processing.core.PImage;

/**
 * Abstract base class for all GUI controls.
 * 
 * @author Peter Lager
 *
 */
public abstract class GAbstractControl implements PConstants, GConstants, GConstantsInternal {

	/*
	 * INTERNAL USE ONLY
	 * This holds a reference to the GComponent that currently has the
	 * focus.
	 * A component loses focus when another component takes focus with the
	 * takeFocus() method. The takeFocus method should use focusIsWith.loseFocus()
	 * before setting its value to the new component. 
	 */
	static GAbstractControl focusIsWith = null;

	static GAbstractControl controlToTakeFocus = null;

	/*
	 * INTERNAL USE ONLY
	 * Keeps track of the component the mouse is over so the mouse
	 * cursor can be changed if we wish.
	 */
	static GAbstractControl cursorIsOver;

	// Increment to be used if on a GPanel
	final static int Z_PANEL = 1024;

	// Components that don't release focus automatically
	// i.e. GTextField
	final static int Z_STICKY = 0;

	// Components that automatically releases focus when appropriate
	// e.g. GButton
	final static int Z_SLIPPY = 24;
	
	// Reference to the PApplet object that owns this control
	protected PApplet winApp;

	// Set to true when mouse is dragging : set false on button released
	protected boolean dragging = false;


	/** Link to the parent panel (if null then it is on main window) */
	protected GAbstractControl parent = null;

	/*
	 * A list of child GComponents added to this component
	 * Created and used by GPanel and GCombo classes
	 */
	protected LinkedList<GAbstractControl> children = null;

	protected int localColorScheme = G4P.globalColorScheme;
	protected int[] palette = null;
	protected Color[] jpalette = null;
	protected int alphaLevel = G4P.globalAlpha;

	/** Top left position of component in pixels (relative to parent or absolute if parent is null) 
	 * (changed form int data type in V3*/
	protected float x, y;
	/** Width and height of component in pixels for drawing background (changed form int data type in V3*/
	protected float width, height;
	/** Half sizes reduces programming complexity later */
	protected float halfWidth, halfHeight;
	/** The centre of the control */
	protected float cx, cy;
	/** The angle to control is rotated (radians) */
	protected float rotAngle;
	/** Introduced V3 to speed up AffineTransform operations */
	protected double[] temp = new double[2];

	// New to V3 components have an image buffer which is only redrawn if 
	// it has been invalidated
	protected PGraphicsJava2D buffer = null;
	protected PGraphics pad = null;
	protected boolean bufferInvalid = true;

	/** Whether to show background or not */
	protected boolean opaque = false;

	// The event type use READ ONLY
//	public GEvent eventType;

	// The cursor image when over a control
	// This should be set in the controls constructor
	protected int cursorOver = HAND;

	/*
	 * Position over control corrected for any transformation. <br>
	 * [0,0] is top left corner of the control.
	 * This is used to determine the mouse position over any 
	 * particular control or part of a control.
	 */
	protected float ox, oy;

	/* Used to when components overlap */
	protected int z = 0;

	/* Simple tag that can be used by the user */
	public String tag;

	/* Allows user to specify a number for this component */
	public int tagNo;

	/* Is the component visible or not */
	protected boolean visible = true;

	/* Is the component enabled to generate mouse and keyboard events */
	protected boolean enabled = true;

	/* 
	 * Is the component available for mouse and keyboard events.
	 * This is on;y used internally to prevent user input being
	 * processed during animation. new to V3
	 * Will preserve enabled and visible flags
	 */
	protected boolean available = true;

	/* The object to handle the event */
	protected Object eventHandlerObject = null;
	/* The method in eventHandlerObject to execute */
	protected Method eventHandlerMethod = null;
	/* the name of the method to handle the event */ 
	protected String eventHandlerMethodName;

	int registeredMethods = 0;

	/*
	 * Specify the PImage that contains the image{s} to be used for the button's state. <br>
	 * This image may be a composite of 1 to 3 images tiled horizontally. 
	 * @param img
	 * @param nbrImages in the range 1 - 3
	 */
	static PImage[] loadImages(PImage img, int nbrImages){
		if(img == null || nbrImages <= 0 || nbrImages > 3)
			return null;
		PImage[] bimage = new PImage[3];
		int iw = img.width / nbrImages;
		for(int i = 0; i < nbrImages;  i++){
			bimage[i] = new PImage(iw, img.height, ARGB);
			bimage[i].copy(img, 
					i * iw, 0, iw, img.height,
					0, 0, iw, img.height);
		}
		// If less than 3 images reuse last image in set
		for(int i = nbrImages; i < 3; i++)
			bimage[i] = bimage[nbrImages - 1];
		return bimage;
	}

	public static String getFocusName(){
		if(focusIsWith == null)
			return "null";
		else
			return focusIsWith.toString();
	}
	
	/*
	 * Base constructor for ALL control ctors. It will set the position and size of the
	 * control based on controlMode. <br>
	 * Since this is an abstract class it is not possible to use it directly
	 * 
	 */
	public GAbstractControl(PApplet theApplet, float p0, float p1, float p2, float p3) {
		// The first applet must be the sketchApplet
		if(G4P.sketchApplet == null)
			G4P.sketchApplet = theApplet;
		winApp = theApplet;
		GCScheme.makeColorSchemes(winApp);
		setPositionAndSize(p0, p1, p2, p3);
		rotAngle = 0;
		z = 0;
		palette = GCScheme.getColor(localColorScheme);
		jpalette = GCScheme.getJavaColor(localColorScheme);
		tag = this.getClass().getSimpleName();
	}

	/*
	 * Calculate all the variables that determine the position and size of the
	 * control. This depends on <pre>control_mode</pre>
	 * 
	 */
	private void setPositionAndSize(float n0, float n1, float n2, float n3){
		switch(G4P.control_mode){
		case PApplet.CORNER:	// (x,y,w,h)
			x = n0; y = n1; width = n2; height = n3;
			halfWidth = width/2; halfHeight = height/2;
			cx = x + halfWidth; cy = y + halfHeight;
			break;			
		case PApplet.CORNERS:	// (x0,y0,x1,y1)
			x = n0; y = n1; width = n2 - n0; height = n3 - n1;
			halfWidth = width/2; halfHeight = height/2;
			cx = x + halfWidth; cy = y + halfHeight;
			break;
		case PApplet.CENTER:	// (cx,cy,w,h)
			cx = n0; cy = n1; width = n2; height = n3;
			halfWidth = width/2; halfHeight = height/2;
			x = cx - halfWidth; y = cy - halfHeight;
			break;
		}
	}

	/*
	 * These are empty methods to enable polymorphism
	 */
	public void draw(){}
	public void mouseEvent(MouseEvent event){ }
	public void keyEvent(KeyEvent e) { }
	public void pre(){ }

	/**
	 * <b>This is for emergency use only!!!! </b><br>
	 * In this version of the library a visual controls is drawn to off-screen buffer
	 * and then drawn to the screen by copying the buffer. This means that the 
	 * computationally expense routines needed to draw the control (especially text 
	 * controls) are only done when a change has been noted. This means that single
	 * changes need not trigger a full redraw to buffer. <br>
	 * It does mean that an error in the library code could mena that the buffer is
	 * not being updated after changes. If this happens then in draw() call this method
	 * on the affected control, and report it as an issue <a href = 'http://code.google.com/p/gui4processing/issues/list'>
	 * here</a><br>
	 * Thanks
	 */
	public void forceBufferUpdate(){
		bufferInvalid = true;
	}
	
	protected HotSpot[] hotspots = null;
	protected int currSpot = -1;

	/**
	 * Stop when we are over a hotspot. <br>
	 * Hotspots should be listed in order of importance.
	 * 
	 * @param px
	 * @param py
	 * @return
	 */
	protected int whichHotSpot(float px, float py){
		if(hotspots == null) return -1;
		int hs = -1;
		for(int i = 0; i < hotspots.length; i++){
			if(hotspots[i].contains(px, py)){
				hs = hotspots[i].id;
				break;
			}
		}
		return hs;
	}

	protected int getCurrHotSpot(){
		return currSpot;
	}

	/**
	 * Set the local colour scheme for this control. Children are ignored.
	 * 
	 * @param cs the colour scheme to use
	 */
	public void setLocalColorScheme(int cs){
		cs = Math.abs(cs) % 16; // Force into valid range
		if(localColorScheme != cs || palette == null){
			localColorScheme = cs;
			palette = GCScheme.getColor(localColorScheme);
			jpalette = GCScheme.getJavaColor(localColorScheme);
			bufferInvalid = true;
		}
	}

	/**
	 * Set the local colour scheme for this control. Children are ignored.
	 * If required include the children and their children.
	 * 
	 * @param cs the colour scheme to use
	 * @param includeChildren if do do the same for all descendants 
	 */
	public void setLocalColorScheme(int cs, boolean includeChildren){
		cs = Math.abs(cs) % 16; // Force into valid range
		if(localColorScheme != cs || palette == null){
			localColorScheme = cs;
			palette = GCScheme.getColor(localColorScheme);
			jpalette = GCScheme.getJavaColor(localColorScheme);
			bufferInvalid = true;
			if(includeChildren && children != null){
				for(GAbstractControl c : children)
					c.setLocalColorScheme(cs, true);
			}
		}
	}

	/**
	 * Set the transparency of the component and make it unavailable to
	 * mouse and keyboard events if below the threshold. Child controls 
	 * are ignored?
	 * 
	 * @param alpha value in the range 0 (transparent) to 255 (opaque)
	 */
	public void setAlpha(int alpha){
		alpha = Math.abs(alpha) % 256;
		alphaLevel = alpha;
		available = (alphaLevel >= ALPHA_BLOCK);
	}
	
	/**
	 * Set the transparency of the component and make it unavailable to
	 * mouse and keyboard events if below the threshold. Child controls 
	 * are ignored? <br>
	 * If required include the children and their children.
	 * 
	 * @param alpha value in the range 0 (transparent) to 255 (opaque)
	 * @param includeChildren if do do the same for all descendants 
	 */
	public void setAlpha(int alpha, boolean includeChildren){
		alpha = Math.abs(alpha) % 256;
		alphaLevel = alpha;
		available = (alphaLevel >= ALPHA_BLOCK);
		if(includeChildren && children != null){
			for(GAbstractControl c : children)
				c.setAlpha(alpha, true);
		}		
	}
	
	/**
	 * Get the parent control. If null then this is a top-level component
	 * @return
	 */
	public GAbstractControl getParent() {
		return parent;
	}

	/**
	 * Get the PApplet that manages this component
	 * @return
	 */
	public PApplet getPApplet() {
		return winApp;
	}

	protected PGraphics getBuffer(){
		return buffer;
	}

	/**
	 * This method should be used sparingly since it is heavy on resources.
	 * 
	 * @return
	 */
	public PGraphics getSnapshot(){
		if(buffer != null){
			updateBuffer();
			PGraphicsJava2D snap = (PGraphicsJava2D) winApp.createGraphics(buffer.width, buffer.height, PApplet.JAVA2D);
			snap.beginDraw();
			snap.image(buffer,0,0);
			return snap;
		}
		return null;
	}

	/*
	 * Empty method at the moment make abstract
	 * in final version
	 */
	protected void updateBuffer() {}
	

	/**
	 * Attempt to create the default event handler for the component class. 
	 * The default event handler is a method that returns void and has a single
	 * parameter of the same type as the component class generating the
	 * event and a method name specific for that class. 
	 * 
	 * @param handlerObj the object to handle the event
	 * @param methodName the method to execute in the object handler class
	 * @param param_classes the parameter classes.
	 * @param param_names that names of the parameters (used for error messages only)
	 */
	@SuppressWarnings("rawtypes")
	protected void createEventHandler(Object handlerObj, String methodName, Class[] param_classes, String[] param_names){
		try{
			eventHandlerMethod = handlerObj.getClass().getMethod(methodName, param_classes );
			eventHandlerObject = handlerObj;
			eventHandlerMethodName = methodName;
		} catch (Exception e) {
			GMessenger.message(MISSING, new Object[] {this, methodName, param_classes, param_names});
			eventHandlerObject = null;
		}
	}

	/**
	 * Attempt to create the default event handler for the component class. 
	 * The default event handler is a method that returns void and has a single
	 * parameter of the same type as the component class generating the
	 * event and a method name specific for that class. 
	 * 
	 * @param obj the object to handle the event
	 * @param methodName the method to execute in the object handler class
	 */
	public void addEventHandler(Object obj, String methodName){
		try{
			eventHandlerObject = obj;
			eventHandlerMethodName = methodName;
			eventHandlerMethod = obj.getClass().getMethod(methodName, new Class[] {this.getClass(), GEvent.class } );
		} catch (Exception e) {
			GMessenger.message(NONEXISTANT, new Object[] {this, methodName, new Class[] { this.getClass(), GEvent.class } } );
			eventHandlerObject = null;
			eventHandlerMethodName = "";
		}
	}

	/**
	 * Attempt to fire an event for this component.
	 * 
	 * The method called must have a single parameter which is the object 
	 * firing the event.
	 * If the method to be called is to have different parameters then it should
	 * be overridden in the child class
	 * The method 
	 */
	protected void fireEvent(Object... objects){
		if(eventHandlerMethod != null){
			try {
				eventHandlerMethod.invoke(eventHandlerObject, objects);
			} catch (Exception e) {
				GMessenger.message(EXCP_IN_HANDLER,  
						new Object[] {eventHandlerObject, eventHandlerMethodName, e } );
			}
		}		
	}

	/**
	 * Set the rotation to apply when displaying this control.
	 * @param rot
	 */
	public void setRotation(float rot){
		this.rotAngle = rot;
	}

	/**
	 * Get the left position of the control
	 * @return the x
	 */
	public float getX() {
		return x;
	}

	/**
	 * Get the left position of the control
	 * @return the y
	 */
	public float getY() {
		return y;
	}

	/**
	 * Get the centre x position of the control
	 * @return the cx
	 */
	public float getCX() {
		return cx;
	}

	/**
	 * Get the centre y position of the control
	 * @return the cy
	 */
	public float getCY() {
		return cy;
	}

	/**
	 * @return the width
	 */
	public float getWidth() {
		return width;
	}

	/**
	 * @return the height
	 */
	public float getHeight() {
		return height;
	}

	/**
	 * @param visible the visibility to set
	 */
	public void setVisible(boolean visible) {
		// If we are making it invisible and it has focus give up the focus
		if(!visible && focusIsWith == this)
			loseFocus(null);
		this.visible = visible;
	}

	/**
	 * @return the component's visibility
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * If a control is made unavailable it will still be drawn but it not respond to user input.
	 * 
	 * @param avail
	 */
	public void setAvailable(boolean avail){
		available = avail;
		if(children != null){
			for(GAbstractControl c : children)
				c.setAvailable(avail);
		}
	}

	/**
	 * Set the availability for the children only.
	 * 
	 * @param avail
	 */
	public void setAvailableChildren(boolean avail){
		if(children != null){
			for(GAbstractControl c : children)
				c.setAvailable(avail);
		}
	}

	/**
	 * Is this control available?
	 * @return
	 */
	protected boolean isAvailable(){
		return available;
	}
	
	/**
	 * Determines whether to show the back colour or not.
	 * Only applies to some components
	 * @param opaque
	 */
	public void setOpaque(boolean opaque){
		// Ensure that we dont't go from true >> false otherwise 
		// it will validate an invalid buffer
		bufferInvalid |= (opaque != this.opaque);
		this.opaque = opaque;
	}

	/**
	 * Find out if the component is opaque
	 * @return true if the background is visible
	 */
	public boolean isOpaque(){
		return opaque;
	}

	public boolean isDragging(){
		return dragging;
	}
	
	/**
	 * Enable or disable the ability of the component to generate mouse events.<br>
	 * GTextField - it also controls key press events <br>
	 * GPanel - controls whether the panel can be moved/collapsed/expanded <br>
	 * @param enable true to enable else false
	 */
	public void setEnabled(boolean enable){
		enabled = enable;
		if(children != null){
			for(GAbstractControl c : children)
				c.setEnabled(enable);
		}
	}

	/**
	 * Is this component enabled
	 * @return true if the component is enabled
	 */
	public boolean isEnabled(){
		return enabled;
	}
	
	/**
	 * Give the focus to this component but only after allowing the 
	 * current component with focus to release it gracefully. <br>
	 * Always cancel the keyFocusIsWith irrespective of the component
	 * type. If the component needs to retain keyFocus then override this
	 * method in that class e.g. GCombo
	 */
	protected void takeFocus(){
		if(focusIsWith != null && focusIsWith != this)
			focusIsWith.loseFocus(this);
		focusIsWith = this;
	}

	/**
	 * For most components there is nothing to do when they loose focus.
	 * Override this method in classes that need to do something when
	 * they loose focus eg TextField
	 */
	protected void loseFocus(GAbstractControl grabber){
		if(cursorIsOver == this)
			cursorIsOver = null;
		focusIsWith = grabber;
	}

	/**
	 * Determines whether this component is to have focus or not
	 * @param focus
	 */
	public void setFocus(boolean focus){
		if(focus)
			takeFocus();
		else
			loseFocus(null);
	}

	/**
	 * Does this component have focus
	 * @return true if this component has focus else false
	 */
	public boolean hasFocus(){
		return (this == focusIsWith);
	}

	/**
	 * Get the Z order value for the object with focus.
	 */
	protected static int focusObjectZ(){
		return (focusIsWith == null) ? -1 : focusIsWith.z;
	}

//	public static GAbstractControl getFocusObject(){
//		return focusIsWith;
//	}
	
	/**
	 * This will set the rotation of the control to angle overwriting
	 * any previous rotation set. Then it calculates the centre position
	 * so that the original top left corner of the control will be the 
	 * position indicated by x,y with respect to the top left corner of
	 * parent
	 * 
	 * @param c the control to add.
	 * @param x the leftmost or centre position depending on controlMode
	 * @param y the topmost or centre position depending on controlMode
	 * @param angle the rotation angle (replaces any the angle specified in control)
	 */
	public void addControl(GAbstractControl c, float x, float y, float angle){
		c.rotAngle = angle; 
		// In child control reset the control so it centred about the origin
		AffineTransform aff = new AffineTransform();
		aff.setToRotation(angle);
		/*
		 * The following code should result in the x,y and cx,cy coordinates of
		 * the added control (c) added being measured relative to the centre of  
		 * this control.
		 */
		switch(G4P.control_mode){
		case PApplet.CORNER:
		case PApplet.CORNERS:
			// Rotate about top corner
			c.x = x; c.y = y;
			c.temp[0] = c.halfWidth;
			c.temp[1] = c.halfHeight;
			aff.transform(c.temp, 0, c.temp, 0, 1);
			c.cx = (float)c.temp[0] + x - halfWidth;
			c.cy = (float)c.temp[1] + y - halfHeight;
			c.x = c.cx - c.halfWidth;
			c.y = c.cy - c.halfHeight;
			break;
		case PApplet.CENTER:
			// Rotate about centre
			c.cx = x; c.cy = y;
			c.temp[0] = -c.halfWidth;
			c.temp[1] = -c.halfHeight;
			aff.transform(c.temp, 0, c.temp, 0, 1);
			c.x = c.cx + (float)c.temp[0] - halfWidth;
			c.y = c.cy - (float)c.temp[1] - halfHeight;
			c.cx -= halfWidth;
			c.cy -= halfHeight;
			break;
		}		
		c.rotAngle = angle;
		// Add to parent
		c.parent = this;
		c.setZ(z);
		// Parent will now be responsible for drawing
		c.registeredMethods &= (ALL_METHOD - DRAW_METHOD);
		if(children == null)
			children = new LinkedList<GAbstractControl>();
		children.addLast(c);
		Collections.sort(children, new Z_Order());
	}

	/**
	 * Add a control at the given position with zero rotation angle.
	 * 
	 * @param c the control to add.
	 * @param x the leftmost or centre position depending on controlMode
	 * @param y the topmost or centre position depending on controlMode
	 */
	public void addControl(GAbstractControl c, float x, float y){
		addControl(c, x, y, 0);
	}

	/**
	 * Add a control at the position and rotation specified in the control.
	 * 
	 * @param c the control to add
	 */
	public void addControl(GAbstractControl c){
		switch(G4P.control_mode){
		case PApplet.CORNER:
		case PApplet.CORNERS:
			addControl(c, c.x, c.y, c.rotAngle);
			break;
		case PApplet.CENTER:
			addControl(c, c.cx, c.cy, c.rotAngle);
			break;
		}		
	}
	
	/**
	 * Get the shape type when the cursor is over a control
	 * @return shape type
	 */
	public int getCursorOver() {
		return cursorOver;
	}

	/**
	 * Set the shape type to use when the cursor is over a control
	 * @param cursorOver the shape type to use
	 */
	public void setCursorOver(int cursorOver) {
		this.cursorOver = cursorOver;
	}

	/**
	 * Get an affine transformation that is the compound of all 
	 * transformations including parents
	 * @param aff
	 * @return
	 */
	protected AffineTransform getTransform(AffineTransform aff){
		if(parent != null)
			aff = parent.getTransform(aff);
		aff.translate(cx, cy);
		aff.rotate(rotAngle);
		return aff;
	}

	/**
	 * This method takes a position px, py and calulates the equivalent
	 * position [ox,oy] as if no transformations have taken place and
	 * the origin is the top-left corner of the control.
	 * @param px
	 * @param py
	 */
	protected void calcTransformedOrigin(float px, float py){
		AffineTransform aff = new AffineTransform();
		aff = getTransform(aff);
		temp[0] = px; temp[1] = py;
		try {
			aff.inverseTransform(temp, 0, temp, 0, 1);
			ox = (float) temp[0] + halfWidth;
			oy = (float) temp[1] + halfHeight;
		} catch (NoninvertibleTransformException e) {
		}
	}
	
	
	/**
	 * Recursive function to set the priority of a component. This
	 * is used to determine who gets focus when components overlap
	 * on the screen e.g. when a combobo expands it might cover a button. <br>
	 * It is used where components have childen e.g. GCombo and
	 * GPaneln
	 * It is used when a child component is added.
	 * @param component
	 * @param parentZ
	 */
	protected void setZ(int parentZ){
		z += parentZ;
		if(children != null){
			for(GAbstractControl c : children){
				c.setZ(parentZ);
			}
		}
	}

	/**
	 * If the control is permanently no longer required then call
	 * this method to remove it and free up resources. <br>
	 * The variable identifier used to create this control should 
	 * be set to null. <br>
	 * For example if you want to dispose of a button called 
	 * <pre>btnDoThis</pre> then to remove the button use the
	 * statements <br> <pre>
	 * btnDoThis.dispose(); <br>
	 * btnDoThis = null; <br></pre>
	 */
	public void dispose(){
		G4P.removeControl(this);
		buffer = null;
		parent = null;
		if(children != null)
			children.clear();
		palette = null;
		jpalette = null;
		eventHandlerObject = null;
		eventHandlerMethod = null;
		winApp = null;
		System.gc();
	}


	public String toString(){
		if(tag == null)
			return this.getClass().getSimpleName();
		else
			return tag;
	}
	
	/**
	 * Comparator used for controlling the order components are drawn
	 * @author Peter Lager
	 */
	public static class Z_Order implements Comparator<GAbstractControl> {

		public int compare(GAbstractControl c1, GAbstractControl c2) {
			if(c1.z != c2.z)
				return  new Integer(c1.z).compareTo( new Integer(c2.z));
			else
				return new Integer((int) -c1.y).compareTo(new Integer((int) -c2.y));
		}

	} // end of comparator class

}