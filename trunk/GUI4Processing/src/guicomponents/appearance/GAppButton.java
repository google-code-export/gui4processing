package guicomponents.appearance;

public class GAppButton extends GAppCore{
//	public int colOff;
	public int colOver;
	public int colDown;

	public GAppButton(int colBack, int colOver, int colDown, int colBorder,
			int colFont, int alpha) {
		super(colBack, colBorder, colFont);
		this.colOver = colOver;
		this.colDown = colDown;
		this.alpha = alpha;
		setAlpha(alpha);
	}
	
	public void setAlpha(int alpha){
		super.setAlpha(alpha);
		colOver = GCSchemes.setAlpha(colOver, alpha);
		colDown = GCSchemes.setAlpha(colDown, alpha);
	}
	
	
}
