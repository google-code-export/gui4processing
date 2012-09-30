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
  Lesser General License for more details.

  You should have received a copy of the GNU Lesser General
  License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
 */

package guicomponents;

import java.awt.BasicStroke;
import java.awt.font.TextAttribute;

/**
 * @author Peter Lager
 *
 */
public interface GConstants {

	// 0x0???????  user can disable these messages 
	// 0x8???????  always display these messages

	static int DRAW_METHOD = 			0x00000001;
	static int MOUSE_METHOD = 			0x00000002;
	static int PRE_METHOD = 			0x00000004;
	static int KEY_METHOD = 			0x00000008;
	static int ALL_METHOD =				0x0000000f;
	
	// Button status values
	static final int OFF_CONTROL = 		0;
	static final int OVER_CONTROL = 	1;
	static final int PRESS_CONTROL = 	2;

	// Text orientation for sliders
	static final int ORIENT_LEFT = 		-1;
	static final int ORIENT_TRACK = 	0;
	static final int ORIENT_RIGHT = 	1;
	
	// ### Event constants ###
	// TextField component (GSlider also uses CHANGED)
	final static int CHANGED = 			0x00000101;	// Text has changed
	final static int ENTERED = 			0x00000102;	// Enter key pressed
	final static int SET = 				0x00000103;	// setText() was used

	// GPanel component
	final static int COLLAPSED = 		0x00000201;	// Panel has been collapsed
	final static int EXPANDED = 		0x00000202;	// Panel has been expanded
	final static int DRAGGED = 			0x00000203;	// Panel has been dragged

	// GButton
	final static int CLICKED = 			0x00000301;
	final static int PRESSED = 			0x00000302;
	final static int RELEASED = 		0x00000303;

	// GCheckbox & GOption
	final static int SELECTED = 		0x00000401;
	final static int DESELECTED = 		0x00000402;

	// GRoundControl
	final static int CTRL_ANGULAR = 	0x00000501;
	final static int CTRL_HORIZONTAL = 	0x00000502;
	final static int CTRL_VERTICAL = 	0x00000503;

	// GWindow
	final static int EXIT_APP = 		0x00000f01;
	final static int CLOSE_WINDOW = 	0x00000f02;
	final static int KEEP_OPEN = 		0x00000f03;

	// ### GUI build constants ###
	final static int ADD_DUPLICATE = 		0x00010101;
	final static int USER_COL_SCHEME = 		0x00010102;
	final static int DISABLE_AUTO_DRAW =	0x00010103;

	// The min alpha level for a control to respond to mouse and keyboard
	final int ALPHA_BLOCK =					200;
	// The min alpha before a pixel is considered for a hot spot
	final int ALPHA_PICK =					48;
	
	// ### Scroll bar policy constants ###
	/** Do not create or display any scrollbars for the text area. */
	static final int SCROLLBARS_NONE = 				0x0000;
	/** Create and display vertical scrollbar only. */
	static final int SCROLLBARS_VERTICAL_ONLY = 	0x0001;
	/** Create and display horizontal scrollbar only. */
	static final int SCROLLBARS_HORIZONTAL_ONLY = 	0x0002;
	/** Create and display both vertical and horizontal scrollbars. */
	static final int SCROLLBARS_BOTH = 				0x0003;

	static final int SCROLLBARS_AUTOHIDE =			0x1000;

	// ### Scroll bar type constants ###
	/** Create and display vertical scrollbar only. */
	static final int SCROLLBAR_VERTICAL = 			1;
	/** Create and display horizontal scrollbar only. */
	static final int SCROLLBAR_HORIZONTAL = 		2;


	// Slider / numeric display types
	static final int INTEGER = 		0;
	static final int DECIMAL = 		1;
	static final int EXPONENT = 	2;

	// ### Error MessageTypes ###
	final static int RUNTIME_ERROR = 	0xf0000000;
	// Event method handler errors
	final static int MISSING = 			0x01000001;	// Can't find standard handler
	final static int NONEXISTANT = 		0x01000002;
	final static int EXCP_IN_HANDLER =	0x81000003;	// Exception in event handler
	// PeasyCam errors
	final static int NOT_PEASYCAM =		0x82000001; // Not a PeasyCam object
	final static int HUD_UNSUPPORTED = 	0x82000002; // HUD not supported
	final static int INVALID_STATUS = 	0x82000003; // HUD not supported

