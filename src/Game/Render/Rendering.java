package Game.Render;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import Game.GameData.*;
import Game.Graphics.*;
import Game.Network.*;
import Game.Init.Setup;

import java.nio.*;
import java.util.*;
import org.jblas.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Rendering {
	
	GameManager manager;
	int[] screenDims;
	RenderSky sky;
	RenderTerrain terrain;
	
	
	public Rendering(GameManager manager) {
		this.manager = manager;
		this.screenDims = manager.getScreenDims();
		sky = new RenderSky(manager);
		terrain = new RenderTerrain(manager);
		glClearColor(0.275f,0.94f,0.97f,1.0f);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_MULTISAMPLE); 
		glLineWidth(3.0f);
	}
	
	public void renderFrame() {
		if (!glfwWindowShouldClose(manager.getWindow())) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			sky.render();
			terrain.render();
			
			glfwSwapBuffers(manager.getWindow());
			glfwPollEvents();
		} else {
			manager.endGame();
		}
	}
	
	public void updateTransformMatrix() {
		terrain.updateTransformMatrix();
	}
	
	public void updateWorld() {
		terrain.updateWorld();
	}
}
