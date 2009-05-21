/*
  Part of the GUI for Processing library 
  	http://gui-for-processing.lagers.org.uk
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

	// Event constants
	// TextField component (GSlider also uses CHANGED)
	public final static int CHANGED = 0x00010001;	// Text has changed
	public final static int ENTERED = 0x0001002;	// Enter key pressed
	public final static int SET = 0x00010003;		// setText() was used

	// GPanel component
	public final static int COLLAPSED = 0x00020001;	// Panel has been collapsed
	public final static int EXPANDED = 0x00020002;	// Panel has been expanded
	public final static int DRAGGED = 0x00020003;	// Panel has been dragged
	
	// GButton
	public final static int CLICKED = 0x00030001;
	
	// GCheckbox & GOption
	public final static int SELECTED = 0x00040001;
	public final static int DESELECTED = 0x00040002;
	
	// MessageTypes
	// Event method handlers
	public final static int MISSING = 0x01010001;	// Can't find standard handler
	public final static int NONEXISTANT = 0x01010002;
	

}
