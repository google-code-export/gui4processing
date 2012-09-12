package guicomponents;
import processing.core.PApplet;
import processing.core.PImage;


public class FTextIconComponent extends GComponent {

	protected PImage[] bimage = null;
	protected int imgWidth = 0;
	protected int imageAlign = GAlign.RIGHT;
	
	protected boolean useImages = false;

	public FTextIconComponent(PApplet theApplet, float p0, float p1, float p2, float p3) {
		super(theApplet, p0, p1, p2, p3);
	}

	public void setTextNew(String ntext){
		super.setTextNew(ntext, (int) width - 4);
	}

}
