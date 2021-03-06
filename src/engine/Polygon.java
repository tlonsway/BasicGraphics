package engine;

import java.util.*;
import org.jblas.*;
import java.awt.Color;

public class Polygon implements Comparable {
	private FloatMatrix[] points;
	float[][] fColor;
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
	
	public Polygon(float[] p1, float[] p2, float[] p3, float[] color) {
		fColor = new float[][] {color, color, color};
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
		float[] colors = new float[] {color[0]/256.0f, color[1]/256.0f, color[2]/256.0f};
		fColor = new float[][] {colors, colors, colors};
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
	
	public void setFColor(float[] c) {
		fColor = new float[][] {c, c, c};
	}
	
	public void setFColors(float[][] c) {
		fColor = c;
	}
	
	public Polygon(FloatMatrix p1, FloatMatrix p2, FloatMatrix p3) {
		points = new FloatMatrix[3];
		points[0] = p1; points[1] = p2; points[2] = p3;
	}
	
	public Polygon(FloatMatrix p1, FloatMatrix p2, FloatMatrix p3, float[] color) {
		fColor = new float[][] {color, color, color};
		points = new FloatMatrix[3];
		points[0] = p1; points[1] = p2; points[2] = p3;
	}
	
	public int[] getColorAsInt(int vertex) {
		return new int[] {(int)(fColor[vertex][0]*255),(int)(fColor[vertex][0]*255),(int)(fColor[vertex][0]*255)};
	}
	
	public Color getColorAsColor(int vertex) {
		return new Color((int)(fColor[vertex][0]*255),(int)(fColor[vertex][0]*255),(int)(fColor[vertex][0]*255));
	}
	
	public FloatMatrix[] getPoints() {
		return points;
	}
	
	public void translate(float x, float y, float z) {
		for(int i = 0; i < points.length; i++) {
			float p1, p2, p3;
			p1 = points[i].get(0)+x;
			p2 = points[i].get(1)+y;
			p3 = points[i].get(2)+z;
			points[i] = new FloatMatrix(new float[] {p1, p2, p3});
		}
	}
	
	public void rotate(float[] rotationPoint, char axis, float angle) {
		for(int i = 0; i < points.length; i++) {
			FloatMatrix t = new FloatMatrix(new float[] {points[i].get(0)-rotationPoint[0], points[i].get(1)-rotationPoint[1], points[i].get(2)-rotationPoint[2]} );
			FloatMatrix t2 = Game.Graphics.Operations.rotatePoint(t, axis, angle);
			points[i].put(0, t2.get(0)+rotationPoint[0]);
			points[i].put(1, t2.get(1)+rotationPoint[1]);
			points[i].put(2, t2.get(2)+rotationPoint[2]);
		}
	}
	public FloatMatrix getNorm() {
		FloatMatrix v1 = points[0];
		FloatMatrix v2 = points[1];
		float s1 = (v1.get(1)*v2.get(2))-(v1.get(2)*v2.get(1));
        float s2 = (v1.get(2)*v2.get(0))-(v1.get(0)*v2.get(2));
        float s3 = (v1.get(0)*v2.get(1))-(v1.get(1)*v2.get(0));
        return new FloatMatrix(new float[] {s1,s2,s3});
	}
	
	public float[][] getFColors(){
		return fColor;
	}
	
	public Polygon clone() {
		Polygon p = new Polygon(new float[] {points[0].get(0), points[0].get(1), points[0].get(2)}, new float[] {points[1].get(0), points[1].get(1), points[1].get(2)}, new float[] {points[2].get(0), points[2].get(1), points[2].get(2)});
		p.setFColors(getFColors());
		return p;
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
			renderedPoints = null;
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
	
	public boolean equals(Object o) {
		if(o instanceof Polygon) {
			Polygon poly = (Polygon)o;
			FloatMatrix[] pts = poly.getPoints();
			ArrayList<FloatMatrix> localpts = new ArrayList<FloatMatrix>();
			Collections.addAll(localpts, points);
			for(FloatMatrix fm: pts) {
				if(!localpts.contains(fm)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	public float getDistance() {
		return distance;
	}
	
	public FloatMatrix[] getRenderedPoints() {
		return renderedPoints;
	}
	
}