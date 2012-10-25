package guicomponents;

import guicomponents.HotSpot.HSrect;
import guicomponents.StyledString.TextLayoutInfo;

import java.awt.Graphics2D;
import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PGraphicsJava2D;

public class FCombo extends FTextControl {


	protected String[] items;
	protected StyledString[] sitems;
	
	protected int selItem = 0;
	protected int overItem = -1;
	
	protected int startItem = 0;
	protected int dropListMaxSize = 4;
	protected int dropListActualSize = 4;
	
	protected float itemHeight, buttonWidth;

	protected boolean expanded = true;   // make false in release version


	private FScrollbar vsb;
	private FButton showList;

	

	public FCombo(PApplet theApplet, float p0, float p1, float p2, float p3, int dropListMaxSize) {
		super(theApplet, p0, p1, p2, p3);
		children = new LinkedList<FAbstractControl>();
		this.dropListMaxSize = Math.max(dropListMaxSize, 3);
		itemHeight = height / (dropListMaxSize + 1); // make allowance for selected text at top
		buttonWidth = Math.max(itemHeight, 16);
		
		// The image buffer is just for the typing area
		buffer = (PGraphicsJava2D) winApp.createGraphics((int)width, (int)height, PApplet.JAVA2D);
		buffer.rectMode(PApplet.CORNER);
		
		vsb = new FScrollbar(theApplet, 0, 0, height - itemHeight, 10);
		vsb.setAutoHide(false);
		addCompoundControl(vsb, width, itemHeight + 1, PI/2);
		vsb.addEventHandler(this, "vsbEventHandler");
		vsb.setAutoHide(true);
	
		buttonWidth = 10;
		showList = new FButton(theApplet, 0, 0, buttonWidth, itemHeight, ":");
		addCompoundControl(showList, width - buttonWidth, 0, 0);
		showList.addEventHandler(this, "showButton");
		
		buffer.g2.setFont(localFont);
		hotspots = new HotSpot[]{
				new HSrect(1, 0, 0, width - buttonWidth, itemHeight),	// selected text area
				new HSrect(2, 0, itemHeight+1, width - 12, height - itemHeight - 1)		// text list area
		};

		z = Z_STICKY;
		createEventHandler(G4P.mainWinApp, "handleComboEvents", new Class[]{ GCombo.class });

		registeredMethods = DRAW_METHOD;
		F4P.addControl(this);
	
	}
	
	
	public void setItems(String[] items, int selected){
		this.items = items;
		this.selItem = selected;
		sitems = new StyledString[items.length];
		for(int i = 0; i < items.length; i++){
			sitems[i] = new StyledString(items[i]);
		}
		dropListActualSize = Math.min(items.length, dropListMaxSize);
		vsb.setVisible(items.length > dropListActualSize);
		
	}
	
	public void showButton(FButton button){
		if(expanded){
			loseFocus(null);
			vsb.setVisible(false);
			expanded = false;
		}
		else {
			takeFocus();
			vsb.setVisible(items.length > dropListActualSize);
			expanded = true;
		}
		bufferInvalid = true;
	}
	
	public void vsbEventHandler(FScrollbar scrollbar){
		System.out.println("Scrolling");
		float pos = vsb.getValue();
		bufferInvalid = true;
	}

	public void draw(){
		if(!visible) return;
		updateBuffer();

		winApp.pushStyle();
		winApp.pushMatrix();

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
		
		winApp.popMatrix();

		if(children != null){
			for(FAbstractControl c : children)
				c.draw();
		}
		winApp.popMatrix();
		winApp.popStyle();
	}

	protected void updateBuffer(){
		if(bufferInvalid) {
			Graphics2D g2d = buffer.g2;
			bufferInvalid = false;
			
			buffer.beginDraw();
			buffer.background(buffer.color(255,0));
			
			buffer.noStroke();
			buffer.fill(palette[BACK_COLOR]);
			buffer.rect(0, 0, width, itemHeight);
			
			if(expanded){
				buffer.fill(palette[LIST_BACK_COLOR]);
				buffer.rect(0,itemHeight, width, itemHeight * dropListActualSize);
			}
			
			StyledString ss = sitems[selItem];
//			TextLayoutInfo tli = ss.getLines(g2d);
			
//			float textYadjust = 
			
			buffer.endDraw();

		}
	}
	
	protected static final int FORE_COLOR = 2;
	protected static final int BACK_COLOR = 5;
	protected static final int LIST_BACK_COLOR = 6;
	protected static final int ITEM_FORE_COLOR = 3;
	protected static final int ITEM_BACK_COLOR = 5;
	

}