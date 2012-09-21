/*
  Part of the GUI for Processing library 
  	http://www.lagers.org.uk/g4p/index.html
	http://gui4processing.googlecode.com/svn/trunk/

  Copyright (c) 2012 Peter Lager

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

import guicomponents.HotSpot.HSrect;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.Iterator;
import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PGraphicsJava2D;


/**
 * A component that can be used to group GUI components that can be
 * dragged, collapsed (leaves title tab only) and un-collapsed.
 * 
 * When created the Panel is collapsed by default. To open the panel
 * use setCollapsed(true); after creating it.
 * 
 * Unlike all the other components the [x,y] coordinates do not represent
 * the top-left corner of the control rather the top-left corner of the 
 * panel drawing surface (which is the bottom left corner of the tab)
 *  
 * @author Peter Lager
 *
 */
public class FPanel extends FTextControl {


	/** Whether the panel is displayed in full or tab only */
	protected boolean tabOnly = true;

	/** The height of the tab calculated from font height + padding */
	protected int tabHeight, tabWidth;

	/** Used to restore position when closing panel */
	protected float dockX, dockY;

	/** true if the panel is being dragged */
	protected boolean beingDragged = false;

	/**
	 * Create a Panel that comprises of 2 parts the tab which is used to 
	 * select and move the panel and the container window below the tab which 
	 * is used to hold other components.
	 *  
	 * @param theApplet the PApplet reference
	 * @param x horizontal position
	 * @param y vertical position
	 * @param width width of the panel
	 * @param height height of the panel (excl. tab)
	 * @param text to appear on tab
	 */
	public FPanel(PApplet theApplet, float p0, float p1, float p2, float p3, String text) {
		super(theApplet, p0, p1, p2, p3);
		children = new LinkedList<FAbstractControl>();
		childLimit = Integer.MAX_VALUE;
//		if(text == null || text.length() == 0)
//			text = "Tab Text";
//		this.text = text;
		// The image buffer is just for the tab area
		buffer = (PGraphicsJava2D) winApp.createGraphics((int)width, (int)height, PApplet.JAVA2D);
		buffer.rectMode(PApplet.CORNER);
		buffer.g2.setFont(localFont);
		setTextNew(text);
		calcHotSpots();
		constrainPanelPosition();
		opaque = true;
		dockX = x;
		dockY = y;
		z = Z_PANEL;

		createEventHandler(winApp, "handleButtonEvents", new Class[]{ FPanel.class });
		registeredMethods = DRAW_METHOD | MOUSE_METHOD;
		F4P.addControl(this);
	}

	public FPanel setTextNew(String text){
		super.setTextNew(text);
		stext.getLines(buffer.g2);
		tabHeight = (int) (stext.getMaxLineHeight() + 4);
		tabWidth = (int) (stext.getMaxLineLength() + 8);
		return this;
	}

	/**
	 * This needs to be called if the tab text is changed
	 */
	private void calcHotSpots(){
		hotspots = new HotSpot[]{
				new HSrect(1, 0, 0, tabWidth, tabHeight),					// tab text area
				new HSrect(2, tabWidth, 0, width - tabWidth, tabHeight),	// tab non-text area
				new HSrect(3, 0, tabHeight, width, height - tabHeight)		// panel content surface
		};
	}

	public FTextControl setFontNew(Font font) {
		if(font != null)
			localFont = font;
		tabHeight = (int) (1.2f * localFont.getSize() + 2);
		if(buffer != null){
			buffer.g2.setFont(localFont);
			bufferInvalid = true;
		}
		return this;
	}

	/**
	 * Bring this panel to the front i.e. above other panels.
	 */
//	public void bringToFront(){
//		if(parent != null)
//			parent.bringToFront();
//		G4P.moveToFrontForDraw(this);
//	}

	/**
	 * What to do when the FPanel loses focus.
	 */
	protected void loseFocus(FAbstractControl grabber){
		focusIsWith = null;
		beingDragged = false;
	}

	//	protected void takeFocus(){
	//		super.takeFocus();
	//		G4P.moveToFrontForDraw(this);
	//	}

	/**
	 * Draw the panel.
	 * If tabOnly == true 
	 * 		then display the tab only
	 * else
	 * 		draw tab and all child (added) components
	 */
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
		winApp.tint(-1, alphaLevel);
		winApp.image(buffer, 0, 0);	
		winApp.popMatrix();

