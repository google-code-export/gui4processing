package guicomponents;

import guicomponents.HotSpot.HSrect;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PGraphicsJava2D;

public class FDropList extends FTextControl {
	
	protected static final int FORE_COLOR = 2;
	protected static final int BACK_COLOR = 5;
	protected static final int ITEM_FORE_COLOR = 3;
	protected static final int ITEM_BACK_COLOR = 6;
	protected static final int OVER_ITEM_FORE_COLOR = 15;
	

	private FScrollbar vsb;
	private FButton showList;

	protected String[] items;
	protected StyledString[] sitems;
	protected StyledString selText;
	
	protected int selItem = 0;
	protected int startItem = 0;
	protected int lastOverItem = -1;
	protected int currOverItem = lastOverItem;
	
	
	protected int dropListMaxSize = 4;
	protected int dropListActualSize = 4;
	
	protected float itemHeight, buttonWidth;

	protected boolean expanded = false;   // make false in release version

	public FDropList(PApplet theApplet, float p0, float p1, float p2, float p3, int dropListMaxSize) {
		super(theApplet, p0, p1, p2, p3);
		children = new LinkedList<FAbstractControl>();
		this.dropListMaxSize = Math.max(dropListMaxSize, 3);
		itemHeight = height / (dropListMaxSize + 1); // make allowance for selected text at top
//		buttonWidth = Math.max(itemHeight, 16);
		
		// The image buffer is just for the typing area
		buffer = (PGraphicsJava2D) winApp.createGraphics((int)width, (int)height, PApplet.JAVA2D);
		buffer.rectMode(PApplet.CORNER);
		
		vsb = new FScrollbar(theApplet, 0, 0, height - itemHeight, 10);
		vsb.setAutoHide(false);
		addCompoundControl(vsb, width, itemHeight + 1, PI/2);
		vsb.addEventHandler(this, "scrollbarEventHandler");
		vsb.setAutoHide(true);
		vsb.setVisible(false);
		
		buttonWidth = 10;
		showList = new FButton(theApplet, 0, 0, buttonWidth, itemHeight, ":");
		addCompoundControl(showList, width - buttonWidth, 0, 0);
		showList.addEventHandler(this, "buttonEventHandler");
		
		buffer.g2.setFont(localFont);
		hotspots = new HotSpot[]{
				new HSrect(1, 0, itemHeight+1, width - 11, height - itemHeight - 1),	// text list area
				new HSrect(2, 0, 0, width - buttonWidth, itemHeight)	// selected text display area
		};

		z = Z_STICKY;
		createEventHandler(winApp, "handleDropListEvents", new Class[]{ FDropList.class });

		registeredMethods = DRAW_METHOD | MOUSE_METHOD;
		F4P.addControl(this);
	}
	
	/**
	 * Use this to change the list of items to appear in the list. If
	 * you enter an invalid selection index then it is forced into
	 * the valid range. <br>
	 * If the list is null or empty then then no changes are made. <br>
	 * @param list
	 * @param selected
	 */
	public void setItems(String[] list, int selected){
		if(list == null || list.length == 0)
			return;
		items = list;
		sitems = new StyledString[list.length];
		// Create styled strings for display
		for(int i = 0; i < list.length; i++)
			sitems[i] = new StyledString(list[i]);
		// Force selected value into valid range
		selItem = PApplet.constrain(selected, 0, list.length - 1);
		// Make selected item bold
		sitems[selItem].addAttribute(WEIGHT, WEIGHT_BOLD);
		// Create separate styled string for display area
		selText = new StyledString(this.items[selItem]);
		dropListActualSize = Math.min(list.length, dropListMaxSize);
		if((list.length > dropListActualSize)){
			float filler = ((float)dropListMaxSize)/list.length;
			float value = ((float)startItem)/list.length;
			vsb.setValue(value, filler);
		}
		vsb.setVisible(false);
	}
	
	public int getSelectedIndex(){
		return selItem;
	}
	
	public String getSelectedText(){
		return items[selItem];
	}
	
