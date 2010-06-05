/*
  Part of the GUI for Processing library 
  	http://www.lagers.org.uk/g4p/index.html
	http://gui4processing.googlecode.com/svn/trunk/

  Copyright (c) 2010 Peter Lager

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
 * Buttons create from this class use a number of images to represent it's 
 * state. This means that buttons can have an irregular and/or discontinuous
 * shape. <br>
 * 
 * The image button needs up to 3 image files to represent the button states <br>
 * OFF mouse is not over button <br>
 * OVER mouse is over the button <br>
 * DOWN the mouse is over the button and a mouse button is being pressed. <br>
 * 
 * Only the OFF image is absolutely required
 * 
 * The GImageButton has 2 constructors depending on how you create the images. <br>
 * 
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
 * The image file can either be a single image which is used for 
 * all button states, or be a composite of 3 images (tiled horizontally)
 * which are used for the different button states OFF, OVER and DOWN 
 * in which case the image width should be divisible by 3. <br>
 * A number of setImages(...) methods exist to set button state images, these
 * can be used once the button is created.<br>
 * 
 * 
 * @author Peter Lager
 *
 */
public class GImageButton extends GComponent {

	// Button status values
	public static final int OFF		= 0;
	public static final int OVER	= 1;
	public static final int DOWN	= 2;

	protected int status;

	protected PImage[] bimage = new PImage[3];
	protected PImage mask;
	
	protected boolean reportAllButtonEvents = false;
	
	public GImageButton(PApplet theApplet, String maskFile, String imgFile, int nbrImages, int x, int y){
		super(theApplet, x, y);
		setImages(maskFile, imgFile, nbrImages);
		width = bimage[0].width;
		height = bimage[0].height;
		createEventHandler(winApp, "handleButtonEvents", new Class[]{ GButton.class });
		registerAutos_DMPK(true, true, false, false);
	}

	public GImageButton(PApplet theApplet, String maskFile, String imgFiles[], int x, int y){
		super(theApplet, x, y);
		setImages(maskFile, imgFiles);
		width = bimage[0].width;
		height = bimage[0].height;
		createEventHandler(winApp, "handleButtonEvents", new Class[]{ GButton.class });
		registerAutos_DMPK(true, true, false, false);
	}
		
	protected void setImages(String maskFile, String imgFile, int nbrImages){
		nbrImages = PApplet.constrain(nbrImages, 1, 3);
		if(maskFile != null)
			mask = winApp.loadImage(maskFile);
		if(imgFile != null && nbrImages > 0){
			PImage img = winApp.loadImage(imgFile);
			if(img != null)
				bimage = splitImages(img, nbrImages);
			else
				if(G4P.messages) System.out.println("Can't find button image file");
		}
	}
	
	protected void setImages(String maskFile, String[] imgFiles){
		int imgCount;
		if(maskFile != null)
			mask = winApp.loadImage(maskFile);
		if(imgFiles != null){
			for(imgCount = 0; imgCount < imgFiles.length;  imgCount++)
				bimage[imgCount] = winApp.loadImage(imgFiles[imgCount]);
			// Make sure we got an 'over' image if not create one
			if(bimage[0] == null){
				if(G4P.messages)
					System.out.println("Can't find image files for GImageButton");
				bimage[0] = getBlankImage();
				imgCount = 1;
			}	
		}
		else {
			if(G4P.messages) System.out.println("No image file listfor image button!");
			bimage[0] = getBlankImage();
			imgCount = 1;
		}
		//Make sure we have 3 images to work with
		for(int j = imgCount; j < 3; j++)
			bimage[j] = bimage[j - 1];
	}
	
	protected PImage getBlankImage(){
		PImage img = new PImage(21,21, RGB);
		int[] c = new int[] {winApp.color(255,0,0), winApp.color(255)};
		img.loadPixels();
		for(int i = 0; i < img.pixels.length; i++){
			if(i % 2 == 0)
				img.pixels[i] = c[i % 2];
		}
		img.updatePixels();
		return img;
	}
	
	/**
	 * Specify the PImage that contains the image{s} to be used for the button's state. <br>
	 * This image may be a composite of 1 to 3 images tiled horizontally. 
	 * @param img
	 * @param nbrImages in the range 1 - 3
	 */
	protected PImage[] splitImages(PImage img, int nbrImages){
		PImage[] imgs = new PImage[3];
		if(img != null){
			int iw = img.width / nbrImages;
			for(int i = 0; i < nbrImages;  i++){
				imgs[i] = new PImage(iw, img.height, ARGB);
				imgs[i].copy(img, 
						i * iw, 0, iw, img.height,
						0, 0, iw, img.height);
			}
		}
		else {
			if(G4P.messages) System.out.println("No image file listfor image button!");
			imgs[0] = getBlankImage();
			nbrImages = 1;
		}
		//Make sure we have 3 images to work with
		for(int i = nbrImages; i < 3; i++){
			imgs[i] = imgs[nbrImages - 1];
		}
		return imgs;
	}

	/**
	 * Determines whether the position ax, ay is over this component.
	 * If it has a mask 
	 * @param ax mouse x position
	 * @param ay mouse y position
	 * @return true if mouse is over the component else false
	 */
	public boolean isOver(int ax, int ay){
		Point p = new Point(0,0);
		calcAbsPosition(p);
		if(ax >= p.x && ax <= p.x + width && ay >= p.y && ay <= p.y + height){
			int dx, dy, pxl;
			dx = ax - p.x;
			dy = ay - p.y;
			if(mask != null){	// we have a mask file
				pxl = mask.get(dx, dy);
				if(winApp.red(pxl) > 250)
					return true;
				else
					return false;
			}
			else { // no mask use transparency of off image
				pxl = bimage[0].get(dx, dy);
				if(winApp.alpha(pxl) < 5)
					return false;
				else
					return true;
			}
		}
		return false;
	}

	/**
	 * Draw the button
	 */
	public void draw(){
		if(!visible) return;
		
		winApp.pushStyle();
		winApp.imageMode(CORNER);
		Point pos = new Point(0,0);
		calcAbsPosition(pos);
		
		// Draw image
		if(bimage[status] != null){
			winApp.image(bimage[status], pos.x, pos.y);
		}
		winApp.popStyle();
	}

	/**
	 * If the parameter is true all 3 event types are generated, if false
	 * only CLICKED events are generated (default behaviour).
	 * @param all
	 */
	public void fireAllEvents(boolean all){
		reportAllButtonEvents = all;
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
				mdx = winApp.mouseX;
				mdy = winApp.mouseY;
				status = DOWN;
				takeFocus();
				eventType = PRESSED;
				if(reportAllButtonEvents)
					fireEvent();
			}
			break;
		case MouseEvent.MOUSE_CLICKED:
			// No need to test for isOver() since if the component has focus
			// and the mouse has not moved since MOUSE_PRESSED otherwise we 
			// would not get the Java MouseEvent.MOUSE_CLICKED event
			if(focusIsWith == this){
				status = OFF;
				loseFocus(null);
				eventType = CLICKED;
				fireEvent();
			}
			break;
		case MouseEvent.MOUSE_RELEASED:	
			// if the mouse has moved then release focus otherwise
			// MOUSE_CLICKED will handle it
			if(focusIsWith == this && mouseHasMoved(winApp.mouseX, winApp.mouseY)){
				loseFocus(null);
				if(isOver(winApp.mouseX, winApp.mouseY)){
					eventType = CLICKED;
					fireEvent();
				}
				else {
					if(reportAllButtonEvents){
						eventType = RELEASED;
						fireEvent();
					}
				}
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
