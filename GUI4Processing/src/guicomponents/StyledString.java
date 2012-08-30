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

	// The width to break a line
	private float breakWidth = 100;
	// Flag to determine whether the text layouts need recalculating
	private boolean invalidLayout = true;
	// Flag to determine whether the actual character string have changed
	private boolean invalidText = true;
	
	// Base style used for base
	private String family = "Dialog";
	private float posture = TextAttribute.POSTURE_REGULAR;
	private float size = 16.0f;
	// Base colours
	private final Color backcolor = new Color(255,255,255,0);
	private Color forecolor = Color.black;
	// Base justification
	private boolean justify = false;
	private float justifyRatio = 0.7f;

	// Stats
	private float textHeight = 0;
	private float maxLineLength = 0;
	private float maxLineHeight = 0;
	private int nbrLines;

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
		}
		invalidLayout = true;
	}

	/**
	 * 
	 * @param chars
	 * @param insertPos the position in the text
	 */
	public boolean insertCharacters(int insertPos, String chars){
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
		invalidText = true;

//		styledText = new AttributedString(plainText);
//		styledText = insertParagraphMarkers(plainText, styledText);
//		applyAttributes();
		return true;
	}

	/**
	 * Remove a number of characters from the string
	 * 
	 * @param nbrToRemove number of characters to remove
	 * @param fromPos start location for removal
	 * @return
	 */
	public boolean deleteCharacters(int fromPos, int nbrToRemove){
		if(fromPos < 0 || fromPos + nbrToRemove >= plainText.length())
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
		invalidText = true;
//		styledText = new AttributedString(plainText);
//		styledText = insertParagraphMarkers(plainText, styledText);
//		applyAttributes();
		return true;
	}


	public LinkedList<TextLayoutInfo> getUpdatedLines(Graphics2D g2d){
		invalidLayout = true;
		return getLines(g2d);
	}

	/**
	 * Get the text layouts for display if the string has changed since last call
	 * to this method regenerate them.
	 * 
	 * @param g2d Graphics2D display context
	 * @return
	 */
	public LinkedList<TextLayoutInfo> getLines(Graphics2D g2d){
		if(invalidText){
			styledText = new AttributedString(plainText);
			styledText = insertParagraphMarkers(plainText, styledText);
			applyAttributes();
			invalidLayout = true;
		}
		if(linesInfo == null)
			linesInfo = new LinkedList<TextLayoutInfo>();
		if(invalidLayout){
			textHeight = 0;
			maxLineLength = 0;
			maxLineHeight = 0;
			nbrLines = 0;
			linesInfo.clear();
			if(plainText.length() > 0){
				AttributedCharacterIterator paragraph = styledText.getIterator(null, 0, plainText.length());
				FontRenderContext frc = g2d.getFontRenderContext();
				lineMeasurer = new LineBreakMeasurer(paragraph, frc);
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
					linesInfo.add(new TextLayoutInfo(nbrLines, layout, charssofar, layout.getCharacterCount(), yposinpara));
					charssofar += layout.getCharacterCount();
					yposinpara += lh;
					nbrLines++;
				}
			}
			invalidLayout = false;
		}
		return linesInfo;
	}

	/**
	 * Return the number of lines in the layout
	 * @return
	 */
	public int getNbrLines(){
		return nbrLines;
	}
	
	/**
	 * Return the height of the text line(s)
	 */
	public float getTextAreaHeight(){
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

	public TextLayoutHitInfo calculateFromXY(Graphics2D g2d, float px, float py){
		TextHitInfo thi = null;
		TextLayoutInfo tli = null;
		TextLayoutHitInfo tlhi = null;
		if(invalidLayout)
			getLines(g2d);
		if(px < 0) px = 0;
		if(py < 0) py = 0;
		tli = getLayoutFromYpos(py);
		// Correct py to match layout's upper-left bounds
		py -= tli.yPosInPara;
		// get hit
		thi = tli.layout.hitTestChar(px,py);
		tlhi = new TextLayoutHitInfo(tli, thi);
		return tlhi;
	}

	/**
	 * Get a layout based on line number
	 * @param ln
	 * @return
	 */
	public TextLayoutInfo getTLIforLineNo(int ln){
		return linesInfo.get(ln);
	}
	
	/**
	 * This will always return a layout.
	 * @param y Must be >= 0
	 * @return the first layout where y is above the upper layout bounds
	 */
	protected TextLayoutInfo getLayoutFromYpos(float y){
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
	 * @param charNo the character position in text (must be >= 0
	 * @return the first layout where c is greater that the layout's start char index.
	 */
	public TextLayoutInfo getTLIforCharNo(int charNo){
		TextLayoutInfo tli = null;
		if(!linesInfo.isEmpty()){
			for(int i = linesInfo.size()-1; i >= 0; i--){
				tli = linesInfo.get(i);
				if(tli.startCharIndex < charNo)
					break;
			}
		}
		return tli;
	}

	/** 
	 * Ensure we do not have blank lines by replacing double EOL characters by 
	 * single EOL until there are only single EOLs.
	 */
	void removeBlankLines(){
		while(plainText.indexOf("\n\n") >= 0){
			invalidText = true;
			plainText = plainText.replaceAll("\n\n", "\n");
		}
	}
	
	/**
	 * Create a graphic image charecter to simulate paragraphs
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
		public TextLayoutInfo tli;
		public TextHitInfo thi;
		
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

		public void copyFrom(TextLayoutHitInfo other){
			this.tli = other.tli;
			this.thi = other.thi;
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
		public TextLayout layout;		// The matching layout
		public int lineNo;				// The line number
		public int startCharIndex;		// Position of the first char in text
		public int nbrChars;			// Number of chars in this layout
		public float yPosInPara; 		// Top-left corner of bounds
		
		/**
		 * @param startCharIndex
		 * @param nbrChars
		 * @param yPosInPara
		 */
		public TextLayoutInfo(int lineNo, TextLayout layout, int startCharIndex, int nbrChars, float yPosInPara) {
			this.lineNo = lineNo;
			this.layout  = layout;
			this.startCharIndex = startCharIndex;
			this.nbrChars = nbrChars;
			this.yPosInPara = yPosInPara;
		}

		public int compareTo(TextLayoutInfo other) {
			//if(layout == other.layout)
			if(lineNo == other.lineNo)
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
