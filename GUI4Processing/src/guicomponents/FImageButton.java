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

import java.awt.event.MouseEvent;

import processing.core.PApplet;

/**
 * Buttons create from this class use a number of images to represent it's 
 * state. This means that buttons can have an irregular and/or discontinuous
 * shape. <br>
 * 
 * Note that any images that do not fit the rectangle specified in the constructor
 * will be rescaled without regard to the original size or aspect ratio. <br>
 * 
 * The image button needs 1 to 3 image files to represent the button states <br>
 * OFF mouse is not over button <br>
 * OVER mouse is over the button <br>
 * DOWN the mouse is over the button and a mouse button is being pressed. <br>
 * 
 * If you only provide one image then this will be used for all states, if you
 * provide two then the second image is used for both  OVER and DOWN states. <br><br>
 * 
 * If you don't provide a mask file then the button 'hotspot' is represented by any
 * non-transparent pixels in the OFF image. If you do provide a mask file then the 
 * hotspot is defined by any black pixels in the mask image. <br><br>
 * 
 * 
 * Three types of event can be generated :-  <br>
 * <b> PRESSED  RELEASED  CLICKED </b><br>
 * 
 * To simplify event handling the button only fires off CLICKED events 
 * if the mouse button is pressed and released over the button face 
 * (the default behaviour). <br>
 * 
 * Using <pre>button1.fireAllEvents(true);</pre> enables the other 2 events
 * for button <b>button1</b>. A PRESSED event is created if the mouse button
 * is pressed down over the button face, the CLICKED event is then generated 
 * if the mouse button is released over the button face. Releasing the 
 * button off the button face creates a RELEASED event. <br>
 * 
 * 
 * @author Peter Lager
 *
 */
public class FImageButton extends FImageControl {

	protected int status;

	protected boolean reportAllButtonEvents = false;

	public FImageButton(PApplet theApplet, float p0, float p1, float p2,
			float p3, String[] fnames) {
		this(theApplet, p0, p1, p2, p3, fnames, null);
	}


	public FImageButton(PApplet theApplet, float p0, float p1, float p2,
			float p3, String[] fnames, String fnameMask) {
		super(theApplet, p0, p1, p2, p3, fnames, fnameMask);
		z = Z_SLIPPY;
		// Now register control with applet
		createEventHandler(winApp, "handleButtonEvents", new Class[]{ FImageButton.class });
		registeredMethods = DRAW_METHOD | MOUSE_METHOD;
		F4P.addControl(this);
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
		// Move matrix to line up with top-left corner
		winApp.translate(-halfWidth, -halfHeight);
		// Draw buffer
		winApp.imageMode(PApplet.CORNER);
		winApp.tint(-1, alphaLevel);
		winApp.image(bimage[status], 0, 0);	
		winApp.popMatrix();		
		winApp.popStyle();
	}

	/**
	 * 
	 * When a button is clicked on a GButton it generates 3 events (in this order) 
	 * mouse down, mouse up and mouse clicked. <br>
	 * You can test for a particular event type with PRESSED, RELEASED: <br>
	 * <pre>
	 * 	void handleButtonEvents(GButton button) {
	 *	  if(button == btnName && button.eventType == GButton.PRESSED){
	 *        // code for button click event
	 *    }
	 * </pre> <br>
	 * Where <pre><b>btnName</b></pre> is the GButton identifier (variable name) <br><br>
	 * 
	 * If you only wish to respond to button click events then use the statement <br>
	 * <pre>btnName.fireAllEvents(false); </pre><br> 
	 * This is the default mode.
	 */
	public void mouseEvent(MouseEvent event){
		if(!visible || !enabled || !available) return;

		calcTransformedOrigin(winApp.mouseX, winApp.mouseY);
		currSpot = whichHotSpot(ox, oy);
		if(currSpot >= 0 || focusIsWith == this)
			cursorIsOver = this;
		else if(cursorIsOver == this)
			cursorIsOver = null;

		switch(event.getID()){
		case MouseEvent.MOUSE_PRESSED:
			if(focusIsWith != this && currSpot >= 0  && z > focusObjectZ()){
				dragging = false;
				status = PRESS_CONTROL;
				takeFocus();
				eventType = PRESSED;
				if(reportAllButtonEvents)
					fireEventX(this);
			}
			break;
		case MouseEvent.MOUSE_CLICKED:
			// No need to test for isOver() since if the component has focus
			// and the mouse has not moved since MOUSE_PRESSED otherwise we 
			// would not get the Java MouseEvent.MOUSE_CLICKED event
			if(focusIsWith == this){
				status = OFF_CONTROL;
				loseFocus(null);
				dragging = false;
				eventType = CLICKED;
				fireEventX(this);
			}
			break;
		case MouseEvent.MOUSE_RELEASED:	
			// if the mouse has moved then release focus otherwise
			// MOUSE_CLICKED will handle it
			if(focusIsWith == this && dragging){
				if(currSpot >= 0){
					eventType = CLICKED;
					fireEventX(this);
				}
				else {
					if(reportAllButtonEvents){
						eventType = RELEASED;
						fireEventX(this);
					}
				}
				dragging = false;
				loseFocus(null);
				status = OFF_CONTROL;
			}
			break;
		case MouseEvent.MOUSE_MOVED:
			// If dragged state will stay as PRESSED
			if(currSpot >= 0)
				status = OVER_CONTROL;
			else
				status = OFF_CONTROL;
			break;
		case MouseEvent.MOUSE_DRAGGED:
			dragging = (focusIsWith == this);
			break;
		}
	}



	/**
	 * If the parameter is true all 3 event types are generated, if false
	 * only CLICKED events are generated (default behaviour).
	 * @param all
	 */
	public void fireAllEvents(boolean all){
		reportAllButtonEvents = all;
	}

}
