package guicomponents;



import java.awt.Font;

public interface IText {

	public abstract IText setText(String ntext);

	public IText setTextAlign(int align);
	
	public IText setFont(Font font);
	
}