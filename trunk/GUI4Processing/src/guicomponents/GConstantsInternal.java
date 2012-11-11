/*
  Part of the GUI for Processing library 
  	http://www.lagers.org.uk/g4p/index.html
	http://gui4processing.googlecode.com/svn/trunk/

  Copyright (c) 2008-12 Peter Lager

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
 */

package guicomponents;

import java.awt.BasicStroke;

/**
 * Constants that are used internally by the library.
 * 
 * @author Peter Lager
 *
 */
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
