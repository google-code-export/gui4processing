package guicomponents;

import processing.core.*;

public class GScheme {

	protected static PApplet app;

	// Colors
	public int panelBG;
	public int panelTabBG;
	public int panelTabFont;


	// Font details
	public PFont gpFont;
	public int gpFontSize;

	public static GScheme getScheme(PApplet theApplet, int colScheme, int fontScheme){
		app = theApplet;
		GScheme scheme = new GScheme();
		switch(colScheme){
		case 1:
			scheme.redColorScheme();
			break;
		case 2:
			scheme.greenColorScheme();
			break;
		case 3:
			scheme.blueColorScheme();
			break;
		default:
			scheme.defaultColorScheme();				
		}
		switch(fontScheme){
		case 1:
			scheme.makeFontScheme("Geneva-11.vlw",11);
			break;
		case 2:
			scheme.makeFontScheme("FrutigerLight-12.vlw",14);
			break;
		case 3:
			scheme.makeFontScheme("FrutigerLight-13.vlw",15);
			break;
		case 4:
			scheme.makeFontScheme("Miriam-48.vlw",16);
			break;
		default:
			scheme.makeFontScheme("Miriam-48.vlw",16);
			break;
		}
		return scheme;
	}

	private void blueColorScheme() {
		panelBG = app.color(50,50,240,128);
		panelTabBG = app.color(50,50,240,160);
		panelTabFont = app.color(255);
	}

	private void greenColorScheme() {
		panelBG = app.color(50,240,50,128);
		panelTabBG = app.color(50,240,50,160);
		panelTabFont = app.color(255);
	}

	private void redColorScheme() {
		panelBG = app.color(240,50,50,128);
		panelTabBG = app.color(240,50,50,160);
		panelTabFont = app.color(255);
	}

	private void defaultColorScheme(){
		panelBG = app.color(50,50,50,128);
		panelTabBG = app.color(240,240,240,160);
		panelTabFont = app.color(0);
	}

	private void makeFontScheme(String fontname, int fsize){
		gpFont = app.loadFont(fontname);
		app.textFont(gpFont, fsize);
		gpFontSize = fsize;
	}

	public void setFont(String fontname, int fsize){
		if(app != null){
			gpFont = app.loadFont(fontname);
			app.textFont(gpFont, fsize);
			gpFontSize = fsize;
		}
	}

} // end of class
