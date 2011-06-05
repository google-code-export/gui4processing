/*
  Part of the GUI for Processing library 
  	http://www.lagers.org.uk/g4p/index.html
	http://gui4processing.googlecode.com/svn/trunk/

  Copyright (c) 2011 Peter Lager

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
 */

package guicomponents;

import java.awt.Point;

import processing.core.PApplet;

/**
 * The provides an extremely configurable GUI knob controller. GKnobOval
 * inherits from GKnob which inherits from GRoundControl so you should 
 * read the documentation for those classes as it also applies to 
 * GKnobOval. <br><br>
 * 
 * For circular knobs use GKnob rather than this class. <br>
 * 
 * Configurable options <br>
 *  Knob height and width (should be oval) <br>
 *  Start and end of rotation arc. <br>
 *  Bezel width with tick marks <br>
 *  <br>
 *  Documentation for the following can be found in GRoundControl <br>
 *  Range of values associated with rotating the knob <br>
 *  Rotation is controlled by mouse movement -  3 modes available <br>
 *  (a) angular -  drag round knob center <br>
 *  (b) horizontal - drag left or right <br>
 *  (c) vertical - drag up or down <br>
 *  User can specify mouse sensitivity for modes (b) and (c)
 *  Use can specify level of inertia to give smoother rotation
 *  
 * 	<b>Note</b>: Angles are measured clockwise starting in the positive x direction i.e.
 * <pre>
 *         270
 *          |
 *    180 --+-- 0
 *          |
 *          90
 * </pre>
 * 
 * @author Peter Lager
 *
 */
public class GKnobOval extends GKnob {

	protected Point pos = new Point();

	/**
	 * Create a GKnobOval control <br><br>
	 * 
	 * Will ensure that width and height are >= 20 pixels <br>
	 * 
	 * The arcStart and arcEnd represent the limits of rotation expressed in 
	 * degrees as shown above. For instance if you want a knob that rotates
	 * from 7 o'clock to 5 o'clock (via 12 o'clock) then arcStart = 120 and
	 * arcEnd = 60 degrees.
	 * 
	 * @param theApplet
	 * @param x left position of knob
	 * @param y top position of knob
	 * @param width width of knob
	 * @param height height of knob (if different from width - oval knob)
	 * @param arcStart start of rotation arc (in degrees)
	 * @param arcEnd end of rotation arc (in degrees)
	 */
	public GKnobOval(PApplet theApplet, int x, int y, int width, int height,
			int arcStart, int arcEnd) {
		super(theApplet, x, y, width, height, arcStart, arcEnd);

		// Calculate the display angle
		start = getDisplayAngle(PApplet.radians(aLow));
		end = getDisplayAngle(PApplet.radians(aHigh));

		// Calculate ticks
		calcTickMarkerPositions(nbrTickMarks);
	}

	public void draw(){
		if(!visible) return;

		Point p = new Point(0,0);
		calcAbsPosition(p);
		p.x += cx;
		p.y += cy;

		float rad = PApplet.radians(needleAngle), nrad;

		winApp.pushMatrix();
		winApp.translate(p.x, p.y);
		winApp.pushStyle();
		winApp.style(G4P.g4pStyle);

		// Draw bezel
		if(bezelWidth > 0){
			winApp.fill(winApp.color(128,48));
			winApp.noStroke();
			winApp.ellipse(0, 0, width, height);
			// draw darker arc for rotation range
			winApp.fill(winApp.color(128,80));
			winApp.arc(0, 0, width, height, start, end);
			// Draw active track
			winApp.fill(localColor.sdrTrack);
			winApp.noStroke();
			nrad = getDisplayAngle(rad);
			winApp.arc(0, 0, 2*barRadX, 2*barRadY, start, nrad);

			// Draw ticks
			winApp.stroke(localColor.sdrBorder);
			winApp.stroke(2);
			for(int i = 0; i < mark.length; i++){
				if(i == 0 || i == mark.length-1)
					winApp.strokeWeight(2.0f);
				else
					winApp.strokeWeight(1.2f);
				winApp.line(mark[i][0].x, mark[i][0].y,mark[i][1].x, mark[i][1].y);
			}
		}
		if(knobRadX > 0 ){
			// Draw knob centre
			winApp.stroke(localColor.sdrBorder);
			winApp.strokeWeight(1.0f);
			winApp.fill(localColor.sdrThumb);
			winApp.ellipse(0, 0, 2*knobRadX, 2*knobRadY);

			// Draw needle
			winApp.stroke(localColor.btnDown);
			winApp.strokeWeight(2.0f);
			nrad = getDisplayAngle(rad);
			winApp.line(0, 0,
					Math.round((knobRadX) * Math.cos(nrad)),
					Math.round((knobRadY) * Math.sin(nrad)) );
		}
		winApp.popStyle();
		winApp.popMatrix();
	}

	/**
	 * Calculates the point of intersection between the circumference of an ellipse and a line from
	 * position xp,yp to the geometric centre of the ellipse.
	 * @param circPos the returned intersection point
	 * @param xp x coordinate of point
	 * @param yp y coordinate of point
	 * @param hWidth half width of ellipse
	 * @param hHeight half height of ellipse
	 */
	protected void calcCircumferencePosition(Point circPos, float xp, float yp, float hWidth, float hHeight){
		double numer, denom;
		numer = hWidth * hHeight;
		denom = (float) Math.sqrt(hWidth*hWidth*yp*yp + hHeight*hHeight*xp*xp);
		circPos.x = (int) Math.round(xp * numer / denom);
		circPos.y = (int) Math.round(yp * numer / denom);
	}

	/**
	 * Takes a real angle and calculates the angle to be used when
	 * drawing an arc so that they match up.
	 * @param ra the real world amgle
	 * @return the angle for the arc method.
	 */
	protected float getDisplayAngle(double ra){
		double cosA = Math.cos(ra), sinA = Math.sin(ra);
		double diff = Math.abs(bezelRadX - bezelRadY);
		double eX = bezelRadX * cosA, eY = bezelRadY * sinA;

		if(bezelRadX > bezelRadY){
			eX -= diff * cosA;
			eY += diff * sinA;
		}
		else {
			eX += diff * cosA;
			eY -= diff * sinA;
		}
		float angle = (float) Math.atan2(eY, eX);
		while(ra - angle >= PI)
			angle += TWO_PI;
		while(angle - ra >= PI)
			angle -= TWO_PI;
		return angle;
	}

	/**
	 * Used to calculate the tick mark positions 
	 * @param nticks the number of actual markers
	 */
	protected void calcTickMarkerPositions(int nticks){
		mark = new Point[nticks][2];
		float cosine, sine;
		float ang = start, deltaAng = (end - start)/(nticks-1);
		for(int i = 0; i < nticks ; i++){
			mark[i][0] = new Point();
			mark[i][1] = new Point();
			cosine = (float) Math.cos(ang);
			sine = (float) Math.sin(ang);
			mark[i][0].x = Math.round(knobRadX * cosine);
			mark[i][0].y = Math.round(knobRadY * sine);
			if(i == 0 || i == nticks - 1)
				calcCircumferencePosition(mark[i][0], mark[i][0].x, mark[i][0].y, bezelRadX, bezelRadY);
			else 
				calcCircumferencePosition(mark[i][0], mark[i][0].x, mark[i][0].y, barRadX, barRadY);
			ang += deltaAng;
		}
		nbrTickMarks = nticks;
	}


}
