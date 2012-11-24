package g4p_controls;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PMatrix;
import processing.core.PMatrix3D;

/**
 * DO NOT ATTEMPT TO USE THIS CLASS <br>
 * 
 * Although this class and many of its methods are declared public this is to make 
 * them available through Refelection and means that should only be used inside the
 * library code. <br> 
 * 
 * This class is used to remember information about a particular applet (i.e. window)
 * and is responsible handling events passes to it from Processing. <br>
 * 
 * It remembers the original transformation matrix to simplify working with 3D renderers
 * and libraries such as PeasyCam.
 * 
 * @author Peter Lager
 *
 */
public class GWindowInfo implements PConstants, GConstants, GConstantsInternal {

	public PApplet app;
	public boolean app_g_3d;
	public PMatrix orgMatrix;
	public LinkedList<GAbstractControl> windowControls = new LinkedList<GAbstractControl>();
	// These next two lists are for controls that are to be added or remove since these
	// actions must be performed outside the draw cyle to avoid concurrent modification
	// exceptions when changing windowControls
	public LinkedList<GAbstractControl> toRemove = new LinkedList<GAbstractControl>();
	public LinkedList<GAbstractControl> toAdd = new LinkedList<GAbstractControl>();

	boolean haveRegisteredMethodsFor151 = false;

	/**
	 * Create an applet info object
	 * @param papplet
	 */
	public GWindowInfo (PApplet papplet) {
		app = papplet;
		app_g_3d = app.g.is3D();
		if(app.g.is3D())
			orgMatrix = papplet.getMatrix((PMatrix3D)null);
//		else
//			orgMatrix = papplet.getMatrix((PMatrix2D)null);
		registerMethodsForWindow();
	}

	void releaseControls() {
		windowControls.clear();
	}
	

	
	public void draw(){
		app.pushMatrix();
		if(app_g_3d) {
			app.hint(PConstants.DISABLE_DEPTH_TEST);
			// Load the identity matrix.
			app.resetMatrix();
			// Apply the original Processing transformation matrix.
			app.applyMatrix(orgMatrix);
		}
		for(GAbstractControl control : windowControls){
			if( (control.registeredMethods & DRAW_METHOD) == DRAW_METHOD && control.parent == null)
				control.draw();
		}		
		if(app_g_3d)
			app.hint(PConstants.ENABLE_DEPTH_TEST);
		app.popMatrix();
	}


	public void mouseEvent(MouseEvent event){
		for(GAbstractControl control : windowControls){
			if( (control.registeredMethods & MOUSE_METHOD) == MOUSE_METHOD)
				control.mouseEvent(event);
		}
	}

	public void keyEvent(KeyEvent event) {
		for(GAbstractControl control : windowControls){
			if( (control.registeredMethods & KEY_METHOD) == KEY_METHOD)
				control.keyEvent(event);
		}			
	}

	public void pre(){
		if(GAbstractControl.controlToTakeFocus != null && GAbstractControl.controlToTakeFocus.getPApplet() == app){
			GAbstractControl.controlToTakeFocus.setFocus(true);
			GAbstractControl.controlToTakeFocus = null;
		}
		for(GAbstractControl control : windowControls){
			if( (control.registeredMethods & PRE_METHOD) == PRE_METHOD)
				control.pre();
		}
	}

	public void post(){
		if(G4P.cursorChangeEnabled){
			if(GAbstractControl.cursorIsOver != null && GAbstractControl.cursorIsOver.getPApplet() == app)
				app.cursor(GAbstractControl.cursorIsOver.cursorOver);			
			else 
				app.cursor(G4P.mouseOff);
		}
		for(GAbstractControl control : windowControls){
			if( (control.registeredMethods & POST_METHOD) == POST_METHOD)
				control.post();
		}
		synchronized (this) {
		// Dispose of ant unwanted controls
		if(!toRemove.isEmpty())
			for(GAbstractControl control : toRemove){
				// Clear control resources
				control.buffer = null;
				if(control.parent != null){
					control.parent.children.remove(control);
					control.parent = null;
				}
				if(control.children != null)
					control.children.clear();
				control.palette = null;
				control.jpalette = null;
				control.eventHandlerObject = null;
				control.eventHandlerMethod = null;
				control.winApp = null;
				windowControls.remove(control);
				System.gc();			
			}
		if(!toAdd.isEmpty()){
			for(GAbstractControl control : toAdd)
				windowControls.add(control);
			toAdd.clear();
			Collections.sort(windowControls, G4P.zorder);
		}
		}
	}

	/**
	 * Dispose of this WIndow. <br>
	 * applets GUI controls, normally used in preparation
	 * to disposing of a window.
	 */
	void dispose(){
		unRegisterMethodsForWindow();
		windowControls.clear();
	}


	/**
	 * If a control is to be added to this window then add the control
	 * to the toAdd list. The control will actually be added during the 
	 * post() method
	 * @param control
	 */
	synchronized void addControl(GAbstractControl control){
		// Make sure we avoid duplicates
		if(!windowControls.contains(control) && !toAdd.contains(control))
			toAdd.add(control);
	}

	/**
	 * If a control is to be added to this window then add the control
	 * to the toAdd list. The control will actually be added during the 
	 * post() method
	 * @param control
	 */
	synchronized void removeControl(GAbstractControl control){
		// Make sure we avoid duplicates
		if(!windowControls.contains(control) && !toAdd.contains(control))
			toRemove.add(control);
	}

	void setColorScheme(int cs){
		for(GAbstractControl control : windowControls)
			control.setLocalColorScheme(cs);
	}

	void setAlpha(int alpha){
		for(GAbstractControl control : windowControls)
			control.setAlpha(alpha);
	}

	/*
	 * This registers this applet for V1.5.1 and V2.0
	 * Eventually these need to be modified to new style for
	 * Processing V2.0
	 */
	public void registerMethodsForWindow(){
		app.registerDraw(this);
		app.registerMouseEvent(this);
		app.registerPre(this);
		app.registerKeyEvent(this);
		app.registerPost(this);
//		app.registerMethod("pre",this);
//		app.registerMethod("post",this);
		haveRegisteredMethodsFor151 = true;
	}

	/*
	 * This registers this applet for V1.5.1 and V2.0
	 * Eventually these need to be modified to new style for
	 * Processing V2.0
	 */
	public void unRegisterMethodsForWindow(){
		if(haveRegisteredMethodsFor151){
			app.unregisterDraw(this);
			app.unregisterMouseEvent(this);
			app.unregisterPre(this);
			app.unregisterKeyEvent(this);	
			app.unregisterPost(this);
//			app.unregisterMethod("pre", this);
//			app.unregisterMethod("post", this);
			haveRegisteredMethodsFor151 = false;
		}
	}
		
}