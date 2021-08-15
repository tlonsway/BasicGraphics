package game;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

public class KeyboardManager {
	
	Graphics g;
	boolean wDown = false;
	boolean aDown = false;
	boolean sDown = false;
	boolean dDown = false;
	boolean eDown = false;
	boolean qDown = false;
	boolean spaceDown = false;
	boolean ctrlDown = false;
	boolean shiftDown = false;
	
	
	public KeyboardManager(Graphics g) {
		this.g = g;
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
		if (key==GLFW_KEY_SPACE && action == GLFW_PRESS) {
			spaceDown=true;
		}
		if (key==GLFW_KEY_SPACE && action == GLFW_RELEASE) {
			spaceDown=false;
		}
		if (key==GLFW_KEY_LEFT_CONTROL && action == GLFW_PRESS) {
			ctrlDown=true;
		}
		if (key==GLFW_KEY_LEFT_CONTROL && action == GLFW_RELEASE) {
			ctrlDown=false;
		}
	}
	
	public float[] getTranslate() {
		float xTr,yTr,zTr;
		xTr=yTr=zTr=0;
		if (wDown) {
			zTr+=.01f;
		}
		if (aDown) {
			xTr+=.01f;
		}
		if (sDown) {
			zTr-=.01f;
		}
		if (dDown) {
			xTr-=.01f;
		}
		if (spaceDown) {
			yTr-=.01f;
		}
		if (ctrlDown) {
			yTr+=.01f;
		}
		return new float[] {xTr,yTr,zTr};
	}
}
