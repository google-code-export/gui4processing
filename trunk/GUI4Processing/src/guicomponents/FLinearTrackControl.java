package guicomponents;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;

import processing.core.PApplet;

public class FLinearTrackControl extends FValueControl {

	static protected float TINSET = 4;
	static protected int THUMB_SPOT = 1;
	static protected int TRACK_SPOT = 2;

	protected float trackWidth, trackLength, trackDisplayLength;
	protected float trackOffset; // The amount to offset the labels to miss ticks and thumb
	
	protected int textOrientation = ORIENT_TRACK;
	
	protected int downHotSpot = -1;
	// Mouse over status
	protected int status = OFF_CONTROL;

	public FLinearTrackControl(PApplet theApplet, float p0, float p1, float p2, float p3) {
		super(theApplet, p0, p1, p2, p3);
	}
	
	public void setLabelDir(int direction){
		switch(direction){
		case ORIENT_LEFT:
		case ORIENT_RIGHT:
		case ORIENT_TRACK:
			textOrientation = direction;
			bufferInvalid = true;
		}
	}
	
	public void mouseEvent(MouseEvent event){
		if(!visible || !enabled || !available) return;

		calcTransformedOrigin(winApp.mouseX, winApp.mouseY);
		currSpot = whichHotSpot(ox, oy);
		// Make ox relative to the centre of the slider
		ox -= width/2;
		ox /= trackLength;
		
		if(currSpot >= 0 || focusIsWith == this)
			cursorIsOver = this;
		else if(cursorIsOver == this)
			cursorIsOver = null;

		switch(event.getID()){
		case MouseEvent.MOUSE_PRESSED:
			if(focusIsWith != this && currSpot > -1 && z > focusObjectZ()){
				downHotSpot = currSpot;
				status = (downHotSpot == THUMB_SPOT) ? PRESS_CONTROL : OFF_CONTROL;
				offset = ox + 0.5f - valuePos; // normalised
				takeFocus();
				bufferInvalid = true;
			}
			break;
		case MouseEvent.MOUSE_CLICKED:
			if(focusIsWith == this ){
				valueTarget = ox + 0.5f;
				if(stickToTicks)
					valueTarget = findNearestTickValueTo(valueTarget);
				dragging = false;
				status = OFF_CONTROL;
				loseFocus(null);
				bufferInvalid = true;
			}
			break;
		case MouseEvent.MOUSE_RELEASED:
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
			}
			dragging = false;
			break;
		case MouseEvent.MOUSE_DRAGGED:
			if(focusIsWith == this){
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

	protected void drawValue(){
		Graphics2D g2d = buffer.g2;
		float px, py;
		TextLayout line;
		ssValue = new StyledString(g2d, getNumericDisplayString(getValueF()));
		line = ssValue.getLines(g2d).getFirst().layout;
		float advance = line.getVisibleAdvance();
		float descent = line.getDescent();
		switch(textOrientation){
		case ORIENT_LEFT:
			px = (valuePos - 0.5f) * trackLength + descent;
			py = -trackOffset;
			buffer.pushMatrix();
			buffer.translate(px, py);
			buffer.rotate(-PI/2);
			line.draw(g2d, 0, 0 );
			buffer.popMatrix();
			break;
		case ORIENT_RIGHT:
			px = (valuePos - 0.5f) * trackLength - descent;
			py = -trackOffset - advance;
			buffer.pushMatrix();
			buffer.translate(px, py);
			buffer.rotate(PI/2);
			line.draw(g2d, 0, 0 );
			buffer.popMatrix();
			break;
		case ORIENT_TRACK:
			px = (valuePos - 0.5f) * trackLength - advance /2;
			if(px < -trackDisplayLength/2)
				px = -trackDisplayLength/2;
			else if(px + advance > trackDisplayLength /2)
				px = trackDisplayLength/2 - advance;
			py = -trackWidth - 2 - line.getDescent();
			line.draw(g2d, px, py );
			break;
		}	
	}

	protected void drawLimits(){
		Graphics2D g2d = buffer.g2;
		float px, py;
		TextLayout line;
		if(limitsInvalid){
			ssStartLimit = new StyledString(g2d, getNumericDisplayString(startLimit));
			ssEndLimit = new StyledString(g2d, getNumericDisplayString(endLimit));
			limitsInvalid = false;
		}
		switch(textOrientation){
		case ORIENT_LEFT:
			line = ssStartLimit.getLines(g2d).getFirst().layout;	
			px = -trackLength/2 + line.getDescent();
			py = trackOffset + line.getVisibleAdvance();
			buffer.pushMatrix();
			buffer.translate(px, py);
			buffer.rotate(-PI/2);
			line.draw(g2d, 0, 0 );
			buffer.popMatrix();
			line = ssEndLimit.getLines(g2d).getFirst().layout;	
			px = trackLength/2  + line.getDescent();
			py = trackOffset + line.getVisibleAdvance();
			buffer.pushMatrix();
			buffer.translate(px, py);
			buffer.rotate(-PI/2);
			line.draw(g2d, 0, 0 );
			buffer.popMatrix();
			break;
		case ORIENT_RIGHT:
			line = ssStartLimit.getLines(g2d).getFirst().layout;	
			px = -trackLength/2 - line.getDescent();
			py = trackOffset;
			buffer.pushMatrix();
			buffer.translate(px, py);
			buffer.rotate(PI/2);
			line.draw(g2d, 0, 0 );
			buffer.popMatrix();
			line = ssEndLimit.getLines(g2d).getFirst().layout;	
			px = trackLength/2  - line.getDescent();
			py = trackOffset;
			buffer.pushMatrix();
			buffer.translate(px, py);
			buffer.rotate(PI/2);
			line.draw(g2d, 0, 0 );
			buffer.popMatrix();
			break;
		case ORIENT_TRACK:
			line = ssStartLimit.getLines(g2d).getFirst().layout;	
			px = -(trackLength + trackWidth)/2;
			py = trackOffset + line.getVisibleAdvance();
			line.draw(g2d, px, py );
			line = ssEndLimit.getLines(g2d).getFirst().layout;	
			px = (trackLength + trackWidth)/2 - line.getVisibleAdvance();
			py = trackOffset + line.getAscent();
			line.draw(g2d, px, py );
			break;
		}
		
	}

	
}
