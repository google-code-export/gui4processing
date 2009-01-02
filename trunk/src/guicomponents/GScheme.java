package guicomponents;

import processing.core.*;

public class GScheme implements GConstants {

	protected static PApplet app;

	// Panels / Labels
	public int panelBG;
	public int panelTabBG;
	public int panelTabFont;
	public int panelTabHeight;

	public int sliderBG;
	public int sliderThumb;
	public int sliderStroke;
	
	// Font details
	public PFont gpFont;
	public int gpFontSize;
	public int gpHeightOffset; // for text(String,int,int,int,int)
	/**
	 * Get a colour and font scheme to be used by a component
	 * 
	 * @param theApplet
	 * @param colScheme see GConstants for appropriate values
	 * @param fontScheme see GConstants for appropriate values
	 * @return a GScheme object for the color and font scheme
	 */
	public static GScheme getScheme(PApplet theApplet, int colScheme, int fontScheme){
		app = theApplet;
		GScheme scheme = new GScheme();
		switch(colScheme){
		case RED:
			scheme.redColorScheme();
			break;
		case GREEN:
			scheme.greenColorScheme();
			break;
		case BLUE:
			scheme.blueColorScheme();
			break;
		case YELLOW:
			scheme.yellowColorScheme();
			break;
		case CYAN:
			scheme.cyanColorScheme();
			break;
		case PURPLE:
			scheme.purpleColorScheme();
			break;
		case GREY:
		default:
			scheme.greyColorScheme();				
		}
		
		switch(fontScheme){
		case FONT16:
			scheme.makeFontScheme("Miriam-48.vlw",16);
			break;
		case FONT11:
		default:
			scheme.makeFontScheme("Geneva-11.vlw",11);
			break;
		}
		return scheme;
	}

	private void blueColorScheme() {
		panelBG = app.color(50,50,255,96);
		panelTabBG = app.color(50,50,255,160);
		panelTabFont = app.color(255);
		sliderBG = app.color(255,128);
		sliderThumb = app.color(0,0,255,255);
		sliderStroke = app.color(128,128,255,255);
	}

	private void purpleColorScheme() {
		panelBG = app.color(255,50,255,96);
		panelTabBG = app.color(255,50,255,160);
		panelTabFont = app.color(255);
		sliderBG = app.color(255,192);
		sliderThumb = app.color(255,0,255,255);
		sliderStroke = app.color(255,128,255,255);
	}

	private void greenColorScheme() {
		panelBG = app.color(50,255,50,96);
		panelTabBG = app.color(50,255,50,160);
		panelTabFont = app.color(0);
		sliderBG = app.color(255,128);
		sliderThumb = app.color(0,255,0,255);
		sliderStroke = app.color(128,255,128,255);
	}

	private void cyanColorScheme() {
		panelBG = app.color(50,255,255,96);
		panelTabBG = app.color(50,255,255,160);
		panelTabFont = app.color(0);
		sliderBG = app.color(255,128);
		sliderThumb = app.color(0,255,255,255);
		sliderStroke = app.color(128,255,255,255);
	}

	private void yellowColorScheme() {
		panelBG = app.color(255,255,50,96);
		panelTabBG = app.color(255,255,50,192);
		panelTabFont = app.color(0);
		sliderBG = app.color(255,128);
		sliderThumb = app.color(255,255,0,255);
		sliderStroke = app.color(255,255,128,255);
	}

	private void redColorScheme() {
		panelBG = app.color(255,50,50,96);
		panelTabBG = app.color(255,50,50,160);
		panelTabFont = app.color(255);
		sliderBG = app.color(255,192);
		sliderThumb = app.color(255,0,0,255);
		sliderStroke = app.color(255,128,128,255);
	}

	private void greyColorScheme(){
		panelBG = app.color(50,50,50,96);
		panelTabBG = app.color(240,240,240,160);
		panelTabFont = app.color(0);
		sliderBG = app.color(255,192);
		sliderThumb = app.color(32,255);
		sliderStroke = app.color(64,255);
	}

	private void makeFontScheme(String fontname, int fsize){
		gpFont = app.loadFont(fontname);
		gpFontSize = fsize;
		
		panelTabHeight = gpFontSize + 4;
	}

	public void setFont(String fontname, int fsize){
		if(app != null){
			gpFont = app.loadFont(fontname);
			gpFontSize = fsize;
			panelTabHeight = gpFontSize + 4;
		}
	}

} // end of class
