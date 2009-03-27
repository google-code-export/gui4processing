package guicomponents.appearance;

public class GAppSlider {
	
	public int colBack;
	public int colThumb;
	public int colBorder;
	
	public int alpha = 0xff000000;

	public GAppSlider(int colBack, int colBorder, int colThumb, int alpha) {
		this.colBack = colBack;
		this.colBorder = colBorder;
		this.colThumb = colThumb;
		this.alpha = alpha;
		setAlpha(alpha);
	}
	
	public void setAlpha(int alpha){
		colBack = GCSchemes.setAlpha(colBack, alpha);
		colBorder = GCSchemes.setAlpha(colBorder, alpha);
		colThumb = GCSchemes.setAlpha(colThumb, alpha);
	}

}
