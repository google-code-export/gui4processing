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
	public int pnlBackground;
	public int pnlTabBackground;
	public int pnlForeground;
	public int panelTabHeight;

	public int buttonOff, buttonOver, buttonDown;
	public int buttonFont;
	
	public int sdrBackground;
	public int sdrThumb;
	public int sdrBorder;
	
	public int txfBackground;
	public int txfForeground;
	public int txfSelection;
	public int txfBorder;
	
	/**
	 * Set the default color scheme
	 * 
	 * @param theApplet
	 * @return
	 */
	public static GColor getColor(PApplet theApplet){
		return getColor(theApplet, -1);
	}
	
	/**
	 * Set the color scheme to one of the preset schemes
	 * BLUE / GREEN / RED / YELLOW / CYAN / PURPLE / GREY
	 * 
	 * @param theApplet
	 * @param colScheme
	 * @return
	 */
	public static GColor getColor(PApplet theApplet, int colScheme){
		app = theApplet;
		GColor scheme = new GColor();
		calcColors(scheme, colScheme);
		return scheme;
	}

	private static void calcColors(GColor scheme, int colScheme){
		switch(colScheme){
		case RED:
			scheme.makeColorScheme(0xff6060, 0xffc0c0, 96, 255, 0x700000, 0x000000);
			break;
		case GREEN:
			scheme.makeColorScheme(0x60ff60, 0xc0ffc0, 96, 255, 0x00ff00, 0x000000);
			break;
		case BLUE:
			scheme.makeColorScheme(0x6060ff, 0xc0c0ff, 96, 240, 0x0000ff, 0x000000);
			break;
		case YELLOW:
			scheme.makeColorScheme(0xffff60, 0xffffc0, 96, 240, 0x000000, 0x000000);				
			break;
		case CYAN:
			scheme.makeColorScheme(0x60ffff, 0xc0ffff, 96, 240, 0x000000, 0x000000);				
			break;
		case PURPLE:
			scheme.makeColorScheme(0xff60ff, 0xffc0ff, 96, 240, 0x000000, 0x000000);	
			break;
		case GREY:
			scheme.makeColorScheme(0x606060, 0xc0c0c0, 96, 240, 0xffffff, 0x000000);				
			break;
		default:
			scheme.makeColorScheme(0x6060ff, 0xc0c0ff, 96, 240, 0x0000ff, 0x000000);				
		}		
	}

	public void setColor(int colScheme){
		calcColors(this, colScheme);
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
		// Mask to get RGB
		int colMask = 0x00ffffff;
		// Mask to get A (alpha)
		int alphaMask = 0xff000000;
		
		// Make opaque then we can adjust alpha as and when
		pdark |= alphaMask;
		plight |= alphaMask;
		
		alphaLow <<= 24;
		alphaHigh <<= 24;
		
		// Panel colours
		pnlBackground = (plight & colMask) | alphaLow;
		pnlTabBackground = (pdark & colMask) | alphaLow;
		pnlForeground = (pfont & colMask) | alphaMask;
		// Slider colours
		sdrBackground = alphaHigh | 0x00ffffff;
		sdrThumb = plight;
		sdrBorder = pdark;
		// button colours
		buttonOff = PApplet.lerpColor(pdark, plight, 50, PConstants.HSB);
		buttonOff = (buttonOff & colMask) | alphaHigh;
		buttonOver = PApplet.lerpColor(pdark, plight, 80, PConstants.HSB);
		buttonOver = (buttonOver & colMask) | alphaHigh;
		buttonDown = PApplet.lerpColor(pdark, plight, 20, PConstants.HSB);
		buttonDown = (buttonDown & colMask) | alphaHigh;
		buttonFont = (font & colMask) | alphaMask;
		// text field colours
		txfBackground = app.color(255);
		txfForeground = app.color(pnlForeground);
		txfSelection = (plight & colMask) | alphaMask;
		txfBorder = app.color(pnlForeground);
	
	}

} // end of class
