import guicomponents.*;

GWSlider sdr1, sdr2, sdr3, sdr4, sdr5, sdr6, sdr7;
GPanel panel;

void setup() {
  size(800, 600);  
 
  G4P.messagesEnabled(false);

  // Simple default slider,
  // constructor is `Parent applet', the x, y position and length
  sdr1 = new GWSlider(this,20,20,300);
  
  // Slider with a custom skin, check the data folder to find the `blue18px'
  // folder which stores the used image files.
  sdr2 = new GWSlider(this,"blue18px",20,80,300);
	  
  // there are 3 types
  // GWSlider.DECIMAL  e.g.  0.002
  // GWSlider.EXPONENT e.g.  2E-3
  // GWSlider.INTEGER
  sdr2.setValueType(GWSlider.DECIMAL);
  sdr2.setLimits(0.5f, 0f, 1.0f);
  sdr2.setRenderValueLabel(false); 

  // Slider with another custom skin
  sdr3 = new GWSlider(this,"red_yellow18px",20,140,300);
  sdr3.setRenderValueLabel(false); 

  // Slider with another custom skin
  sdr4 = new GWSlider(this,"purple18px",20,200,300);
  sdr4.setInertia(30);
  
  // Standard slider with labels switched off
  sdr5 = new GWSlider(this,20,260,300);
  sdr5.setValueType(GWSlider.DECIMAL);
  sdr5.setLimits(0.5f, 0f, 1.0f);
  sdr5.setTickCount(3); 
  sdr5.setRenderMaxMinLabel(false); //hides labels
  sdr5.setRenderValueLabel(false);  //hides value label
  sdr5.setStickToTicks(true);       //false by default 
  // `Stick to ticks' enforces that the handle can only rest at a 
  // tick position.
  		
  // This example shows small float numbers used and settings
  // the accuracy of the display labels
  sdr6 = new GWSlider(this,20,320,300);
  sdr6.setValueType(GWSlider.EXPONENT);
  sdr6.setLimits(3E-10f, 2.0E-10f, 3.5E-10f);
  sdr6.setTickCount(15); 
  sdr6.setPrecision(2);
  sdr6.setStickToTicks(true);
  
  // We can also add custom labels to ticks
  // Note: 
  // setTickLabels() changes the number of ticks previously 
  //                 set with setTickCount() to match the 
  //                 number of labels in the array.
  // setTickCount()  cancels labels that were previously set 
  //                 with setTickLabels()
  String[] sdr6TickLabels = new String[] {"A", "B", "C", "D", "E"};
  sdr7 = new GWSlider(this,20,380,300);
  sdr7.setTickLabels(sdr6TickLabels);
  sdr7.setLimits(1, 0, 400);
  sdr7.setStickToTicks(true);
  sdr7.setValue(102.345f);
  // notice that we are setting a value that is not 
  // exactly a tick when `stick to tick' is true, 
  // setValue will stick to nearest tick value
  
  panel = new GPanel(this, "Cool Sliders", 30,30,350,430);
  panel.setCollapsed(false);
  panel.add(sdr1);
  panel.add(sdr2);
  panel.add(sdr3);
  panel.add(sdr4);
  panel.add(sdr5);
  panel.add(sdr6);
  panel.add(sdr7);
}

void draw() {
  background(200);
}

void handleSliderEvents(GSlider slider) {
  println("integer value:" + slider.getValue() + " float value:" + slider.getValuef());
}
