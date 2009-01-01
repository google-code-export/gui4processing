/**
 * Interactive Toroid
 * by Ira Greenberg. 
 * 
 * Illustrates the geometric relationship between Toroid, Sphere, and Helix
 * 3D primitives, as well as lathing principal.
 * 
 * Instructions: <br />
 * UP arrow key pts++ <br />
 * DOWN arrow key pts-- <br />
 * LEFT arrow key segments-- <br />
 * RIGHT arrow key segments++ <br />
 * 'a' key toroid radius-- <br />
 * 's' key toroid radius++ <br />
 * 'z' key initial polygon radius-- <br />
 * 'x' key initial polygon radius++ <br />
 * 'w' key toggle wireframe/solid shading <br />
 * 'h' key toggle sphere/helix <br />
 */

import guicomponents.*;

int pts = 40;
float angle = 0;
float radius = 60.0;

// lathe segments
int segments = 60;
float latheAngle = 0;
float latheRadius = 100.0;

//vertices
PVector vertices[], vertices2[];

// for shaded or wireframe rendering 
boolean isWireFrame = false;

// for optional helix
boolean isHelix = false;
float helixOffset = 5.0;

GLabel lbl1, lbl2;
GPanel p , sp1, sp2;
Toroid t1;

void setup(){
  size(640, 360, P3D);
  t1 = new Toroid();
  
  p = new GPanel(this, "Main Panel", 100, 100, 250, 160, GConstants.RED, GConstants.FONT16);
  sp1 = new GPanel(this, "Sub panel 1", 2, 22, 200, 100, GConstants.GREEN, GConstants.FONT16);
  sp2 = new GPanel(this, "Sub panel 2", 52, 30, 80, 60, GConstants.BLUE, GConstants.FONT11);
  p.addComponent(sp1);
  sp1.addComponent(sp2);
  lbl1 = new GLabel(this, "Peter", 25, 25, 100, GConstants.RED, GConstants.FONT16);
  
  sp2.display();
 }

void draw(){
  pushMatrix();
  background(50, 64, 42);

  translate(0,0,-200);
  // basic lighting setup
  lights();
  // 2 rendering styles
  //center and spin toroid
  translate(width/2, height/2, -100);

  rotateX(frameCount*PI/150);
  rotateY(frameCount*PI/170);
  rotateZ(frameCount*PI/90);

  
  // draw toroid
  t1.draw();
  popMatrix();
}

/*
 left/right arrow keys control ellipse detail
 up/down arrow keys control segment detail.
 'a','s' keys control lathe radius
 'z','x' keys control ellipse radius
 'w' key toggles between wireframe and solid
 'h' key toggles between toroid and helix
 */
void keyPressed(){
  if(key == CODED) { 
    // pts
    if (keyCode == UP) { 
      if (pts<40){
        pts++;
      } 
    } 
    else if (keyCode == DOWN) { 
      if (pts>3){
        pts--;
      }
    } 
    // extrusion length
    if (keyCode == LEFT) { 
      if (segments>3){
        segments--; 
      }
    } 
    else if (keyCode == RIGHT) { 
      if (segments<80){
        segments++; 
      }
    } 
  }
  // lathe radius
  if (key =='a'){
    if (latheRadius>0){
      latheRadius--; 
    }
  } 
  else if (key == 's'){
    latheRadius++; 
  }
  // ellipse radius
  if (key =='z'){
    if (radius>10){
      radius--;
    }
  } 
  else if (key == 'x'){
    radius++;
  }
  // wireframe
  if (key =='w'){
    if (isWireFrame){
      isWireFrame=false;
    } 
    else {
      isWireFrame=true;
    }
  }
  // helix
  if (key =='h'){
    if (isHelix){
      isHelix=false;
    } 
    else {
      isHelix=true;
    }
  }
}