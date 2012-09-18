package guicomponents;



import java.awt.Font;

import processing.core.PImage;

public interface IText {

	public abstract IText setTextNew(String ntext);

	public abstract IText setTextNew(String ntext, int wrapWidth);

	public abstract IText setTextNew(String ntext, int wrapWidth, boolean justify);

	public IText setTextAlignNew(int align);
	
	public IText setFontNew(Font font);
	
}