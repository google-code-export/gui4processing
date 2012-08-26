package guicomponents;

/**
 * This class is useful to hold information about a position in the 
 * StyledString. <br>
 * 
 * Inside this class [x,y] coordinates are measured from the top-left corner of the 
 * TextLayouts. <br>
 * 
 * The actual values to store in the attributes are calculated from the StyledString
 * class.
 * 
 * @author Peter Lager
 *
 */
public class Location implements Comparable<Location> {
	
	public int charInText, charOnLine, lineNo;
	public float cursorX, cursorY, cursorHeight;
	public boolean valid;

	/**
	 * Create an invalidated Location
	 */
	public Location(){
		charInText = charOnLine = lineNo = -1;
		cursorX = cursorY = cursorHeight = 0;
		valid = false;
	}
	
	/**
	 * Copy constructor in case we need it.
	 * @param loc
	 */
	public Location(Location loc){
		charInText = loc.charInText;
		charOnLine = loc.charOnLine;
		lineNo = loc.lineNo;
		cursorX = loc.cursorX;
		cursorY = loc.cursorY;
		cursorHeight = loc.cursorHeight;
		valid = loc.valid;
	}
	
	/**
	 * Invalidate this location.
	 */
	public void invalidate(){
		charInText = charOnLine = lineNo = -1;
		cursorX = cursorY = cursorHeight = 0;
		valid = false;	
	}
	
	/**
	 * Two locations are compared based on their character position in 
	 * the string.
	 */
	public int compareTo(Location loc) {
		Integer cit = new Integer(charInText);
		Integer loc_cit = new Integer(loc.charInText);
		return cit.compareTo(loc_cit);
	}

	/**
	 * Two locations are considered equal if they represent the same
	 * character position in the string.
	 */
	public boolean equals(Location loc){
		return charInText == loc.charInText;
	}
	
	/**
	 * Set this location to be the same as another location.
	 * @param loc the location to copy
	 */
	public void setEqualTo(Location loc){
		charInText = loc.charInText;
		charOnLine = loc.charOnLine;
		lineNo = loc.lineNo;
		cursorX = loc.cursorX;
		cursorY = loc.cursorY;
		cursorHeight = loc.cursorHeight;
		valid = loc.valid;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder("Curser in text " + charInText);
		sb.append("@ Line " + lineNo);
		sb.append("  character " + charOnLine);
		sb.append("  Cursor [" + cursorX + ", " + cursorY + "]");
		return sb.toString();
	}

}
