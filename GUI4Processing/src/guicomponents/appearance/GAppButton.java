package guicomponents.appearance;

public class GAppButton {
	public int colOff;
	public int colOver;
	public int colDown;
	public int colBorder;
	public int colText;
	
	public int alpha = 0xff000000;

	public GAppButton(int colOff, int colOver, int colDown, int colBorder,
			int colText, int alpha) {
		super();
		this.colOff = colOff;
		this.colOver = colOver;
		this.colDown = colDown;
		this.colBorder = colBorder;
		this.colText = colText;
		this.alpha = alpha;
	}
	
	public void setAlpha(int alpha){
		colOff = GCSchemes.setAlpha(colOff, alpha);
		colOver = GCSchemes.setAlpha(colOver, alpha);
		colDown = GCSchemes.setAlpha(colDown, alpha);
		colBorder = GCSchemes.setAlpha(colBorder, alpha);
		colText = GCSchemes.setAlpha(colText, alpha);
	}
	
	
}
