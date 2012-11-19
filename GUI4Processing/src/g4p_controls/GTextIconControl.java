/*
  Part of the GUI for Processing library 
  	http://www.lagers.org.uk/g4p/index.html
	http://gui4processing.googlecode.com/svn/trunk/

  Copyright (c) 2008-12 Peter Lager

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

package g4p_controls;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * Base class for controls with text and/or icon.<br>
 * 
 * This class forms the basis for any control that has text and/or an icon. <br>
 * Use the setIcon, setIconAlign, setText and setTextAlign to control 
 * horizontal and vertical alignment of the icon and text withing the control face.
 * 
 * @author Peter Lager
 *
 */
public abstract class GTextIconControl extends GTextControl {

	protected PImage[] bicon = null;
	protected int iconW = 0, iconH = 0;
	protected int iconAlignH = GAlign.RIGHT, iconAlignV = GAlign.MIDDLE;
	protected int siX, siY;


	public GTextIconControl(PApplet theApplet, float p0, float p1, float p2, float p3) {
		super(theApplet, p0, p1, p2, p3);
	}

	/**
	 * Set the text to be displayed.
	 * NEW version for FPanel etc.
	 * @param ptext
	 */
	public void setText(String text){
		if(text == null || text.length() == 0 )
			text = " ";
		if(iconW == 0)
			stext = new StyledString(text, (int) width - TPAD2);
		else
			stext = new StyledString(text, (int) width - iconW - TPAD4);
			
		bufferInvalid = true;
	}

	/**
	 * Get the icon alignment.
	 * 
	 * @return
	 */
	public int getIconAlign(){
		return iconAlignH | iconAlignV;
	}
	
	/**
	 * Set the icon image and alignment. <br>
	 * 
	 * @param fname the filename of the icon
	 * @param nbrImages number of tiled images in the icon
	 * @param align horz and vertical alignment (see @see GAlign)
	 */
	public void setIcon(String fname, int nbrImages, int align){
		PImage iconImage = winApp.loadImage(fname);
		setIcon(iconImage, nbrImages, align);
	}

	/**
	 * Set the icon image and alignment. <br>
	 * 
	 * @param icon the icon
	 * @param nbrImages number of tiled images in the icon
	 * @param align horz and vertical alignment (see @see GAlign)
	 */
	public void setIcon(PImage icon, int nbrImages, int align){
		bicon = loadImages(icon, nbrImages);
		if(bicon == null)
			return;
		// We have loaded the image so validate alignment
		if((align & GAlign.HA_VALID) != 0){
			iconAlignH = align & GAlign.HA_VALID;
			if(iconAlignH != GAlign.LEFT && iconAlignH != GAlign.RIGHT)
				iconAlignH = GAlign.RIGHT;
		}
		if((align & GAlign.VA_VALID) != 0){
			iconAlignV = align & GAlign.VA_VALID;
			if(iconAlignV != GAlign.TOP && iconAlignV != GAlign.MIDDLE && iconAlignV != GAlign.BOTTOM)
				iconAlignV = GAlign.MIDDLE;
		}
		iconW = bicon[0].width;
		iconH = bicon[0].height;
		stext.setWrapWidth((int) width - iconW - TPAD4);
		bufferInvalid = true;
	}

	/**
	 * Change the alignment of an existing icn.
	 * @param align horz and vertical alignment (see @see GAlign)
	 */
	public void setIconAlign(int align){
		if(iconW != 0){
			if((align & GAlign.HA_VALID) != 0){
				iconAlignH = align & GAlign.HA_VALID;
				if(iconAlignH != GAlign.LEFT && iconAlignH != GAlign.RIGHT)
					iconAlignH = GAlign.RIGHT;
			}
			if((align & GAlign.VA_VALID) != 0){
				iconAlignV = align & GAlign.VA_VALID;
				if(iconAlignV != GAlign.TOP && iconAlignV != GAlign.MIDDLE && iconAlignV != GAlign.BOTTOM)
					iconAlignV = GAlign.MIDDLE;
			}
			bufferInvalid = true;
		}
	}

	/**
	 * Calculate various values based on alignment of text and icon
	 */
	protected void calcAlignment(){
		super.calcAlignment();	// calculate the text alignment
		if(iconW != 0){
			switch(iconAlignH){
			case GAlign.LEFT:
				siX = TPAD;
				if(textAlignH != GAlign.RIGHT)
					stX += (iconW + TPAD2); // Image on left so adjust text start x position
				break;
			case GAlign.RIGHT:
			default:
				siX = (int)width - iconW - TPAD2;
				if(textAlignH == GAlign.RIGHT)
					stX -= (iconW + TPAD2);
				break;
			}
			switch(iconAlignV){
			case GAlign.TOP:
				siY = TPAD;
				break;
			case GAlign.BOTTOM:
				siY =(int) height - iconH - TPAD2;
				break;
			case GAlign.MIDDLE:
			default:
				siY = (int)(height - iconH)/2;
			}
		}
	}
	
	public String toString(){
		return tag;
	}
}
