/*
  Part of the GUI for Processing library 
  	http://gui4processing.lagers.org.uk
	http://code.google.com/p/gui4processing/
	
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

import processing.core.PApplet;


@SuppressWarnings("serial")
public class GApplet extends PApplet {

	protected HashSet<GComponent> widgets = new HashSet<GComponent>();
	
	public GApplet(){
		super();
	}
	
	protected boolean addComponent(GComponent widget){
		if(widgets.contains(widget)){
			System.out.println("This component has already been added to this panel");
			return false;
		} else {
			widgets.add(widget);
			return true;
		}
	}
	
	public void panel(String id, String text, int x, int y, int width, int height){
		GPanel widget = new GPanel(this, text, x, y, width, height);
		addComponent(widget);
		
	}
	
	public void panel(String id, String text, int x, int y, int width, int height,
			GColor colorScheme, GFont fontScheme){
		GPanel widget = new GPanel(this, text, x, y, width, height, colorScheme, fontScheme);
		addComponent(widget);
	}

} // end of class
