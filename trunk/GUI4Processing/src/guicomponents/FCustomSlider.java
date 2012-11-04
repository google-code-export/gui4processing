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

import guicomponents.HotSpot.HSalpha;
import guicomponents.HotSpot.HSrect;

import java.awt.Graphics2D;
import java.awt.RenderingHints;

import processing.core.PApplet;
import processing.core.PGraphicsJava2D;
import processing.core.PImage;

public class FCustomSlider extends FLinearTrackControl {

	protected PImage leftEnd;
	protected PImage thumb;
	protected PImage thumb_mouseover;
	protected PImage rightEnd;
	protected PImage centre;

	
	public FCustomSlider(PApplet theApplet, float p0, float p1, float p2, float p3, String style) {
		super(theApplet, p0, p1, p2, p3);
		loadSkin(style);
		float maxEndLength = Math.max(leftEnd.width, rightEnd.width);
		maxEndLength = Math.max(maxEndLength, 10); // make sure we have enough for right limits value
		trackLength = Math.round(width - 2 * maxEndLength - TINSET);
		trackDisplayLength = trackLength + 2 * Math.min(leftEnd.width, rightEnd.width);
		trackWidth = centre.height;
		trackOffset = Math.max(trackWidth, centre.height/2) + 3;
		extendCentreImage();

		buffer = (PGraphicsJava2D) winApp.createGraphics((int)width, (int)height, PApplet.JAVA2D);
		buffer.g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		buffer.g2.setFont(F4P.numericLabelFont);
		buffer.imageMode(PApplet.CENTER);
		hotspots = new HotSpot[]{
				new HSalpha(THUMB_SPOT, width/2 + (valuePos - 0.5f) * trackLength, height/2, thumb, PApplet.CENTER),  // thumb
				new HSrect(TRACK_SPOT, (width-trackLength)/2, (height-trackWidth)/2, trackLength, trackWidth),		// track
		};
		opaque = false;
		z = Z_SLIPPY;

		epsilon = 0.98f / trackLength;
		ssStartLimit = new StyledString("0.00");
		ssEndLimit = new StyledString("1.00");
		ssValue = new StyledString("0.50");

		// Now register control with applet
		createEventHandler(F4P.sketchApplet, "handleSliderEvents", new Class[]{ FLinearTrackControl.class, boolean.class });
		registeredMethods = PRE_METHOD | DRAW_METHOD | MOUSE_METHOD;
		cursorOver = HAND;
		F4P.addControl(this);
	}

	protected void updateBuffer(){
		if(bufferInvalid) {
			if(isValueChanging)
				hotspots[0].x = (width/2  + (valuePos - 0.5f) * trackLength);		
			Graphics2D g2d = buffer.g2;
			bufferInvalid = false;
			buffer.beginDraw();
			// Back ground colour
			buffer.background(buffer.color(255,0));

			// Draw track, thumb, ticks etc.
			buffer.pushMatrix();
			buffer.translate(width/2, height/2);
			// draw ticks
			if(showTicks){
				float delta = 1.0f / (nbrTicks - 1);
				for(int i = 0; i < nbrTicks; i++){
					int tickx = Math.round((i * delta - 0.5f)*trackLength);
					buffer.strokeWeight(1);
					buffer.stroke(255);
					buffer.line(tickx+1, -trackWidth, tickx+1, trackWidth);
					buffer.strokeWeight(1.5f);
					buffer.stroke(0);
					buffer.line(tickx, -trackWidth, tickx, trackWidth);
				}
			}
			buffer.image(centre,0,0);
			buffer.image(leftEnd, -(trackLength + leftEnd.width)/2, 0);
			buffer.image(rightEnd, (trackLength + rightEnd.width)/2, 0);
			switch(status){
			case OFF_CONTROL:
				buffer.image(thumb,(valuePos - 0.5f) * trackLength, 0);
				break;
			case OVER_CONTROL:
				buffer.image(thumb_mouseover,(valuePos - 0.5f) * trackLength, 0);
				break;
			case PRESS_CONTROL:
				buffer.image(thumb_mouseover,(valuePos - 0.5f) * trackLength, 0);
				break;
			}
			
			// Display slider values
			g2d.setColor(jpalette[2]);
			if(labels != null){
				drawLabels();
			}
			else {

				if(showLimits)
					drawLimits();
				// Display slider value
				if(showValue)
					drawValue();
			}
			buffer.popMatrix();
			buffer.endDraw();
		}
	}
	
	/**
	 * Stretch the centre image from 1 pixel wide to length
	 * of track. 
	 */
	protected void extendCentreImage(){
		PImage p = centre;
		centre = new PImage((int)trackLength, p.height, ARGB);

		p.loadPixels();
		
		int index = 0;
		for(int h = 0; h < centre.height; h++){
			int v = p.pixels[h];
			for(int w = 0; w < centre.width; w++)
				centre.pixels[index++] = v; 
		}
		centre.updatePixels();	
	}
	
	private void loadSkin(String style){
		leftEnd = winApp.loadImage(style + "/end_left.png");
		rightEnd = winApp.loadImage(style + "/end_right.png");
		thumb = winApp.loadImage(style +"/handle.png");
		thumb_mouseover = winApp.loadImage(style +"/handle_mouseover.png");
		//	will be stretched before use
		centre = winApp.loadImage(style + "/centre.png");
		boolean err = false;

		StringBuilder errmess = new StringBuilder();
		if(leftEnd == null) errmess.append("end_left.png\n");
		if(rightEnd == null) errmess.append("end_right.png\n");
		if(thumb== null) errmess.append("handle.png\n");
		if(thumb_mouseover == null) errmess.append("handle_mouseover.png\n");
		
		// See if we have problems with the skin files
		if(errmess.length() > 0){
			err = true;
			System.out.println("The following files could not be found for the skin " + style + ": \n" + errmess.toString()
					+ "\nCheck that these files are correctly placed in the data directory under a folder with"
					+ " the same name as the skin used.\n");
		}
		else {
			if(centre != null && centre.width != 1){
				err = true;
				System.out.println("The supplied centre image for this skin is not of width 1px.");
			}
			if(centre.height != leftEnd.height || leftEnd.height != rightEnd.height){
				err = true;
				System.out.println("The image components of the slider are not all the same height.");
			}
		}
		if(err){
			System.out.println("Reverting to default 'grey_blue' style");
			loadSkin("grey_blue");
		}
		return;
	}
}
