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
 * This class is the Button component.
 * 
 * The button face can have either text or an image or both just
 * pick the right constructor.
 * 
 * The image file can either be a single image which is used for 
 * all button states, or be a composite of 3 images (tiled horizontally)
 * which are used for the different button states OFF, OVER and DOWN 
 * in which case the image width should be divisible by 3
 * 
 * 
 * @author Peter Lager
 *
 */
public class GButton extends GComponent {

	// Button status values
	public static final int OFF		= 0;
	public static final int OVER	= 1;
	public static final int DOWN	= 2;

	protected int status;

	protected int[] col = new int[3];

	protected PImage[] bimage = new PImage[3];
	protected int btnImgWidth = 0;
	protected int imageAlign = GAlign.CENTER;

	protected int imgAlignX;
	
	/**
	 * Create a button with text only.
	 * 
	 * Height and width may increase depending on initial text a
	 * 
	 * @param theApplet
	 * @param text text appearing on the button
	 * @param x horz position of button
	 * @param y vert position
	 * @param width minimum width of button
	 * @param height minimum height of button
	 */
	public GButton(PApplet theApplet, String text, int x, int y, int width, int height){
		super(theApplet, x, y);
		setText(text);
		buttonCtorCore(width, height, null, 0);
	}

	/**
	 * Create a button with image only.
	 * 
	 * Height and width may increase depending on image size.
	 * 
	 * @param theApplet
	 * @param imgFile filename of image to use on the button
	 * @param nbrImages number of images in the filmstrip normally 1 or 3 (OFF OVER DOWN)
	 * @param x horz position of button
	 * @param y vert position
	 * @param width minimum width of button
	 * @param height minimum height of button
	 */
	public GButton(PApplet theApplet, String imgFile, int nbrImages, int x, int y, int width, int height){
		super(theApplet, x, y);
		PImage img = winApp.loadImage(imgFile);
		if(img == null){
			if(G4P.messages) System.out.println("Can't find image file for GButton");
		}
		else
			btnImgWidth = img.width /  nbrImages;
		buttonCtorCore(width, height, img, nbrImages);
	}

	/**
	 * Create a button with both text and image.
	 * 
	 * Height and width may increase depending on initial text length
	 * and image size.
	 * 
	 * @param theApplet
	 * @param text text appearing on the button
	 * @param imgFile filename of image to use on the button
	 * @param nbrImages number of images in the filmstrip normally 1 or 3 (OFF OVER DOWN)
	 * @param x horz position of button
	 * @param y vert position
	 * @param width minimum width of button
	 * @param height minimum height of button
	 */
	public GButton(PApplet theApplet, String text, String imgFile, int nbrImages, int x, int y, int width, int height){
		super(theApplet, x, y);
		setText(text);
		PImage img = winApp.loadImage(imgFile);
		if(img == null){
			if(G4P.messages) System.out.println("Can't find image file for GButton");
		}
		else
			btnImgWidth = img.width / nbrImages;
		buttonCtorCore(width, height, img, nbrImages);
	}

