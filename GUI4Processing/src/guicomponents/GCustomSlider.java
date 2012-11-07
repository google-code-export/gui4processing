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
import processing.core.PGraphics;
import processing.core.PGraphicsJava2D;
import processing.core.PImage;

/**
 * Slider that can be customised with user provided graphics. <br>
 * 
 * This class replaces the GWSlider provided in pre v3 editions of this library.
 * <p>
 * The main difference to the GSlider class is the ability to skin the slider with user provided graphics.
 * The slider is broken down into 4 segments, each having a specific image file that relates
 * to them:
 * </p>
 * 
 * <ul>
 * <li>Left end cap of the slider(end_left.png)</li>
 * <li>Right end cap of the slider(end_right.png)</li>
 * <li>An extendible centre segment(centre.png)</li>
 * <li>Draggable thumb (handle.png and handle_mouseover.png)</li>
 * </ul>
 * 
 * <p>
 * The five images stated above define the skin that is applied to slider. A default skin is included in the library
 * and applied when no other alternative is provided. To generate a skin all these images must be included into a 
 * folder in the sketches data directory, where the name of the folder is the name of the skin. When creating a new
 * slider, there is a constructor available that allows you to specify the skin to use. Eg, if you have a folder name
 * 'ShinyRedSkin' in your data directory that has the above images in, then pass a string with "ShinyRedSkin" to the
 * constructor.
 * </p>
 * 
 * <p>
 * The images need to related. The end_left, end_right and centre png's must all be the same height. The height can be
 * whatever is required, though values round 20 is recommended. The end segments can both be different lengths and the
 * length of the centre images must be 1 pixel. The centre image must be just 1 pixel wide to represent the colours
 * across its width. This will be <i>stretched</i> to fit the slider track length. <br>
 * The thumb/handle can be any height and width but should be an odd number of pixels. An odd number allows a perfect
 * centre to be found as fractional pixels are not possible. Alpha channel use is recommended to generate interesting skins.
 * </p>
 *  * @author Peter Lager
 *
 */
public class GCustomSlider extends GLinearTrackControl {

	protected PImage leftEnd;
	protected PImage thumb;
	protected PImage thumb_mouseover;
	protected PImage rightEnd;
	protected PImage centre;

	public GCustomSlider(PApplet theApplet, float p0, float p1, float p2, float p3, String skin) {
		super(theApplet, p0, p1, p2, p3);
		loadSkin(skin);
		float maxEndLength = Math.max(leftEnd.width, rightEnd.width);
		maxEndLength = Math.max(maxEndLength, 10); // make sure we have enough to show limits value
		trackLength = Math.round(width - 2 * maxEndLength - TINSET);
		trackDisplayLength = trackLength + 2 * Math.min(leftEnd.width, rightEnd.width);
		trackWidth = centre.height;
		trackOffset = Math.max(trackWidth, thumb.height/2) + 3;
		extendCentreImage();

		buffer = (PGraphicsJava2D) winApp.createGraphics((int)width, (int)height, PApplet.JAVA2D);
		buffer.g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		buffer.g2.setFont(G4P.numericLabelFont);
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
		createEventHandler(G4P.sketchApplet, "handleSliderEvents", new Class[]{ GLinearTrackControl.class, boolean.class });
		registeredMethods = PRE_METHOD | DRAW_METHOD | MOUSE_METHOD;
		cursorOver = HAND;
		G4P.addControl(this);
	}

	protected void updateDueToValueChanging(){
		hotspots[0].x = (width/2  + (valuePos - 0.5f) * trackLength);
	}

	protected void updateBuffer(){
		if(bufferInvalid) {
			Graphics2D g2d = buffer.g2;
			bufferInvalid = false;
			buffer.beginDraw();
			
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
					float tickx = Math.round((i * delta - 0.5f) * trackLength);
					buffer.strokeWeight(2);
					buffer.stroke(255);
					buffer.line(tickx+1, -trackWidth, tickx+1, trackWidth);
					buffer.strokeWeight(1.0f);
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
				if(showValue)
					drawValue();
			}
			buffer.popMatrix();
			buffer.endDraw();
		}
	}
	
	protected void extendCentreImage(){
		int tl = (int)trackLength;
		PGraphics pg = winApp.createGraphics(tl, centre.height, JAVA2D);
		int rem = tl % centre.width;
		int n = tl / centre.width;
		n = (rem == 0) ? n : n + 1;
		int px = (tl - centre.width * n)/2;
		pg.beginDraw();
		pg.background(winApp.color(255,0));
		pg.imageMode(CORNER);
		
		while(px < tl){
			pg.image(centre, px, 0);
			px += centre.width;
		}
		
		pg.endDraw();
		centre = pg;
	}
	
	private void loadSkin(String style){
		leftEnd = winApp.loadImage(style + "/end_left.png");
		rightEnd = winApp.loadImage(style + "/end_right.png");
		thumb = winApp.loadImage(style +"/handle.png");
		thumb_mouseover = winApp.loadImage(style +"/handle_mouseover.png");
		//	will be stretched before use
		centre = winApp.loadImage(style + "/centre.png");

		StringBuilder errmess = new StringBuilder();
		if(leftEnd == null) errmess.append("end_left.png\n");
		if(rightEnd == null) errmess.append("end_right.png\n");
		if(thumb== null) errmess.append("handle.png\n");
		if(thumb_mouseover == null) errmess.append("handle_mouseover.png\n");
		
		// See if we have problems with the skin files
		if(errmess.length() > 0){
			System.out.println("The following files could not be found for the skin " + style + ": \n" + errmess.toString()
					+ "\nCheck that these files are correctly placed in the data directory under a folder with"
					+ " the same name as the skin used.\n");
			System.out.println("Reverting to default 'grey_blue' style");
			loadSkin("grey_blue");
		}
	}
}
