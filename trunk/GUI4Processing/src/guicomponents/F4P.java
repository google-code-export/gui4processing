package guicomponents;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PMatrix;
import processing.core.PMatrix2D;
import processing.core.PMatrix3D;

public class F4P {

	
	public static int globalColorScheme = FCScheme.CYAN_SCHEME;
	public static Font globalFont = new Font("Dialog", Font.PLAIN, 11);

	static LinkedList<WindowInfo> windows = new LinkedList<WindowInfo>();
	static FAbstractControl.Z_Order zorder = new FAbstractControl.Z_Order();
	
	static void addControl(FAbstractControl control){
		PApplet app = control.getPApplet();
		
	}
	
	private class WindowInfo implements PConstants, GConstants {

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
				if( (control.registeredMethods & DRAW_METHOD) != 0)
					control.draw();
			}			
		}
		
		public void mouseEvent(MouseEvent event){
			for(FAbstractControl control : windowControls){
				if( (control.registeredMethods & MOUSE_METHOD) != 0)
					control.mouseEvent(event);
			}
			
		}

		public void keyEvent(KeyEvent event) {
			for(FAbstractControl control : windowControls){
				if( (control.registeredMethods & KEY_METHOD) != 0)
					control.keyEvent(event);
			}			
		}
		
		public void pre(){
			for(FAbstractControl control : windowControls){
				if( (control.registeredMethods & PRE_METHOD) != 0)
					control.pre();
			}
		}
				
		public void dispose(){
			unRegisterMethodsFor151();
			windowControls.clear();
		}
		
		public void addControl(FAbstractControl control){
			windowControls.remove(control);
			windowControls.add(control);
			Collections.sort(windowControls, zorder);
		}
		
		public void removeControl(FAbstractControl control){
			windowControls.remove(control);
		}

	}
}
