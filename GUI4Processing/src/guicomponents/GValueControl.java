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

import processing.core.PApplet;

/**
 * Base class for all slider and knob type controls.
 * 
 * This class enables the creation of tick marks and constraining values to 
 * the tick mark values. <br>
 * 
 * It also controls how the values are to be displayed INTEGER, DECIMAL or EXPONENT
 * 
 * @author Peter Lager
 *
 */
public class GValueControl extends GAbstractControl {

	static protected float HINSET = 2;

	protected float startLimit = 0, endLimit = 1;
	protected boolean showLimits = true;
	
	protected StyledString ssStartLimit, ssEndLimit, ssValue;

	protected int valueType = DECIMAL;
	protected int precision = 2;
	protected String unit = "";
	protected boolean showValue = true;
	
	protected float epsilon = 0.01f;
	
	protected float valuePos = 0.5f, valueTarget = 0.5f;
	protected boolean isValueChanging  = false;
	protected float easing  = 1.0f; // must be >= 1.0
	

	protected int nbrTicks = 2;
	protected boolean stickToTicks = false;
	protected boolean showTicks = true;					//  make false for final release
	
	protected boolean limitsInvalid = true;
	
	// Offset to between mouse and thumb centre
	protected float offset;
	
	// Can be used to prevent changes
	protected boolean fixed = false;
	
	public GValueControl(PApplet theApplet, float p0, float p1, float p2, float p3) {
		super(theApplet, p0, p1, p2, p3);
	}
	
	public void pre(){
		if(Math.abs(valueTarget - valuePos) > epsilon){
			valuePos += (valueTarget - valuePos) / easing;
			isValueChanging = bufferInvalid = true;
			fireEvent(this, isValueChanging);
		}
		else {
			valuePos = valueTarget;
			if(isValueChanging){
				bufferInvalid = true;
				isValueChanging = false;
				fireEvent(this, isValueChanging);
			}
		}
	}
	
	/**
	 * Used to format the number into a string for display.
	 * @param number
	 * @return
	 */
	protected String getNumericDisplayString(float number){
		String s = "";
		switch(valueType){
		case INTEGER:
			s = String.format("%d %s", Math.round(number), unit);
			break;
		case DECIMAL:
			s = String.format("%." + precision + "f %s", number, unit);
			break;
		case EXPONENT:
			s = String.format("%." + precision + "f %s", number, unit);
			break;
		}
		return s.trim();
	}
	
	
	/**
	 * Sets the range of values to be returned. This method will
	 * assume that you want to set the valueType to INTEGER
	 * 
	 * @param start
	 * @param end
	 */
	public void setLimits(int start, int end){
		if(fixed) return;
		startLimit = start;
		endLimit = end;
		valueType = INTEGER;
		limitsInvalid = true;
		bufferInvalid = true;
	}
	
	/**
	 * Sets the range of values to be returned. This method will
	 * assume that you want to set the valueType to DECIMAL
	 * 
	 * @param start
	 * @param end
	 */
	public void setLimits(float start, float end){
		if(fixed) return;
		startLimit = start;
		endLimit = end;
		if(valueType == INTEGER){
			valueType = DECIMAL;
			if(precision == 0)
				precision = 1;
		}
		limitsInvalid = true;
		bufferInvalid = true;
	}
	
	/**
	 * Set the value for the slider. <br>
	 * The user must ensure that the value is valid for the slider range.
	 * @param v
	 */
	public void setValue(float v){
		if(valueType == INTEGER)
			v = Math.round(v);
		float p = (v - startLimit) / (endLimit - startLimit);
		p = PApplet.constrain(p, 0.0f, 1.0f);
		if(p < 0)
			p = 0;
		else if(p > 1)
			p = 1;
		if(stickToTicks)
			p = findNearestTickValueTo(p);
		valueTarget = p;
	}
	
	/**
	 * For DECIMAL values this sets the number of decimal places to 
	 * be displayed.
	 * @param p must be >= 1 otherwise will use 1
	 */
	public void setPrecision(int p){
		if(fixed) return;
		if(p < 1)
			p = 1;
		if(p != precision){
			precision = p;
			limitsInvalid = true;
			bufferInvalid = true;
		}
	}
	
	/**
	 * The units to be displayed with the current and limit values e.g.
	 * kg, m, ($), fps etc. <br>
	 * Do not use long labels such as 'miles per hour' as these take a
	 * lot of space and can look messy.
	 *  
	 * @param units for example  kg, m, ($), fps
	 */
	public void setUnits(String units){
		if(fixed) return;
		if(units == null)
			units = "";
		if(!unit.equals(units)){
			unit = units;
			limitsInvalid = true;
			bufferInvalid = true;			
		}
	}
	
	/**
	 * Set the numberFormat, precision and units in one go. <br>
	 * Valid number formats are INTEGER, DECIMAL, EXPONENT <br>
	 * Precision must be >= 1 and is ignored for INTEGER.
	 * 
	 * @param numberFormat INTEGER, DECIMAL or EXPONENT
	 * @param precision must be >= 1
	 * @param units for example  kg, m, ($), fps
	 */
	public void setNumberFormat(int numberFormat, int precision, String unit){
		if(fixed) return;
		this.unit = (unit == null) ? "" : unit;
		setNumberFormat(numberFormat, precision);
	}

