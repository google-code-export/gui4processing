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

package g4p_controls;

import g4p_controls.HotSpot.HScircle;

import java.awt.Graphics2D;
import java.awt.RenderingHints;

import processing.core.PApplet;
import processing.core.PGraphicsJava2D;
import processing.event.MouseEvent;

public class GStick extends GAbstractControl {
	// palette index constants
	protected static final int BORDERS = 0;
	protected static final int LED_INACTIVE = 1;
	protected static final int LED_ACTIVE = 14;
	protected static final int STICK = 0;
	protected static final int STICK_TOP = 3;
	protected static final int STICK_TOP_OVER = 11;
	protected static final int STICK_TOP_DRAG = 14;
	protected static final int OUTERRING = 6;
	protected static final int ACTIONRING = 5;
	protected static final int BACK = 6;
	//angle constants
	protected static final float RAD90 = PApplet.radians(90);
	protected static final float RAD45 = PApplet.radians(45);
	protected static final float RAD22_5 = PApplet.radians(22.5f);


	protected static final int[] posMap = new int[] { 0x01, 0x07, 0x04, 0x1c, 0x10, 0x70, 0x40, 0xc1 };
	protected static final int[] posX = new int[] {  1,  1,  0, -1, -1, -1,  0,  1 };
	protected static final int[] posY= new int[] {  0,  1,  1,  1,  0, -1, -1, -1 };
	
	protected int mode = 1;  // 1 = 4 and 2 = 8 directions

	protected final float ledHeight, ledWidth;
	protected float ledRingRad;

	protected float actionRad, actionRadLimit, gripRadius;

	protected int position = -1;

	protected int status = OFF_CONTROL;

	
	public GStick(PApplet theApplet, float p0, float p1, float p2) {
		super(theApplet, p0, p1, PApplet.max(p2, 50), PApplet.max(p2, 50));

		buffer = (PGraphicsJava2D) winApp.createGraphics((int)width, (int)height, PApplet.JAVA2D);
		buffer.g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		buffer.rectMode(PApplet.CORNER);
		buffer.ellipseMode(PApplet.CORNER);
		opaque = false;

		ledHeight = PApplet.max(6.0f, width * 0.06f);
		ledWidth = 1.6f * ledHeight;

		ledRingRad = (width - ledHeight - 3)/2;
		actionRad = 0.4f * ledRingRad;
		gripRadius = PApplet.max(10, 0.1f * actionRad);
		actionRadLimit = ledRingRad - gripRadius - ledHeight/2;
		
		hotspots = new HotSpot[]{
				new HScircle(1, width/2, height/2, gripRadius)
		};
		z = Z_SLIPPY;

		// Now register control with applet
		createEventHandler(G4P.sketchApplet, "handleStickEvents",
				new Class<?>[]{ GStick.class, GEvent.class }, 
				new String[]{ "stick", "event" } 
		);
		registeredMethods = DRAW_METHOD | MOUSE_METHOD ;
		cursorOver = CROSS;
		G4P.addControl(this);
	}

	public void setMode(int m){
		switch(m){
		case X4:
			m = 1;
			break;
		case X8:
			m = 2;
			break;
		default:
			m = mode;
		}
		if(m != mode){
			mode = m;
			bufferInvalid = true;
		}
	}
	
	public int getPosition(){
		return position;
	}
	
	public int getStickX(){
		return (position < 0) ? 0 : posX[position];
	}
	
	public int getStickY(){
		return (position < 0) ? 0 : posY[position];
	}
	
	

	/**
	 * Calculate the angle to the knob centre making sure it is in
	 * the range 0-360
	 * @param px relative to centre
	 * @param py relative to centre
	 * @return
	 */
	protected float calcStickAngle(float px, float py){
		float a = PApplet.atan2(py, px);
		if(a < 0)
			a += PApplet.TWO_PI;
		return a;	
	}
	
	protected int getPositionFromAngle(float a){
//		a = (a + RAD22_5) % PApplet.TWO_PI;
		int newState;
		if(mode == 1){
			a = (a + RAD45) % PApplet.TWO_PI;
			newState = 2 * (int)(a / RAD90);
		}
		else {
			a = (a + RAD22_5) % PApplet.TWO_PI;
			newState = (int)(a / RAD45);
		}
		return newState % 8;
	}

