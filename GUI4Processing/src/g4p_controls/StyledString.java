/*
  Part of the GUI for Processing library 
  	http://www.lagers.org.uk/g4p/index.html
	http://gui4processing.googlecode.com/svn/trunk/

  Copyright (c) 2008-13 Peter Lager

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

package g4p_controls;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import java.util.Iterator;
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
 * selected text in GTextField and GTextArea components. <br>
 *  
 *  It is also used for all controls that use text.
 * @author Peter Lager
 *
 */
final public class StyledString implements GConstantsInternal, Serializable {

	private static final long serialVersionUID = 3577177485527475867L;

	transient private AttributedString styledText = null;
	transient private ImageGraphicAttribute spacer = null;
	transient private LineBreakMeasurer lineMeasurer = null;
	transient private LinkedList<TextLayoutInfo> linesInfo = new LinkedList<TextLayoutInfo>();
	transient private Font font = null;

	// The plain text to be styled
	private String plainText = "";
	// List of attribute runs to match font
	private LinkedList<AttributeRun> baseStyle = new LinkedList<AttributeRun>();
	// List of attribute runs to be applied over the base style
	private LinkedList<AttributeRun> atrun = new LinkedList<AttributeRun>();

	// The width to break a line
	private int wrapWidth = Integer.MAX_VALUE;
	// Flag to determine whether the text layouts need recalculating
	private boolean invalidLayout = true;
	// Flag to determine whether the actual character string have changed
	private boolean invalidText = true;

	// Base justification
	private boolean justify = false;
	private float justifyRatio = 0.7f;

	// Stats
	private float textHeight = 0;
	private float maxLineLength = 0;
	private float maxLineHeight = 0;
	private int nbrLines;

	// These are only used by GTextField and GTextArea to store the start and end positions
	// for selected text when the string is to be saved.
	int startIdx = -1;
	int endIdx = -1;

	/**
	 * This is assumed to be a single line of text (i.e. no wrap). 
	 * EOL characters will be stripped from the text before use.
	 * 
	 * @param startText
	 */
	public StyledString(String startText){
		plainText = removeSingleSpacingFromPlainText(startText);
		spacer = getParagraghSpacer(1); //  safety
		// Get rid of any EOLs
		styledText = new AttributedString(plainText);
		applyAttributes();
		invalidText = true;
		invalidLayout = true;
	}

	/**
	 * Supports multiple lines of text wrapped on word boundaries. <br>
	 * 
	 * @param startText
	 * @param wrapWidth
	 */
	public StyledString(String startText, int wrapWidth){
		if(wrapWidth > 0 && wrapWidth < Integer.MAX_VALUE)
			this.wrapWidth = wrapWidth;
		plainText = (wrapWidth == Integer.MAX_VALUE) ? removeSingleSpacingFromPlainText(startText) : removeDoubleSpacingFromPlainText(startText);
		spacer = getParagraghSpacer(this.wrapWidth);
		styledText = new AttributedString(plainText);
		styledText = insertParagraphMarkers(plainText, styledText);
		applyAttributes();
		invalidText = true;
		invalidLayout = true;
	}

	/**
	 * Converts this StyledString from multi-line to single-line by replacing all EOL
	 * characters with the space character
	 * for paragraphs
	 * @param ptext
	 * @param as
	 * @return the converted string
	 */
	StyledString convertToSingleLineText(){
		// Make sure we have something to work with.
		if(styledText == null || plainText == null){
			plainText = "";
			styledText = new AttributedString(plainText);
		}
		else {
			// Scan through plain text and for each EOL replace the paragraph spacer from
			// the attributed string (styledText).
			int fromIndex = plainText.indexOf('\n', 0);
			if(fromIndex >= 0){
				while(fromIndex >= 0){
					try { // if text == "\n" then an exception is thrown
						styledText.addAttribute(TextAttribute.CHAR_REPLACEMENT, ' ', fromIndex, fromIndex + 1);
						fromIndex = plainText.indexOf('\n', fromIndex + 1);
					}
					catch(Exception excp){
						break;
					}
				}
				// Finally replace all EOL in the plainText
				plainText = plainText.replace('\n', ' ');
			}
		}
		wrapWidth = Integer.MAX_VALUE;
		return this;
	}

