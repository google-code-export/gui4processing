package guicomponents;

import java.awt.Point;
import java.awt.event.MouseEvent;

import processing.core.PApplet;

public class GHorzSlider extends GSlider {

	
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
			GColor colorScheme){
		super(theApplet, x, y, width, height, colorScheme);
		initThumbDetails();
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
		super(theApplet, x, y, width, height);
		initThumbDetails();
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
	 * Draw the slider
	 */
	public void draw(){
		if(visible){
			Point pos = new Point(0,0);
			calcAbsPosition(pos);
			app.noStroke();
			app.fill(localColor.sliderBG);
			app.rect(pos.x, pos.y, width, height);
			app.fill(localColor.sliderThumb);
			app.rect(pos.x + thumbPos - thumbSize/2, pos.y, thumbSize, height);
			if(border != 0){
				app.strokeWeight(border);
				app.noFill();
				app.stroke(localColor.sliderStroke);
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
				aimThumbPos = constrain(app.mouseX - offset - p.x, thumbMin, thumbMax);
			}
			break;
		}
	}

	/**
	 * Determines whether the position ax, ay is over the thumb
	 * of this Slider.
	 * 
	 * @return true if mouse is over the panel tab else false
	 */
	public boolean isOver(int ax, int ay){
		Point p = new Point(0,0);
		calcAbsPosition(p);
		if(ax >= p.x + thumbPos - thumbSize/2 && ax <= p.x + thumbPos + thumbSize/2 && ay >= p.y && ay <= p.y + height){
			offset = ax - (p.x + thumbPos);
			return true;
		}
		else 
			return false;
	}

} // end of class
