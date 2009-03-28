/*
  Part of the GUI for Processing library 
  	http://gui4processing.lagers.org.uk
	http://code.google.com/p/gui-for-processing/
	
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
import processing.core.PImage;

/**
 * Stores all the colour information for the GUI components into a scheme.
 * 
 * This class has scope for further work to provide more customisable
 * schemes.
 * 
 * @author Peter Lager
 *
 */
public class GCScheme  {

	// Color scheme constants
	public static final int BLUE_SCHEME 	= 1;
	public static final int GREEN_SCHEME 	= 2;
	public static final int RED_SCHEME 		= 3;
	public static final int PURPLE_SCHEME	= 4;
	public static final int YELLOW_SCHEME	= 5;
	public static final int CYAN_SCHEME 	= 6;
	public static final int GREY_SCHEME 	= 7;
	public static final int USER_SCHEME_01	= 8;
	public static final int USER_SCHEME_02	= 9;
	public static final int USER_SCHEME_03	= 10;
	public static final int USER_SCHEME_04	= 11;
	public static final int USER_SCHEME_05	= 12;
	public static final int USER_SCHEME_06	= 13;
	public static final int USER_SCHEME_07	= 14;
	public static final int USER_SCHEME_08	= 15;
	public static final int USER_SCHEME_09	= 16;
	public static final int USER_SCHEME_10	= 17;
	public static final int USER_SCHEME_11	= 18;
	public static final int USER_SCHEME_12	= 19;
	
	protected static PApplet app;

	protected static PImage image = null;
	
	
//	public int panelTabHeight;

	
	// Mask to get RGB
	public static final int COL_MASK 		= 0x00ffffff;
	// Mask to get alpha
	public static final int ALPHA_MASK 		= 0xff000000;

	private void getSchemeImage(){
		app.loadImage("schemes.png");		
	}

	public static int setAlpha(int col, int alpha){
		alpha = (alpha & 0xff) << 24;
		col = (col & COL_MASK) | alpha;
		return col;
	}

	/**
	 * Set the default color scheme
	 * 
	 * @param theApplet
	 * @return
	 */
	public static GCScheme getColor(PApplet theApplet){
		return getColor(theApplet, 1);
	}
	
	/**
	 * Set the color scheme to one of the preset schemes
	 * BLUE / GREEN / RED / YELLOW / CYAN / PURPLE / GREY
	 * 
	 * @param theApplet
	 * @param colScheme
	 * @return
	 */
	public static GCScheme getColor(PApplet theApplet, int colScheme){
		app = theApplet;
		if(image == null)
			app.loadImage("schemes.png");
		GCScheme scheme = new GCScheme();
		scheme.pnlFont = findColor(colScheme, 0, 0);
		scheme.pnlTabBack = findColor(colScheme, 0, 1);
		scheme.pnlBack = findColor(colScheme, 0, 2);
		scheme.pnlBorder = findColor(colScheme, 0, 3);
		
		scheme.btnFont = findColor(colScheme, 1, 0);
		scheme.btnOff = findColor(colScheme, 1, 1);
		scheme.btnOver = findColor(colScheme, 1, 2);
		scheme.btnDown = findColor(colScheme, 1, 3);
		scheme.btnBorder = findColor(colScheme, 1, 4);
		
		
		return scheme;
	}

	private static int findColor(int scheme, int component, int type){
		return image.get( ((component * 5) + type)* 10 + 5, scheme * 10 + 5);
	}

	// Panels
	public int pnlFont, pnlTabBack, pnlBack, pnlBorder;
	// Buttons
	public int btnFont, btnOff, btnOver, btnDown, btnBorder;
	// Sliders
	public int sdrBackground, sdrThumb, sdrBorder;
	// TextFields
	public int txfFont, txfBack, txfSelFont, txfSelBack, txfBorder;
	// Label
	public int lblFont, lblBack, lblBorder;
	// Option
	public int optFont, optBack, optBorder;
	// Checkbox
	public int cbxFont, cbxBack, cbxBorder;
	
	
	
	
	
	
	private static void calcColors(GCScheme scheme, int colScheme){
		switch(colScheme){
		case RED_SCHEME:
			scheme.makeColorScheme(0xff6060, 0xffc0c0, 96, 255, 0x700000, 0x000000);
			break;
		case GREEN_SCHEME:
			scheme.makeColorScheme(0x60ff60, 0xc0ffc0, 96, 255, 0x00ff00, 0x000000);
			break;
		case BLUE_SCHEME:
			scheme.makeColorScheme(0x6060ff, 0xc0c0ff, 96, 240, 0x0000ff, 0x000000);
			break;
		case YELLOW_SCHEME:
			scheme.makeColorScheme(0xffff60, 0xffffc0, 96, 240, 0x000000, 0x000000);				
			break;
		case CYAN_SCHEME:
			scheme.makeColorScheme(0x60ffff, 0xc0ffff, 96, 240, 0x000000, 0x000000);				
			break;
		case PURPLE_SCHEME:
			scheme.makeColorScheme(0xff60ff, 0xffc0ff, 96, 240, 0x000000, 0x000000);	
			break;
		case GREY_SCHEME:
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
		pnlBack = (plight & colMask) | alphaLow;
		pnlTabBack = (pdark & colMask) | alphaLow;
		pnlFont = (pfont & colMask) | alphaMask;
		// Slider colours
		sdrBackground = alphaHigh | 0x00ffffff;
		sdrThumb = plight;
		sdrBorder = pdark;
		// button colours
		btnOff = PApplet.lerpColor(pdark, plight, 50, PConstants.HSB);
		btnOff = (btnOff & colMask) | alphaHigh;
		btnOver = PApplet.lerpColor(pdark, plight, 80, PConstants.HSB);
		btnOver = (btnOver & colMask) | alphaHigh;
		btnDown = PApplet.lerpColor(pdark, plight, 20, PConstants.HSB);
		btnDown = (btnDown & colMask) | alphaHigh;
		btnFont = (font & colMask) | alphaMask;
		// text field colours
		txfBack = app.color(255);
		txfFont = app.color(pnlFont);
		txfSelBack = (plight & colMask) | alphaMask;
		txfBorder = app.color(pnlFont);
	
	}

} // end of class
