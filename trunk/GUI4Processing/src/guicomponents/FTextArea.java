package guicomponents;
import guicomponents.HotSpot.HSrect;
import guicomponents.StyledString.TextLayoutHitInfo;
import guicomponents.StyledString.TextLayoutInfo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
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

	private static float pad = 4;


	// The typing area
	protected float tx,ty,th,tw;
	// Offsetd to display area
	protected float ptx, pty;


	protected TextLayoutHitInfo startTLHI = null, endTLHI = null;
	protected int startChar = 0, endChar = 0;
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
			sTextHeight = stext.getAllLinesHeight();
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
			boolean isSelection = false;
			TextLayoutHitInfo startSelTLHI = null, endSelTLHI = null;
			buffer.beginDraw();
			buffer.background(buffer.color(255,0));
			buffer.translate(-ptx, -pty);
			buffer.strokeWeight(1.5f);
			LinkedList<TextLayoutInfo> lines = stext.getLines(g2d);
			if(endTLHI != null && startTLHI != null){
				switch(endTLHI.compareTo(startTLHI)){
				case -1:
					startSelTLHI = endTLHI;
					endSelTLHI = startTLHI;
					isSelection = true;
					break;
				case 1:
					startSelTLHI = startTLHI;
					endSelTLHI = endTLHI;
					isSelection = true;
					break;
				default:
					isSelection = false;
				}
			}
			buffer.pushMatrix();
			for(TextLayoutInfo lineInfo : lines){
				TextLayout layout = lineInfo.layout;
				buffer.translate(0, layout.getAscent());
				// Draw selection if any
				if(isSelection && lineInfo.compareTo(startSelTLHI.tli) >= 0 && lineInfo.compareTo(endSelTLHI.tli) <= 0 ){				
					int ss = 0;
					ss = (lineInfo.compareTo(startSelTLHI.tli) == 0) ? startSelTLHI.thi.getInsertionIndex()  : 0;
					int ee = endSelTLHI.thi.getInsertionIndex();
					ee = (lineInfo.compareTo(endSelTLHI.tli) == 0) ? endSelTLHI.thi.getInsertionIndex() : lineInfo.nbrChars-1;
					System.out.println("  In line " + ss + "  " + ee + "  " + lineInfo.startCharIndex);
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
				if(caret[1] != null){
					g2d.setColor(Color.green);
					g2d.draw(caret[1]);
					System.out.println(caret[1]);
				}
				buffer.popMatrix();
				
				
			}
			buffer.endDraw();
			bufferInvalid = false;
		}
	}
//	protected void updateBuffer(){
//		if(bufferInvalid) {
//			Graphics2D g2d = buffer.g2;
//			float drawPosX, drawPosY = 0;
//
//			buffer.beginDraw();
//			buffer.background(buffer.color(255,0));
//			buffer.translate(-ptx, -pty);
//			LinkedList<TextLayout> lines = stext.getLines(g2d);
//			for(TextLayout layout : lines){
//				// Leave the possibility for right justified text
//				drawPosX = (layout.isLeftToRight() ? 0 : stext.getBreakWidth() - layout.getAdvance());
//				drawPosY += layout.getAscent();
//				layout.draw(g2d, drawPosX, drawPosY);
//				drawPosY += layout.getDescent() + layout.getLeading();
//			}
//			if(endSel.valid){
//				buffer.strokeWeight(1.5f);
//				buffer.stroke(255,0,0);
//				buffer.line(endSel.cursorX, endSel.cursorY, endSel.cursorX, endSel.cursorY - endSel.cursorHeight);
//			}
//			buffer.endDraw();
//			bufferInvalid = false;
//		}
//	}

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
			if(y < pty + stext.getMaxLineHeight()){				// UP?
				if(pty < 0) pty = 0;
				pty--;
				vertScroll = true;
			}
			else if(y > pty + th - stext.getMaxLineHeight()){	// DOWN?
				pty++;
				vertScroll = true;
			}
			if(horzScroll && hsb != null)
				hsb.setValue(ptx / (stext.getMaxLineLength() + 4));
			if(vertScroll && vsb != null)
				vsb.setValue(pty / (stext.getAllLinesHeight() + 1.5f * stext.getMaxLineHeight()));
		}
		// If we have scrolled invalidate the buffer otherwise forget it
		if(horzScroll || vertScroll)
			bufferInvalid = true;
		return bufferInvalid;
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
		// Draw the textarea background
		if(opaque)
			winApp.fill(palette[6]);
		else
			winApp.fill(buffer.color(255,0));
		winApp.noStroke();
		// Whole control surface
		winApp.rectMode(CORNER);
		winApp.rect(0,0,width,height);		
		// Typing area surface
		winApp.fill(palette[7]);
		winApp.rect(tx,ty,tw,th);
		// Text
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
			
		}
	}


	public void mouseEvent(MouseEvent event){
		if(!visible  || !enabled || !available) return;

		calcTransformedOrigin(winApp.mouseX, winApp.mouseY);

		currSpot = whichHotSpot(ox, oy);
		if(currSpot >= 0)
			ox -= tx; oy -= ty;

			// This next line will also set ox and oy
			//		boolean mouseOver = contains(winApp.mouseX, winApp.mouseY);

			if(currSpot >= 0 || focusIsWith == this)
				cursorIsOver = this;
			else if(cursorIsOver == this)
				cursorIsOver = null;


			switch(event.getID()){
			case MouseEvent.MOUSE_PRESSED:
				if(focusIsWith != this && currSpot == 1 && z > focusObjectZ()){
					mdx = winApp.mouseX;
					mdy = winApp.mouseY;
					endTLHI = stext.calculateFromXY(buffer.g2, ox + ptx, oy + pty);
					startTLHI = new TextLayoutHitInfo(endTLHI);
					bufferInvalid = true;
					takeFocus();
				}
				break;
			case MouseEvent.MOUSE_CLICKED:
				if(focusIsWith == this){
					loseFocus(null);
					mdx = mdy = Integer.MAX_VALUE;
				}
				break;
			case MouseEvent.MOUSE_RELEASED:
				if(focusIsWith == this){
					if(endTLHI.compareTo(startTLHI) == -1){
						TextLayoutHitInfo temp = endTLHI;
						endTLHI = startTLHI;
						startTLHI = temp;
						System.out.println("SWAP ends");
					}
					dragging = false;
					loseFocus(null);
					bufferInvalid = true;
				}
				break;
			case MouseEvent.MOUSE_DRAGGED:
				if(focusIsWith == this){
					dragging = true;
					endTLHI = stext.calculateFromXY(buffer.g2, ox + ptx, oy + pty);
//					if(endTHI.compareTo(startTHI) == -1){
//						TextLayoutHitInfo temp = endTHI;
//						endTHI = startTHI;
//						startTHI = temp;
//						System.out.println("SWAP ends");
//					}
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
		pty = vsb.getValue() * (stext.getAllLinesHeight() + 1.5f * stext.getMaxLineHeight());
		bufferInvalid = true;
	}

}
