package Game.GameContent;

import java.util.ArrayList;

import org.jblas.FloatMatrix;

import Game.Graphics.*;

public class Model {
	
	boolean vaoQualityEnabled;
	
	VAOStorage vao;
	AABB bounds;
	FloatMatrix modelMat;
	FloatMatrix modelRotMat;
	FloatMatrix modelTransMat;
	float[] modelMatFlat;
	float[] position;
	float[] rotation;
	float[] velocity;
	float[] acceleration;
	
	final static float[][] identMat = new float[][] {{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
	
	public Model(VAOStorage vao, Mesh m, float[] position, float[] rotation) {
		this.vao = vao;
		this.bounds = genBounds(m);
		this.velocity = new float[3];
		this.acceleration = new float[3];
		modelMat = new FloatMatrix(identMat);
		updateFlatMat();
		modelRotMat = new FloatMatrix(identMat);
		modelTransMat = new FloatMatrix(identMat);
		this.setPosition(position);
		this.setRotation(rotation);
		vaoQualityEnabled = false;
	}
	
	public float distanceToXZ(Camera cam) {
		float[] camPosT = cam.getCamPos();
		float[] camPos = new float[] {-camPosT[0],-camPosT[2]};
		float[] thisPos = new float[] {position[0],position[2]};
		return distance2(thisPos,camPos);
	}
	
	public float distanceToXZ(Model m) {
		float[] thisPos = new float[] {position[0],position[2]};
		float[] oPos = new float[] {m.getPosition()[0],m.getPosition()[2]};
		return distance2(thisPos,oPos);
	}
	
	public float distanceTo(Camera cam) {
		float[] camPosT = cam.getCamPos();
		float[] camPos = new float[] {-camPosT[0],-camPosT[1],-camPosT[2]};
		return distance3(position,camPos);
	}
	public float distanceTo(Model m) {
		return distance3(position,m.getPosition());
	}
	
	private float distance2(float[] p1, float[] p2) {
		float t1 = (p1[0]-p2[0]);
		float t2 = (p1[1]-p2[1]);
		float tF = (t1*t1)+(t2*t2);
		return (float)Math.sqrt(tF);
	}
	private float distance3(float[] p1, float[] p2) {
		float t1 = (p1[0]-p2[0]);
		float t2 = (p1[1]-p2[1]);
		float t3 = (p1[2]-p2[2]);
		float tF = (t1*t1)+(t2*t2)+(t3*t3);
		return (float)Math.sqrt(tF);
	}
	
	//update the physical position of the object based on acceleration and velocity
	public void updatePhysicsPosition() {
		velocity[0] += acceleration[0];
		velocity[1] += acceleration[1];
		velocity[2] += acceleration[2];
		this.translate(velocity[0], velocity[1], velocity[2]);
	}
	
	//set the position of the object
	public void setPosition(float[] newPos) {
		modelTransMat.put(0,3,newPos[0]);
		modelTransMat.put(1,3,newPos[1]);
		modelTransMat.put(2,3,newPos[2]);
		this.recompose();
	}
	
	//set the rotation of the object
	public void setRotation(float[] newRot) {
		FloatMatrix newRotMat = new FloatMatrix(identMat);
		newRotMat = Operations.rotateMat(newRotMat, 'x', newRot[0]);
		newRotMat = Operations.rotateMat(newRotMat, 'y', newRot[1]);
		newRotMat = Operations.rotateMat(newRotMat, 'z', newRot[2]);
		modelRotMat = newRotMat;
		this.recompose();
	}
	
	//translate the object
	public void translate(float x, float y, float z) {
		FloatMatrix tempRotM = new FloatMatrix(identMat);
		tempRotM = Operations.rotateMat(tempRotM, 'x', -rotation[0]);
		tempRotM = Operations.rotateMat(tempRotM, 'y', -rotation[1]);
		tempRotM = Operations.rotateMat(tempRotM, 'z', -rotation[2]);
		FloatMatrix inP = new FloatMatrix(new float[] {x,y,z,1});
		FloatMatrix transInP = tempRotM.mmul(inP);
		modelTransMat = Operations.translateMat(modelTransMat,transInP.get(0),transInP.get(1),transInP.get(2));
		this.recompose();
	}
	
	//rotate the object
	public void rotate(char dir, float degree, boolean flip) {
		//flip will switch the side of matrix multiplication, needed for chaining 2-axis rotation without affecting 3rd axis
		modelRotMat = Operations.rotateMat(modelRotMat, dir, degree, flip);
		int in = 0;
		switch(dir) {
		case('x'): in=0; break;
		case('y'): in=1; break;
		case('z'): in=2; break;
		}
		rotation[in]+=degree;
		this.recompose();
	}
	
	
	//regenerate a modelMat from rotation and translation matrices
	private void recompose() {
		modelMat = modelTransMat.mmul(modelRotMat);
		updateFlatMat();
		position = new float[] {modelTransMat.get(0,3),modelTransMat.get(1,3),modelTransMat.get(2,3)};
	}
	
	//regenerate the flattened version of the modelMat
	private void updateFlatMat() {
		float[] temp = new float[16];
		int t = 0;
		for(int r=0;r<4;r++) {
			for(int c=0;c<4;c++) {
				temp[t] = modelMat.get(c,r);
				t++;
			}
		}
		modelMatFlat = temp;
	}
	
	//getter methods
	public VAOStorage getVAOStorage() {
		return vao;
	}
	public boolean vaoQualityEnabled() {
		return vaoQualityEnabled;
	}
	
	public AABB getBounds() {
		return bounds;
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
	public float[] getModelMatFlat() {
		return modelMatFlat;
	}
	public float[] getPosition() {
		return position;
	}
	public float[] getRotation() {
		return rotation;
	}
	public float[] getVelocity() {
		return velocity;
	}
	public float[] getAcceleration() {
		return acceleration;
	}
	
	//setter methods
	public void setVelocity(float[] f) {
		velocity = f;
	}
	public void setAcceleration(float[] f) {
		acceleration = f;
	}
	
	//generate a bounding box from an input mesh
	private AABB genBounds(Mesh m) {
		ArrayList<Polygon> polys = m.getPolygons();
		float[] pMinT = new float[] {polys.get(0).getPoints()[0].get(0),polys.get(0).getPoints()[0].get(1),polys.get(0).getPoints()[0].get(2)};
		float[] pMaxT = new float[] {polys.get(0).getPoints()[0].get(0),polys.get(0).getPoints()[0].get(1),polys.get(0).getPoints()[0].get(2)};
		for(Polygon poly : polys) {
			FloatMatrix[] points = poly.getPoints();
			for(FloatMatrix point : points) {
				float pXT = point.get(0);
				float pYT = point.get(1);
				float pZT = point.get(2);
				if(pXT < pMinT[0]) {
					pMinT[0] = pXT;
				}
				if(pXT > pMaxT[0]) {
					pMaxT[0] = pXT;
				}
				
				if(pYT < pMinT[1]) {
					pMinT[1] = pYT;
				}
				if(pYT > pMaxT[1]) {
					pMaxT[1] = pYT;
				}
				
				if(pZT < pMinT[2]) {
					pMinT[2] = pZT;
				}
				if(pZT > pMaxT[2]) {
					pMaxT[2] = pZT;
				}
			}
		}
		return new AABB(pMinT,pMaxT,this);
	}
	
	
}
