package guicomponents;

import processing.core.PApplet;
import processing.core.PFont;

public class GFont {
	protected static PApplet app;

	// Font details
	public PFont gpFont;
	public int gpFontSize;
	public int gpHeightOffset; // for text(String,int,int,int,int)

	public static GFont getFont(PApplet theApplet, String fontname, int fsize){
		app = theApplet;
		GFont gfont = new GFont();
		gfont.app = theApplet;
		gfont.gpFont = app.loadFont(fontname);
		gfont.gpFontSize = fsize;

		//panelTabHeight = gpFontSize + 4;
		return gfont;
	}
	
	public static GFont getFont(PApplet theApplet){
		return getFont(theApplet, "Geneva-11.vlw", 11);
	}

	public void setFont(String fontname, int fsize){
		gpFont = app.loadFont(fontname);
		gpFontSize = fsize;
		//panelTabHeight = gpFontSize + 4;
	}

}
