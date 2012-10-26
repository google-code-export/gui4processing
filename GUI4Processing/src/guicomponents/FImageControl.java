/*
  Part of the GUI for Processing library 
  	http://www.lagers.org.uk/g4p/index.html
	http://gui4processing.googlecode.com/svn/trunk/

  Copyright (c) 2008-12 Peter Lager

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

import guicomponents.HotSpot.HSalpha;
import guicomponents.HotSpot.HSmask;
import processing.core.PApplet;
import processing.core.PImage;

public class FImageControl extends FAbstractControl {

	protected PImage[] bimage = null;
	public PImage mask = null;

	public FImageControl(PApplet theApplet, float p0, float p1, float p2, float p3,
			String[] fnames, String fnameMask) {
		super(theApplet, p0, p1, p2, p3);
		int controlWidth = (int)width, controlHeight = (int)height;
		bimage = new PImage[3];
		if(fnames == null)
			fnames = new String[] { "err0.png", "err1.png", "err2.png" };
		for(int i = 0; i < fnames.length; i++){
			bimage[i] = winApp.loadImage(fnames[i]);
			if(bimage[i] == null)
				bimage[i] = winApp.loadImage("err"+i+".png");
			if(bimage[i].width != controlWidth || bimage[i].height != controlHeight)
				bimage[i].resize(controlWidth, controlHeight);
		}
		for(int i = fnames.length; i < 3; i++)
			bimage[i] = bimage[i-1];

		// Get mask image if appropriate
		if(fnameMask != null)
			mask = winApp.loadImage(fnameMask);
		if(mask != null){	// if we have a mask use it for the hot spot
			if(mask.width != controlWidth || mask.height != controlHeight){
				mask.resize(controlWidth, controlHeight);
			}
			hotspots = new HotSpot[]{
					new HSmask(1,mask)
			};
		}
		else {   // no mask then use alpha channel of the OFF image
			hotspots = new HotSpot[]{
					new HSalpha(1, 0, 0, bimage[0], PApplet.CORNER)
			};
		}

	}


}
