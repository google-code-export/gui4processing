/**
 * TextField demo with GUI control
 *   by Peter Lager
 *  
 *  Demonstrates the use of the TextField.
 *  Copying and pasting between TextFields (and other 
 *  apps e.g. Notepad) 
 *  is supported (Not tried on a Mac.)
 *  Also supports text being longer than visible area 
 * of TextField.
 * 
 */
import guicomponents.*;

GTextField txf1, txf2;
GButton btnCopy12, btnCopy21;
GLabel lblLine1, lblLine2;

void setup(){
  size(400,200);

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
  GComponent.globalFont = GFont.getFont(this, "Georgia", 16);

  String s1 = "Mary had a little lamb it's fleece as white as snow";

  // Create a text input area
  // (this, initial text, x, y, width, height)
  // The height will automatically be increased if too small to
  // display the font. (It is not reduced.
  txf1 = new GTextField(this, s1, 40,20,320,0);
  txf2 = new GTextField(this, "Then the wolf appeared!",  40,60,320,0);

  // Create a button
  // (this, buttton text, x, y, width, height)
  btnCopy12 = new GButton(this, "Copy 1 > 2", 40,100,140,20);
  btnCopy21 = new GButton(this, "Copy 2 > 1", 220,100,140,20);

  lblLine1 = new GLabel(this, "", 40, 140, 320, 0);
  lblLine1.setBorder(0);
  lblLine2 = new GLabel(this, "", 40, 170, 320, 0);
  lblLine2.setBorder(1);
  lblLine2.setOpaque(true);
}

// Handle TextField events
// Three types of event are reported
// CHANGED     The text has been changed
// SET         The text has been set programmatically using setText()
//             this will not generate a CHANGED event as well
// ENTERED     The enter key has been pressed
void handleTextFieldEvents(GTextField tfield){
  String line1 = "", line2 = "";
  if(tfield == txf1){
    line1 = "Field txf1 has triggered ";
    switch (txf1.eventType){
    case GTextField.CHANGED:
      line1 += "CHANGED";
      break;
    case GTextField.SET:
      line1 += "SET";
      break;
    case GTextField.ENTERED:
      line1 += "ENTERED";
      break;
    }
    line1 += " event and now is =" + txf1.getText();
    line2 = txf1.getText();
  }
  if(tfield == txf2){
    line1 = "Text in field txf2 has been ";
    switch (txf2.eventType){
    case GTextField.CHANGED:
      line1 += "CHANGED";
      break;
    case GTextField.SET:
      line1 += "SET";
      break;
    case GTextField.ENTERED:
      line1 += "ENTERED";
      break;
    }
    line1 += " it is now " + txf2.getText();
    line2 = txf2.getText();
  }
  lblLine1.setText(line1);
  lblLine2.setText(line2);

}

// Handle button events
void handleButtonEvents(GButton button){
  if(button == btnCopy12 && button.eventType == GButton.CLICKED)
    txf2.setText(txf1.getText());
  else if(button == btnCopy21 && button.eventType == GButton.CLICKED)
    txf1.setText(txf2.getText());
}

void draw(){
  background(200);
}