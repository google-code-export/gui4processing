package guicomponents;

import processing.core.PApplet;

public class FCheckbox extends FToggleControl {

	public FCheckbox(PApplet theApplet, float p0, float p1, float p2, float p3, String text) {
		super(theApplet, p0, p1, p2, p3);
		opaque = false;
		setText(text);
		setIcon("tick.png", 2, GAlign.LEFT);
		z = Z_SLIPPY;
		// Now register control with applet
		createEventHandler(winApp, "handleToggleControlEvents", new Class[]{ FToggleControl.class });
		registeredMethods = DRAW_METHOD | MOUSE_METHOD;
		F4P.addControl(this);
	}

	/**
	 * This enforces independent action because this control cannot be added
	 * to a toggle group
	 */
	@Override
	protected void setToggleGroup(FToggleGroup tg) {}
	
}
