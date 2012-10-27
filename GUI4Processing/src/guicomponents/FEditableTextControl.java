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
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.geom.GeneralPath;

import guicomponents.StyledString.TextLayoutHitInfo;
import guicomponents.StyledString.TextLayoutInfo;
import processing.core.PApplet;

class FEditableTextControl extends FAbstractControl {

	protected static float HORZ_SCROLL_RATE = 2.5f;
	protected static float VERT_SCROLL_RATE = 3.5f;
	
	/** Text value associated with component */
	protected String text = "";
	// The styled version of text
	protected StyledString stext = null;
	protected StyledString defaultText = null;
	
	protected int textAlign = GAlign.CENTER | GAlign.MIDDLE;

	protected Font localFont = F4P.globalFont;

	// The typing area
	protected float tx,ty,th,tw;
	// Offset to display area
	protected float ptx, pty;
	// Caret position
	protected float caretX, caretY;
	
	protected boolean keepCursorInView = true;
	
	protected GeneralPath gpTextDisplayArea;
	
	protected TextLayoutHitInfo startTLHI = null, endTLHI = null;

	// The scrollbar policy
	protected final int sbPolicy;
	protected boolean autoHide = true;
	protected FScrollbar hsb, vsb;

	protected GTimer caretFlasher;
	protected boolean showCaret = false;
	// Stuff to manage text selections
	protected int endChar, startChar, pos = endChar, nbr = 0, adjust = 0;
	protected boolean textChanged = false;

	public FEditableTextControl(PApplet theApplet, float p0, float p1, float p2, float p3, int scrollbars) {
		super(theApplet, p0, p1, p2, p3);
		sbPolicy = scrollbars;
		autoHide = ((scrollbars & SCROLLBARS_AUTOHIDE) == SCROLLBARS_AUTOHIDE);
		caretFlasher = new GTimer(theApplet, this, "flashCaret", 400);
		caretFlasher.start();
		opaque = true;
	}
	
	public void setDefaultText(String dtext){
		if(dtext == null || dtext.length() == 0)
			defaultText = null;
		else {
			defaultText = new StyledString(dtext);
			defaultText.addAttribute(F4P.POSTURE, F4P.POSTURE_OBLIQUE);
		}
		bufferInvalid = true;
	}
	
	public String getDefaultText(){
		return defaultText.getPlainText();
	}
	
	/**
	 * If some text has been selected then set the style. If there is no selection then 
	 * the text is unchanged.
	 * 
	 * 
	 * @param style
	 */
	public void setStyle(TextAttribute style, Object value){
		if(!hasSelection())
			return;
		TextLayoutHitInfo startSelTLHI;
		TextLayoutHitInfo endSelTLHI;
		if(endTLHI.compareTo(startTLHI) == -1){
			startSelTLHI = endTLHI;
			endSelTLHI = startTLHI;
		}
		else {
			startSelTLHI = startTLHI;
			endSelTLHI = endTLHI;
		}
		int ss = startSelTLHI.tli.startCharIndex + startSelTLHI.thi.getInsertionIndex();
		int ee = endSelTLHI.tli.startCharIndex + endSelTLHI.thi.getInsertionIndex();
		stext.addAttribute(style, value, ss, ee);
		
		// We have modified the text style so the end of the selection may have
		// moved, so it needs to be recalculated. The start will be unaffected.
		stext.getLines(buffer.g2);
		endSelTLHI.tli = stext.getTLIforCharNo(ee);
		int cn = ee - endSelTLHI.tli.startCharIndex;
		if(cn == 0) // start of line
			endSelTLHI.thi = endSelTLHI.tli.layout.getNextLeftHit(1);
		else 
			endSelTLHI.thi = endSelTLHI.tli.layout.getNextRightHit(cn-1);

		bufferInvalid = true;
	}

	public String getSelectedText(){
		if(!hasSelection())
			return "";
		TextLayoutHitInfo startSelTLHI;
		TextLayoutHitInfo endSelTLHI;
		if(endTLHI.compareTo(startTLHI) == -1){
			startSelTLHI = endTLHI;
			endSelTLHI = startTLHI;
		}
		else {
			startSelTLHI = startTLHI;
			endSelTLHI = endTLHI;
		}
		int ss = startSelTLHI.tli.startCharIndex + startSelTLHI.thi.getInsertionIndex();
		int ee = endSelTLHI.tli.startCharIndex + endSelTLHI.thi.getInsertionIndex();
		String s = text.substring(ss, ee);
		return s;
	}
	
