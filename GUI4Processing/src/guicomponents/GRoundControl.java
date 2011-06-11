/*
  Part of the GUI for Processing library 
  	http://www.lagers.org.uk/g4p/index.html
	http://gui4processing.googlecode.com/svn/trunk/

  Copyright (c) 2011 Peter Lager

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
 * This is an abstract class that provides the basic functionality for 'round controls'
 * such as knobs. <br>
 * Round components include both circular and oval components. This class provides basic 
 * functionality (including mouse event handling) for circular components. <br>
 *  
 * @author Peter Lager
 *
 */
public abstract class GRoundControl extends GComponent {

	protected int cx,cy;

	protected float start,end;
	protected int halfWidth, halfHeight;

	protected boolean strictOver = false;

	/*	 
	 * These are the values of the start angle and end angle used to define
	 *  the limits of the clockwise rotation range. They are adjusted such 
	 *  that aLow < aHigh. If start angle > end angle then aLow is calculated as 
	 *  the equivalent negative rotation i.e. 
	 *  start angle = 110 and end angle = 70 then 
	 *  aLow = 360 - start angle = 360 - 110 = -250
	 */	 
	protected int aLow, aHigh;

	// Used to indicate if the arc goes over the 0 (east) position
	public boolean wrap0;

	// These angles are adjusted to be in range aLow to aHigh
	// this is updated in pre() and is used to calculate the current value
	// These angle can be in the range -360 - +360
	public int needleAngle, lastTargetNeedleAngle, targetNeedleAngle;
	protected int needleDir;

	// These angles are adjusted to be in the range 0-360
	protected int mouseAngle, lastMouseAngle;
	// When the mouse is pressed this measures the difference between
	// mouseAngle and targetNeedleAngle preventing discontinuous jumps
	// in the knob value. Offset is adjusted when the targetNeedleAngle
	// is stopped at either end of the slider again to prevent
	// discontinuous movement of the needle
	protected int offset;

	/*
	These represent the range of values that will be returned by the
	control. If start < end then the value will increase with
	clockwise rotation but if start>= end then the value increases 
	with counter-clockwise rotation.
	The control remembers which which the boolean.
	value = current value of the control
	lastValue = last value of the control
	the difference between these can be used to indicate the direction
	the user is attempting to rotate the control
	 */
	protected float valueStart = 20, valueEnd = 270;
	protected float value = 300;
	protected boolean isValueChanging;
	protected boolean clockwiseValues = (valueStart < valueEnd);

	// Provides inertia for the needle thereby smoothing the needle 
	// during rapid mouse movement. This value must be >= 1 although
	// there is no maximum value, values over 20 do not increase the
	// visual effect. A value of 1 means no inertia.s
	protected int needleInertia = 1;

	protected int mode = CTRL_ANGULAR;
	protected float sensitivity = 1.0f;
	protected int startMouseX, startMouseY;

	/**
	 * This constructor should be called by the appropriate child class constructor
	 */
	public GRoundControl(PApplet theApplet, int x, int y, int width, int height, int arcStart, int arcEnd) {
		super(theApplet, x, y);
		this.width = (width < 20) ? 20 : width;
		this.height = (height < 20) ? 20 : height;
		halfWidth = cx =this.width/2;
		halfHeight = cy = this.height/2;

		aLow = getValidArcAngle(arcStart);
		aHigh = getValidArcAngle(arcEnd);
		wrap0 = (arcStart > arcEnd);

		aLow = (aLow >= aHigh) ? aLow - 360 : aLow;

		start = PApplet.radians(aLow);
		end = PApplet.radians(aHigh);

		z= Z_SLIPPY;

		registerAutos_DMPK(true, true, true, false);
		setLimits(50,0,100);
	}

