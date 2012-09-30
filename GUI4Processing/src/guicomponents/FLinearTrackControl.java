package guicomponents;

import java.awt.event.MouseEvent;

import processing.core.PApplet;

public class FLinearTrackControl extends FValueControl {

	static protected float TINSET = 4;
	static protected int THUMB_SPOT = 1;
	static protected int TRACK_SPOT = 2;

	static final protected int ORIENT_LEFT = -1;
	static final protected int ORIENT_TRACK = 0;
	static final protected int ORIENT_RIGHT = 1;
	
	protected float trackWidth, trackLength, trackDisplayLength;
	protected float trackOffset; // The amount to offset the labels to miss ticks and thumb
	
	protected int textOrientation = ORIENT_RIGHT;
	
	protected int downHotSpot = -1;
	// Mouse over status
	protected int status = -1;

//	protected float[] lx = new float[3];
//	protected float[] ly = new float[3];
	
	public FLinearTrackControl(PApplet theApplet, float p0, float p1, float p2, float p3) {
		super(theApplet, p0, p1, p2, p3);
	}

	
	public void mouseEvent(MouseEvent event){
		if(!visible || !enabled || !available) return;

		calcTransformedOrigin(winApp.mouseX, winApp.mouseY);
		currSpot = whichHotSpot(ox, oy);
		// Normalise ox and oy to the centre of the slider
		ox -= width/2;
		ox /= trackLength;
		
//		System.out.println("Custom slider      " + currSpot + "   thumb at " + hotspots[0].x);
		// currSpot == 1 for text display area
		
		if(currSpot >= 0 || focusIsWith == this)
			cursorIsOver = this;
		else if(cursorIsOver == this)
			cursorIsOver = null;

		switch(event.getID()){
		case MouseEvent.MOUSE_PRESSED:
//			System.out.println("P " + focusIsWith);
			if(focusIsWith != this && currSpot > -1 && z > focusObjectZ()){
				downHotSpot = currSpot;
				status = (downHotSpot == THUMB_SPOT) ? PRESS_CONTROL : OFF_CONTROL;
//				if(downHotSpot == THUMB_SPOT)
//					status = PRESS_CONTROL;
//				else
//					status = OFF_CONTROL;
				offset = ox + 0.5f - valuePos; // normalised
				takeFocus();
				bufferInvalid = true;
				System.out.println("PRESSED   state = " + status );
			}
			break;
		case MouseEvent.MOUSE_CLICKED:
//			System.out.println("C " + focusIsWith);
			if(focusIsWith == this ){
				valueTarget = ox + 0.5f;
				if(stickToTicks)
					valueTarget = findNearestTickValueTo(valueTarget);
				dragging = false;
				status = OFF_CONTROL;
				loseFocus(null);
				bufferInvalid = true;
				System.out.println("CLICKED state = " + status  );
			}
			break;
		case MouseEvent.MOUSE_RELEASED:
//			System.out.println("R " + focusIsWith);
			if(focusIsWith == this && dragging){
				if(downHotSpot == THUMB_SPOT){
					valueTarget = (ox - offset) + 0.5f;
					if(valueTarget < 0){
						valueTarget = 0;
						offset = 0;
					}
					else if(valueTarget > 1){
						valueTarget = 1;
						offset = 0;
					}
					if(stickToTicks)
						valueTarget = findNearestTickValueTo(valueTarget);
				}
				status = OFF_CONTROL;
				bufferInvalid = true;
				loseFocus(null);				
				System.out.println("RELEASED    state = " + status );
			}
			dragging = false;
			break;
		case MouseEvent.MOUSE_DRAGGED:
			if(focusIsWith == this){
				System.out.println("DRAGGED    state = " + status );
				dragging = true;
				if(downHotSpot == THUMB_SPOT){
					isValueChanging = true;
					valueTarget = (ox - offset) + 0.5f;
					if(valueTarget < 0){
						valueTarget = 0;
						offset = 0;
					}
					else if(valueTarget > 1){
						valueTarget = 1;
						offset = 0;
					}
				}
			}
			break;
		case MouseEvent.MOUSE_MOVED:
			int currStatus = status;
			// If dragged state will stay as PRESSED
			if(currSpot == THUMB_SPOT)
				status = OVER_CONTROL;
			else
				status = OFF_CONTROL;
			if(currStatus != status)
				bufferInvalid = true;
			System.out.println("MOVED   state = " + status);
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
		winApp.popMatrix();

		winApp.popStyle();
	}

	
}
