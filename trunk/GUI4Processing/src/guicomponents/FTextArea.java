package guicomponents;
import guicomponents.HotSpot.HSrect;
import guicomponents.StyledString.TextLayoutHitInfo;
import guicomponents.StyledString.TextLayoutInfo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PGraphicsJava2D;


public class FTextArea extends GComponent {


	/** Do not create or display any scrollbars for the text area. */
	public static final int SCROLLBARS_NONE = 0;
	/** Create and display vertical scrollbar only. */
	public static final int SCROLLBARS_VERTICAL_ONLY = 1;
	/** Create and display horizontal scrollbar only. */
	public static final int SCROLLBARS_HORIZONTAL_ONLY = 2;
	/** Create and display both vertical and horizontal scrollbars. */
	public static final int SCROLLBARS_BOTH = 3;


	/** Create and display vertical scrollbar only. */
	protected static final int SCROLLBAR_VERTICAL = 1;
	/** Create and display horizontal scrollbar only. */
	protected static final int SCROLLBAR_HORIZONTAL = 2;

	private static float pad = 6;


	// The typing area
	protected float tx,ty,th,tw;
	// Offsetd to display area
	protected float ptx, pty;


	protected TextLayoutHitInfo startTLHI = null, endTLHI = null;
//	protected int startChar = 0, endChar = 0;
	protected boolean dragging = false;
	
	protected int allTextHeight = 0;

	// The scrollbar policy
	protected final int sbPolicy;
	protected boolean autoHide = true;
	FScrollbar hsb, vsb;


	public FTextArea(PApplet theApplet, float p0, float p1, float p2, float p3) {
		this(theApplet, p0, p1, p2, p3, SCROLLBARS_NONE);
	}

	public FTextArea(PApplet theApplet, float p0, float p1, float p2, float p3, int scrollbars) {
		super(theApplet, p0, p1, p2, p3);
		sbPolicy = scrollbars;
		tx = ty = pad;
		tw = width - 2 * pad - ((sbPolicy & SCROLLBAR_VERTICAL) != 0 ? 18 : 0);
		th = height - 2 * pad - ((sbPolicy & SCROLLBAR_HORIZONTAL) != 0 ? 18 : 0);
		// The image buffer is just for the typing area
		buffer = (PGraphicsJava2D) winApp.createGraphics((int)tw, (int)th, PApplet.JAVA2D);
		buffer.rectMode(PApplet.CORNER);
		hotspots = new HotSpot[]{
				new HSrect(1, tx, ty, tw, th),			// typing area
				new HSrect(9, 0, 0, width, height),		// control surface
		};
		if((sbPolicy & SCROLLBAR_HORIZONTAL) != 0){
			hsb = new FScrollbar(theApplet, 0, 0, tw, 16);
			addCompoundControl(hsb, tx, ty + th + 2, 0);
			hsb.addEventHandler(this, "hsbEventHandler");
		}
		if((sbPolicy & SCROLLBAR_VERTICAL) != 0){
			vsb = new FScrollbar(theApplet, 0, 0, th, 16);
			addCompoundControl(vsb, tx + tw + 18, ty, PI/2);
			vsb.addEventHandler(this, "vsbEventHandler");
		}
		opaque = true;
		setText("",tw);
		z = Z_STICKY;
		//createEventHandler(G4P.mainWinApp, "handleTextAreaEvents", new Class[]{ FTextArea.class });
		registerAutos_DMPK(true, true, false, true);
	}

	public void setText(String text){
		setText(text, tw);
	}

