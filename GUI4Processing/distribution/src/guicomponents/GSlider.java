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

	/**
	 * These are the values that are supplied back to the user
	 */
	protected int minValue = 0;
	protected int maxValue = 100;
	protected int value = 50;
	
	/** 
	 * Pixel values relative to slider top left
	 */
	protected int thumbMin, thumbMax;
	// The position to display the thumb
	protected int thumbPos;
	// The final position for the thumb
	protected int thumbTargetPos;
	
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
	 * @param colorScheme
	 */
	public GSlider(PApplet theApplet, int x, int y, int width, int height,
			GCScheme colorScheme){
		super(theApplet, x, y, colorScheme);
		this.width = width;
		this.height = height;
		app.registerPre(this);
		app.registerDraw(this);
		app.registerMouseEvent(this);
		createEventHandler(theApplet);
	}

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
		app.registerPre(this);
		app.registerDraw(this);
		app.registerMouseEvent(this);
		createEventHandler(theApplet);
	}

	/**
	 * Override the default event handler created with createEventHandler(Object obj)
	 * @param obj
	 * @param methodName
	 */
	public void addEventHandler(Object obj, String methodName){
		try{
			this.eventHandler = obj.getClass().getMethod(methodName, new Class[] { GSlider.class } );
			eventHandlerObject = obj;
		} catch (Exception e) {
			eventHandlerObject = null;
			System.out.println("The class " + obj.getClass().getSimpleName() + " does not have a method called " + methodName);
			System.out.println("with a single parameter of type GSlider");
		}
	}
	
	/**
	 * Create an event handler that will call a method handleTextFieldEvents(GTextField tfield)
	 * when text is changed or entered
	 * @param obj
	 */
	protected void createEventHandler(Object obj){
		try{
			this.eventHandler = obj.getClass().getMethod("handleSliderEvents", new Class[] { GSlider.class } );
			eventHandlerObject = obj;
		} catch (Exception e) {
			eventHandlerObject = null;
			System.out.println("You might want to add a method to handle \nslider events the syntax is");
			System.out.println("void handleSliderEvents(GSlider slider){\n   ...\n}\n\n");
		}
	}
	
	/**
	 * The user can change the range and initial value of the 
	 * slider from the default values of range 0-100 and 
	 * initial value of 50
	 * 
	 * @param init
	 * @param min
	 * @param max
	 */
	public void setLimits(int init, int min, int max){
		minValue = Math.min(min, max);
		maxValue = Math.max(min, max);
		init = PApplet.constrain(init, minValue, maxValue);
		
		if(thumbMax - thumbMin < maxValue - minValue){
			System.out.println(this.getClass().getSimpleName()+".setLimits");
			System.out.println("  not all values in the range "+min+" - "+max+" can be returned");
			System.out.print("  either reduce the range or make the slider ");
			if(this.getClass().getSimpleName().equals("GHorzSlider")) 
				System.out.print("width");
			else
				System.out.print("height");
			System.out.println(" at least " + (max-min+thumbSize));
		}
		thumbTargetPos = thumbPos;
		setValue(init);
	}
	
	/**
	 * Move thumb if not at desired position
	 */
	public void pre(){
		int change, inertia = thumbInertia;
		if(thumbPos != thumbTargetPos){
			// Make sure we get a change value by repeatedly decreasing the inertia value
			do {
				change = (thumbTargetPos - thumbPos)/inertia;
				inertia--;
			} while (change == 0 && inertia > 0);
			// If there is a change update the current value and generate an event
			if(change != 0){
				thumbPos += change;
				value = (int) PApplet.map(thumbPos, thumbMin, thumbMax, minValue, maxValue);
				fireEvent();
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
	 * Get the current value represented by the slider
	 * 
	 * @return current value
	 */
	public int getValue(){
		return value;
	}
	
	/**
	 * Sets the target value of the slider, if setInertia(x) has been 
	 * to implement inertia then the actual slider value will gradually
	 * change until it reaches the target value. The slider thumb is 
	 * always in the right position for the current slider value.
	 * 
	 * @param newValue the value we wish the slider to become
	 */
	public void setValue(int newValue){
		value = PApplet.constrain(newValue, minValue, maxValue);
		thumbTargetPos = (int) PApplet.map(value, minValue, maxValue, thumbMin, thumbMax);
	}
	
	/**
	 * The same as setValue(newValue) except the second parameter determines 
	 * whether we should ignore any inertia value so the affect is immediate.
	 * 
	 * @param newValue the value we wish the slider to become
	 * @param ignoreInteria if true change is immediate
	 */
	public void setValue(int newValue,  boolean ignoreInteria){
		setValue(newValue);
		if(ignoreInteria)
			thumbPos = thumbTargetPos;
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
