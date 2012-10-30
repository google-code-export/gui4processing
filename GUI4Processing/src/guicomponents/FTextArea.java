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

import guicomponents.HotSpot.HSrect;
import guicomponents.StyledString.TextLayoutHitInfo;
import guicomponents.StyledString.TextLayoutInfo;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.geom.GeneralPath;
import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PGraphicsJava2D;


public class FTextArea extends FEditableTextControl {

	private static float pad = 6;

	public FTextArea(PApplet theApplet, float p0, float p1, float p2, float p3) {
		this(theApplet, p0, p1, p2, p3, SCROLLBARS_NONE, Integer.MAX_VALUE);
	}

	public FTextArea(PApplet theApplet, float p0, float p1, float p2, float p3, int scrollbars) {
		this(theApplet, p0, p1, p2, p3, scrollbars, Integer.MAX_VALUE);
	}

	public FTextArea(PApplet theApplet, float p0, float p1, float p2, float p3, int scrollbars, int wrapWidth) {
		super(theApplet, p0, p1, p2, p3, scrollbars);
		children = new LinkedList<FAbstractControl>();
		tx = ty = pad;
		tw = width - 2 * pad - ((sbPolicy & SCROLLBAR_VERTICAL) != 0 ? 18 : 0);
		th = height - 2 * pad - ((sbPolicy & SCROLLBAR_HORIZONTAL) != 0 ? 18 : 0);
		this.wrapWidth = (wrapWidth == Integer.MAX_VALUE) ? (int)tw : wrapWidth;
		gpTextDisplayArea = new GeneralPath();
		gpTextDisplayArea.moveTo( 0,  0);
		gpTextDisplayArea.lineTo( 0, th);
		gpTextDisplayArea.lineTo(tw, th);
		gpTextDisplayArea.lineTo(tw,  0);
		gpTextDisplayArea.closePath();

		// The image buffer is just for the typing area
		buffer = (PGraphicsJava2D) winApp.createGraphics((int)width, (int)height, PApplet.JAVA2D);
		buffer.rectMode(PApplet.CORNER);
		buffer.g2.setFont(localFont);
		hotspots = new HotSpot[]{
				new HSrect(1, tx, ty, tw, th),			// typing area
				new HSrect(9, 0, 0, width, height)		// control surface
		};
		if((sbPolicy & SCROLLBAR_HORIZONTAL) != 0){
			hsb = new FScrollbar(theApplet, 0, 0, tw, 16);
			addControl(hsb, tx, ty + th + 2, 0);
			hsb.addEventHandler(this, "hsbEventHandler");
			hsb.setAutoHide(autoHide);
		}
		if((sbPolicy & SCROLLBAR_VERTICAL) != 0){
			vsb = new FScrollbar(theApplet, 0, 0, th, 16);
			addControl(vsb, tx + tw + 18, ty, PI/2);
			vsb.addEventHandler(this, "vsbEventHandler");
			vsb.setAutoHide(autoHide);
		}
		setText("", (int)tw);
		z = Z_STICKY;
		createEventHandler(F4P.sketchApplet, "handleButtonEvents", new Class[]{ FTextArea.class });
		registeredMethods = PRE_METHOD | DRAW_METHOD | MOUSE_METHOD | KEY_METHOD;
		F4P.addControl(this);
	}

	public void setDefaultText(String dtext){
		if(dtext == null || dtext.length() == 0)
			defaultText = null;
		else {
			defaultText = new StyledString(dtext, (int)tw);
			defaultText.addAttribute(F4P.POSTURE, F4P.POSTURE_OBLIQUE);
		}
		bufferInvalid = true;
	}

	/**
	 * Give the focus to this component but only after allowing the 
	 * current component with focus to release it gracefully. <br>
	 * Always cancel the keyFocusIsWith irrespective of the component
	 * type. If the component needs to retain keyFocus then override this
	 * method in that class e.g. GCombo
	 */
//	protected void takeFocus(){
//		if(focusIsWith != null && focusIsWith != this)
//			focusIsWith.loseFocus(this);
//		focusIsWith = this;
//	}


