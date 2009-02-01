package gui4ptests;

/**
 * Interactive Toroid
 * by Ira Greenberg. 
 * 
 * Illustrates the geometric relationship between Toroid, Sphere, and Helix
 * 3D primitives, as well as lathing principal.
 */

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PVector;

class Toroid implements PConstants {

	private PApplet app;

	int pts = 40; 
	float angle = 0;
	float radius = 60.0f;

	// lathe segments
	int segments = 60;
	float latheAngle = 0;
	float latheRadius = 100.0f;

	//vertices
	PVector vertices[], vertices2[];

	// for shaded or wireframe rendering 
	boolean isWireFrame = false;

	// for optional helix
	boolean isHelix = false;
	float helixOffset = 5.0f;


	boolean modelChange = false;

	public Toroid(PApplet theApplet){
		app = theApplet;
		fillVertexArrays();
	}

	public void setSegmentDetail(int segments){
		this.segments = segments; 
		fillVertexArrays();
	}

	public void setEllipseDetail(int points){
		pts = points;
		fillVertexArrays();
	}

	public void setEllipseRadius(int eradius){
		radius = eradius;
		fillVertexArrays();
	}

	public void setLatheRadius(int lradius){
		latheRadius = lradius;
		fillVertexArrays();
	}

	public void setIsHelix(boolean helix){
		isHelix = helix;
		fillVertexArrays();
	}

	public void setIsWire(boolean wire){
		isWireFrame = wire;
	}

	void fillVertexArrays(){
		// initialize point arrays
		vertices = new PVector[pts+1];
		vertices2 = new PVector[pts+1];

		// fill arrays
		for(int i=0; i<=pts; i++){
			vertices[i] = new PVector();
			vertices2[i] = new PVector();
			vertices[i].x = (float) (latheRadius + Math.sin(Math.toRadians(angle))*radius);
			if (isHelix){
				vertices[i].z = (float) (Math.cos(Math.toRadians(angle))*radius-(helixOffset* segments)/2);
			} 
			else{
				vertices[i].z = (float) (Math.cos(Math.toRadians(angle))*radius);
			}
			angle+=360.0/pts;
		}
	}

	void draw(){
		if(isHelix)
			fillVertexArrays();
		app.pushMatrix();
		if (isWireFrame){
			app.stroke(255, 255, 150);
			app.strokeWeight(1);
			app.noFill();
		} 
		else {
			app.noStroke();
			app.fill(150, 195, 125);
		}

		// draw toroid
		latheAngle = 0;
		for(int i=0; i<=segments; i++){
			app.beginShape(QUAD_STRIP);
			for(int j=0; j<=pts; j++){
				if (i>0){
					app.vertex(vertices2[j].x, vertices2[j].y, vertices2[j].z);
				}
				vertices2[j].x = (float) (Math.cos(Math.toRadians(latheAngle))*vertices[j].x);
				vertices2[j].y = (float) (Math.sin(Math.toRadians(latheAngle))*vertices[j].x);
				vertices2[j].z = vertices[j].z;
				// optional helix offset
				if (isHelix){
					vertices[j].z+=helixOffset;
				} 
				app.vertex(vertices2[j].x, vertices2[j].y, vertices2[j].z);
			}
			// create extra rotation for helix
			if (isHelix){
				latheAngle+=720.0/segments;
			} 
			else {
				latheAngle+=360.0/segments;
			}
			app.endShape();
		}
		app.popMatrix();
	}

}










