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

import processing.core.PApplet;
import processing.core.PConstants;

public class F4P implements GConstants, PConstants {

	static PApplet sketchApplet = null;
	
	public static int globalColorScheme = FCScheme.CYAN_SCHEME;
	public static int globalAlpha = 255;

	public static Font globalFont = new Font("Dialog", Font.PLAIN, 12);
	public static Font numericLabelFont = new Font("DialogInput", Font.PLAIN, 12);
	
	// Store of info about windows and controls
	static HashMap<PApplet, WindowInfo> windows = new HashMap<PApplet, WindowInfo>();
	// Used to order controls
	static FAbstractControl.Z_Order zorder = new FAbstractControl.Z_Order();

	/* INTERNAL USE ONLY  Mouse over changer */
	static boolean cursorChangeEnabled = true;
	static int mouseOff = ARROW;

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
		if(sketchApplet == null)
			sketchApplet = app;
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
			for(WindowInfo winfo : windows.values())
				winfo.setGlobalColorScheme(globalColorScheme);
		}
	}

	/**
	 * Set the transparency of all controls. <br>
	 * If alpha is below GConstants.ALPHA_BLOCK then the control is no
	 * longer available., so will not respond to mouse and keyboard events.
	 * 
	 * @param alpha value in the range 0 (transparent) to 255 (opaque)
	 */
	public static void setGlobalAlpha(int alpha){
		alpha = Math.abs(alpha) % 256; // Force into valid range
		if(globalAlpha != alpha){
			globalAlpha = alpha;
			for(WindowInfo winfo : windows.values())
				winfo.setGlobalAlpha(globalAlpha);
		}
	}
	
	static void addControl(FAbstractControl control){
		PApplet app = control.getPApplet();
		// The first applet must be the sketchApplet
		if(F4P.sketchApplet == null)
			F4P.sketchApplet = app;
		WindowInfo winfo = windows.get(app);
		if(winfo == null){
			winfo = new WindowInfo(app);
			windows.put(app, winfo);
		}
		winfo.addControl(control);
	}
		
	static void addWindow(FWindow window){
		PApplet app = window.papplet;
		// The first applet must be the sketchApplet
		if(F4P.sketchApplet == null)
			F4P.sketchApplet = app;
		WindowInfo winfo = windows.get(app);
		if(winfo == null){
			winfo = new WindowInfo(app);
			windows.put(app, winfo);
		}
	}
	
	static void removeWindow(FWindow window){
		PApplet app = window.papplet;
		WindowInfo winfo = windows.get(app);
		if(winfo != null){
			winfo.unRegisterMethodsForWindow();
			winfo.releaseControls();
			windows.remove(winfo);
		}
		
	}
	
	/**
	 * Remove a control from the window. This is used in preparation 
	 * for disposing of a control.
	 * @param control
	 * @return true if control was remove else false
	 */
	static boolean removeControl(FAbstractControl control){
		PApplet app = control.getPApplet();
		WindowInfo winfo = windows.get(app);
		if(winfo != null){
			winfo = new WindowInfo(app);
			//control
			winfo.removeControl(control);
			return true;
		}
		return false;
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

}
