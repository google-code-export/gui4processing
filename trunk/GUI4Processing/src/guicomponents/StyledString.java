package guicomponents;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.font.FontRenderContext;
import java.awt.font.GraphicAttribute;
import java.awt.font.ImageGraphicAttribute;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextHitInfo;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;
import java.util.LinkedList;
import java.util.ListIterator;

import processing.core.PApplet;

/**
 * This class is used to represent text with attributes. <br>
 * It means that you don't have to have the same style of font
 * or even the same font face over the whole length of the text. <br>
 * 
 * Most font features can be modified all except the text background 
 * which is transparent. There is a feature to highlight part of the string
 * by having a different background colour but this is used for highlighting
 * selected text in GTextField and GTextArea components.
 *  
 * @author Peter Lager
 *
 */
final public class StyledString implements Serializable {

	/**
	 * An array of names representing all the available fonts.
	 */
	transient final public static String[] fontFamilies = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

	transient private AttributedString styledText = null;
	transient private ImageGraphicAttribute spacer = null;
	transient private LineBreakMeasurer lineMeasurer = null;
	transient private LinkedList<TextLayoutInfo> linesInfo = new LinkedList<TextLayoutInfo>();

	// The plain text to be styled
	private String plainText = "";
	// List of attribute runs to return the string to its base style
	private LinkedList<AttributeRun> base = new LinkedList<AttributeRun>();
	// List of attribute runs to be applied over the base style
	private LinkedList<AttributeRun> atrun = new LinkedList<AttributeRun>();
	// A single attribute run to represent a text selection
	private AttributeRun textSelection;

	// The width to break a line
	private float breakWidth = 100;
	// Flag to determine whether the text layouts need recalculating
	private boolean invalidLayout = true;

	// Base style used for base
	private String family = "Dialog";
	private float posture = TextAttribute.POSTURE_REGULAR;
	private float size = 16.0f;
	// Base colours
	private final Color backcolor = new Color(255,255,255,0);
	private Color forecolor = Color.black;
	// Base justification
	private boolean justify = true;
	private float justifyRatio = 0.7f;

	private float textHeight = 0;
	private float maxLineLength = 0;
	private float maxLineHeight = 0;

	public StyledString(Graphics2D g2d, String startText, float lineWidth){
		if(lineWidth < 0 || lineWidth == Integer.MAX_VALUE)
			breakWidth = Integer.MAX_VALUE;
		else
			breakWidth = lineWidth;
		spacer = getParagraghSpacer((int)breakWidth);
		plainText = startText;
		styledText = new AttributedString(plainText);
		styledText = insertParagraphMarkers(plainText, styledText);
		resetStyles();
		linesInfo = getLines(g2d);
	}

	/**
	 * Get the plain text as a STring. Any line breaks will kept and will
	 * be represented by the character 'backslash n' <br>
	 * @return the associated plain text
	 */
	public String getPlainText(){
		return plainText;
	}

	/**
	 * Get the number of characters in this styled string
	 * @return
	 */
	public int length(){
		return plainText.length();
	}

	/* %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	 * 
	 * The following methods allow the user to change the base font style
	 * 
	 * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	 */

	/**
	 * Change the base foreground colour.
	 * @param c the font face colour to use
	 */
	public void setBaseForeground(Color c){
		forecolor = c;
		resetStyles();
	}

	/**
	 * Change the base foreground colour. <br>
	 * The value passed is a 32bit ARGB integer
	 * @param c the font face colour to use.
	 */
	public void setBaseForeground(int c){
		forecolor = new Color((c >> 16) & 0xff, (c >> 8) & 0xff, c & 0xff, (c >> 24));
		resetStyles();
	}


	/**
	 * Set the font face to be used. This will be system specific 
	 * but all Java implementations support the following logical
	 * fonts. <br>
	 * Dialog		<br>
	 * DialogInput	<br>
	 * Monospaced	<br>
	 * Serif		<br>
	 * SansSerif	<br>
	 * and the physical font faces for <br>
	 * Lucinda		<br>
	 * Requesting a font family that does not existing on the system 
	 * will be ignored. <br>
	 * Note the class attribute fontFamilies is an array of all 
	 * @param family
	 * @return true if the font was found and different from the current font family
	 */
	public boolean setBaseFontFamily(String family){
		String f = getFamily(family);
		if(f.length() > 0 && !f.equals(family)){
			this.family = f;
			resetStyles();
			return true;
		}
		return false;
	}

