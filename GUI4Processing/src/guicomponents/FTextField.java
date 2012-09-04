package guicomponents;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.util.LinkedList;

import guicomponents.HotSpot.HSrect;
import guicomponents.StyledString.TextLayoutHitInfo;
import guicomponents.StyledString.TextLayoutInfo;
import processing.core.PApplet;
import processing.core.PGraphicsJava2D;

public class FTextField extends FTextComponent {

	int pad = 2;
	
	public FTextField(PApplet theApplet, float p0, float p1, float p2, float p3) {
		this(theApplet, p0, p1, p2, p3, SCROLLBARS_NONE);
	}
	
	public FTextField(PApplet theApplet, float p0, float p1, float p2, float p3, int scrollbars) {
		super(theApplet, p0, p1, p2, p3, scrollbars);
		tx = ty = pad;
		tw = width - 2 * pad;
		th = height - ((sbPolicy & SCROLLBAR_HORIZONTAL) != 0 ? 11 : 0);
		// The image buffer is just for the typing area
		buffer = (PGraphicsJava2D) winApp.createGraphics((int)tw, (int)th, PApplet.JAVA2D);
		buffer.rectMode(PApplet.CORNER);
		buffer.g2.setFont(fLocalFont);
		hotspots = new HotSpot[]{
				new HSrect(1, tx, ty, tw, th),			// typing area
				new HSrect(9, 0, 0, width, height),		// control surface
		};
		if((sbPolicy & SCROLLBAR_HORIZONTAL) != 0){
			hsb = new FScrollbar(theApplet, 0, 0, tw, 10);
			addCompoundControl(hsb, tx, ty + th + 2, 0);
			hsb.addEventHandler(this, "hsbEventHandler");
		}
		setText("");
		z = Z_STICKY;
		registerAutos_DMPK(true, true, false, true);
	}

	public void setText(String text){
		this.text = text;
		stext = new StyledString(buffer.g2, text);
		if(hsb != null){
			System.out.println(stext.getMaxLineLength());
			if(stext.getMaxLineLength() < tw)
				hsb.setValue(0,1);
			else
				hsb.setValue(0, tw/stext.getMaxLineLength());
		}
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
					int ss = startSelTLHI.thi.getInsertionIndex();
					int ee = endSelTLHI.thi.getInsertionIndex();
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
			if(showCaret && endTLHI != null){
				buffer.pushMatrix();
				buffer.translate(0, endTLHI.tli.layout.getAscent() );
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
		boolean horzScroll = false;
		if(endTLHI != null){
			if(caretX < ptx ){ 										// LEFT?
				ptx--;
				if(ptx < 0) ptx = 0;
				horzScroll = true;
			}
			else if(caretX > ptx + tw - 4){ 						// RIGHT?
				ptx++;
				horzScroll = true;
			}
			if(horzScroll && hsb != null)
				hsb.setValue(ptx / (stext.getMaxLineLength() + 4));
		}
		// If we have scrolled invalidate the buffer otherwise forget it
		if(horzScroll)
			bufferInvalid = true;
		// Let the user know we have scrolled
		return horzScroll;
	}
	
	protected void calculateCaretPos(TextLayoutHitInfo tlhi){
		float temp[] = tlhi.tli.layout.getCaretInfo(tlhi.thi);
		caretX = temp[0];
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
		case KeyEvent.VK_HOME:
			caretMoved = moveCaretStartOfLine(endTLHI);
			break;
		case KeyEvent.VK_END:
			caretMoved = moveCaretEndOfLine(endTLHI);
			break;
		}
		calculateCaretPos(endTLHI);
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
		if(ascii >= 32 && ascii < 127){
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
			if((posInLine > tli.nbrChars)) posInLine = tli.nbrChars;
			
			try{
				thiLeft = tli.layout.getNextLeftHit(posInLine);
			}
			catch(Exception excp){
				thiLeft = null;
			}
			try{
				thiRight = tli.layout.getNextRightHit(posInLine);
			}
			catch(Exception excp){
				thiRight = null;
			}

			System.out.println("Pos = " + posInLine + "  " + pos + "  " + adjust);
			System.out.println("\t\tLEFT   " + thiLeft);
			System.out.println("\t\tRIGHT  " + thiRight);
								
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
//				else
//					thi = tli.layout.getNextRightHit(tli.nbrChars - 1);	
				break;
			case -1:
				if(thiLeft == null){		// we are at end of line
//					if(thiLeft.getCharIndex() == 0)
					thi = tli.layout.getNextLeftHit(0);	
				}
				else if(thiRight == null)	
					thi = tli.layout.getNextRightHit(tli.nbrChars - 1);	
				else
					thi = thiLeft;
//					thi = tli.layout.getNextLeftHit(tli.nbrChars);
				break;
			case 1:
				if(thiRight == null)	// we are at the end of line
					thi = tli.layout.getNextRightHit(tli.nbrChars - 1);	
				else 	// Move to end of the line
					thi = thiRight;
				break;
			}
			endTLHI.setInfo(tli, thi);
			// Cursor at end of paragraph graphic
			calculateCaretPos(endTLHI);
			
			// Finish off by ensuring no selection, invalidate buffer etc.
			startTLHI.copyFrom(endTLHI);
			setScrollbarValues();
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
//		if(opaque)
//			winApp.fill(palette[6]);
//		else
			winApp.fill(buffer.color(255,0));
		winApp.noStroke();
		winApp.rectMode(CORNER);
		winApp.rect(0,0,width,height);		
		// Typing area surface
		winApp.stroke(palette[3]);
		winApp.strokeWeight(1);
		winApp.fill(palette[7]);
		winApp.rect(0,0,width,th+2);
		// Now display the text 
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


}