/**
 * TextField demo with GUI control
 * by Peter Lager
 * 
 * Demonstrates the use of the TextField.
 * Copying and pasting between TextFields (and other 
 * apps e.g. Notepad) 
 * is supported (Not tried on a Mac.)
 * Also supports text being longer than visible area 
 * of TextField.
 *
 */

import guicomponents.*;

GTextField txf1, txf2;
GButton btnCopy;

void setup(){
  size(400,140);
  
  // Sets the colour scheme for the GUI components 
  // Schemes available are 
  // BLUE_SCHEME, GREEN_SCHEME, RED_SCHEME, GREY_SCHEME
  // YELLOW_SCHEME, CYAN_SCHEME, PURPLE_SCHEME
  // Defaults to BLUE_SCHEME 
  GComponent.globalColor = GCScheme.getColor(this,  GCScheme.GREEN_SCHEME);
  
  /* GFont.getFont() - parameters
   * 1) this (always)
   * 2) font name (see below)
   * 3) font size
   *
   * The font name will depend on the OS used and fonts installed. It should be the same
   * as those listed in the 'Create Font' tool in processing. Alternatively use
   * println(PFont.list());
   * in a Processing sketch
   */
  GComponent.globalFont = GFont.getFont(this, "Georgia", 18);

  String s1 = "Mary had a little lamb it's fleece as white as snow";
  
  // Create a text input area
  // (this, initial text, x, y, width, height)
  // The height will automatically be increased if too small to
  // display the font. (It is not reduced.
  txf1 = new GTextField(this, s1, 40,20,320,0);
  txf2 = new GTextField(this, "Then the wolf appeared!",  40,60,320,0);
  
  // Create a button
  // (this, buttton text, x, y, width, height)
  btnCopy = new GButton(this, "Copy 1 > 2", 60,100,150,20);
  
}

// Handle TextField events
// Three types of event are reported
// CHANGED     The text has been changed
// SET         The text has been set programmatically using setText()
//             this will not generate a CHANGED event as well
// ENTERED     The enter key has been pressed
void handleTextFieldEvents(GTextField tfield){
  if(tfield == txf1){
    print("Text in field txf1 has been ");
    switch (txf1.getEventType()){
      case GTextField.CHANGED:
        print("CHANGED");
        break;
      case GTextField.SET:
        print("SET");
        break;
      case GTextField.ENTERED:
        print("ENTERED");
        break;
    }
    println(" it is now " + txf1.getText());
  }
  if(tfield == txf2){
    print("Text in field txf1 has been ");
    switch (txf2.getEventType()){
      case GTextField.CHANGED:
        print("CHANGED");
        break;
      case GTextField.SET:
        print("SET");
        break;
      case GTextField.ENTERED:
        print("ENTERED");
        break;
    }
    println(" it is now " + txf2.getText());
  }
}

// Handle button events
void handleButtonEvents(GButton button){
  txf2.setText(txf1.getText());
}

void draw(){
  background(200);
}