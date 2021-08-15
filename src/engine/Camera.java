package engine;

import org.jblas.*;
import org.jblas.Solve;

public class Camera {
	private FloatMatrix camMat;
	private float[] camPos;
	public int[] screenDims; //{width,height} in pixels
	private Projection projection;
	//left, right, forward, backwards, up, down, shift
	public boolean[] translationState = {false, false, false, false, false, false, false};
	public Camera(int[] screenDims) {
		float[][] identMat = new float[][] {{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
		camMat = new FloatMatrix(identMat);
		camPos = new float[] {0,0,0};
		this.screenDims = screenDims;
		this.projection = new Projection();
	}
	
	public Camera(float[] camPos, int[] screenDims) {
		float[][] identMat = new float[][] {{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
		camMat = new FloatMatrix(identMat);
		this.camPos = camPos;
		this.screenDims = screenDims;
		this.projection = new Projection();
	}
	
	public void setY(float newY) {
		camMat.put(1, 3, newY);
	}
	
	public void translate(float x, float y, float z) {
		camPos[0] += x; camPos[1] += y; camPos[2] += z;
		camMat = Operations.translateMat(camMat, x, y, z);
		//Operations.printMat(camMat);
	}
	
	public void scale(float x, float y, float z) {
		camMat = Operations.scaleMat(camMat, x, y, z);
	}
	
	public void rotate(char dir, float degree) {
		camMat = Operations.rotateMat(camMat, dir, degree);
	}
	
	public float[] getCamPos() {
		FloatMatrix inverse = Solve.pinv(camMat);
		return new float[] {inverse.get(0,3),inverse.get(1,3),inverse.get(2,3)};
	}
	
	public float[] renderPoint(FloatMatrix point, FloatMatrix cameraMat) {
		FloatMatrix temp3D = new FloatMatrix(new float[] {point.get(0),point.get(1),point.get(2),1});
		FloatMatrix transformed = cameraMat.mmul(temp3D);
		if (transformed.get(2) > 0) { //check if in front
			FloatMatrix projected = projection.project(transformed);
			return new float[] {projected.get(0)*screenDims[0],projected.get(1)*screenDims[1],transformed.get(2)};
		} else {
			return null;
		}
	}
	
	public FloatMatrix getCamMat() {
		return camMat;
	}
	
	public float[] getCamMatFlat() {
		float[] ret = new float[16];
		int t=0;
		for(int r=0;r<4;r++) {
			for(int c=0;c<4;c++) {
				ret[t] = camMat.get(r,c);
				t++;
			}
		}
		return ret;
	}
}