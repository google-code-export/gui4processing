package guicomponents;

import guicomponents.HotSpot.*;
import processing.core.PApplet;
import processing.core.PGraphicsJava2D;
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
					new HSalpha(1, bimage[0])
			};
		}

	}


}