	/**
	 * Get the plain text as a String. Any line breaks will kept and will
	 * be represented by the character 'backslash n' <br>
	 * @return the associated plain text
	 */
	public String getPlainText(){
		return plainText;
	}

	/**
	 * Get the number of characters in this styled string
	 */
	public int length(){
		return plainText.length();
	}

	/*
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
	 * @return the styled string with paragraph marker images embedded
	 */
	private AttributedString insertParagraphMarkers(String ptext, AttributedString as ){
		if(ptext != null && ptext.length() > 0)
			plainText = ptext;
		int fromIndex = ptext.indexOf('\n', 0);
		while(fromIndex >= 0){
			try { // if text == "\n" then an exception is thrown
				as.addAttribute(TextAttribute.CHAR_REPLACEMENT, spacer, fromIndex, fromIndex + 1);
				fromIndex = ptext.indexOf('\n', fromIndex + 1);
			}
			catch(Exception excp){
				break;
			}
		}
		return as;
	}

	/**
	 * Add an attribute that affects the whole length of the string.
	 * @param type attribute type
	 * @param value attribute value
	 */
	public void addAttribute(Attribute type, Object value){
		addAttribute(type, value, Integer.MIN_VALUE, Integer.MAX_VALUE);
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
		// If we already have attributes try and rationalise the number by merging
		// runs if possible and removing runs that no longer have a visible effect.
		if(atrun.size() > 0){
			ListIterator<AttributeRun> iter = atrun.listIterator(atrun.size());
			while(iter.hasPrevious()){
				AttributeRun a = iter.previous();
				int action = ar.intersectionWith(a);
				int intersect = action & I_MODES;
				int combiMode = action & COMBI_MODES;
				if(combiMode == MERGE_RUNS){
					switch(intersect){
					case I_TL:
					case I_CL:
							ar.start = a.start;
							iter.remove();
						break;
					case I_TR:
					case I_CR:
							ar.end = a.end;
							iter.remove();
						break;
					}
				}
				else if(combiMode == CLIP_RUN){
					switch(intersect){
					case I_CL:
						a.end = ar.start;
						break;
					case I_CR:
						a.start = ar.end;
						break;
					}
				}
				switch(intersect){
				case I_INSIDE:
					iter.remove();
					break;
				case I_COVERED:
					ar = null;
					break;				
				}
			}
		}
		// If the run is still effective then add it
		if(ar != null)
			atrun.addLast(ar);
		applyAttributes();
		invalidLayout = true;
	}

	
	/**
	 * Must call this method to apply
	 */
	private void applyAttributes(){
		if(plainText.length() > 0){
			for(AttributeRun bsar : baseStyle)
				styledText.addAttribute(bsar.atype, bsar.value);
			Iterator<AttributeRun> iter = atrun.iterator();
			AttributeRun ar;
			while(iter.hasNext()){
				ar = iter.next();
				if(ar.end == Integer.MAX_VALUE)
					styledText.addAttribute(ar.atype, ar.value);
				else {
					// If an attribute run fails do not try and fix it - dump it
					try {
							styledText.addAttribute(ar.atype, ar.value, ar.start, ar.end);
					}
					catch(Exception excp){
						System.out.println("Dumping " + ar);
						iter.remove();
					}
				}
			}
		}
		invalidLayout = true;
	}

	/**
	 * Clears all attributes from start to end-1
	 * @param start
	 * @param end
	 */
	public void clearAttributes(int start, int end){
		ListIterator<AttributeRun> iter = atrun.listIterator();
		AttributeRun ar;
		while(iter.hasNext()){
			ar = iter.next();
			// Make sure we have intersection
			if( !(start >= ar.end && end >= ar.start )){
				// Find the limits to clear
				int s = Math.max(start, ar.start);
				int e = Math.min(end, ar.end);
				if(ar.start == s && ar.end == e)
					iter.remove();
				else if(ar.start == s) // clear style from beginning
					ar.start = e;
				else if(ar.end == e) // clear style from end
					ar.end = s;
				else {	// Split attribute run
					AttributeRun ar2 = new AttributeRun(ar.atype, ar.value, e, ar.end);
					iter.add(ar2);
					ar.end = s;
				}
			}
		}
		invalidText = true;
	}
	
