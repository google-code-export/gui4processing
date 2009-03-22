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

/**
 * This class has only static methods. It keeps track of all GComponents created.
 * Its primary role is encapsulated in the draw() method which can override
 * the default Processing drawing mechanism.
 * 
 * @author Peter Lager
 *
 */
public class G4P {

	/**
	 * Set of all the GUI components created
	 */
	private static HashSet<GComponent> all = new HashSet<GComponent>();
	
	private static boolean unused = true;
	
	/**
	 * This should by all ctors in GComponent and since all GUI components
	 * inherit from GComponent and are required to call a GComponent ctor
	 * then all GUI components will automatically be registered in the set.
	 * 
	 * @param c
	 */
	public static void addComponent(GComponent c){
		if(all.contains(c))
			System.out.println("Component " + c + " has already been regitered!");
		else
			all.add(c);
	}
	
	/**
	 * Use G4P.draw() if you want to control when you the GUI is to be drawn
	 */
	public static void draw(){
		if(unused)
			unregisterFromPAppletDraw();
		// Draw the components note that GPanels will call the appropriate
		// draw methods for the components on them
		Iterator<GComponent> iter = all.iterator();
		GComponent c;
		while(iter.hasNext()){
			c = iter.next();
			if(c.getParent() == null)
				c.draw();
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
		unused = false;
	}
	
}
