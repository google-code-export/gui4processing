package guicomponents;

import java.awt.Point;

import processing.core.PApplet;

public class GLabel extends GComponent {

	/**
	 * 
	 */
//	public GLabel() {
//		super();
//	}
	
	/**
	 * 
	 * @param theApplet
	 * @param text
	 * @param x
	 * @param y
	 * @param width
	 */
	public GLabel(PApplet theApplet, String text, int x, int y, int width) {
		super(theApplet, x, y);
		label2(text, width, GUI.LEFT);
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
		label2(text, width, align);
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
		label2(text, width, align);
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
		super(theApplet, x, y, colorScheme, fontScheme);
		label2(text, width, GUI.LEFT);
	}
	
	private void label2(String text, int width, int align){
		this.width = width;
		this.height = localFont.gpFontSize + 2;
		if(text != null)
			setText(text);
		app.registerDraw(this);		
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
			app.text(getText(), pos.x + (int)textX, pos.y + localFont.gpFontSize / 8, 
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