	/**
	 * Set the base font size. The size must be at least 8pt
	 * 
	 * @param size
	 */
	public void setBaseFontSize(float size){
		size = Math.max(8, size);
		this.size = size;
		resetStyles();
	}

	/**
	 * Set the base posture. <br>
	 * Values are constrained to the range 0.0 (REGULAR) 
	 * and 0.2 (ITALIC)
	 * @param posture
	 */
	public void setBaseFontPosture(float posture){
		if(posture <0)
			posture = 0;
		else if(posture > 0.2f)
			posture = 0.2f;
		this.posture = posture;
		resetStyles();
	}


	/* %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	 * 
	 * Set and clear the text selection highlight. Used by GTextField and
	 * GTextArea classes
	 * 
	 * %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	 */

	/**
	 * Set the highlight area
	 * @param c
	 * @param charStart
	 * @param charEnd
	 */
	public void setSelectionArea(Color c, int charStart, int charEnd){
		textSelection = new AttributeRun(TextAttribute.BACKGROUND, c, charStart, charEnd);
		applyAttributes();
	}

	/**
	 * Clear the highlight area
	 */
	public void clearSelection(){
		if(textSelection != null){
			textSelection = null;
			applyAttributes();
		}
	}

	/**
	 * Text can be either left or fully justified.
	 * @param justify true for full justification
	 */
	public void setJustify(boolean justify){
		if(this.justify != justify){
			this.justify = justify;
			invalidLayout = true;
		}
	}

	/**
	 * Justify only if the line has sufficient text to do so.
	 * 
	 * @param jRatio ratio of text length to visibleWidth 
	 */
	public void setJustifyRatio(float jRatio){
		if(justifyRatio != jRatio){
			justifyRatio = jRatio;
			if(justify)
				invalidLayout = true;
		}
	}

	/**
	 * This class uses transparent images to simulate end/starting positions
	 * for paragraphs
	 * @param ptext
	 * @param as
	 * @return
	 */
	private AttributedString insertParagraphMarkers(String ptext, AttributedString as ){
		plainText = ptext;
		int fromIndex = ptext.indexOf('\n', 0);
		while(fromIndex >= 0){
			as.addAttribute(TextAttribute.CHAR_REPLACEMENT, spacer, fromIndex, fromIndex + 1);
			fromIndex = ptext.indexOf('\n', fromIndex + 1);
		}
		return as;
	}

	/**
	 * Recalculate the TextLayouts when the plainText has changed. The user can specify 
	 * the line number to start the calculation.
	 * @param charStart
	 * @param charEnd
	 * @param lineStart
	 */
	public void resetStyles(){
		base.clear();
		base.add(new AttributeRun(TextAttribute.FAMILY, family));
		base.add(new AttributeRun(TextAttribute.SIZE, size));
		base.add(new AttributeRun(TextAttribute.POSTURE, posture));
		base.add(new AttributeRun(TextAttribute.JUSTIFICATION, TextAttribute.JUSTIFICATION_FULL));
		base.add(new AttributeRun(TextAttribute.BACKGROUND, backcolor));
		base.add(new AttributeRun(TextAttribute.FOREGROUND, forecolor));
		applyAttributes();
	}

	/**
	 * Set charStart and charEnd to <0 if full length.
	 * 
	 * @param type
	 * @param value
	 * @param charStart
	 * @param charEnd
	 */
	public void addAttribute(Attribute type, Object value, int charStart, int charEnd){
		AttributeRun ar = new AttributeRun(type, value, charStart, charEnd);
		atrun.addLast(ar);
		applyAttributes();
	}

	/**
	 * Add an attribute that affects the whole length of the string.
	 * @param type attribute type
	 * @param value attribute value
	 */
	public void addAttribute(Attribute type, Object value){
		addAttribute(type, value, -1, Integer.MAX_VALUE);
	}

