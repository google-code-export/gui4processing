package guicomponents;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GraphicAttribute;
import java.awt.font.ImageGraphicAttribute;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
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

final public class StyledString implements Serializable {

	transient final public static String[] fontFamilies = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

	transient private AttributedString styledText = null;
	transient private ImageGraphicAttribute spacer = null;
	transient private Location cursorPos = new Location();
	transient private LineBreakMeasurer lineMeasurer = null;
	transient private LinkedList<TextLayout> lines = new LinkedList<TextLayout>();

	// The plain text to be styled
	private String plainText = "";
	// List of attribute runs still active and waiting to be applied
	private LinkedList<AttributeRun> atrun = new LinkedList<AttributeRun>();
	// The width to break a line
	private float breakWidth = 100;
	// Flag to determine whether the text layouts need recalculating
	private boolean invalidLayout = true;

	// Default style used for base
	private final String family = "Dialog";
	private final float posture = TextAttribute.POSTURE_REGULAR;
	private final float size = 11.0f;
	// Default colours
	private final Color backcolor = new Color(255,255,255,0);
	private final Color forecolor = Color.black;
	private final Color cursorColor = Color.red;
	// Default justification
	private boolean justify = true;
	private float justifyRatio = 0.7f;


	private boolean showCursor = false;


	public StyledString(Graphics2D g2d, String startText, float viewWidth){
		if(viewWidth < 0 || viewWidth == Integer.MAX_VALUE)
			breakWidth = Integer.MAX_VALUE;
		else
			breakWidth = viewWidth;
		spacer = getParagraghSpacer((int)breakWidth);
		cursorPos = new Location();
		plainText = startText;
		styledText = new AttributedString(plainText);
		styledText = insertParagraphMarkers(plainText, styledText);
		resetStyles();
		lines = getLines(g2d);
	}

	public String getPlainText(){
		return new String(plainText);
	}
	
	public int length(){
		return plainText.length();
	}
	
	public void setForeground(Color c, int charStart, int charEnd){
		addStyle(TextAttribute.FOREGROUND, c, charStart, charEnd);
	}

	public void setForeground(Color c){
		setForeground(c, -1, Integer.MAX_VALUE);
	}

	public void setBackground(Color c, int charStart, int charEnd){
		addStyle(TextAttribute.BACKGROUND, c, charStart, charEnd);
	}

	public void setBackground(Color c){
		setBackground(c, -1, Integer.MAX_VALUE);
	}

	public void setFontSize(float size, int charStart, int charEnd){
		size = Math.max(8, size);
		addStyle(TextAttribute.SIZE, size, charStart, charEnd);
		invalidLayout = true;
	}

	public void setFontSize(float size){
		setFontSize(size, -1, Integer.MAX_VALUE);
	}
	
