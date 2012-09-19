package guicomponents;

import guicomponents.GComponent.Z_Order;

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

public class FAbstractControl implements IControl, PConstants, GConstants{

	/**
	 * INTERNAL USE ONLY
	 * This holds a reference to the GComponent that currently has the
	 * focus.
	 * A component loses focus when another component takes focus with the
	 * takeFocus() method. The takeFocus method should use focusIsWith.loseFocus()
	 * before setting its value to the new component 
	 */
	protected static FAbstractControl focusIsWith = null;

	protected static IText keyFocusIsWith = null;

	protected PApplet winApp;

	/**
	 * INTERNAL USE ONLY
	 * Keeps track of the component the mouse is over so the mouse
	 * cursor can be changed if we wish.
	 */
	protected static FAbstractControl cursorIsOver;

	// Determines how position and size parameters are interpreted when
	// a control is created
	// Introduced V3.0
	protected static int control_mode = PApplet.CORNER;

	// Increment to be used if on a GPanel
	protected final static int Z_PANEL = 1024;

	// Components that don't release focus automatically
	// i.e. GTextField
	protected final static int Z_STICKY = 0;

	// Components that automatically releases focus when appropriate
	// e.g. GButton
	protected final static int Z_SLIPPY = 24;

	// Set to true when mouse is dragging : set false on button released
	protected boolean dragging = false;


	/** Link to the parent panel (if null then it is on main window) */
	protected FAbstractControl parent = null;

	/**
	 * A list of child GComponents added to this component
	 * Created and used by GPanel and GCombo classes
	 */
	protected LinkedList<FAbstractControl> children = null;
	protected int childLimit = 0;

	protected int localColorScheme = F4P.globalColorScheme;

	protected int[] palette = null;
	protected Color[] jpalette = null;


	/** Top left position of component in pixels (relative to parent or absolute if parent is null) 
	 * (changed form int data type in V3*/
	protected float x, y;
	/** Width and height of component in pixels for drawing background (changed form int data type in V3*/
	protected float width, height;
	/** Half sizes reduces programming complexity later */
	protected float halfWidth, halfHeight;
	/** The cenre of the control */
	protected float cx, cy;
	/** The angle to control is rotated (radians) */
	protected float rotAngle;
	/** Introduced V3 to speed up AffineTransform operations */
	protected double[] temp = new double[2];

	// New to V3 components have an image buffer which is only redrawn if 
	// it has been invalidated
	protected PGraphicsJava2D buffer = null;
	protected boolean bufferInvalid = true;

	//	/** Text value associated with component */
	//	protected String text = "";
	//	// The styled version of text
	//	protected StyledString stext = null;

	/** Whether to show background or not */
	protected boolean opaque = true;

	// The event type use READ ONLY
	public int eventType = 0;


	/** 
	 * Position over control corrected for any transformation. <br>
	 * [0,0] is top left corner
	 */
	protected float ox, oy;

	/** Used to when components overlap */
	public int z = 0;

	/** Simple tag that can be used by the user */
	public String tag;

	/** Allows user to specify a number for this component */
	public int tagNo;

	/** Is the component visible or not */
	protected boolean visible = true;

	/** Is the component enabled to generate mouse and keyboard events */
	protected boolean enabled = true;

	/** 
	 * Is the component available for mouse and keyboard events.
	 * This is on;y used internally to prevent user input being
	 * processed during animation. new to V3
	 * Will preserve enabled and visible flags
	 */
	protected boolean available = true;

	/** The object to handle the event */
	protected Object eventHandlerObject = null;
	/** The method in eventHandlerObject to execute */
	protected Method eventHandlerMethod = null;
	/** the name of the method to handle the event */ 
	protected String eventHandlerMethodName;

	int registeredMethods = 0;

	/**
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

	public FAbstractControl(PApplet theApplet, float p0, float p1, float p2, float p3) {
		winApp = theApplet;
		FCScheme.makeColorSchemes(winApp);
		setPositionAndSize(p0, p1, p2, p3);
		rotAngle = 0;
		z = 0;
		palette = FCScheme.getColor(localColorScheme);
		jpalette = FCScheme.getJavaColor(localColorScheme);
	}

	/**
	 * Calculate all the variables that determine the position and size of the
	 * control. This depends on <pre>control_mode</pre>
	 * 
	 */
	protected void setPositionAndSize(float n0, float n1, float n2, float n3){
		switch(control_mode){
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
			x = cx - halfWidth; y = cy + halfHeight;
			break;
		}
	}

