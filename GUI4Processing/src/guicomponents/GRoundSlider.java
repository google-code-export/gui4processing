package guicomponents;

import processing.core.PApplet;

public class GRoundSlider extends GComponent {

	protected int aLow = -270;
	protected int aHigh = 90;
	
	protected int aCurr = aLow, aTarget = aLow;
	
	public GRoundSlider(PApplet theApplet, int x, int y) {
		super(theApplet, x, y);
		// TODO Auto-generated constructor stub
	}

}
