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
import processing.core.PFont;
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
	
	
	public GCheckbox(PApplet theApplet, String text, int x, int y, int width, int align,
			GCScheme colors, PFont font){
		super(theApplet, x, y, colors, font);
		checkboxCtorCore(text, width, align);
	}

	public GCheckbox(PApplet theApplet, String text, int x, int y, int width,
			GCScheme colors, PFont font){
		super(theApplet, x, y, colors, font);
		checkboxCtorCore(text, width, GTAlign.LEFT);
	}

	public GCheckbox(PApplet theApplet, String text, int x, int y, int width){
		super(theApplet, x, y);
		checkboxCtorCore(text, width, GTAlign.LEFT);
	}

	public GCheckbox(PApplet theApplet, String text, int x, int y, int width, int align){
		super(theApplet, x, y);
		checkboxCtorCore(text, width, align);
	}

	private void checkboxCtorCore(String text, int width, int align){
		this.width = width;
		height = localFont.size + 2 * PADV;
		setText(text, align);
		if(imgSelected == null)
			imgSelected = app.loadImage("check1.png");
		if(imgCleared == null)
			imgCleared = app.loadImage("check0.png");
		createEventHandler(app);
		registerAutos_DMPK(true, true, false, false);
	}
	
	public void addEventHandler(Object obj, String methodName){
		try{
			this.eventHandler = obj.getClass().getMethod(methodName, new Class[] { GCheckbox.class } );
			eventHandlerObject = obj;
		} catch (Exception e) {
			System.out.println("The class " + obj.getClass().getSimpleName() + " does not have a method called " + methodName);
			System.out.println("with a single parameter of type GCheckbox");
			eventHandlerObject = null;
		}
	}
	
	protected void createEventHandler(Object obj){
		try{
			this.eventHandler = obj.getClass().getMethod("handleCheckboxEvents", new Class[] { GCheckbox.class } );
			eventHandlerObject = obj;
		} catch (Exception e) {
			eventHandlerObject = null;
			System.out.println("You might want to add a method to handle \ncheckbox events the syntax is");
			System.out.println("void handleCheckboxEvents(GCheckbox cbox){\n   ...\n}\n\n");
		}
	}
	
	public void draw(){
		if(visible){
			Point pos = new Point(0,0);
			calcAbsPosition(pos);
			if (!text.equals("")){
				app.noStroke();
				app.fill(localColor.pnlForeground);
				app.textFont(localFont, localFont.size);
				app.text(text, pos.x + 20, pos.y - 1, textWidth, height);
			}
			app.fill(app.color(255,255));
			if(selected)
				app.image(imgSelected, pos.x, pos.y);
			else
				app.image(imgCleared, pos.x, pos.y);
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
				this.takeFocus();
			}
			break;
		case MouseEvent.MOUSE_CLICKED:
			if(focusIsWith == this /*&& isOver(app.mouseX, app.mouseY)*/){
				selected = !selected;
				fireEvent();
				this.looseFocus();
				mdx = mdy = Integer.MAX_VALUE;
			}
			break;
		case MouseEvent.MOUSE_RELEASED:
			if(focusIsWith == this && mouseHasMoved(app.mouseX, app.mouseY)){
				this.looseFocus();
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
