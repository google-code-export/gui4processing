// =============================================
// Create the configuration window and controls
// =============================================

GWindow windControl;  
GButton[] btnColours = new GButton[8];
GSlider sdrAlpha;
GKnob knbAngle;
GLabel lblAlpha1, lblAlpha2;

public void createControlWindow() {
  windControl = new GWindow(this, "Controls", 600, 400, 240, 300, false, JAVA2D);
  PApplet app = windControl.papplet; // save some typing
  // Create colour scheme selectors
  int x = app.width - 42;
  int y = 8;
  for (int i = 0; i < btnColours.length; i++) {
    btnColours[i] = new GButton(app, x, y + i * 20, 40, 18, "" + (i+1));
    btnColours[i].tag = "Button: " + (i+1);
    btnColours[i].setLocalColorScheme(i);
    btnColours[i].tagNo = 1000+i;
  }  
  x = app.width - 110; 
  y = 166;    
  sdrAlpha = new GSlider(app, x, y, 162, 80, 12);
  sdrAlpha.setLimits(255, 0, 255);
  sdrAlpha.setRotation(-PI/2);
  sdrAlpha.setTextOrientation(G4P.ORIENT_RIGHT);
  sdrAlpha.setEasing(20);
  sdrAlpha.setShowValue(true);
  sdrAlpha.setShowTicks(true);

  x = 20; 
  y = 40;
  lblAlpha1 = new GLabel(app, x, y, 120, 26, "Transparency  >>>");
  lblAlpha1.setTextBold();
  lblAlpha2 = new GLabel(app, x, y + 30, 110, 80, "When alpha falls below 64 it will disable the controls in the main sketch.");

  x = 70; 
  y = 180;
  knbAngle = new GKnob(app, x, y, 100, 100, 0.75f);
  knbAngle.setTurnRange(0, 360);
  knbAngle.setLimits(0.0f, 0.0f, TWO_PI);
  knbAngle.setTurnMode(G4P.CTRL_ANGULAR);
  knbAngle.setIncludeOverBezel(true);
  knbAngle.setNbrTicks(13);
  knbAngle.setStickToTicks(true);

  windControl.addDrawHandler(this, "drawController");
}

/*
   * The draw handler for the control window
 */
public void drawController(GWinApplet appc, GWinData data) {
  appc.background(227, 230, 255);
}