	/**
	 * Must call this method to apply
	 */
	private void applyAttributes(){
		if(plainText.length() > 0){
			// Apply base
			for(AttributeRun ar : base){
				styledText.addAttribute(ar.atype, ar.value);
			}
			// Apply other attributes
			for(AttributeRun ar : atrun){
				if(ar.end == Integer.MAX_VALUE)
					styledText.addAttribute(ar.atype, ar.value);
				else {
					styledText.addAttribute(ar.atype, ar.value, ar.start, ar.end);
				}
			}
			// Apply text selection
			if(textSelection != null){
				if(textSelection.end == Integer.MAX_VALUE)
					styledText.addAttribute(textSelection.atype, textSelection.value);
				else {
					styledText.addAttribute(textSelection.atype, textSelection.value, textSelection.start, textSelection.end);
				}
			}
		}
		invalidLayout = true;
	}

	/**
	 * 
	 * @param chars
	 * @param insertPos the position in the text
	 */
	public void insertCharacters(String chars, int insertPos){
		int nbrChars = chars.length();
		plainText = plainText.substring(0, insertPos) + chars + plainText.substring(insertPos);
		for(AttributeRun ar : atrun){
			if(ar.end < Integer.MAX_VALUE){
				if(ar.end >= insertPos){
					ar.end += nbrChars;
					if(ar.start >= insertPos)
						ar.start += nbrChars;
				}
			}
		}
		styledText = new AttributedString(plainText);
		styledText = insertParagraphMarkers(plainText, styledText);
		applyAttributes();
	}

	/**
	 * Remove a number of characters from the string
	 * 
	 * @param nbrToRemove number of characters to remove
	 * @param fromPos start location for removal
	 * @return
	 */
	public boolean deleteCharacters(int nbrToRemove, int fromPos){
		if(fromPos < 0 || fromPos + nbrToRemove > plainText.length())
			return false;
		plainText = plainText.substring(0, fromPos) + plainText.substring(fromPos + nbrToRemove);
		ListIterator<AttributeRun> iter = atrun.listIterator(atrun.size());
		AttributeRun ar;
		while(iter.hasPrevious()){
			ar = iter.previous();
			if(ar.end < Integer.MAX_VALUE){
				if(ar.end >= fromPos){
					ar.end -= nbrToRemove;
					if(ar.start > ar.end)
						iter.remove();
					else if(ar.start >= fromPos)
						ar.start -= nbrToRemove;
				}
			}		
		}
		styledText = new AttributedString(plainText);
		styledText = insertParagraphMarkers(plainText, styledText);
		applyAttributes();
		return true;
	}

	
	/**
	 * Get the text layouts for display if the string has changed since last call
	 * to this method regenerate them.
	 * 
	 * @param g2d Graphics2D display context
	 * @return
	 */
	public LinkedList<TextLayoutInfo> getLines(Graphics2D g2d){
		if(linesInfo == null)
			linesInfo = new LinkedList<TextLayoutInfo>();
		if(invalidLayout){
			System.out.println("\t\t\t##############   Recreate text layouts");
			textHeight = 0;
			maxLineLength = 0;
			maxLineHeight = 0;
			linesInfo.clear();
			if(plainText.length() > 0){
				//int nbrChars = plainText.length();
				System.out.println("\t\t\t##############   Recreate line break measurer");
				AttributedCharacterIterator paragraph = styledText.getIterator(null, 0, plainText.length());
				FontRenderContext frc = g2d.getFontRenderContext();
				long t = System.nanoTime();
				lineMeasurer = new LineBreakMeasurer(paragraph, frc);
				t = System.nanoTime() - t;
				System.out.println("New line measurer tokk " + (t * 1e-6) + " milli seconds");
				
				float yposinpara = 0;
				int charssofar = 0;
				while (lineMeasurer.getPosition() < plainText.length()) {
					TextLayout layout = lineMeasurer.nextLayout(breakWidth);
					float advance = layout.getVisibleAdvance();
					if(justify){
						if(justify && advance > justifyRatio * breakWidth){
							//System.out.println(layout.getVisibleAdvance() + "  " + breakWidth + "  "+ layout.get);
							// If advance > breakWidth then we have a line break
							float jw = (advance > breakWidth) ? advance - breakWidth : breakWidth;
							layout = layout.getJustifiedLayout(jw);
						}
					}
					// Get line limit stats
					float lh = getHeight(layout);
					if(lh > maxLineHeight)
						maxLineHeight = lh;
					textHeight += lh;
					if(advance <= breakWidth && advance > maxLineLength)
						maxLineLength = advance;
					
					// Store line and line info
					linesInfo.add(new TextLayoutInfo(layout, charssofar, layout.getCharacterCount(), yposinpara));
					charssofar += layout.getCharacterCount();
					yposinpara += lh;
				}
			}
			invalidLayout = false;
		}
		return linesInfo;
	}

