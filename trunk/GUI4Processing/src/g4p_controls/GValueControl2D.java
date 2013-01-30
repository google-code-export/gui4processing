package g4p_controls;

import processing.core.PApplet;

public class GValueControl2D extends GAbstractControl {

	static protected int THUMB_SPOT = 1;
	static protected int TRACK_SPOT = 2;

	
	protected float parametricPosX = 0.5f, parametricTargetX = 0.5f;
	protected float parametricPosY = 0.5f, parametricTargetY = 0.5f;
	
	protected float easing  = 1.0f; // must be >= 1.0

	// Offset to between mouse and thumb centre
	protected float offsetH, offsetV;
	
	protected int valueType = DECIMAL;
	protected int precision = 2;

	public GValueControl2D(PApplet theApplet, float p0, float p1, float p2, float p3) {
		super(theApplet, p0, p1, p2, p3);
	}

	public void pre(){
		if(Math.abs(parametricTargetX - parametricPosX) > epsilon || Math.abs(parametricTargetY - parametricPosY) > epsilon){
			parametricPosX += (parametricTargetX - parametricPosX) / easing;
			parametricPosY += (parametricTargetY - parametricPosY) / easing;
			updateDueToValueChanging();
			bufferInvalid = true;
			if(Math.abs(parametricTargetX - parametricPosX) > epsilon || Math.abs(parametricTargetY - parametricPosY) > epsilon){
				fireEvent(this, GEvent.VALUE_CHANGING);
			}
			else {
				parametricPosX = parametricTargetX;
				parametricPosY = parametricTargetY;
				fireEvent(this, GEvent.VALUE_STEADY);
			}
		}
	}
	
	/**
	 * This should be overridden in child classes so they can perform any class specific
	 * actions when the value changes.
	 * Override this in GSlider to change the hotshot poaition.
	 */
	protected void updateDueToValueChanging(){
	}

	/**
	 * Make epsilon to match the value of 1 pixel or the precision which ever is the smaller
	 */
	protected void setEpsilon(){
		epsilon = (float) Math.min(0.001, Math.pow(10, -precision));
	}

}
