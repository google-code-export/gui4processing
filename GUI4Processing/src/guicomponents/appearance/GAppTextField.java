package guicomponents.appearance;

public class GAppTextField extends GAppCore {

	public int colSelFont;
	public int colSelBack;
	
	protected GAppTextField(int colBack, int colBorder, int colFont, 
			int colSelBack, int colSelFont, int alpha) {
		super(colBack, colBorder, colFont);
		this.colSelBack = colSelBack;
		this.colSelFont = colSelFont;
		this.alpha = alpha;
		setAlpha(alpha);
	}

	public void setAlpha(int alpha){
		super.setAlpha(alpha);
		colSelFont = GCSchemes.setAlpha(colSelFont, alpha);
		colSelBack = GCSchemes.setAlpha(colSelBack, alpha);
	}

}
