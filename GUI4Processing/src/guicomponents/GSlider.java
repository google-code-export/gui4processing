/*
  Part of the GUI for Processing library 
  	http://www.lagers.org.uk/g4p/index.html
	http://gui4processing.googlecode.com/svn/trunk/

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

import java.awt.event.MouseEvent;

import processing.core.PApplet;

/**
 * Abstract class to provide a slider - GHorzSlider and GVertSlider
 * inherit from this class.
 * 
 * @author Peter Lager
 *
 */
public abstract class GSlider extends GComponent {

	public static final int INTEGER = 0;
	public static final int DECIMAL = 1;
	public static final int EXPONENT = 2;

	/*
	 * These are the values that are supplied back to the user
	 */
	protected float sInitValue;			// Initial slider value (was init)
	protected float sValue0 = 0;		// Slider value on left/bottom end of slider (was minValue)
	protected float sValue1 = 100;		// Slider value at right/top end of slider (was maxValue)
	protected float sValue;				// Slider value based on current thumb value (was value)

	protected float low, high;

	protected float nValue;				// Normalised value i.e. in range 0-1 inclusive
	protected float nTargetValue;		// Normalised value i.e. in range 0-1 inclusive

	// Indicates the type of value used in the display
	protected int _valueType = INTEGER;

	/* 
	 * Pixel values of slider to be used in drawings
	 */
	protected int thumb0;		// was thumbMin
	protected int thumb1;		// was thumbMax
	// The position to display the thumb
	protected int thumbValue;	// was thumbPos
	// The final position for the thumb
	protected int thumbTargetValue;

	protected float onePixel;

	/**
	 * Calculate the slider value from a normalised value
	 * @param value in the range 0-1 inclusive
	 * @return
	 */
	protected float calcSliderValue(float value){
		value = (value < 0) ? 0 : value;
		value = (value > 1) ? 1 : value;
		nValue = value;
		return sValue0 + (sValue1 - sValue0) * nValue;
	}

	/**
	 * Calculate the normalised value from a sliderValue
	 * @param value
	 * @return
	 */
	protected float calcNormalisedValue(float value){
		float low = Math.min(sValue0, sValue1);
		float high = Math.max(sValue0, sValue1);
		value = (value < low) ? low : value;
		value = (value > high) ? high : value;
		return 1 - (sValue1 - sValue)/(sValue1 - sValue0);
	}


	protected int thumbSize = 10;

	protected int thumbInertia = 1;

	protected int offset;

	protected boolean isValueChanging = false;

	/**
	 * Called by GHorzSlider and GVertSlider.
	 * 
	 * @param theApplet
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public GSlider(PApplet theApplet, int x, int y, int width, int height){
		super(theApplet, x, y);
		this.width = width;
		this.height = height;
		registerAutos_DMPK(true, true, true, false);
		createEventHandler(winApp, "handleSliderEvents", new Class[]{ GSlider.class });
	}

	/**
	 * The user can change the range and initial value of the 
	 * slider from the default values of range 0-100 and 
	 * initial value of 50.
	 * This method ignores inertia so the effect is immediate.
	 * 
	 * @param init
	 * @param min
	 * @param max
	 */
	public void setLimits(int init, int min, int max){
		low = Math.min(min, max);
		high = Math.max(min, max);
		sValue0 = min;
		sValue1 = max;
		onePixel =  (high - low) / (thumb1 - thumb0);

		this.sInitValue = Math.round(PApplet.constrain((float)init, sValue0, sValue1));

		thumbTargetValue = thumbValue;
		// Set the value immediately ignoring inertia
		setValue(init, true);
	}


	/**
	 * Sets the limits of the slider as float values. Converted to floats or integer depending
	 * on the type of the slider.
	 */
	public void setLimits(float init, float min, float max){
		low = Math.min(min, max);
		high = Math.max(min, max);
		sValue0 = min;
		sValue1 = max;
		onePixel =  (high - low) / (thumb1 - thumb0);

		this.sInitValue = PApplet.constrain(init, sValue0, sValue1);

		thumbTargetValue = thumbValue;
		// Set the value immediately ignoring inertia
		setValue(this.sInitValue, true);
	}

