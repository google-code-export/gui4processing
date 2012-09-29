package guicomponents;

import guicomponents.HotSpot.HSrect;
import guicomponents.StyledString.TextLayoutInfo;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PGraphicsJava2D;

public abstract class FToggleControl extends FTextIconControl {

	protected FToggleGroup group = null;

	protected boolean selected = false;

	public FToggleControl(PApplet theApplet, float p0, float p1, float p2, float p3) {
		super(theApplet, p0, p1, p2, p3);
		// The image buffer is just for the typing area
		buffer = (PGraphicsJava2D) winApp.createGraphics((int)width, (int)height, PApplet.JAVA2D);
		buffer.rectMode(PApplet.CORNER);
		buffer.g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
//		buffer.g2.setFont(localFont);
		opaque = false;
		hotspots = new HotSpot[]{
				new HSrect(1, 0, 0, width, height)		// control surface
		};
	}

	// This method is called if this control is added to a toggle group. A toggle group
	// enforces single option selection from the group. Override this with an empty method
	// to allow each toggle control to be independant of others.
	protected void setToggleGroup(FToggleGroup tg) {
		this.group = tg;
	}	
	
	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		bufferInvalid = (this.selected != selected);
		if(selected && group != null)
			group.makeSelected(this);
		this.selected = selected;
//		System.out.println("  " + tag + "  buffer invalid? " + bufferInvalid);
	}

	protected void hasBeenClicked(){
		if(group == null){
			// Independent action e.e. check box
			selected = !selected;
			bufferInvalid = true;
		}
		else {
			// Only need to do something if we click on an unselected option
			if(!selected)
				setSelected(true);
		}
	}
	
	public void mouseEvent(MouseEvent event){
		// If this option does not belong to a group then ignore mouseEvents
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
			if(focusIsWith != this && currSpot >= 0 && z > focusObjectZ()){
				dragging = false;
				takeFocus();
			}
			break;
		case MouseEvent.MOUSE_CLICKED:
			if(focusIsWith == this){
				hasBeenClicked();
				loseFocus(null);
				eventType = SELECTED;
				fireEventX(this);
			}
			break;
		case MouseEvent.MOUSE_DRAGGED:
			dragging = true;
			break;
		case MouseEvent.MOUSE_RELEASED:
			// Release focus without firing an event - that would have 
			// been done
			if(focusIsWith == this && dragging)
				this.loseFocus(null);
			dragging = false;
			break;
		}
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
		if(alphaLevel < 255)
			winApp.tint(-1, alphaLevel);
		winApp.image(buffer, 0, 0);	
		winApp.popMatrix();
		
		winApp.popStyle();
	}

	protected void updateBuffer(){
		if(bufferInvalid) {
			Graphics2D g2d = buffer.g2;
			// Get the latest lines of text
			LinkedList<TextLayoutInfo> lines = stext.getLines(g2d);	
			bufferInvalid = false;

			buffer.beginDraw();
			// Back ground colour
			if(opaque == true)
				buffer.background(palette[6]);
			else
				buffer.background(buffer.color(255,0));
			// Calculate text and icon placement
			calcAlignment();
			// If there is an icon draw it
			if(iconW != 0)
				if(selected)
					buffer.image(bicon[1], siX, siY);
				else
					buffer.image(bicon[0], siX, siY);
			float wrapWidth = stext.getWrapWidth();
			float sx = 0, tw = 0;
			buffer.translate(stX, stY);
			for(TextLayoutInfo lineInfo : lines){
				TextLayout layout = lineInfo.layout;
				buffer.translate(0, layout.getAscent());
				switch(textAlignH){
				case GAlign.CENTER:
					tw = layout.getAdvance();
					tw = (tw > wrapWidth) ? tw - wrapWidth : tw;
					sx = (wrapWidth - tw)/2;
					break;
				case GAlign.RIGHT:
					tw = layout.getAdvance();
					tw = (tw > wrapWidth) ? tw - wrapWidth : tw;
					sx = wrapWidth - tw;
					break;
				case GAlign.LEFT:
				case GAlign.JUSTIFY:
				default:
					sx = 0;		
				}
				// display text
				g2d.setColor(jpalette[2]);
				lineInfo.layout.draw(g2d, sx, 0);
				buffer.translate(0, layout.getDescent() + layout.getLeading());
			}
			buffer.endDraw();
		}	
	}
}
