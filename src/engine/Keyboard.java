package engine;

import java.awt.event.*;

public class Keyboard extends KeyAdapter {
	Scene scene;
	final float MOVESPEED = 0.1f;
	
	
	public Keyboard(Scene scene) {
		this.scene = scene;
	}
	
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
		case(KeyEvent.VK_W): scene.getCam().translate(0, 0, -MOVESPEED); break;
		case(KeyEvent.VK_A): scene.getCam().translate(MOVESPEED, 0, 0); break;
		case(KeyEvent.VK_S): scene.getCam().translate(0, 0, MOVESPEED); break;
		case(KeyEvent.VK_D): scene.getCam().translate(-MOVESPEED, 0, 0); break;
		case(KeyEvent.VK_SPACE): scene.getCam().translate(0, MOVESPEED, 0); break;
		case(KeyEvent.VK_CONTROL): scene.getCam().translate(0, -MOVESPEED, 0); break;
		}
		
	}
	
}
