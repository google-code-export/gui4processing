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

package guicomponents;

import processing.core.PApplet;
import processing.core.PImage;


public class FTextIconControl extends FTextControl { //implements ITextIcon {

	protected PImage[] bicon = null;
	protected int iconW = 0, iconH = 0;
	protected int iconAlignH = GAlign.RIGHT, iconAlignV = GAlign.MIDDLE;
	protected int siX, siY;

	public FTextIconControl(PApplet theApplet, float p0, float p1, float p2, float p3) {
		super(theApplet, p0, p1, p2, p3);
	}

	/**
	 * Set the text to be displayed.
	 * NEW version for FPanel etc.
	 * @param ptext
	 */
	public void setText(String ntext){
		if(ntext == null || ntext.length() == 0 )
			ptext = " ";
		else 
			ptext = ntext;
		if(iconW == 0)
			stext = new StyledString(ptext, (int) width - TPAD2);
		else
			stext = new StyledString(ptext, (int) width - iconW - TPAD4);
			
		bufferInvalid = true;
	}

	public int getIconAlign(){
		return iconAlignH | iconAlignV;
	}
	
	public void setIcon(String fname, int nbrImages, int align){
		PImage iconImage = winApp.loadImage(fname);
		setIcon(iconImage, nbrImages, align);
	}

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

	public FTextIconControl setIconAlign(int align){
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
		return this;
	}

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
