package game;

import org.jblas.*;

public abstract class GameObject {
	FloatMatrix modelMat;
	FloatMatrix modelRotMat;
	FloatMatrix modelTransMat;
	float[] rotations;
	
	static float[][] identMat = new float[][] {{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
	
	public GameObject() {
		modelMat = new FloatMatrix(identMat);
		modelRotMat = new FloatMatrix(identMat);
		modelTransMat = new FloatMatrix(identMat);
		rotations = new float[3];
	}
	
	public float[] getPosition() {
		return new float[] {modelTransMat.get(0,3),modelTransMat.get(1,3),modelTransMat.get(2,3)};
	}
	
	public float[] getRotation() {
		return rotations;
	}
	
	public void setPosition(float[] newPos) {
		modelTransMat.put(0,3,newPos[0]);
		modelTransMat.put(1,3,newPos[1]);
		modelTransMat.put(2,3,newPos[2]);
		recompose();
	}
	
	public void setRotation(float[] newRot) {
		FloatMatrix newRotMat = new FloatMatrix(identMat);
		newRotMat = Operations.rotateMat(newRotMat, 'x', newRot[0]);
		newRotMat = Operations.rotateMat(newRotMat, 'y', newRot[1]);
		newRotMat = Operations.rotateMat(newRotMat, 'z', newRot[2]);
		modelRotMat = newRotMat;
		recompose();
	}
	
	public void translate(float x, float y, float z) {
		FloatMatrix tempRotM = new FloatMatrix(identMat);
		tempRotM = Operations.rotateMat(tempRotM, 'x', -rotations[0]);
		tempRotM = Operations.rotateMat(tempRotM, 'y', -rotations[1]);
		tempRotM = Operations.rotateMat(tempRotM, 'z', -rotations[2]);
		FloatMatrix inP = new FloatMatrix(new float[] {x,y,z,1});
		FloatMatrix transInP = tempRotM.mmul(inP);
		modelTransMat = Operations.translateMat(modelTransMat,transInP.get(0),transInP.get(1),transInP.get(2));
		this.recompose();
	}
	
	private void recompose() {
		modelMat = modelRotMat.mmul(modelTransMat);
	}
	
	
}
