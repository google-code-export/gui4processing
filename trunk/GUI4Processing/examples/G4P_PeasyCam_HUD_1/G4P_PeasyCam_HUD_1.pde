/**
 * PeasyCam with HUD display
 *
 * Demonstrates how to combine PeasyCam with GUI4processing
 * to provide a 'head up display' 
 *
 * by Peter Lager
 */
 
import processing.opengl.*;
import guicomponents.*;
import peasy.*;

PeasyCam cam;

GTextField txf1;
GButton btn1;

// These are needed to remember PeasyCam offset and distance
float[] offsets = new float[3];
//float[] rotations = new float[3];
double distance = 0.0f;

// Used to remember PGraphics3D transformation matrix
PGraphics3D g3;

// Required for setting HUD position
int w, h;
int hudHeight;

void setup() {
  // Change OPENGL to P3D to use Java 3D both work but
  // OpenGL gives a better picture while Java 3D is probaly
  // better for applets - smaller file size.
  size(400,400,OPENGL);
  // Create a PeasyCam object
  cam = new PeasyCam(this, 100);
  cam.setMinimumDistance(50);
  cam.setMaximumDistance(500);
  // Create G4P GUI components
  w = getWidth();
  h = getHeight();
  hudHeight = 60;
  
  // Sets the colour scheme for the GUI components 
  // Schemes available are 
  // BLUE_SCHEME, GREEN_SCHEME, RED_SCHEME, GREY_SCHEME
  // YELLOW_SCHEME, CYAN_SCHEME, PURPLE_SCHEME
  // Defaults to BLUE_SCHEME 
  GComponent.globalColor = GCScheme.getColor(this,  GCScheme.BLUE_SCHEME);
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
  GComponent.globalFont = GFont.getFont(this, "Georgia", 14);

  txf1 = new GTextField(this, "Hello", (w - 320)/2, h - hudHeight + 10,320,0);
  btn1 = new GButton(this, "Clear Text Field",(w - 180)/2, h - hudHeight + 30,180,0);
  // Remember PGraphics3D transformation matrix
  g3 = (PGraphics3D)g;
}

void draw() {
  pushMatrix();
  rotateX(-.5);
  rotateY(-.5);
  background(0);
  fill(255,0,0);
  box(30);
  translate(0,0,20);
  fill(0,0,255);
  box(5);
  popMatrix();
  hud();
}

// Event handler for G4P buttons
void handleButtonEvents(GButton button){
  if(button == btn1){
    txf1.setText("");
    println("Button 1 clicked");
  }
}

// Event handler for G4P text fields
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
}

/*
This function displays how we can create a HUD with PeasyCam.
 */
void hud(){
  // Get the current PeasyCam details to restore later
  offsets = cam.getLookAt();
  distance = cam.getDistance();

  // Get a handle on the current PGraphics£D transformation matrix
  PMatrix3D currCameraMatrix = g3.camera;

  // This statement resets the camera in PGraphics3D, this effectively 
  // sets the display to behave as 2D with the origin 0,0 at the top-left
  // of the display
  camera();

  // Draw the HUD using PApplet graphics commands and GUI for Processing
  // 2D gui components
  stroke(240,0,0,64);
  fill(240,0,0,64);
  rect(0, h - hudHeight, w, hudHeight);
  G4P.draw();

  // Reset the PGraphics3D transformation matrix
  g3.camera = currCameraMatrix;
  // Reset the PeasyCam position (do not add rotation commands here)
  cam.lookAt(offsets[0],offsets[1],offsets[2]);
  cam.setDistance(distance);
}

