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

import java.util.TreeSet;

import processing.core.PApplet;

/**
 * This is used to group options together to provide single-selection
 * from 2 or more option buttons.
 * 
 * @author Peter Lager
 *
 */
public class GOptionGroup {

	protected PApplet app;
	
	protected GOption selected;
	protected GOption deselected;
	
	protected TreeSet<GOption> options;
	
	protected int nbrOptions;
	
	public GOptionGroup(PApplet theApplet){
		app = theApplet;
		options = new TreeSet<GOption>();
		selected = null;
		deselected = null;
	}
	
	public void addOption(GOption option){
		if(options.isEmpty())
			selected = option;
		nbrOptions++;
		options.add(option);
		option.setGroup(this);
	}
	
	public void setSelected(GOption option){
		deselected = selected;
		selected = option;
	}
	
	public GOption getSelected(){
		return selected;
	}
	
	public GOption getDeselected(){
		return deselected;
	}
	
}