	public void setText(String text, float maxLineLength){
		this.text = text;
		stext = new StyledString(buffer.g2, text, maxLineLength);
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

	public boolean hasSelection(){
		return (startTLHI != null && endTLHI != null && startTLHI.compareTo(endTLHI) != 0);	
	}
	
	/**
	 * If the buffer is invalid then redraw it.
	 * @TODO need to use palette for colours
	 */
	protected void updateBuffer(){
		if(bufferInvalid) {
			Graphics2D g2d = buffer.g2;
			TextLayoutHitInfo startSelTLHI = null, endSelTLHI = null;
			
			buffer.beginDraw();
			buffer.background(buffer.color(255,0));
			buffer.translate(-ptx, -pty);
			buffer.strokeWeight(1.5f);
			LinkedList<TextLayoutInfo> lines = stext.getLines(g2d);
			boolean hasSelection = hasSelection();
			if(hasSelection){
				if(endTLHI.compareTo(startTLHI) == -1){
					startSelTLHI = endTLHI;
					endSelTLHI = startTLHI;
				}
				else {
					startSelTLHI = startTLHI;
					endSelTLHI = endTLHI;
				}
			}
			buffer.pushMatrix();
			for(TextLayoutInfo lineInfo : lines){
				TextLayout layout = lineInfo.layout;
				buffer.translate(0, layout.getAscent());
				// Draw selection if any
				if(hasSelection && lineInfo.compareTo(startSelTLHI.tli) >= 0 && lineInfo.compareTo(endSelTLHI.tli) <= 0 ){				
					int ss = 0;
					ss = (lineInfo.compareTo(startSelTLHI.tli) == 0) ? startSelTLHI.thi.getInsertionIndex()  : 0;
					int ee = endSelTLHI.thi.getInsertionIndex();
					ee = (lineInfo.compareTo(endSelTLHI.tli) == 0) ? endSelTLHI.thi.getInsertionIndex() : lineInfo.nbrChars-1;
					g2d.setColor(Color.cyan);
					Shape selShape = layout.getLogicalHighlightShape(ss, ee);
					g2d.fill(selShape);
				}
				g2d.setColor(Color.black);
				lineInfo.layout.draw(g2d, 0, 0);
				buffer.translate(0, layout.getDescent() + layout.getLeading());
			}
			buffer.popMatrix();
			// Draw caret
			if(endTLHI != null ){
				buffer.pushMatrix();
				buffer.translate(0, endTLHI.tli.yPosInPara + endTLHI.tli.layout.getAscent() );
				Shape[] caret = endTLHI.tli.layout.getCaretShapes(endTLHI.thi.getInsertionIndex());
				g2d.setColor(Color.red);
				g2d.draw(caret[0]);
				buffer.popMatrix();
			}
			buffer.endDraw();
			bufferInvalid = false;
		}
	}


	/**
	 * See if the cursor is off screen if so pan the display
	 * @return
	 */
	protected boolean keepCursorInDisplay(){
		boolean horzScroll = false, vertScroll = false;
		if(endTLHI != null){
			float temp[] = endTLHI.tli.layout.getCaretInfo(endTLHI.thi);
			float x = temp[0];		
			float y = endTLHI.tli.yPosInPara;
			if(x < ptx ){ 										// LEFT?
				ptx--;
				if(ptx < 0) ptx = 0;
				horzScroll = true;
			}
			else if(x > ptx + tw){ 								// RIGHT?
				ptx++;
				horzScroll = true;
			}
			if(y < pty){				// UP?
				pty--;
				if(pty < 0) pty = 0;
				vertScroll = true;
			}
			else if(y > pty + th  - stext.getMaxLineHeight()){	// DOWN?
				pty++;
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
		// Let the user know we have scrolled
		return horzScroll | vertScroll;
	}

	public void draw(){
		if(!visible) return;
		updateBuffer();

		winApp.pushStyle();
		winApp.pushMatrix();

		winApp.translate(cx, cy);
		winApp.rotate(rotAngle);

		winApp.pushMatrix();
		winApp.translate(-halfWidth, -halfHeight);
		// Whole control surface if opaque
		if(opaque)
			winApp.fill(palette[6]);
		else
			winApp.fill(buffer.color(255,0));
		winApp.noStroke();
		winApp.rectMode(CORNER);
		winApp.rect(0,0,width,height);		
		// Typing area surface
		winApp.fill(palette[7]);
		winApp.rect(tx-1,ty-1,tw+2,th+2);
		// Now the text 
		winApp.imageMode(PApplet.CORNER);
		winApp.image(buffer, tx, ty);

		winApp.popMatrix();

		if(children != null){
			for(GComponent c : children)
				c.draw();
		}
		winApp.popMatrix();
		winApp.popStyle();
	}

	public void keyEvent(KeyEvent e) {
		if(!visible  || !enabled || !available) return;
		if(focusIsWith == this && endTLHI != null){

			//			int shortcutMask = java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
			boolean shiftDown = ((e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) == KeyEvent.SHIFT_DOWN_MASK);
			boolean ctrlDown = ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK);

			// KEY PRESSED ================================================================================================================================
			// Process caret movement
			if(e.getID() == KeyEvent.KEY_PRESSED) {
				boolean caretMoved = false;
				if(e.getKeyCode() == KeyEvent.VK_LEFT) {
					caretMoved = moveCaretLeft(endTLHI);
				}
				else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
					caretMoved = moveCaretRight(endTLHI);
				}
				else if(e.getKeyCode() == KeyEvent.VK_HOME) {
					if(ctrlDown){
						// move to start of text
						System.out.println("Move to start of text");
						caretMoved = moveCaretStartOfText(endTLHI);
					}
					else {
						// Move to start of line
						caretMoved = moveCaretStartOfLine(endTLHI);
					}
				}
				else if(e.getKeyCode() == KeyEvent.VK_END) {
					if(ctrlDown){
						// move to end of text
						System.out.println("Move to end of text");
						caretMoved = moveCaretEndOfText(endTLHI);
					}
					else {
						// Move to end of line
						caretMoved = moveCaretEndOfLine(endTLHI);
					}
				}
				// After testing for cursor moving keys
				if(caretMoved){
					if(!shiftDown)				// Not extending selection
						startTLHI.copyFrom(endTLHI);
					bufferInvalid = true;							
				}
			}			
			// KEY TYPED ================================================================================================================================
			else if(e.getID() == KeyEvent.KEY_TYPED && e.getKeyChar() != KeyEvent.CHAR_UNDEFINED){
				boolean hasSelection = hasSelection();
				int endChar = endTLHI.tli.startCharIndex + endTLHI.thi.getInsertionIndex();
				int startChar = (startTLHI != null) ? startTLHI.tli.startCharIndex + startTLHI.thi.getInsertionIndex() : endChar;
				int pos, nbr, adjust = 0;

				int adjustPart = 0;
				
				if(startChar < endChar){
					pos = startChar;
					nbr = endChar - pos;
				}
				else if(startChar > endChar){
					pos = endChar;
					nbr = startChar - pos;
				}
				else {
					pos = endChar;
					nbr = 0;
				}
				// Assume that the text will not be changed by the keypress
				// set this to true when the text does change
				boolean textChanged = false;
				// If we have a selection then any key typed will delete it
				if(hasSelection){
					stext.deleteCharacters(pos, nbr);
					adjust = 0;
					textChanged = true;
				}
				else {		
					// Only process back_space and delete if there was no selection
					if(e.getKeyChar() == KeyEvent.VK_BACK_SPACE){
						if(stext.deleteCharacters(pos - 1, 1)){
							adjust = -1;
							textChanged = true;
						}
					}
					else if(e.getKeyChar() == KeyEvent.VK_DELETE){
						if(stext.deleteCharacters(pos, 1)){
							adjust = 0;
							textChanged = true;
						}
					}
				}
				// Now we have got rid of any selection be can process other kys
				if(e.getKeyChar() == KeyEvent.VK_ENTER){
					if(stext.insertCharacters(pos, "\n ")){
						adjust = 1;
						textChanged = true;
					}
				}
				else {
					int ascii = (int)(e.getKeyChar());
					if(ascii >= 32 && ascii < 127){
						if(stext.insertCharacters(pos, "" + e.getKeyChar())){
							adjust = 1;
							textChanged = true;
						}
					}
				}
				
				if(textChanged){
					TextLayoutInfo tli;
					TextHitInfo thi = null, thiLeft, thiRight;
					// Force update
					stext.getLines(buffer.g2);
					
					tli = stext.getTLIforCharNo(pos);
					int posInLine = pos - tli.startCharIndex;
					
					System.out.println(tli.lineNo + "  starts @ " + tli.startCharIndex + "      pos in line " + posInLine + "    length " +  tli.nbrChars);
					
					thiLeft = tli.layout.getNextLeftHit(posInLine);
					thiRight = tli.layout.getNextRightHit(posInLine);
//					// These should always be not null
//					if(thiLeft == null)
//						System.out.println(">>>>>>>>>>> thiLeft null");
//					if(thiRight == null)
//						System.out.println(">>>>>>>>>>> thiRight null");
//					if(thiLeft != null && thiRight != null){
//						System.out.println("===============================================");
//						System.out.print("Pos in line " + posInLine + "  Hit left " + thiLeft.getInsertionIndex());
//						System.out.println("    Hit right " + thiRight.getInsertionIndex());
//						System.out.println("===============================================");
//					}
//					System.out.println();
					
					// We need to 'adjust' the caret position based on what we did
					//  0 caret does not move from current position
					// -1 caret needs to go left
					//  1 caret needs to go right
					switch(adjust){
					case 0:		
						if(thiLeft != null)
							thi = tli.layout.getNextRightHit(thiLeft);
						else if(thiRight != null)
							thi = tli.layout.getNextLeftHit(thiRight);
						break;
					case -1:
						if(thiLeft == null){   // we are at start of line
							if(tli.lineNo == 0){
								// first line so move to beginning of text
								thi = tli.layout.getNextLeftHit(0);
							}
							else {
								// Attempt to move to end of previous line
								tli = stext.getTLIforLineNo(tli.lineNo - 1);
								thi = tli.layout.getNextRightHit(tli.nbrChars - 1);
							}
						}
						else {
							thi = thiLeft;
						}
						break;
					case 1:
						if(thiRight == null){	// we are at the end of line
							if(tli.lineNo == stext.getNbrLines() - 1){ 
								// Last line so move to right of last character
								thi = tli.layout.getNextRightHit(tli.nbrChars - 1);	
								adjustPart = 1;
							}
							else {
								// Attempt to move to start of next line
								tli = stext.getTLIforLineNo(tli.lineNo + 1);
								thi = tli.layout.getNextRightHit(0);
								adjustPart = 2;
							}
							
						}
						else {
							thi = thiRight;
							adjustPart = 3;
						}
						
						break;
					}
					if(thi == null){
						System.out.println("KeyEvent handling thi = null   adjust = " + adjust + "  " + adjustPart + "\n\n");
					}
					endTLHI = new TextLayoutHitInfo(tli, thi);
					startTLHI.copyFrom(endTLHI);
					bufferInvalid = true;
				}
			}
			while(keepCursorInDisplay());
		}

	}
/*
 * If arrow key VK_HOME or VK_END but no shift
 * 		advance caret to new position
 * 			if result is null move to end of last line or move to beginning of next line if possible
 * 		end selection by setting startTHLI to null
 * 		invalidate buffer if caret moved
 * If arrow key VK_HOME or VK_END but with shift
 * 		advance caret to new position
 * 			if result is null move to end of last line or move to beginning of next line if possible
 * 		invalidate buffer if caret moved
 * if arrow key and ctrl key
 * 		move to start/end line/text
 * 		end selection
 * if ctrl key and c and has selection
 * 		copy to clipboard
 * if ctrl key and v
 * 		if has selection delete the selected text
 * 		paste from clipboard
 * * 		insert key at current position
 * 		recalculate endTHLI to end of pasted test
 * 		startTHLI = null
 *		invalidate buffer
 * if backspace key
 * 
 * if delete key
 * 
 * if any other key
 * 		if has selection delete the selected text set startTLHI to null
 * 		insert key at current position
 * 		recalculate endTHLI
 * 		startTHLI = null
 * 		advance caret to next right position
 * 		invalidate layouts
 * 		invalidate buffer
 * 
 * 
 * 
 */
	
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
				bufferInvalid = true;
			}
		}
		else {
			// Move the caret to the left of current position
			currPos.thi = nthi;
			bufferInvalid = true;			
		}
		return true;
	}
	
	/**
	 * Move caret left by one character. If necessary move to the end of the line above
	 * @return true if caret was moved else false
	 */
	protected boolean moveCaretRight(TextLayoutHitInfo currPos){
		TextLayoutInfo ntli;
		TextHitInfo nthi = currPos.tli.layout.getNextRightHit(currPos.thi);
		if(nthi == null){ 
			// Move the caret to the end of the previous line 
			if(currPos.tli.lineNo >= stext.getNbrLines() - 1)
				// Can't goto next line because this is the last line
				return false;
			else {
				// Move to start of next line
				ntli = stext.getTLIforLineNo(currPos.tli.lineNo + 1);
				nthi = ntli.layout.getNextLeftHit(1);
				currPos.tli = ntli;
				currPos.thi = nthi;
				bufferInvalid = true;
			}
		}
		else {
			// Move the caret to the right of current position
			currPos.thi = nthi;
			bufferInvalid = true;			
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
				if(focusIsWith != this && z > focusObjectZ()){
					takeFocus();
				}
				mdx = winApp.mouseX;
				mdy = winApp.mouseY;
				endTLHI = stext.calculateFromXY(buffer.g2, ox + ptx, oy + pty);
				startTLHI = new TextLayoutHitInfo(endTLHI);
				bufferInvalid = true;
			}
			break;
		case MouseEvent.MOUSE_RELEASED:
			dragging = false;
			bufferInvalid = true;
			break;
		case MouseEvent.MOUSE_DRAGGED:
			if(focusIsWith == this){
				dragging = true;
				endTLHI = stext.calculateFromXY(buffer.g2, ox + ptx, oy + pty);
				bufferInvalid = true;
				keepCursorInDisplay();
			}
			break;
		}
	}

	protected void calcScrollbaValue(){
		
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
