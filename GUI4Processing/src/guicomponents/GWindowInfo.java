package guicomponents;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PMatrix;
import processing.core.PMatrix2D;
import processing.core.PMatrix3D;

/**
 * This class is used to remember information about a particular applet (i.e. window).
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

	boolean haveRegisteredMethodsFor151 = false;

	/**
	 * Create an applet info object
	 * @param papplet
	 */
	public GWindowInfo (PApplet papplet) {
		app = papplet;
		app_g_3d = app.g.is3D();
		if(app_g_3d)
			orgMatrix = papplet.getMatrix((PMatrix3D)null);
		else
			orgMatrix = papplet.getMatrix((PMatrix2D)null);
		registerMethodsForWindow();
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

	void releaseControls() {
		windowControls.clear();
	}
	
	public void draw(){
		app.pushMatrix();
		if(app_g_3d)
			app.hint(PConstants.DISABLE_DEPTH_TEST);
		// Load the identity matrix.
		app.resetMatrix();
		// Apply the original Processing transformation matrix.
		app.applyMatrix(orgMatrix);
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
			if(GAbstractControl.cursorIsOver != null && GAbstractControl.cursorIsOver.getPApplet() == app){
				app.cursor(GAbstractControl.cursorIsOver.cursorOver);			
			}
			else {
				app.cursor(G4P.mouseOff);
			}
		}
		for(GAbstractControl control : windowControls){
			if( (control.registeredMethods & POST_METHOD) == POST_METHOD)
				control.pre();
		}
	}

	/**
	 * dispose of this applets GUI controls, normally used in preparation
	 * to disposing of a window.
	 */
	void dispose(){
		unRegisterMethodsForWindow();
		windowControls.clear();
	}

	void addControl(GAbstractControl control){
		// Make sure we avoid duplicates
		windowControls.remove(control);
		windowControls.add(control);
		Collections.sort(windowControls, G4P.zorder);
	}

	void removeControl(GAbstractControl control){
		windowControls.remove(control);
	}

	void setColorScheme(int cs){
		for(GAbstractControl control : windowControls)
			control.setLocalColorScheme(cs);
	}

	void setAlpha(int alpha){
		for(GAbstractControl control : windowControls)
			control.setAlpha(alpha);
	}

}