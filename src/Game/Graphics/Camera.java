package Game.Graphics;

import org.jblas.*;

import Game.GameData.World;

public class Camera {
	private FloatMatrix camMat;
	private FloatMatrix rotMat;
	private FloatMatrix transMat;
	private float[] rotations;
	public int[] screenDims; //{width,height} in pixels
	World world;
	
	AABB bounds;
	
	float[] velocity;
	float[] acceleration;
	public boolean falling = false;
	public boolean jumping = false;
	
	public Camera(int[] screenDims) {
		float[][] identMat = new float[][] {{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
		camMat = new FloatMatrix(identMat);
		rotMat = new FloatMatrix(identMat);
		transMat = new FloatMatrix(identMat);
		rotations = new float[3];
		this.screenDims = screenDims;
		bounds = new AABB(new float[] {-1.0f,-1.0f,-1.0f},new float[] {1.0f,0.5f,1.0f});
		//GameObject cameraObj = new GameObject("Temp");
		//cameraObj.setPosition(this.getCamPos());
		//bounds.setObject(cameraObj);
		bounds.setCamera(this);
		velocity = new float[3];
		acceleration = new float[3];
	}
	
	public Camera(float[] camPos, int[] screenDims, World world) {
		System.exit(-22);
		float[][] identMat = new float[][] {{1,0,0,0},{0,1,0,0},{0,0,1,0},{0,0,0,1}};
		camMat = new FloatMatrix(identMat);
		rotMat = new FloatMatrix(identMat);
		transMat = new FloatMatrix(identMat);
		rotations = new float[3];
		this.screenDims = screenDims;
		velocity = new float[3];
		acceleration = new float[3];
	}
	
	public void setWorld(World w) {
		this.world = w;
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
	public void updatePosition() {
		velocity[0] += acceleration[0];
		velocity[1] += acceleration[1];
		velocity[2] += acceleration[2];
		this.translate(velocity[0], velocity[1], velocity[2]);
	}
	
	public void setPosition(float[] newPos) {
		transMat.put(0,3,newPos[0]);
		transMat.put(1,3,newPos[1]);
		transMat.put(2,3,newPos[2]);
		this.recompose();
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
	
	public boolean touchingGround() {
		Mesh groundBelow = world.generateChunk(world.seed, (int)(getCamPos()[0]), (int)(getCamPos()[2]), (int)bounds.getXWidth()*2, (int)bounds.getZLength()*2);
		//System.out.println("Number of POLYS: " + groundBelow.getPolygons().size());
		//System.exit(-1);
		//GameObject cameraObj = new GameObject("Temp");
		
		//System.out.println("Cam Position:");
		//Operations.printMat(this.getTransMat());
		
		//System.out.println("Ground Height")
		
		//cameraObj.setPosition(this.getCamPos());
		//bounds.setObject(cameraObj);
		if (bounds.intersectsMesh(groundBelow)) {
			//System.out.println("Ground mesh intersection detected");
			return true;
		}
		float disToGround = (world.getHeight(-getCamPos()[0], -getCamPos()[2])) - (-(getCamPos()[1]+bounds.getYHeight()));
		
		//System.out.println("Dist To Ground: " + disToGround);
		
		//System.out.println("JUMPING: " + jumping + " FALLING: " + falling + " DISTTOGROUND: " + disToGround);
		
		if (disToGround > 0) {
		//if (-(getCamPos()[1]+bounds.getYHeight()) < world.getHeight(-getCamPos()[0], -getCamPos()[2])) {
			//this.setPosition(new float[] {getCamPos()[0], -(world.getHeight(-getCamPos()[0], -getCamPos()[2])+bounds.getYHeight()),getCamPos()[2]});
			snapToGround();
			return true;
		}
		if (disToGround <= 0 && disToGround > -0.1 && !jumping) {
			//this.setPosition(new float[] {getCamPos()[0], -(world.getHeight(-getCamPos()[0], -getCamPos()[2])+bounds.getYHeight()),getCamPos()[2]});
			snapToGround();
			return true;
		}
		if (disToGround < 0 && disToGround > -0.1 && jumping && !falling) {
			//this.setPosition(new float[] {getCamPos()[0], -(world.getHeight(-getCamPos()[0], -getCamPos()[2])+bounds.getYHeight()),getCamPos()[2]});
			//snapToGround();
			return false;
		}
		
		
		return false;
	}
	
	public void jump() {
		if (touchingGround() && !jumping) {
			setVelocity(new float[] {this.getVelocity()[0],this.getVelocity()[1]-0.15f,this.getVelocity()[2]});
			jumping=true;
		}
	}
	
	private void snapToGround() {
		this.setPosition(new float[] {getCamPos()[0], -(world.getHeight(-getCamPos()[0], -getCamPos()[2])+bounds.getYHeight()),getCamPos()[2]});
	}
}