package guicomponents.appearance;

public class GAppCore {
	public int colBack;
	public int colBorder;
	public int colFont;
	
	public int alpha = 0xff000000;

	protected GAppCore(int colBack, int colBorder, int colFont) {
		this.colBack = colBack;
		this.colBorder = colBorder;
		this.colFont = colFont;
	}
	
	public GAppCore(int colBack, int colBorder, int colFont, int alpha) {
		this.colBack = colBack;
		this.colBorder = colBorder;
		this.colFont = colFont;
		this.alpha = alpha;
		setAlpha(alpha);
	}
	
	public void setAlpha(int alpha){
		colBack = GCSchemes.setAlpha(colBack, alpha);
		colBorder = GCSchemes.setAlpha(colBorder, alpha);
		colFont = GCSchemes.setAlpha(colFont, alpha);
	}

}
