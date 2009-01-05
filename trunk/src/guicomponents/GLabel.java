package guicomponents;

import java.awt.Point;

import processing.core.PApplet;

public class GLabel extends GComponent {

	/**
	 * 
	 */
	public GLabel() {
		super();
	}

	/**
	 * 
	 * @param theApplet
	 * @param text
	 * @param x
	 * @param y
	 * @param width
	 */
	public GLabel(PApplet theApplet, String text, int x, int y, int width) {
		this(theApplet, text, x, y, width, GUI.LEFT);
	}

	/**
	 * 
	 * @param theApplet
	 * @param text
	 * @param x
	 * @param y
	 * @param width
	 * @param align
	 */
	public GLabel(PApplet theApplet, String text, int x, int y, int width, int align) {
		super(theApplet, x, y);
//		if(maxWidth < this.maxWidth)
//			this.maxWidth = maxWidth;
		this.width = width;
		this.height = localFont.gpFontSize + 2;
		textAlign = align;
		if(text != null)
			setText(text);
		app.registerDraw(this);
	}

	/**
	 * 
	 * @param theApplet
	 * @param text
	 * @param x
	 * @param y
	 * @param width
	 * @param align
	 * @param colorScheme
	 * @param fontScheme
	 */
	public GLabel(PApplet theApplet, String text, int x, int y, int width, int align,
			GColor colorScheme, GFont fontScheme) {
		super(theApplet, x, y, colorScheme, fontScheme);
//		if(maxWidth < this.maxWidth)
//			this.maxWidth = maxWidth;
		this.width = width;
		this.height = localFont.gpFontSize + 2;
		textAlign = align;
		if(text != null)
			setText(text);
		app.registerDraw(this);
	}
	
	/**
	 * 
	 * @param theApplet
	 * @param text
	 * @param x
	 * @param y
	 * @param width
	 * @param colorScheme
	 * @param fontScheme
	 */
	public GLabel(PApplet theApplet, String text, int x, int y, int width,
			GColor colorScheme, GFont fontScheme){
		this(theApplet, text, x, y, width, GUI.LEFT, colorScheme, fontScheme);
	}
	
	public void draw(){
		if(visible){
			float textX = 2 * border;
			switch(textAlign){
			case GUI.RIGHT:
				textX += width - textWidth - 4 * border;
				break;
			case GUI.CENTER:
				textX += (width - textWidth - 4 * border)/2;
				break;
			}
			Point pos = new Point(0,0);
			calcAbsPosition(pos);
			app.noStroke();
			app.fill(localColor.panelTabFont);
			app.textFont(localFont.gpFont, localFont.gpFontSize);
//			app.text(text, pos.x + 4, pos.y + localGScheme.gpFontSize);
			app.text(text, pos.x + (int)textX, pos.y + localFont.gpFontSize / 8, 
					width - 4 * border, height);
			if(border != 0){
				app.strokeWeight(1);
				app.stroke(255);
				app.noFill();
				app.rect(pos.x,pos.y, width, height);
			}
		}
	}

}