	/**
	 * Set the currently selected item from the droplist by index position. <br>
	 * Invalid values are ignored.
	 * 
	 * @param selected
	 */
	public void setSelected(int selected){
		if(selected >=0 && selected < sitems.length){
			selItem = selected;
			for(StyledString s : sitems)
				s.clearAllAttributes();
			sitems[selItem].addAttribute(WEIGHT, WEIGHT_BOLD);
			selText = new StyledString(this.items[selItem]);			
			bufferInvalid = true;
		}
	}
	
	public void mouseEvent(MouseEvent event){
		if(!visible || !enabled || !available) return;

		calcTransformedOrigin(winApp.mouseX, winApp.mouseY);
		currSpot = whichHotSpot(ox, oy);

		if(currSpot >= 0 || focusIsWith == this)
			cursorIsOver = this;
		else if(cursorIsOver == this)
			cursorIsOver = null;

		switch(event.getID()){
		case MouseEvent.MOUSE_CLICKED:
			// No need to test for isOver() since if the component has focus
			// and the mouse has not moved since MOUSE_PRESSED otherwise we 
			// would not get the Java MouseEvent.MOUSE_CLICKED event
			if(focusIsWith == this ){
				loseFocus(null);
				vsb.setVisible(false);
				expanded = false;
				bufferInvalid = true;
				// Make sure that we have selected a valid item and that
				// it is not the same as before;
				if(currOverItem >= 0 && currOverItem != selItem){
					setSelected(currOverItem);
					fireEventX(this);
				}
				currOverItem = lastOverItem = -1;
				eventType = SELECTED;
			}
			break;
		case MouseEvent.MOUSE_MOVED:
			if(focusIsWith == this){
				if(currSpot == 1)
					currOverItem = startItem + Math.round(oy / itemHeight) - 1; 
				else
					currOverItem = -1;
				// Only invalidate the buffer if the over item has changed
				if(currOverItem != lastOverItem){
					lastOverItem = currOverItem;
					bufferInvalid = true;
				}
			}
			break;
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
				buffer.fill(palette[ITEM_BACK_COLOR]);
				buffer.rect(0,itemHeight, width, itemHeight * dropListActualSize);
			}
			
			float px = TPAD, py;
			TextLayout line;
			// Get selected text for display
			line = selText.getLines(g2d).getFirst().layout;
			py = (itemHeight + line.getAscent() - line.getDescent())/2;
		
			g2d.setColor(jpalette[FORE_COLOR]);
			line.draw(g2d, px, py);
			
			if(expanded){
				g2d.setColor(jpalette[ITEM_FORE_COLOR]);
				for(int i = 0; i < dropListActualSize; i++){
					py += itemHeight;
					if(currOverItem == startItem + i)
						g2d.setColor(jpalette[OVER_ITEM_FORE_COLOR]);
					else
						g2d.setColor(jpalette[ITEM_FORE_COLOR]);

					line = sitems[startItem + i].getLines(g2d).getFirst().layout;				
					line.draw(g2d, px, py);
				}
			}
			buffer.endDraw();
		}
	}
	
	/**
	 * For most components there is nothing to do when they loose focus.
	 * Override this method in classes that need to do something when
	 * they loose focus eg TextField
	 */
	protected void loseFocus(FAbstractControl grabber){
		if(grabber != vsb){
			expanded = false;
			vsb.setVisible(false);
			bufferInvalid = true;
		}
		if(cursorIsOver == this)
			cursorIsOver = null;
		focusIsWith = grabber;
	}


	/**
	 * This method should <b>not</b> be called by the user. It
	 * is for internal library use only.
	 */
	public void scrollbarEventHandler(FScrollbar scrollbar){
		int newStartItem = Math.round(vsb.getValue() * items.length);
		startItem = newStartItem;
		bufferInvalid = true;
	}

	/**
	 * This method should <b>not</b> be called by the user. It
	 * is for internal library use only.
	 */
	public void buttonEventHandler(FButton button){
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

}