package guicomponents;

import java.awt.BasicStroke;

public interface GConstantsInternal {

	// Constants for the control methods
	public static final int DRAW_METHOD = 			0x00000001;
	public static final int MOUSE_METHOD = 			0x00000002;
	public static final int PRE_METHOD = 			0x00000004;
	public static final int KEY_METHOD = 			0x00000008;
	public static final int POST_METHOD = 			0x00000010;
	public static final int ALL_METHOD =			0x0000001f;

	// ### Error MessageTypes ###
	public final static int RUNTIME_ERROR = 	0xf0000000;
	// Event method handler errors
	public final static int MISSING = 			0x01000001;	// Can't find standard handler
	public final static int NONEXISTANT = 		0x01000002;
	public final static int EXCP_IN_HANDLER =	0x81000003;	// Exception in event handler

	// Button/slider status values
	public static final int OFF_CONTROL	= 		0;
	public static final int OVER_CONTROL	= 	1;
	public static final int PRESS_CONTROL = 	2;

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
	
	// merger decision grid
	public static final int[][] grid = new int[][] {
		{ I_NONE,	I_TL,		I_CL,		I_COVERED,	I_COVERED },
		{ I_NONE,	I_NONE, 	I_INSIDE, 	I_INSIDE, 	I_COVERED },
		{ I_NONE,	I_NONE, 	I_INSIDE, 	I_INSIDE, 	I_CR },
		{ I_NONE,	I_NONE, 	I_NONE, 	I_NONE, 	I_TR },
		{ I_NONE,	I_NONE, 	I_NONE, 	I_NONE, 	I_NONE }
	};
	
	// Basic strokes needed when using the Graphics2D object for drawing on the buffer
	public static BasicStroke pen_1_0 = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	public static BasicStroke pen_2_0 = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	public static BasicStroke pen_3_0 = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	public static BasicStroke pen_4_0 = new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

}
