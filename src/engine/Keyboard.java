package engine;

import java.awt.event.*;

public class Keyboard extends KeyAdapter {
	Scene scene;
	boolean running;
	final float MOVESPEED = 0.1f;
	
	
	public Keyboard(Scene scene) {
		this.scene = scene;
		running = true;
	}
	
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		/*
		case(KeyEvent.VK_W): scene.getCam().translate(0, 0, -MOVESPEED); break;
		case(KeyEvent.VK_A): scene.getCam().translate(MOVESPEED, 0, 0); break;
		case(KeyEvent.VK_S): scene.getCam().translate(0, 0, MOVESPEED); break;
		case(KeyEvent.VK_D): scene.getCam().translate(-MOVESPEED, 0, 0); break;
		case(KeyEvent.VK_SPACE): scene.getCam().translate(0, MOVESPEED, 0); break;
		case(KeyEvent.VK_CONTROL): scene.getCam().translate(0, -MOVESPEED, 0); break;
		*/
		case(KeyEvent.VK_W): scene.getCam().translationState[2] = true; break;
		case(KeyEvent.VK_A): scene.getCam().translationState[0] = true; break;
		case(KeyEvent.VK_S): scene.getCam().translationState[3] = true; break;
		case(KeyEvent.VK_D): scene.getCam().translationState[1] = true; break;
		case(KeyEvent.VK_SPACE): scene.getCam().translationState[4] = true; break;
		case(KeyEvent.VK_CONTROL): scene.getCam().translationState[5] = true; break;
		case(KeyEvent.VK_ESCAPE): System.exit(0); break;
		}
		
	}
	public void keyRelease(KeyEvent e) {
		switch(e.getKeyCode()) {
		case(KeyEvent.VK_W): scene.getCam().translationState[2] = false; break;
		case(KeyEvent.VK_A): scene.getCam().translationState[0] = false; break;
		case(KeyEvent.VK_S): scene.getCam().translationState[3] = false; break;
		case(KeyEvent.VK_D): scene.getCam().translationState[1] = false; break;
		case(KeyEvent.VK_SPACE): scene.getCam().translationState[4] = false; break;
		case(KeyEvent.VK_CONTROL): scene.getCam().translationState[5] = false; break;
		}
	}
	
}
