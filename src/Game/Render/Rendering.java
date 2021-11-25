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
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glLineWidth;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
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
	int SHADOW_WIDTH = 10000, SHADOW_HEIGHT = 10000;
	int depthMap;
	
	int quadShaderProgram;
	
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
		
		Shader quadVertShader = new Shader("Shaders/depthMapVert.vtxs",GL_VERTEX_SHADER);
		Shader quadFragShader = new Shader("Shaders/depthMapFrag.frgs",GL_FRAGMENT_SHADER);
		quadShaderProgram = glCreateProgram();
		glAttachShader(quadShaderProgram,quadVertShader.getShader());
		glAttachShader(quadShaderProgram,quadFragShader.getShader());
		glLinkProgram(quadShaderProgram);
		glUseProgram(quadShaderProgram);
		glDeleteShader(quadVertShader.getShader());
		glDeleteShader(quadFragShader.getShader());
		
		
		depthProj = new Projection(70f, 0.1f, 100000f, new int[] {1920,1080});
		
		
		
		depthMapFBO = glGenFramebuffers();
		int code = glCheckFramebufferStatus(GL_FRAMEBUFFER);
		/*System.out.println("fbo compilation: " + code);
	
		switch(code) {
			case(GL_FRAMEBUFFER_COMPLETE): System.out.println("complete"); break;
			case(GL_FRAMEBUFFER_UNDEFINED): System.out.println("incomplete"); break;
			case(GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT): System.out.println("incomplete"); break;
			case(GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT): System.out.println("incomplete"); break;
			case(GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER): System.out.println("incomplete"); break;
			case(GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER):System.out.println("incomplete"); break;
			case(GL_FRAMEBUFFER_UNSUPPORTED): System.out.println("incomplete"); break;
		}*/
		
		depthMap = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, depthMap);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, SHADOW_WIDTH, SHADOW_HEIGHT, 0, GL_DEPTH_COMPONENT, GL_FLOAT, (ByteBuffer) null);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE); 
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);  
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
		//terrain.render();
	}
	
	private void runRenderMethodsNormal() {
		//glDisable(GL_CULL_FACE);
		sky.render();
		UI.render();
		clouds.render();
		terrain.render();
		resourceManager.render();
		water.render();
	}
	
	private void bindFullScreenQuad() {
		glUseProgram(quadShaderProgram);
		
		int nearPlaneLoc = glGetUniformLocation(quadShaderProgram,"near_plane");
		glUniform1f(nearPlaneLoc, 0.1f);
		
		int farPlaneLoc = glGetUniformLocation(quadShaderProgram,"far_plane");
		glUniform1f(farPlaneLoc, 10000f);
		
		float[] quadVert = new float[] {-1.0f,1.0f,0.0f,0.0f,1.0f,
							  		   -1.0f,-1.0f,0.0f,0.0f,0.0f,
						               1.0f,1.0f,0.0f,1.0f,1.0f,
						               1.0f,-1.0f,0.0f,1.0f,0.0f};
		int VBOt,VAOt;
		VBOt = glGenBuffers();
		VAOt = glGenVertexArrays();
		glBindVertexArray(VAOt);
		glBindBuffer(GL_ARRAY_BUFFER,VBOt);
		glBufferData(GL_ARRAY_BUFFER, quadVert, GL_STATIC_DRAW);
		glVertexAttribPointer(0,3,GL_FLOAT,false,20,0l);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1,2,GL_FLOAT,false,20,12l);
		glEnableVertexAttribArray(1);
		//int quadVAO = VAOt;
		glBindBuffer(GL_ARRAY_BUFFER,0);
		glDrawArrays(GL_TRIANGLE_STRIP,0,4);
	}
	
	public void renderFrame() {
		if (!glfwWindowShouldClose(manager.getWindow())) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			/*
			int code = glCheckFramebufferStatus(GL_FRAMEBUFFER);
			System.out.println("fbo compilation: " + code);
			switch(code) {
			case(GL_FRAMEBUFFER_COMPLETE): System.out.println("complete"); break;
			case(GL_FRAMEBUFFER_UNDEFINED): System.out.println("incomplete"); break;
			case(GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT): System.out.println("incomplete"); break;
			case(GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT): System.out.println("incomplete"); break;
			case(GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER): System.out.println("incomplete"); break;
			case(GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER):System.out.println("incomplete"); break;
			case(GL_FRAMEBUFFER_UNSUPPORTED): System.out.println("incomplete"); break;
			}*/
			
			glViewport(0, 0, SHADOW_WIDTH, SHADOW_HEIGHT);
			glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO);
			glClear(GL_DEPTH_BUFFER_BIT);
			
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, depthMap);
			
			runRenderMethodsShadows();	
			//runRenderMethodsNormal();
			
			
			glBindFramebuffer(GL_FRAMEBUFFER, 0);
			
			glViewport(0, 0, 1920, 1080);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			//ConfigureShaderAndMatrices();
			glActiveTexture(GL_TEXTURE0);
	        glBindTexture(GL_TEXTURE_2D, depthMap);
			
	        /*
	        byte[] pixels = new byte[1024 * 1024 * 1];
	        ByteBuffer buffer = ByteBuffer.allocateDirect(pixels.length).order(ByteOrder.nativeOrder());
	        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_BYTE, buffer);
	        buffer.get(pixels);
	        for(int i = 0; i < pixels.length; i++) {
	            System.out.println( pixels[i] & 0xFF );
	        }
	        */
	        
	        
			//bindFullScreenQuad();
			
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
	
	private void setShadowMapUniforms() {
		
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
