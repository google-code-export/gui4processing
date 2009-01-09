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

import processing.core.PApplet;
import processing.core.PFont;

public class GFont {
	protected static PApplet app;

	// Font details
	public PFont gpFont;
	public int gpFontSize;
	public int gpHeightOffset; // for text(String,int,int,int,int)

	public static GFont getFont(PApplet theApplet, String fontname, int fsize){
		app = theApplet;
		GFont gfont = new GFont();
		gfont.app = theApplet;
		gfont.gpFont = app.loadFont(fontname);
		gfont.gpFontSize = fsize;

		//panelTabHeight = gpFontSize + 4;
		return gfont;
	}
	
	public static GFont getFont(PApplet theApplet){
		return getFont(theApplet, "Geneva-11.vlw", 11);
	}

	public void setFont(String fontname, int fsize){
		gpFont = app.loadFont(fontname);
		gpFontSize = fsize;
		//panelTabHeight = gpFontSize + 4;
	}

}
