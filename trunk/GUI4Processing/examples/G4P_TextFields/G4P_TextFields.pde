/**
 * TextField demo with GUI control
 * by Peter Lager
 * 
 * Demonstrates the use of the TextField 
 * created with Brendan Nichols
 * 
 * Copying and pasting between TextFields (and other 
 * apps e.g. Notepad) 
 * is supported (Not tried on a Mac.)
 * Also supports text being longer than visible area 
 * of TextField.
 *
 */

import guicomponents.*;

GTextField txfMl1, txfMl2, txfSl1, txfSl2, txfEvents;
GLabel lblMl1, lblMl2, lblSl1, lblSl2, lblEvents;
GCheckbox cbxMl1, cbxMl2;

ArrayList events = new ArrayList();

void setup(){
  size(500,320);

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
  GComponent.globalFont = GFont.getFont(this, "Arial", 12);

  String[] lines = loadStrings("quarks.txt");
  String ml1 = join(lines, '\n');

  lines = loadStrings("nickel.txt");
  String ml2 = join(lines, '\n');

  // Create a text input area the last parameter is true for multiline components
  // (this, initial text, x, y, width, height, true)
  txfMl1 = new GTextField(this, ml1, 10, 26, 200, 100, true);
  txfMl1.tag = "Multi-line 1 -> ";
  cbxMl1 = new GCheckbox(this, "Separation lines", 10, 130, 100);
  lblMl1 = new GLabel(this, "ML 1", 10,10,40,16);

  txfMl2 = new GTextField(this, ml2, 10, 176, 200, 100, true);
  txfMl2.tag = "Multi-line 2 -> ";
  cbxMl2 = new GCheckbox(this, "Separation lines", 10, 280, 100);
  lblMl2 = new GLabel(this, "ML 2", 10,160,40,16);

  // Create a single line text input area
  // (this, initial text, x, y, width, height)
  txfSl1 = new GTextField(this, "Peter Lager", 240, 26, 200, 16);
  txfSl1.tag = "Single line 1 -> ";
  lblSl1 = new GLabel(this, "SL 1", 240, 10, 180, 16);

  txfSl2 = new GTextField(this, "Brendan Nichols", 240, 66, 200, 16);
  txfSl2.tag = "Single line 2 -> ";
  lblSl2 = new GLabel(this, "SL 2", 240, 50, 180, 16);

  lblEvents = new GLabel(this, "EVENT LOG", 240, 94, 200);
  lblEvents.setTextAlign(GAlign.CENTER);

  txfEvents = new GTextField(this, "", 240, 116, 200, 180, true);
  txfEvents.setFont("monospaced", 11);

  frameRate(10);
}

void handleCheckboxEvents(GCheckbox checkbox) { 
  if(checkbox == cbxMl1)
    txfMl1.showLines(checkbox.isSelected());
  if(checkbox == cbxMl2)
    txfMl2.showLines(checkbox.isSelected());	
}

// Handle TextField events
// Three types of event are reported
// CHANGED     The text has been changed
// SET         The text has been set programmatically using setText()
//             this will not generate a CHANGED event as well
// ENTERED     The enter key has been pressed
void handleTextFieldEvents(GTextField tfield){
  String event = "";
  if(tfield != txfEvents){
    event = tfield.tag;
    switch (tfield.getEventType()){
    case GTextField.CHANGED:
      event += "CHANGED";
      break;
    case GTextField.SET:
      event += "SET";
      break;
    case GTextField.ENTERED:
      event += "ENTERED";
      break;
    }
    addEvent(event);
  }
}

void addEvent(String e){
  events.add(e);
  while(events.size() > 12)
    events.remove(0);
  txfEvents.setText(join((String[])events.toArray(new String[events.size()]), '\n'));
}

void draw(){
  background(200);
}