	/**
	 * Return the height of the text line(s)
	 */
	public float getAllLinesHeight(){
		return textHeight;
	}

	/**
	 * Return the length of the longest line.
	 */
	public float getMaxLineLength(){
		return maxLineLength;
	}

	/** 
	 * Get the height of the tallest line
	 */
	public float getMaxLineHeight(){
		return maxLineHeight;
	}

	/**
	 * Get the height of the given TextLayout
	 * @param layout
	 * @return
	 */
	private float getHeight(TextLayout layout){
		return layout.getAscent() +layout.getDescent() + layout.getLeading();
	}

	/**
	 * Get the break width used to create the lines.
	 * @return
	 */
	public float getBreakWidth(){
		return breakWidth;
	}

	/**
	 * This is not used at the moment and I am not sure it will needed
	 * in the final release. <br>
	 * It has NOT been tested<br>
	 * 
	 * @param loc
	 * @param charNumber
	 * @param lines
	 * @return
	 */
	public Location calculateFromCharNo(Location loc, int charNumber){
//		if(loc == null)
//			loc = new Location();
//		if(charNumber < 0 || charNumber >= plainText.length()){
//			loc.invalidate();
//			return loc;
//		} 
//		loc.valid = true;
//		loc.charInText = charNumber;
//		loc.lineNo = 0;
//		loc.cursorY = 0;
//		for(TextLayout line : lines){
//			if(charNumber > line.getCharacterCount()){
//				charNumber -= line.getCharacterCount();
//				loc.charOnLine = charNumber;
//				loc.cursorY += getHeight(line);
//				loc.lineNo ++;
//			}
//			else {
//				loc.cursorHeight = line.getAscent() + line.getDescent();
//				loc.cursorY += loc.cursorHeight;
//			}
//		}
		return loc;
	}

	/**
	 * For a given position [px, py] calculate and return the Location. <br>
	 * 
	 * 
	 * @param g2d
	 * @param loc the Location object to store the results
	 * @param px distance from the left hand side of text
	 * @param py distance to the top of the first line
	 * @return
	 */
	public Location calculateFromXY(Graphics2D g2d, Location loc, float px, float py){
//		if(loc == null)
//			loc = new Location();
//		loc.charInText = 0;
//		loc.valid = true;
//		float yLine = 0, layoutHeight;
//		if(px < 0) px = 0;
//		if(py < 0) py = 0;
//
//		loc.lineNo = 0;
//		TextLayout line = null;
//		do {
//			line = lines.get(loc.lineNo);
//			layoutHeight = getHeight(line);
//			if(py < layoutHeight)
//				break;
//			else {
//				loc.charInText += line.getCharacterCount();
//				yLine += layoutHeight;
//				py -= layoutHeight;
//				loc.lineNo++;
//			}
//		} while(loc.lineNo < lines.size());
//		if(loc.lineNo < lines.size()){
//			TextHitInfo thi = line.hitTestChar(px,py);
//			loc.charOnLine = thi.getInsertionIndex();
//			loc.charInText += loc.charOnLine;
//			Point2D caretPos = new Point2D.Float();
//			line.hitToPoint(thi, caretPos);
//			loc.cursorX = (float) caretPos.getX();
//			//cursorY = (float) line.getBaseline() + yLine;
//			loc.cursorY = getHeight(line) + yLine;
//			//cursorY += cursorHeight;
//			loc.cursorHeight = line.getAscent() + line.getDescent();
//		}
//		else {
//			// Place cursor after last character in last line
//			TextLayout lastline = lines.getLast();
//			loc.charInText = plainText.length();
//			loc.charOnLine = lastline.getCharacterCount();
//			loc.lineNo = lines.size() - 1;
//			loc.cursorHeight = getHeight(lastline);
//			loc.cursorX = lastline.getVisibleAdvance();
//			if(loc.cursorX > breakWidth)
//				loc.cursorX -= breakWidth;
//			loc.cursorY = yLine;
//		}
		return loc;
	}

