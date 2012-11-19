package g4p_controls;

/**
 * UNDER CONSTRUCTION - DO NOT ATTEMPT TO USE
 * 
 * @author Peter Lager
 *
 */
public class GColorChooser {
	
	private static GColorChooser instance;
	
	
	
	public static int getColor(){
		if(instance == null)
			instance = new GColorChooser();
		
		if(instance.window == null)
			return 0;
		
		
		
		
		
		return 0;
	}
	
	private GWindow window = null;
	
	private boolean selecting = false;
	
}
