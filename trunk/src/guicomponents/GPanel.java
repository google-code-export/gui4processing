/*
  Part of the GUI for Processing library 
  	http://gui4processing.lagers.org.uk
	http://code.google.com/p/gui4processing/
	
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
import java.util.ArrayList;
import java.util.Iterator;

import processing.core.PApplet;

/**
 * Core component
 * 
 * @author Peter Lager
 *
 */
public class GPanel extends GComponent {

	/** Whether the panel is displayed in full or tab only */
	protected boolean tabOnly = true;

	/** The height of the tab calculated from font height + padding */
	protected int tabHeight;
	
	/** Is panel body opaque */
	protected boolean opaque = false;
	
	/** Used to restore position when closing panel */
	protected int dockX, dockY;

	/**
	 * A list of child GComponents added to this panel
	 * Only applicable to GPanel class
	 */
	private ArrayList<GComponent> children = new ArrayList<GComponent>();

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
	 * @param width minimum width of the panel
	 * @param height minimum height of the panel (excl. tab)
	 * @param colorScheme color to be used
	 * @param fontScheme font to be used
	 */
	public GPanel(PApplet theApplet, String text, int x, int y, int width, int height,
			GColor colorScheme, GFont fontScheme){
		super(theApplet, x, y, colorScheme, fontScheme);
		panelCtorCore(text, width, height);
	}

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
	 * @param width minimum width of the panel
	 * @param height minimum height of the panel (excl. tab)
	 */
	public GPanel(PApplet theApplet, String text, int x, int y, int width, int height){
		super(theApplet, x, y);
		panelCtorCore(text, width, height);
	}

	private void panelCtorCore(String text, int width, int height){
		setText(text);
		tabHeight = localFont.gpFontSize + 2 * PADV;
		constrainPanelPosition();
		dockX = x;
		dockY = y;
		this.width = width;
		this.height = height;
		app.registerDraw(this);
		app.registerMouseEvent(this);
	}
	
	/**
	 * Add a GUI component to this Panel at the position specified by
	 * component being added.
	 * Unregister the component for drawing this is managed by the 
	 * Panel draw method to preserve z-ordering
	 * 
	 * @return always true
	 */
	public boolean add(GComponent component){
		// TODO need to validate addition based on size
		component.parent = this;
		children.add(component);
		app.unregisterDraw(component);
		return true;
	}

	/**
	 * Draw the panel tab.
	 * If tabOnly == false then also draw all child (added) components
	 */
	public void draw(){
		if(visible){
			Point pos = new Point(0,0);
			calcAbsPosition(pos);
			app.noStroke();
			app.fill(localColor.panelTab);
			// Display tab (length depends on whether panel is open or closed
			int w = (tabOnly)? textWidth + PADH * 2 : width;
			app.rect(pos.x, pos.y - tabHeight, w, tabHeight);
			// Display tab text
			app.fill(localColor.panelTabFont);
			app.textFont(localFont.gpFont, localFont.gpFontSize);
			app.text(getText(), pos.x + PADH, pos.y - tabHeight + PADV, textWidth, tabHeight);
			if(!tabOnly){
				if(opaque){
					app.fill(localColor.panel);
					app.rect(pos.x, pos.y, width, height);
				}
				Iterator<GComponent> iter = children.iterator();
				while(iter.hasNext()){
					iter.next().draw();
				}
			}
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
			if(focusIsWith == this){
				tabOnly = !tabOnly;
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
		case MouseEvent.MOUSE_DRAGGED:
			if(focusIsWith == this && parent == null){
				x += (app.mouseX - app.pmouseX);
				y += (app.mouseY - app.pmouseY);
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
		if(y - tabHeight - PADV * 2  < 0) 
			y = tabHeight + PADV * 2;
		else if(y + h > app.getHeight()) 
			y = app.getHeight() - h;
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

	public void setOpaque(boolean opaque){
		this.opaque = opaque;
	}

} // end of class
