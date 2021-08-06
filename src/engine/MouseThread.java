package engine;

import java.awt.*;
import javax.swing.*;


public class MouseThread implements Runnable {
	
	JFrame frame;
	Scene scene;
	Robot rb;
	
	public MouseThread(JFrame frame, Scene scene) {
		this.scene = scene;
		this.frame = frame;
	}
	public void run() {
		float x;
		float y;
		try {
			rb = new Robot();
			rb.mouseMove(900, 500);
			while(true) {
				Thread.sleep(10);
				if (frame.isFocused()) {
					x = (float)MouseInfo.getPointerInfo().getLocation().getX();
	                y = (float)MouseInfo.getPointerInfo().getLocation().getY();
	                if (x-900 != 0 | y-500 != 0) {
	                    scene.getCam().rotate('y', -(x-900)/2000);
	                    scene.getCam().rotate('x', (y-500)/2000);
	                	//plane.look('y', -(x-400)/200);
	                    //plane.look('x', (y-400)/200);
	                }
	                rb.mouseMove(900,500);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
