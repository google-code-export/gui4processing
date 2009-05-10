import guicomponents.*;


GPanel pnlControls;
GLabel lblSomeString, lblAlpha, lblAction;
GTextField txfSomeText;
GCombo cboColor, cboFont;
GHorzSlider sdrAlpha;
GActivityBar acyBar;
GTimer tmrTimer;
GButton btnTimer;

PImage imgBgImage;

int count = 0;

void setup(){
  size(700,340);
  imgBgImage = loadImage("bground.jpg");

  // Set the colour scheme
  G4P.setColorScheme(this, GCScheme.GREEN_SCHEME);

  pnlControls = new GPanel(this,"Panel Tab Text (drag to move : click to open/close)", 10,30,600,280);
  pnlControls.setOpaque(true);
  pnlControls.setCollapsed(false);
  lblAction = new GLabel(this, "USER ACTION FEEDBACK DISPLAYED HERE!", 0, pnlControls.getHeight()-20, pnlControls.getWidth(), 20);
  lblAction.setBorder(1);
  lblAction.setOpaque(true);
  lblAction.setColorScheme(GCScheme.RED_SCHEME);
  lblSomeString = new GLabel(this, "LABEL: Use combo boxes to change color scheme and font", 10, 10, 400, 20 );
  lblSomeString.setBorder(1);
  lblSomeString.setOpaque(true);
  txfSomeText = new GTextField(this, "TEXTFIELD: Use combo boxes to change color scheme and font", 10, 50, 400, 20);
  createCombos();

  lblAlpha = new GLabel(this,"Adjust Panel transparency ->",10,172,150);
  lblAlpha.setFont("Arial", 14);
  sdrAlpha = new GHorzSlider(this,210,170,300,22);
  sdrAlpha.setBorder(2);
  sdrAlpha.setLimits(255, 128, 255);

  btnTimer = new GButton(this, "Start", "time.png", 1, pnlControls.getWidth()-100, pnlControls.getHeight()-60, 100, 40);

  this.acyBar = new GActivityBar(this,pnlControls.getWidth()/2 - 60, 220,120,10);
  acyBar.start(0);

  pnlControls.add(btnTimer);
  pnlControls.add(sdrAlpha);
  pnlControls.add(lblSomeString);
  pnlControls.add(lblAlpha);
  pnlControls.add(txfSomeText);
  pnlControls.add(cboColor);
  pnlControls.add(cboFont);
  pnlControls.add(lblAction);
  pnlControls.add(acyBar);
  tmrTimer = new GTimer(this, this, "myTimerFunction", 500);
}

void createCombos() {
  String[] colors = new String[] {
    "Blue", "Green", "Red", "Purple", "Yellow", "Cyan", "Grey"  };
  cboColor = new GCombo(this, colors, 4, 10, 90, 60);
  cboColor.setSelected(1);
  String[] fonts = new String[] { 
    "SansSerif 11", "Serif 11", "Georgia 15", "Times New Roman 18", "Arial Bold 10",
    "Arial 10", "Courier New 9"   };
  cboFont = new GCombo(this, fonts, 4, 120, 90, 160);
}

public void handleComboEvents(GCombo combo){
  if(cboColor == combo){
    pnlControls.setColorScheme(cboColor.selectedIndex());
    lblSomeString.setColorScheme(cboColor.selectedIndex());
    txfSomeText.setColorScheme(cboColor.selectedIndex());
    acyBar.setColorScheme(cboColor.selectedIndex());
    btnTimer.setColorScheme(cboColor.selectedIndex());
    sdrAlpha.setColorScheme(cboColor.selectedIndex());
    sdrAlpha.setValue(255);
    lblAction.setText("Color changed to " + cboColor.selectedText());
  }
  if(cboFont == combo){
    // Get font name and size from
    String[] fs = cboFont.selectedText().split(" ");
    String font = fs[0];
    if(fs.length > 2){
      for(int i = 1; i < fs.length - 1; i++)
        font = font + " " + fs[i];
    }
    int fsize = Integer.parseInt(fs[fs.length - 1]);
    // Set fonts
    lblSomeString.setFont(font, fsize);
    txfSomeText.setFont(font, fsize);
    lblAction.setText("Font changed to " + cboFont.selectedText() + "px");			
  }
}	

void handleSliderEvents(GSlider slider){
  if(sdrAlpha == slider){
    pnlControls.setAlpha(sdrAlpha.getValue());
    lblAction.setText("Panel transparency is " + pnlControls.getAlpha());			
  }
}

void handleButtonEvents(GButton button){
  if(btnTimer == button){
    boolean running = tmrTimer.isRunning();
    if(tmrTimer.isRunning()){
      lblAction.setText("Timer stopped");
      btnTimer.setText("Start");
      tmrTimer.stop();
    }
    else {
      lblAction.setText("Timer started");
      btnTimer.setText("Stop");
      tmrTimer.start();
    }
  }
}

void myTimerFunction(){
  count++;
  lblAction.setText("My timer has counted to " + count);
}

void draw(){
  background(imgBgImage);
}

