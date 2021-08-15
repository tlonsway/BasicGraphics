package game;

import org.jblas.*;

public abstract class GameObject {
	FloatMatrix modelMat;
	FloatMatrix modelRotMat;
	FloatMatrix modelTransMat;
	float[] rotations;
	World world;
	Mesh mesh;
	AABB bounds;
	
	final static float[][] identMat = new float[][] {{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
	
	public GameObject(World world) {
		modelMat = new FloatMatrix(identMat);
		modelRotMat = new FloatMatrix(identMat);
		modelTransMat = new FloatMatrix(identMat);
		rotations = new float[3];
		this.world = world;
		mesh = new Mesh();
	}
	
	public GameObject(World world, Mesh mesh) {
		modelMat = new FloatMatrix(identMat);
		modelRotMat = new FloatMatrix(identMat);
		modelTransMat = new FloatMatrix(identMat);
		rotations = new float[3];
		this.world = world;
		this.mesh = mesh;
	}
	
	public GameObject(World world, Mesh mesh, AABB boundingBox) {
		modelMat = new FloatMatrix(identMat);
		modelRotMat = new FloatMatrix(identMat);
		modelTransMat = new FloatMatrix(identMat);
		rotations = new float[3];
		this.world = world;
		this.mesh = mesh;
		this.bounds = boundingBox;
	}
	
	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}
	
	public float[] getPosition() {
		return new float[] {modelTransMat.get(0,3),modelTransMat.get(1,3),modelTransMat.get(2,3)};
	}
	
	public float[] getRotation() {
		return rotations;
	}
	
	public FloatMatrix getModelMat() {
		return modelMat;
	}
	
	public FloatMatrix getModelRotMat() {
		return modelRotMat;
	}
	
	public FloatMatrix getModelTransMat() {
		return modelTransMat;
	}
	
	public void setPosition(float[] newPos) {
		modelTransMat.put(0,3,newPos[0]);
		modelTransMat.put(1,3,newPos[1]);
		modelTransMat.put(2,3,newPos[2]);
		this.recompose();
	}
	
	public void setRotation(float[] newRot) {
		FloatMatrix newRotMat = new FloatMatrix(identMat);
		newRotMat = Operations.rotateMat(newRotMat, 'x', newRot[0]);
		newRotMat = Operations.rotateMat(newRotMat, 'y', newRot[1]);
		newRotMat = Operations.rotateMat(newRotMat, 'z', newRot[2]);
		modelRotMat = newRotMat;
		this.recompose();
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
	
	public void rotate(char dir, float degree, boolean flip) {
		//flip will switch the side of matrix multiplication, needed for chaining 2-axis rotation without affecting 3rd axis
		modelRotMat = Operations.rotateMat(modelRotMat, dir, degree, flip);
		int in = 0;
		switch(dir) {
		case('x'): in=0; break;
		case('y'): in=1; break;
		case('z'): in=2; break;
		}
		rotations[in]+=degree;
		this.recompose();
	}
	
	public boolean touchingGround() {
		if (bounds.minY-.05f <= world.getHeight(getPosition()[0],getPosition()[2])) {
			return true;
		}
		return false;
	}
	
	private void recompose() {
		modelMat = modelRotMat.mmul(modelTransMat);
	}
	
}
