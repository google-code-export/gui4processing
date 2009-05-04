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
public class G4P implements PConstants {

	/**
	 * Set of all the GUI components created
	 */
	private static HashSet<GComponent> all = new HashSet<GComponent>();

	private static boolean autoDrawOn = true;

	public static PApplet app = null;

	public static PStyle g4pStyle = null;

	public static boolean messages = true;

	/**
	 * This should be called by all ctors in GComponent and since all GUI 
	 * components inherit from GComponent and are required to call a 
	 * GComponent ctor then all GUI components will automatically be 
	 * registered in the set.
	 * 
	 * @param c the component that has been created.
	 */
	public static void addComponent(GComponent c){
		if(g4pStyle == null)
			getStyle();
		if(all.contains(c)){
			if(messages)
				System.out.println("Component " + c + " has already been regitered!");
		}
		else
			all.add(c);
	}

	private static void getStyle(){
		PGraphics temp = new PGraphics();

		g4pStyle = temp.getStyle();

		g4pStyle.rectMode = CORNER;
		g4pStyle.ellipseMode = DIAMETER;

		g4pStyle.colorMode = RGB;
		g4pStyle.colorModeA = 255.0f;
		g4pStyle.colorModeX = 255.0f;
		g4pStyle.colorModeY = 255.0f;
		g4pStyle.colorModeZ = 255.0f;
	}


	public static void disableAutoDraw(){
		unregisterFromPAppletDraw();
		if(messages){
			System.out.println("You have disabled autoDraw so you have to use");
			System.out.println("G4P.draw() when you want to display the GUI" );
			System.out.println("this is not action is not reversible." );
		}
	}

	public static boolean isAutoDrawOn(){
		return autoDrawOn;
	}

	/**
	 * Use G4P.draw() if you want to control when you the GUI is to be drawn
	 */
	public static void draw(){
		if(all.size() > 0){
			// Time to take over the responsibility for drawing
			if(autoDrawOn)
				unregisterFromPAppletDraw();
			// Draw the components note that GPanels will call the appropriate
			// draw methods for the components on them
			app.hint(DISABLE_DEPTH_TEST);
			Iterator<GComponent> iter = all.iterator();
			GComponent c;
			while(iter.hasNext()){
				c = iter.next();
				if(c.getParent() == null)
					c.draw();
			}
			app.hint(ENABLE_DEPTH_TEST);
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
		Iterator<GComponent> iter = all.iterator();
		GComponent c;
		while(iter.hasNext()){
			c = iter.next();
			if(c.getParent() == null){
				c.getPApplet().unregisterDraw(c);
			}
		}
		autoDrawOn = false;
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
