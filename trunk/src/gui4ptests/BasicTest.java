package gui4ptests;

import guicomponents.GButton;
import guicomponents.GCheckbox;
import guicomponents.GColor;
import guicomponents.GComponent;
import guicomponents.GFont;
import guicomponents.GHorzSlider;
import guicomponents.GLabel;
import guicomponents.GOption;
import guicomponents.GOptionGroup;
import guicomponents.GPanel;
import guicomponents.GSlider;
import guicomponents.GTextField;
import guicomponents.GUI;
import processing.core.PApplet;
import processing.core.PConstants;

@SuppressWarnings("serial")
public class BasicTest extends PApplet implements PConstants{

	private int bg = 0x888888;

	GLabel lblSegs, lblERad, lblPts, lblLRad;
	GSlider sdrSegs, sdrERad, sdrPts, sdrLRad;
	GCheckbox cbxWire;
	GOption optTorroid, optHelix;
	GOptionGroup optShape;
	GPanel p;
	GButton btn;
	GTextField txf1, txf2;
	
	public void setup(){
		size(600,300);

		GComponent.globalColor = GColor.getColor(this,  GUI.YELLOW);
		GComponent.globalFont = GFont.getDefaultFont(this);
		p = new GPanel(this, "Toroid Control Panel", 300, 30, 460, 90);
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

		btn = new GButton(this, "Start",360, 60,100,20);

		txf2 = new GTextField(this, "This is text field 2", 20,20,100,0);
		txf1 = new GTextField(this, "Start text",  200,60,100,0);
		p.add(txf1);
		
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
	}
	
	public void draw(){
		background(bg);
		this.pushMatrix();
		strokeWeight(2);
		stroke(255);
		line(mouseX, mouseY, width/2.0f, height/2.0f);
		this.popMatrix();
	}
	
//	public void handleTextFieldEvents(GTextField tfield){
//		switch(tfield.getEventType()){
//		case GTextField.CHANGED:
//			System.out.print("CHANGED         ");
//			break;
//		case GTextField.ENTERED:
//			System.out.print("ENTER PRESSED   ");
//			break;
//		case GTextField.SET:
//			System.out.print("SET             ");
//			break;
//		}
//		System.out.println(tfield.getText());
//	}
}
