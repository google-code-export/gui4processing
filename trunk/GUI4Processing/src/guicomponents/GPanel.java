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
import java.util.HashSet;
import java.util.Iterator;

import processing.core.PApplet;

/**
 * A component that can be used to group GUI components that can be
 * dragged, collapsed (leaves title tab only) and un-collapsed.
 * 
 * When created the Panel is collapsed by default. To open the panel
 * use setCollapsed(true); after creating it.
 * 
 * @author Peter Lager
 *
 */
public class GPanel extends GComponent {

	/** Whether the panel is displayed in full or tab only */
	protected boolean tabOnly = true;

	/** The height of the tab calculated from font height + padding */
	protected int tabHeight;

	/** Used to restore position when closing panel */
	protected int dockX, dockY;

	/** true if the panel is being dragged */
	protected boolean beingDragged = false;

	/**
	 * Create a Panel that comprises of 2 parts the tab which is used to 
	 * select and move the panel and the container window below the tab which 
	 * is used to hold other components.
	 * The size of the container window will grow to fit components added
	 * provided that it does not exceed the width and height of the applet
	 * window.
	 *  
	 * @param theApplet the PApplet reference
	 * @param text to appear on tab
	 * @param x horizontal position
	 * @param y vertical position
	 * @param width width of the panel
	 * @param height height of the panel (excl. tab)
	 */
	public GPanel(PApplet theApplet, String text, int x, int y, int width, int height){
		super(theApplet, x, y);
		panelCtorCore(text, width, height);
	}

	/**
	 * Code common for all constructors.
	 * @param text to appear on the tab
	 * @param width
	 * @param height
	 */
	private void panelCtorCore(String text, int width, int height){
		children = new HashSet<GComponent>();
		setText(text);
		tabHeight = localFont.size + 2 * PADV;
		constrainPanelPosition();
		createEventHandler(app);
		opaque = true;
		dockX = x;
		dockY = y;
		this.width = width;
		this.height = height;
		registerAutos_DMPK(true, true, false, false);
	}

	/**
	 * Create an event handler that will call a method handlePanelEvents(GPanle panel)
	 * when paned is opned or closed
	 * @param obj
	 */
	protected void createEventHandler(Object obj){
		try{
			this.eventHandler = obj.getClass().getMethod("handlePanelEvents", new Class[] { GPanel.class } );
			eventHandlerObject = obj;
		} catch (Exception e) {
			if(G4P.messages){
				System.out.println("You might want to add a method to handle \npanel events the syntax is");
				System.out.println("void handlePanelEvents(GPanle panel){\n   ...\n}\n\n");
			}
			eventHandlerObject = null;
		}
	}	

	/**
	 * Override the default event handler created with createEventHandler(Object obj)
	 * @param obj
	 * @param methodName
	 */
	public void addEventHandler(Object obj, String methodName){
		try{
			this.eventHandler = obj.getClass().getMethod(methodName, new Class[] { GPanel.class } );
			eventHandlerObject = obj;
		} catch (Exception e) {
			if(G4P.messages){
				System.out.println("The class " + obj.getClass().getSimpleName() + " does not have a method called " + methodName);
				System.out.println("with a parameter of type GPanel");
			}
			eventHandlerObject = null;
		}
	}

	/**
	 * Set the font & size for the tab text changing the height (+/-) 
	 * of the tab if necessary to display text.  
	 */
	public void setFont(String fontname, int fontsize){
		int fs = (int) localFont.size;
		localFont = GFont.getFont(app, fontname, fontsize);
		if(fontsize != fs)
			tabHeight += (fontsize - fs);
		setText(text);
	}

	/**
	 * What to do when the GPanel looses focus.
	 */
	protected void looseFocus(GComponent grabber){
		focusIsWith = null;
		beingDragged = false;
	}