	/**
	 * Set the numberFormat and precision in one go. <br>
	 * Valid number formats are INTEGER, DECIMAL, EXPONENT <br>
	 * Precision must be >= 1 and is ignored for INTEGER.
	 * 
	 * @param numberFormat INTEGER, DECIMAL or EXPONENT
	 * @param precision must be >= 1
	 */
	public void setNumberFormat(int numberFormat, int precision){
		if(fixed) return;
		switch(numberFormat){
		case INTEGER:
		case DECIMAL:
		case EXPONENT:
			this.valueType = numberFormat;
			break;
		default:
			valueType = DECIMAL;
		}
		this.precision = Math.max(1, precision);
		bufferInvalid = true;
	}
	
	/**
	 * Get the current value as a float
	 */
	public float getValueF(){
		return startLimit + (endLimit - startLimit) * valuePos;
	}

	/**
	 * Get the current value as an integer. <br>
	 * DECIMAL and EXPONENT value types will be rounded to the nearest integer.
	 */
	public float getValueI(){
		return Math.round(startLimit + (endLimit - startLimit) * valuePos);
	}
	
	/**
	 * Get the current value used for easing.
	 * @return the easing
	 */
	public float getEasing() {
		return easing;
	}

	/**
	 * Set the amount of easing to be used when a value is changing. The default value
	 * is 1 (no easing) values > 1 will cause the value to rush from its starting value
	 * and decelerate towards its final values. In other words it smoothes the movement
	 * of the slider thumb or knob rotation.
	 * 
	 * @param easeBy the easing to set
	 */
	public void setEasing(float easeBy) {
		if(easeBy < 1)
			easing = 1;
		else
			easing = easeBy;
	}

	/**
	 * Get the number of tick marks.
	 * @return the nbrTicks
	 */
	public int getNbrTicks() {
		return nbrTicks;
	}

	/**
	 * The number of ticks must be >= 2 since 2 are required for the slider limits.
	 * 
	 * @param noOfTicks the nbrTicks to set
	 */
	public void setNbrTicks(int noOfTicks) {
		if(fixed) return;
		if(noOfTicks < 2)
			noOfTicks = 2;
		if(nbrTicks != noOfTicks){
			nbrTicks = noOfTicks;
			bufferInvalid = true;
		}
	}

	/**
	 * Is the value constrained to the tick marks?
	 * @return the stickToTicks true if values constrained else false
	 */
	public boolean isStickToTicks() {
		return stickToTicks;
	}

	/**
	 * Specify whether the values are to be constrained to the tick marks or not
	 * @param stickToTicks true if you want to constrain the values else false
	 */
	public void setStickToTicks(boolean stickToTicks) {
		if(fixed) return;
		this.stickToTicks = stickToTicks;
		if(stickToTicks){
			valueTarget = findNearestTickValueTo(valuePos);
			bufferInvalid = true;
		}
	}

	/**
	 * These are normalised values i.e. between 0.0 and 1.0 inclusive
	 * @param p
	 * @return
	 */
	protected float findNearestTickValueTo(float p){
		float tickSpace = 1.0f / (nbrTicks - 1);
		int tn =  (int) (p / tickSpace + 0.5f);
		return tickSpace * tn;
	}
	
	/**
	 * Are the tick marks visible?
	 * @return the showTicks
	 */
	public boolean isShowTicks() {
		return showTicks;
	}

	/**
	 * Set whether the tick marks are to be displayed or not.
	 * @param showTicks the showTicks to set
	 */
	public void setShowTicks(boolean showTicks) {
		if(fixed) return;
		this.showTicks = showTicks;
	}

	
	/**
	 * Are the limit values visible?
	 * @return the showLimits
	 */
	public boolean isShowLimits() {
		return showLimits;
	}

	/**
	 * Set whether the limits are to be displayed or not.
	 * @param showLimits the showLimits to set
	 */
	public void setShowLimits(boolean showLimits) {
		if(fixed) return;
		this.showLimits = showLimits;
	}

	/**
	 * Is the current value to be displayed?
	 * @return the showValue
	 */
	public boolean isShowValue() {
		return showValue;
	}

	/**
	 * Set whether the current value is to be displayed or not.
	 * @param showValue the showValue to set
	 */
	public void setShowValue(boolean showValue) {
		this.showValue = showValue;
	}

	/**
	 * @return the startLimit
	 */
	public float getStartLimit() {
		return startLimit;
	}

	/**
	 * @return the endLimit
	 */
	public float getEndLimit() {
		return endLimit;
	}

	/**
	 * 
	 * @return the valueType
	 */
	public int getValueType() {
		return valueType;
	}

	/**
	 * Precision used with floating point numbers
	 * @return the precision
	 */
	public int getPrecision() {
		return precision;
	}

	/**
	 * @return the unit
	 */
	public String getUnit() {
		return unit;
	}

	/**
	 * If you are using easing then this returns true if the current value
	 * is moving towards the required value as controlled at the interface, 
	 * otherwise it will return false.
	 * 
	 * @return the isValueChanging
	 */
	public boolean isValueChanging() {
		return isValueChanging;
	}

	
}