	// GTextField text scroll constants
	final static int SCROLL_UP =		0x00000111;	// Scroll text up
	final static int SCROLL_DOWN =		0x00000112;	// Scroll text down
	final static int SCROLL_LEFT =		0x00000113;	// Scroll text left
	final static int SCROLL_RIGHT =		0x00000114;	// Scroll text right


	// Attribute:- fontface   Value Type:- String font family name e.g. "Times New Roman"
	static final TextAttribute FAMILY = TextAttribute.FAMILY;

	// Attribute:- font weight   Value Type:- Float in range (0.5 to 2.75)
	static final TextAttribute WEIGHT = TextAttribute.WEIGHT;
	// Predefined constants for font weight
	static final Float WEIGHT_EXTRA_LIGHT = 	new Float(0.5f);
	static final Float WEIGHT_LIGHT = 			new Float(0.75f);
	static final Float WEIGHT_DEMILIGHT = 		new Float(0.875f);
	static final Float WEIGHT_REGULAR = 		new Float(1.0f);
	static final Float WEIGHT_SEMIBOLD = 		new Float(1.25f);
	static final Float WEIGHT_MEDIUM = 			new Float(1.5f);
	static final Float WEIGHT_DEMIBOLD = 		new Float(1.75f);
	static final Float WEIGHT_BOLD = 			new Float(2.0f);
	static final Float WEIGHT_HEAVY = 			new Float(2.25f);
	static final Float WEIGHT_EXTRABOLD = 		new Float(2.5f);
	static final Float WEIGHT_ULTRABOLD = 		new Float(2.75f);

	// Attribute:- font width   Value Type:- Float in range (0.75 to 1.5)
	static final TextAttribute WIDTH = 			TextAttribute.WIDTH;
	// Predefined constants for font width
	static final Float WIDTH_CONDENSED = 		new Float(0.75f);
	static final Float WIDTH_SEMI_CONDENSED = 	new Float(0.875f);
	static final Float WIDTH_REGULAR = 			new Float(1.0f);
	static final Float WIDTH_SEMI_EXTENDED = 	new Float(1.25f);
	static final Float WIDTH_EXTENDED = 		new Float(1.5f);

	// Attribute:- font posture   Value Type:- Float in range (0.0 to 0.20)
	static final TextAttribute POSTURE = 		TextAttribute.POSTURE;
	// Predefined constants for font posture (plain or italic)
	static final Float POSTURE_REGULAR = 		new Float(0.0f);
	static final Float POSTURE_OBLIQUE = 		new Float(0.20f);

	// Attribute:- font size   Value Type:- Float
	static final TextAttribute SIZE = 			TextAttribute.SIZE;

	// Attribute:- font superscript   Value Type:- Integer (1 : super or -1 subscript)
	static final TextAttribute SUPERSCRIPT = 	TextAttribute.SUPERSCRIPT;
	// Predefined constants for font super/subscript
	static final Integer SUPERSCRIPT_SUPER = 	new Integer(1);
	static final Integer SUPERSCRIPT_SUB = 		new Integer(-1);
	static final Integer SUPERSCRIPT_OFF = 		new Integer(0);

	// Attribute:- font foreground snd bsckground colour   Value Type:- Color
	static final TextAttribute FOREGROUND = 	TextAttribute.FOREGROUND;
	static final TextAttribute BACKGROUND = 	TextAttribute.BACKGROUND;

	// Attribute:- font strike through   Value:- Boolean
	static final TextAttribute STRIKETHROUGH = 	TextAttribute.STRIKETHROUGH;
	// Predefined constants for font strike through on/off
	static final Boolean STRIKETHROUGH_ON = 	new Boolean(true);
	static final Boolean STRIKETHROUGH_OFF = 	new Boolean(false);


//	static final TextAttribute JUSTIFICATION = TextAttribute.JUSTIFICATION;
//	static final Float JUSTIFICATION_FULL = new Float(1.0f);
//	static final Float JUSTIFICATION_NONE = new Float(0.0f);

	
	static BasicStroke pen_1_0 = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	static BasicStroke pen_2_0 = new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	static BasicStroke pen_3_0 = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	static BasicStroke pen_4_0 = new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

}