	/**
	 * This constructor should be called by the appropriate child class constructor
	 */
	public GRoundControl(PApplet theApplet, int x, int y, int size, int arcStart, int arcEnd) {
		this(theApplet, x, y, size, size, arcStart, arcEnd);
	}

//	/**
//	 * Determines whether the position ax, ay is over the round control
//	 * of this Slider.
//	 * 
//	 * @return true if mouse is over the slider thumb else false
//	 */
//	public boolean isOver(int ax, int ay){
//		Point p = new Point(0,0);
//		calcAbsPosition(p);
//		boolean inside;
//		int dx = ax - p.x - cx;
//		int dy = ay - p.y - cy;
//		if(width == height)
//			inside = (dx * dx  + dy * dy < width * width /4);
//		else {	// Elliptical knob
//			float ratioX = (2.0f * dx)/ width;
//			float ratioY = (2.0f * dy)/ height;
//			inside = (ratioX * ratioX + ratioY * ratioY < 1.0f);
//		}
//		return inside;
//	}
//
//	public boolean isOverStrict(int ax, int ay){
//		Point p = new Point(0,0);
//		calcAbsPosition(p);
//		p.x += cx;
//		p.y += cx;
//		boolean inside = false;
//		int dx = ax - p.x;
//		int dy = ay - p.y;
//		if(width == height)
//			inside = (dx * dx  + dy * dy < width * width /4);
//		else {	// Elliptical knob
//			float ratioX = (2.0f * dx)/ width;
//			float ratioY = (2.0f * dy)/ height;
//			inside = (ratioX * ratioX + ratioY * ratioY < 1.0f);
//		}
//		if(inside){
//			int degs = getAngleFromXY(p, ax, ay);
//			degs = (degs < 0) ? degs + 360 : degs;
//			inside = isInValidArc(degs);
//		}
//		return inside;
//	}

	/**
	 * Used to implement inertia
	 */
	public void pre(){
		int change, inertia = needleInertia;
		if(needleAngle == targetNeedleAngle){
			isValueChanging = false;
			needleDir = 0;
		}
		else {
			// Make sure we get a change value by repeatedly decreasing the inertia value
			do {
				change = (targetNeedleAngle - needleAngle)/inertia;
				inertia--;
			} while (change == 0 && inertia > 0);
			// If there is a change update the current value and generate an event
			needleDir = signInt(change);
			if(change != 0){
				needleAngle += change;
				isValueChanging = true;
				fireEvent();
			}
			else
				isValueChanging = false;
		}			
	}

	/**
	 * Basic mouse event handler for circular components.
	 */
	public void mouseEvent(MouseEvent event){
		if(!visible  || !enabled) return;

		// Calculate absolute position of centre of rotation
		Point p = new Point(0,0);
		calcAbsPosition(p);
		p.x += cx;
		p.y += cy;

		int degs = 0;

		boolean mouseOver;
		if(strictOver)
			mouseOver = isOverStrict(winApp.mouseX, winApp.mouseY);
		else
			mouseOver = isOver(winApp.mouseX, winApp.mouseY);

//s		System.out.println("Strict "+strictOver + "    " + mouseOver);
//		if(mouseOver && strictOver){
//			degs = getAngleFromUser(p);
//			mouseOver &= isInValidArc(degs);
//		}

		if(mouseOver || focusIsWith == this)
			cursorIsOver = this;
		else if(cursorIsOver == this)
			cursorIsOver = null;


		switch(event.getID()){
		case MouseEvent.MOUSE_PRESSED:
			if(focusIsWith != this && mouseOver && z > focusObjectZ()){
				startMouseX = winApp.mouseX - p.x;
				startMouseY = winApp.mouseY - p.y;
				degs = getAngleFromUser(p);
					lastMouseAngle = mouseAngle = (degs < 0) ? degs + 360 : degs;
					offset = targetNeedleAngle - mouseAngle;
					takeFocus();

			}
			break;
		case MouseEvent.MOUSE_RELEASED:
			if(focusIsWith == this){
				loseFocus(null);
			}
			lastTargetNeedleAngle = targetNeedleAngle;	
			break;
		case MouseEvent.MOUSE_CLICKED:
			break;
		case MouseEvent.MOUSE_DRAGGED:
			if(focusIsWith == this){
				degs = getAngleFromUser(p);
				mouseAngle = (degs < 0) ? degs + 360 : degs;
				if(mouseAngle != lastMouseAngle){
					int deltaMangle = mouseAngle - lastMouseAngle;
					// correct when we go over zero degree position
					if(deltaMangle < -180)
						deltaMangle += 360;
					else if(deltaMangle > 180)
						deltaMangle -= 360;
					// Calculate and adjust new needle angle so it is in the range aLow >>> aHigh
					targetNeedleAngle = PApplet.constrain(targetNeedleAngle + deltaMangle, aLow, aHigh);
					// Update offset for use with angular mouse control
					offset += (targetNeedleAngle - lastTargetNeedleAngle - deltaMangle);
					// Remember target needle and mouse angles
					lastTargetNeedleAngle = targetNeedleAngle;
					lastMouseAngle = mouseAngle;
				}
			}
			break;
		}
	}