	public FEditableTextControl setFont(Font font) {
		if(font != null && font != localFont && buffer != null){
			localFont = font;
			buffer.g2.setFont(localFont);
			stext.getLines(buffer.g2);
			ptx = pty = 0;
			setScrollbarValues(ptx, pty);
			bufferInvalid = true;
		}
		return this;
	}

	void setScrollbarValues(float sx, float sy){
		if(vsb != null){
			float sTextHeight = stext.getTextAreaHeight();
			if(sTextHeight < th)
				vsb.setValue(0.0f, 1.0f);
			else 
				vsb.setValue(sy/sTextHeight, th/sTextHeight);
		}
		// If needed update the horizontal scrollbar
		if(hsb != null){
			float sTextWidth = stext.getMaxLineLength();
			if(stext.getMaxLineLength() < tw)
				hsb.setValue(0,1);
			else
				hsb.setValue(sx/sTextWidth, tw/sTextWidth);
		}
	}
	
	/**
	 * Move caret to home position
	 * @param currPos the current position of the caret
	 * @return true if caret moved else false
	 */
	protected boolean moveCaretStartOfLine(TextLayoutHitInfo currPos){
		if(currPos.thi.getCharIndex() == 0)
			return false; // already at start of line
		currPos.thi = currPos.tli.layout.getNextLeftHit(1);
		return true;
	}

	/**
	 * Move caret to the end of the line that has the current caret position
	 * @param currPos the current position of the caret
	 * @return true if caret moved else false
	 */
	protected boolean moveCaretEndOfLine(TextLayoutHitInfo currPos){
		if(currPos.thi.getCharIndex() == currPos.tli.nbrChars - 1)
			return false; // already at end of line
		currPos.thi = currPos.tli.layout.getNextRightHit(currPos.tli.nbrChars - 1);
		return true;
	}

	/**
	 * Move caret left by one character.
	 * @param currPos the current position of the caret
	 * @return true if caret moved else false
	 */
	protected boolean moveCaretLeft(TextLayoutHitInfo currPos){
		TextHitInfo nthi = currPos.tli.layout.getNextLeftHit(currPos.thi);
		if(nthi == null){ 
			return false;
		}
		else {
			// Move the caret to the left of current position
			currPos.thi = nthi;
		}
		return true;
	}

	/**
	 * Move caret right by one character.
	 * @param currPos the current position of the caret
	 * @return true if caret moved else false
	 */
	protected boolean moveCaretRight(TextLayoutHitInfo currPos){
		TextHitInfo nthi = currPos.tli.layout.getNextRightHit(currPos.thi);
		if(nthi == null){ 
			return false;
		}
		else {
			currPos.thi = nthi;
		}
		return true;
	}
	
	public void setJustify(boolean justify){
		stext.setJustify(justify);
		bufferInvalid = true;
	}
	
	public void setLocalColorScheme(int cs){
		super.setLocalColorScheme(cs);
		if(hsb != null)
			hsb.setLocalColorScheme(localColorScheme);
		if(vsb != null)
			vsb.setLocalColorScheme(localColorScheme);
	}

	public boolean hasSelection(){
		return (startTLHI != null && endTLHI != null && startTLHI.compareTo(endTLHI) != 0);	
	}

	protected void calculateCaretPos(TextLayoutHitInfo tlhi){
		float temp[] = tlhi.tli.layout.getCaretInfo(tlhi.thi);
		caretX = temp[0];		
		caretY = tlhi.tli.yPosInPara;
	}

