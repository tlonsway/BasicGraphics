package testing;

import java.awt.*;


public class MouseThread implements Runnable {
	
	public MouseThread() {
	}
	public void run() {
		int x;
		int y;
		try {
			Thread.sleep(2000);
			while(true) {
				Thread.sleep(1000);
				x = (int)MouseInfo.getPointerInfo().getLocation().getX();
                y = (int)MouseInfo.getPointerInfo().getLocation().getY();
                getPixelValue(x, y);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void getPixelValue(int x, int y) {
		try {
			Robot r = new Robot();
			Color c = r.getPixelColor(x, y);
			System.out.print("R value: " + (c.getRed()*2.0-256)/256 + " ");
	        System.out.print("G value: " + (c.getGreen()*2.0-256)/256 + " ");
	        System.out.println("B value: " + (c.getBlue()*2.0-256)/256 + " ");
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}