	/**
	 * Removes all styling from the string.
	 * 
	 */
	public void clearAllAttributes(){
		atrun.clear();
		invalidText = true;
	}

	/**
	 * Insert 1 or more characters into the string. The inserted text will first be made
	 * safe by removing any inappropriate EOL characters. <br>
	 * Do not use this method to insert EOL characters, use the <pre>insertEOL(int)</pre>
	 * method instead.
	 * 
	 * @param chars
	 * @param insertPos the position in the text
	 */
	public int insertCharacters(int insertPos, String chars){
		if(chars.length() > 0)
			chars = makeStringSafeForInsert(chars);
		return insertCharactersImpl(insertPos, chars, false);
//		if(chars.length() > 0){
//			int nbrChars = chars.length();
//			if(plainText.equals(" "))
//				plainText = chars;
//			else
//				plainText = plainText.substring(0, insertPos) + chars + plainText.substring(insertPos);
//			insertParagraphMarkers(plainText, styledText);
//			for(AttributeRun ar : atrun){
//				if(ar.end < Integer.MAX_VALUE){
//					if(ar.end >= insertPos){
//						ar.end += nbrChars;
//						if(ar.start >= insertPos)
//							ar.start += nbrChars;
//					}
//				}
//			}
//			invalidText = true;
//		}
//		return chars.length();
	}

	public int insertCharacters(int insertPos, String chars, boolean startNewLine){
		if(chars.length() > 0)
			chars = makeStringSafeForInsert(chars);
		return insertCharactersImpl(insertPos, chars, startNewLine);
	}
	
	private int insertCharactersImpl(int insertPos, String chars, boolean startNewLine){
		if(chars.length() > 0){
			if(startNewLine)
				chars = "\n" + chars;
			int nbrChars = chars.length();
			if(plainText.equals(" "))
				plainText = chars;
			else
				plainText = plainText.substring(0, insertPos) + chars + plainText.substring(insertPos);
			insertParagraphMarkers(plainText, styledText);
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
		}
		return chars.length();		
	}
	
	/**
	 * This is ONLY used when multiple characters are to be inserted. <br>
	 * If it is single line text i.e. no wrapping then it removes all EOLs
	 * If it is multiple line spacing it will reduce all double EOLs to single
	 * EOLs and remove any EOLs at the start or end of the string.
	 * 
	 * @param chars
	 * @return a string that is safe for inserting
	 */
	private String makeStringSafeForInsert(String chars){
		// Get rid of single / double line spacing
		if(chars.length() > 0){
			if(wrapWidth == Integer.MAX_VALUE) // no wrapping remove all
				chars = removeSingleSpacingFromPlainText(chars);
			else {
				chars = removeDoubleSpacingFromPlainText(chars); // wrapping remove double spacing
				// no remove EOL at ends of string
				while(chars.length() > 0 && chars.charAt(0) == '\n')
					chars = chars.substring(1);
				while(chars.length() > 0 && chars.charAt(chars.length() - 1) == '\n')
					chars = chars.substring(0, chars.length() - 1);
			}
		}
		return chars;
	}
	
