package guicomponents;

import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;

import processing.core.PApplet;

/**
 * Abstract class to provide a slider
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
	protected int thumbPos;
	protected int aimThumbPos;
	protected int thumbSize = 10;
	
	protected int thumbInertia = 1;
	
	protected int offset;
	
	
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
			int colorScheme){
		super(theApplet, x, y, colorScheme);
//		this.minWidth = width;
//		this.minHeight = height;
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
//		this.minWidth = width;
//		this.minHeight = height;
		this.width = width;
		this.height = height;
		app.registerPre(this);
		app.registerDraw(this);
		app.registerMouseEvent(this);
		createEventHandler(theApplet);
	}

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
		value = constrain(init, minValue, maxValue);
		setValue(value);
		aimThumbPos = thumbPos;
		
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
	}
	
	/**
	 * Move thumb if not at desired position
	 */
	public void pre(){
		int change, inertia = thumbInertia;
		if(thumbPos != aimThumbPos){
			do {
				change = (aimThumbPos - thumbPos)/inertia;
				inertia--;
			} while (change == 0 && inertia > 0);
			// If there is a change update the current value and generate an event
			if(change != 0){
				thumbPos += change;
				value = (int) PApplet.map(thumbPos, thumbMin, thumbMax, minValue, maxValue);
				fireEvent();
			}
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
	 * Allows the user to set the value of the slider in the program
	 * and then positions the slider appropriately.
	 * 
	 * @param newValue
	 */
	public void setValue(int newValue){
		value = constrain(newValue, minValue, maxValue);
		thumbPos = (int) PApplet.map(value, minValue, maxValue, thumbMin, thumbMax);
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
		thumbInertia = constrain(inertia, 1,50);
		inertia = thumbInertia;
	}
	

}
