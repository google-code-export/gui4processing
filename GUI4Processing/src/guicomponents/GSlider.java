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

import guicomponents.HotSpot.HScircle;
import guicomponents.HotSpot.HSrect;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import processing.core.PApplet;
import processing.core.PGraphicsJava2D;

/**
 * A simple graphical slider.
 * 
 * Either drag the thumb or click on the track to change the slider value. <br>
 * 
 * Supports <br>
 * user defined limits (ascending or descending values) <br>
 * numeric display for limits and current value <br>
 * track ticks and stick to ticks <br>
 * 
 * 
 * @author Peter Lager
 *
 */
public class GSlider extends GLinearTrackControl {

	protected RoundRectangle2D track;

	public GSlider(PApplet theApplet, float p0, float p1, float p2, float p3, float tr_width) {
		super(theApplet, p0, p1, p2, p3);
		trackWidth = tr_width;
		trackDisplayLength = width - 2 * TINSET;
		trackLength = trackDisplayLength - trackWidth;
		track = new RoundRectangle2D.Float(-trackDisplayLength/2, -trackWidth/2, 
				trackDisplayLength, trackWidth, 
				trackWidth, trackWidth );
		trackOffset = trackWidth + 3;
	
		buffer = (PGraphicsJava2D) winApp.createGraphics((int)width, (int)height, PApplet.JAVA2D);
		buffer.g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		buffer.g2.setFont(G4P.numericLabelFont);
		hotspots = new HotSpot[]{
				new HScircle(THUMB_SPOT, width/2 + (valuePos - 0.5f) * trackLength, height/2, trackWidth/2 ),  // thumb
				new HSrect(TRACK_SPOT, (width-trackLength)/2, (height-trackWidth)/2, trackLength, trackWidth),		// track
		};
		z = Z_SLIPPY;

		epsilon = 0.98f / trackLength;

		ssStartLimit = new StyledString("0.00");
		ssEndLimit = new StyledString("1.00");
		ssValue = new StyledString("0.50");

		// Now register control with applet
		createEventHandler(G4P.sketchApplet, "handleSliderEvents",
				new Class[]{ GValueControl.class, GEvent.class },
				new String[]{ "slider", "event" }
		);
		registeredMethods = PRE_METHOD | DRAW_METHOD | MOUSE_METHOD;
		cursorOver = HAND;
		G4P.addControl(this);
	}

	protected void updateDueToValueChanging(){
//		hotspots[0].adjust(new Float(width/2  + (valuePos - 0.5f) * trackLength));
		hotspots[0].x = (width/2  + (valuePos - 0.5f) * trackLength);	
	}

	protected void updateBuffer(){
		if(bufferInvalid) {
			Graphics2D g2d = buffer.g2;
			bufferInvalid = false;
			buffer.beginDraw();
			buffer.rectMode(PApplet.CENTER);
			buffer.ellipseMode(PApplet.CENTER);
			// Back ground colour
			if(opaque == true)
				buffer.background(palette[6]);
			else
				buffer.background(buffer.color(255,0));

			// Draw track, thumb, ticks etc.
			buffer.pushMatrix();
			buffer.translate(width/2, height/2);
			// draw ticks
			if(showTicks){
				float delta = 1.0f / (nbrTicks - 1);
				for(int i = 0; i < nbrTicks; i++){
					float tickx = ((i * delta - 0.5f)*trackLength);
					buffer.strokeWeight(2);
					buffer.stroke(palette[4]);
					buffer.line(tickx, -trackWidth, tickx, trackWidth);
					buffer.strokeWeight(1.2f);
					buffer.stroke(palette[1]);
					buffer.line(tickx, -trackWidth, tickx, trackWidth);
				}
			}
			// Draw track surface
			g2d.setColor(jpalette[5]);
			g2d.fill(track);
			// Draw thumb
			switch(status){
			case OFF_CONTROL:
				buffer.fill(palette[0]);
				break;
			case OVER_CONTROL:
				buffer.fill(palette[1]);
				break;
			case PRESS_CONTROL:
				buffer.fill(palette[14]);
				break;
			}
			buffer.noStroke();
			buffer.ellipse((valuePos - 0.5f) * trackLength, 0, trackWidth, trackWidth);
			// Draw track border
			g2d.setStroke(pen_2_0);
			g2d.setColor(jpalette[3]);
			g2d.draw(track);

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

}