	public void setJustifyOn(){
		if(!justify){
			invalidLayout = true;
			justify = true;
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
		atrun.clear();
		atrun.add(new AttributeRun(TextAttribute.FAMILY, family));
		atrun.add(new AttributeRun(TextAttribute.SIZE, size));
		atrun.add(new AttributeRun(TextAttribute.POSTURE, posture));
		atrun.add(new AttributeRun(TextAttribute.JUSTIFICATION, TextAttribute.JUSTIFICATION_FULL));
		atrun.add(new AttributeRun(TextAttribute.BACKGROUND, backcolor));
		atrun.add(new AttributeRun(TextAttribute.FOREGROUND, forecolor));
		applyStyles();
	}

	/**
	 * Set charStart and charEnd to <0 if full length.
	 * 
	 * @param type
	 * @param value
	 * @param charStart
	 * @param charEnd
	 */
	public void addStyle(Attribute type, Object value, int charStart, int charEnd){
		AttributeRun ar = new AttributeRun(type, value, charStart, charEnd);
		atrun.addLast(ar);
		applyStyles();
	}

	public void addStyle(Attribute type, Object value){
		addStyle(type, value, -1, Integer.MAX_VALUE);
	}

	private void applyStyles(){
		if(plainText.length() > 0){
			for(AttributeRun ar : atrun){
				if(ar.end == Integer.MAX_VALUE)
					styledText.addAttribute(ar.atype, ar.value);
				else
					styledText.addAttribute(ar.atype, ar.value, ar.start, ar.end);
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
		applyStyles();
	}
	
	/**
	 * Remolve a number of characters from the string
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
		applyStyles();
//		System.out.println("Number of attribute runs = " + atrun.size() );
		return true;
	}
	
	/**
	 * Get the text layouts for display if the string has changed since last call
	 * to this method regenerate them.
	 * 
	 * @param g2d Graphics2D display context
	 * @return
	 */
	public LinkedList<TextLayout> getLines(Graphics2D g2d){
		if(lines == null)
			lines = new LinkedList<TextLayout>();
		if(invalidLayout){
			lines.clear();
			if(plainText.length() > 0){
				int nbrChars = plainText.length();
				AttributedCharacterIterator paragraph = styledText.getIterator(null, 0, nbrChars);
				FontRenderContext frc = g2d.getFontRenderContext();
				lineMeasurer = new LineBreakMeasurer(paragraph, frc);		
				while (lineMeasurer.getPosition() < nbrChars) {
					TextLayout layout = lineMeasurer.nextLayout(breakWidth);
					if(justify){
						float advance = layout.getVisibleAdvance();
						if(justify && advance > justifyRatio * breakWidth){
							//System.out.println(layout.getVisibleAdvance() + "  " + breakWidth + "  "+ layout.get);
							// If advance > breakWidth then we have a line break
							float jw = (advance > breakWidth) ? advance - breakWidth : breakWidth;
							layout = layout.getJustifiedLayout(jw);
						}
					}
					lines.add(layout);
				}
			}
			invalidLayout = false;
		}
		return lines;
	}

	public void drawText(Graphics2D g2d){
		if(invalidLayout)
			lines = getLines(g2d);

		float drawPosX = 0, drawPosY = 0;
		//g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		for(TextLayout layout : lines){
			// Compute pen x position. If the paragraph is right-to-left we
			// will align the TextLayouts to the right edge of the panel.
			// Note: this won't occur for the English text in this sample.
			// Note: drawPosX is always where the LEFT of the text is placed.
			drawPosX = (layout.isLeftToRight() ? 0 : breakWidth - layout.getAdvance());

			// Move y-coordinate by the ascent of the layout.
			drawPosY += layout.getAscent();

			// Draw the TextLayout at (drawPosX, drawPosY).
			layout.draw(g2d, drawPosX, drawPosY);

			// Move y-coordinate in preparation for next layout.
			drawPosY += layout.getDescent() + layout.getLeading();
		}
		if(showCursor){
			g2d.setColor(cursorColor);
			g2d.drawLine((int)cursorPos.cursorX, (int)cursorPos.cursorY, (int)cursorPos.cursorX, (int)(cursorPos.cursorY - cursorPos.cursorHeight));
		}
	}

	public boolean getCursorPos(Graphics2D g2d, Location cursorPos, float px, float py){
		if(cursorPos == null)
			cursorPos = new Location();
		if(invalidLayout)
			lines = getLines(g2d);
		showCursor = false;
		if(cursorPos.calculateFromXY(px, py, breakWidth, lines)){
			System.out.println(cursorPos);
			showCursor = true;
		}
		this.cursorPos = cursorPos;
		return showCursor;
	}

	@SuppressWarnings("unused")
	private float getHeight(TextLayout layout){
		return layout.getAscent() +layout.getDescent() + layout.getLeading();
	}

	private ImageGraphicAttribute getParagraghSpacer(int bw){
		if(bw == Integer.MAX_VALUE)
			bw = 1;
		BufferedImage img = new BufferedImage(bw, 10, BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();
		g.setColor(new Color(255, 255, 255, 0));
		g.fillRect(0,0,img.getWidth(), img.getHeight());
		return new ImageGraphicAttribute(img, GraphicAttribute.TOP_ALIGNMENT);
	}

	public static void save(PApplet papp, StyledString sp, String fname){
		OutputStream os;
		ObjectOutputStream oos;
		try {
			os = papp.createOutput(fname);
			oos = new ObjectOutputStream(os);
			oos.writeObject(sp);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static StyledString load(PApplet papp, String fname){
		InputStream is;
		StyledString sp = null;
		ObjectInputStream ios;	
		try {
			is = papp.createInput(fname);
			ios = new ObjectInputStream(is);
			sp = (StyledString) ios.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return sp;
	}


	private void readObject(ObjectInputStream ois)
	throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		// Recreate transient elements
		spacer = getParagraghSpacer((int)breakWidth);
		styledText = new AttributedString(plainText);
		styledText = insertParagraphMarkers(plainText, styledText);
		applyStyles();
	}

	/**
	 * Since most of the Java classes associated with AttributedString 
	 * are immutable with virtually no public methods this class was 
	 * to store all styles applied.
	 * 
	 * @author Peter Lager
	 *
	 */
	private class AttributeRun implements Serializable {
		public Attribute atype;
		public Object value;
		public Integer start;
		public Integer end;


		/**
		 * @param atype
		 * @param value
		 * @param start
		 * @param end
		 */
		public AttributeRun(Attribute atype, Object value) {
			this.atype = atype;
			this.value = value;
			this.start = Integer.MIN_VALUE; // was -1
			this.end = Integer.MAX_VALUE;
		}

		/**
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
