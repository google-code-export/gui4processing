package guicomponents;

import guicomponents.GComponent;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import processing.core.PApplet;
import processing.core.PImage;

public class GControlWindow extends Frame{

	public GCWinApplet embed;

	private int dWidth, dHeight;
	private int bkColor;
	private String winName;
	
	public GControlWindow(String name, int x, int y, int w, int h, int background) {
		super(name);
		winName = name;
		dWidth = w;
		dHeight = h;
		bkColor = background;

		embed = new GCWinApplet();
		embed.frame = this;
		embed.frame.setResizable(true);
		
		embed.resize(dWidth, dHeight);
		embed.setPreferredSize(new Dimension(dWidth, dHeight));
		embed.setMinimumSize(new Dimension(dWidth, dHeight));
		
		// add the PApplet to the Frame
		setLayout(new BorderLayout());
		add(embed, BorderLayout.CENTER);
	
		// ensures that the animation thread is started and
		// that other internal variables are properly set.
		embed.init();

		// add an exit on close listener
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				// close this frame
				dispose();
			}
		});
		
		pack();
		setLocation(x,y);
		setVisible(true);
	}
	
	public void add(GComponent component){
		component.changeWindow(embed);
		
	}
	public class GCWinApplet extends PApplet {

		public void setup() {
			size(dWidth, dHeight);
			System.out.println(dWidth+" X " +dHeight);
			frameRate(15);
		}

		public void draw() {
//			System.out.println("DRAW "+winName); //+"   "+w+" X "+h);
			background(bkColor);
			strokeWeight(1);
			stroke(255,255,0);
			noFill();
			rect(0,0,dWidth-1,dHeight-1);
		}
	}
}
