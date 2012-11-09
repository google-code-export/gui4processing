package guicomponents;

public interface GConstantsInternal {

	// Constants for the control methods
	public static final int DRAW_METHOD = 			0x00000001;
	public static final int MOUSE_METHOD = 			0x00000002;
	public static final int PRE_METHOD = 			0x00000004;
	public static final int KEY_METHOD = 			0x00000008;
	public static final int POST_METHOD = 			0x00000010;
	public static final int ALL_METHOD =			0x0000001f;

	
	
	// Constants for merging attribute runs
	public static final int I_NONE = 			0;
	public static final int I_TL = 				1;
	public static final int I_TR = 				2;
	public static final int I_CL = 				4;
	public static final int I_CR = 				8;
	public static final int I_INSIDE =			16;
	public static final int I_COVERED =			32;
	public static final int I_MODES =			63;
	
	// Merger action
	public static final int MERGE_RUNS = 		256;
	public static final int CLIP_RUN = 			512;
	public static final int COMBI_MODES = 		768;
	
	public static final int[][] grid = new int[][] {
		{ I_NONE,	I_TL,		I_CL,		I_COVERED,	I_COVERED },
		{ I_NONE,	I_NONE, 	I_INSIDE, 	I_INSIDE, 	I_COVERED },
		{ I_NONE,	I_NONE, 	I_INSIDE, 	I_INSIDE, 	I_CR },
		{ I_NONE,	I_NONE, 	I_NONE, 	I_NONE, 	I_TR },
		{ I_NONE,	I_NONE, 	I_NONE, 	I_NONE, 	I_NONE }
	};
	
}
