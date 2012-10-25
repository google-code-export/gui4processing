package guicomponents;



import java.awt.Font;

public interface IText {

	public abstract void setText(String ntext);

	public void setTextAlign(int align);
	
	public void setFont(Font font);
	
}