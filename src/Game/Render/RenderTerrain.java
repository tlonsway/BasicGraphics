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
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
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
import static org.lwjgl.opengl.GL20.glUniform3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class RenderTerrain {
	
	GameManager manager;
	
	int VAO;
	int numElements;
	int shaderProgram;
	
	static final float[] iMatFlat = new float[] {1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1};
	
	public RenderTerrain(GameManager manager) {
		this.manager = manager;
		//Shader vertShader = new Shader("Shaders/basicProjModelLighting.vtxs",GL_VERTEX_SHADER);
		//Shader fragShader = new Shader("Shaders/basicLighting.frgs",GL_FRAGMENT_SHADER);
		Shader vertShader = new Shader("Shaders/lightShadowProjVert.vtxs",GL_VERTEX_SHADER);
		Shader fragShader = new Shader("Shaders/lightShadowFrag.frgs",GL_FRAGMENT_SHADER);
		shaderProgram = glCreateProgram();
		glAttachShader(shaderProgram,vertShader.getShader());
		glAttachShader(shaderProgram,fragShader.getShader());
		glLinkProgram(shaderProgram);
		glUseProgram(shaderProgram);
		glDeleteShader(vertShader.getShader());
		glDeleteShader(fragShader.getShader());
		updateSun();
	}
	
	public void render() {
		Camera cam = manager.getCamera();
		glEnable(GL_DEPTH_TEST);
		glUseProgram(shaderProgram);
		float[] cpt = cam.getCamPos();
		int viewPosLoc = glGetUniformLocation(shaderProgram, "viewPos");
		glUniform3fv(viewPosLoc,new float[] {-cpt[0],-cpt[1],-cpt[2]});		
		int modelMatLoc = glGetUniformLocation(shaderProgram,"model");
		glUniformMatrix4fv(modelMatLoc, false, iMatFlat);
		int modelInvTranMatLoc = glGetUniformLocation(shaderProgram, "invTranMod");
		glUniformMatrix4fv(modelInvTranMatLoc,false,iMatFlat);
		updateSun();
		setLightSpaceMatUniform();
		glBindVertexArray(VAO);
		glDrawArrays(GL_TRIANGLES,0,numElements);
	}
	
	public void renderShadows() {
		glEnable(GL_DEPTH_TEST);
		glUseProgram(manager.getShadowShaderProgram());
		setShadowUniforms();
		int modelMatLoc = glGetUniformLocation(manager.getShadowShaderProgram(),"model");
		glUniformMatrix4fv(modelMatLoc, false, iMatFlat);
		glBindVertexArray(VAO);
		glDrawArrays(GL_TRIANGLES,0,numElements);
	}
	
	public void setShadowUniforms() {
		glUseProgram(manager.getShadowShaderProgram());
		
		/*
		Projection project = manager.getProjection();
		FloatMatrix lightPosition = new FloatMatrix(manager.getSunPosition());
		lightPosition = new FloatMatrix(new float[] {lightPosition.get(0),lightPosition.get(1),lightPosition.get(2)});
		FloatMatrix target = new FloatMatrix(new float[] {0,0,0});
		FloatMatrix up = new FloatMatrix(new float[] {0,1,0});
		FloatMatrix depthView = Operations.lookAt(lightPosition, target, up);
		Camera c2 = new Camera(new int[] {1920,1080});
		c2.rotate('x', 3.74f);
		c2.translate(1000, 500, 1000);
		depthView = c2.getCamMat();
		float[] matCom = combineMats(project.getProjMatFMat(),depthView);
		*/
		
		
		//float[] camPos = manager.getCamera().getCamPos();
		//System.out.println("POSITION: (" + -camPos[0] + "," + -camPos[1] + "," + -camPos[2] + ")");
		
		//Projection project = manager.getProjection();
		Projection project = new Projection(70, 1000f, 25000f, new int[] {10000,10000});
		FloatMatrix lightPosition = new FloatMatrix(manager.getSunPosition());
		lightPosition = new FloatMatrix(new float[] {lightPosition.get(0),lightPosition.get(1),lightPosition.get(2)});
		FloatMatrix target = new FloatMatrix(new float[] {0,0,0});
		Camera cam = manager.getCamera();
		FloatMatrix up = new FloatMatrix(new float[] {0,1,0});
		FloatMatrix depthView = Operations.lookAt(lightPosition, target, up);
		float[] matCom = combineMats(project.getProjMatFMat(),depthView);
		
		
		int fullMatLoc = glGetUniformLocation(manager.getShadowShaderProgram(),"fullMat");
		glUniformMatrix4fv(fullMatLoc, false, matCom);	
	}
	
	public void setLightSpaceMatUniform() {
		glUseProgram(shaderProgram);
		/*
		Camera c2 = new Camera(new int[] {1920,1080});
		c2.rotate('x', 3.74f);
		c2.translate(1000, 500, 1000);
		FloatMatrix depthView = c2.getCamMat();
		Projection project = manager.getProjection();
		float[] matCom = combineMats(project.getProjMatFMat(),depthView);*/
		
		/*
		Projection project = manager.getProjection();
		FloatMatrix lightPosition = new FloatMatrix(manager.getSunPosition());
		lightPosition = new FloatMatrix(new float[] {lightPosition.get(0),lightPosition.get(1),lightPosition.get(2)});
		lightPosition = new FloatMatrix(new float[] {100,100,100});
		FloatMatrix target = new FloatMatrix(new float[] {0,0,0});
		Camera cam = manager.getCamera();
		//target = new FloatMatrix(new float[] {-cam.getCamPos()[0],-cam.getCamPos()[1],-cam.getCamPos()[2]});
		FloatMatrix up = new FloatMatrix(new float[] {0,1,0});
		FloatMatrix depthView = Operations.lookAt(lightPosition, target, up);
		float[] matCom = combineMats(project.getProjMatFMat(),depthView);
		*/
		Projection project = new Projection(70, 1000f, 25000f, new int[] {10000,10000});
		FloatMatrix lightPosition = new FloatMatrix(manager.getSunPosition());
		lightPosition = new FloatMatrix(new float[] {lightPosition.get(0),lightPosition.get(1),lightPosition.get(2)});
		FloatMatrix target = new FloatMatrix(new float[] {0,0,0});
		Camera cam = manager.getCamera();
		FloatMatrix up = new FloatMatrix(new float[] {0,1,0});
		FloatMatrix depthView = Operations.lookAt(lightPosition, target, up);
		float[] matCom = combineMats(project.getProjMatFMat(),depthView);
		
		int fullMatLoc = glGetUniformLocation(shaderProgram,"lightSpaceMatrix");
		glUniformMatrix4fv(fullMatLoc, false, matCom);
	}
	
	public void updateTransformMatrix() {
		/*Projection project = manager.getProjection();
		Camera cam = manager.getCamera();
		glUseProgram(shaderProgram);
		float[] matCom = combineMats(project.getProjMatFMat(),cam.getCamMat());
		int fullMatLoc = glGetUniformLocation(shaderProgram,"fullMat");
		glUniformMatrix4fv(fullMatLoc, false, matCom);*/
		glUseProgram(shaderProgram);
		Projection project = manager.getProjection();
		FloatMatrix lightPosition = new FloatMatrix(manager.getSunPosition());
		lightPosition = new FloatMatrix(new float[] {lightPosition.get(0),lightPosition.get(1),lightPosition.get(2)});
		lightPosition = new FloatMatrix(new float[] {100,100,100});
		FloatMatrix target = new FloatMatrix(new float[] {0,0,0});
		Camera cam = manager.getCamera();
		target = new FloatMatrix(new float[] {-cam.getCamPos()[0],-cam.getCamPos()[1],-cam.getCamPos()[2]});
		FloatMatrix up = new FloatMatrix(new float[] {0,-1,0});
		FloatMatrix depthView = Operations.lookAt(lightPosition, target, up);
		
		Camera c2 = new Camera(new int[] {1920,1080});
		c2.rotate('x', 70);
		c2.rotate('y', -80);
		c2.rotate('z', 180);
		c2.translate(100, 200, 100);
		depthView = c2.getCamMat();
		depthView = cam.getCamMat();
		float[] matCom = combineMats(project.getProjMatFMat(),depthView);
		int fullMatLoc = glGetUniformLocation(shaderProgram,"fullMat");
		glUniformMatrix4fv(fullMatLoc, false, matCom);
	}
	
	public void updateSun() {
		glUseProgram(shaderProgram);
		int lightPosLoc = glGetUniformLocation(shaderProgram, "lightPos");
		int lightColLoc = glGetUniformLocation(shaderProgram, "lightColor");
		float[] sunPos = manager.getSunPosition();
		float[] sunCol = manager.getSunColor();
		glUniform3fv(lightPosLoc, sunPos);
		glUniform3fv(lightColLoc, sunCol);
	}
	
	public void updateWorld() {
		World w = manager.getWorld();
		float[] vert = w.vertices;
		int VBOt,VAOt;
		VBOt = glGenBuffers();
		VAOt = glGenVertexArrays();
		glBindVertexArray(VAOt);
		glBindBuffer(GL_ARRAY_BUFFER,VBOt);
		glBufferData(GL_ARRAY_BUFFER, vert, GL_STREAM_DRAW);
		glVertexAttribPointer(0,3,GL_FLOAT,false,36,0l);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1,3,GL_FLOAT,false,36,12l);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(2,3,GL_FLOAT,false,36,24l);
		glEnableVertexAttribArray(2);
		glBindBuffer(GL_ARRAY_BUFFER,0);
		glBindVertexArray(VAOt);
		VAO=VAOt;
		numElements = vert.length;
	}

	private static float[] combineMats(FloatMatrix projmat, FloatMatrix camMat) {
		FloatMatrix res = projmat.mmul(camMat);
		float[] ret = new float[16];
		int t = 0;
		for(int r=0;r<4;r++) {
			for(int c=0;c<4;c++) {
				ret[t] = res.get(c,r);
				t++;
			}
		}
		return ret;
	}
	
}