	/**
	 * Set the text to be used. The wrap width is determined by the size of the component.
	 * @param text
	 */
	public FTextArea setText(String text){
		setText(text, wrapWidth);
		return this;
	}

	/**
	 * Set the text to display and adjust any scrollbars
	 * @param text
	 * @param wrapWidth
	 */
	public void setText(String text, int wrapWidth){
		startTLHI = null; endTLHI = null;
		this.text = text;
		stext = new StyledString(text, wrapWidth);
		stext.getLines(buffer.g2);
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
	 * If the buffer is invalid then redraw it.
	 */
	protected void updateBuffer(){
		if(bufferInvalid) {
			Graphics2D g2d = buffer.g2;
			// Get the latest lines of text
			LinkedList<TextLayoutInfo> lines = stext.getLines(g2d);
			if(lines.isEmpty() && defaultText != null)
				lines = defaultText.getLines(g2d);
				
			bufferInvalid = false;

			TextLayoutHitInfo startSelTLHI = null, endSelTLHI = null;
			buffer.beginDraw();
			// Whole control surface if opaque
			if(opaque)
				buffer.background(palette[6]);
			else
				buffer.background(buffer.color(255,0));

			// Now move to top left corner of text display area
			buffer.translate(tx,ty); 

			// Typing area surface
			buffer.noStroke();
			buffer.fill(palette[7]);
			buffer.rect(-1,-1,tw+2,th+2);

			g2d.setClip(gpTextDisplayArea);
			buffer.translate(-ptx, -pty);
			// Translate in preparation for display selection and text
			if(hasSelection()){
				if(endTLHI.compareTo(startTLHI) == -1){
					startSelTLHI = endTLHI;
					endSelTLHI = startTLHI;
				}
				else {
					startSelTLHI = startTLHI;
					endSelTLHI = endTLHI;
				}
			}	

			// Display selection and text
			for(TextLayoutInfo lineInfo : lines){
				TextLayout layout = lineInfo.layout;
				buffer.translate(0, layout.getAscent());
				// Draw selection if any
				if(hasSelection() && lineInfo.compareTo(startSelTLHI.tli) >= 0 && lineInfo.compareTo(endSelTLHI.tli) <= 0 ){				
					int ss = 0;
					ss = (lineInfo.compareTo(startSelTLHI.tli) == 0) ? startSelTLHI.thi.getInsertionIndex()  : 0;
					int ee = endSelTLHI.thi.getInsertionIndex();
					ee = (lineInfo.compareTo(endSelTLHI.tli) == 0) ? endSelTLHI.thi.getInsertionIndex() : lineInfo.nbrChars-1;
					g2d.setColor(jpalette[14]);
					Shape selShape = layout.getLogicalHighlightShape(ss, ee);
					g2d.fill(selShape);
				}
				// display text
				g2d.setColor(jpalette[2]);
				lineInfo.layout.draw(g2d, 0, 0);
				buffer.translate(0, layout.getDescent() + layout.getLeading());
			}
			g2d.setClip(null);
			buffer.endDraw();
		}
	}

	public void pre(){
		if(keepCursorInView){
//			System.out.println("SCROLL "+ caretX + " >> " + ptx + "      " + caretY + " >> " + pty);
			boolean horzScroll = false, vertScroll = false;
			if(endTLHI != null){
				if(ptx > caretX){ 										// LEFT?
					ptx -= HORZ_SCROLL_RATE;
					if(ptx < 0) ptx = 0;
					horzScroll = true;
				}
				else if(ptx  < caretX  - tw + 4){ 						// RIGHT?
//					else if(caretX > ptx + tw - 2){ 						// RIGHT?
					ptx += HORZ_SCROLL_RATE;
					horzScroll = true;
				}
				if(pty > caretY){										// UP?
					pty -= VERT_SCROLL_RATE;
					if(pty < 0) pty = 0;
					vertScroll = true;
				}
				else if(pty < caretY - th  + 2 *  stext.getMaxLineHeight()){	// DOWN?
//					else if(caretY > pty + th  - 2 *  stext.getMaxLineHeight()){	// DOWN?
					pty += VERT_SCROLL_RATE;
					vertScroll = true;
				}
				if(horzScroll && hsb != null)
					hsb.setValue(ptx / (stext.getMaxLineLength() + 4));
				if(vertScroll && vsb != null)
					vsb.setValue(pty / (stext.getTextAreaHeight() + 1.5f * stext.getMaxLineHeight()));
			}
			// If we have scrolled invalidate the buffer otherwise forget it
			if(horzScroll || vertScroll)
				bufferInvalid = true;
			else
				keepCursorInView = false;
		}
	}

	public void draw(){
		if(!visible) return;

		// Update buffer if invalid
		updateBuffer();
		winApp.pushStyle();

		winApp.pushMatrix();
		// Perform the rotation
		winApp.translate(cx, cy);
		winApp.rotate(rotAngle);

		winApp.pushMatrix();
		// Move matrix to line up with top-left corner
		winApp.translate(-halfWidth, -halfHeight);
		// Draw buffer
		winApp.imageMode(PApplet.CORNER);
		if(alphaLevel < 255)
			winApp.tint(-1, alphaLevel);
		winApp.image(buffer, 0, 0);
		
		// Draw caret if text display area
		if(focusIsWith == this && showCaret && endTLHI != null){
			float[] cinfo = endTLHI.tli.layout.getCaretInfo(endTLHI.thi);
			float x_left =  - ptx + cinfo[0];
			float y_top = - pty + endTLHI.tli.yPosInPara; 
			float y_bot = y_top - cinfo[3] + cinfo[5];
			if(x_left >= 0 && x_left <= tw && y_top >= 0 && y_bot <= th){
				winApp.strokeWeight(1.9f);
				winApp.stroke(palette[15]);
				winApp.line(tx+x_left, ty+Math.max(0, y_top), tx+x_left, ty+Math.min(th, y_bot));
			}
		}
		
		winApp.popMatrix();
		// Draw scrollbars
		if(children != null){
			for(FAbstractControl c : children)
				c.draw();
		}
		winApp.popMatrix();
		winApp.popStyle();
	}

	public PGraphics getSnapshot(){
		updateBuffer();
		PGraphicsJava2D snap = (PGraphicsJava2D) winApp.createGraphics(buffer.width, buffer.height, PApplet.JAVA2D);
		snap.beginDraw();
		snap.image(buffer,0,0);
		if(hsb != null){
			snap.pushMatrix();
			snap.translate(hsb.getX(), hsb.getY());
			snap.image(hsb.getBuffer(), 0, 0);
			snap.popMatrix();
		}
		if(vsb != null){
			snap.pushMatrix();
			snap.translate(vsb.getX(), vsb.getY());
			snap.rotate(PApplet.PI/2);
			snap.image(vsb.getBuffer(), 0, 0);
			snap.popMatrix();
		}
		snap.endDraw();
		return snap;
	}

	protected void processKeyPressed(int keyCode, char keyChar, boolean shiftDown, boolean ctrlDown){
		boolean validKeyCombo = true;

		switch(keyCode){
		case KeyEvent.VK_LEFT:
			moveCaretLeft(endTLHI);
			break;
		case KeyEvent.VK_RIGHT:
			moveCaretRight(endTLHI);
			break;
		case KeyEvent.VK_UP:
			moveCaretUp(endTLHI);
			break;
		case KeyEvent.VK_DOWN:
			moveCaretDown(endTLHI);
			break;
		case KeyEvent.VK_HOME:
			if(ctrlDown)		// move to start of text
				moveCaretStartOfText(endTLHI);
			else 	// Move to start of line
				moveCaretStartOfLine(endTLHI);
			break;
		case KeyEvent.VK_END:
			if(ctrlDown)		// move to end of text
				moveCaretEndOfText(endTLHI);
			else 	// Move to end of line
				moveCaretEndOfLine(endTLHI);
			break;
		case KeyEvent.VK_A:
			if(ctrlDown){
				moveCaretStartOfText(startTLHI);
				moveCaretEndOfText(endTLHI);
				// Make shift down so that the start caret position is not
				// moved to match end caret position.
				shiftDown = true; // Prevent copying of 
			}
			break;
		case KeyEvent.VK_C:
			if(ctrlDown)
				FClip.copy(getSelectedText());
			validKeyCombo = false;
			break;
		case KeyEvent.VK_V:
			if(ctrlDown){
				String p = FClip.paste();
				if(p.length() > 0){
					// delete selection and add 
					if(hasSelection())
						stext.deleteCharacters(pos, nbr);
					stext.insertCharacters(pos, p);
					adjust = p.length();
					textChanged = true;
				}
			}
			break;
		default:
			validKeyCombo = false;
		}

		if(validKeyCombo){
			calculateCaretPos(endTLHI);
			//****************************************************************
			// If we have moved  to the end of a paragraph marker
			if(caretX > stext.getWrapWidth()){
				switch(keyCode){
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_UP:
				case KeyEvent.VK_DOWN:
				case KeyEvent.VK_END:
					moveCaretLeft(endTLHI);
					validKeyCombo = true;
					break;
				case KeyEvent.VK_RIGHT:
					if(!moveCaretRight(endTLHI))
						moveCaretLeft(endTLHI);
					validKeyCombo = true;
				}
				// Calculate new caret position
				// calculateCaretPos(startTLHI); 
				calculateCaretPos(endTLHI);
			}
			//****************************************************************
			
			calculateCaretPos(endTLHI);	

			if(!shiftDown)
				startTLHI.copyFrom(endTLHI);
			else
				bufferInvalid = true;
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

	protected boolean moveCaretStartOfText(TextLayoutHitInfo currPos){
		if(currPos.tli.lineNo == 0 && currPos.thi.getCharIndex() == 0)
			return false; // already at start of text
		currPos.tli = stext.getTLIforLineNo(0);
		currPos.thi = currPos.tli.layout.getNextLeftHit(1);
		return true;
	}

	protected boolean moveCaretEndOfText(TextLayoutHitInfo currPos){
		if(currPos.tli.lineNo == stext.getNbrLines() - 1 && currPos.thi.getCharIndex() == currPos.tli.nbrChars - 1)
			return false; // already at end of text
		currPos.tli = stext.getTLIforLineNo(stext.getNbrLines() - 1);		
		currPos.thi = currPos.tli.layout.getNextRightHit(currPos.tli.nbrChars - 1);
		return true;
	}

	protected boolean moveCaretUp(TextLayoutHitInfo currPos){
		if(currPos.tli.lineNo == 0)
			return false;
		TextLayoutInfo ntli = stext.getTLIforLineNo(currPos.tli.lineNo - 1);	
		TextHitInfo nthi = ntli.layout.hitTestChar(caretX, 0);
		currPos.tli = ntli;
		currPos.thi = nthi;
		return true;
	}

	protected boolean moveCaretDown(TextLayoutHitInfo currPos){
		if(currPos.tli.lineNo == stext.getNbrLines() - 1)
			return false;
		TextLayoutInfo ntli = stext.getTLIforLineNo(currPos.tli.lineNo + 1);	
		TextHitInfo nthi = ntli.layout.hitTestChar(caretX, 0);
		currPos.tli = ntli;
		currPos.thi = nthi;
		return true;
	}

	/**
	 * Move caret left by one character. If necessary move to the end of the line above
	 * @return true if caret was moved else false
	 */
	protected boolean moveCaretLeft(TextLayoutHitInfo currPos){
		TextLayoutInfo ntli;
		TextHitInfo nthi = currPos.tli.layout.getNextLeftHit(currPos.thi);
		if(nthi == null){ 
			// Move the caret to the end of the previous line 
			if(currPos.tli.lineNo == 0)
				// Can't goto previous line because this is the first line
				return false;
			else {
				// Move to end of previous line
				ntli = stext.getTLIforLineNo(currPos.tli.lineNo - 1);
				nthi = ntli.layout.getNextRightHit(ntli.nbrChars-1);
				currPos.tli = ntli;
				currPos.thi = nthi;
			}
		}
		else {
			// Move the caret to the left of current position
			currPos.thi = nthi;
		}
		return true;
	}

	/**
	 * Move caret right by one character. If necessary move to the start of the next line 
	 * @return true if caret was moved else false
	 */
	protected boolean moveCaretRight(TextLayoutHitInfo currPos){
		TextLayoutInfo ntli;
		TextHitInfo nthi = currPos.tli.layout.getNextRightHit(currPos.thi);
		if(nthi == null){ 
			// Move the caret to the start of the next line the previous line 
			if(currPos.tli.lineNo >= stext.getNbrLines() - 1)
				// Can't goto next line because this is the last line
				return false;
			else {
				// Move to start of next line
				System.out.println("NEXT LINE (FTextArea) \n"+currPos);
				ntli = stext.getTLIforLineNo(currPos.tli.lineNo + 1);
				nthi = ntli.layout.getNextLeftHit(1);
				currPos.tli = ntli;
				currPos.thi = nthi;
				System.out.println(currPos + "\n");
			}
		}
		else {
			// Move the caret to the right of current position
			currPos.thi = nthi;
		}
		return true;
	}

	public void mouseEvent(MouseEvent event){
		if(!visible  || !enabled || !available) return;

		calcTransformedOrigin(winApp.mouseX, winApp.mouseY);
		ox -= tx; oy -= ty; // Remove translation

		currSpot = whichHotSpot(ox, oy);

		if(currSpot == 1 || focusIsWith == this)
			cursorIsOver = this;
		else if(cursorIsOver == this)
			cursorIsOver = null;

		switch(event.getID()){
		case MouseEvent.MOUSE_PRESSED:
			if(currSpot == 1){
				if(focusIsWith != this && z >= focusObjectZ()){
					keepCursorInView = true;
					takeFocus();
				}
				dragging = false;
				if(stext == null || stext.length() == 0){
					stext = new StyledString(" ", (int)tw);
					stext.getLines(buffer.g2);
				}
				endTLHI = stext.calculateFromXY(buffer.g2, ox + ptx, oy + pty);
				startTLHI = new TextLayoutHitInfo(endTLHI);
				calculateCaretPos(endTLHI);
				bufferInvalid = true;
			}
			else { // Not over this control so if we have focus loose it
				if(focusIsWith == this)
					loseFocus(null);
			}
			break;
		case MouseEvent.MOUSE_RELEASED:
			dragging = false;
			bufferInvalid = true;
			break;
		case MouseEvent.MOUSE_DRAGGED:
			if(focusIsWith == this){
				keepCursorInView = true;
				dragging = true;
				endTLHI = stext.calculateFromXY(buffer.g2, ox + ptx, oy + pty);
				calculateCaretPos(endTLHI);
				bufferInvalid = true;
			}
			break;
		}
	}

	protected void calculateCaretPos(TextLayoutHitInfo tlhi){
		float temp[] = tlhi.tli.layout.getCaretInfo(tlhi.thi);
		caretX = temp[0];		
		caretY = tlhi.tli.yPosInPara;
	}

}
