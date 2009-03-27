package guicomponents.appearance;

import processing.core.PApplet;

public class GCSchemes {

	// Color scheme constants
	public static final int BLUE_SCHEME 	= 0x00010001;
	public static final int GREEN_SCHEME 	= 0x00010002;
	public static final int RED_SCHEME 		= 0x00010003;
	public static final int GREY_SCHEME 	= 0x00010004;
	public static final int YELLOW_SCHEME	= 0x00010005;
	public static final int CYAN_SCHEME 	= 0x00010006;
	public static final int PURPLE_SCHEME	= 0x00010007;

	// Mask to get RGB
	public static final int COL_MASK 		= 0x00ffffff;
	// Mask to get alpha
	public static final int ALPHA_MASK 		= 0xff000000;

	protected static PApplet app;

	public static int setAlpha(int col, int alpha){
		alpha = (alpha & 0xff) << 24;
		col = (col & COL_MASK) | alpha;
		return col;
	}
}