	public abstract boolean isOverStrict(int mouseX, int mouseY);

	/**
	 * Calculates the 'angle' from the current mouse position based on the type
	 * of 'controller' set.
	 * @param p the absolute pixel position for the control centre
	 * @return the unconstrained angle
	 */
	protected int getAngleFromUser(Point p){
		int degs = 0;
		switch(mode){
		case CTRL_ANGULAR:
			degs = Math.round(PApplet.degrees((float)Math.atan2(winApp.mouseY - p.y, winApp.mouseX - p.x)));
			degs = (degs < 0) ? degs + 360 : degs;
			break;
		case CTRL_HORIZONTAL:
			degs = (int) (sensitivity * (winApp.mouseX - p.x - startMouseX));
			break;
		case CTRL_VERTICAL:
			degs = (int) (sensitivity * (winApp.mouseY - p.y - startMouseY));
			break;
		}
		return degs;
	}

	/**
	 * Get the angle from a position
	 * @param p the absolute pixel position for the control centre
	 * @param x
	 * @param y
	 * @return
	 */
	protected int getAngleFromXY(Point p, float x, float y){
		return Math.round(PApplet.degrees((float)Math.atan2(y - p.y, x - p.x)));
	}

	/**
	 * Is the value changing as a result of the knob being rotated  
	 * with the mouse.
	 * 
	 * @return true if value being changed at GUI
	 */
	public boolean isValueChanging(){
		return this.isValueChanging;
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
		this.mode = mode;
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
		this.sensitivity = sensitivity;
	}

	/**
	 * See if the 'strict over' option is set
	 * @return the strictOver
	 */
	public boolean isStrictOver() {
		return strictOver;
	}

	/**
	 * If this is set to false (the default value) then the mouse button 
	 * can be pressed over any part of the knob and bzeel  to start rotating
	 * the knob. If it is true then only that portion of the knob within
	 * the rotation arc.
	 * 
	 * param strict the strictOver to set
	 */
	public void setStrictOver(boolean strict) {
		this.strictOver = strict;
	}

	/**
	 * Get the current value represented by the control as a floating point value.
	 * @return current float value
	 */
	public float getValuef(){
		if(clockwiseValues)
			return PApplet.map(needleAngle, aLow, aHigh, valueStart, valueEnd);
		else
			return -PApplet.map(needleAngle, aLow, aHigh, -valueStart, -valueEnd);
	}

	/**
	 * Get the current value represented by the control as an integer value.
	 * @return current integer value
	 */
	public int getValue(){
		return Math.round(getValuef());
	}

	/**
	 * Set the range of values that are to be returned by this control. <br>
	 * 
	 * 
	 * @param init initial value of control
	 * @param start value matching the start rotation
	 * @param end  value matching the start rotation (Values < start are acceptable)
	 */
	public void setLimits(float init, float start, float end)
	{
		valueStart = start;
		valueEnd = end;
		clockwiseValues = (start < end);
		setValue(init, true);
	}

	/**
	 * Set the current value of the control
	 * @param newValue the value to use will be constrained to legal vales.
	 */
	public void setValue(float newValue){
		if(clockwiseValues){
			newValue = PApplet.constrain(newValue, valueStart, valueEnd);
		}
		else {
			newValue = PApplet.constrain(newValue, valueEnd, valueStart);
		}
		targetNeedleAngle = getAngleFromValue(newValue);
	}

	/**
	 * The same as setValue(newValue) except the second parameter determines 
	 * whether we should ignore any inertia value so the affect is immediate. <br>
	 * <b>Note</b> if false then events will continue to be generated so if this
	 * causes unexpected behaviour then use setValue(newValue, true)
	 * 
	 * @param newValue the value we wish the slider to become
	 * @param ignoreInteria if true change is immediate
	 */
	public void setValue(float newValue,  boolean ignoreInteria){
		setValue(newValue);
		if(ignoreInteria){
			needleAngle = targetNeedleAngle;
		}
	}

