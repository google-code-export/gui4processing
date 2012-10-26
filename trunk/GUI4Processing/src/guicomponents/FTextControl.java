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
import java.awt.Font;

import processing.core.PApplet;


public class FTextControl extends FAbstractControl implements IText, GAlign {

	protected static final int TPAD = 2;
	protected static final int TPAD2 = TPAD * 2;
	protected static final int TPAD4 = TPAD * 4;
	
	/** Text value associated with component */
	protected String ptext = "";
	/** The styled version of text */
	public StyledString stext = null;
	
	protected int textAlignH = GAlign.CENTER, textAlignV =  GAlign.MIDDLE;
	protected float stX, stY;

	protected Font localFont = F4P.globalFont;
	
	public FTextControl(PApplet theApplet, float p0, float p1, float p2, float p3) {
		super(theApplet, p0, p1, p2, p3);
	}

	public void setTextAlign(int align){
		if((align & GAlign.HA_VALID) != 0){
			textAlignH = align & GAlign.HA_VALID;
			switch(textAlignH){
			case GAlign.JUSTIFY:
				stext.setJustify(true);
				break;
			case GAlign.LEFT:
			case GAlign.CENTER:
			case GAlign.RIGHT:
				stext.setJustify(false);
				break;
			default:
				textAlignH = GAlign.CENTER;
				stext.setJustify(false);
			}
		}
		if((align & GAlign.VA_VALID) != 0){
			textAlignV = align & GAlign.VA_VALID;
			if(textAlignV != GAlign.TOP && textAlignV != GAlign.MIDDLE && textAlignV != GAlign.BOTTOM)
				textAlignV = GAlign.MIDDLE;
		}
		bufferInvalid = true;
	}

	public int getTextAlign(){
		return textAlignH | textAlignV;
	}

	/**
	 * Set the text to be displayed.
	 * NEW version for FPanel etc.
	 * @param ptext
	 */
	public void setText(String text){
		if(text == null || text.length() == 0 )
			ptext = " ";
		else 
			ptext = text;
		stext = new StyledString(ptext, (int)width - TPAD2);
		bufferInvalid = true;
	}
	
	public FTextControl setStyledText(StyledString stext){
		if(stext != null) {
			ptext = stext.getPlainText();
			this.stext = stext;
			this.stext.setWrapWidth((int)width - TPAD2);
			bufferInvalid = true;
		}
		return this;
	}
	
//	public FTextControl setFontNew(Font font) {
//		if(font != null)
//			localFont = font;
//		if(buffer != null){
//			buffer.g2.setFont(localFont);
//			bufferInvalid = true;
//		}
//		return this;
//	}

	public void setFont(Font font) {
		if(font != null && font != localFont && buffer != null){
			localFont = font;
			buffer.g2.setFont(localFont);
			bufferInvalid = true;
		}
	}

	protected void calcAlignment(){
		switch(textAlignH){
		case GAlign.RIGHT:
			stX = width - stext.getWrapWidth() - TPAD;
			break;
		case GAlign.LEFT:
		case GAlign.CENTER:
		case GAlign.JUSTIFY:
		default:
			stX = TPAD;	
		}
		switch(textAlignV){
		case GAlign.TOP:
			stY = TPAD;
			break;
		case GAlign.BOTTOM:
			stY = height - stext.getTextAreaHeight() - TPAD;
			break;
		case GAlign.MIDDLE:
		default:
			stY = (height - stext.getTextAreaHeight()) / 2;
		}
	}


}
