package guicomponents;

import guicomponents.HotSpot.HScircle;
import guicomponents.HotSpot.HSrect;
import guicomponents.StyledString.TextLayoutInfo;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedList;

import processing.core.PApplet;
import processing.core.PGraphicsJava2D;

public class FSlider extends FValueControl {

	static protected float TINSET = 4;
	static protected int THUMB_SPOT = 1;
	static protected int TRACK_SPOT = 2;
	
	protected float trackStart, trackEnd, trackWidth, trackLength, trackDisplayLength;
	protected RoundRectangle2D track;

//	protected boolean dragable = false;
	protected int downHotSpot = -1;
	
	public FSlider(PApplet theApplet, float p0, float p1, float p2, float p3, float tr_width) {
		super(theApplet, p0, p1, p2, p3);
		trackWidth = tr_width;
		trackDisplayLength = width - 2 * TINSET;
		trackLength = trackDisplayLength - trackWidth;
		trackStart = (width - trackLength)/2;
		trackEnd = (width + trackLength)/2;
		track = new RoundRectangle2D.Float(-trackDisplayLength/2, -trackWidth/2, 
				trackDisplayLength, trackWidth, 
				trackWidth, trackWidth );

		buffer = (PGraphicsJava2D) winApp.createGraphics((int)width, (int)height, PApplet.JAVA2D);
		hotspots = new HotSpot[]{
				new HScircle(THUMB_SPOT, width/2 + (valuePos - 0.5f) * trackLength, height/2, trackWidth/2 ),  // thumb
				new HSrect(TRACK_SPOT, (width-trackLength)/2, (height-trackWidth)/2, trackLength, trackWidth),		// track
		};
		//		opaque = false;
		z = Z_SLIPPY;

		epsilon = 0.98f / trackLength;
		ssStartLimit = new StyledString(buffer.g2, "0.00");
		ssEndLimit = new StyledString(buffer.g2, "1.00");
		ssValue = new StyledString(buffer.g2, "0.50");

		// Now register control with applet
		createEventHandler(winApp, "handleSliderEvents", new Class[]{ FValueControl.class });
		registeredMethods = PRE_METHOD | DRAW_METHOD | MOUSE_METHOD;
		F4P.addControl(this);
	}

