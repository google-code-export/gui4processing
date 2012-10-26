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

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;

import processing.core.PApplet;

public class FLinearTrackControl extends FValueControl {

	static protected float TINSET = 4;
	static protected int THUMB_SPOT = 1;
	static protected int TRACK_SPOT = 2;

	protected float trackWidth, trackLength, trackDisplayLength;

	protected int textOrientation = ORIENT_TRACK;

	protected int downHotSpot = -1;
	// Mouse over status
	protected int status = -1;


	public FLinearTrackControl(PApplet theApplet, float p0, float p1, float p2, float p3) {
		super(theApplet, p0, p1, p2, p3);
	}

	/**
	 * Set the text orientation for the display of the limits and value if appropriate. <br>
	 * Acceptable values are G4P.ORIENT_LEFT, G4P.ORIENT_RIGHT or G4P.ORIENT_TRACK <br>
	 * If an invalid value is passed the ORIENT_TRACK is used.
	 * 
	 * @param orient the orientation of the number labels
	 */
	public void setTextOrientation(int orient){
		switch(orient){
		case ORIENT_LEFT:
		case ORIENT_RIGHT:
		case ORIENT_TRACK:
			textOrientation = orient;
			break;
		default:
			textOrientation = ORIENT_TRACK;
		}
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

	protected void drawValue(float trackOffset){
		Graphics2D g2d = buffer.g2;
		float px, py;
		TextLayout line;
		ssValue = new StyledString(getNumericDisplayString(getValueF()));
		line = ssValue.getLines(g2d).getFirst().layout;
		float advance = line.getVisibleAdvance();
		switch(textOrientation){
		case ORIENT_LEFT:
			px = (valuePos - 0.5f) * trackLength + line.getDescent();
			py = -trackOffset;
			buffer.pushMatrix();
			buffer.translate(px, py);
			buffer.rotate(-PI/2);
			line.draw(g2d, 0, 0 );
			buffer.popMatrix();
			break;
		case ORIENT_RIGHT:
			px = (valuePos - 0.5f) * trackLength - line.getDescent();
			py = - trackOffset - advance;
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
			py = -trackOffset - line.getDescent();
			line.draw(g2d, px, py );
			line = ssEndLimit.getLines(g2d).getFirst().layout;	
			break;
		}
	}
	
	protected void drawLimits(float trackOffset){
		Graphics2D g2d = buffer.g2;
		float px, py;
		TextLayout line;
		if(limitsInvalid){
			ssStartLimit = new StyledString(getNumericDisplayString(startLimit));
			ssEndLimit = new StyledString(getNumericDisplayString(endLimit));
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
			py = trackOffset + line.getAscent();
			line.draw(g2d, px, py );
			line = ssEndLimit.getLines(g2d).getFirst().layout;	
			px = (trackLength + trackWidth)/2 - line.getVisibleAdvance();
			py = trackOffset + line.getAscent();
			line.draw(g2d, px, py );
			break;
		}	
	}

}