	// Enable polymorphism for 1.5.1
	public void draw(){ }
	public void mouseEvent(MouseEvent event){ }
	public void keyEvent(KeyEvent e) { }
	public void pre(){ }

	protected HotSpot[] hotspots = null;
	protected int currSpot = -1;

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

	// Change local scheme v3
	public void setLocalColorScheme(int cs){
		cs = Math.abs(cs) % 16; // Force into valid range
		if(localColorScheme != cs || palette == null){
			localColorScheme = cs;
			palette = FCScheme.getColor(localColorScheme);
			jpalette = FCScheme.getJavaColor(localColorScheme);
			bufferInvalid = true;
		}
	}

	/**
	 * Change the way position and size parameters are interpreted when a control is created. 
	 * There are 3 modes. <br><pre>
	 * PApplet.CORNER	 (x, y, w, h)
	 * PApplet.CORNERS	 (x0, y0, x1, y1)
	 * PApplet.CENTER	 (cx, cy, w, h) </pre><br>
	 * 
	 * @param mode illegal values are ignored leaving the mode unchanged
	 */
	public static void ctrlMode(int mode){
		switch(mode){
		case PApplet.CORNER:	// (x, y, w, h)
		case PApplet.CORNERS:	// (x0, y0, x1, y1)
		case PApplet.CENTER:	// (cx, cy, w, h)
			control_mode = mode;
		}
	}

	/**
	 * Get the control creation mode @see ctrlMode(int mode)
	 * @return
	 */
	public static int getCtrlMode(){
		return control_mode;
	}

	public IControl getParent() {
		return parent;
	}

	public PApplet getPApplet() {
		return winApp;
	}

