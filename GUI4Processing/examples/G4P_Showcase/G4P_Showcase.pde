/**
 This example showcases many of the controls available. Click 
 on the 'G4P config window' button to open the control window 
 for more options.
 
 Some of the other examples  demonstrate individual controls 
 in more detail.

 (c) 2012 Peter Lager
 */

import g4p_controls.*;


// G4P components for main window
GPanel pnlControls;
GLabel lblAction;
GTextField txfDemo;
GTextArea txaDemo;
GKnob knbDemo;
GTimer tmrTimer;
GButton btnTimer;
GTabManager tt;
GCheckbox cbxSticky;
GButton btnControl;  // Used to start controller window

String startText;
PImage imgBgImage;

int count = 0;
GLabel lblMC;
GOption optAngular, optYdrag, optXdrag;
GToggleGroup tg;
GSketchPad spad;
PGraphics pg;

public void setup() {
  size(700, 340);

  G4P.registerSketch(this);
  // Load the background image
  imgBgImage = loadImage("bground.jpg");
  G4P.messagesEnabled(true);

  makeDemoControls();
}

public void updateGraphic(int n) {
  n = (n < 0) ? 0 : n % 30;
  pg.beginDraw();
  pg.background(0);
  pg.noStroke();
  pg.fill(255);
  pg.ellipseMode(CENTER);
  if (n >= 0) {
    for (int i = 0; i < n; i++) {
      int r = i / 10;
      int c = i % 10;
      pg.ellipse(5 + c * 10, 7 + r * 15, 6, 10);
    }
  }
  pg.endDraw();
}

public void draw() {
  background(imgBgImage);
  updateGraphic(count);
}

public void handleTextEvents(GEditableTextControl textcontrol, GEvent event) {
  if (textcontrol == txaDemo)
    lblAction.setText("TextArea: " + event);
  if (textcontrol == txfDemo)
    lblAction.setText("TextField: " + event);
}

public void handlePanelEvents(GPanel panel, GEvent event) { 
  lblAction.setText("Panel: " + event);
}

public void handleSliderEvents(GValueControl slider, GEvent event) {
  if (slider == sdrAlpha) {
    G4P.setWindowAlpha(this, sdrAlpha.getValueI());
  }
}

public void handleToggleControlEvents(GToggleControl option, GEvent event) {
  if (option == optAngular) {
    lblAction.setText("Knob is now using angular drag for rotation");
    knbDemo.setTurnMode(G4P.CTRL_ANGULAR);
  }
  if (option == optXdrag) {
    lblAction.setText("Knob is now using horizontal drag for rotation");
    knbDemo.setTurnMode(G4P.CTRL_HORIZONTAL);
  }
  if (option == optYdrag) {
    lblAction.setText("Knob is now using vertical drag for rotation");
    knbDemo.setTurnMode(G4P.CTRL_VERTICAL);
  }
  if (option == cbxSticky) {
    lblAction.setText("Stick to ticks option changed");
    knbDemo.setStickToTicks(cbxSticky.isSelected());
  }
}

public void handleKnobEvents(GValueControl knob, GEvent event) { 
  if (knob == knbDemo)
    lblAction.setText("Knob value is now " + knbDemo.getValueS());
  if (knob == knbAngle) {
    pnlControls.setRotation(knbAngle.getValueF(), PApplet.CENTER);
  }
}

public void handleButtonEvents(GButton button, GEvent event) {
  // Create the control window?
  if (button == btnControl && event == GEvent.CLICKED) {
    lblAction.setText("Open control window and disable the button");
    createControlWindow();
    btnControl.setEnabled(false);
  }
  // Change the colour scheme
  if (button.tagNo >= 1000 && event == GEvent.CLICKED)
    G4P.setWindowColorScheme(this, button.tagNo - 1000);
  if (button == btnTimer && event == GEvent.CLICKED) {
    if (tmrTimer.isRunning())
      tmrTimer.stop();
    else
      tmrTimer.start();
  }
}

public void myTimerFunction(GTimer timer) {
  count++;
}

