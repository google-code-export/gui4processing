/*
  Part of the GUI for Processing library 
  	http://gui4processing.lagers.org.uk
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

import java.util.HashSet;
import java.util.Iterator;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PStyle;

/**
 * This class has only static methods. It keeps track of all GComponents created.
 * Its primary role is encapsulated in the draw() method which can override
 * the default Processing drawing mechanism.
 * 
 * @author Peter Lager
 *
 */
public class G4P implements PConstants, GConstants {

	/**
	 * Set of all the GUI components created
	 */
	private static HashSet<GComponent> allComponents = new HashSet<GComponent>();

	/**
	 * Set of GControlWindows
	 */
	private static HashSet<GWindow> allWinApps = new HashSet<GWindow>();
	
	
	private static boolean autoDrawOn = true;

	// Will be set when and first component is created
	public static PApplet mainWinApp = null;

	public static PStyle g4pStyle = null;

	public static boolean messages = true;
	
	/** INTERNAL USE ONLY  Cursor over changer */
	private static GCursorImageChanger mcd = new GCursorImageChanger();
	public static boolean overControl = false;
	public static boolean cursorChangeEnabled = false;
	public static int mouseOff = ARROW;
	public static int mouseOver = HAND;

	/**
	 * Enables or disables cursor over component change
	 * 
	 * This is ignored if no G4P components have been created yet
	 * @param enable
	 */
	public static void setMouseOverEnabled(boolean enable){
		cursorChangeEnabled = enable;
		// If disabling make sure that the cursor is set to mouseOff
		// for the mainWinApp and all control windows
		if(cursorChangeEnabled == false){
			mainWinApp.cursor(mouseOff);
			Iterator<GWindow> iter = allWinApps.iterator();
			while(iter.hasNext()){
				iter.next().embed.cursor(mouseOff);
			}
		}
	}
	
	/**
	 * Inform G4P which cursor shapes will be used.
	 * Initial values are ARROW (off) and HAND (over)
	 * 
	 * @param cursorOff
	 * @param cursorOver
	 */
	public static void cursor(int cursorOff, int cursorOver){
		mouseOff = cursorOff;
		mouseOver = cursorOver;
	}
	
	/**
	 * Inform G4P which cursor to use for mouse over.
	 * 
	 * @param cursorOver
	 */
	public static void cursor(int cursorOver){
		mouseOver = cursorOver;
	}
	
	/**
	 * INTERNAL USE ONLY
	 * This should be called by all ctors in GComponent and since all GUI 
	 * components inherit from GComponent and are required to call a 
	 * GComponent ctor then all GUI components will automatically be 
	 * registered in the set.
	 * 
	 * @param c the component that has been created.
	 */
	public static void addComponent(GComponent c){
//		if()
		if(g4pStyle == null)
			getStyle();
		if(allComponents.contains(c)){
			GMessenger.message(ADD_DUPLICATE, c ,null);
		}
		else
			allComponents.add(c);
	}
	
	/**
	 * INTERNAL USE ONLY
	 * Used to register the main window for cursor over behaviour.
	 * 
	 */
	public static void setMainApp(PApplet theApplet){
		if(mainWinApp == null){
			mainWinApp = theApplet;
			mainWinApp.registerPost(mcd);
		}
	}
	
	/**
	 * INTERNAL USE ONLY
	 * Record a new control window
	 * @param controlWindow
	 */
	public static void addControlWindow(GWindow controlWindow){
		allWinApps.add(controlWindow);
	}
	
	/**
	 * INTERNAL USE ONLY
	 * Remove control window - called when a ControlWindow is closed
	 * for good.
	 *  
	 * @param controlWindow
	 */
	public static void removeControlWindow(GWindow controlWindow){
		allWinApps.remove(controlWindow);
	}
	
	/**
	 * INTERNAL USE ONLY
	 */
	private static void getStyle(){
		PGraphics temp = new PGraphics();

		g4pStyle = temp.getStyle();

		g4pStyle.rectMode = CORNER;
		g4pStyle.ellipseMode = DIAMETER;
		g4pStyle.imageMode = CORNER;
		g4pStyle.shapeMode = CORNER;
		
		g4pStyle.colorMode = RGB;
		g4pStyle.colorModeA = 255.0f;
		g4pStyle.colorModeX = 255.0f;
		g4pStyle.colorModeY = 255.0f;
		g4pStyle.colorModeZ = 255.0f;
	}

	/**
	 * Set the color scheme to be used by G4P<br>
	 * Only reqd if different from the default blue scheme to be
	 * global specify before creating GUI components
	 * 
	 * Available schemes:
	 * BLUE_SCHEME, GREEN_SCHEME, RED_SCHEME, PURPLE_SCHEME
	 * YELLOW_SCHEME, CYAN_SCHEME, GREY_SCHEME
	 * 
	 * @param theApplet
	 * @param schemeNo GCScheme.GREEN_SCHEME
	 */
	public static void setColorScheme(PApplet theApplet, int schemeNo){
		// If both theApplet and app are null there is nothing we can do!
		if(theApplet != null)
			setMainApp(theApplet);
		else if(mainWinApp == null)
			return;
		GComponent.globalColor = GCScheme.getColor(mainWinApp,  schemeNo);
	}

	/**
	 * Set the font type and size to be used by G4P<br>
	 * Only reqd if different from the default "Serif" 11 <br>
	 * to be global specify before creating GUI components
	 * 
	 * @param theApplet
	 * @param fontName name of font
	 * @param fontSize font size
	 */
	public static void setFont(PApplet theApplet, String fontName, int fontSize){
		// If both theApplet and app are null there is nothing we can do!
		if(theApplet != null)
			setMainApp(theApplet);
		else if(mainWinApp == null)
			return;
		GComponent.globalFont = GFont.getFont(mainWinApp, fontName, fontSize);
	}

	/**
	 * Use G4P.draw() if you want to control when you the GUI is to be drawn
	 */
	public static void draw(){
		if(allComponents.size() > 0){
			// Time to take over the responsibility for drawing
			if(autoDrawOn)
				unregisterFromPAppletDraw();
			// Draw the components on the mainWinApp only.
			// Note that GPanels will call the appropriate
			// draw methods for the components on them
			mainWinApp.hint(DISABLE_DEPTH_TEST);
			Iterator<GComponent> iter = allComponents.iterator();
			GComponent c;
			while(iter.hasNext()){
				c = iter.next();
				if(c.getParent() == null && c.getPApplet() == mainWinApp)
					c.draw();
			}
			mainWinApp.hint(ENABLE_DEPTH_TEST);
		}
	}

	/**
	 * Unregister all GComponents that have no parent from PApplets auto draw
	 * mechanism. The G4P.draw() will now be responsible for drawing the 
	 * components.
	 * 
	 * It is called once on the first call to G4P.draw()
	 * 
	 */
	private static void unregisterFromPAppletDraw() {
		Iterator<GComponent> iter = allComponents.iterator();
		GComponent c;
		while(iter.hasNext()){
			c = iter.next();
			if(c.getParent() == null && c.getPApplet() == mainWinApp){
				c.regDraw = false;
				c.getPApplet().unregisterDraw(c);
			}
		}
		autoDrawOn = false;
	}

	/**
	 * Once disabled you need to call G4P.draw() from the draw() method if you
	 * wish to see the GUI
	 */
	public static void disableAutoDraw(){
		unregisterFromPAppletDraw();
		GMessenger.message(DISABLE_AUTO_DRAW, null, null);
	}

	/**
	 * Is autodraw on?
	 * 
	 */
	public static boolean isAutoDrawOn(){
		return autoDrawOn;
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

}
