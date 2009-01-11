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
float zoom;

void setup() {
  size(640, 360);
  
  smooth();
  // The file "bot1.svg" must be in the data folder
  // of the current sketch to load successfully
  bot = loadShape("bot1.svg");
  
  sdrScale = new GVertSlider(this, 0, 0, 20, height);
  sdrScale.setLimits(45, 1, 45);
  zoom = 0.1;
} 

void draw() {
  pushMatrix();
  background(192,192,0);
  translate(width/2, height/2);
  //float zoom = map(mouseX, 0, width, 0.1, 4.5);
  scale(zoom);
  shape(bot, -140, -140);
  popMatrix();
}

void handleSliderEvents(GSlider slider){
  zoom = (46 - slider.getValue()) * 0.1;
}
