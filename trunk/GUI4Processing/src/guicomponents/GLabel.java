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

import java.awt.Point;

import processing.core.PApplet;

/**
 * The label component.
 * 
 * @author Peter Lager
 *
 */
public class GLabel extends GComponent {

	/**
	 * 
	 * @param theApplet
	 * @param text
	 * @param x
	 * @param y
	 * @param width
	 */
	public GLabel(PApplet theApplet, String text, int x, int y, int width) {
		super(theApplet, x, y);
		labelCoreCtor(text, width, 0);
	}

	/**
	 * 
	 * @param theApplet
	 * @param text
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public GLabel(PApplet theApplet, String text, int x, int y, int width, int height) {
		super(theApplet, x, y);
		labelCoreCtor(text, width, height);
	}
	
	/**
	 * 
	 * @param text
	 * @param width
	 * @param height
	 */
	private void labelCoreCtor(String text, int width, int height){
		this.width = width;
		this.height = localFont.size + 2 * PADV;
		if(height > this.height)
			this.height = height;
		opaque = false;
		if(text != null)
			setText(text);
		registerAutos_DMPK(true, false, false, false);
	}
	
	/**
	 * Draw the label
	 */
	public void draw(){
		if(visible){
			app.pushStyle();
			Point pos = new Point(0,0);
			calcAbsPosition(pos);
			app.strokeWeight(border);
			app.stroke(localColor.lblBorder);
			if(opaque)
				app.fill(localColor.lblBack);
			else
				app.noFill();
			app.rect(pos.x,pos.y, width, height);
			// Draw text
			app.noStroke();
			app.fill(localColor.lblFont);
			app.textFont(localFont, localFont.size);
			app.text(text, pos.x + alignX, pos.y + (height - localFont.size)/2, width - PADH - 2* border, height);
			app.popStyle();
		}
	}

}
