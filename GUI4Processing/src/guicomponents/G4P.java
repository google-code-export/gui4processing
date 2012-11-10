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

import java.awt.Font;
import java.util.HashMap;
import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PConstants;

public class G4P implements GConstants, PConstants {

	static PApplet sketchApplet = null;
	

	public static int globalColorScheme = GCScheme.BLUE_SCHEME;
	public static int globalAlpha = 255;

	public static Font globalFont = new Font("Dialog", Font.PLAIN, 12);
	public static Font numericLabelFont = new Font("DialogInput", Font.PLAIN, 12);
	
	// Store of info about windows and controls
	static HashMap<PApplet, GWindowInfo> windows = new HashMap<PApplet, GWindowInfo>();
	// Used to order controls
	static GAbstractControl.Z_Order zorder = new GAbstractControl.Z_Order();

	/* INTERNAL USE ONLY  Mouse over changer */
	static boolean cursorChangeEnabled = true;
	static int mouseOff = ARROW;

	static boolean messages = true;

	// Determines how position and size parameters are interpreted when
	// a control is created
	// Introduced V3.0
	static int control_mode = PApplet.CORNER;

	static LinkedList<G4Pstyle> styles = new LinkedList<G4Pstyle>();
	
	/**
	 * Used to register the main sketch window with G4P. This is ignored if any
	 * G4P controls or windows have already been created because the act of
	 * creating a control will do this for you. <br>
	 * 
	 * Some controls are created without passing a reference to the sketch applet
	 * but still need to know it. An example is the GColorChooser control which
	 * cannot be used until this method is called or some other G4P control has
	 * been created.
	 * 
	 * @param app
	 */
	public static void registerSketch(PApplet app){
		if(sketchApplet == null) {
			sketchApplet = app;
			GWindowInfo winfo = windows.get(app);
			if(winfo == null){
				winfo = new GWindowInfo(app);
				windows.put(app, winfo);
			}			
		}
	}
	
	/**
	 * Set the global colour scheme. This will change the local
	 * colour scheme for every control.
	 * @param cs colour scheme to use (0-15)
	 */
	public static void setGlobalColorScheme(int cs){
		cs = Math.abs(cs) % 16; // Force into valid range
		if(globalColorScheme != cs){
			globalColorScheme = cs;
			for(GWindowInfo winfo : windows.values())
				winfo.setColorScheme(globalColorScheme);
		}
	}

	/**
	 * Set the colour scheme for all the controls drawn by the given 
	 * PApplet. This will override any previous colour scheme for 
	 * these controls.
	 * @param app
	 * @param cs
	 */
	public static void setWindowColorScheme(PApplet app, int cs){
		cs = Math.abs(cs) % 16; // Force into valid range
		GWindowInfo winfo = windows.get(app);
		if(winfo != null)
			winfo.setColorScheme(cs);
	}
	
	/**
	 * Set the colour scheme for all the controls drawn by the given 
	 * GWindow. This will override any previous colour scheme for 
	 * these controls.
	 * @param win
	 * @param cs
	 */
	public static void setWindowColorScheme(GWindow win, int cs){
		cs = Math.abs(cs) % 16; // Force into valid range
		GWindowInfo winfo = windows.get(win.papplet);
		if(winfo != null)
			winfo.setColorScheme(cs);
	}
	

	/**
	 * Set the transparency of all controls. If the alpha level for a 
	 * control falls below G4P.ALPHA_BLOCK then it will no longer 
	 * respond to mouse and keyboard events.
	 * 
	 * @param alpha value in the range 0 (transparent) to 255 (opaque)
	 */
	public static void setGlobalAlpha(int alpha){
		alpha = Math.abs(alpha) % 256; // Force into valid range
		if(globalAlpha != alpha){
			globalAlpha = alpha;
			for(GWindowInfo winfo : windows.values())
				winfo.setAlpha(globalAlpha);
		}
	}
	
	/**
	 * Set the transparency level for all controls drawn by the given
	 * PApplet. If the alpha level for a control falls below 
	 * G4P.ALPHA_BLOCK then it will no longer respond to mouse
	 * and keyboard events.
	 * 
	 * @param app
	 * @param alpha value in the range 0 (transparent) to 255 (opaque)
	 */
	public static void setWindowAlpha(PApplet app, int alpha){
		alpha = Math.abs(alpha) % 256; // Force into valid range
		GWindowInfo winfo = windows.get(app);
		if(winfo != null)
			winfo.setAlpha(alpha);
	}
	
