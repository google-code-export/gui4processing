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
import processing.core.PFont;

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
		labelCoreCtor(text, width, GTAlign.LEFT);
	}

	/**
	 * 
	 * @param theApplet
	 * @param text
	 * @param x
	 * @param y
	 * @param width
	 * @param align
	 */
	public GLabel(PApplet theApplet, String text, int x, int y, int width, int align) {
		super(theApplet, x, y);
		labelCoreCtor(text, width, align);
	}

	/**
	 * 
	 * @param theApplet
	 * @param text
	 * @param x
	 * @param y
	 * @param width
	 * @param align
	 * @param colors
	 * @param fontScheme
	 */
//	public GLabel(PApplet theApplet, String text, int x, int y, int width, int align,
//			GCScheme colors, PFont fontScheme) {
//		super(theApplet, x, y, colors, fontScheme);
//		labelCoreCtor(text, width, align);
//	}
	
	/**
	 * 
	 * @param theApplet
	 * @param text
	 * @param x
	 * @param y
	 * @param width
	 * @param colors
	 * @param font
	 */
//	public GLabel(PApplet theApplet, String text, int x, int y, int width,
//			GCScheme colors, PFont font){
//		super(theApplet, x, y, colors, font);
//		labelCoreCtor(text, width, GTAlign.LEFT);
//	}
	
	private void labelCoreCtor(String text, int width, int align){
		this.width = width;
		this.height = localFont.size + 2 * PADV;
		opaque = false;
		if(text != null)
			setText(text);
		registerAutos_DMPK(true, false, false, false);
	}
	
	public void draw(){
		if(visible){
			int textX = 2 * border;
			switch(textAlign){
			case GTAlign.RIGHT:
				textX += width - textWidth - PADH * border;
				break;
			case GTAlign.CENTER:
				textX += (width - textWidth - PADH * border)/2;
				break;
			}
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
			app.text(text, pos.x + textX, pos.y + PADV, width - PADH - 2* border, height);
		}
	}

}