	protected PGraphicsJava2D getBuffer(){
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
	 * @param parameters the parameter classes.
	 */
	@SuppressWarnings("rawtypes")
	protected void createEventHandler(Object handlerObj, String methodName, Class[] parameters){
		try{
			eventHandlerMethod = handlerObj.getClass().getMethod(methodName, parameters );
			eventHandlerObject = handlerObj;
			eventHandlerMethodName = methodName;
		} catch (Exception e) {
			GMessenger.message(MISSING, this, new Object[] {methodName, parameters});
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
			eventHandlerMethod = obj.getClass().getMethod(methodName, new Class[] {this.getClass() } );
		} catch (Exception e) {
			GMessenger.message(NONEXISTANT, this, new Object[] {methodName, new Class[] { this.getClass() } } );
			eventHandlerObject = null;
			eventHandlerMethodName = "";
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
	 * @param parameters the parameter classes.
	 */
	@SuppressWarnings("rawtypes")
	public void addEventHandler(Object obj, String methodName, Class[] parameters){
		if(parameters == null)
			parameters = new Class[0];
		try{
			eventHandlerObject = obj;
			eventHandlerMethodName = methodName;
			eventHandlerMethod = obj.getClass().getMethod(methodName, parameters );
		} catch (Exception e) {
			GMessenger.message(NONEXISTANT, eventHandlerObject, new Object[] {methodName, parameters } );
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
	protected void fireEvent(){
		if(eventHandlerMethod != null){
			try {
				eventHandlerMethod.invoke(eventHandlerObject, new Object[] { this });
			} catch (Exception e) {
				GMessenger.message(EXCP_IN_HANDLER, eventHandlerObject, 
						new Object[] {eventHandlerMethodName, e } );
			}
		}		
	}

	// This is for visualisation
	public void setRotation(float rot){
		this.rotAngle = rot;
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
	 * @param y
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * @return the x
	 */
	public float getX() {
		return x;
	}

	/**
	 * @return the y
	 */
	public float getY() {
		return y;
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


	protected void setAvailable(boolean avail){
		available = avail;
		if(children != null){
			for(FAbstractControl c : children)
				c.setAvailable(avail);
		}
	}

	protected boolean isAvailable(){
		return available;
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
	 * @return true if the background is visible
	 */
	public boolean getOpaque(){
		return opaque;
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
			for(FAbstractControl c : children)
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
	
	/*
	 * The following methods are related to handling focus.
	 * Most components can loose focus without affecting their state
	 * but TextComponents that support mouse text selection need to 
	 * clear this selection when they loose focus. Also components
	 * like GCombo that comprise other G4P components need additional
	 * work
	 */

	/**
	 * This method stub is overridden in GPanel
	 */
	protected void bringToFront(){
		if(parent != null)
			parent.bringToFront();
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
		keyFocusIsWith = null;
		this.bringToFront();
	}

	/**
	 * For most components there is nothing to do when they loose focus.
	 * Override this method in classes that need to do something when
	 * they loose focus eg TextField
	 */
	protected void loseFocus(FAbstractControl grabber){
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

	protected boolean hasParentFocus(){
		if(this == focusIsWith)
			return true;
		else if(parent != null)
			parent.hasParentFocus();
		return false;
	}

	protected boolean hasChildFocus(){
		if(this == focusIsWith)
			return true;
		else if(children != null){
			boolean hf = false;
			for(FAbstractControl comp : children){
				hf |= comp.hasChildFocus();
			}
			return hf;
		}
		return false;
	}

	protected static int focusObjectZ(){
		return (focusIsWith == null) ? -1 : focusIsWith.z;
	}

	/**
	 * This will set the rotation of the control to angle overwriting
	 * any previous rotation set. Then it calculates the centre position
	 * so that the original top left corner of the control will be the 
	 * position indicated by x,y with respect to the top left corner of
	 * parent
	 * 
	 * @param c
	 * @param x
	 * @param y
	 * @param angle
	 */
	public void addCompoundControl(FAbstractControl c, float x, float y, float angle){
		if(children == null){
			System.out.println("This is not a valid container");
			return;
		}
		else if(children.size() >= childLimit){
			System.out.println("This container is full");
			return;
		}
		if(angle == 0)
			angle = c.rotAngle;
		// In child control reset the control so it centred about the origin
		AffineTransform aff = new AffineTransform();
		aff.setToRotation(angle);

		switch(control_mode){
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
			// @TODO must test this

			break;
		}		
		c.rotAngle = angle;
		// Add to parent
		c.parent = this;
		c.setZ(z);
		// Parent will now be responsible for drawing
		System.out.print("Reg for " + c.registeredMethods);
		c.registeredMethods &= (ALL_METHOD - DRAW_METHOD);
		System.out.println("   now for " + c.registeredMethods);
		if(children == null)
			children = new LinkedList<FAbstractControl>();
		children.addLast(c);
		Collections.sort(children, new Z_Order());
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
	 * NOT REQD
	 * Remove this method from the release version performs the same task as 
	 * calcTransformedOrigin(px,py) but also tests to see if it is over
	 * the control.
	 * 
	 * @param px
	 * @param py
	 * @return
	 */
	public boolean contains(float px, float py){
		AffineTransform aff = new AffineTransform();
		aff = getTransform(aff);
		temp[0] = px; temp[1] = py;
		try {
			aff.inverseTransform(temp, 0, temp, 0, 1);
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
			return false;
		}
		ox = (float) temp[0] + halfWidth;
		oy = (float) temp[1] + halfHeight;
		boolean over = (ox >= 0 && ox <= width && oy >= 0 && oy <= height);
		return over;
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
			for(FAbstractControl c : children){
				c.setZ(parentZ);
			}
		}
	}

	/*
	 * The following methods are used for z-ordering
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		return new Integer(this.hashCode()).compareTo(new Integer(o.hashCode()));
	}

	/**
	 * Comparator used for controlling the order components are drawn
	 * @author Peter Lager
	 */
	public static class Z_Order implements Comparator<FAbstractControl> {

		public int compare(FAbstractControl c1, FAbstractControl c2) {
			if(c1.z != c2.z)
				return  new Integer(c1.z).compareTo( new Integer(c2.z));
			else
				return new Integer((int) -c1.y).compareTo(new Integer((int) -c2.y));
		}

	} // end of comparator class

	public String toString(){
		return this.getClass().getSimpleName();
	}
}
