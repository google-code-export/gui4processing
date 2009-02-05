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

import java.util.HashMap;

import processing.core.PApplet;
import processing.core.PFont;

public class GFont {
	protected PApplet app;

	// Keep track of all fonts made and prevent duplicates
	private static final HashMap<GFontKey, PFont> fontmap = new HashMap<GFontKey, PFont>();

//	public static final String SERIF = "Arial Bold";
//	public static final String SANS_SERIF = "Arial Bold";
//	public static final String PRETTY = "Georgia Bold";
	
	/**
	 * Create a font
	 * 
	 * @param theApplet
	 * @param fontname .vlw name for font
	 * @param fsize
	 * @return
	 */
	public static PFont getFont(PApplet theApplet, String fontname, int fsize){
		// Keep size to something manageable
		fsize = PApplet.constrain(fsize, 8, 72);
		GFontKey fkey = new GFontKey(fontname, fsize);
		PFont pfont = null;
		// See if the font has already been created
		// if so return it else make it
		if(fontmap.containsKey(fkey))
			pfont = fontmap.get(fkey);
		else {
			// Attempt to make the this font
			pfont = theApplet.createFont(fontname, fsize, true);
			// if no such system font then make one using default sans-serif font of same size
			if(pfont != null){
				fontmap.put(fkey, pfont);	// remember it
			} else {
				System.out.println("Unable to find font " + fontname + 
						" using default Sans Serif font of same size");
				// make sans-serif font at this size if not already done
				fkey = new GFontKey("SansSerif", fsize);
				if(fontmap.containsKey(fkey))
					pfont = fontmap.get(fkey);
				else {
					pfont = theApplet.createFont("SansSerif", fsize, true);
					fontmap.put(fkey, pfont);
				}
			}
		}
		System.out.println("Font = "+fkey + "     "+fkey.hashCode());
		return pfont;
	}
	
	public static PFont getSerifFont(PApplet theApplet, int fsize){
		fsize = PApplet.constrain(fsize, 8, 72);
		GFontKey fkey = new GFontKey("Serif", fsize);
		PFont pfont;
		if(fontmap.containsKey(fkey))
			return fontmap.get(fkey);
		else {
			pfont = theApplet.createFont("Serif", fsize, true);
			fontmap.put(fkey, pfont);
			return pfont;
		}
	}

	public static PFont getSansSerifFont(PApplet theApplet, int fsize){
		fsize = PApplet.constrain(fsize, 8, 72);
		GFontKey fkey = new GFontKey("SansSerif", fsize);
		PFont pfont;
		if(fontmap.containsKey(fkey))
			pfont = fontmap.get(fkey);
		else {
			pfont = theApplet.createFont("SansSerif", fsize, true);
			fontmap.put(fkey, pfont);
		}
		return pfont;
	}

	public static PFont getMonospacedFont(PApplet theApplet, int fsize){
		fsize = PApplet.constrain(fsize, 8, 72);
		GFontKey fkey = new GFontKey("Monospaced", fsize);
		PFont pfont;
		if(fontmap.containsKey(fkey))
			pfont = fontmap.get(fkey);
		else {
			pfont = theApplet.createFont("Monospaced", fsize, true);
			fontmap.put(fkey, pfont);
		}
		return pfont;
	}

	public static PFont getDefaultFont(PApplet theApplet){
		PFont pfont = GFont.getFont(theApplet, "Arial Bold", 11);
		return pfont;
	}

	
	// Defines a key to uniquely identify fonts created
	private static class GFontKey implements Comparable<GFontKey>{

		private final String fontKey;

		public GFontKey(String fontname, int fontsize){
			fontKey = fontname + "-" + fontsize;
		}

		public boolean equals(Object o){
			GFontKey fkey = (GFontKey) o;
			if(fkey == null)
				return false;
			return fontKey.equals(fkey.fontKey);
		}
	
		public int hashCode(){
			return fontKey.hashCode();
		}
		
		@Override
		public int compareTo(GFontKey fkey) {
			if(fkey == null)
				return 1;
			return fontKey.compareTo(fkey.fontKey );
		}

		public String toString(){
			return fontKey;
		}
	}
}
