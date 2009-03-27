package guicomponents.appearance;


public class GAppPanel extends GAppCore {

	public int colTab;

	public GAppPanel(int colBack, int colTab, int colBorder,
			int colFont, int alpha) {
		super(colBack, colBorder, colFont);
		this.colTab = colTab;
		this.alpha = alpha;
		setAlpha(alpha);
	}
	
	public void setAlpha(int alpha){
		super.setAlpha(alpha);
		colTab = GCSchemes.setAlpha(colTab, alpha);
	}

}
