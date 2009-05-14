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

import processing.core.PApplet;
import processing.core.PConstants;

/**
 * DO NOT CREATE OBJECTS FROM THIS CLASS
 * use the appropriate cursor control methods 
 * in the G4P class
 * 
 * @author Peter Lager
 *
 */
public class CursorManager implements PConstants {

	public boolean overControl = false;
	private boolean enabled = true;
	private int cursorOff = ARROW;
	private int cursorOn = HAND;

	CursorManager(){
	}

	public void setEnabled(PApplet theApplet, boolean enable){
		
		if(enabled != enable){
			enabled = enable;
			if(enabled){  // register the pre and post methos
				theApplet.registerPre(this);
				theApplet.registerPost(this);
			}
			else { // unregister the pre and post methods and restore cursor shape
				theApplet.unregisterPre(this);
				theApplet.unregisterPost(this);
				theApplet.cursor(cursorOff);
			}

		}
	}
	
	public void pre(){
		if(enabled && overControl){
			
		}
	}
	
	public void post(){
		overControl = false;
	}

}
