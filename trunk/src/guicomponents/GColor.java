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

public class GColor implements GUI {
	protected static PApplet app;

	// Panels / Labels
	public int panelBG;
	public int panelTabBG;
	public int panelTabFont;
	public int panelTabHeight;

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
			scheme.greyColorScheme();				
			break;
		default:
			scheme.blueColorScheme();				
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
			blueColorScheme();
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
			blueColorScheme();				
		}		
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


}
