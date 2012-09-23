package guicomponents;

import processing.core.PApplet;

public class FOption extends FToggleControl{

	public FOption(PApplet theApplet, float p0, float p1, float p2, float p3, String text) {
		super(theApplet, p0, p1, p2, p3);
		opaque = false;
		setText(text);
		setIcon("pinhead.png", 2, GAlign.LEFT);
		z = Z_SLIPPY;
		// Now register control with applet
		createEventHandler(winApp, "handleToggleControlEvents", new Class[]{ FToggleControl.class });
		registeredMethods = DRAW_METHOD | MOUSE_METHOD;
		F4P.addControl(this);
	}




}