	public void mouseEvent(MouseEvent event){
		if(!visible || !enabled || !available) return;

		calcTransformedOrigin(winApp.mouseX, winApp.mouseY);
		currSpot = whichHotSpot(ox, oy);
		// Normalise ox and oy to the centre of the slider
		ox -= width/2;
		ox /= trackLength;
		
//		System.out.println(currSpot);
		// currSpot == 1 for text display area
		if(currSpot >= 0 || focusIsWith == this)
			cursorIsOver = this;
		else if(cursorIsOver == this)
			cursorIsOver = null;

		switch(event.getID()){
		case MouseEvent.MOUSE_PRESSED:
//			System.out.println("P " + focusIsWith);
			if(focusIsWith != this && currSpot > -1 && z > focusObjectZ()){
				downHotSpot = currSpot;
				offset = ox + 0.5f - valuePos; // normalised
				takeFocus();
//				System.out.println("PRESSED " + currSpot );
			}
			break;
		case MouseEvent.MOUSE_CLICKED:
//			System.out.println("C " + focusIsWith);
			if(focusIsWith == this ){
				valueTarget = ox + 0.5f;
				if(stickToTicks)
					valueTarget = findNearestTickValueTo(valueTarget);
				dragging = false;
				loseFocus(null);
//				System.out.println("CLICKED " + currSpot );
			}
			break;
		case MouseEvent.MOUSE_RELEASED:
//			System.out.println("R " + focusIsWith);
			if(focusIsWith == this && dragging){
				if(downHotSpot == THUMB_SPOT){
					valueTarget = (ox - offset) + 0.5f;
					if(valueTarget < 0){
						valueTarget = 0;
						offset = 0;
					}
					else if(valueTarget > 1){
						valueTarget = 1;
						offset = 0;
					}
					if(stickToTicks)
						valueTarget = findNearestTickValueTo(valueTarget);
				}
				loseFocus(null);				
//				System.out.println("RELEASED 1 " );
			}
			dragging = false;
			break;
		case MouseEvent.MOUSE_DRAGGED:
			if(focusIsWith == this){
//				System.out.println("DRAGGED " );
				dragging = true;
				if(downHotSpot == THUMB_SPOT){
					isValueChanging = true;
					valueTarget = (ox - offset) + 0.5f;
					if(valueTarget < 0){
						valueTarget = 0;
						offset = 0;
					}
					else if(valueTarget > 1){
						valueTarget = 1;
						offset = 0;
					}
				}
			}
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
		winApp.pushMatrix();
		// Move matrix to line up with top-left corner
		winApp.translate(-halfWidth, -halfHeight);
		// Draw buffer
		winApp.imageMode(PApplet.CORNER);
		if(alphaLevel < 255)
			winApp.tint(-1, alphaLevel);
		winApp.image(buffer, 0, 0);	
		winApp.popMatrix();
		// Value labels
		if(children != null){
			for(FAbstractControl c : children)
				c.draw();

		}
		winApp.popMatrix();

		winApp.popStyle();
	}

	protected void updateBuffer(){
		float px, py;
		LinkedList<TextLayoutInfo> lines;
		if(bufferInvalid) {
//			System.out.println("Update FSlider " + System.currentTimeMillis());
			if(isValueChanging){
				hotspots[0].adjust(new Float(width/2  + (valuePos - 0.5f) * trackLength));
//				System.out.println("Thumb at " + hotspots[0]);
			}
			
			TextLayoutInfo line;
			Graphics2D g2d = buffer.g2;
			bufferInvalid = false;
			buffer.beginDraw();
			buffer.rectMode(PApplet.CENTER);
			buffer.ellipseMode(PApplet.CENTER);
			// Back ground colour
			if(opaque == true)
				buffer.background(palette[6]);
			else
				buffer.background(buffer.color(255,0));

			// Draw track, thumb, ticks etc.
			buffer.pushMatrix();
			buffer.translate(width/2, height/2);
			// draw ticks
			if(showTicks){
				float delta = 1.0f / (nbrTicks - 1);
				for(int i = 0; i < nbrTicks; i++){
					int tickx = Math.round((i * delta - 0.5f)*trackLength);
					buffer.strokeWeight(2);
					buffer.stroke(palette[4]);
					buffer.line(tickx+1, -trackWidth, tickx+1, trackWidth);
					buffer.strokeWeight(1.2f);
					buffer.stroke(palette[1]);
					buffer.line(tickx, -trackWidth, tickx, trackWidth);
				}
			}
			g2d.setColor(jpalette[5]);
			g2d.fill(track);
			buffer.fill(palette[0]);
			buffer.noStroke();
			buffer.ellipse((valuePos - 0.5f) * trackLength, 0, trackWidth, trackWidth);
			g2d.setStroke(pen_2_0);
			g2d.setColor(jpalette[3]);
			g2d.draw(track);

			g2d.setColor(jpalette[2]);
			if(showLimits){
				if(limitsInvalid){
					ssStartLimit = new StyledString(g2d, getNumericDisplayString(startLimit));
					ssEndLimit = new StyledString(g2d, getNumericDisplayString(endLimit));
					limitsInvalid = false;				}
				line = ssStartLimit.getLines(g2d).getFirst();	
				px = -(trackLength + trackWidth)/2;
				py = trackWidth + 2 + line.layout.getAscent();
				line.layout.draw(g2d, px, py );
				line = ssEndLimit.getLines(g2d).getFirst();	
				px = (trackLength + trackWidth)/2 - line.layout.getVisibleAdvance();
				py = trackWidth + 2 + line.layout.getAscent();
				line.layout.draw(g2d, px, py );
			}

			if(showValue){
				ssValue = new StyledString(g2d, getNumericDisplayString(getValueF()));
				line = ssValue.getLines(g2d).getFirst();
				float advance = line.layout.getVisibleAdvance();
				px = (valuePos - 0.5f) * trackLength - advance /2;
				if(px < -trackDisplayLength/2)
					px = -trackDisplayLength/2;
				else if(px + advance > trackDisplayLength /2){
					px = trackDisplayLength/2 - advance;
				}
				py = -trackWidth - 2 - line.layout.getDescent();
				line.layout.draw(g2d, px, py );

			}
			buffer.popMatrix();
			buffer.endDraw();
		}

	}

}
