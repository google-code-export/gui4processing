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
	public static final int BLUE_SCHEME 	= 0;
	public static final int GREEN_SCHEME 	= 1;
	public static final int RED_SCHEME 		= 2;
	public static final int PURPLE_SCHEME	= 3;
	public static final int YELLOW_SCHEME	= 4;
	public static final int CYAN_SCHEME 	= 5;
	public static final int GREY_SCHEME 	= 6;
	public static final int USER_SCHEME_01	= 7;
	public static final int USER_SCHEME_02	= 8;
	public static final int USER_SCHEME_03	= 9;
	public static final int USER_SCHEME_04	= 10;
	public static final int USER_SCHEME_05	= 11;
	public static final int USER_SCHEME_06	= 12;
	public static final int USER_SCHEME_07	= 13;
	public static final int USER_SCHEME_08	= 14;
	public static final int USER_SCHEME_09	= 15;
	public static final int USER_SCHEME_10	= 16;
	public static final int USER_SCHEME_11	= 17;
	public static final int USER_SCHEME_12	= 18;
	
	protected static PApplet app;

	protected static PImage image = null;
	
	
//	public int panelTabHeight;

	
	// Mask to get RGB
	public static final int COL_MASK 		= 0x00ffffff;
	// Mask to get alpha
	public static final int ALPHA_MASK 		= 0xff000000;

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
		return getColor(theApplet, 0);
	}
	
	/**
	 * Set the color scheme to one of the preset schemes
	 * BLUE / GREEN / RED / YELLOW / CYAN / PURPLE / GREY
	 * 
	 * @param theApplet
	 * @param csn
	 * @return
	 */
	public static GCScheme getColor(PApplet theApplet, int csn){
		app = theApplet;
		if(image == null){
			image = app.loadImage("user_col_schema.png");
			if(image == null){
				image = app.loadImage("default_col_schema.png");
				System.out.println("Using default colour schema");
			}
			else
				System.out.println("Using default colour schema");				
		}
		System.out.println("Scheme no " + csn);
		GCScheme scheme = new GCScheme(csn);
		populateScheme(scheme, csn);
		
		return scheme;
	}

	public static void populateScheme(GCScheme s, int schemeNo){
		s.pnlFont = image.get(0, schemeNo) | 0xff000000;
		s.pnlTabBack = image.get(1, schemeNo) | 0xff000000;
		s.pnlBack = image.get(2, schemeNo) | 0xff000000;
		s.pnlBorder = image.get(3, schemeNo) | 0xff000000;
		
		s.btnFont = image.get(5, schemeNo) | 0xff000000;
		s.btnOff = image.get(6, schemeNo) | 0xff000000;
		s.btnOver = image.get(7, schemeNo) | 0xff000000;
		s.btnDown = image.get(8, schemeNo) | 0xff000000;
		
		s.sdrTrack = image.get(10, schemeNo) | 0xff000000;
		s.sdrThumb = image.get(11, schemeNo) | 0xff000000;
		s.sdrBorder = image.get(12, schemeNo) | 0xff000000;
		
		s.txfFont = image.get(15, schemeNo) | 0xff000000;
		s.txfBack = image.get(16, schemeNo) | 0xff000000;
		//s.txfSelFont = image.get(17, schemeNo);
		s.txfSelBack = image.get(18, schemeNo)  | 0xff000000;
		s.txfBorder = image.get(19, schemeNo)  | 0xff000000;
		
		s.lblFont = image.get(20, schemeNo) | 0xff000000;
		s.lblBack = image.get(21, schemeNo) | 0xff000000;
		s.lblBorder = image.get(22, schemeNo) | 0xff000000;
		
		s.optFont = image.get(25, schemeNo) | 0xff000000;
		s.optBack = image.get(26, schemeNo) | 0xff000000;
		s.optBorder = image.get(27, schemeNo) | 0xff000000;
		
		s.cbxFont = image.get(30, schemeNo) | 0xff000000;
		s.cbxBack = image.get(31, schemeNo) | 0xff000000;
		s.cbxBorder = image.get(32, schemeNo) | 0xff000000;
	}
	
	// Class attributes and methods start here
	
	// Scheme number
	public int schemeNo = 0;
	// Panels
	public int pnlFont, pnlTabBack, pnlBack, pnlBorder;
	// Buttons
	public int btnFont, btnOff, btnOver, btnDown, btnBorder;
	// Sliders
	public int sdrTrack, sdrThumb, sdrBorder;
	// TextFields
	public int txfFont, txfBack, txfSelFont, txfSelBack, txfBorder;
	// Label
	public int lblFont, lblBack, lblBorder;
	// Option
	public int optFont, optBack, optBorder;
	// Checkbox
	public int cbxFont, cbxBack, cbxBorder;
	
	/**
	 * Create a default (blue) scheme
	 */
	public GCScheme(){
		schemeNo = 0;
		populateScheme(this, schemeNo);		
	}
	
	/**
	 * Create a scheme for a given scheme number
	 * @param csn
	 */
	public GCScheme (int csn){
		schemeNo = csn;
		populateScheme(this, schemeNo);
	}

	/**
	 *  Copy ctor
	 * @param gcScheme scheme to copy
	 */
	public GCScheme(GCScheme gcScheme){
		schemeNo = gcScheme.schemeNo;
		populateScheme(this, schemeNo);		
	}


	/**
	 * 
	 * @param alpha in the range 0 (fully transparent) to 255 (fully opaque)
	 */
	public void setAlpha(int alpha){
		alpha = (alpha & 0xff) << 24;
	
		pnlFont = (pnlFont & 0x00ffffff) | alpha;
		pnlTabBack = (pnlTabBack & 0x00ffffff) | alpha;
		pnlBack = (pnlBack & 0x00ffffff) | alpha;
		pnlBorder = (pnlBorder & 0x00ffffff) | alpha;
		btnFont = (btnFont & 0x00ffffff) | alpha;
		btnOff = (btnOff & 0x00ffffff) | alpha;
		btnOver = (btnOver & 0x00ffffff) | alpha;
		btnDown = (btnDown & 0x00ffffff) | alpha;
		btnBorder = (btnBorder & 0x00ffffff) | alpha;
		sdrTrack = (sdrTrack & 0x00ffffff) | alpha;
		sdrThumb = (sdrThumb & 0x00ffffff) | alpha;
		sdrBorder = (sdrBorder & 0x00ffffff) | alpha;
		txfFont = (txfFont & 0x00ffffff) | alpha;
		txfBack = (txfBack & 0x00ffffff) | alpha;
		txfSelBack = (txfSelBack & 0x00ffffff) | alpha;
		txfBorder = (txfBorder & 0x00ffffff) | alpha;
		lblFont = (lblFont & 0x00ffffff) | alpha;
		lblBack = (lblBack & 0x00ffffff) | alpha;
		lblBorder = (lblBorder & 0x00ffffff) | alpha;
		optFont = (optFont & 0x00ffffff) | alpha;
		optBack = (optBack & 0x00ffffff) | alpha;
		optBorder = (optBorder & 0x00ffffff) | alpha;
		cbxFont = (cbxFont & 0x00ffffff) | alpha;
		cbxBack = (cbxBack & 0x00ffffff) | alpha;
		cbxBorder = (cbxBorder & 0x00ffffff) | alpha;
	}
	
} // end of class
