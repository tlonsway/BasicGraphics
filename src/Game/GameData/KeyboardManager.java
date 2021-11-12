package Game.GameData;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

import Game.Graphics.Graphics;

public class KeyboardManager {
	
	boolean wDown = false;
	boolean aDown = false;
	boolean sDown = false;
	boolean dDown = false;
	boolean eDown = false;
	boolean qDown = false;
	boolean spaceDown = false;
	boolean ctrlDown = false;
	boolean shiftDown = false;
	
	
	public KeyboardManager() {
		
	}
	
	public static int getHotbarKey(int key, int action) { 
		if (action == GLFW_PRESS) {
			switch(key) {
				case(GLFW_KEY_1): return 0;
				case(GLFW_KEY_2): return 1;
				case(GLFW_KEY_3): return 2;
				case(GLFW_KEY_4): return 3;
				case(GLFW_KEY_5): return 4;
				case(GLFW_KEY_6): return 5;
				case(GLFW_KEY_7): return 6;
				case(GLFW_KEY_8): return 7;
				case(GLFW_KEY_9): return 8;
				case(GLFW_KEY_0): return 9;
			}
		}
		return -1;
	}
	
	public void keyEvent(int key, int action) {
		if (key==GLFW_KEY_W && action == GLFW_PRESS) {
			wDown=true;
		}
		if (key==GLFW_KEY_W && action == GLFW_RELEASE) {
			wDown=false;
		}
		if (key==GLFW_KEY_A && action == GLFW_PRESS) {
			aDown=true;
		}
		if (key==GLFW_KEY_A && action == GLFW_RELEASE) {
			aDown=false;
		}
		if (key==GLFW_KEY_S && action == GLFW_PRESS) {
			sDown=true;
		}
		if (key==GLFW_KEY_S && action == GLFW_RELEASE) {
			sDown=false;
		}
		if (key==GLFW_KEY_D && action == GLFW_PRESS) {
			dDown=true;
		}
		if (key==GLFW_KEY_D && action == GLFW_RELEASE) {
			dDown=false;
		}
		//if (key==GLFW_KEY_SPACE && action == GLFW_PRESS) {
		//	spaceDown=true;
		//}
		//if (key==GLFW_KEY_SPACE && action == GLFW_RELEASE) {
		//	spaceDown=false;
		//}
		if (key==GLFW_KEY_LEFT_CONTROL && action == GLFW_PRESS) {
			ctrlDown=true;
		}
		if (key==GLFW_KEY_LEFT_CONTROL && action == GLFW_RELEASE) {
			ctrlDown=false;
		}
		if (key==GLFW_KEY_LEFT_SHIFT && action == GLFW_PRESS) {
			shiftDown=true;
		}
		if (key==GLFW_KEY_LEFT_SHIFT && action == GLFW_RELEASE) {
			shiftDown=false;
		}
	}
	
	public float[] getTranslate() {
		float movespeed = 0.1f;
		if (shiftDown) {
			movespeed *= 5;
		}
		float xTr,yTr,zTr;
		xTr=yTr=zTr=0;
		if (wDown) {
			zTr+=movespeed;
		}
		if (aDown) {
			xTr+=movespeed;
		}
		if (sDown) {
			zTr-=movespeed;
		}
		if (dDown) {
			xTr-=movespeed;
		}
		if (spaceDown) {
			yTr-=movespeed;
		}
		if (ctrlDown) {
			yTr+=movespeed;
		}
		return new float[] {xTr,yTr,zTr};
	}
}
