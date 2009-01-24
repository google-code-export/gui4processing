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
	protected PApplet app;

	// Font details
	public PFont gpFont;
	public int gpFontSize;
	public int gpHeightOffset; // for text(String,int,int,int,int)

	/**
	 * Create a font
	 * 
	 * @param theApplet
	 * @param fontname .vlw name for font
	 * @param fsize
	 * @return
	 */
	public static GFont getFont(PApplet theApplet, String fontname, int fsize){
		return new GFont(theApplet, fontname, fsize);
	}
	
	/**
	 * Get the default font
	 * @param theApplet
	 * @return
	 */
	public static GFont getDefaultFont(PApplet theApplet){
		return getFont(theApplet, "Arial-BoldMT-12.vlw", 12);
	}

	/**
	 * Create a GFont object with the given filename and size
	 * @param theApplet
	 * @param fontname
	 * @param fsize
	 */
	public GFont(PApplet theApplet, String fontname, int fsize) {
		app = theApplet;
		setFont(fontname, fsize);
	}

	public void setFont(String fontname, int fsize){
		gpFont = app.loadFont(fontname);
		gpFontSize = fsize;
	}

	
}
