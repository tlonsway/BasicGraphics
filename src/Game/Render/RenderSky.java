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
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.GL_MULTISAMPLE;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
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
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class RenderSky {

	GameManager manager;
	Camera cam;
	
	int skyVAO;
	int skyShaderProgram;
	
	static final float[] skyVert = new float[] {-1.0f,1.0f,-1.0f,-1.0f,1.0f,1.0f,
												-1.0f,-1.0f,1.0f,-1.0f,1.0f,1.0f};
	
	
	public RenderSky(GameManager manager) {
		this.manager = manager;
		cam = manager.getCamera();
		//setup shader program for the sky
		Shader skyVertShader = new Shader("Shaders/simpleSkyVert.vtxs",GL_VERTEX_SHADER);
		Shader skyFragShader = new Shader("Shaders/basicSunFrag.frgs",GL_FRAGMENT_SHADER);
		skyShaderProgram = glCreateProgram();
		glAttachShader(skyShaderProgram,skyVertShader.getShader());
		glAttachShader(skyShaderProgram,skyFragShader.getShader());
		glLinkProgram(skyShaderProgram);
		glDeleteShader(skyVertShader.getShader());
		glDeleteShader(skyFragShader.getShader());
		//bind vertices for displaying the sky
		int VBOt,VAOt;
		VBOt = glGenBuffers();
		VAOt = glGenVertexArrays();
		glBindVertexArray(VAOt);
		glBindBuffer(GL_ARRAY_BUFFER,VBOt);
		glBufferData(GL_ARRAY_BUFFER, skyVert, GL_STATIC_DRAW);
		glVertexAttribPointer(0,2,GL_FLOAT,false,8,0l);
		glEnableVertexAttribArray(0);
		skyVAO = VAOt;
		glBindBuffer(GL_ARRAY_BUFFER,0);
		glBindVertexArray(0);
	}
	
	public void render() {
		glDisable(GL_DEPTH_TEST);
		glUseProgram(skyShaderProgram);
		int camRotYUniformLoc = glGetUniformLocation(skyShaderProgram, "camRotYU");
		glUniform1f(camRotYUniformLoc, -(float)Math.sin(cam.getRotations()[0]));
		glBindVertexArray(skyVAO);
		glDrawArrays(GL_TRIANGLES,0,6);
		glEnable(GL_DEPTH_TEST);
		this.updateSun();
	}
	
	private void updateSun() {
		float[] sunPosition = manager.getSunPosition();
		glUseProgram(skyShaderProgram);
		Projection project = manager.getProjection();
		FloatMatrix projMat = project.getProjMatFMat();
		FloatMatrix camMat = cam.getCamMat();
		FloatMatrix sunPoint = new FloatMatrix(sunPosition);
		FloatMatrix resultTemp = projMat.mmul(camMat.mmul(sunPoint));
		float zValTemp = resultTemp.get(2);
		resultTemp.divi(resultTemp.get(2));
		float[] sunPositionProjected = new float[] {resultTemp.get(0),resultTemp.get(1),zValTemp};
		int skySunProjUniformLoc = glGetUniformLocation(skyShaderProgram, "sunPosition");
		glUniform3fv(skySunProjUniformLoc, sunPositionProjected);
		int time = manager.getTime();
		float[] sunCol = manager.getSunColor();
		int timeUniformLoc = glGetUniformLocation(skyShaderProgram, "time");
		glUniform1i(timeUniformLoc,time);
		int sunColorUniformLoc = glGetUniformLocation(skyShaderProgram, "sunColor");
		glUniform3fv(sunColorUniformLoc,sunCol);
	}
	
}
