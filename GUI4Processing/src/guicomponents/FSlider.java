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
public class FSlider extends FLinearTrackControl {

	protected RoundRectangle2D track;

	public FSlider(PApplet theApplet, float p0, float p1, float p2, float p3, float tr_width) {
		super(theApplet, p0, p1, p2, p3);
		trackWidth = tr_width;
		trackDisplayLength = width - 2 * TINSET;
		trackLength = trackDisplayLength - trackWidth;
		track = new RoundRectangle2D.Float(-trackDisplayLength/2, -trackWidth/2, 
				trackDisplayLength, trackWidth, 
				trackWidth, trackWidth );
	
		buffer = (PGraphicsJava2D) winApp.createGraphics((int)width, (int)height, PApplet.JAVA2D);
		buffer.g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		buffer.g2.setFont(F4P.numericLabelFont);
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
		createEventHandler(winApp, "handleSliderEvents", new Class[]{ FValueControl.class, boolean.class });
		registeredMethods = PRE_METHOD | DRAW_METHOD | MOUSE_METHOD;
		F4P.addControl(this);
	}

	protected void updateBuffer(){
		if(bufferInvalid) {
			if(isValueChanging)
				hotspots[0].adjust(new Float(width/2  + (valuePos - 0.5f) * trackLength));		
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
					int tickx = Math.round((i * delta - 0.5f)*trackLength);
					buffer.strokeWeight(2);
					buffer.stroke(palette[4]);
					buffer.line(tickx+1, -trackWidth, tickx+1, trackWidth);
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

			// Display slider limits
			g2d.setColor(jpalette[2]);
			if(showLimits)
				drawLimits(trackWidth + 3);
			// Display slider value
			if(showValue)
				drawValue(trackWidth + 3);

			buffer.popMatrix();
			buffer.endDraw();
		}

	}

}