	/**
	 * Use this method to insert an EOL character.
	 * @param insertPos index position to insert EOL
	 * @return true if an EOL was inserted into the string
	 */
	public boolean insertEOL(int insertPos){
		if(wrapWidth != Integer.MAX_VALUE){
			if(insertPos > 0 && plainText.charAt(insertPos-1) == '\n')
				return false;
			if(insertPos < plainText.length()-1 && plainText.charAt(insertPos+1) == '\n'){
				return false;
			}
			plainText = plainText.substring(0, insertPos) + "\n" + plainText.substring(insertPos);
			insertParagraphMarkers(plainText, styledText);
			for(AttributeRun ar : atrun){
				if(ar.end < Integer.MAX_VALUE){
					if(ar.end >= insertPos){
						ar.end += 1;
						if(ar.start >= insertPos)
							ar.start += 1;
					}
				}
			}
			invalidText = true;
			return true;
		}
		return false;
	}
	
	
	/**
	 * Remove a number of characters from the string
	 * 
	 * @param nbrToRemove number of characters to remove
	 * @param fromPos start location for removal
	 * @return true if the deletion was successful else false
	 */
	public boolean deleteCharacters(int fromPos, int nbrToRemove){
		if(fromPos < 0 || fromPos + nbrToRemove > plainText.length())
			return false;
		/*
		 * If the character preceding the selection and the character immediately after the selection
		 * are both EOLs then increment the number of characters to be deleted
		 */
		if(wrapWidth != Integer.MAX_VALUE){
			if(fromPos > 0 && fromPos + nbrToRemove < plainText.length() - 1){
				if(plainText.charAt(fromPos) == '\n' && plainText.charAt(fromPos + nbrToRemove) == '\n'){
					nbrToRemove++;
				}
			}
		}
		if(fromPos != 0)
			plainText = plainText.substring(0, fromPos) + plainText.substring(fromPos + nbrToRemove);
		else
			plainText = plainText.substring(fromPos + nbrToRemove);
		// For wrappable text make sure we have not created
		if(plainText.length() == 0){
			atrun.clear();
			styledText = null;
		}
		else {
			ListIterator<AttributeRun> iter = atrun.listIterator(atrun.size());
			AttributeRun ar;
			while(iter.hasPrevious()){
				ar = iter.previous();
				if(ar.end < Integer.MAX_VALUE){
					// Only need to worry about this if the run ends after the deletion point
					if(ar.end >= fromPos){
						int lastPos = fromPos + nbrToRemove;
						// Deletion removes entire run
						if(fromPos <= ar.start && lastPos >= ar.end){
							iter.remove();
							continue;
						}
						// Deletion fits entirely within the run
						if(fromPos > ar.start && lastPos < ar.end){
							ar.end -= nbrToRemove;
							continue;
						}
						// Now we have overlap either at one end of the run
						// Overlap at start of run?
						if(fromPos <= ar.start){
							ar.start = fromPos;
							ar.end -= nbrToRemove;
							continue;
						}
						// Overlap at end of run?
						if(lastPos >= ar.end){
							ar.end = fromPos;
							continue;
						}
						System.out.println("This run was not modified");
						System.out.println("Run from " + ar.start + " to " + ar.end);
						System.out.println("Delete from " + fromPos + " To " + lastPos + "  (" + nbrToRemove + " to remove)");
					}
				}		
			}
		}
		invalidText = true;
		return true;
	}

	public void setFont(Font a_font){
		if(a_font != null){
			font = a_font;
			baseStyle.clear();
			baseStyle.add(new AttributeRun(TextAttribute.FAMILY, font.getFamily()));
			baseStyle.add(new AttributeRun(TextAttribute.SIZE, font.getSize()));
			if(font.isBold())
				baseStyle.add(new AttributeRun(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD));
			if(font.isItalic())
				baseStyle.add(new AttributeRun(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE));	
			invalidText = true;
		}
	}

