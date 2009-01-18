import guicomponents.*;


/**
 * Scale Shape.  
 * Illustration by George Brower. 
 * 
 * Move the mouse left and right to zoom the SVG file.
 * This shows how, unlike an imported image, the lines
 * remain smooth at any size.
 */

PShape bot;
GVertSlider sdrScale;

GLabel lblSegs, lblERad, lblPts, lblLRad;
GSlider sdrSegs, sdrERad, sdrPts, sdrLRad;
GCheckbox cbxWire;
GOption optTorroid, optHelix;
GOptionGroup optShape;
GPanel p;
GButton btn;

float zoom;

void setup() {
  size(640, 360);
  smooth();

  GComponent.globalColor = GColor.getColor(this,  GUI.BLUE);
  GComponent.globalFont = GFont.getFont(this);
  p = new GPanel(this, "Toroid Control Panel", 30, 30, 460, 90);
  lblSegs = new GLabel(this, "Segment detail", 2, 4, 120);
  lblPts = new GLabel(this, "Ellipse detail", 2, 18, 120);
  lblERad = new GLabel(this, "Ellipse Radius", 2, 32, 120);
  lblLRad = new GLabel(this, "Toroid Radius", 2, 46, 120);
  sdrSegs = new GHorzSlider(this, 125, 4, 325, 11);
  sdrPts = new GHorzSlider(this, 125, 18, 325, 11);
  sdrERad = new GHorzSlider(this, 125, 32, 325, 11);
  sdrLRad = new GHorzSlider(this, 125, 46, 325, 11);
  sdrSegs.setLimits(60, 3, 80);
  sdrPts.setLimits(40,3,40);
  sdrERad.setLimits(60,10,100);
  sdrLRad.setLimits(100,0,240);

  optTorroid = new GOption(this, "Toroid?", 2, 60, 80);
  optHelix = new GOption(this, "Helix?", 2, 74, 80);
  cbxWire = new GCheckbox(this, "Wire frame?", 102, 60, 100);

  btn = new GButton(this, "Start",300, 60,100,20);
  
  p.add(lblSegs);
  p.add(lblPts);
  p.add(lblERad);
  p.add(lblLRad);
  p.add(sdrSegs);
  p.add(sdrPts);
  p.add(sdrERad);
  p.add(sdrLRad);
  optShape = new GOptionGroup(this);
  p.add(optHelix);
  p.add(optTorroid);
  p.add(cbxWire);
  optShape.addOption(optTorroid);
  optShape.addOption(optHelix);
  p.add(btn);
  p.setOpaque(true);

  // The file "bot1.svg" must be in the data folder
  // of the current sketch to load successfully
  bot = loadShape("bot1.svg");
  
  sdrScale = new GVertSlider(this, 0, 0, 20, height);
  sdrScale.setLimits(45, 1, 45);
  zoom = 0.1;
} 

void draw() {
  pushMatrix();
  background(224,224,224);
  translate(width/2, height/2);
  //float zoom = map(mouseX, 0, width, 0.1, 4.5);
  scale(zoom);
  shape(bot, -140, -140);
  popMatrix();
}

void handleSliderEvents(GSlider slider){
  zoom = (46 - slider.getValue()) * 0.1;
}
