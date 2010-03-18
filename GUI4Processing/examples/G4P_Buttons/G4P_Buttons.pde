import guicomponents.*;

GButton b1,b2,b3;

void setup(){
  size(340,260);

  G4P.setColorScheme(this, GCScheme.BLUE_SCHEME);
  G4P.setFont(this, "Verdana", 38);

  // A text only button that generates all event types
  b1= new GButton(this, "Text Only", 20,20,300,50);
  b1.setBorder(2);
  b1.fireAllEvents(true);

  // Image only button : CLICKED events only
  b2= new GButton(this, "smile01.png",3, 100,100,66,66);
  b2.setBorder(0);

  // Image & text button : CLICKED events only
  b3= new GButton(this, "pic001.png",1, 60,190,220,50);
  b3.setText("Pictures");
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
  background(180,220,180);
}
