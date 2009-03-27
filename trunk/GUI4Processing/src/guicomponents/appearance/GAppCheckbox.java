package guicomponents.appearance;

public class GAppCheckbox {
	public int colBack;
	public int colBorder;
	public int colFont;
	
	public int alpha = 0xff000000;

	public GAppCheckbox(int colBack, int colBorder, int colText, int alpha) {
		super();
		this.colBack = colBack;
		this.colBorder = colBorder;
		this.colFont = colText;
		this.alpha = alpha;
		setAlpha(alpha);
	}
	
	public void setAlpha(int alpha){
		colBack = GCSchemes.setAlpha(colBack, alpha);
		colBorder = GCSchemes.setAlpha(colBorder, alpha);
		colFont = GCSchemes.setAlpha(colFont, alpha);
	}

}