	/**
	 * Set the transparency level for all controls drawn by the given
	 * GWindow. If the alpha level for a control falls below 
	 * G4P.ALPHA_BLOCK then it will no longer respond to mouse
	 * and keyboard events.
	 * 
	 * @param app
	 * @param alpha value in the range 0 (transparent) to 255 (opaque)
	 */
	public static void setWindowAlpha(GWindow win, int alpha){
		alpha = Math.abs(alpha) % 256; // Force into valid range
		GWindowInfo winfo = windows.get(win.papplet);
		if(winfo != null)
			winfo.setAlpha(alpha);
	}
	
	
	static void addWindow(GWindow window){
		PApplet app = window.papplet;
		// The first applet must be the sketchApplet
		if(G4P.sketchApplet == null)
			G4P.sketchApplet = app;
		GWindowInfo winfo = windows.get(app);
		if(winfo == null){
			winfo = new GWindowInfo(app);
			windows.put(app, winfo);
		}
	}
	
	/**
	 * Used internally to remove a window from the list of windows. Done when
	 * a window is to be dispose of.
	 * 
	 * @param window
	 */
	static void removeWindow(GWindow window){
		PApplet app = window.papplet;
		GWindowInfo winfo = windows.get(app);
		if(winfo != null){
			winfo.unRegisterMethodsForWindow();
			winfo.releaseControls();
			windows.remove(winfo);
		}
		
	}
	
	/**
	 * Used internally to registera control with its applet.
	 * @param control
	 */
	static void addControl(GAbstractControl control){
		PApplet app = control.getPApplet();
		// The first applet must be the sketchApplet
		if(G4P.sketchApplet == null)
			G4P.sketchApplet = app;
		GWindowInfo winfo = windows.get(app);
		if(winfo == null){
			winfo = new GWindowInfo(app);
			windows.put(app, winfo);
		}
		winfo.addControl(control);
	}
		
	/**
	 * Remove a control from the window. This is used in preparation 
	 * for disposing of a control.
	 * @param control
	 * @return true if control was remove else false
	 */
	static boolean removeControl(GAbstractControl control){
		PApplet app = control.getPApplet();
		GWindowInfo winfo = windows.get(app);
		if(winfo != null){
			winfo = new GWindowInfo(app);
			//control
			winfo.removeControl(control);
			return true;
		}
		return false;
	}
	
	/**
	 * Change the way position and size parameters are interpreted when a control is created. 
	 * or added to another control e.g. GPanel. <br>
	 * There are 3 modes. <br><pre>
	 * PApplet.CORNER	 (x, y, w, h)
	 * PApplet.CORNERS	 (x0, y0, x1, y1)
	 * PApplet.CENTER	 (cx, cy, w, h) </pre><br>
	 * 
	 * @param mode illegal values are ignored leaving the mode unchanged
	 */
	public static void setCtrlMode(int mode){
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

	/**
	 * G4P has a range of support messages eg <br>if you create a GUI component 
	 * without an event handler or, <br>a slider where the visible size of the
	 * slider is less than the difference between min and max values.
	 * 
	 * This method allows the user to enable (default) or disable this option. If
	 * disable then it should be called before any GUI components are created.
	 * 
	 * @param enable
	 */
	public static void messagesEnabled(boolean enable){
		messages = enable;
	}

	/**
	 * Enables or disables cursor over component change. <br>
	 * 
	 * Calls to this method are ignored if no G4P controls have been created.
	 * 
	 * @param enable true to enable cursor change over components.
	 */
	public static void setMouseOverEnabled(boolean enable){
		cursorChangeEnabled = enable;
	}

	/**
	 * Inform G4P which cursor shapes will be used.
	 * Initial values are ARROW (off) and HAND (over)
	 * 
	 * @param cursorOff
	 * @param cursorOver
	 */
	public static void setCursorOff(int cursorOff){
		mouseOff = cursorOff;
	}

	/**
	 * Inform G4P which cursor to use for mouse over.
	 * 
	 * @param cursorOver
	 */
	public static int getCursorOff(){
		return mouseOff;
	}

	/**
	 * Save the current style on a stack. <br>
	 * There should be a matching popStyle otherwise the program it will
	 * cause a memory leakage.
	 */
	static void pushStyle(){
		G4Pstyle s = new G4Pstyle();
		s.ctrlMode = control_mode;
		styles.addLast(s);
	}
	
	/**
	 * Remove and restore the current style from the stack. <br>
	 * There should be a matching pushStyle otherwise the program will crash.
	 */
	static void popStyle(){
		G4Pstyle s = styles.removeLast();
		control_mode = s.ctrlMode;
	}
	
	/**
	 * This class represents the current style used by G4P. 
	 * It can be extended to add other attributes but these should be 
	 * included in the pushStyle and popStyle. 
	 * @author Peter
	 *
	 */
	static class G4Pstyle {
		int ctrlMode;
		
	
	}
}