	/**
	 * Move thumb if not at desired position
	 */
	public void pre(){
		float change;
		int inertia = thumbInertia;
		if(Math.abs(nValue - nTargetValue) < onePixel){
			nValue = nTargetValue;
			sValue = calcSliderValue(nValue);
			isValueChanging = false;
		}
		else {
			// Make sure we get a change value by repeatedly decreasing the inertia value
			do {
				change = (nTargetValue - nValue)/inertia;
				inertia--;
			} while (Math.abs(change) < 1E-4 && inertia > 0);
			// If there is a change update the current value and generate an event
			if(Math.abs(change) >= onePixel){
				nTargetValue += change;
				nTargetValue = PApplet.constrain(nTargetValue, 0.0f, 1.0f);
				sValue = calcSliderValue(nTargetValue);
				eventType = CHANGED;
				fireEvent();
			}
			else
				isValueChanging = false;
		}			
	}
	public void preOLD(){
		int change, inertia = thumbInertia;
		if(thumbValue == thumbTargetValue){
			isValueChanging = false;
		}
		else {
			// Make sure we get a change value by repeatedly decreasing the inertia value
			do {
				change = (thumbTargetValue - thumbValue)/inertia;
				inertia--;
			} while (change == 0 && inertia > 0);
			// If there is a change update the current value and generate an event
			if(change != 0){
				thumbValue += change;
				float newValue = PApplet.map(thumbValue, thumb0, thumb1, sValue0, sValue1);
				boolean valueChanged = (newValue != sValue);
				sValue = newValue;
				if(valueChanged){
					eventType = CHANGED;
					fireEvent();
				}
			}
			else
				isValueChanging = false;
		}			
	}

	/**
	 * Override in child classes
	 */
	public void draw(){
	}

	/**
	 * Override in child classes
	 */
	public void mouseEvent(MouseEvent event){
	}

	/**
	 * Override in child classes
	 *  
	 * @return always false
	 */
	public boolean isOver(int ax, int ay){
		return false;
	}

	/**
	 * Get the minimum slider value
	 * @return min value
	 */
	public int getMinValue() {
		return Math.round(sValue0);
	}

	/**
	 * Get the maximum slider value
	 * @return max value
	 */
	public int getMaxValue() {
		return Math.round(sValue1);
	}

	/**
	 * Get the current value represented by the slider
	 * 
	 * @return current value
	 */
	public int getValue(){
		return Math.round(sValue);
	}

	/**
	 * Gets the current value of the slider. If the value type is integer
	 * then the value is rounded.
	 */
	public float getValuef(){
		if(_valueType == INTEGER)
			return Math.round(sValue);
		else
			return sValue;
	}


	/**
	 * Is the value changing as a results of the slider thumb being 
	 * dragged with the mouse.
	 * 
	 * @return true if value being changed at GUI
	 */
	public boolean isValueChanging() {
		return isValueChanging;
	}

	/**
	 * Sets the target value of the slider, if setInertia(x) has been used
	 * to implement inertia then the actual slider value will gradually
	 * change until it reaches the target value. The slider thumb is 
	 * always in the right position for the current slider value. <br>
	 * <b>Note</b> that events will continue to be generated so if this
	 * causes unexpected behaviour then use setValue(newValue, true)
	 * 
	 * @param newValue the value we wish the slider to become
	 */
	public void setValue(int newValue){
		sValue = PApplet.constrain(newValue, sValue0, sValue1);
		thumbTargetValue = (int) PApplet.map(sValue, sValue0, sValue1, thumb0, thumb1);
	}

	/**
	 * Sets the target value of the slider, if setInertia(x) has been 
	 * to implement inertia then the actual slider value will gradually
	 * change until it reaches the target value. The slider thumb is 
	 * always in the right position for the current slider value. <br>
	 * <b>Note</b> that events will continue to be generated so if this
	 * causes unexpected behaviour then use setValue(newValue, true)
	 * 
	 * @param newValue the value we wish the slider to become
	 */
	public void setValue(float newValue){
		sValue = PApplet.constrain(newValue, sValue0, sValue1);
		thumbTargetValue = (int) PApplet.map(sValue, sValue0, sValue1, thumb0, thumb1);
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
	public void setValue(int newValue,  boolean ignoreInteria){
		setValue(newValue);
		if(ignoreInteria){
			thumbValue = thumbTargetValue;
		}
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
			thumbValue = thumbTargetValue;
		}
	}

	/**
	 * When dragging the slider thumb rapidly with the mouse a certain amount of 
	 * inertia will give a nice visual effect by trailing the thumb behind the
	 * mouse. A value of 1 (default) means the thumb is always in step with 
	 * the mouse. Increasing values will increase the amount of trailing and the
	 * length of time needed to reach the final value.
	 * I have found values around 10 give quite nice effect but much over 20 and
	 * you start to loose the gliding effect due to acceleration and deacceleration.
	 * 
	 * @param inertia values passed is constrained to the range 1-50.
	 */
	public void setInertia(int inertia){
		thumbInertia = PApplet.constrain(inertia, 1, 100);
	}

}
