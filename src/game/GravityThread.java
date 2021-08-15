package game;

import java.util.*;

public class GravityThread {
	ArrayList<GameObject> gameObjects;
	static final float gravAmt = 0.05f;
	
	
	public GravityThread() {
		gameObjects = new ArrayList<GameObject>();
	}
	
	public void addGameObject(GameObject g) {
		gameObjects.add(g);
	}
	
	public void setGameObjects(ArrayList<GameObject> objects) {
		this.gameObjects = objects;
	}
	
	public void run() {
		for(GameObject go : gameObjects) {
			if (!go.touchingGround()) {
				go.translate(0f,-gravAmt,0f);
			}
		}
	}
}
