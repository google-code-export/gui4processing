package guicomponents;

public enum GEvent {

	CHANGED 			( "Text has changed" ),
	SELECTION_CHANGED 	( "Text selection has changed" ),
	ENTERED			 	( "Enter/return key typed" ),
	
	// GPanel component
	COLLAPSED  			( "Control was collapsed" ),
	EXPANDED 			( "Control was expanded" ),
	DRAGGED 			( "Control is being dragged" ),

	// GButton
	CLICKED  			( "Mouse button clicked" ),
	PRESSED  			( "Mouse button pressed" ),
	RELEASED  			( "Mouse button released" ),

	VALUE_CHANGING		( "Value is changing" ),
	VALUE_STEADY		( "Value has reached a steady state" ),
	DRAGGING			( "The mouse is being dragged over a component "),
	
	// GCheckbox & GOption
	SELECTED			( "Option selected" );

	
	private String description;
	
	private GEvent(String desc ){
		description = desc;
	}
	
	public String toString(){
		return description;
	}
}
