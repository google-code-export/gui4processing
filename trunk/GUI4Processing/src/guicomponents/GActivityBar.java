package guicomponents;

import processing.core.PApplet;

public class GActivityBar extends GComponent {

	final protected int NBR_THUMBS = 4;
	
	protected int[] thumbX = new int[NBR_THUMBS];
	protected int[] thumbDeltaX = new int[NBR_THUMBS];
	protected int thumbY, thumbDiameter;
	protected int trackHeight;
	protected GTimer timer;
	
	GActivityBar(PApplet app, int x, int y, int width, int height, int time){
		height = Math.max(height,10);
		trackHeight = height - 6;
		width = Math.max(height * 6, width);
		thumbX[0] = x + width/2 - NBR_THUMBS * trackHeight /2;
		thumbDeltaX[0] = 1;
		for(int i=1; i<NBR_THUMBS; i++){
			thumbX[i] = thumbX[i-1]- trackHeight + 2;
			thumbDeltaX[i] = 1;
		}
		
	}
	
	
	
	class GActivityBarThumb {
		private int x,y;
		private int diameter;
		private int col;
		
		
	}
}