	public void keyEvent(KeyEvent e) {
		if(!visible  || !enabled || !available) return;
		if(focusIsWith == this && endTLHI != null){
			textChanged = false;
			keepCursorInView = true;
			boolean shiftDown = ((e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) == KeyEvent.SHIFT_DOWN_MASK);
			boolean ctrlDown = ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK);

			// Get selection details
			endChar = endTLHI.tli.startCharIndex + endTLHI.thi.getInsertionIndex();
			startChar = (startTLHI != null) ? startTLHI.tli.startCharIndex + startTLHI.thi.getInsertionIndex() : endChar;
			pos = endChar;
			nbr = 0;
			adjust = 0;
			if(endChar != startChar){ // Have we some text selected?
				if(startChar < endChar){ // Forward selection
					pos = startChar; nbr = endChar - pos;
				}
				else if(startChar > endChar){ // Backward selection
					pos = endChar;	nbr = startChar - pos;
				}
			}

			if(e.getID() == KeyEvent.KEY_PRESSED) {
				processKeyPressed(e, shiftDown, ctrlDown);
				setScrollbarValues(ptx, pty);
			}
			else if(e.getID() == KeyEvent.KEY_TYPED && e.getKeyChar() != KeyEvent.CHAR_UNDEFINED){
				processKeyTyped(e, shiftDown, ctrlDown);
				setScrollbarValues(ptx, pty);
			}
			if(textChanged)
				changeText();
		}
	}
	
	// Enable polymorphism. 
	protected boolean processKeyPressed(KeyEvent e, boolean shiftDown,
			boolean ctrlDown) {
		return false;		
	}

	protected void processKeyTyped(KeyEvent e, boolean shiftDown, boolean ctrlDown){
		char keyChar = e.getKeyChar();		
		int ascii = (int)keyChar;

		if(keyChar == KeyEvent.VK_BACK_SPACE){
			if(hasSelection()){
				stext.deleteCharacters(pos, nbr);
				adjust = 0; textChanged = true;				
			}
			else if(stext.deleteCharacters(pos - 1, 1)){
				adjust = -1; textChanged = true;
			}
		}
		else if(keyChar == KeyEvent.VK_DELETE){
			if(hasSelection()){
				stext.deleteCharacters(pos, nbr);
				adjust = 0; textChanged = true;				
			}
			else if(stext.deleteCharacters(pos, 1)){
				adjust = 0; textChanged = true;
			}
			else {
				System.out.println("Failed to delete " + stext.length());
			}
		}
		
		// Now we have got rid of any selection be can process other keys
		if(ascii >= 32 && ascii < 127){
			if(hasSelection())
				stext.deleteCharacters(pos, nbr);
			stext.insertCharacters(pos, "" + keyChar);
			adjust = 1; textChanged = true;
		}
	}
	
	
	// What to do if the text has changed
	protected void changeText(){
		pos += adjust;
		stext.getLines(buffer.g2);

		TextLayoutInfo tli;
		TextHitInfo thi = null, thiRight;

		tli = stext.getTLIforCharNo(pos);
		if(tli == null){
			endTLHI = null;
			startTLHI = null;
		}
		else {
			int posInLine = pos - tli.startCharIndex;

			// Get some hit info so we can see what is happening
			try{
				thiRight = tli.layout.getNextRightHit(posInLine);
			}
			catch(Exception excp){
				thiRight = null;
			}

			if(posInLine <= 0){					// At start of line
				thi = tli.layout.getNextLeftHit(thiRight);				
			}
			else if(posInLine >= tli.nbrChars){	// End of line
				thi = tli.layout.getNextRightHit(tli.nbrChars - 1);					
			}
			else {								// Character in line;
				thi = tli.layout.getNextLeftHit(thiRight);	
			}

			endTLHI.setInfo(tli, thi);
			// Cursor at end of paragraph graphic
			calculateCaretPos(endTLHI);

			// Finish off by ensuring no selection, invalidate buffer etc.
			startTLHI.copyFrom(endTLHI);
		}
		bufferInvalid = true;
	}
	
	public void flashCaret(){
		showCaret = !showCaret;
	}
	
	public void hsbEventHandler(FScrollbar scrollbar){
		keepCursorInView = false;
		ptx = hsb.getValue() * (stext.getMaxLineLength() + 4);
		bufferInvalid = true;
	}

	public void vsbEventHandler(FScrollbar scrollbar){
		keepCursorInView = false;
		pty = vsb.getValue() * (stext.getTextAreaHeight() + 1.5f * stext.getMaxLineHeight());
		bufferInvalid = true;
	}

}
