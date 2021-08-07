package engine;

import org.jblas.*;

public class Camera {
	private FloatMatrix camMat;
	private float[] camPos;
	public int[] screenDims; //{width,height} in pixels
	private Projection projection;
	//left, right, forward, backwards
	public boolean[] translationState = {false, false, false, false};
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
	
	public void translate(float x, float y, float z) {
		//camPos[0] += x; camPos[1] += y; camPos[2] += z;
		camMat = Operations.translateMat(camMat, x, y, z);
		//Operations.printMat(camMat);
	}
	
	public void scale(float x, float y, float z) {
		camMat = Operations.scaleMat(camMat, x, y, z);
	}
	
	public void rotate(char dir, float degree) {
		camMat = Operations.rotateMat(camMat, dir, degree);
	}
	
	public float[] renderPoint(FloatMatrix point, FloatMatrix cameraMat) {
		FloatMatrix temp3D = new FloatMatrix(new float[] {point.get(0),point.get(1),point.get(2),1});
		FloatMatrix transformed = cameraMat.mmul(temp3D);
		if (transformed.get(2) > 0) { //check if in front
			FloatMatrix projected = projection.project(transformed);
			return new float[] {projected.get(0)*screenDims[0],projected.get(1)*screenDims[1]};
		} else {
			return null;
		}
	}
	
	public FloatMatrix getCamMat() {
		return camMat;
	}
}