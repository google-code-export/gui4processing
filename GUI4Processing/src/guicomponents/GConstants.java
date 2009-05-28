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

/**
 * @author Peter Lager
 *
 */
public interface GConstants {

	// 0x0???????  user can disabled these messages 
	// 0x8???????  always display these messages

	
	// ### Event constants ###
	// TextField component (GSlider also uses CHANGED)
	public final static int CHANGED = 			0x00000101;	// Text has changed
	public final static int ENTERED = 			0x00000102;	// Enter key pressed
	public final static int SET = 				0x00000103;		// setText() was used

	// GPanel component
	public final static int COLLAPSED = 		0x00000201;	// Panel has been collapsed
	public final static int EXPANDED = 			0x00000202;	// Panel has been expanded
	public final static int DRAGGED = 			0x00000203;	// Panel has been dragged
	
	// GButton
	public final static int CLICKED = 			0x00000301;
	
	// GCheckbox & GOption
	public final static int SELECTED = 			0x00000401;
	public final static int DESELECTED = 		0x00000402;
	
	// GWindow
	public final static int SHUTDOWN_ON_EXIT = 	0x00000f01;
	public final static int CLOSE_ON_EXIT = 	0x00000f02;
	
	
	// ### GUI build constants ###
	public final static int ADD_DUPLICATE = 	0x00010101;
	public final static int USER_COL_SCHEME = 	0x00010102;
	public final static int DISABLE_AUTO_DRAW =	0x00010103;

	
	// ### Error MessageTypes ###
	public final static int RUNTIME_ERROR = 	0xf0000000;
	// Event method handler errors
	public final static int MISSING = 			0x01000001;	// Can't find standard handler
	public final static int NONEXISTANT = 		0x01000002;
	public final static int EXCP_IN_HANDLER =	0x81000003;	// Exception in event handler
	

}
