package guicomponents;

import guicomponents.HotSpot.HSrect;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;

import processing.core.PApplet;
import processing.core.PGraphicsJava2D;

public class FScrollbar extends GComponent {

	private static final int OFF_FILL = 4;
	private static final int OFF_STROKE = 4;
	private static final int OVER_FILL = 3;
	private static final int OVER_STROKE = 2;
	private static final int TRACK = 6;
	
	protected RoundRectangle2D lowCap, highCap;
	private BasicStroke pen = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

	protected float value = 0.2f;
	protected float startDragValue;
	protected float filler = .5f;
	protected boolean autoHide = false;
	boolean currOverThumb = false;
	boolean isValueChanging = false;
	
	public FScrollbar(PApplet theApplet, float p0, float p1, float p2, float p3) {
		super(theApplet, p0, p1, p2, p3);
		buffer = (PGraphicsJava2D) winApp.createGraphics((int)width, (int)height, PApplet.JAVA2D);
		buffer.rectMode(PApplet.CORNER);
		hotspots = new HotSpot[]{
				new HSrect(1, 0, 0, 16, height),
				new HSrect(2, width - 16, 0, 16, height),
				new HSrect(9, 17, 0, width - 17, height)
		};
		lowCap = new RoundRectangle2D.Float(1, 1, 15, height-2, 6, 6);
		highCap = new RoundRectangle2D.Float(width - 15, 1, 13, height-2, 6, 6);
		
		Arrays.sort(hotspots); // belt and braces
		registerAutos_DMPK(true, true, false, false);
	}

	public void setValue(float value, float filler){
		this.value = value;
		this.filler = filler;
		bufferInvalid = true;
	}
	
	/**
	 * All GUI components are registered for mouseEvents
	 */
	public void mouseEvent(MouseEvent event){
		if(!visible  || !enabled || !available) return;
		
		// This next line will also set ox and oy
		boolean mouseOver = contains(winApp.mouseX, winApp.mouseY);
		

		int spot = whichHotSpot(ox, oy);
		if(spot >= 9){
			if(isOverThumb(ox, oy))
				spot = 10;
			else
				spot = 9;
		}
		if(spot != currSpot){
			currSpot = spot;
			bufferInvalid = true;
		}
		
//		System.out.println(ox +"  " +oy +"  " + spot);
		
		if(mouseOver || focusIsWith == this)
			cursorIsOver = this;
		else if(cursorIsOver == this)
			cursorIsOver = null;

		switch(event.getID()){
		case MouseEvent.MOUSE_PRESSED:
			if(focusIsWith != this && mouseOver && z > focusObjectZ()){
				mdx = winApp.mouseX;
				mdy = winApp.mouseY;
				startDragValue = value;
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
				loseFocus(null);
				mdx = mdy = Integer.MAX_VALUE;
				isValueChanging = false;
				bufferInvalid = true;
			}
			break;
		case MouseEvent.MOUSE_DRAGGED:
			if(focusIsWith == this){
				float movement = winApp.mouseX - winApp.pmouseX;
				float deltaV = movement / (width - 32);
				value += deltaV;
				value = PApplet.constrain(value, 0, 1.0f - filler);
				isValueChanging = true;
				bufferInvalid = true;
			}
			break;
		}
	}
	
	protected boolean isOverThumb(float px, float py){
		float p = (px - 16) / (width - 32);
		boolean over =( p >= value && p < value + filler);
		return over;
	}
	
	protected void updateBuffer(){
		Graphics2D g2d = buffer.g2;
		buffer.beginDraw();
		buffer.background(buffer.color(255,0));
		buffer.fill(palette[TRACK]);
		buffer.noStroke();
		buffer.rect(8,2,width-8,height-3);
		g2d.setStroke(pen);
		
		// Draw low end placement
		g2d.setColor(jpalette[3]);
		
		buffer.fill(palette[3]);
		buffer.strokeWeight(2.0f);
		if(currSpot == 1){
			g2d.setColor(jpalette[OVER_FILL]);
			g2d.fill(lowCap);
			g2d.setColor(jpalette[OVER_STROKE]);
			g2d.draw(lowCap);
		}
		else {
			g2d.setColor(jpalette[OFF_FILL]);
			g2d.fill(lowCap);
			g2d.setColor(jpalette[OFF_STROKE]);
			g2d.draw(lowCap);
		}
		if(currSpot == 2){
			g2d.setColor(jpalette[OVER_FILL]);
			g2d.fill(highCap);
			g2d.setColor(jpalette[OVER_STROKE]);
			g2d.draw(highCap);
		}
		else {
			g2d.setColor(jpalette[OFF_FILL]);
			g2d.fill(highCap);
			g2d.setColor(jpalette[OFF_STROKE]);
			g2d.draw(highCap);
		}

		float thumbWidth = (width - 32) * filler;
		System.out.println(thumbWidth);
		RoundRectangle2D thumb = new RoundRectangle2D.Float(1,1,thumbWidth-1, height-2,6,6);
		buffer.translate((width - 32) * value + 16, 0);
		if(currSpot == 10 || isValueChanging){
			g2d.setColor(jpalette[OVER_FILL]);
			g2d.fill(thumb);
			g2d.setColor(jpalette[OVER_STROKE]);
			g2d.draw(thumb);
		}
		else {
			g2d.setColor(jpalette[OFF_FILL]);
			g2d.fill(thumb);
			g2d.setColor(jpalette[OFF_STROKE]);
			g2d.draw(thumb);
		}
		// draw thumb
//		buffer.noStroke();
//		buffer.translate((width - 16) * value + 16, 0);
//		buffer.beginShape(PApplet.QUADS);
//		buffer.fill(palette[5]);
//		buffer.vertex(0, 0);
//		buffer.vertex(thumbWidth, 0);
//		buffer.fill(palette[3]);
//		buffer.vertex(thumbWidth, height);
//		buffer.vertex(0, height);
//		buffer.endShape();
//		// Draw thumb highlight if mouse over
//		if(currSpot == 10){
//			buffer.noFill();
//			buffer.stroke(palette[2]);
//			buffer.beginShape();
//			buffer.vertex(0, 0);
//			buffer.vertex(thumbWidth, 0);
//			buffer.vertex(thumbWidth, height);
//			buffer.vertex(0, height);
//			buffer.endShape(PApplet.CLOSE);
//		}
		bufferInvalid = false;
		buffer.endDraw();
	}
	
	public void draw(){
		if(!visible) return;
		if(bufferInvalid)
			updateBuffer();
		// Get absolute position
//		Point pos = new Point(0,0);
//		calcAbsPosition(pos);

		winApp.pushStyle();
		winApp.pushMatrix();
		
		winApp.translate(cx, cy);
		winApp.rotate(rotAngle);
		winApp.imageMode(PApplet.CENTER);
		winApp.image(buffer, 0, 0);

		if(children != null){
		for(GComponent c : children)
			c.draw();
		}
		winApp.popMatrix();
		winApp.popStyle();

	}	
		
}