	/**
	 * Get the text layouts for display if the string has changed since last call
	 * to this method regenerate them.
	 * 
	 * @param g2d Graphics2D display context
	 * @return a list of text layouts for rendering
	 */
	public LinkedList<TextLayoutInfo> getLines(Graphics2D g2d){
		if(font != g2d.getFont()){
			setFont(g2d.getFont());
			invalidText = true;
		}
		if(invalidText){
			styledText = new AttributedString(plainText);
			styledText = insertParagraphMarkers(plainText, styledText);
			applyAttributes();
			invalidText = false;
			invalidLayout = true;
		}
		if(invalidLayout){
			linesInfo.clear();
			if(plainText.length() > 0){
				textHeight = 0;
				maxLineLength = 0;
				maxLineHeight = 0;
				nbrLines = 0;
				AttributedCharacterIterator paragraph = styledText.getIterator(null, 0, plainText.length());
				FontRenderContext frc = g2d.getFontRenderContext();
				lineMeasurer = new LineBreakMeasurer(paragraph, frc);
				float yposinpara = 0;
				int charssofar = 0;
				while (lineMeasurer.getPosition() < plainText.length()) {
					TextLayout layout = lineMeasurer.nextLayout(wrapWidth);
					float advance = layout.getVisibleAdvance();
					if(justify){
						if(justify && advance > justifyRatio * wrapWidth){
							//System.out.println(layout.getVisibleAdvance() + "  " + breakWidth + "  "+ layout.get);
							// If advance > breakWidth then we have a line break
							float jw = (advance > wrapWidth) ? advance - wrapWidth : wrapWidth;
							layout = layout.getJustifiedLayout(jw);
						}
					}
					// Remember the longest and tallest value for a layout so far.
					float lh = getHeight(layout);
					if(lh > maxLineHeight)
						maxLineHeight = lh;
					textHeight += lh;
					if(advance <= wrapWidth && advance > maxLineLength)
						maxLineLength = advance;

					// Store layout and line info
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
	 * Get the number of lines in the layout
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
	 * @return the height of a given text layout
	 */
	private float getHeight(TextLayout layout){
		return layout.getAscent() +layout.getDescent() + layout.getLeading();
	}

	/**
	 * Get the break width used to create the lines.
	 */
	public int getWrapWidth(){
		return wrapWidth;
	}

	/**
	 * Set the maximum width of a line. 
	 * @param wrapWidth
	 */
	public void setWrapWidth(int wrapWidth){
		if(this.wrapWidth != wrapWidth){
			this.wrapWidth = wrapWidth;
			invalidLayout = true;
		}
	}

	TextLayoutHitInfo calculateFromXY(Graphics2D g2d, float px, float py){
		TextHitInfo thi = null;
		TextLayoutInfo tli = null;
		TextLayoutHitInfo tlhi = null;
		if(invalidLayout)
			getLines(g2d);
		if(px < 0) px = 0;
		if(py < 0) py = 0;
		tli = getTLIforYpos(py);
		// Correct py to match layout's upper-left bounds
		py -= tli.yPosInPara;
		// get hit
		thi = tli.layout.hitTestChar(px,py);
		tlhi = new TextLayoutHitInfo(tli, thi);
		return tlhi;
	}

	/**
	 * Get a layout based on line number
	 * @param ln line number 
	 * @return text layout info for the line ln
	 */
	TextLayoutInfo getTLIforLineNo(int ln){
		return linesInfo.get(ln);
	}

	/**
	 * This will always return a layout provide there is some text.
	 * @param y Must be >= 0
	 * @return the first layout where y is above the upper layout bounds
	 */
	TextLayoutInfo getTLIforYpos(float y){
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
	 * This will always return a layout provided charNo >= 0. <br>
	 * 
	 * If charNo > than the index of the last character in the plain text then this
	 * should be corrected to the last character in the layout by the caller.
	 * 
	 * @param charNo the character position in text (must be >= 0)
	 * @return the first layout where c is greater that the layout's start char index.
	 */
	TextLayoutInfo getTLIforCharNo(int charNo){
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
	 * single EOL until there are only single EOLs. <br>
	 * Using replaceAll on its own will not work because EOL/EOL/EOL would 
	 * become EOL/EOL not the single EOL required.
	 * 
	 */
	private String removeDoubleSpacingFromPlainText(String chars){
		while(chars.indexOf("\n\n") >= 0){
			invalidText = true;
			chars = chars.replaceAll("\n\n", "\n");
		}
		return chars;
	}

	/**
	 * Remove all EOL characters from the string. This is necessary if the string
	 * is for a single line component.
	 * @param chars the string to use
	 * @return the string with all EOLs removed
	 */
	private String removeSingleSpacingFromPlainText(String chars){
		while(chars.indexOf("\n") >= 0){
			invalidText = true;
			chars = chars.replaceAll("\n", "");
		}
		return chars;
	}
	
	/**
	 * Create a graphic image character to simulate paragraph breaks
	 * 
	 * @param ww
	 * @return a blank image to manage paragraph ends.
	 */
	private ImageGraphicAttribute getParagraghSpacer(int ww){
		if(ww == Integer.MAX_VALUE)
			ww = 1;
		BufferedImage img = new BufferedImage(ww, 10, BufferedImage.TYPE_INT_ARGB);
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
			os.close();
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load and return a StyledString object from the given file.
	 * 
	 * @param papp
	 * @param fname the filename of the StyledString
	 */
	public static StyledString load(PApplet papp, String fname){
		StyledString ss = null;
		InputStream is;
		ObjectInputStream ios;	
		try {
			is = papp.createInput(fname);
			ios = new ObjectInputStream(is);
			ss = (StyledString) ios.readObject();
			is.close();
			ios.close();
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
		spacer = getParagraghSpacer(wrapWidth);
		styledText = new AttributedString(plainText);
		styledText = insertParagraphMarkers(plainText, styledText);
		linesInfo = new LinkedList<TextLayoutInfo>();
		applyAttributes();
	}

	/**
	 * For multi-line text, the TextHitInfo class is not enough. We also need 
	 * information about the layout so that the caret(s) can be drawn.
	 * 
	 * @author Peter Lager
	 *
	 */
	static public class TextLayoutHitInfo implements Comparable<TextLayoutHitInfo>{
		public TextLayoutInfo tli;
		public TextHitInfo thi;

		
		public TextLayoutHitInfo() {
			this.tli = null;
			this.thi = null;
		}
		
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

		public void setInfo(TextLayoutInfo tli, TextHitInfo thi) {
			this.tli = tli;
			this.thi = thi;
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
			StringBuilder s = new StringBuilder(tli.toString());
			s.append("  Hit char = " + thi.getCharIndex());
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
	static public class TextLayoutInfo implements Comparable<TextLayoutInfo> {
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
			if(lineNo == other.lineNo)
				return 0;
			return (startCharIndex < other.startCharIndex) ? -1 : 1;
		}

		public String toString(){
			StringBuilder s = new StringBuilder("{ Line no = " + lineNo + "    starts @ char pos " + startCharIndex);
			s.append("  last index " + (startCharIndex+nbrChars+1));
			s.append("  (" + nbrChars +")  ");
			return new String(s);
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
	private class AttributeRun implements Serializable {

		private static final long serialVersionUID = -8401062069478890163L;
		
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
			this.start = Integer.MIN_VALUE;
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

		/**
		 * If possible merge the two runs or crop the prevRun run.
		 * 
		 * If both runs have the same attribute type and the represent
		 * the same location and size in the text then the intersection
		 * mode will be MM_SURROUNDS rather than MM_SURROUNDED because 
		 * 'this' is the attribute being added.
		 * @param m
		 * @param s
		 * @return
		 */
		private int intersectionWith(AttributeRun ar){
			// Different attribute types?
			if(atype != ar.atype)
				return I_NONE;
			// Check for combination mode
			int combi_mode = (value.equals(ar.value)) ? MERGE_RUNS : CLIP_RUN;
			int sdx = 4, edx = 0;
			// Start index
			if(ar.start < start)
				sdx = 0;
			else if(ar.start == start)
				sdx = 1;
			else if (ar.start < end)
				sdx = 2;
			else if(ar.start == end)
				sdx = 3;
			if(sdx < 4){
				if(ar.end > end)
					edx = 4;
				else if(ar.end == end)
					edx = 3;
				else if(ar.end > start)
					edx = 2;
				else if(ar.end == start)
					edx = 1;
			}
			combi_mode |= grid[sdx][edx];
			return combi_mode;
		}
		
		public String toString(){
			String s = atype.toString() + "  value = " + value.toString() + "  from " + start + "   to " + end;
			return s;
		}

	}  // End of AttributeRun class

}
