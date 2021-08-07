package engine;

import javax.swing.*;
import java.awt.Color;
import org.jblas.FloatMatrix;
import java.awt.image.BufferedImage;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;

public class Init {

	public static void main(String[] args) { 
		System.setProperty("sun.java2d.opengl", "true");
		JFrame frame = new JFrame("Live Rendering Window");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1920,1080);
		frame.setResizable(false);
		//frame.setVisible(true);
		Screen scr = new Screen(new int[] {1920,1080});
		for(int i=0;i<30;i++) {
			for(int i2=0;i2<30;i2++) {
				//System.out.println("Adding cube");
				scr.getScene().addCube(new FloatMatrix(new float[] {i,0,-i2}), new FloatMatrix(new float[] {1,1,1}), new int[] {i+50,0,i2+50});
				try {
					//Thread.sleep(50);
				} catch (Exception e) {
					
				}
			}
		}
		//System.out.println("Done adding cubes");
		
		//scr.getScene().addCube(new FloatMatrix(new float[] {0,0,-5}), new FloatMatrix(new float[] {3,3,3}), new int[] {255,0,0});
		//frame.add(scr);
		frame.getContentPane().add(scr);
		frame.getContentPane().setBackground(Color.BLACK);
		frame.setVisible(true);
		scr.setVisible(true);
		//scr.redraw();
		frame.addKeyListener(new Keyboard(scr.getScene()));
		
		FrameThread ft = new FrameThread(scr,60); //can't raise fps without screen going black?
		(new Thread(ft)).start();
		MouseThread mt = new MouseThread(frame,scr.getScene());
		(new Thread(mt)).start();
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank");
		frame.getContentPane().setCursor(blankCursor);
		//scr.setVisible(true);
	}
}
