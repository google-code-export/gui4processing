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
 * This class is used to remember information about a particular applet (i.e. windows).
 * 
 * It remembers the original transformation matrix to simplify working with 3D renderers
 * and libraries such as PeasyCam.
 * 
 * @author Peter Lager
 *
 */
public class WindowInfo implements PConstants, GConstants {

	public PApplet app;
	public PMatrix orgMatrix;
	public LinkedList<FAbstractControl> windowControls = new LinkedList<FAbstractControl>();

	boolean haveRegisteredMethodsFor151 = false;

	/**
	 * Create an applet info object
	 * @param papplet
	 */
	public WindowInfo (PApplet papplet) {
		app = papplet;
		if(papplet.g.is3D())
			orgMatrix = papplet.getMatrix((PMatrix3D)null);
		else
			orgMatrix = papplet.getMatrix((PMatrix2D)null);
		registerMethodsFor151();
	}

	public void registerMethodsFor151(){
		app.registerDraw(this);
		app.registerMouseEvent(this);
		app.registerPre(this);
		app.registerKeyEvent(this);
		haveRegisteredMethodsFor151 = true;

	}

	public void unRegisterMethodsFor151(){
		if(haveRegisteredMethodsFor151){
			app.unregisterDraw(this);
			app.unregisterMouseEvent(this);
			app.unregisterPre(this);
			app.unregisterKeyEvent(this);	
			haveRegisteredMethodsFor151 = false;
		}
	}

	public void draw(){
		for(FAbstractControl control : windowControls){
			if( (control.registeredMethods & DRAW_METHOD) == DRAW_METHOD && control.parent == null)
				control.draw();
		}			
	}

	public void mouseEvent(MouseEvent event){
		for(FAbstractControl control : windowControls){
			if( (control.registeredMethods & MOUSE_METHOD) == MOUSE_METHOD)
				control.mouseEvent(event);
		}
	}

	public void keyEvent(KeyEvent event) {
		for(FAbstractControl control : windowControls){
			if( (control.registeredMethods & KEY_METHOD) == KEY_METHOD)
				control.keyEvent(event);
		}			
	}

	public void pre(){
		for(FAbstractControl control : windowControls){
			if( (control.registeredMethods & PRE_METHOD) == PRE_METHOD)
				control.pre();
		}
	}

	/**
	 * dispose of this applets GUI controls, normally used in preparation
	 * to disposing of a window.
	 */
	public void dispose(){
		unRegisterMethodsFor151();
		windowControls.clear();
	}

	public void addControl(FAbstractControl control){
		// Make sure we avoid duplicates
		windowControls.remove(control);
		windowControls.add(control);
		Collections.sort(windowControls, F4P.zorder);
	}

	public void removeControl(FAbstractControl control){
		windowControls.remove(control);
	}

	public void setGlobalColorScheme(int cs){
		for(FAbstractControl control : windowControls)
			control.setLocalColorScheme(cs);
	}


	public void setGlobalAlpha(int alpha){
		for(FAbstractControl control : windowControls)
			control.setAlpha(alpha);
	}




}