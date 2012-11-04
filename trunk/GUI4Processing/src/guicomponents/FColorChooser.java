package guicomponents;

public class FColorChooser {
	
	private static FColorChooser instance;
	
	
	
	public static int getColor(){
		if(instance == null)
			instance = new FColorChooser();
		
		if(instance.window == null)
			return 0;
		
		
		
		
		
		return 0;
	}
	
	private FWindow window = null;
	
	private boolean selecting = false;
	
}