	/**
	 * 
	 * @param text
	 * @param width
	 * @param height
	 */
	private void buttonCtorCore(int width, int height, PImage img, int nbrImages) {
		col[0] = localColor.btnOff;
		col[1] = localColor.btnOver;
		col[2] = localColor.btnDown;

		// Check button is wide and tall enough for both text
		this.width = Math.max(width, textWidth + 2 * PADH);
		this.height = Math.max(height, localFont.size + 2 * PADV);
		// and now update for image/text combined
		if(img != null){
			this.width = Math.max(this.width, textWidth + btnImgWidth + 2 * PADH);
			this.height = Math.max(this.height, btnImgWidth + 2 * PADV);
			// See if we have multiple images
			for(int i = 0; i < nbrImages;  i++){
				bimage[i] = new PImage(btnImgWidth, img.height, ARGB);
				bimage[i].copy(img, 
						i * btnImgWidth, 0, btnImgWidth, img.height,
						0, 0, btnImgWidth, img.height);
			}
			for(int i = nbrImages; i < 3; i++){
				bimage[i] = bimage[nbrImages - 1];
			}
		}
		calcAlignX();
		createEventHandler(winApp, "handleButtonEvents", new Class[]{ GButton.class });
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
			if(G4P.messages) {
				System.out.println("The class " + obj.getClass().getSimpleName() + " does not have a method called " + methodName);
				System.out.println("with a parameter of type GButton");
			}
			eventHandlerObject = null;
		}
	}

	/**
	 * Set the color scheme for this button
	 */
	public void setColorScheme(int schemeNo){
		localColor = GCScheme.getColor(winApp, schemeNo);
		col[0] = localColor.btnOff;
		col[1] = localColor.btnOver;
		col[2] = localColor.btnDown;
	}

	/**
	 * @param text the text to set with alignment
	 */
	public void setText(String text) {
		this.text = text;
		winApp.textFont(localFont, localFont.size);
		textWidth = (int) winApp.textWidth(text);
		calcAlignX();
	}

	/**
	 * Set the font & size for the button increasing height and
	 * width of the button if necessary to display text. <br>
	 * It will not shrink if the font size is decreased.  
	 */
	public void setFont(String fontname, int fontsize){
		int tw = textWidth;
		int fs = (int) localFont.size;
		localFont = GFont.getFont(winApp, fontname, fontsize);
		if(fontsize > fs)
			height += (fontsize - fs);
		setText(text);
		if(textWidth > tw)
			width += (textWidth - tw);
	}

	/**
	 * Set the text alignment inside the box
	 * @param align
	 */
	public void setTextAlign(int align){
		// Ignore text alignment
	}

	/**
	 * Sets the position of the image in relation to the button text
	 * @param align either GAlign.LEFT or GAlign.RIGHT
	 */
	public void setImageAlign(int align){
		if(align == GAlign.LEFT || align == GAlign.RIGHT){			
			imageAlign = align;
			calcAlignX();
		}
	}
	/**
	 * Calculate text and image X alignment position
	 */
	protected void calcAlignX(){
		if(bimage[0] == null){
			// text only, centre it
			alignX = (width - textWidth)/2;
		}
		else if(bimage[0] != null && text.length() == 0){
			// Image only, centre it
			imageAlign = GAlign.CENTER;
			imgAlignX = (width - btnImgWidth)/2;
		}
		else {
			// text and image
			alignX = (width - btnImgWidth - textWidth)/2;
			switch(imageAlign){
			case GAlign.CENTER:
				imageAlign = GAlign.LEFT;
			case GAlign.LEFT:
				imgAlignX = PADH;
				alignX += btnImgWidth + PADH;
				break;
			case GAlign.RIGHT:
				imgAlignX = width - btnImgWidth - PADH;
				alignX += PADH;				
			}
		}
	}

	/**
	 * Draw the button
	 */
	public void draw(){
		if(!visible) return;
		winApp.pushStyle();
		winApp.style(G4P.g4pStyle);
		Point pos = new Point(0,0);
		calcAbsPosition(pos);
		// Draw button rectangle
		winApp.strokeWeight(1);
		winApp.stroke(localColor.btnBorder);			
		winApp.fill(col[status]);	// depends on button state
		winApp.rect(pos.x,pos.y,width,height);
		// Draw image
		if(bimage[status] != null){
			winApp.image(bimage[status], pos.x + imgAlignX, pos.y+(height-bimage[status].height)/2);
		}
		// Draw text
		winApp.noStroke();
		winApp.fill(localColor.btnFont);
		winApp.textFont(localFont, localFont.size);
		winApp.text(text, pos.x + alignX, pos.y + (height - localFont.size)/2 - PADV, width, height);
		winApp.popStyle();
	}


	/**
	 * All GUI components are registered for mouseEvents
	 */
	public void mouseEvent(MouseEvent event){
		if(!visible) return;

		boolean mouseOver = isOver(winApp.mouseX, winApp.mouseY);
		if(mouseOver) 
			cursorIsOver = this;
		else if(cursorIsOver == this)
				cursorIsOver = null;
		
		switch(event.getID()){
		case MouseEvent.MOUSE_PRESSED:
			if(focusIsWith != this && mouseOver){
				mdx = winApp.mouseX;
				mdy = winApp.mouseY;
				status = DOWN;
				takeFocus();
			}
			break;
		case MouseEvent.MOUSE_CLICKED:
			// No need to test for isOver() since if the component has focus
			// the mouse has not moved since MOUSE_PRESSED	
			if(focusIsWith == this){
				status = OFF;
				eventType = CLICKED;
				fireEvent();
				looseFocus(null);
				mdx = mdy = Integer.MAX_VALUE;
			}
			break;
		case MouseEvent.MOUSE_RELEASED:	
			// if the mouse has moved then release focus otherwise
			// MOUSE_CLICKED will handle it
			if(focusIsWith == this && mouseHasMoved(winApp.mouseX, winApp.mouseY)){
				looseFocus(null);
				mdx = mdy = Integer.MAX_VALUE;
				status = OFF;
			}
			break;
		case MouseEvent.MOUSE_MOVED:
			// If dragged state will stay as DOWN
			if(isOver(winApp.mouseX, winApp.mouseY))
				status = OVER;
			else
				status = OFF;
		}
	}

}
