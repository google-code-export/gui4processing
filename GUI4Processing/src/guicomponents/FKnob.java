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

import guicomponents.HotSpot.HSarc;

import java.awt.event.MouseEvent;

import processing.core.PApplet;
import processing.core.PGraphicsJava2D;


/**
 * The provides an extremely configurable GUI knob controller. GKnob
 * inherits from GValueControl so you should read the documentation 
 * for that class as it also applies to GKnob. <br><br>
 * 
 * Configurable options <br>
 *  Knob size but it must be circular <br>
 *  Start and end of rotation arc. <br>
 *  Bezel width with tick marks <br>
 *  User defined value limits (i.e. the range of values returned <br>
 *  <br>
 *  Range of values associated with rotating the knob <br>
 *  Rotation is controlled by mouse movement -  3 modes available <br>
 *  (a) angular -  drag round knob center <br>
 *  (b) horizontal - drag left or right <br>
 *  (c) vertical - drag up or down <br>
 *  User can specify mouse sensitivity for modes (b) and (c)
 *  Use can specify easing to give smoother rotation
 *  
 * 	<b>Note</b>: Angles are measured clockwise starting in the positive x direction i.e.
 * <pre>
 *         270
 *          |
 *    180 --+-- 0
 *          |
 *          90
 * </pre>
 * 
 * @author Peter Lager
 *
 */
public class FKnob extends FValueControl {


	protected float startAng = 110, endAng = 70;
	//	protected float needleAngle = 270;

	protected int mode = CTRL_HORIZONTAL;

	protected boolean showTrack = true;

	protected float bezelRadius, bezelWidth, gripRadius;
	protected float inset = 0;
	protected float sensitivity = 1.0f;

	protected boolean showArcOnly = true;
	protected boolean mouseOverArcOnly = false;

	protected float startMouseX, startMouseY;
	protected float lastMouseAngle, mouseAngle;

	//	protected float targetNeedleAngle, targetNeedleValue, lastTargetNeedleAngle;

	// corresponds to target and current values
	//				valueTarget valuePos
	protected float angleTarget, anglePos;
	protected float lastAngleTarget;

	public FKnob(PApplet theApplet, float p0, float p1, float p2, float p3, float borderInset) {
		super(theApplet, p0, p1, p2, p3);
		this.inset = borderInset;
		bezelRadius = Math.min(width, height) / 2 - inset;
		bezelWidth = 10;
		gripRadius = bezelRadius - bezelWidth;
		buffer = (PGraphicsJava2D) winApp.createGraphics((int)width, (int)height, PApplet.JAVA2D);
		setKnobRange(startAng, endAng);
		// Should not really need theese here???
		//		valueTarget = 0.9f; easing = 200;
		anglePos = scaleValueToAngle(valuePos);
		lastAngleTarget = angleTarget = scaleValueToAngle(valueTarget);

		System.out.println(startAng + "  " + endAng + "    " +anglePos);

		hotspots = new HotSpot[]{
				new HSarc(1, width/2 , height/2, gripRadius, startAng, endAng)  // over grip
		};
		//		opaque = false;
		z = Z_SLIPPY;

		epsilon = 0.98f / (endAng - startAng);

		//		ssStartLimit = new StyledString(buffer.g2, "0.00");
		//		ssEndLimit = new StyledString(buffer.g2, "1.00");
		//		ssValue = new StyledString(buffer.g2, "0.50");

		// Now register control with applet
		createEventHandler(winApp, "handleSliderEvents", new Class[]{ FValueControl.class, boolean.class });
		registeredMethods = PRE_METHOD | DRAW_METHOD | MOUSE_METHOD ;
		F4P.addControl(this);

	}

