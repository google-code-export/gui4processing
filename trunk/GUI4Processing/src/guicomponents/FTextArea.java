package guicomponents;
import guicomponents.HotSpot.HSrect;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.geom.GeneralPath;
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

	protected float ptx, pty;
	protected Location endSel = new Location();
	
	protected int allTextHeight = 0;

	// The scrollbar policy
	protected final int sbPolicy;
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
		if(vsb != null){
			sTextHeight = stext.getAllLinesHeight();
			ptx = pty = 0;
			if(sTextHeight < th){
				vsb.setValue(0.0f, 1.0f);
			}
			else {
				vsb.setValue(0, th/sTextHeight);
			}
		}
		if(hsb != null){
			if(stext.getMaxLineLength() < tw)
				hsb.setValue(0,1);
			else
				hsb.setValue(0, tw/stext.getMaxLineLength());
		}
	}


	public void updateBuffer(){
		Graphics2D g2d = buffer.g2;
		buffer.beginDraw();
		buffer.background(buffer.color(255,0));
		buffer.translate(-ptx, -pty);
		float drawPosX, drawPosY = 0;
		LinkedList<TextLayout> lines = this.stext.getLines(g2d);
		for(TextLayout layout : lines){
			// Leave the possibility for right justified text
			drawPosX = (layout.isLeftToRight() ? 0 : stext.getBreakWidth() - layout.getAdvance());
			drawPosY += layout.getAscent();
			layout.draw(g2d, drawPosX, drawPosY);
			drawPosY += layout.getDescent() + layout.getLeading();
		}
		if(endSel.valid){
			buffer.strokeWeight(1.5f);
			buffer.stroke(255,0,0);
			buffer.line(endSel.cursorX, endSel.cursorY, endSel.cursorX, endSel.cursorY - endSel.cursorHeight);
		}
		buffer.endDraw();
		bufferInvalid = false;
	}

//	g2d.drawLine((int)cursorPos.cursorX, (int)cursorPos.cursorY, (int)cursorPos.cursorX, (int)(cursorPos.cursorY - cursorPos.cursorHeight));

	public void draw(){
		if(!visible) return;
		if(bufferInvalid)
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
	}
	
	
	public void mouseEvent(MouseEvent event){
		if(!visible  || !enabled || !available) return;
		
		// This next line will also set ox and oy
		boolean mouseOver = contains(winApp.mouseX, winApp.mouseY);
		
		if(mouseOver || focusIsWith == this)
			cursorIsOver = this;
		else if(cursorIsOver == this)
			cursorIsOver = null;

		int spot = whichHotSpot(ox, oy);
		
		if(spot == 1) {
			ox -= tx; oy -= ty;
			System.out.println("TextArea @ " + ox + "  " + oy);
		}


		switch(event.getID()){
		case MouseEvent.MOUSE_PRESSED:
			if(focusIsWith != this && mouseOver && z > focusObjectZ()){
				mdx = winApp.mouseX;
				mdy = winApp.mouseY;
//				startDragValue = value;
				stext.getCursorPos(buffer.g2, endSel, ox + ptx, oy +pty);
				bufferInvalid = true;
				System.out.println("Get cursor position " + endSel);
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
			if(focusIsWith == this && mouseHasMoved(winApp.mouseX, winApp.mouseY)){
//				loseFocus(null);
//				mdx = mdy = Integer.MAX_VALUE;
//				isValueChanging = false;
//				bufferInvalid = true;
			}
			break;
		case MouseEvent.MOUSE_DRAGGED:
			if(focusIsWith == this){
//				float movement = ox - last_ox;
//				last_ox = ox;
//				float deltaV = movement / (width - 32);
//				value += deltaV;
//				value = PApplet.constrain(value, 0, 1.0f - filler);
//				isValueChanging = true;
//				bufferInvalid = true;
//				eventType = CHANGED;
//				fireEvent();
			}
			break;
		}
	}

	
	public void hsbEventHandler(FScrollbar scrollbar){
		System.out.println("HORZ " + hsb.getValue());
		ptx = hsb.getValue() * (stext.getMaxLineLength() + 4);
		bufferInvalid = true;
	}

	public void vsbEventHandler(FScrollbar scrollbar){
		System.out.println("VERT " + vsb.getValue());
		pty = vsb.getValue() * (stext.getAllLinesHeight() + 1.5f * stext.getMaxLineHeight());
		bufferInvalid = true;
	}

}
