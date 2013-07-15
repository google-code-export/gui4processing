package g4p_controls;

import processing.core.PApplet;
import processing.core.PImage;

public class GImageToggleButton extends GAbstractControl {

	protected int nbrStates;
	protected int currState;
	
	protected PImage[] offImage;
	protected PImage[] overImage;
	
	protected boolean overHighlight = true;
	
	
	public GImageToggleButton(PApplet theApplet, float p0, float p1, float p2, float p3) {
		super(theApplet, p0, p1, p2, p3);
		// TODO Auto-generated constructor stub
	}

	
	public GImageToggleButton(PApplet theApplet, float p0, float p1, String offPicture, String overPicture, int nbrStates){
		super(theApplet, p0, p1, 0, 0);
		
		
		
	}
}
