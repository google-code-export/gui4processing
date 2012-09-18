package guicomponents;

import guicomponents.HotSpot.HSrect;
import guicomponents.StyledString.TextLayoutInfo;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PGraphicsJava2D;
import processing.core.PImage;

public class FButton extends FTextIconComponent {

	// Button status values
	static final int OFF	= 0;
	static final int OVER	= 1;
	static final int DOWN	= 2;

	protected int status;

	
	// Only report CLICKED events
	protected boolean reportAllButtonEvents = false;
	

	public FButton(PApplet theApplet, float p0, float p1, float p2, float p3, String text) {
		super(theApplet, p0, p1, p2, p3);
		if(text == null || text.length() == 0)
			text = "Button Text";
		this.text = text;
		// The image buffer is just for the button surface
		buffer = (PGraphicsJava2D) winApp.createGraphics((int)width, (int)height, PApplet.JAVA2D);
		buffer.rectMode(PApplet.CORNER);
		buffer.g2.setFont(localFont);
		hotspots = new HotSpot[]{
				new HSrect(1, 0, 0, width, height)		// control surface
		};
		setTextNew(text, (int) width - 4);
		opaque = false;
		createEventHandler(G4P.mainWinApp, "handleButtonEvents", new Class[]{ FButton.class });
		z = Z_SLIPPY;
		// Now register control with applet
		registeredMethods = DRAW_METHOD;
		F4P.addControl(this);
	}
	
	/**
	 * If the parameter is true all 3 event types are generated, if false
	 * only CLICKED events are generated (default behaviour).
	 * @param all
	 */
	public void fireAllEvents(boolean all){
		reportAllButtonEvents = all;
	}

	/**
	 * 
	 * When a button is clicked on a GButton it generates 3 events (in this order) 
	 * mouse down, mouse up and mouse clicked. <br>
	 * You can test for a particular event type with PRESSED, RELEASED: <br>
	 * <pre>
	 * 	void handleButtonEvents(GButton button) {
	 *	  if(button == btnName && button.eventType == GButton.PRESSED){
	 *        // code for button click event
	 *    }
	 * </pre> <br>
	 * Where <pre><b>btnName</b></pre> is the GButton identifier (variable name) <br><br>
	 * 
	 * If you only wish to respond to button click events then use the statement <br>
	 * <pre>btnName.fireAllEvents(false); </pre><br> 
	 * This is the default mode.
	 */
	public void mouseEvent(MouseEvent event){
		if(!visible || !enabled || !available) return;

		calcTransformedOrigin(winApp.mouseX, winApp.mouseY);
		currSpot = whichHotSpot(ox, oy);

		// currSpot == 1 for text display area
		if(currSpot >= 0 || focusIsWith == this)
			cursorIsOver = this;
		else if(cursorIsOver == this)
			cursorIsOver = null;

		switch(event.getID()){
		case MouseEvent.MOUSE_PRESSED:
			if(focusIsWith != this && currSpot >= 0  && z > focusObjectZ()){
				dragging = false;
				status = DOWN;
				takeFocus();
				eventType = PRESSED;
				if(reportAllButtonEvents)
					fireEvent();
			}
			break;
		case MouseEvent.MOUSE_CLICKED:
			// No need to test for isOver() since if the component has focus
			// and the mouse has not moved since MOUSE_PRESSED otherwise we 
			// would not get the Java MouseEvent.MOUSE_CLICKED event
			if(focusIsWith == this){
				status = OFF;
				loseFocus(null);
				dragging = false;
				eventType = CLICKED;
				fireEvent();
			}
			break;
		case MouseEvent.MOUSE_RELEASED:	
			// if the mouse has moved then release focus otherwise
			// MOUSE_CLICKED will handle it
			if(focusIsWith == this && dragging){
				if(currSpot >= 0){
					eventType = CLICKED;
					fireEvent();
				}
				else {
					if(reportAllButtonEvents){
						eventType = RELEASED;
						fireEvent();
					}
				}
				dragging = false;
				loseFocus(null);
				status = OFF;
			}
			break;
		case MouseEvent.MOUSE_MOVED:
			// If dragged state will stay as PRESSED
			if(currSpot >= 0)
				status = OVER;
			else
				status = OFF;
			break;
		case MouseEvent.MOUSE_DRAGGED:
			if(focusIsWith == this)
				dragging = true;
			break;
		}
	}
	
	private void calcTransformedOrigin(int mouseX, int mouseY) {
		// TODO Auto-generated method stub
		
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
		// Move matrix to line up with top-left corner
		winApp.translate(-halfWidth, -halfHeight);
		// Draw buffer
		winApp.imageMode(PApplet.CORNER);
		winApp.image(buffer, 0, 0);	
		winApp.popMatrix();		
		winApp.popStyle();
	}
	
	protected void updateBuffer(){
		if(bufferInvalid) {
			Graphics2D g2d = buffer.g2;
			buffer.beginDraw();
			switch(status){
			case OVER:
				buffer.background(palette[6]);
				break;
			case DOWN:
				buffer.background(palette[14]);
				break;
			default:
				buffer.background(palette[4]);

			}			
			LinkedList<TextLayoutInfo> lines = stext.getLines(g2d);	

			buffer.translate(2, (height - stext.getTextAreaHeight())/2);
			for(TextLayoutInfo lineInfo : lines){
				TextLayout layout = lineInfo.layout;
				buffer.translate(0, layout.getAscent());
				// display text
				g2d.setColor(jpalette[2]);
				float dx = (stext.getWrapWidth() - stext.getMaxLineLength())/2;
				lineInfo.layout.draw(g2d, dx, 0);
				buffer.translate(0, layout.getDescent() + layout.getLeading());
			}
			buffer.endDraw();
		}	
	}
	
}
