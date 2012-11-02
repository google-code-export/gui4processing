package guicomponents;

import processing.core.PApplet;

public interface IControl {

	/** Used to when components overlap */
	public int z = 0;

	public IControl getParent();
	
	public PApplet getPApplet();

//	public int compareTo(Object o);
}
