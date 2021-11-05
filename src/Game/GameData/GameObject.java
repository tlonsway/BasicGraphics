package Game.GameData;

import org.jblas.*;

import Game.Graphics.*;

import java.util.*;

public class GameObject {
	
	String name;
	FloatMatrix modelMat;
	FloatMatrix modelRotMat;
	FloatMatrix modelTransMat;
	float[] rotations;
	World world;
	Mesh mesh;
	AABB bounds;
	int tempVAO;
	public float[] vertT;
	
	float[] velocity;
	float[] acceleration;
	public boolean falling = false;
	public boolean gravDisabled;
	
	final static float[][] identMat = new float[][] {{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
	
	
	public GameObject(String name) {
		this.name = name;
		modelMat = new FloatMatrix(identMat);
		modelRotMat = new FloatMatrix(identMat);
		modelTransMat = new FloatMatrix(identMat);
		rotations = new float[3];
		mesh = new Mesh(true);
		velocity = new float[3];
		acceleration = new float[3];
		gravDisabled = false;
	}
	
	public GameObject(String name, World world) {
		this.name = name;
		modelMat = new FloatMatrix(identMat);
		modelRotMat = new FloatMatrix(identMat);
		modelTransMat = new FloatMatrix(identMat);
		rotations = new float[3];
		this.world = world;
		mesh = new Mesh(false);
		bounds = genBounds(mesh);
		velocity = new float[3];
		acceleration = new float[3];
		gravDisabled = false;
	}
	
	public GameObject(String name, World world, Mesh mesh) {
		this.name = name;
		modelMat = new FloatMatrix(identMat);
		modelRotMat = new FloatMatrix(identMat);
		modelTransMat = new FloatMatrix(identMat);
		rotations = new float[3];
		this.world = world;
		this.mesh = mesh;
		float[] cent = findCenter(mesh);
		mesh.translate(-cent[0], -cent[1], -cent[2]);
		this.translate(cent[0], cent[1], cent[2]);
		bounds = genBounds(mesh);
		velocity = new float[3];
		acceleration = new float[3];
		gravDisabled = false;
	}
	
	public GameObject(String name, World world, Mesh mesh, AABB boundingBox) {
		System.exit(-21);
		this.name = name;
		modelMat = new FloatMatrix(identMat);
		modelRotMat = new FloatMatrix(identMat);
		modelTransMat = new FloatMatrix(identMat);
		rotations = new float[3];
		this.world = world;
		this.mesh = mesh;
		this.bounds = boundingBox;
		float[] cent = findCenter(mesh);
		mesh.translate(-cent[0], -cent[1], -cent[2]);
		this.translate(cent[0], cent[1], cent[2]);
		velocity = new float[3];
		acceleration = new float[3];
		gravDisabled = false;
	}
	
	public void disableGravity() {
		gravDisabled = true;
	}
	
	public float[] getVelocity() {
		return velocity;
	}
	
	public float[] getAcceleration() {
		return acceleration;
	}
	
	public void setVelocity(float[] f) {
		velocity = f;
	}
	
	public void setAcceleration(float[] f) {
		acceleration = f;
	}
	
	long timerVal;
	
	private void startTimer() {
		timerVal = System.nanoTime();
	}
	
	private void endTimer(String title) {
		System.out.println("TIME [" + title + "]: " + (System.nanoTime()-timerVal));
		startTimer();
	}
	
	
	
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
		//System.out.println("MinX: " + pMinT[0] + " MaxX: " + pMaxT[0]);
		
		return new AABB(pMinT,pMaxT,this);
	}
	
	private float[] findCenter(Mesh m) {
		ArrayList<Polygon> meshPolys = m.getPolygons();
		double xTot,yTot,zTot;
		xTot=yTot=zTot=0;
		for(Polygon p :meshPolys) {
			FloatMatrix[] points = p.getPoints();
			for(FloatMatrix tempPoint : points) {
				xTot += tempPoint.get(0);
				yTot += tempPoint.get(1);
				zTot += tempPoint.get(2);
			}
		}
		float xAve = (float)(xTot/(3*meshPolys.size()));
		float yAve = (float)(yTot/(3*meshPolys.size()));
		float zAve = (float)(zTot/(3*meshPolys.size()));
		return new float[] {xAve,yAve,zAve};
	}
	
	
	public void setVAO(int vao) {
		this.tempVAO = vao;
	}
	
	public int getVAO() {
		return tempVAO;
	}
	
	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
		float[] cent = findCenter(mesh);
		mesh.translate(-cent[0], -cent[1], -cent[2]);
		this.translate(cent[0], cent[1], cent[2]);
		bounds = genBounds(mesh);
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
	
	public float[] getModelMatFlat() {
		float[] ret = new float[16];
		int t = 0;
		for(int r=0;r<4;r++) {
			for(int c=0;c<4;c++) {
				ret[t] = modelMat.get(c,r);
				t++;
			}
		}
		return ret;
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
	
	public float[] getVertices() {
		ArrayList<Polygon> polys = mesh.getPolygons();
		float[] vertices = new float[polys.size()*27];
		int vertIn = 0;
		for(Polygon p : polys) {
			FloatMatrix[] polyPoints = p.getPoints();
			for(int pi2=0;pi2<3;pi2++) {
				for(int pi=0;pi<3;pi++) {
					vertices[vertIn] = polyPoints[pi2].get(pi);
					vertIn++;
				}
				float[] colT = p.fColor[pi2];
				vertices[vertIn] = colT[0];
				vertices[vertIn+1] = colT[1];
				vertices[vertIn+2] = colT[2];
				float[] normVec = p.getNorm().toArray();
				vertices[vertIn+3] = normVec[0];
				vertices[vertIn+4] = normVec[1];
				vertices[vertIn+5] = normVec[2];
				vertIn+=6;
			}
		}
		return vertices;
	}
	
	public float[] getVerticesOriginal() {
		ArrayList<Polygon> polys = mesh.getPolygons();
		float[] vertices = new float[polys.size()*18];
		int vertIn = 0;
		for(Polygon p : polys) {
			FloatMatrix[] polyPoints = p.getPoints();
			for(int pi2=0;pi2<3;pi2++) {
				for(int pi=0;pi<3;pi++) {
					vertices[vertIn] = polyPoints[pi2].get(pi);
					vertIn++;
				}
				float[] colT = p.fColor[pi2];
				vertices[vertIn] = colT[0];
				vertices[vertIn+1] = colT[1];
				vertices[vertIn+2] = colT[2];
				vertIn+=3;
			}
		}
		return vertices;
	}
	
	public int[] getIndices() {
		ArrayList<Polygon> polys = mesh.getPolygons();
		int[] indices = new int[polys.size()*18];
		return indices;
	}
	
	public AABB getBounds() {
		return bounds;
	}
	
	public void updatePosition() {
		velocity[0] += acceleration[0];
		velocity[1] += acceleration[1];
		velocity[2] += acceleration[2];
		this.translate(velocity[0], velocity[1], velocity[2]);
	}
	
	public boolean touchingGround() {
		//System.out.println("Object bound minimum at " + bounds.minY);
		//System.out.println("Object XZ: (" + getPosition()[0] + "," + getPosition()[2] + ")");
		/*int startPosX = (int)(getPosition()[0]);
		int startPosZ = (int)(getPosition()[2]);
		int widthX = (int)(bounds.getXWidth()+0.51f);
		int lengthZ = (int)(bounds.getZLength()+0.51f);
		
		if (widthX == 0) {
			widthX = 3;
		}
		if (lengthZ == 0) {
			lengthZ = 3;
		}
		startPosX -= 4*(int)((float)widthX);
		startPosZ -= 4*(int)((float)lengthZ);
		
		widthX *= 4;
		lengthZ *= 4;*/
		
		//System.out.println("startPosX: " + startPosX + ", startPosZ: " + startPosZ + ", widthX: " + widthX + ", lengthZ: " + lengthZ);
		
		//startTimer();
		//Mesh groundBelow = world.generateChunk(world.seed, (int)(getPosition()[0]), (int)(getPosition()[2]), (int)bounds.getXWidth(), (int)bounds.getZLength());
		//Mesh groundBelow = world.terrain;
		
		int xWid = (int)bounds.getXWidth();
		int zLen = (int)bounds.getZLength();
		
		if (xWid == 0) {
			xWid = 1;
		}
		if (zLen == 0) {
			zLen = 1;
		}
		
		Mesh groundBelow = world.generateChunk(world.seed, (int)(getPosition()[0]), (int)(getPosition()[2]), xWid, zLen, 1);
				
		//endTimer("Getting ground below");
		//Mesh groundBelow = world.generateChunk(world.seed, startPosX, startPosZ, widthX, lengthZ);
		//System.out.println("Number of polygons in mesh: " + groundBelow.getPolygons().size());
		if (bounds.intersectsMesh(groundBelow)) {
			//endTimer("Getting intersection");
			return true;
		}
		//endTimer("Getting intersection");
		return false;
		/*
		if ((getPosition()[1]+bounds.minY) <= world.getHeight(getPosition()[0],getPosition()[2])) {
			return true;
		}e
		return false;*/
	}
	
	private void recompose() {
		//modelMat = modelRotMat.mmul(modelTransMat);
		modelMat = modelTransMat.mmul(modelRotMat);
	}
	
}
