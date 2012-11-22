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

package g4p_controls;

import g4p_controls.StyledString.TextLayoutHitInfo;
import g4p_controls.StyledString.TextLayoutInfo;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.geom.GeneralPath;
import java.util.LinkedList;

import processing.core.PApplet;

/**
 * 
 * This class is the basis for the GTextField and GTextArea classes.
 * 
 * @author Peter Lager
 *
 */
public abstract class GEditableTextControl extends GAbstractControl {

	protected static float HORZ_SCROLL_RATE = 4f;
	protected static float VERT_SCROLL_RATE = 8;
	
	GTabManager tabManager = null;
	
	/** Text value associated with component */
	protected String text = "";
	// The styled version of text
	protected StyledString stext = null;
	protected StyledString defaultText = null;
	// The width to break a line
	protected int wrapWidth = Integer.MAX_VALUE;

//	protected GAlign textAlignH = GAlign.CENTER | GAlignXXX.MIDDLE;

	protected Font localFont = G4P.globalFont;

	// The typing area
	protected float tx,ty,th,tw;
	// Offset to display area
	protected float ptx, pty;
	// Caret position
	protected float caretX, caretY;
	
	protected boolean keepCursorInView = false;
	
	protected GeneralPath gpTextDisplayArea;
	
	// Used for identifying selection and cursor position
	protected TextLayoutHitInfo startTLHI = new TextLayoutHitInfo();
	protected TextLayoutHitInfo endTLHI = new TextLayoutHitInfo();

	// The scrollbars available
	protected final int scrollbarPolicy;
	protected boolean autoHide = false;
	protected GScrollbar hsb, vsb;

	protected GTimer caretFlasher;
	protected boolean showCaret = false;
	
	// Stuff to manage text selections
	protected int endChar = -1, startChar = -1, pos = endChar, nbr = 0, adjust = 0;
	protected boolean textChanged = false, newline = false, selectionChanged = false;

	public GEditableTextControl(PApplet theApplet, float p0, float p1, float p2, float p3, int scrollbars) {
		super(theApplet, p0, p1, p2, p3);
		scrollbarPolicy = scrollbars;
		autoHide = ((scrollbars & SCROLLBARS_AUTOHIDE) == SCROLLBARS_AUTOHIDE);
		caretFlasher = new GTimer(theApplet, this, "flashCaret", 400);
		caretFlasher.start();
		opaque = true;
		cursorOver = TEXT;
	}
	
	/**
	 * Give up focus but if the text is only made from spaces
	 * then set it to null text.
	 */
	protected void loseFocus(GAbstractControl grabber){
		if(cursorIsOver == this)
			cursorIsOver = null;
		focusIsWith = grabber;
		// If only blank text clear it out allowing default text (if any) to be displayed
		if(stext.length() > 0){
			int tl = stext.getPlainText().trim().length();
			if(tl == 0){
				text = "";
				stext = new StyledString(text);
			}
		}
//		System.out.println("GEditableTextControl losefocus");
//		startTLHI.copyFrom(endTLHI);
		keepCursorInView = true;
		bufferInvalid = true;
	}

	/**
	 * Determines whether this component is to have focus or not
	 * @param focus
	 */
	public void setFocus(boolean focus){
		if(!focus){
			loseFocus(null);
			return;
		}
		// Make sure we have some text
		if(focusIsWith != this){
			dragging = false;
			if(stext == null || stext.length() == 0)
				stext = new StyledString(" ");
			text = stext.getPlainText();
			LinkedList<TextLayoutInfo> lines = stext.getLines(buffer.g2);
			startTLHI = new TextLayoutHitInfo(lines.getFirst(), null);
			startTLHI.thi = startTLHI.tli.layout.getNextLeftHit(1);

			endTLHI = new TextLayoutHitInfo(lines.getLast(), null);
			int lastChar = endTLHI.tli.layout.getCharacterCount();
			endTLHI.thi = startTLHI.tli.layout.getNextRightHit(lastChar-1);

			calculateCaretPos(endTLHI);
			bufferInvalid = true;
		}
		keepCursorInView = true;
		takeFocus();

	}
	
	/**
	 * Set the default text for this control. If provided this text will be 
	 * displayed in italic whenever it is empty.
	 * @param dtext
	 */
	public void setDefaultText(String dtext){
		if(dtext == null || dtext.length() == 0)
			defaultText = null;
		else {
			defaultText = new StyledString(dtext);
			defaultText.addAttribute(G4P.POSTURE, G4P.POSTURE_OBLIQUE);
		}
		bufferInvalid = true;
	}
	
	/**
	 * Get the default text for this control
	 * @return
	 */
	public String getDefaultText(){
		return defaultText.getPlainText();
	}
	
	/**
	 * Get the text in the control
	 * @return
	 */
	public String getText(){
		return stext.getPlainText();
	}
	
