package guicomponents;

import java.awt.Point;
import java.awt.event.MouseEvent;

import processing.core.PApplet;

public class GHorzSlider extends GComponent {

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
	
	protected int offsetX;
	
	protected int border = 0;
	
	/**
	 * Create a horizontal slider.
	 * Default values:
	 * 		Range 0-100
	 *      Initial value 50
	 * Use the setLimits method to customise these values.
	 * 
	 * @param theApplet
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param colorScheme
	 */
	public GHorzSlider(PApplet theApplet, int x, int y, int width, int height,
			int colorScheme){
		super(theApplet, x, y, colorScheme);
		this.minWidth = width;
		this.minHeight = height;
		this.width = width;
		this.height = height;
		initThumbDetails();
		app.registerPre(this);
		app.registerDraw(this);
		app.registerMouseEvent(this);
	}

	/**
	 * Create a horizontal slider.
	 * Default values:
	 * 		Range 0-100
	 *      Initial value 50
	 * Use the setLimits method to customise these values.
	 * 
	 * @param theApplet
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public GHorzSlider(PApplet theApplet, int x, int y, int width, int height){
		super(theApplet, x, y);
		this.minWidth = width;
		this.minHeight = height;
		this.width = width;
		this.height = height;
		initThumbDetails();
		app.registerPre(this);
		app.registerDraw(this);
		app.registerMouseEvent(this);
	}

	/**
	 * Initialises the thumb details
	 */
	protected void initThumbDetails(){
		thumbSize = Math.max(10, width / 20);
		thumbMin = thumbSize/2;
		thumbMax = width - thumbSize/2;
		setValue(value);
		aimThumbPos = thumbPos;
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
		if(thumbMax - thumbMin < width){
			System.out.println("GHorzSlider.setLimits" );
			System.out.println("  not all values in the range "+min+" - "+max+" can be returned");
			System.out.println("  either reduce range or make slider width at least "+(max-min+thumbSize));
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
				value = (int) app.map(thumbPos, thumbMin, thumbMax, minValue, maxValue);
				// event generated here
				System.out.println("Current value "+value);
			}
		}
	}
	
	/**
	 * Draw the slider
	 */
	public void draw(){
		if(visible){
			Point pos = new Point(0,0);
			calcAbsPosition(pos);
			app.noStroke();
			app.fill(localGScheme.sliderBG);
			app.rect(pos.x, pos.y, width, height);
			app.fill(localGScheme.sliderThumb);
			app.rect(pos.x + thumbPos - thumbSize/2, pos.y, thumbSize, height);
			if(border != 0){
				app.strokeWeight(border);
				app.noFill();
				app.stroke(localGScheme.sliderStroke);
				app.rect(pos.x, pos.y, width, height);
			}
		}
	}
	
	/**
	 * All GUI components are registered for mouseEvents
	 */
	public void mouseEvent(MouseEvent event){
		switch(event.getID()){
		case MouseEvent.MOUSE_PRESSED:
			if(mouseFocusOn == null && isOver(app.mouseX, app.mouseY))
				mouseFocusOn = this;
			break;
		case MouseEvent.MOUSE_CLICKED:
			mouseFocusOn = null;
			break;
		case MouseEvent.MOUSE_RELEASED:
			if(mouseFocusOn == this){
				mouseFocusOn = null;
			}
			break;
		case MouseEvent.MOUSE_DRAGGED:
			if(mouseFocusOn == this){
				Point p = new Point(0,0);
				calcAbsPosition(p);
				aimThumbPos = constrain(app.mouseX - offsetX - p.x, thumbMin, thumbMax);
			}
			break;
		}
	}

	/**
	 * Determines whether the position ax, ay is over the thumb
	 * of this GPanel.
	 * 
	 * @return true if mouse is over the panel tab else false
	 */
	public boolean isOver(int ax, int ay){
		Point p = new Point(0,0);
		calcAbsPosition(p);
		if(ax >= p.x + thumbPos - thumbSize/2 && ax <= p.x + thumbPos + thumbSize/2 && ay >= p.y && ay <= p.y + height){
			offsetX = ax - (p.x + thumbPos);
			return true;
		}
		else 
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
		thumbPos = (int) app.map(value, minValue, maxValue, thumbMin, thumbMax);
	}
	
	/**
	 * When dragging the slider thumb rapidly with the mouse a certain amount of 
	 * inertia will give a nice visual effect by trailing the thumb behind the
	 * mouse. A value of 1 (default) means the thumb is always in step with 
	 * the mouse. Increasing values will increase the amount of trailing and the
	 * length of time needed to reach  
	 * caused 
	 * by giving the thumb some inertia. It causes the thumb to 
	 * @param inertia
	 */
	public void setInertia(int inertia){
		thumbInertia = constrain(inertia, 1,20);
		inertia = thumbInertia;
	}
	
	/**
	 * The user can add a border by specifying it's thickness
	 * a value of 0 means no border (this is the default)
	 * @param border width in pixels
	 */
	public void setBorder(int border){
		this.border = border;
	}
}
