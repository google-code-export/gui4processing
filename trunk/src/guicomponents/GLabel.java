package guicomponents;

import java.awt.Point;

import processing.core.PApplet;

public class GLabel extends GComponent {

	/**
	 * 
	 */
	public GLabel() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param theApplet
	 * @param text
	 * @param x
	 * @param y
	 * @param maxWidth
	 */
	public GLabel(PApplet theApplet, String text, int x, int y, int maxWidth) {
		super(theApplet, x, y);
		// TODO Auto-generated constructor stub
		if(maxWidth < this.maxWidth)
			this.maxWidth = maxWidth;
		if(text != null)
			this.text = text;
		app.registerDraw(this);
	}
	
	/**
	 * 
	 * @param theApplet
	 * @param text
	 * @param x
	 * @param y
	 * @param maxWidth
	 * @param colorScheme
	 * @param fontScheme
	 */
	public GLabel(PApplet theApplet, String text, int x, int y, int maxWidth, int colorScheme, int fontScheme) {
		super(theApplet, x, y, colorScheme, fontScheme);
		// TODO Auto-generated constructor stub
		if(maxWidth < this.maxWidth)
			this.maxWidth = maxWidth;
		if(text != null)
			this.text = text;
		app.registerDraw(this);
	}
	
	public void draw(){
		//System.out.println("DRAW "+this + "  "+((3 * localGScheme.gpFontSize) / 4) );
		if(visible){
			Point pos = new Point(0,0);
			calcAbsPosition(pos);
			app.noStroke();
			app.fill(localGScheme.panelTabFont);
			app.textFont(localGScheme.gpFont, localGScheme.gpFontSize);
			app.text(text, pos.x + 4, pos.y + (3 * localGScheme.gpFontSize) / 4);
		}
	}
	
}
