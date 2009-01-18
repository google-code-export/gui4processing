/*
  Part of the GUI for Processing library 
  	http://gui4processing.lagers.org.uk
	http://code.google.com/p/gui4processing/
	
  Copyright (c) 2008-09 Peter Lager

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
 */

package guicomponents;

import processing.core.PApplet;
import processing.core.PConstants;

public class GColor implements GUI {
	protected static PApplet app;

	// Panels / Labels
	public int panel;
	public int panelTab;
	public int panelTabFont;
	public int panelTabHeight;

	public int buttonOff, buttonOver, buttonDown;
	public int buttonFont;
	
	public int sliderBG;
	public int sliderThumb;
	public int sliderStroke;

	public static GColor getColor(PApplet theApplet){
		return getColor(theApplet, -1);
	}
	
	public static GColor getColor(PApplet theApplet, int colScheme){
		app = theApplet;
		GColor scheme = new GColor();
		switch(colScheme){
		case RED:
			scheme.redColorScheme();
			break;
		case GREEN:
			scheme.greenColorScheme();
			break;
		case BLUE:
			scheme.makeColorScheme(0x6060ff, 0xc0c0ff, 96, 240, 0x0000ff, 0x000000);
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
			scheme.greyColorScheme();				
			break;
		default:
			scheme.makeColorScheme(0x6060ff, 0xc0c0ff, 96, 240, 0x0000ff, 0x000000);				
		}		
		return scheme;
	}

	public void setColor(int colScheme){
		switch(colScheme){
		case RED:
			redColorScheme();
			break;
		case GREEN:
			greenColorScheme();
			break;
		case BLUE:
			makeColorScheme(0x6060ff, 0xc0c0ff, 96, 255, 0x0000ff, 0x000000);
			break;
		case YELLOW:
			yellowColorScheme();
			break;
		case CYAN:
			cyanColorScheme();
			break;
		case PURPLE:
			purpleColorScheme();
			break;
		case GREY:
			greyColorScheme();				
			break;
		default:
			makeColorScheme(0x6060ff, 0xc0c0ff, 96, 240, 0x0000ff, 0x000000);				
		}		
	}
	
	/**
	 * Create a color scheme
	 * @param pdark the darkest color
	 * @param plight the lightest color
	 * @param alphaLow 0-255 low value i.e. most transparency
	 * @param alphaHigh 0-255 high value i.e. most opaque
	 * @param pfont panel font color
	 * @param font font color for rest
	 */
	private void makeColorScheme(int pdark, int plight, int alphaLow, int alphaHigh, int pfont, int font)
	{
		int colMask = 0x00ffffff;
		int alphaMask = 0xff000000;
		
//		int pdarkAlpha = (pdark & alphaMask) >> 24;
//		int pLightAlpha =(plight & alphaMask) >> 24;
		
		// Make opaque
		pdark |= alphaMask;
		plight |= alphaMask;
		
		alphaLow <<= 24;
		alphaHigh <<= 24;
		
		// Panel colours
		panel = (plight & colMask) | alphaLow;
		panelTab = (pdark & colMask) | alphaLow;
		panelTabFont = (pfont & colMask) | alphaMask;
		sliderBG = alphaHigh | 0x00ffffff;
		sliderThumb = plight;
		sliderStroke = pdark;
		buttonOff = PApplet.lerpColor(pdark, plight, 50, PConstants.HSB);
		buttonOff = (buttonOff & colMask) | alphaHigh;
		buttonOver = PApplet.lerpColor(pdark, plight, 80, PConstants.HSB);
		buttonOver = (buttonOver & colMask) | alphaHigh;
		buttonDown = PApplet.lerpColor(pdark, plight, 20, PConstants.HSB);
		buttonDown = (buttonDown & colMask) | alphaHigh;
		buttonFont = (font & colMask) | alphaMask;
	}
	
	private void blueColorScheme() {
		panel = app.color(50,50,255,96);
		panelTab = app.color(50,50,255,160);
		panelTabFont = app.color(255);
		sliderBG = app.color(255,128);
		sliderThumb = app.color(0,0,255,255);
		sliderStroke = app.color(128,128,255,255);
		buttonOff = app.color(128, 128, 255);
		buttonOver = app.color(192, 192, 255);
		buttonDown = app.color(96, 96, 255);
		buttonFont = app.color(0);
	}

	private void purpleColorScheme() {
		panel = app.color(255,50,255,96);
		panelTab = app.color(255,50,255,160);
		panelTabFont = app.color(255);
		sliderBG = app.color(255,192);
		sliderThumb = app.color(255,0,255,255);
		sliderStroke = app.color(255,128,255,255);
	}

	private void greenColorScheme() {
		panel = app.color(50,255,50,96);
		panelTab = app.color(50,255,50,160);
		panelTabFont = app.color(0);
		sliderBG = app.color(255,128);
		sliderThumb = app.color(0,255,0,255);
		sliderStroke = app.color(128,255,128,255);
	}

	private void cyanColorScheme() {
		panel = app.color(50,255,255,96);
		panelTab = app.color(50,255,255,160);
		panelTabFont = app.color(0);
		sliderBG = app.color(255,128);
		sliderThumb = app.color(0,255,255,255);
		sliderStroke = app.color(128,255,255,255);
	}

	private void yellowColorScheme() {
		panel = app.color(255,255,50,96);
		panelTab = app.color(255,255,50,192);
		panelTabFont = app.color(0);
		sliderBG = app.color(255,128);
		sliderThumb = app.color(255,255,0,255);
		sliderStroke = app.color(255,255,128,255);
	}

	private void redColorScheme() {
		panel = app.color(255,50,50,96);
		panelTab = app.color(255,50,50,160);
		panelTabFont = app.color(255);
		sliderBG = app.color(255,192);
		sliderThumb = app.color(255,0,0,255);
		sliderStroke = app.color(255,128,128,255);
	}

	private void greyColorScheme(){
		panel = app.color(50,50,50,96);
		panelTab = app.color(240,240,240,160);
		panelTabFont = app.color(0);
		sliderBG = app.color(255,192);
		sliderThumb = app.color(32,255);
		sliderStroke = app.color(64,255);
	}


}
