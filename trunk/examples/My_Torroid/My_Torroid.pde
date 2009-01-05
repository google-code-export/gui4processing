/**
 * Interactive Toroid
 * by Ira Greenberg. 
 * 
 * Illustrates the geometric relationship between Toroid, Sphere, and Helix
 * 3D primitives, as well as lathing principal.
 * 
 */

import guicomponents.*;

GLabel lblSegs, lblERad, lblPts, lblLRad;
GSlider sdrSegs, sdrERad, sdrPts, sdrLRad;
GCheckbox cbxHelix, cbxWire;
GPanel p;

Toroid t1;

void setup(){
  size(640, 360, P3D);
  t1 = new Toroid();

  GComponent.globalColor = GColor.getColor(this,  GUI.RED);
  GComponent.globalFont = GFont.getFont(this);
  p = new GPanel(this, "Toroid Control Panel", 30, 30, 460, 80);
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

  cbxHelix = new GCheckbox(this, "Helix?", 2, 60, 80);
  cbxWire = new GCheckbox(this, "Wire frame?", 102, 60, 100);

  p.addComponent(lblSegs);
  p.addComponent(lblPts);
  p.addComponent(lblERad);
  p.addComponent(lblLRad);
  p.addComponent(sdrSegs);
  p.addComponent(sdrPts);
  p.addComponent(sdrERad);
  p.addComponent(sdrLRad);
  p.addComponent(cbxHelix);
  p.addComponent(cbxWire);

  p.setOpaque(true);

}

public void handleSliderEvents(GSlider slider){
  if(slider == sdrSegs)
    t1.setSegmentDetail(sdrSegs.getValue());
  if(slider == sdrPts)
    t1.setEllipseDetail(sdrPts.getValue());
  if(slider == sdrERad)
    t1.setEllipseRadius(sdrERad.getValue()); 
  if(slider == sdrLRad)
    t1.setLatheRadius(sdrLRad.getValue()); 
}

public void handleCheckboxEvents(GCheckbox cbox){
  if(cbox == cbxHelix)
    t1.setIsHelix(cbxHelix.isSelected());
  if(cbox == cbxWire)
    t1.setIsWire(cbxWire.isSelected());
}


void draw(){
  pushMatrix();
  background(50, 64, 42);
  // basic lighting setup
  lights();
  // 2 rendering styles
  //center and spin toroid
  translate(width/2, height/2, -200);

  rotateX(frameCount*PI/150);
  rotateY(frameCount*PI/170);
  rotateZ(frameCount*PI/90);


  // draw toroid
  t1.draw();
  popMatrix();
}
