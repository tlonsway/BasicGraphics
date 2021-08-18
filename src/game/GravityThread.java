package game;

import java.util.*;

public class GravityThread {
	ArrayList<GameObject> gameObjects;
	Camera cam;
	static final float gravAcc = -0.004f;
	
	
	public GravityThread() {
		gameObjects = new ArrayList<GameObject>();
	}
	
	public void addGameObject(GameObject g) {
		gameObjects.add(g);
	}
	
	public void setGameObjects(ArrayList<GameObject> objects) {
		this.gameObjects = objects;
	}
	
	public void setCamera(Camera cam) {
		this.cam = cam;
	}
	
	public void run() {
		cam.updatePosition();
		if (!cam.touchingGround()) {
			if (!cam.falling) {
				cam.falling = true;
				float[] camAcc = cam.getAcceleration();
				cam.setAcceleration(new float[] {camAcc[0],camAcc[1]-gravAcc,camAcc[2]});
			}
		} else {
			if (cam.jumping) {
				cam.jumping = false;
			}
			if (cam.falling) {
				cam.falling = false;
				float[] camAcc = cam.getAcceleration();
				float[] camVec = cam.getVelocity();
				cam.setVelocity(new float[] {camVec[0],0f,camVec[2]});
				cam.setAcceleration(new float[] {camAcc[0],0f,camAcc[2]});
			}
		}
		
		for(GameObject go : gameObjects) {
			go.updatePosition();
			if (!go.gravDisabled) {
				if (!go.touchingGround()) {
					if (!go.falling) {
						go.falling = true;
						float[] objAcc = go.getAcceleration();
						go.setAcceleration(new float[] {objAcc[0],objAcc[1]+gravAcc,objAcc[2]});
					}
					//go.translate(0f,-gravAmt,0f);
				} else {
					//go.updatePosition();
					if (go.falling) {
						go.falling = false;
						float[] objAcc = go.getAcceleration();
						float[] objVec = go.getVelocity();
						//go.setVelocity(new float[] {objVec[0],0f,objVec[2]});
						go.setVelocity(new float[] {0f,0f,0f});
						go.setAcceleration(new float[] {objAcc[0],0f,objAcc[2]});
					}
				}
			}
			
		}
	}
}
