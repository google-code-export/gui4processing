package guicomponents;
import processing.core.PApplet;
import processing.core.PImage;


public class FTextIconComponent extends GComponent {

	protected static final int TPAD = 2;
	protected static final int TPAD2 = TPAD * 2;
	
	protected PImage[] bimage = null;
	protected int imgHspace = 0, imgVspace = 0;
	protected int iconAlign = GAlign.RIGHT | GAlign.MIDDLE;
	protected int textAlign = GAlign.CENTER | GAlign.MIDDLE;
	
	protected boolean useImages = false;

	public FTextIconComponent(PApplet theApplet, float p0, float p1, float p2, float p3) {
		super(theApplet, p0, p1, p2, p3);
	}

	public void setTextNew(String ntext){
		super.setTextNew(ntext, (int) width - TPAD2);
	}

	public GComponent setIcon(String fname, int nbrImages, int align){
		PImage iconImage = winApp.loadImage(fname);
		setIcon(iconImage, nbrImages, align);
		return this;
	}
	
	public GComponent setIcon(PImage icon, int nbrImages, int align){
		bimage = loadImages(icon, nbrImages);
		// Make sure we managed to load something
		if(bimage != null){
			imgHspace = bimage[0].width + TPAD2;
			imgVspace = bimage[0].height + TPAD2;
			// Make sure the image will fit the space with some padding
			if(imgHspace + TPAD2 < width && imgVspace < height){
				// Now sort out text and justify if necessary
				stext = new StyledString(text, (int) width - TPAD2 - imgHspace);
				stext.setJustify((align & GAlign.H_ALIGN) == GAlign.JUSTIFY);
				iconAlign = align;
			}
		}
		return this;
	}
	
	public GComponent setTextAlignNew(int align){
		if(align != textAlign){
			stext = new StyledString(text, (int) width - TPAD2 - imgHspace);
			stext.setJustify((align & GAlign.H_ALIGN) == GAlign.JUSTIFY);
			textAlign = align;			
		}
		return this;
	}
	
}
