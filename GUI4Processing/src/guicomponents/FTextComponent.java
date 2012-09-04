package guicomponents;

import java.awt.Font;
import java.awt.font.TextHitInfo;

import guicomponents.StyledString.TextLayoutHitInfo;
import processing.core.PApplet;

public class FTextComponent extends GComponent {

	
	// The typing area
	protected float tx,ty,th,tw;
	// Offset to display area
	protected float ptx, pty;
	// Caret position
	float caretX, caretY;

	protected TextLayoutHitInfo startTLHI = null, endTLHI = null;

	// Set to true when mouse is dragging : set false on button released
	protected boolean dragging = false;

	// The scrollbar policy
	protected final int sbPolicy;
	protected boolean autoHide = true;
	protected FScrollbar hsb, vsb;

	protected GTimer caretFlasher;
	protected boolean showCaret = false;


	public FTextComponent(PApplet theApplet, float p0, float p1, float p2, float p3, int scrollbars) {
		super(theApplet, p0, p1, p2, p3);
		sbPolicy = scrollbars;
		autoHide = ((scrollbars & SCROLLBARS_AUTOHIDE) == SCROLLBARS_AUTOHIDE);
		caretFlasher = new GTimer(theApplet, this, "flashCaret", 400);
		caretFlasher.start();
		opaque = true;
	}


	
	void setScrollbarValues(){
		float sTextHeight;
		if(vsb != null){
			sTextHeight = stext.getTextAreaHeight();
			ptx = pty = 0;
			if(sTextHeight < th)
				vsb.setValue(0.0f, 1.0f);
			else 
				vsb.setValue(0, th/sTextHeight);
		}
		// If needed update the horizontal scrollbar
		if(hsb != null){
			if(stext.getMaxLineLength() < tw)
				hsb.setValue(0,1);
			else
				hsb.setValue(0, tw/stext.getMaxLineLength());
		}
	}
	
	/**
	 * Move caret to home position
	 * @return true if caret moved else false
	 */
	protected boolean moveCaretStartOfLine(TextLayoutHitInfo currPos){
		if(currPos.thi.getCharIndex() == 0)
			return false; // already at start of line
		currPos.thi = currPos.tli.layout.getNextLeftHit(1);
		return true;
	}

	protected boolean moveCaretEndOfLine(TextLayoutHitInfo currPos){
		if(currPos.thi.getCharIndex() == currPos.tli.nbrChars - 1)
			return false; // already at end of line
		currPos.thi = currPos.tli.layout.getNextRightHit(currPos.tli.nbrChars - 1);
		return true;
	}

	/**
	 * Move caret left by one character.
	 * @return true if caret was moved else false
	 */
	protected boolean moveCaretLeft(TextLayoutHitInfo currPos){
		TextHitInfo nthi = currPos.tli.layout.getNextLeftHit(currPos.thi);
		if(nthi == null){ 
			return false;
		}
		else {
			// Move the caret to the left of current position
			currPos.thi = nthi;
			bufferInvalid = true;			
		}
		return true;
	}

	/**
	 * Move caret left by one character.
	 * @return true if caret was moved else false
	 */
	protected boolean moveCaretRight(TextLayoutHitInfo currPos){
		TextHitInfo nthi = currPos.tli.layout.getNextRightHit(currPos.thi);
		if(nthi == null){ 
			return false;
		}
		else {
			currPos.thi = nthi;
			bufferInvalid = true;			
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

	public void setFont(Font font){
		if(font != null && font != fLocalFont){
			fLocalFont = font;
			buffer.g2.setFont(fLocalFont);
		}
	}

	public boolean hasSelection(){
		return (startTLHI != null && endTLHI != null && startTLHI.compareTo(endTLHI) != 0);	
	}

	public void flashCaret(){
		showCaret = !showCaret;
		if(focusIsWith == this && endTLHI != null){
			bufferInvalid = true;
		}
	}
	

	public void hsbEventHandler(FScrollbar scrollbar){
		ptx = hsb.getValue() * (stext.getMaxLineLength() + 4);
		bufferInvalid = true;
	}

	public void vsbEventHandler(FScrollbar scrollbar){
		pty = vsb.getValue() * (stext.getTextAreaHeight() + 1.5f * stext.getMaxLineHeight());
		bufferInvalid = true;
	}

}