	/**
	 * Calculate the equivalent needle angle for a given value.
	 * 
	 * @param value a valid value for the knob range
	 * @return the angle that is equivalent to the given vale;
	 */
	public int getAngleFromValue(float value){
		int angle;
		if(clockwiseValues){
			angle = (int) PApplet.map(value, valueStart, valueEnd, aLow, aHigh);
		}
		else {
			angle = (int) PApplet.map(-value, -valueStart, -valueEnd, aLow, aHigh);
		}
		return angle;
	}

	/**
	 * Confirm if the angle is within the limits of rotation. <br>
	 * 
	 * @param angle must be in range 0-360 
	 * @return true if angle is within rotation angle range
	 */
	public boolean isInValidArc(int angle){
		System.out.println(aLow + "  >>  "+ aHigh + "      " + angle);
		return (aLow < 0) ? (angle >= 360 + aLow || angle <= aHigh) : (angle >= aLow && angle <= aHigh);
	}

	/**
	 * Convert the angle to a positive value in the range 0 - 359
	 * without altering its 'slope' <br>
	 * Only used to validate initial values
	 * @param angle must be in range 0-360 
	 */
	protected int getValidArcAngle(int angle){
		while(angle < 0) angle+=360;
		while(angle > 360) angle-=360;
		return angle;
	}

	/**
	 * Get the sign of a number similar to signNum but returns zero when the number is 0
	 * rather than 1.
	 * 
	 * @param n
	 * @return 
	 */
	protected int signInt(int n){
		return (n == 0) ? 0 : (n < 0) ? -1 : +1;
	}
	
	/**
	 * Takes a real angle and calculates the angle to be used when
	 * drawing an arc so that they match up.
	 * @param ra the real world angle
	 * @return the angle for the arc method.
	 */
	public float convertRealAngleToOval(double ra, float rX, float rY){
		double cosA = Math.cos(ra), sinA = Math.sin(ra);
		double h = Math.abs(rX - rY)/2.0;
		double eX = rX * cosA, eY = rY * sinA;

		if(rX > rY){
			eX -= h * cosA;
			eY += h * sinA;
		}
		else {
			eX += h * cosA;
			eY -= h * sinA;
		}
		float angle = (float) Math.atan2(eY, eX);
		while(ra - angle >= PI)
			angle += TWO_PI;
		while(angle - ra >= PI)
			angle -= TWO_PI;
		return angle;
	}

	/**
	 * Calculates the point of intersection between the circumference of an ellipse and a line from
	 * position xp,yp to the geometric centre of the ellipse.
	 * @param circPos the returned intersection point
	 * @param xp x coordinate of point
	 * @param yp y coordinate of point
	 * @param rX half width of ellipse
	 * @param rY half height of ellipse
	 */
	protected void calcCircumferencePosition(Point circPos, float xp, float yp, float rX, float rY){
		double numer, denom;
		numer = rX * rY;
		denom = (float) Math.sqrt(rX*rX*yp*yp + rY*rY*xp*xp);
		circPos.x = (int) Math.round(xp * numer / denom);
		circPos.y = (int) Math.round(yp * numer / denom);
	}
	
	
	public float getRealAngleFromOvalPosition(float ox, float oy, float rX, float rY){
//		double cosA = Math.cos(da), sinA = Math.sin(da);
		float h = Math.abs(rX - rY)/2.0f;
//		double r = (bezelRadX + bezelRadY)/2.0;
//		double rX = r * cosA, rY = r * sinA;

		Point p = new Point();
		calcCircumferencePosition(p, ox, oy, rX, rY);
		
		float roX = p.x, roY = p.y;
		float da = (float) Math.atan2(roY, roX);
		float cosA = (float) Math.cos(da), sinA = (float) Math.sin(da);
		if(rX > rY){
			roX += h * cosA;
			roY -= h * sinA;
		}
		else {
			roX -= h * cosA;
			roY += h * sinA;
		}
		float angle = (float) Math.atan2(roY, roX);
		while(da - angle >= PI)
			angle += TWO_PI;
		while(angle - da >= PI)
			angle -= TWO_PI;
		return angle;
	}
}
