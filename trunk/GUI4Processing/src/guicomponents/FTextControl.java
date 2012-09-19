package guicomponents;
import java.awt.Font;

import processing.core.PApplet;


public class FTextControl extends FAbstractControl implements IText {

	protected static final int TPAD = 2;
	protected static final int TPAD2 = TPAD * 2;
	
	/** Text value associated with component */
	protected String text = "";
	// The styled version of text
	protected StyledString stext = null;

	protected int textAlign = GAlign.CENTER | GAlign.MIDDLE;

	protected Font localFont = F4P.globalFont;
	

	public FTextControl(PApplet theApplet, float p0, float p1, float p2, float p3) {
		super(theApplet, p0, p1, p2, p3);
	}

	public FTextControl setTextAlignNew(int align){
		if(align != textAlign){
			stext = new StyledString(text, (int) width - TPAD2);
			stext.setJustify((align & GAlign.H_ALIGN) == GAlign.JUSTIFY);
			textAlign = align;			
		}
		return this;
	}
	
	/**
	 * Set the text to be displayed.
	 * NEW version for FPanel etc.
	 * @param text
	 */
	public FTextControl setTextNew(String ntext){
		if(ntext == null || ntext.length() == 0 )
			text = ntext;
		else 
			text = "???";
		stext = new StyledString(text);
		bufferInvalid = true;
		return this;
	}
	
	public FTextControl setTextNew(String ntext, int wrapWidth){
		setTextNew(ntext, wrapWidth, false);
		return this;
	}
	
	public FTextControl setTextNew(String ntext, int wrapWidth, boolean justify){
		text = ntext;
		if(text == null || text.length() == 0 )
			text = "";
		stext = new StyledString(text, wrapWidth);
		stext.setJustify(justify);
		bufferInvalid = true;
		return this;
	}

	public FTextControl setFontNew(Font font) {
		if(font != null)
			localFont = font;
		if(buffer != null){
			buffer.g2.setFont(localFont);
			bufferInvalid = true;
		}
		return this;
	}




}
