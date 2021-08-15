package game;

import org.jblas.*;

public class Camera {
	private FloatMatrix camMat;
	private FloatMatrix rotMat;
	private FloatMatrix transMat;
	//private 
	public int[] screenDims; //{width,height} in pixels
	//left, right, forward, backwards, up, down, shift
	public boolean[] translationState = {false, false, false, false, false, false, false};
	public Camera(int[] screenDims) {
		float[][] identMat = new float[][] {{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
		camMat = new FloatMatrix(identMat);
		rotMat = new FloatMatrix(identMat);
		transMat = new FloatMatrix(identMat);
		this.screenDims = screenDims;
	}
	
	public Camera(float[] camPos, int[] screenDims) {
		float[][] identMat = new float[][] {{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
		camMat = new FloatMatrix(identMat);
		rotMat = new FloatMatrix(identMat);
		transMat = new FloatMatrix(identMat);
		this.screenDims = screenDims;
	}
	
	public float[] getCamPos() {
		FloatMatrix inverse = Solve.pinv(camMat);
		return new float[] {inverse.get(0,3),inverse.get(1,3),inverse.get(2,3)};
	}
	
	private void recomposeCamMat() {
		camMat = rotMat.transpose().mmul(transMat);
	}
	
	public void translate(float x, float y, float z) {
		transMat = Operations.translateMat(transMat,x,y,z);
		//camMat = Operations.translateMat(camMat, x, y, z);
	}
	
	public void scale(float x, float y, float z) {
		//camMat = Operations.scaleMat(camMat, x, y, z);
		System.out.println("NOTHING HAPPENS, CODE THIS IF NEEDED");
	}
	
	public void rotate(char dir, float degree) {
		//camMat = Operations.rotateMat(camMat, dir, degree);
		rotMat = Operations.rotateMat(rotMat, dir, degree);
	}
	
	public void rotate(char dir, float degree, boolean flip) {
		//camMat = Operations.rotateMat(camMat, dir, degree, flip);
		rotMat = Operations.rotateMat(rotMat, dir, degree, flip);
	}
	
	public void rotate(float degX, float degY, float degZ) {
		//camMat = Operations.rotateMatAll(camMat, degX, degY, degZ);
		rotMat = Operations.rotateMatAll(rotMat, degX, degY, degZ);
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