package game;

import org.jblas.*;

public class Camera {
	private FloatMatrix camMat;
	public int[] screenDims; //{width,height} in pixels
	//left, right, forward, backwards, up, down, shift
	public boolean[] translationState = {false, false, false, false, false, false, false};
	public Camera(int[] screenDims) {
		float[][] identMat = new float[][] {{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
		camMat = new FloatMatrix(identMat);
		this.screenDims = screenDims;
	}
	
	public Camera(float[] camPos, int[] screenDims) {
		float[][] identMat = new float[][] {{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
		camMat = new FloatMatrix(identMat);
		this.screenDims = screenDims;
	}
	
	public void translate(float x, float y, float z) {
		camMat = Operations.translateMat(camMat, x, y, z);
	}
	
	public void scale(float x, float y, float z) {
		camMat = Operations.scaleMat(camMat, x, y, z);
	}
	
	public void rotate(char dir, float degree) {
		camMat = Operations.rotateMat(camMat, dir, degree);
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