		if(!tabOnly){
			if(children != null){
				for(FAbstractControl c : children)
					c.draw();
			}
		}
		winApp.popMatrix();
		winApp.popStyle();
	}

	protected void updateBuffer(){
		if(bufferInvalid) {
			Graphics2D g2d = buffer.g2;
			buffer.beginDraw();

			buffer.background(buffer.color(255,0));
			buffer.noStroke();
			buffer.fill(palette[4]);
			if(tabOnly){
				buffer.rect(0, 0, tabWidth, tabHeight);	
			}
			else {
				buffer.rect(0, 0, width, tabHeight);
			}
			stext.getLines(g2d);
			g2d.setColor(jpalette[12]);
			TextLayout tl = stext.getTLIforLineNo(0).layout;
			tl.draw(g2d, 4, 2 + tl.getAscent());

			if(!tabOnly){
				buffer.noStroke();
				buffer.fill(palette[5]);
				buffer.rect(0, tabHeight, width, height - tabHeight);
			}
			buffer.endDraw();
		}	
	}

	/**
	 * All GUI components are registered for mouseEvents
	 */
	public void mouseEvent(MouseEvent event){
		if(!visible  || !enabled || !available) return;

		calcTransformedOrigin(winApp.mouseX, winApp.mouseY);

		currSpot = whichHotSpot(ox, oy);
		// Is mouse over the panel tab (taking into account extended with when not collapsed)
		boolean mouseOverTab = (currSpot == 1  || (currSpot == 2 && !tabOnly));
		// Is the mouse anywhere over the panel (taking into account whether the panel is
		// collapsed or not)
		boolean mouseOverPanel = (currSpot == 1  && tabOnly) || (currSpot > 0 && !tabOnly);

		if(mouseOverPanel || focusIsWith == this)
			cursorIsOver = this;
		else if(cursorIsOver == this)
			cursorIsOver = null;

		if(mouseOverPanel || focusIsWith == this) 
			cursorIsOver = this;
		else if(cursorIsOver == this)
			cursorIsOver = null;

		switch(event.getID()){
		case MouseEvent.MOUSE_PRESSED:
			if(focusIsWith != this && mouseOverPanel && z >= focusObjectZ()){
//				mdx = winApp.mouseX;
//				mdy = winApp.mouseY;
				takeFocus();
				beingDragged = false;
			}
			// If focus is with some other control with the same depth and the mouse is over the panel
			// Used to ensure that GTextField controls on GPanels release focus
			if(focusIsWith != null && focusIsWith != this && z == focusObjectZ() && currSpot >= 0)
				focusIsWith.loseFocus(null);
			break;
		case MouseEvent.MOUSE_CLICKED:
			if(focusIsWith == this && mouseOverTab){
				tabOnly = !tabOnly;
				// Perform appropriate action depending on collapse state
				setCollapsed(tabOnly);
				if(tabOnly){
					x = dockX;
					y = dockY;
				}
				else {
					dockX = x;
					dockY = y;
					// Open panel move on screen if needed
					if(y + height > winApp.getHeight())
						y = winApp.getHeight() - height;
					if(x + width > winApp.getWidth())
						x = winApp.getWidth() - width;
				}
				//				constrainPanelPosition();
				if(tabOnly)
					eventType = COLLAPSED;
				else
					eventType = EXPANDED;
				// fire an event
				fireEvent();
				// This component does not keep the focus when clicked
				loseFocus(null);
//				mdx = mdy = Integer.MAX_VALUE;
			}
			break;
		case MouseEvent.MOUSE_RELEASED: // After dragging NOT clicking
			if(focusIsWith == this){
				if(beingDragged){
					beingDragged = false;
					loseFocus(null);
				}
			}
			break;
		case MouseEvent.MOUSE_DRAGGED:
			if(focusIsWith == this && parent == null){
				x += (winApp.mouseX - winApp.pmouseX);
				y += (winApp.mouseY - winApp.pmouseY);
				constrainPanelPosition();
				beingDragged = true;
				eventType = DRAGGED;
				fireEvent();
				if(!tabOnly){
					dockX = x;
					dockY = y;
				}
			}
			break;
		}
		// Maintain centre for drawing purposes
		cx = x + width/2;
		cy = y + height/2;
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
	 * Ensures that the panel tab and panel body if open does not
	 * extend off the screen.
	 */
	private void constrainPanelPosition(){
		int w = (int) ((tabOnly)? tabWidth : width);
		int h = (int) ((tabOnly)? tabHeight : height);
		// Constrain horizontally
		if(x < 0) 
			x = 0;
		else if(x + w > winApp.getWidth()) 
			x = (int) (winApp.getWidth() - w);
		// Constrain vertically
		if(y < 0) 
			y = 0;
		else if(y + h > winApp.getHeight()) 
			y = winApp.getHeight() - h;
	}

	/**
	 * Collapse or open the panel
	 * @param collapse
	 */
	public void setCollapsed(boolean collapse){
		tabOnly = collapse;
		// If we open the panel make sure it fits on the screen but if we
		// collapse the panel disable the panel controls
		if(tabOnly){
			setAvailable(false);
			available = true;
		}
		else {
			constrainPanelPosition();
			setAvailable(true);
		}
	}

	/**
	 * Find out if the panel is collapsed
	 * @return true if collapsed
	 */
	public boolean isCollapsed(){
		return tabOnly;
	}

	public int getTabHeight(){
		return tabHeight;
	}

}
