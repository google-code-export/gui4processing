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
 * The option button class. This is used with the GOptionGroup
 * class to provide sets of options.
 * 
 * @author Peter Lager
 *
 */
public class GOption extends GComponent {
	/**
	 * All GOption objects should belong to a group
	 */
	protected GOptionGroup ownerGroup;

	// Images used for selected/deselected option
	protected static PImage imgSelected;
	protected static PImage imgCleared;


	/**
	 * Create an option button
	 * 
	 * @param theApplet
	 * @param text
	 * @param x
	 * @param y
	 * @param width
	 */
	public GOption(PApplet theApplet, String text, int x, int y, int width){
		super(theApplet, x, y);
		optionCtorCore(text, width, 0);
	}

	/**
	 * Code common to all ctors
	 * @param text
	 * @param width
	 * @param height
	 */
	private void optionCtorCore(String text, int width, int height){
		if(imgSelected == null)
			imgSelected = app.loadImage("radio1.png");
		if(imgCleared == null)
			imgCleared = app.loadImage("radio0.png");
		this.width = width;
		this.height = localFont.size + 2 * PADV;
		if(height > this.height)
			this.height = height;
		opaque = false;
		setText(text);
		createEventHandler(app);
		registerAutos_DMPK(true, true, false, false);
	}

	public void addEventHandler(Object obj, String methodName){
		try{
			this.eventHandler = obj.getClass().getMethod(methodName, new Class[] { GOption.class, GOption.class } );
			eventHandlerObject = obj;
		} catch (Exception e) {
			if(G4P.messages){
				System.out.println("The class " + obj.getClass().getSimpleName() + " does not have a method called " + methodName);
				System.out.println("with a two parameters of type GOption");
			}
			eventHandlerObject = null;
		}
	}

	protected void createEventHandler(Object obj){
		try{
			this.eventHandler = obj.getClass().getMethod("handleOptionEvents", new Class[] { GOption.class, GOption.class } );
			eventHandlerObject = obj;
		} catch (Exception e) {
			if(G4P.messages){
				System.out.println("You might want to add a method to handle \noption events the syntax is");
				System.out.println("void handleOptionEvents(GOption selected, GOption deselected){\n   ...\n}\n\n");
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
			alignX = imgSelected.width + 2 * border + PADV;
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
	 * draw the option
	 */
	public void draw(){
		if(!visible) return;

		app.pushStyle();
		app.style(G4P.g4pStyle);
		Point pos = new Point(0,0);
		calcAbsPosition(pos);
		if (!text.equals("")){
			if(border == 0){
				app.noStroke();					
			}
			else {
				app.stroke(localColor.btnBorder);
				app.strokeWeight(border);					
			}
			if(opaque)
				app.fill(localColor.txfBack);
			else
				app.noFill();
			app.rect(pos.x, pos.y, width, height);
			// Draw text
			app.noStroke();
			app.fill(localColor.optFont);
			app.textFont(localFont, localFont.size);
//				app.text(text, pos.x + alignX, pos.y + (height - localFont.size)/2, textWidth, height);
			app.text(text, pos.x + alignX, pos.y + (height - localFont.size)/2 - PADV, width - imgSelected.width, height);
		}
		app.fill(app.color(255,255));
		if(ownerGroup != null && ownerGroup.selectedOption() == this)
			app.image(imgSelected, pos.x + 1, pos.y + (height - imgSelected.height)/2);
		else
			app.image(imgCleared, pos.x + 1, pos.y + (height - imgSelected.height)/2);
		app.popStyle();

	}


	/**
	 * All GUI components are registered for mouseEvents
	 */
	public void mouseEvent(MouseEvent event){
		// If this option does not belong to a group then ignore mouseEvents
		if(!visible || ownerGroup == null) return;
		switch(event.getID()){
		case MouseEvent.MOUSE_PRESSED:
			if(focusIsWith != this && isOver(app.mouseX, app.mouseY)){
				mdx = app.mouseX;
				mdy = app.mouseY;
				this.takeFocus();
			}
			break;
		case MouseEvent.MOUSE_CLICKED:
			if(focusIsWith == this){
				ownerGroup.setSelected(this);
				this.looseFocus(null);
				mdx = mdy = Integer.MAX_VALUE;
				fireEvent();
			}
			break;
		case MouseEvent.MOUSE_RELEASED:
			if(focusIsWith == this && mouseHasMoved(app.mouseX, app.mouseY)){
				mdx = mdy = Integer.MAX_VALUE;
				this.looseFocus(null);
			}
			break;
		}
	}

	/**
	 * Fire an event for this component which has a reference to the
	 * option being deselected as well as the option being selected.
	 * 
	 */
	protected void fireEvent(){
		if(eventHandler != null){
			try {
				eventHandler.invoke(eventHandlerObject, 
						new Object[] { this, ownerGroup.deselectedOption() });
			} catch (Exception e) {
				System.out.println("Disabling " + eventHandler.getName() + " due to an error");
				eventHandler = null;
				eventHandlerObject = null;
			}
		}		
	}

	/**
	 * Find out if this option is selected
	 * @return
	 */
	public boolean isSelected(){
		return (ownerGroup != null && ownerGroup.selectedOption() == this);
	}

	/**
	 * Find out if this object is deselected
	 * @return
	 */
	public boolean isNotSelected(){
		return !(ownerGroup != null && ownerGroup.selectedOption() == this);		
	}

	/**
	 * User can make this option selected - this does not cause
	 * events being fired
	 * 
	 * @param selected
	 */
	public void setSelected(boolean selected){
		if(ownerGroup != null){
			ownerGroup.setSelected(this);
		}
	}

	/**
	 * Get the option group that owns this option
	 * 
	 * @return
	 */
	public GOptionGroup getGroup(){
		return ownerGroup;
	}

	/**
	 * Set the option group - at the present this method does not allow the option
	 * to be moved from one group to another.
	 * @param group
	 */
	public void setGroup(GOptionGroup group){
		this.ownerGroup = group;
		//group.addOption(this);
	}

}
