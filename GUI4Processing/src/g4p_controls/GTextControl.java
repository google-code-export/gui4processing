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

import java.awt.Font;
import java.awt.font.TextAttribute;

import processing.core.PApplet;


/**
 * 
 * Is the basis for all classes that have some simple non-editable text element to them. <br>
 * 
 * @author Peter Lager
 * 
 */
public abstract class GTextControl extends GAbstractControl {

	protected static final int TPAD = 2;
	protected static final int TPAD2 = TPAD * 2;
	protected static final int TPAD4 = TPAD * 4;
	
	/** The styled version of text */
	public StyledString stext = null;
	
	protected GAlign textAlignH = GAlign.CENTER, textAlignV =  GAlign.MIDDLE;
	
	protected float stX, stY;

	protected Font localFont = G4P.globalFont;
	
	public GTextControl(PApplet theApplet, float p0, float p1, float p2, float p3) {
		super(theApplet, p0, p1, p2, p3);
	}

	/**
	 * Set the horizontal and/or vertical text alignment. Use the constants in GAlign 
	 * e.g. <pre>GAlign.LEFT</pre> <br>
	 * 
	 * If you want to set just one of these then pass null in the other 
	 * 
	 * @param horz LEFT, CENTER, RIGHT or JUSTIFY
	 * @param vert TOP, MIDDLE, BOTTOM
	 */
	public void setTextAlign(GAlign horz, GAlign vert){
		if(horz != null && horz.isHorzAlign()){
			textAlignH = horz;
			stext.setJustify(textAlignH == GAlign.JUSTIFY);
		}
		if(vert != null && vert.isVertAlign()){
			textAlignV = vert;
		}
		bufferInvalid = true;
	}

	/**
	 * Set the text to be displayed.
	 * NEW version for FPanel etc.
	 * @param ptext
	 */
	public void setText(String text){
		if(text == null || text.length() == 0 )
			text = " ";
		stext = new StyledString(text, (int)width - TPAD2);
		bufferInvalid = true;
	}
	
	/**
	 * Allows the user to provide their own styled text for this component
	 * @param stext
	 */
	public void setStyledText(StyledString stext){
		if(stext != null) {
			this.stext = stext;
			this.stext.setWrapWidth((int)width - TPAD2);
			bufferInvalid = true;
		}
	}
	
	/**
	 * Clear <b>all</b> applied styles from the whole text.
	 */
	public void setTextPlain(){
		stext.clearAllAttributes();
		bufferInvalid = true;
	}
	
	/**
	 * Make the selected characters bold. <br>
	 * Characters affected are >= start and < end
	 * 
	 * @param s the first character to style
	 * @param e the first character not to style
	 */
	public void setTextBold(int start, int end){
		addAttribute(G4P.WEIGHT, G4P.WEIGHT_BOLD, start, end);
	}

	/**
	 * Make all the characters bold. <br>
	 */
	public void setTextBold(){
		stext.addAttribute(G4P.WEIGHT, G4P.WEIGHT_BOLD);
		bufferInvalid = true;
	}

	/**
	 * Make the selected characters italic. <br>
	 * Characters affected are >= start and < end
	 * 
	 * @param s the first character to style
	 * @param e the first character not to style
	 */
	public void setTextItalic(int start, int end){
		addAttribute(G4P.POSTURE, G4P.POSTURE_OBLIQUE, start, end);
	}

	/**
	 * Make all the characters italic. <br>
	 */
	public void setTextItalic(){
		stext.addAttribute(G4P.POSTURE, G4P.POSTURE_OBLIQUE);
		bufferInvalid = true;
	}

	protected void addAttribute(TextAttribute style, Object value, int s, int e){
		if(s >= e) return;
		if(s < 0) s = 0;
		if(e > stext.length()) e = stext.length();
		stext.addAttribute(style, value, s, e);
		bufferInvalid = true;
	}
	
	/** 
	 * Set the font to be used in this control
	 * 
	 * @param font
	 */
	public void setFont(Font font) {
		if(font != null && font != localFont && buffer != null){
			localFont = font;
			buffer.g2.setFont(localFont);
			bufferInvalid = true;
		}
	}

	protected void calcAlignment(){
		switch(textAlignH){
		case RIGHT:
			stX = width - stext.getWrapWidth() - TPAD;
			break;
		case LEFT:
		case CENTER:
		case JUSTIFY:
		default:
			stX = TPAD;	
		}
		switch(textAlignV){
		case TOP:
			stY = TPAD;
			break;
		case BOTTOM:
			stY = height - stext.getTextAreaHeight() - TPAD;
			break;
		case MIDDLE:
		default:
			stY = (height - stext.getTextAreaHeight()) / 2;
		}
	}


}
