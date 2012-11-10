package guicomponents;

public enum GEvent {

	CHANGED 			( "Text has changed" ),
	SELECTION_CHANGED 	( "Text selection has changed" ),
	ENTERED			 	( "Enter/return key typed" ),
	
	
	
	
	// Selected text has change
//	public final static int SET = 				0x00000104;	// setText() was used

	// GPanel component
	COLLAPSED  			( "Control was collapsed"),
	EXPANDED 			( "Control was expanded"),
	DRAGGED 			( "Control has moved"),

	// GButton
	CLICKED  			( "Mouse button clicked"),
	PRESSED  			( "Mouse button pressed"),
	RELEASED  			( "Mouse button released"),

	// GCheckbox & GOption
	SELECTED			( "Option selected");

	
	private String description;
	
	private GEvent(String desc ){
		description = desc;
	}
	
	public String toString(){
		return description;
	}
}
