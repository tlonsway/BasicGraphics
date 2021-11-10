package Game.Graphics;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

import org.jblas.FloatMatrix;
import org.lwjgl.opengl.GL;

public class DayNightThread implements Runnable {

	Graphics g;
	boolean paused;
	FloatMatrix sunPosition;
	int time = 0;
	
	
	static final float ticksPerSecond = 120f;
	
	public DayNightThread(Graphics g) {
		this.g = g;
		paused = false;
		sunPosition = new FloatMatrix(new float[] {0f,10000.0f,0f,1.0f});
		time = 0;
	}
	
	public void run() {
		while(!paused) {
			FloatMatrix newPoint = Operations.rotatePoint(sunPosition, 'z', 0.001f);
			sunPosition = newPoint;
			//g.updateSunPosition(sunPosition.data);
			g.initSunUpdate(sunPosition.data);
			time=(time+1)%6283;
			//System.out.println("Time: " + time);
			
			try {
				Thread.sleep((int)(1000.0f/ticksPerSecond));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void close() throws Throwable {
		paused = true;
		try {
			Thread.sleep(2*(int)(1000.0f/ticksPerSecond));
			finalize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getTime() {
		return time;
	}
}
