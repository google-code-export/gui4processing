package guicomponents;
import java.awt.Font;

import processing.core.PApplet;


public class FTextControl extends FAbstractControl implements IText, GAlign {

	protected static final int TPAD = 2;
	protected static final int TPAD2 = TPAD * 2;
	protected static final int TPAD4 = TPAD * 4;
	
	/** Text value associated with component */
	protected String ptext = "";
	/** The styled version of text */
	protected StyledString stext = null;
	
	protected int textAlignH = GAlign.CENTER, textAlignV =  GAlign.MIDDLE;
	protected float stX, stY;

	protected Font localFont = F4P.globalFont;
	
	public FTextControl(PApplet theApplet, float p0, float p1, float p2, float p3) {
		super(theApplet, p0, p1, p2, p3);
	}

	public FTextControl setTextAlignNew(int align){
		if((align & GAlign.HA_VALID) != 0){
			textAlignH = align & GAlign.HA_VALID;
			switch(textAlignH){
			case GAlign.JUSTIFY:
				stext.setJustify(true);
				break;
			case GAlign.LEFT:
			case GAlign.CENTER:
			case GAlign.RIGHT:
				stext.setJustify(false);
				break;
			default:
				textAlignH = GAlign.CENTER;
			}
		}
		if((align & GAlign.VA_VALID) != 0){
			textAlignV = align & GAlign.VA_VALID;
			if(textAlignV != GAlign.TOP && textAlignV != GAlign.MIDDLE && textAlignV != GAlign.BOTTOM)
				textAlignV = GAlign.MIDDLE;
		}
		bufferInvalid = true;
		return this;
	}

	public int getTextAlign(){
		return textAlignH | textAlignV;
	}

	/**
	 * Set the text to be displayed.
	 * NEW version for FPanel etc.
	 * @param ptext
	 */
	public FTextControl setTextNew(String text){
		if(text == null || text.length() == 0 )
			ptext = " ";
		else 
			ptext = text;
		stext = new StyledString(ptext, (int)width - TPAD2);
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

	protected void calcAlignment(){
		switch(textAlignH){
		case GAlign.RIGHT:
			stX = width - stext.getWrapWidth() - TPAD;
			break;
		case GAlign.LEFT:
		case GAlign.CENTER:
		case GAlign.JUSTIFY:
		default:
			stX = TPAD;	
		}
		switch(textAlignV){
		case GAlign.TOP:
			stY = TPAD;
			break;
		case GAlign.BOTTOM:
			stY = height - stext.getTextAreaHeight() - TPAD;
			break;
		case GAlign.MIDDLE:
		default:
			stY = (height - stext.getTextAreaHeight()) / 2;
		}
	}


}
