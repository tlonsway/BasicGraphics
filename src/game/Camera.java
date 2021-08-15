package game;

import org.jblas.*;

public class Camera {
	private FloatMatrix camMat;
	private FloatMatrix rotMat;
	private FloatMatrix transMat;
	private float[] rotations;
	//private 
	public int[] screenDims; //{width,height} in pixels
	//left, right, forward, backwards, up, down, shift
	public boolean[] translationState = {false, false, false, false, false, false, false};
	public Camera(int[] screenDims) {
		float[][] identMat = new float[][] {{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
		camMat = new FloatMatrix(identMat);
		rotMat = new FloatMatrix(identMat);
		transMat = new FloatMatrix(identMat);
		rotations = new float[3];
		this.screenDims = screenDims;
	}
	
	public Camera(float[] camPos, int[] screenDims) {
		float[][] identMat = new float[][] {{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
		camMat = new FloatMatrix(identMat);
		rotMat = new FloatMatrix(identMat);
		transMat = new FloatMatrix(identMat);
		rotations = new float[3];
		this.screenDims = screenDims;
	}
	
	public float[] getCamPos() {
		return new float[] {transMat.get(0,3),transMat.get(1,3),transMat.get(2,3)};
		//FloatMatrix inverse = Solve.pinv(camMat);
		//return new float[] {inverse.get(0,3),inverse.get(1,3),inverse.get(2,3)};
	}
	
	private void recompose() {
		camMat = rotMat.mmul(transMat);
		//camMat = transMat.mmul(rotMat);
	}
	
	public void translate(float x, float y, float z) {
		float[][] identMat = new float[][] {{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
		FloatMatrix tempRotM = new FloatMatrix(identMat);
		//tempRotM = Operations.rotateMat(tempRotM, 'x', -rotations[0]);
		tempRotM = Operations.rotateMat(tempRotM, 'y', -rotations[1]);
		//tempRotM = Operations.rotateMat(tempRotM, 'z', -rotations[2]);
		FloatMatrix inP = new FloatMatrix(new float[] {x,y,z,1});
		FloatMatrix transInP = tempRotM.mmul(inP);
		transMat = Operations.translateMat(transMat,transInP.get(0),transInP.get(1),transInP.get(2));
		this.recompose();
	}
	
	public void scale(float x, float y, float z) {
		//camMat = Operations.scaleMat(camMat, x, y, z);
		System.out.println("NOTHING HAPPENS, CODE THIS IF NEEDED");
	}
	
	public void rotate(char dir, float degree) {
		//camMat = Operations.rotateMat(camMat, dir, degree);
		rotMat = Operations.rotateMat(rotMat, dir, degree);
		int in = 0;
		switch(dir) {
		case('x'): in=0; break;
		case('y'): in=1; break;
		case('z'): in=2; break;
		}
		rotations[in]+=degree;
		this.recompose();
	}
	
	public void rotate(char dir, float degree, boolean flip) {
		//camMat = Operations.rotateMat(camMat, dir, degree, flip);
		rotMat = Operations.rotateMat(rotMat, dir, degree, flip);
		int in = 0;
		switch(dir) {
		case('x'): in=0; break;
		case('y'): in=1; break;
		case('z'): in=2; break;
		}
		rotations[in]+=degree;
		this.recompose();
	}
	
	public void rotate(float degX, float degY, float degZ) {
		//camMat = Operations.rotateMatAll(camMat, degX, degY, degZ);
		rotMat = Operations.rotateMatAll(rotMat, degX, degY, degZ);
		System.out.println("ADD SWITCH CASE BEFORE USING");
		this.recompose();
	}
	
	public FloatMatrix getCamMat() {
		return camMat;
	}
	
	public FloatMatrix getRotMat() {
		return rotMat;
	}
	
	public FloatMatrix getTransMat() {
		return transMat;
	}
	
	public float[] getRotations() {
		return rotations;
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