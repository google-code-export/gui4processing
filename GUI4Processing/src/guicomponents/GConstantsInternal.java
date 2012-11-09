package guicomponents;

public interface GConstantsInternal {

	// Constants for the control methods
	public static final int DRAW_METHOD = 			0x00000001;
	public static final int MOUSE_METHOD = 			0x00000002;
	public static final int PRE_METHOD = 			0x00000004;
	public static final int KEY_METHOD = 			0x00000008;
	public static final int POST_METHOD = 			0x00000010;
	public static final int ALL_METHOD =			0x0000001f;

	
	// Attribute run intersection constants
	public static final int NO_INTERSECTION = 		0;
	public static final int START_INTERSECTION = 	1;
	public static final int END_INTERSECTION = 		2;
	public static final int SURROUNDS =			 	3;
	public static final int SURROUNDED =		 	4;
	
	// Merger Mode
	public static final int MM_NONE = 		0;
	public static final int MM_START = 		1;
	public static final int MM_END = 		2;
	public static final int MM_SURROUNDS =	3;
	public static final int MM_SURROUNDED =	4;
	
	// Merger action
	public static final int MM_MERGE = 		256;
	public static final int MM_CROP = 			257;
	
}
