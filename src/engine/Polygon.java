package engine;

import org.jblas.*;
import java.awt.Color;

public class Polygon implements Comparable {
	private FloatMatrix[] points;
	private int[] color;
	private float distance;
	FloatMatrix[] renderedPoints;
	
	public Polygon(float[] p1, float[] p2, float[] p3) {
		points = new FloatMatrix[3];
		float[] tAc = p1;
		for(int i=0;i<3;i++) {
			switch(i) {
			case(1): tAc = p2; break;
			case(2): tAc = p3; break;
			}
			FloatMatrix tFMat = new FloatMatrix(tAc);
			points[i] = tFMat;
		}
	}
	
	public Polygon(float[] p1, float[] p2, float[] p3, int[] color) {
		this.color = color;
		points = new FloatMatrix[3];
		float[] tAc = p1;
		for(int i=0;i<3;i++) {
			switch(i) {
			case(1): tAc = p2; break;
			case(2): tAc = p3; break;
			}
			FloatMatrix tFMat = new FloatMatrix(tAc);
			points[i] = tFMat;
		}
	}
	
	public Polygon(FloatMatrix p1, FloatMatrix p2, FloatMatrix p3) {
		points = new FloatMatrix[3];
		points[0] = p1; points[1] = p2; points[2] = p3;
	}
	
	public Polygon(FloatMatrix p1, FloatMatrix p2, FloatMatrix p3, int[] color) {
		this.color = color;
		points = new FloatMatrix[3];
		points[0] = p1; points[1] = p2; points[2] = p3;
	}
	
	public void setColor(int[] col) {
		this.color = col;
	}
	
	public int[] getColorAsInt() {
		return color;
	}
	
	public Color getColorAsColor() {
		return new Color(color[0],color[1],color[2]);
	}
	
	public FloatMatrix[] getPoints() {
		return points;
	}
	
	public FloatMatrix getNorm() {
		FloatMatrix v1 = points[0];
		FloatMatrix v2 = points[1];
		float s1 = (v1.get(1)*v2.get(2))-(v1.get(2)*v2.get(1));
        float s2 = (v1.get(2)*v2.get(0))-(v1.get(0)*v2.get(2));
        float s3 = (v1.get(0)*v2.get(1))-(v1.get(1)*v2.get(0));
        return new FloatMatrix(new float[] {s1,s2,s3});
	}
	
	public FloatMatrix[] getRendered(Camera cam, FloatMatrix camMat) {
		try {
			
			float[] ren1 = cam.renderPoint(points[0],camMat);
			float[] ren2 = cam.renderPoint(points[1],camMat);
			float[] ren3 = cam.renderPoint(points[2],camMat);
			float closest = ren1[2];
			if (ren2[2] < closest) {
				closest = ren2[2];
			}
			if (ren3[2] < closest) {
				closest = ren3[2];
			}
			distance = closest;
			FloatMatrix[] ret = new FloatMatrix[3];
			ret[0] = new FloatMatrix(ren1);
			ret[1] = new FloatMatrix(ren2);
			ret[2] = new FloatMatrix(ren3);
			renderedPoints = ret;
			return ret;
		} catch (NullPointerException e) {
			return null;
		}
	}
	
	public int compareTo(Object other) {
		Polygon op = (Polygon)other;
		if (this.distance < op.getDistance()) {
			return 1;
		} else if (this.distance == op.getDistance()) {
			return 0;
		} else {
			return -1;
		}
	}
	
	public float getDistance() {
		return distance;
	}
	
	public FloatMatrix[] getRenderedPoints() {
		return renderedPoints;
	}
	
}