	public void mouseEvent(MouseEvent event){
		if(!visible || !enabled || !available) return;

		calcTransformedOrigin(winApp.mouseX, winApp.mouseY);
		currSpot = whichHotSpot(ox, oy);
		// Normalise ox and oy to the centre of the knob
		ox -= width/2;
		oy -= height/2;

		//		System.out.println(currSpot);
		// currSpot == 1 for text display area
		if(currSpot >= 0 || focusIsWith == this)
			cursorIsOver = this;
		else if(cursorIsOver == this)
			cursorIsOver = null;

		switch(event.getID()){
		case MouseEvent.MOUSE_PRESSED:
			//			System.out.println("P " + focusIsWith);
			if(focusIsWith != this && currSpot > -1 && z > focusObjectZ()){
				startMouseX = ox;
				startMouseY = oy;
				lastMouseAngle = mouseAngle = getAngleFromUser(ox, oy);
//				System.out.println("FKnob mouse pressed " + mouseAngle);
				offset = scaleValueToAngle(valueTarget) - mouseAngle;
				takeFocus();
			}
			break;
		case MouseEvent.MOUSE_CLICKED:
			//			System.out.println("C " + focusIsWith);
			if(focusIsWith == this ){
				//				System.out.println("CLICKED " + currSpot );
			}
			break;
		case MouseEvent.MOUSE_RELEASED:
			//			System.out.println("R " + focusIsWith);
			if(focusIsWith == this){
				loseFocus(null);
			}
			// Correct for sticky ticks if needed
			if(stickToTicks)
				valueTarget = findNearestTickValueTo(valueTarget);
//			lastAngleTarget = angleTarget = scaleValueToAngle(valueTarget);
			//				System.out.println("RELEASED 1 " );
			dragging = false;
			break;
		case MouseEvent.MOUSE_DRAGGED:
			if(focusIsWith == this){
				mouseAngle = getAngleFromUser(ox, oy);
				if(mouseAngle != lastMouseAngle){
					float deltaMangle = mouseAngle - lastMouseAngle;
					// correct when we go over zero degree position
					if(deltaMangle < -180)
						deltaMangle += 360;
					else if(deltaMangle > 180)
						deltaMangle -= 360;
//					System.out.println("FKnob mouse dragged " + mouseAngle + "  delta mouse angle " + deltaMangle);
					// Calculate and adjust new needle angle so it is in the range aLow >>> aHigh
					angleTarget = constrainToKnobRange(angleTarget + deltaMangle);
					valueTarget = calcAngletoValue(angleTarget);
					// Update offset for use with angular mouse control
					offset += (angleTarget - lastAngleTarget - deltaMangle);
					// Remember target needle and mouse angles
					lastAngleTarget = angleTarget;
					lastMouseAngle = mouseAngle;
				}
				isValueChanging = true;
			}
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
		winApp.pushMatrix();
		// Move matrix to line up with top-left corner
		winApp.translate(-halfWidth, -halfHeight);
		// Draw buffer
		winApp.imageMode(PApplet.CORNER);
		if(alphaLevel < 255)
			winApp.tint(-1, alphaLevel);
		winApp.image(buffer, 0, 0);	
		winApp.popMatrix();
		// Value labels
		if(children != null){
			for(FAbstractControl c : children)
				c.draw();

		}
		winApp.popMatrix();

		winApp.popStyle();
	}

	protected void updateBuffer(){
		double a, sina, cosa;
		float tickLength;
		if(bufferInvalid) {
			// If the value is still changing recalculate the needle angle
			if(isValueChanging)
				anglePos = scaleValueToAngle(valuePos);
			bufferInvalid = false;
			buffer.beginDraw();
			buffer.ellipseMode(PApplet.CENTER);
			// Back ground colour
			// Back ground colour
			if(opaque == true)
				buffer.background(palette[6]);
			else
				buffer.background(buffer.color(255,0));
			buffer.translate(width/2, height/2);
			buffer.noStroke();

			if(bezelWidth > 0){
				// Draw bezel, track,  ticks etc
				buffer.noStroke();
				buffer.fill(palette[5]);
				if(showArcOnly)
					buffer.arc(0,0,2*bezelRadius, 2*bezelRadius, PApplet.radians(startAng), PApplet.radians(endAng));
				else
					buffer.ellipse(0,0,2*bezelRadius, 2*bezelRadius);
				// Since we have a bezel test for ticks
				buffer.noFill();
				buffer.strokeWeight(1.6f);
				buffer.stroke(palette[3]);
				float deltaA = (endAng - startAng)/(nbrTicks - 1);
				for(int t = 0; t < nbrTicks; t++){
					tickLength = gripRadius + ((t == 0 || t == nbrTicks - 1) ? bezelWidth : bezelWidth * 0.8f); 
					a =  Math.toRadians(startAng + t * deltaA);
					sina = Math.sin(a);
					cosa = Math.cos(a);
					buffer.line((float)(gripRadius * cosa), (float)(gripRadius * sina), (float)(tickLength * cosa), (float)(tickLength * sina));
				}
				// draw track?
				if(showTrack){
					buffer.noStroke();
					buffer.fill(palette[14]);
					buffer.arc(0,0, 2*(gripRadius + bezelWidth * 0.3f), 2*(gripRadius + bezelWidth * 0.3f), PApplet.radians(startAng), PApplet.radians(anglePos));					
				}
			}

			// draw grip (inner) part of knob
			buffer.strokeWeight(1.6f);
			buffer.stroke(palette[14]);
			buffer.fill(palette[2]);
			if(showArcOnly)
				buffer.arc(0,0,2*gripRadius, 2*gripRadius, PApplet.radians(startAng), PApplet.radians(endAng));
			else
				buffer.ellipse(0,0,2*gripRadius, 2*gripRadius);

			// Draw needle
			buffer.noFill();
			buffer.stroke(palette[14]);
			buffer.strokeWeight(3);
			a = Math.toRadians(anglePos);
			sina = Math.sin(a);
			cosa = Math.cos(a);
			buffer.line(0, 0, (float)(gripRadius * cosa), (float)(gripRadius * sina));
			buffer.endDraw();
		}
	}

	/**
	 * Get the current mouse controller mode possible values are <br>
	 * GKnob.CTRL_ANGULAR or GKnob.CTRL_HORIZONTAL) orGKnob.CTRL_VERTICAL
	 * @return the mode
	 */
	public int getControlMode() {
		return mode;
	}

	/**
	 * Set the mouse control mode to use, acceptable values are <br>
	 * GKnob.CTRL_ANGULAR or GKnob.CTRL_HORIZONTAL) orGKnob.CTRL_VERTICAL
	 * @param mode the mode to set
	 */
	public void setControlMode(int mode) {
		switch(mode){
		case CTRL_ANGULAR:
		case CTRL_HORIZONTAL:
		case CTRL_VERTICAL:
			this.mode = mode;			
		}
	}

	/**
	 * This gets the sensitivity to be used in modes CTRL_HORIZONTAL and CTRL_VERTICAL
	 * @return the sensitivity
	 */
	public float getSensitivity() {
		return sensitivity;
	}

	/**
	 * This gets the sensitivity to be used in modes CTRL_HORIZONTAL and CTRL_VERTICAL <br>
	 * A value of 1 is 1 degree per pixel and a value of 2 is 2 degrees per pixel. <br>
	 * @param sensitivity the sensitivity to set
	 */
	public void setSensitivity(float sensitivity) {
		this.sensitivity = (sensitivity < 0.1f) ? 0.1f : sensitivity;
	}

	
	/**
	 * Calculates the 'angle' from the current mouse position based on the type
	 * of 'controller' set.
	 * @param px the distance from the knob centre in the x direction
	 * @param py the distance from the knob centre in the y direction
	 * @return the unconstrained angle
	 */
	protected float getAngleFromUser(float px, float py){
		float degs = 0;
		switch(mode){
		case CTRL_ANGULAR:
			degs = calcRealAngleFromXY(ox, oy);
			break;
		case CTRL_HORIZONTAL:
			degs = sensitivity * (px - startMouseX);
			break;
		case CTRL_VERTICAL:
			degs = sensitivity * (py - startMouseY);
			break;
		}
		return degs;
	}

	/**
	 * For a particular normalised value calculate the angle (degrees)
	 * 
	 * @param v
	 * @return
	 */
	protected float scaleValueToAngle(float v){
		float a = startAng + v * (endAng - startAng);
		return a;		
	}

	/**
	 * Calculates the knob angle based on the normalised value.
	 * 
	 * @param a
	 */
	protected float calcAngletoValue(float a){
		if(a < startAng)
			a += 360;
		float v = (a - startAng) / (endAng - startAng);
		return v;
	}

	/**
	 * Set the limits for the range of valid rotation angles for the knob.
	 * 
	 * @param start the range start angle in degrees
	 * @param end the range end angle in degrees
	 */
	public void setKnobRange(float start, float end){
		start = constrain360(start);
		end = constrain360(end);
		startAng = start;
		endAng = (startAng >= end) ? end + 360 : end;
	}

	/**
	 * Determines whether an angle is within the knob
	 * rotation range.
	 * @param a the angle in degrees
	 * @return
	 */
	protected boolean isInKnobRange(float a){
		a = constrain360(a);
		if(a < startAng)
			a += 360;
		return (a >= startAng && a <= endAng);
	}

	protected float constrainToKnobRange(float a){
		if(a < startAng)
			a = startAng;
		else if(a > endAng)
			a = endAng;
		return a;
	}

	protected float constrain360(float a){
		while(a < 0)
			a += 360;
		while(a > 360)
			a -= 360;
		return a;
	}

	protected float calcRealAngleFromXY(float px, float py){
		float a = (float) Math.toDegrees(Math.atan2(py, px));
		if(a < 0)
			a += 360;
		return a;	
	}
}