	/**
	 * Draw the panel.
	 * If tabOnly == true 
	 * 		then display the tab only
	 * else
	 * 		draw tab and all child (added) components
	 */
	public void draw(){
		if(!visible) return;

		app.pushStyle();
		app.style(G4P.g4pStyle);
		Point pos = new Point(0,0);
		calcAbsPosition(pos);
		app.noStroke();
		if(border > 0){
			app.strokeWeight(border);
			app.stroke(localColor.pnlBorder);
		}
		app.fill(localColor.pnlTabBack);
		// Display tab (length depends on whether panel is open or closed
		int w = (tabOnly)? textWidth + PADH * 2 : width;
		app.rect(pos.x, pos.y - tabHeight, w, tabHeight);
		// Display tab text
		app.fill(localColor.pnlFont);
		app.textFont(localFont, localFont.size);
		app.text(text, pos.x + PADH, pos.y - (tabHeight + localFont.size)/2 - PADV , textWidth, tabHeight);
		if(!tabOnly){
			if(opaque){
				app.fill(localColor.pnlBack);
				app.rect(pos.x, pos.y, width, height);
			}
			Iterator<GComponent> iter = children.iterator();
			while(iter.hasNext()){
				iter.next().draw();
			}
		}
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
				takeFocus();
				// May become true but will soon be set to false when
				// we loose focus
				beingDragged = true;
			}
			break;
		case MouseEvent.MOUSE_CLICKED:
			if(focusIsWith == this){
				tabOnly = !tabOnly;
				// fire an event
				fireEvent();
				if(tabOnly){
					x = dockX;
					y = dockY;					
				}
				else {
					dockX = x;
					dockY = y;
					// Open panel move on screen if needed
					if(y + height > app.getHeight())
						y = app.getHeight() - height;
					if(x + width > app.getWidth())
						x = app.getWidth() - width;
				}
				// This component does not keep the focus when clicked
				looseFocus(null);
				mdx = mdy = Integer.MAX_VALUE;
			}
			break;
		case MouseEvent.MOUSE_RELEASED:
			if(focusIsWith == this){
				if(mouseHasMoved(app.mouseX, app.mouseY)){
					mdx = mdy = Integer.MAX_VALUE;
					looseFocus(null);
				}
			}
			break;
		case MouseEvent.MOUSE_DRAGGED:
			if(focusIsWith == this && parent == null){
				x += (app.mouseX - app.pmouseX);
				y += (app.mouseY - app.pmouseY);
				beingDragged = true;
				constrainPanelPosition();
				if(!tabOnly){
					dockX = x;
					dockY = y;
				}
			}
			break;
		}
	}

	/**
	 * This method is used to discover whether the panel is being 
	 * dragged to a new position on the screen.
	 * @return true if being dragged to a new position
	 */
	public boolean isDragging(){
		return beingDragged;
	}

	/**
	 * Ensures that the panel tab and panel body if open doesnot
	 * extend off the screen.
	 */
	private void constrainPanelPosition(){
		int w = (tabOnly)? textWidth + PADH * 2 : width;
		int h = (tabOnly)? 0 : height;
		// Constrain horizontally
		if(x < 0) 
			x = 0;
		else if(x + w > app.getWidth()) 
			x = (int) (app.getWidth() - w);
		// Constrain vertically
		if(y - tabHeight  < 0) 
			y = tabHeight;
		else if(y + h > app.getHeight()) 
			y = app.getHeight() - h;
//		if(y - tabHeight - PADV * 2  < 0) 
//			y = tabHeight + PADV * 2;
//		else if(y + h > app.getHeight()) 
//			y = app.getHeight() - h;
	}

	/**
	 * Determines whether the position ax, ay is over the tab
	 * of this GPanel.
	 * @return true if mouse is over the panel tab else fale
	 */
	public boolean isOver(int ax, int ay){
		Point p = new Point(0,0);
		calcAbsPosition(p);
		int w = (tabOnly)? textWidth + PADH * 2 : width;
		if(ax >= p.x && ax <= p.x + w && ay >= p.y - tabHeight && ay <= p.y)
			return true;
		else
			return false;
	}

	/**
	 * For GPanel set children
	 * @param visible the visible to set
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * Controls the transparency of this panel and all the
	 * components on it.
	 * 0 = fully transparent
	 * 255 = fully opaque
	 * 
	 * @param alpha
	 */
	public void setAlpha(int alpha){
		localColor.setAlpha(alpha);
		if(!children.isEmpty()){
			Iterator<GComponent> iter = children.iterator();
			while(iter.hasNext())
				iter.next().setAlpha(alpha);
		}
	}

	/**
	 * Collapse or open the panel
	 * @param collapse
	 */
	public void setCollapsed(boolean collapse){
		tabOnly = collapse;
		// If we open the panel make sure it fits on the screen
		if(!tabOnly)
			constrainPanelPosition();
	}

	/**
	 * Find out if the panel is collapsed
	 * @return true if collapsed
	 */
	public boolean isCollapsed(){
		return tabOnly;
	}

} // end of class
