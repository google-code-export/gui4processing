import guicomponents.*;

GButton b1,b2,b3;

void setup(){
  size(200,200);
  // A text only button that generates all event types
  b1= new GButton(this, "No image here", 50,20,100,20);
  b1.fireAllEvents(true);

  // Image only button : CLICKED events only
  b2= new GButton(this, "btnA.png",1, 80,70,40,40);

  // Image & text button : CLICKED events only
  b3= new GButton(this, "btnB.png",3, 50,130,100,40);
  b3.setText("Peter Lager");
  b3.setImageAlign(GAlign.LEFT);
  
  // Enable mouse over image change
  G4P.setMouseOverEnabled(true);
}

void handleButtonEvents(GButton button) {
  print(button.getText()+"\t\t");
  switch(button.eventType){
  case GButton.PRESSED:
    System.out.println("PRESSED");
    break;
  case GButton.RELEASED:
    System.out.println("RELEASED");
    break;
  case GButton.CLICKED:
    System.out.println("CLICKED");
    break;
  default:
    println("Unknown mouse event");
  }
}	

void draw(){
  background(255,255,200);
}




