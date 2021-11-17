package Game.Graphics;

import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;

import org.jblas.FloatMatrix;
import org.lwjgl.opengl.GL;

import Game.GameData.GameManager;

public class DayNightThread implements Runnable {

	GameManager g;
	boolean paused;
	FloatMatrix sunPosition;
	int time = 0;
	
	
	static final float ticksPerSecond = 8.0f;
	
	public DayNightThread(GameManager g) {
		this.g = g;
		paused = false;
		sunPosition = new FloatMatrix(new float[] {5000.0f,0.0f,-10000.0f,1.0f});
		time = 4712;
	}
	
	public void run() {
		while(!paused) {
			FloatMatrix newPoint = Operations.rotatePoint(sunPosition, 'x', 0.001f);
			sunPosition = newPoint;
			//g.updateSunPosition(sunPosition.data);
			g.sunPositionUpdate(sunPosition.data);
			time=(time+1)%6283;
			//System.out.println(time);
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
