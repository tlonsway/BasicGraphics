package Game.GameData;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import Game.sound.SoundManager;
import Game.GameData.*;
import Game.Graphics.*;
import Game.Network.*;
import Game.Init.Setup;
import Game.Render.*;

import java.nio.*;
import java.util.*;
import org.jblas.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class GameManager {
	
	Camera cam;
	Projection proj;
	Rendering renderer;
	World world;
	
	KeyboardManager keyboardThread;
	MouseManager mouseThread;
	SoundManager soundManager;
	Thread dayNightThreadT;
	DayNightThread dayNightThreadDNT;
	
	GravityThread gravity;
	
	int[] screenDims;
	long window;
	
	boolean gameRunning;
	
	float[] sunPosition;
	float[] sunColor;
	
	float[] playerXZ = new float[2];
	boolean worldUpdateReady = false;
	
	
	public GameManager(int[] screenDims) {
		gameRunning = true;
		window = Setup.start(screenDims, "Game Window");
		keyboardThread = new KeyboardManager();
		mouseThread = new MouseManager();
		gravity = new GravityThread();
		cam = new Camera(screenDims);
		//Sound stuff
		soundManager = new SoundManager(cam);
		soundManager.addBuffer("Data/Audio/suck.ogg", "pop"); 
		soundManager.addSource("pop" , "speaker", true, false);
		soundManager.setSourcePosition("speaker", 0f, 180f, 0f);
		gravity.setCamera(cam);
		proj = new Projection(70f, 0.1f, 10000f, screenDims);
		
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true);
			if ( key == GLFW_KEY_SPACE && action == GLFW_PRESS) {
				cam.jump();
			}
			keyboardThread.keyEvent(key, action);
		});
		glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
			mouseThread.mouseMovement(xpos, ypos);
		});
		cam.translate(0f, -60f, 0f);
		dayNightThreadDNT = new DayNightThread(this);
		dayNightThreadT = new Thread(dayNightThreadDNT);
		dayNightThreadT.start();
		
		sunPosition = new float[3];
		sunColor = new float[3];
		
		renderer = new Rendering(this);
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		//gameLoop();
	}
	
	
	public void gameLoop() {
		soundManager.playSound("speaker");
		while(gameRunning) {
			
			float[] camPos = cam.getCamPos();
			//System.out.println("Camera position: (" + camPos[0] + "," + camPos[1] + "," + camPos[2] + ")");
			renderer.renderFrame();
			camPositionUpdate();
			chunkUpdateCheck();
			updateWorldIfReady();
			updateSunColor();
			soundManager.updateListner();
			gravity.run();
		}
		try {
			dayNightThreadDNT.close();
			((Thread)dayNightThreadT).join();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void setWorld(World w) {
		this.world = w;
		cam.setWorld(w);
	}
	
	private void chunkUpdateCheck() {
		float[] currentXZ = new float[] {cam.getCamPos()[0],cam.getCamPos()[2]};
		if (Math.sqrt((currentXZ[0]-playerXZ[0])*(currentXZ[0]-playerXZ[0])+(currentXZ[1]-playerXZ[1])*(currentXZ[1]-playerXZ[1])) > 200) {
			playerXZ = currentXZ;
			new Thread(new WorldUpdateThread(this,world,new int[] {(int)currentXZ[0],(int)currentXZ[1]})).start();
		}
	}
	
	public void setWorldUpdateReady() {
		worldUpdateReady = true;
	}
	
	private void updateWorldIfReady() {
		if (worldUpdateReady) {
			renderer.updateWorld();
			worldUpdateReady = false;
		}
	}
	
	private void camPositionUpdate() {
		float[] camPos = cam.getCamPos();
		float[] translateT = keyboardThread.getTranslate();
		cam.translate(translateT[0], translateT[1], translateT[2]);
		float[] rotateT = mouseThread.getRotation();
		cam.rotate('x', rotateT[1], false);
		cam.rotate('y', rotateT[0], true);
		this.updateTransformMatrix();
	}

	private void updateSunColor() {
		double timeVal = ((double)dayNightThreadDNT.getTime()/(1000.0f));
		float sunRedT = 1.0f;
		float sunGreenT = (float)(Math.max(-0.0343*(1.2*timeVal-3.1415)*(1.2*timeVal-3.1415)*(1.2*timeVal-3.1415), 0.0) + Math.cos((0.5)*((1/2.5)*timeVal-3.5)));
		float sunBlueT = (float)(1.0/(100*(timeVal+0.44)));
		if (sunGreenT > 1.0f) {
			sunGreenT = 1.0f;
		}
		if (sunGreenT < 0.0f) {
			sunGreenT = 0.0f;
		}
		float[] newSunCol = new float[] {sunRedT,sunGreenT,sunBlueT};
		this.sunColor = newSunCol;
	}
	
	private void updateTransformMatrix() {
		renderer.updateTransformMatrix();
	}
	
	public void sunPositionUpdate(float[] newPos) {
		this.sunPosition = newPos;
	}
	
	public void endGame() {
		gameRunning = false;
	}
	
	public int[] getScreenDims() {
		return screenDims;
	}
	public long getWindow() {
		return window;
	}
	public Camera getCamera() {
		return cam;
	}
	public Projection getProjection() {
		return proj;
	}
	public Rendering getRendering() {
		return renderer;
	}
	public float[] getSunPosition() {
		return sunPosition;
	}
	public float[] getSunColor() {
		return sunColor;
	}
	public World getWorld() {
		return world;
	}
	public int getTime() {
		return -1;
	}
}
