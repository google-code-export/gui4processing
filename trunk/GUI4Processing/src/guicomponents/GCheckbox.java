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
import processing.core.PImage;

/**
 * The checkbox component
 * 
 * @author Peter Lager
 *
 */
public class GCheckbox extends GComponent {

	protected boolean selected;

	protected static PImage imgSelected;
	protected static PImage imgCleared;

	/**
	 * Create a check box.
	 * 
	 * The height will be calculated using the font height.
	 * Will use the default global font
	 * 
	 * @param theApplet
	 * @param text text to appear alongside checkbox
	 * @param x horz position
	 * @param y vert position
	 * @param width width of component
	 */
	public GCheckbox(PApplet theApplet, String text, int x, int y, int width){
		super(theApplet, x, y);
		checkboxCtorCore(text, width, 0);
	}

	/**
	 * Create a check box.
	 * 
	 * The height will be calculated using the font height
	 * 
	 * @param theApplet
	 * @param text text to appear alongside checkbox
	 * @param x horz position
	 * @param y vert position
	 * @param width width of component
	 * @param height height of component
	 */
	public GCheckbox(PApplet theApplet, String text, int x, int y, int width, int height){
		super(theApplet, x, y);
		checkboxCtorCore(text, width, height);
	}

	/**
	 * Core stuff to be done by all ctors
	 * @param text
	 * @param width
	 * @param height
	 */
	private void checkboxCtorCore(String text, int width, int height){
		if(imgSelected == null)
			imgSelected = app.loadImage("check1.png");
		if(imgCleared == null)
			imgCleared = app.loadImage("check0.png");
		this.width = width;
		this.height = localFont.size + 2 * PADV;
		if(height > this.height)
			this.height = height;
		opaque = false;
		setText(text);
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
			this.eventHandler = obj.getClass().getMethod(methodName, new Class[] { GCheckbox.class } );
			eventHandlerObject = obj;
		} catch (Exception e) {
			if(G4P.messages){
				System.out.println("The class " + obj.getClass().getSimpleName() + " does not have a method called " + methodName);
				System.out.println("with a single parameter of type GCheckbox");
			}
			eventHandlerObject = null;
		}
	}

	/**
	 * Create an event handler that will call a method handleCheckboxEvents(GCheckbox cbox)
	 * when text is changed or entered
	 * @param obj
	 */
	protected void createEventHandler(Object obj){
		try{
			this.eventHandler = obj.getClass().getMethod("handleCheckboxEvents", new Class[] { GCheckbox.class } );
			eventHandlerObject = obj;
		} catch (Exception e) {
			if(G4P.messages){
				System.out.println("You might want to add a method to handle \ncheckbox events the syntax is");
				System.out.println("void handleCheckboxEvents(GCheckbox cbox){\n   ...\n}\n\n");
			}
			eventHandlerObject = null;
		}
	}

	/**
	 * Calculate text X position based on text alignment
	 */
	protected void calcAlignX(){
		switch(textAlign){
		case GAlign.LEFT:
			alignX = imgSelected.width + 2 * border;
			break;
		case GAlign.RIGHT:
			alignX = width - textWidth - 2 * border;
			break;
		case GAlign.CENTER:
			alignX = imgSelected.width + (width - imgSelected.width - textWidth)/2;
			break;
		}
	}

	/**
	 * Draw the checkbox
	 */
	public void draw(){
		if(!visible) return;
		app.pushStyle();
		app.style(G4P.g4pStyle);
		Point pos = new Point(0,0);
		calcAbsPosition(pos);
		if (!text.equals("")){
			app.strokeWeight(border);
			app.stroke(localColor.cbxBorder);	
			if(opaque)
				app.fill(localColor.cbxBack);	// depends on button state
			else 
				app.noFill();
			app.rect(pos.x, pos.y, width, height);
			// Draw text
			app.noStroke();
			app.fill(localColor.cbxFont);
			app.textFont(localFont, localFont.size);
			app.text(text, pos.x + alignX, pos.y + (height - localFont.size)/2, textWidth, height);
		}
		app.fill(app.color(255,255));
		if(selected)
			app.image(imgSelected, pos.x, pos.y + (height - imgSelected.height)/2);
		else
			app.image(imgCleared, pos.x, pos.y + (height - imgSelected.height)/2);
		app.popStyle();
	}

	/**
	 * All GUI components are registered for mouseEvents
	 */
	public void mouseEvent(MouseEvent event){
		if(!visible) return;

		switch(event.getID()){
		case MouseEvent.MOUSE_PRESSED:
			if(focusIsWith != this && isOver(app.mouseX, app.mouseY)){
				mdx = app.mouseX;
				mdy = app.mouseY;
				this.takeFocus();
			}
			break;
		case MouseEvent.MOUSE_CLICKED:
			if(focusIsWith == this /*&& isOver(app.mouseX, app.mouseY)*/){
				selected = !selected;
				fireEvent();
				this.looseFocus(null);
				mdx = mdy = Integer.MAX_VALUE;
			}
			break;
		case MouseEvent.MOUSE_RELEASED:
			if(focusIsWith == this && mouseHasMoved(app.mouseX, app.mouseY)){
				this.looseFocus(null);
				mdx = mdy = Integer.MAX_VALUE;
			}
			break;
		}
	}

	public boolean isSelected(){
		return selected;
	}

	public void setSelected(boolean selected){
		this.selected = selected;
	}

}
