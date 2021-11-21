package Game.Render;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import Game.GameContent.*;
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
	ResourceManager resourceManager;
	int[] screenDims;
	RenderSky sky;
	RenderTerrain terrain;
	RenderWater water;
	RenderClouds clouds;
	RenderUI UI;
	
	Projection depthProj;
	int depthMapFBO;
	int SHADOW_WIDTH = 1024, SHADOW_HEIGHT = 1024;
	int depthMap;
	
	public Rendering(GameManager manager, ResourceManager resourceManager) {
		this.manager = manager;
		this.resourceManager = resourceManager;
		this.screenDims = manager.getScreenDims();
		sky = new RenderSky(manager);
		terrain = new RenderTerrain(manager);
		water = new RenderWater(manager);
		UI = new RenderUI(manager);
		clouds = new RenderClouds(manager);
		glClearColor(0.275f,0.94f,0.97f,1.0f);
		glEnable(GL_DEPTH_TEST);
		//glEnable(GL_MULTISAMPLE);
		boolean wireframe = false;
		if (wireframe) {
			glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
		}
		//glLightModeli(GL_LIGHT_MODEL_TWO_SIDE, GL_TRUE);
		//glEnable(GL_CULL_FACE);
		glLineWidth(3.0f);
		
		
		
		depthProj = new Projection(70f, 0.1f, 10000f, new int[] {1920,1080});
		
		
		
		depthMapFBO = glGenFramebuffers();
		depthMap = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, depthMap);
		float[] tNull = null;
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, SHADOW_WIDTH, SHADOW_HEIGHT, 0, GL_DEPTH_COMPONENT, GL_FLOAT, tNull);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT); 
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);  
		glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
		glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthMap, 0);
		glDrawBuffer(GL_NONE);
		glReadBuffer(GL_NONE);
		glBindFramebuffer(GL_FRAMEBUFFER, 0);  
		
		//System.out.println("get here H");
		//System.exit(-21);
		
	}
	
	private void runRenderMethodsShadows() {
		//terrain.render();
		//resourceManager.render();
		terrain.renderShadows();
		resourceManager.renderShadows();
	}
	
	private void runRenderMethodsNormal() {
		sky.render();
		UI.render();
		terrain.render();
		resourceManager.render();
		water.render();
		clouds.render();
	}
	
	public void renderFrame() {
		if (!glfwWindowShouldClose(manager.getWindow())) {
			//glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			glViewport(0, 0, SHADOW_WIDTH, SHADOW_HEIGHT);
			glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
			glClear(GL_DEPTH_BUFFER_BIT);
			
			runRenderMethodsShadows();
			
			glBindFramebuffer(GL_FRAMEBUFFER, 0);
			
			glViewport(0, 0, 1920, 1080);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			//ConfigureShaderAndMatrices();
			glBindTexture(GL_TEXTURE_2D, depthMap);
			
			runRenderMethodsNormal();
			
			glfwSwapBuffers(manager.getWindow());
			glfwPollEvents();
		} else {
			manager.endGame();
		}
	}
	
	private void setDepthMapUniforms() {
		FloatMatrix lightPosition = new FloatMatrix(manager.getSunPosition());
		FloatMatrix target = new FloatMatrix(new float[] {0,0,0});
		FloatMatrix up = new FloatMatrix(new float[] {0,1,0});
		FloatMatrix depthView = Operations.lookAt(lightPosition, target, up);
		
	}
	
	private void setRenderUniforms() {
		
		
	}
	
	
	public void updateTransformMatrix() {
		terrain.updateTransformMatrix();
		water.updateTransformMatrix();
		water.updateWavePos();
		clouds.updateTransformMatrix();
	}
	
	public void updateWorld() {
		terrain.updateWorld();
	}
}
