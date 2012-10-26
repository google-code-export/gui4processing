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


/**
 * Use this class to create a single selection collection of options. <br>
 * 
 * To use create an object of this class and add GOption and GCheckbox 
 * objects with the add(Control method.
 * 
 * @author Peter Lager
 *
 */
public class FToggleGroup {

	private FToggleControl selected = null;
	private FToggleControl deselected = null;
	
	/**
	 * Create a toggle group object.
	 */
	public FToggleGroup(){	}

	/**
	 * Add an object to this group.
	 * @param tc
	 */
	public void addControl(FToggleControl tc){
		tc.setToggleGroup(this);
	}
	
	/*
	 * Used internally to change selection
	 */
	void makeSelected(FToggleControl tc){
		deselected = selected;
		if(deselected != null)
			deselected.setSelected(false);
		selected = tc;
	}
}
