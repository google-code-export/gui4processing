/*
  Part of the GUI for Processing library 
  	http://gui4processing.lagers.org.uk
	http://code.google.com/p/gui4processing/
	
  Copyright (c) 2008-09 Peter Lager

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

public interface GUI {
	// Color scheme
	public static final int BLUE 	= 0x00010001;
	public static final int GREEN 	= 0x00010002;
	public static final int RED 	= 0x00010003;
	public static final int GREY 	= 0x00010004;
	public static final int YELLOW	= 0x00010005;
	public static final int CYAN	= 0x00010006;
	public static final int PURPLE	= 0x00010007;

	// Fonts
	public static final int FONT11 = 0x00020001;
	public static final int FONT12 = 0x00020002;
	public static final int FONT16 = 0x00020003;
	
	// Alignment
	public static final int LEFT	= 0x00040001;
	public static final int RIGHT	= 0x00040002;
	public static final int CENTER	= 0x00040003;
	
	// Button states
	public static final int OFF	= 0x00050001;
	public static final int OVER	= 0x00050002;
	public static final int DOWN	= 0x00050003;
}
