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

import java.awt.Point;
import java.awt.event.MouseEvent;

import processing.core.PApplet;

/**
 * The button component.
 * 
 * @author Peter Lager
 *
 */
public class GButton extends GComponent {
	
	// Button states
	public static final int OFF		= 0x00050001;
	public static final int OVER	= 0x00050002;
	public static final int DOWN	= 0x00050003;

	private int status;
	
//	public GButton(PApplet theApplet, String text, int x, int y, int width, int height,
//			GCScheme color, PFont font){
//		super(theApplet, x, y, color, font);
//		buttonCtorCore(text, width, height);
//	}
public int ccc;

	/**
	 * Creat a button.
	 * @param theApplet
	 * @param text
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public GButton(PApplet theApplet, String text, int x, int y, int width, int height){
		super(theApplet, x, y);
		buttonCtorCore(text, width, height);
	}

	private void buttonCtorCore(String text, int width, int height) {
		setText(text);
		this.width = Math.max(width, textWidth + PADH * 2);
		this.height = Math.max(height, localFont.size + 2 * PADV);
		createEventHandler(app);
		registerAutos_DMPK(true, true, false, false);
	}
	
	/**
	 * Override the default event handler created with createEventHandler(Object obj)
	 * @param obj
	 * @param methodName
	 */
	public void addEventHandler(Object obj, String methodName){
		try{
			this.eventHandler = obj.getClass().getMethod(methodName, new Class[] { GButton.class } );
			eventHandlerObject = obj;
		} catch (Exception e) {
			System.out.println("The class " + obj.getClass().getSimpleName() + " does not have a method called " + methodName);
			System.out.println("with a parameter of type GButton");
			eventHandlerObject = null;
		}
	}
	
	/**
	 * Create an event handler that will call a method handleButtonEvents(GButton cbox)
	 * when text is changed or entered
	 * @param obj
	 */
	protected void createEventHandler(Object obj){
		try{
			this.eventHandler = obj.getClass().getMethod("handleButtonEvents", new Class[] { GButton.class } );
			eventHandlerObject = obj;
		} catch (Exception e) {
			eventHandlerObject = null;
			System.out.println("You might want to add a method to handle \noption events the syntax is");
			System.out.println("void handleButtonEvents(GButton button){\n   ...\n}\n\n");
		}
	}
	
	/**
	 * Draw the button
	 */
	public void draw(){
		if(visible){
			Point pos = new Point(0,0);
			calcAbsPosition(pos);
			// Select draw color
			int col;
			switch(status){
			case OVER:
				col = localColor.btnOver;
				break;
			case DOWN:
				col = localColor.btnDown;
				break;
			case OFF:
			default:
				col = localColor.btnOff;
			}
			
			app.strokeWeight(1);
			app.stroke(localColor.btnBorder);			
			app.fill(col);	// depends on button state
			app.rect(pos.x,pos.y,width,height);
			app.noStroke();
			app.fill(localColor.btnFont);
			app.textFont(localFont, localFont.size);
			app.text(text, pos.x + (width - textWidth)/2, pos.y -1 + (height - localFont.size)/2, width, height);
		}
	}

	/**
	 * All GUI components are registered for mouseEvents
	 */
	public void mouseEvent(MouseEvent event){
		switch(event.getID()){
		case MouseEvent.MOUSE_PRESSED:
			if(focusIsWith != this && isOver(app.mouseX, app.mouseY)){
				mdx = app.mouseX;
				mdy = app.mouseY;
				status = DOWN;
				this.takeFocus();
			}
			break;
		case MouseEvent.MOUSE_CLICKED:
			// No need to test for isOver() since if the component has focus
			// the mouse has not moved since MOUSE_PRESSED	
			if(focusIsWith == this /* && isOver(app.mouseX, app.mouseY) */){
				status = OFF;
				fireEvent();
				this.looseFocus();
				mdx = mdy = Integer.MAX_VALUE;
			}
			break;
		case MouseEvent.MOUSE_RELEASED:	
			// if the mouse has moved then release focus otherwise
			// MOUSE_CLICKED will handle it
			if(focusIsWith == this && mouseHasMoved(app.mouseX, app.mouseY)){
				looseFocus();
				mdx = mdy = Integer.MAX_VALUE;
				status = OFF;
			}
			break;
		case MouseEvent.MOUSE_MOVED:
			// If dragged state will stay as DOWN
			if(isOver(app.mouseX, app.mouseY))
				status = OVER;
			else
				status = OFF;
		}
	}

	
}
