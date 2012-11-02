package guicomponents;

import processing.core.PApplet;
import processing.core.PGraphics;

public class FSketchPad extends FAbstractControl {

	protected boolean scaleGraphic = false;
	
	public FSketchPad(PApplet theApplet, float p0, float p1, float p2, float p3) {
		super(theApplet, p0, p1, p2, p3);
		cursorOver = F4P.mouseOff; // does not change
		registeredMethods = DRAW_METHOD;
		F4P.addControl(this);
	}
	
	public void setGraphic(PGraphics pg){
		if(pg == null)
			return;
		pad = pg;
		scaleGraphic = (int)width == pg.width && (int)height == pg.height;
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
		winApp.rectMode(PApplet.CORNER);
		winApp.imageMode(PApplet.CORNER);
		if(alphaLevel < 255)
			winApp.tint(-1, alphaLevel);
		if(pad != null){
			try {
				if(scaleGraphic)
					winApp.image(pad, 0, 0, width, height);
				else
					winApp.image(pad, 0, 0, width, height);				
			}
			catch(Exception excp){ /* Do nothing */	}
		}
		winApp.noFill();
		winApp.stroke(palette[3]);
		winApp.strokeWeight(1.5f);
		winApp.rect(0, 0, width, height);
		winApp.popMatrix();		
	}

}
