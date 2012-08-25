package guicomponents;
import guicomponents.HotSpot.HSrect;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;

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
		tw = width - 2 * pad - ((sbPolicy & SCROLLBAR_HORIZONTAL) != 0 ? 18 : 0);
		th = height - 2 * pad - ((sbPolicy & SCROLLBAR_VERTICAL) != 0 ? 18 : 0);
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

	public void updateBuffer(){
		Graphics2D g2d = buffer.g2;
		buffer.beginDraw();
		buffer.background(buffer.color(255,0));
//		buffer.background(palette[14]);


		buffer.endDraw();
		bufferInvalid = false;
	}
	
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
			winApp.fill(palette[0]);
		else
			winApp.fill(buffer.color(255,0));
		winApp.noStroke();
		winApp.rectMode(CORNER);
		winApp.rect(0,0,width,height);
		winApp.fill(palette[7]);
		winApp.rect(tx,ty,tw,th);
		
		winApp.imageMode(PApplet.CORNER);
		winApp.image(buffer, tx,	ty);
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
		

		int spot = whichHotSpot(ox, oy);
		// 
		if(spot >= 0){
			System.out.println("OVER typing area");
		}

	}
	
	public void hsbEventHandler(FScrollbar scrollbar){
		System.out.println("HORZ " + hsb.getValue());
	}

	public void vsbEventHandler(FScrollbar scrollbar){
		System.out.println("VERT " + vsb.getValue());

	}

}