	public void mouseEvent(MouseEvent event){
		if(!visible || !enabled || !available) return;

		calcTransformedOrigin(winApp.mouseX, winApp.mouseY);
		currSpot = whichHotSpot(ox, oy);
		// Move ox and oy relative to the centre of the stick
		ox -= width/2;
		oy -= height/2;

		// currSpot == 1 for text display area
		if(currSpot >= 0 || focusIsWith == this)
			cursorIsOver = this;
		else if(cursorIsOver == this)
			cursorIsOver = null;

		switch(event.getAction()){
		case MouseEvent.PRESS:
			if(focusIsWith != this && currSpot > -1 && z > focusObjectZ()){
				status = PRESS_CONTROL;
				position = getPositionFromAngle(calcStickAngle(ox, oy));
				dragging = false;
				takeFocus();
				bufferInvalid = true;
			}
			break;
		case MouseEvent.RELEASE:
			if(focusIsWith == this){
				loseFocus(null);
			}
			// Correct for sticky ticks if needed
			if(position != -1){
				position = -1;
				fireEvent(this, GEvent.CHANGED);
			}
			hotspots[0].adjust(width/2, height/2);
			dragging = false;
			status = OFF_CONTROL;
			bufferInvalid = true;
			break;
		case MouseEvent.DRAG:
			if(focusIsWith == this){
				dragging = true;
				float offset = PApplet.sqrt(ox*ox + oy*oy);
				int newPosition = -1;
				if(offset >= actionRad){
					newPosition = getPositionFromAngle(calcStickAngle(ox, oy));
				}
				if(offset > actionRadLimit){
					float stickAngle = calcStickAngle(ox, oy);
					ox = actionRadLimit * PApplet.cos(stickAngle);
					oy = actionRadLimit * PApplet.sin(stickAngle);
				}
				hotspots[0].adjust(ox + width/2, oy + height/2);	
				if(newPosition != position){
					position = newPosition;
					fireEvent(this, GEvent.CHANGED);
				}
				bufferInvalid = true;
			}
			break;
		case MouseEvent.MOVE:
			int currStatus = status;
			// If dragged state will stay as PRESSED
			if(currSpot == 1)
				status = OVER_CONTROL;
			else
				status = OFF_CONTROL;
			if(currStatus != status)
				bufferInvalid = true;
			break;			
		}
	}
	
	public void draw(){
		if(!visible) return;

		// Update buffer if invalid
		updateBuffer();
		winApp.pushStyle();

		winApp.pushMatrix();
		// Perform the rotation
		winApp.translate(cx, cy);
		winApp.rotate(rotAngle);
		// Move matrix to line up with top-left corner
		winApp.translate(-halfWidth, -halfHeight);
		// Draw buffer
		winApp.imageMode(PApplet.CORNER);
		if(alphaLevel < 255)
			winApp.tint(TINT_FOR_ALPHA, alphaLevel);
		winApp.image(buffer, 0, 0);	
		winApp.popMatrix();

		winApp.popStyle();
	}

	
	protected void updateBuffer(){
		if(bufferInvalid) {
			Graphics2D g2d = buffer.g2;
			bufferInvalid = false;
			buffer.beginDraw();
			// Back ground colour
			if(opaque == true)
				buffer.background(palette[BACK]);
			else
				buffer.background(buffer.color(255,0));
			// Move origin to centre

			buffer.translate(width/2, height/2);

			buffer.fill(palette[OUTERRING]);
			buffer.stroke(palette[BORDERS]);
			buffer.strokeWeight(1.0f);
			buffer.ellipse(0,0,2*ledRingRad, 2*ledRingRad);
			buffer.ellipse(0,0,2*actionRad, 2*actionRad);
			
			buffer.pushMatrix();
			int led = 0x00000001, delta = 2/mode;
			for(int i = 0; i < 8; i += delta){
				if(i%2 == 0){
					buffer.stroke(palette[BORDERS]);
					buffer.strokeWeight(1.0f);
					buffer.line(0,0,ledRingRad,0);
				}
				buffer.noStroke();
				if(position >= 0 && (posMap[position] & led) == led)
					buffer.fill(palette[LED_ACTIVE]);
				else
					buffer.fill(palette[LED_INACTIVE]);
				buffer.ellipse(ledRingRad,0,ledHeight,ledWidth);

				led <<= delta;
				buffer.rotate(delta * RAD45);
			}
			buffer.popMatrix();
			
			buffer.fill(palette[ACTIONRING]);
			buffer.stroke(palette[BORDERS]);
			buffer.strokeWeight(1.0f);
			buffer.ellipse(0,0,2*actionRad, 2*actionRad);

			buffer.strokeWeight(1);
			buffer.stroke(palette[2]);
			buffer.fill(palette[STICK_TOP_DRAG]);
			// Draw thumb
			switch(status){
			case OFF_CONTROL:
				buffer.fill(palette[STICK_TOP]);
				break;
			case OVER_CONTROL:
				buffer.fill(palette[STICK_TOP_OVER]);
				break;
			case PRESS_CONTROL:
				buffer.fill(palette[STICK_TOP_DRAG]);
				break;
			}

			buffer.ellipse(hotspots[0].x - width/2,hotspots[0].y - height/2,2*gripRadius, 2*gripRadius);

			buffer.endDraw();
		}	
	}
}
