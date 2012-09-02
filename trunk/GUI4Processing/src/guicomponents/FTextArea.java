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
	// Offset to display area
	protected float ptx, pty;
	// Caret position
	float caretX, caretY;

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
					g2d.setColor(jpalette[14]);
					Shape selShape = layout.getLogicalHighlightShape(ss, ee);
					g2d.fill(selShape);
				}
				g2d.setColor(jpalette[2]);
				lineInfo.layout.draw(g2d, 0, 0);
				buffer.translate(0, layout.getDescent() + layout.getLeading());
			}
			buffer.popMatrix();
			// Draw caret
			if(endTLHI != null ){
				buffer.pushMatrix();
				buffer.translate(0, endTLHI.tli.yPosInPara + endTLHI.tli.layout.getAscent() );
				Shape[] caret = endTLHI.tli.layout.getCaretShapes(endTLHI.thi.getInsertionIndex());
				g2d.setColor(jpalette[15]);
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
			//			float temp[] = endTLHI.tli.layout.getCaretInfo(endTLHI.thi);
			//			float x = temp[0];		
			//			float y = endTLHI.tli.yPosInPara;
			if(caretX < ptx ){ 										// LEFT?
				ptx--;
				if(ptx < 0) ptx = 0;
				horzScroll = true;
			}
			else if(caretX > ptx + tw){ 								// RIGHT?
				ptx++;
				horzScroll = true;
			}
			if(caretY < pty){				// UP?
				pty--;
				if(pty < 0) pty = 0;
				vertScroll = true;
			}
			else if(caretY > pty + th  + stext.getMaxLineHeight()){	// DOWN?
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

	protected boolean processKeyPressed(KeyEvent e, boolean shiftDown, boolean ctrlDown){
		int keyCode = e.getKeyCode();
		boolean caretMoved = false;

		switch(keyCode){
		case KeyEvent.VK_LEFT:
			caretMoved = moveCaretLeft(endTLHI);
			break;
		case KeyEvent.VK_RIGHT:
			caretMoved = moveCaretRight(endTLHI);
			break;
		case KeyEvent.VK_UP:
			caretMoved = moveCaretUp(endTLHI);
			break;
		case KeyEvent.VK_DOWN:
			caretMoved = moveCaretDown(endTLHI);
			break;
		case KeyEvent.VK_HOME:
			if(ctrlDown){		// move to start of text
				System.out.println("Move to start of text");
				caretMoved = moveCaretStartOfText(endTLHI);
			}
			else 	// Move to start of line
				caretMoved = moveCaretStartOfLine(endTLHI);
			break;
		case KeyEvent.VK_END:
			if(ctrlDown){		// move to end of text
				caretMoved = moveCaretEndOfText(endTLHI);
			}
			else 	// Move to end of line
				caretMoved = moveCaretEndOfLine(endTLHI);
			break;
		}
		calculateCaretPos(endTLHI);
		if(caretX > stext.getBreakWidth()){
			switch(keyCode){
			case KeyEvent.VK_LEFT:
			case KeyEvent.VK_UP:
			case KeyEvent.VK_DOWN:
			case KeyEvent.VK_END:
				moveCaretLeft(endTLHI);
				caretMoved = true;
				break;
			case KeyEvent.VK_RIGHT:
				if(!moveCaretRight(endTLHI))
					moveCaretLeft(endTLHI);
				caretMoved = true;
			}
			// Calculate new caret position
			calculateCaretPos(endTLHI);
		}
		// After testing for cursor moving keys
		if(caretMoved){
			if(!shiftDown)				// Not extending selection
				startTLHI.copyFrom(endTLHI);
			bufferInvalid = true;	
			while(keepCursorInDisplay());
		}
		return caretMoved;
	}

	protected void processKeyTyped(KeyEvent e, boolean shiftDown, boolean ctrlDown){
		int endChar = endTLHI.tli.startCharIndex + endTLHI.thi.getInsertionIndex();
		int startChar = (startTLHI != null) ? startTLHI.tli.startCharIndex + startTLHI.thi.getInsertionIndex() : endChar;
		int pos = endChar, nbr = 0, adjust = 0;
		boolean hasSelection = (startTLHI.compareTo(endTLHI) != 0);

		if(hasSelection){ // Have we some text selected?
			if(startChar < endChar){ // Forward selection
				pos = startChar; nbr = endChar - pos;
			}
			else if(startChar > endChar){ // Backward selection
				pos = endChar;	nbr = startChar - pos;
			}
		}
		
		char keyChar = e.getKeyChar();
		int ascii = (int)keyChar;
		boolean textChanged = false;
		// If we have a selection then any key typed will delete it
		if(hasSelection){
			stext.deleteCharacters(pos, nbr);
			adjust = 0; textChanged = true;
		}
		else {	// Only process back_space and delete if there was no selection
			if(keyChar == KeyEvent.VK_BACK_SPACE){
				if(stext.deleteCharacters(pos - 1, 1)){
					adjust = -1; textChanged = true;
				}
			}
			else if(keyChar == KeyEvent.VK_DELETE){
				if(stext.deleteCharacters(pos, 1)){
					adjust = 0; textChanged = true;
				}
			}
		}
		// Now we have got rid of any selection be can process other keys
		if(keyChar == KeyEvent.VK_ENTER){
			if(stext.insertCharacters(pos, "\n ")){
				adjust = 1; textChanged = true;
			}
		}
		else if(ascii >= 32 && ascii < 127){
			if(stext.insertCharacters(pos, "" + e.getKeyChar())){
				adjust = 1; textChanged = true;
			}
		}
		if(textChanged){
			TextLayoutInfo tli;
			TextHitInfo thi = null, thiLeft, thiRight;
			// Force update
			stext.getLines(buffer.g2);

			tli = stext.getTLIforCharNo(pos);
			int posInLine = pos - tli.startCharIndex;
//
//			System.out.println(tli.lineNo + "  starts @ " + tli.startCharIndex + "      pos in line " + posInLine + "    length " +  tli.nbrChars);

			thiLeft = tli.layout.getNextLeftHit(posInLine);
			thiRight = tli.layout.getNextRightHit(posInLine);					
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
				if(thiLeft == null){		// we are at start of line
					if(tli.lineNo == 0){	// first line so move to beginning of text
						thi = tli.layout.getNextLeftHit(0);
					}
					else {					// Attempt to move to end of previous line
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
					}
					else {	// Attempt to move to start of next line
						tli = stext.getTLIforLineNo(tli.lineNo + 1);
						thi = tli.layout.getNextRightHit(0);
					}
				}
				else {
					thi = thiRight;
				}
				break;
			}
			endTLHI.setInfo(tli, thi);
			// Cursor at end of paragraph graphic
			calculateCaretPos(endTLHI);
			if(caretX > stext.getBreakWidth()){
				switch(keyChar){
				case KeyEvent.VK_DELETE:
				case KeyEvent.VK_BACK_SPACE:
					moveCaretLeft(endTLHI);
					break;
				case KeyEvent.VK_ENTER:
					if(!moveCaretRight(endTLHI))
						moveCaretLeft(endTLHI);

					break;
				}
				calculateCaretPos(endTLHI);
			}
			// Finish off by ensuring no selection, invalidate buffer etc.
			startTLHI.copyFrom(endTLHI);
			bufferInvalid = true;
			while(keepCursorInDisplay());
		} // End of text changed == true

	}

	public void keyEvent(KeyEvent e) {
		if(!visible  || !enabled || !available) return;
		if(focusIsWith == this && endTLHI != null){
			//			int shortcutMask = java.awt.Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
			boolean shiftDown = ((e.getModifiersEx() & KeyEvent.SHIFT_DOWN_MASK) == KeyEvent.SHIFT_DOWN_MASK);
			boolean ctrlDown = ((e.getModifiersEx() & KeyEvent.CTRL_DOWN_MASK) == KeyEvent.CTRL_DOWN_MASK);

			if(e.getID() == KeyEvent.KEY_PRESSED) {
				processKeyPressed(e, shiftDown, ctrlDown);
			}
			else if(e.getID() == KeyEvent.KEY_TYPED && e.getKeyChar() != KeyEvent.CHAR_UNDEFINED){
				processKeyTyped(e, shiftDown, ctrlDown);
			}
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
				calculateCaretPos(endTLHI);
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
				calculateCaretPos(endTLHI);
				bufferInvalid = true;
				keepCursorInDisplay();
			}
			break;
		}
	}

	protected void calculateCaretPos(TextLayoutHitInfo tlhi){
		float temp[] = tlhi.tli.layout.getCaretInfo(tlhi.thi);
		caretX = temp[0];		
		caretY = tlhi.tli.yPosInPara;

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