	/**
	 * Get the text that has been selected (highlighted) by the user. <br>
	 * @return
	 */
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
		String s = stext.getPlainText().substring(ss, ee);
		return s;
	}
	
	/**
	 * If some text has been selected then set the style. If there is no selection then 
	 * the text is unchanged.
	 * 
	 * 
	 * @param style
	 */
	public void setSelectedTextStyle(TextAttribute style, Object value){
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
	
	public void clearStyle(){
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
		stext.clearAttributes(ss, ee);
		
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
	/**
	 * Set the font for this control.
	 * @param font
	 * @return
	 */
	public GEditableTextControl setFont(Font font) {
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

	/**
	 * Used internally to set the scrollbar values as the text changes.
	 * 
	 * @param sx
	 * @param sy
	 */
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
	
	/**
	 * Sets the local colour scheme for this control
	 */
	public void setLocalColorScheme(int cs){
		super.setLocalColorScheme(cs);
		if(hsb != null)
			hsb.setLocalColorScheme(localColorScheme);
		if(vsb != null)
			vsb.setLocalColorScheme(localColorScheme);
	}

	/**
	 * Find out if some text is selected (highlighted)
	 * @return
	 */
	public boolean hasSelection(){
		return (startTLHI.tli != null && endTLHI.tli != null && startTLHI.compareTo(endTLHI) != 0);	
	}

	/**
	 * Calculate the caret (text insertion point)
	 * 
	 * @param tlhi
	 */
	protected void calculateCaretPos(TextLayoutHitInfo tlhi){
		float temp[] = tlhi.tli.layout.getCaretInfo(tlhi.thi);
		caretX = temp[0];		
		caretY = tlhi.tli.yPosInPara;
	}

	public void keyEvent(KeyEvent e) {
		if(!visible  || !enabled || !available) return;
		if(focusIsWith == this && endTLHI != null){
			char keyChar = e.getKeyChar();
			int keyCode = e.getKeyCode();
			int keyID = e.getID();
			
			textChanged = false;
			newline = false;
			keepCursorInView = true;
			
			boolean shiftDown = ((e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) == KeyEvent.SHIFT_DOWN_MASK);
			boolean ctrlDown = ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK);

			int startPos = pos, startNbr = nbr;
			
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
			if(startPos >= 0){
				if(startPos != pos || startNbr != nbr)
					fireEvent(this, GEvent.SELECTION_CHANGED);
			}
			
			if(keyID == KeyEvent.KEY_PRESSED) {
				keyPressedProcess(keyCode, keyChar, shiftDown, ctrlDown);
				setScrollbarValues(ptx, pty);
			}
			else if(keyID == KeyEvent.KEY_TYPED && e.getKeyChar() != KeyEvent.CHAR_UNDEFINED && !ctrlDown){
				keyTypedProcess(keyCode, keyChar, shiftDown, ctrlDown);
				setScrollbarValues(ptx, pty);
			}
			if(textChanged){
				changeText();
				fireEvent(this, GEvent.CHANGED);
			}
		}
	}
	
	// Enable polymorphism. 
	protected void keyPressedProcess(int keyCode, char keyChar, boolean shiftDown, boolean ctrlDown) {
	}

	protected void keyTypedProcess(int keyCode, char keyChar, boolean shiftDown, boolean ctrlDown){
		int ascii = (int)keyChar;

		if(ascii >= 32 && ascii < 127){
			if(hasSelection())
				stext.deleteCharacters(pos, nbr);
			stext.insertCharacters(pos, "" + keyChar);
			adjust = 1; textChanged = true;
		}
		else if(keyChar == KeyEvent.VK_BACK_SPACE){
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
		}
		else if(keyChar == KeyEvent.VK_ENTER && stext.getWrapWidth() != Integer.MAX_VALUE) {
			if(stext.insertEOL(pos)){
				adjust = 1; textChanged = true;
				newline = true;
			}
		}
		else if(keyChar == KeyEvent.VK_TAB){
			// If possible move to next text control
			if(tabManager != null){
				boolean result = (shiftDown) ? tabManager.prevControl(this) : tabManager.nextControl(this);
				if(result){
					startTLHI.copyFrom(endTLHI);
					return;
				}
			}
		}
		// If we have emptied the text then recreate a one character string (space)
		if(stext.length() == 0){
			stext.insertCharacters(0, " ");
			adjust++; textChanged = true;
		}
	}
	
	
	// Only executed if text has changed
	protected void changeText(){
		TextLayoutInfo tli;
		TextHitInfo thi = null, thiRight = null;
		
		pos += adjust;
		// Force layouts to be updated
		stext.getLines(buffer.g2);

		// Try to get text layout info for the current position
		tli = stext.getTLIforCharNo(pos);
		if(tli == null){
			// If unable to get a layout for pos then reset everything
			System.out.println("FeditableTextControl - changeText");
			endTLHI = null;
			startTLHI = null;
			ptx = pty = 0;
			caretX = caretY = 0;
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

			// Is do we have to move cursor to start of next line
			if(newline) {    ///stext.getWrapWidth() != Integer.MAX_VALUE && caretX > stext.getWrapWidth()){
				if(pos >= stext.length()){
					stext.insertCharacters(pos, " ");
					stext.getLines(buffer.g2);
				}
				moveCaretRight(endTLHI);
				calculateCaretPos(endTLHI);
			}
			// Finish off by ensuring no selection, invalidate buffer etc.
			startTLHI.copyFrom(endTLHI);
		}
		bufferInvalid = true;
	}
	
	/**
	 * Do not call this directly. A timer calls this method as and when required.
	 */
	public void flashCaret(GTimer timer){
		showCaret = !showCaret;
	}
	
	/**
	 * Do not call this method directly, G4P uses it to handle input from
	 * the horizontal scrollbar.
	 */
	public void hsbEventHandler(GScrollbar scrollbar, GEvent event){
		keepCursorInView = false;
		ptx = hsb.getValue() * (stext.getMaxLineLength() + 4);
		bufferInvalid = true;
	}

	/**
	 * Do not call this method directly, G4P uses it to handle input from
	 * the vertical scrollbar.
	 */
	public void vsbEventHandler(GScrollbar scrollbar, GEvent event){
		keepCursorInView = false;
		pty = vsb.getValue() * (stext.getTextAreaHeight() + 1.5f * stext.getMaxLineHeight());
		bufferInvalid = true;
	}

	/**
	 * Permanently dispose of this control.
	 */
	public void markForDisposal(){
		if(tabManager != null)
			tabManager.removeControl(this);
		super.markForDisposal();
	}
}
