package guicomponents;

import guicomponents.HotSpot.HSrect;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PGraphicsJava2D;

public class FCombo extends FAbstractControl {

	protected static final int FORE_COLOR = 2;
	protected static final int BACK_COLOR = 4;
	protected static final int OVER_LIST = 6;
	protected static final int OVER_ITEM = 5;
	protected static final int ITEM_FORE_COLOR = 3;
	
	protected Font localFont = F4P.globalFont;

	protected String[] items;
	protected StyledString[] sitems;
	
	protected int selItem = 0;
	
	protected int startItem = 0;
	protected int listSize = 4;
	
	protected float itemHeight, buttonWidth;

	protected boolean expanded = false;

	private FScrollbar vsb;
	private FButton showList;


	public FCombo(PApplet theApplet, float p0, float p1, float p2, float p3, int dropListSize) {
		super(theApplet, p0, p1, p2, p3);
		children = new LinkedList<FAbstractControl>();
		dropListSize = Math.max(dropListSize, 4);
		itemHeight = height / dropListSize;
		buttonWidth = Math.max(itemHeight, 16);
		
		// The image buffer is just for the typing area
		buffer = (PGraphicsJava2D) winApp.createGraphics((int)width, (int)height, PApplet.JAVA2D);
		buffer.rectMode(PApplet.CORNER);
		
		vsb = new FScrollbar(theApplet, 0, 0, height - itemHeight, 10);
		addCompoundControl(vsb, width, itemHeight + 1, PI/2);
		vsb.addEventHandler(this, "vsbEventHandler");
		vsb.setAutoHide(true);
	
		showList = new FButton(theApplet, 0, 0, buttonWidth, itemHeight, ":");
		addCompoundControl(showList, width - buttonWidth, 0, 0);
		showList.addEventHandler(this, "showButton");
		
		buffer.g2.setFont(localFont);
		hotspots = new HotSpot[]{
				new HSrect(1, 0, 0, width - buttonWidth, itemHeight),	// selected text area
				new HSrect(2, 0, itemHeight+1, width - 12, height - itemHeight - 1)		// text list area
		};

		
		
		// Set dummy list for testing
		String[] x = new String[] {"Peter Lager", "Donald Duck", "Charlie Brown", "Doctor Who", "Fireman Sam", "Manic Miner" };
		setItems(x,1);
	}
	
	
	public void setItems(String[] items, int selected){
		this.items = items;
		this.selItem = selected;
		for(int i = 0; i < items.length; i++){
			sitems[i] = new StyledString(buffer.g2, items[i]);
		}
	}
	
	public void vsbEventHandler(FScrollbar scrollbar){
		float pos = vsb.getValue();
		bufferInvalid = true;
	}

	protected void updateBuffer(){
		if(bufferInvalid) {
			Graphics2D g2d = buffer.g2;

		}
	}
	
}