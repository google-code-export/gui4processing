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
import java.awt.event.MouseEvent;

import processing.core.PApplet;

/**
 * The vertical slider component
 * 
 * @author Peter Lager
 *
 */
public class GVertSlider extends GSlider {
	
	/**
	 * Create a vertical slider.
	 * Default values:
	 * 		Range 0-100
	 *      Initial value 50
	 * Use the setLimits method to customise these values.
	 * 
	 * @param theApplet
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param colorScheme
	 */
	public GVertSlider(PApplet theApplet, int x, int y, int width, int height,GCScheme colorScheme){
		super(theApplet, x, y, width, height, colorScheme);
		initThumbDetails();
	}

	/**
	 * Create a vertical slider.
	 * Default values:
	 * 		Range 0-100
	 *      Initial value 50
	 * Use the setLimits method to customise these values.
	 * 
	 * @param theApplet
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public GVertSlider(PApplet theApplet, int x, int y, int width, int height){
		super(theApplet, x, y, width, height);
		initThumbDetails();
	}

	/**
	 * Initialises the thumb details called by constructor
	 */
	protected void initThumbDetails(){
		thumbSize = Math.max(10, height / 20);
		thumbMin = thumbSize/2;
		thumbMax = height - thumbSize/2;
		thumbTargetPos = thumbPos;
//		setValue(value);
	}
	
	/**
	 * Draw the slider
	 */
	public void draw(){
		if(visible){
			Point pos = new Point(0,0);
			calcAbsPosition(pos);
			app.noStroke();
			app.fill(localColor.sdrBackground);
			app.rect(pos.x, pos.y, width, height);
			app.fill(localColor.sdrThumb);
			app.rect(pos.x, pos.y + thumbPos - thumbSize/2, width, thumbSize);
			if(border != 0){
				app.strokeWeight(border);
				app.noFill();
				app.stroke(localColor.sdrBorder);
				app.rect(pos.x, pos.y, width, height);
			}
		}
	}
	
	/**
	 * All GUI components are registered for mouseEvents
	 */
	public void mouseEvent(MouseEvent event){
		switch(event.getID()){
		case MouseEvent.MOUSE_PRESSED:
			if(focusIsWith != this && isOver(app.mouseX, app.mouseY)){
				mdx = app.mouseX;
				mdy = app.mouseY;
				takeFocus();
			}
			break;
		case MouseEvent.MOUSE_CLICKED:
			if(focusIsWith == this){
				looseFocus();
				mdx = mdy = Integer.MAX_VALUE;
			}
			break;
		case MouseEvent.MOUSE_RELEASED:
			if(focusIsWith == this && mouseHasMoved(app.mouseX, app.mouseY)){
				looseFocus();
				mdx = mdy = Integer.MAX_VALUE;
			}
			break;
		case MouseEvent.MOUSE_DRAGGED:
			if(focusIsWith == this){
				isValueChanging = true;
				Point p = new Point(0,0);
				calcAbsPosition(p);
				thumbTargetPos = PApplet.constrain(app.mouseY - offset - p.y, thumbMin, thumbMax);
			}
			break;
		}
	}

	/**
	 * Determines whether the position ax, ay is over the thumb
	 * of this GPanel.
	 * 
	 * @return true if mouse is over the panel tab else false
	 */
	public boolean isOver(int ax, int ay){
		Point p = new Point(0,0);
		calcAbsPosition(p);
		if(ax >= p.x && ax <= p.x + width && ay >= p.y + thumbPos - thumbSize/2 && ay <= p.y + thumbPos + thumbSize/2){
			offset = ay - (p.y + thumbPos);
			return true;
		}
		else 
			return false;
	}
} // end of class
