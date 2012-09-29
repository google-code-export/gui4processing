package guicomponents;

import guicomponents.HotSpot.HSalpha;
import guicomponents.HotSpot.HSrect;
import guicomponents.StyledString.TextLayoutInfo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;

import processing.core.PApplet;
import processing.core.PGraphicsJava2D;
import processing.core.PImage;

public class FCustomSlider extends FValueControl {

	static protected float TINSET = 4;
	static protected int THUMB_SPOT = 1;
	static protected int TRACK_SPOT = 2;

	protected PImage leftEnd;
	protected PImage thumb;
	protected PImage thumb_mouseover;
	protected PImage rightEnd;
	protected PImage centre;

	protected float trackWidth, trackLength, trackDisplayLength;

	protected int downHotSpot = -1;
	// Mouse over status
	protected int status = -1;

	public FCustomSlider(PApplet theApplet, float p0, float p1, float p2, float p3, String style) {
		super(theApplet, p0, p1, p2, p3);
		loadSkin(style);
		float maxEndLength = Math.max(leftEnd.width, rightEnd.width);
		trackLength = Math.round(width - 2 * maxEndLength - TINSET);
		trackDisplayLength = trackLength + 2 * Math.min(leftEnd.width, rightEnd.width);
		trackWidth = centre.height;
		extendCentreImage();

		buffer = (PGraphicsJava2D) winApp.createGraphics((int)width, (int)height, PApplet.JAVA2D);
		buffer.g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		buffer.g2.setFont(F4P.numericLabelFont);
		buffer.imageMode(PApplet.CENTER);
		hotspots = new HotSpot[]{
				new HSalpha(THUMB_SPOT, width/2 + (valuePos - 0.5f) * trackLength, height/2, thumb, PApplet.CENTER),  // thumb
				new HSrect(TRACK_SPOT, (width-trackLength)/2, (height-trackWidth)/2, trackLength, trackWidth),		// track
		};
		opaque = false;
		z = Z_SLIPPY;

		epsilon = 0.98f / trackLength;
		ssStartLimit = new StyledString(buffer.g2, "0.00");
		ssEndLimit = new StyledString(buffer.g2, "1.00");
		ssValue = new StyledString(buffer.g2, "0.50");

		// Now register control with applet
		createEventHandler(winApp, "handleSliderEvents", new Class[]{ FValueControl.class, boolean.class });
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
		
//		System.out.println("Custom slider      " + currSpot + "   thumb at " + hotspots[0].x);
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
				status = OVER_CONTROL;
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
				status = OFF_CONTROL;
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
				status = OFF_CONTROL;
				bufferInvalid = true;
				loseFocus(null);				
//				System.out.println("RELEASED 1 " );
			}
			dragging = false;
			break;
		case MouseEvent.MOUSE_DRAGGED:
			if(focusIsWith == this){
//				System.out.println("DRAGGED " + downHotSpot );
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
		case MouseEvent.MOUSE_MOVED:
			int currStatus = status;
			// If dragged state will stay as PRESSED
			if(currSpot == THUMB_SPOT)
				status = OVER_CONTROL;
			else
				status = OFF_CONTROL;
			if(currStatus != status)
				bufferInvalid = true;
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
		winApp.popMatrix();

		winApp.popStyle();
	}

	
	
	
	protected void updateBuffer(){
		float px, py;
		if(bufferInvalid) {
//			System.out.println("Update FSlider " + System.currentTimeMillis() + "    " + isValueChanging);
			if(isValueChanging)
				hotspots[0].x = (width/2  + (valuePos - 0.5f) * trackLength);		
//			hotspots[0].adjust(new Float(width/2  + (valuePos - 0.5f) * trackLength));		
			TextLayoutInfo line;
			Graphics2D g2d = buffer.g2;
			bufferInvalid = false;
			buffer.beginDraw();
			// Back ground colour
			buffer.background(buffer.color(255,0));

			// Draw track, thumb, ticks etc.
			buffer.pushMatrix();
			buffer.translate(width/2, height/2);
			// draw ticks
			if(showTicks){
				float delta = 1.0f / (nbrTicks - 1);
				for(int i = 0; i < nbrTicks; i++){
					int tickx = Math.round((i * delta - 0.5f)*trackLength);
					buffer.strokeWeight(1);
					buffer.stroke(255);
					buffer.line(tickx+1, -trackWidth, tickx+1, trackWidth);
					buffer.strokeWeight(1.5f);
					buffer.stroke(0);
					buffer.line(tickx, -trackWidth, tickx, trackWidth);
				}
			}
			buffer.image(centre,0,0);
			buffer.image(leftEnd, -(trackLength + leftEnd.width)/2, 0);
			buffer.image(rightEnd, (trackLength + rightEnd.width)/2, 0);
			if(status == OVER_CONTROL)
				buffer.image(thumb_mouseover,(valuePos - 0.5f) * trackLength, 0);
			else
				buffer.image(thumb,(valuePos - 0.5f) * trackLength, 0);
			
			g2d.setColor(Color.black);
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
				// Make sure it is above thumb and tick marks
				py = -Math.max(thumb.height/2, trackWidth) - line.layout.getDescent()-2;
				line.layout.draw(g2d, px, py );
			}
			buffer.popMatrix();
			buffer.endDraw();
		}
	}
	
	protected void extendCentreImage(){
		PImage p = centre;
		centre = new PImage((int)trackLength, p.height, ARGB);

		p.loadPixels();
		
		int index = 0;
		for(int h = 0; h < centre.height; h++){
			int v = p.pixels[h];
			for(int w = 0; w < centre.width; w++)
				centre.pixels[index++] = v; 
		}
		centre.updatePixels();	
	}
	
	private void loadSkin(String style){
		leftEnd = winApp.loadImage(style + "/end_left.png");
		rightEnd = winApp.loadImage(style + "/end_right.png");
		thumb = winApp.loadImage(style +"/handle.png");
		thumb_mouseover = winApp.loadImage(style +"/handle_mouseover.png");
		//	will be strcehed before use
		centre = winApp.loadImage(style + "/centre.png");
		boolean err = false;

		StringBuilder errmess = new StringBuilder();
		if(leftEnd == null) errmess.append("end_left.png\n");
		if(rightEnd == null) errmess.append("end_right.png\n");
		if(thumb== null) errmess.append("handle.png\n");
		if(thumb_mouseover == null) errmess.append("handle_mouseover.png\n");
		
		// See if we have problems with the skin files
		if(errmess.length() > 0){
			err = true;
			System.out.println("The following files could not be found for the skin " + style + ": \n" + errmess.toString()
					+ "\nCheck that these files are correctly placed in the data directory under a folder with"
					+ " the same name as the skin used.\n");
		}
		else {
			if(centre != null && centre.width != 1){
				err = true;
				System.out.println("The supplied centre image for this skin is not of width 1px.");
			}
			if(centre.height != leftEnd.height || leftEnd.height != rightEnd.height){
				err = true;
				System.out.println("The image components of the slider are not all the same height.");
			}
		}
		if(err){
			System.out.println("Reverting to default 'grey_blue' style");
			loadSkin("grey_blue");
		}
		return;
	}
}
