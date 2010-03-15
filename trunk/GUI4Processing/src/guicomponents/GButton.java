/*
  Part of the GUI for Processing library 
   	http://www.lagers.org.uk/g4p/index.html
	http://gui4processing.googlecode.com/svn/trunk/

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

	protected PImage[] bimage = new PImage[3];
	protected int btnImgWidth = 0;
	protected int imageAlign = GAlign.CENTER;
	protected boolean useImages = false;
	
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
		buttonCtorCore(width, height);
	}

	/**
	 * Create a button with image only.
	 * 
	 * Height and width may increase depending on image size.
	 * 
	 * @param theApplet
	 * @param imgFile filename of image to use on the button
	 * @param nbrImages number of images in the film strip normally 1 or 3 (OFF OVER DOWN)
	 * @param x horz position of button
	 * @param y vert position
	 * @param width minimum width of button
	 * @param height minimum height of button
	 */
	public GButton(PApplet theApplet, String imgFile, int nbrImages, int x, int y, int width, int height){
		super(theApplet, x, y);
		setImages(imgFile, nbrImages);
		btnImgWidth = this.getButtonImageWidth();
		useImages = (btnImgWidth > 0);
		buttonCtorCore(width, height);
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
	 * @param nbrImages number of images in the film strip normally 1 or 3 (OFF OVER DOWN)
	 * @param x horz position of button
	 * @param y vert position
	 * @param width minimum width of button
	 * @param height minimum height of button
	 */
	public GButton(PApplet theApplet, String text, String imgFile, int nbrImages, int x, int y, int width, int height){
		super(theApplet, x, y);
		setImages(imgFile, nbrImages);
		btnImgWidth = this.getButtonImageWidth();
		useImages = (btnImgWidth > 0);
		setText(text);
		buttonCtorCore(width, height);
	}

	/**
	 * 
	 * @param width
	 * @param height
	 */
	private void buttonCtorCore(int width, int height) {
		// Check button is wide and tall enough for both text
		this.width = Math.max(width, textWidth + 2 * PADH);
		this.height = Math.max(height, localFont.getFont().getSize() + 2 * PADV);
		// and now update for image/text combined
		calcAlignX();
		createEventHandler(winApp, "handleButtonEvents", new Class[]{ GButton.class });
		registerAutos_DMPK(true, true, false, false);
	}

	public void setColours(int normal, int mouseOver, int pressed){
		localColor.btnOff = normal;
		localColor.btnOver = mouseOver;
		localColor.btnDown = pressed;
	}

	protected int getButtonImageWidth(){
		int bw = 0;
		for(int i = 0; i < 3; i++)
			bw = Math.max(bw, ((bimage[i] == null) ? 0 : bimage[i].width));
		return bw;		
	}
	
	
	public void setImages(String ifileNormal, String ifileOver, String ifilePressed){
		bimage[0] = winApp.loadImage(ifileNormal);
		if(bimage[0] == null)
			if(G4P.messages) System.out.println("Can't find normal button image file");
		bimage[1] = winApp.loadImage(ifileOver);
		if(bimage[2] == null)
			if(G4P.messages) System.out.println("Can't find over button image file");
		bimage[0] = winApp.loadImage(ifilePressed);
		if(bimage[2] == null)
			if(G4P.messages) System.out.println("Can't find pressed button image file");
	}
	
	public void setImages(String[] imgFiles){
		if(imgFiles != null && imgFiles.length > 1)
			setImages(imgFiles[0], imgFiles[1 % imgFiles.length], imgFiles[2 % imgFiles.length]);
	}
	
	public void setImages(String imgFile, int nbrImages){
		if(imgFile != null && nbrImages > 0){
			PImage img = winApp.loadImage(imgFile);
			if(img == null){
				if(G4P.messages) System.out.println("Can't find button image file");
			}
			else
				setImages(img, nbrImages);
		}
	}
	
	public void setImages(PImage imgNormal, PImage imgOver, PImage imgPressed){
		bimage[0] = imgNormal;
		bimage[1] = imgOver;
		bimage[2] = imgPressed;
	}
	
	public void setImages(PImage[] images){
		if(images != null){
			for(int i = 0; i < images.length ; i++)
				bimage[i] = images[i];
			for(int i = images.length; i < 3; i++)
				bimage[i] = bimage[images.length - 1];
		}
	}
	
	public void setImages(PImage img, int nbrImages){
		if(img != null && nbrImages > 0){
			int iw = img.width / nbrImages;
			System.out.println("Width "+iw);
			for(int i = 0; i < nbrImages;  i++){
				bimage[i] = new PImage(iw, img.height, ARGB);
				bimage[i].copy(img, 
						i * iw, 0, iw, img.height,
						0, 0, iw, img.height);
			}
			for(int i = nbrImages; i < 3; i++){
				bimage[i] = bimage[nbrImages - 1];
			}
		}
	}
	
	public void setUseImages(boolean use){
		useImages = use;
	}
	
	/**
	 * Set the color scheme for this button
	 */
	public void setColorScheme(int schemeNo){
		localColor = GCScheme.getColor(winApp, schemeNo);
	}

	/**
	 * @param text the text to set with alignment
	 */
	public void setText(String text) {
		this.text = text;
		winApp.textFont(localFont, localFont.getFont().getSize());
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
		int fs = (int) localFont.getFont().getSize();
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
		switch(status){
		case 0:
			winApp.fill(localColor.btnOff);
			break;
		case 1:
			winApp.fill(localColor.btnOver);
			break;
		case 2:
			winApp.fill(localColor.btnDown);
			break;
		}

		winApp.rect(pos.x,pos.y,width,height);
		// Draw image
		if(bimage[status] != null && useImages){
			winApp.image(bimage[status], pos.x + imgAlignX, pos.y+(height-bimage[status].height)/2);
		}
		// Draw text
		winApp.noStroke();
		winApp.fill(localColor.btnFont);
		winApp.textFont(localFont, localFont.getFont().getSize());
		winApp.text(text, pos.x + alignX, pos.y + (height - localFont.getFont().getSize())/2 - PADV, width, height);
		winApp.popStyle();
	}


	/**
	 * All GUI components are registered for mouseEvents. <br>
	 * When a button is clicked on a GButton it generates 3 events (in this order) 
	 * mouse down, mouse up and mouse pressed. <br>
	 * If you only wish to respond to button click events then you should test the 
	 * event type e.g. <br>
	 * <pre>
	 * 	void handleButtonEvents(GButton button) {
	 *	  if(button == btnName && button.eventType == GButton.CLICKED){
	 *        // code for button click event
	 *    }
	 * </pre> <br>
	 * Where <pre><b>btnName</b></pre> is the GButton identifier (variable name)
	 * 
	 */
	public void mouseEvent(MouseEvent event){
		if(!visible || !enabled) return;

		boolean mouseOver = isOver(winApp.mouseX, winApp.mouseY);
		if(mouseOver) 
			cursorIsOver = this;
		else if(cursorIsOver == this)
			cursorIsOver = null;

		switch(event.getID()){
		case MouseEvent.MOUSE_PRESSED:
			if(focusIsWith != this && mouseOver){
				status = DOWN;
				takeFocus();
				eventType = PRESSED;
				fireEvent();
			}
			break;
		case MouseEvent.MOUSE_CLICKED:
			// No need to test for isOver() since if the component has focus
			// and the mouse has not moved since MOUSE_PRESSED otherwise we 
			// would not get the Java MouseEvent.MOUSE_CLICKED event
			if(focusIsWith == this){
				status = OFF;
				looseFocus(null);
				eventType = CLICKED;
				fireEvent();
			}
			break;
		case MouseEvent.MOUSE_RELEASED:	
			// if the mouse has moved then release focus otherwise
			// MOUSE_CLICKED will handle it
			if(focusIsWith == this){ // && mouseHasMoved(winApp.mouseX, winApp.mouseY)){
				looseFocus(null);
				status = OFF;
				eventType = RELEASED;
				fireEvent();
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