	public TextLayoutHitInfo calculateFromXY(Graphics2D g2d, float px, float py){
		TextHitInfo thi = null;
		TextLayoutInfo tli = null;
		TextLayoutHitInfo tlhi = null;
		if(invalidLayout)
			getLines(g2d);
		if(px < 0) px = 0;
		if(py < 0) py = 0;
		tli = getLayoutInfo(py);
		// Correct py to match layout's upper-left bounds
		py -= tli.yPosInPara;
		// get hit
		thi = tli.layout.hitTestChar(px,py);
		tlhi = new TextLayoutHitInfo(tli, thi);
		return tlhi;
	}

	/**
	 * This will always return a layout.
	 * @param y Must be >= 0
	 * @return the first layout where y is above the upper layout bounds
	 */
	public TextLayoutInfo getLayoutInfo(float y){
		TextLayoutInfo tli = null;
		if(!linesInfo.isEmpty()){
			for(int i = linesInfo.size()-1; i >= 0; i--){
				tli = linesInfo.get(i);
				if(tli.yPosInPara <= y)
					break;
			}
		}
		return tli;
	}

	/**
	 * This will always return a layout.
	 * If c > than the index of the last character in the plain text then this
	 * should be corrected to the last character in the layout by the caller.
	 * 
	 * @param c the character position in text (must be >= 0
	 * @return the first layout where c is greater that the layout's start char index.
	 */
	public TextLayoutInfo getLayoutInfo(int c){
		TextLayoutInfo tli = null;
		if(!linesInfo.isEmpty()){
			for(int i = linesInfo.size()-1; i >= 0; i--){
				tli = linesInfo.get(i);
				if(tli.startCharIndex < c)
					break;
			}
		}
		return tli;
	}

	
	/**
	 * Create 
	 * @param bw
	 * @return
	 */
	private ImageGraphicAttribute getParagraghSpacer(int bw){
		if(bw == Integer.MAX_VALUE)
			bw = 1;
		BufferedImage img = new BufferedImage(bw, 10, BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();
		g.setColor(new Color(255, 255, 255, 0));
		g.fillRect(0,0,img.getWidth(), img.getHeight());
		return new ImageGraphicAttribute(img, GraphicAttribute.TOP_ALIGNMENT);
	}


	/* %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	 * Serialisation routines to save/restore the StyledString to disc.
	 */

	/**
	 * Save the named StyleString in the named file.
	 * 
	 * @param papp 
	 * @param ss the styled string
	 * @param fname 
	 */
	public static void save(PApplet papp, StyledString ss, String fname){
		OutputStream os;
		ObjectOutputStream oos;
		try {
			os = papp.createOutput(fname);
			oos = new ObjectOutputStream(os);
			oos.writeObject(ss);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static StyledString load(PApplet papp, String fname){
		InputStream is;
		StyledString ss = null;
		ObjectInputStream ios;	
		try {
			is = papp.createInput(fname);
			ios = new ObjectInputStream(is);
			ss = (StyledString) ios.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return ss;
	}


	private void readObject(ObjectInputStream ois)
	throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		// Recreate transient elements
		spacer = getParagraghSpacer((int)breakWidth);
		styledText = new AttributedString(plainText);
		styledText = insertParagraphMarkers(plainText, styledText);
		applyAttributes();
	}

	private String getFamily(String family){
		for(String f : fontFamilies)
			if(f.equalsIgnoreCase(family))
				return f;
		return "";
	}

	/**
	 * For multi-line text, the TextHitInfo class is not enough. We also need 
	 * information about the layout so that the caret(s) can be drawn.
	 * 
	 * @author Peter Lager
	 *
	 */
	static class TextLayoutHitInfo implements Comparable<TextLayoutHitInfo>{
		public final TextLayoutInfo tli;
		public final TextHitInfo thi;
		
		/**
		 * @param tli
		 * @param thi
		 */
		public TextLayoutHitInfo(TextLayoutInfo tli, TextHitInfo thi) {
			this.tli = tli;
			this.thi = thi;
		}
		
		/**
		 * Copy constructor
		 * @param tlhi
		 */
		public TextLayoutHitInfo(TextLayoutHitInfo tlhi){
			tli = tlhi.tli;
			thi = tlhi.thi;
		}

		public int compareTo(TextLayoutHitInfo other) {
			int layoutComparison = tli.compareTo(other.tli);
			if(layoutComparison != 0)
				return layoutComparison; // Different layouts so return comparison
			// Same layout SO test hit info
			if(thi.equals(other.thi))
				return 0;
			// Same layout different hit info SO test char index
			if(thi.getCharIndex() != other.thi.getCharIndex()){
				// Different current chars so order on position
				return (thi.getCharIndex() < other.thi.getCharIndex() ? -1 : 1);
			}
			// Same layout same char different edge hit SO test on edge hit
			return (thi.isLeadingEdge() ? -1 : 1);			
		}
		
		public String toString(){
			StringBuilder s = new StringBuilder("{ Line starts @ " + tli.startCharIndex);
			s.append("");
			return new String(s);
			
		}
	}
	
	/**
	 * Class to hold information about a text layout. This class helps simplify the
	 * algorithms needed for multi-line text.
	 * 
	 * @author Peter Lager
	 *
	 */
	static class TextLayoutInfo implements Comparable<TextLayoutInfo> {
		public final TextLayout layout;		// The matching layout
		public final int startCharIndex;	// The position of the first layout char in text
		public final int nbrChars;			// Number of chars in this layout
		public final float yPosInPara; 		// Top-left corner of bounds
		
		/**
		 * @param startCharIndex
		 * @param nbrChars
		 * @param yPosInPara
		 */
		public TextLayoutInfo(TextLayout layout, int startCharIndex, int nbrChars, float yPosInPara) {
			this.layout  = layout;
			this.startCharIndex = startCharIndex;
			this.nbrChars = nbrChars;
			this.yPosInPara = yPosInPara;
		}

		public int compareTo(TextLayoutInfo other) {
			if(layout == other.layout)
				return 0;
			return (startCharIndex < other.startCharIndex) ? -1 : 1;
		}
	}

	/**
	 * Since most of the Java classes associated with AttributedString 
	 * are immutable with virtually no public methods this class represents
	 * an attribute to be applied. <br>
	 * 
	 * This class is only used from within StyledString.
	 * 
	 * @author Peter Lager
	 *
	 */
	protected class AttributeRun {
		public Attribute atype;
		public Object value;
		public Integer start;
		public Integer end;


		/**
		 * The attribute and value to be applied over the whole string
		 * @param atype
		 * @param value
		 */
		public AttributeRun(Attribute atype, Object value) {
			this.atype = atype;
			this.value = value;
			this.start = Integer.MIN_VALUE; // was -1
			this.end = Integer.MAX_VALUE;
		}

		/**
		 * The attribute and value to be applied over the given range
		 * @param atype
		 * @param value
		 * @param start
		 * @param end
		 */
		public AttributeRun(Attribute atype, Object value, int start, int end) {
			this.atype = atype;
			this.value = value;
			this.start = start;
			this.end = end;
		}

		public boolean sameType(AttributeRun ar){
			return atype == ar.atype;
		}

		public boolean sameValue(AttributeRun ar){
			if(value instanceof java.lang.Float && ar.value instanceof java.lang.Float ){
				return ((Float)value) == ((Float)ar.value);
			}
			if(value instanceof java.lang.Integer && ar.value instanceof java.lang.Integer ){
				return ((Integer)value) == ((Integer)ar.value);
			}
			if(value instanceof java.lang.String && ar.value instanceof java.lang.String ){
				return ((String)value).equalsIgnoreCase((String)ar.value);
			}
			return false;
		}

	}  // End of AttributeRun class

}
