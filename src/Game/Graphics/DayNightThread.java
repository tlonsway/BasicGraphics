package Game.Graphics;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

import org.jblas.FloatMatrix;
import org.lwjgl.opengl.GL;

public class DayNightThread implements Runnable {

	Graphics g;
	boolean paused;
	FloatMatrix sunPosition;
	
	
	
	static final float ticksPerSecond = 120f;
	
	public DayNightThread(Graphics g) {
		this.g = g;
		paused = false;
		sunPosition = new FloatMatrix(new float[] {-10000.0f,10000.0f,0f,1.0f});
	}
	
	public void run() {
		while(!paused) {
			FloatMatrix newPoint = Operations.rotatePoint(sunPosition, 'z', 0.001f);
			sunPosition = newPoint;
			//g.updateSunPosition(sunPosition.data);
			g.initSunUpdate(sunPosition.data);
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
}
