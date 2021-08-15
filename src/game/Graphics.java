package game;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import org.jblas.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Graphics {

	int[] screenDims;
	long window;
	Shader vertShader;
	Shader fragShader;
	int shaderProgram;
	float[] vertices;
	int[] indices;
	Camera cam;
	Projection project;
	int VBO,VAO,EBO;
	int numElements;
	KeyboardManager keyboardThread;
	
	public Graphics() {
		screenDims = new int[] {1920,1080};
		String windowTitle = "Game Window";
		window = Setup.start(screenDims, windowTitle);
		keyboardThread = new KeyboardManager(this);
		//(new Thread(keyboardThread)).start();
		init();
	}
	
	public void init() {
		cam = new Camera(screenDims);
		project = new Projection(80f, 0.1f, 10f, screenDims);
		vertShader = new Shader("Shaders/basicProjection.vtxs",GL_VERTEX_SHADER);
		fragShader = new Shader("Shaders/singleColor.frgs",GL_FRAGMENT_SHADER);
		shaderProgram = glCreateProgram();
		
		glAttachShader(shaderProgram,vertShader.getShader());
		
		glAttachShader(shaderProgram,fragShader.getShader());
		
		glLinkProgram(shaderProgram);
		
		glUseProgram(shaderProgram);
		//int err = glGetError();
		//System.out.println("Error found: " + err);
		//System.exit(-1);
		//vertShader.printShaderCode();
		
		glDeleteShader(vertShader.getShader());
		glDeleteShader(fragShader.getShader());
		vertices = null;
		indices = null;
		numElements = 0;
		glClearColor(0.0f,0.0f,0.0f,1.0f);
		glEnable(GL_DEPTH_TEST);
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true);
			keyboardThread.keyEvent(key, action);
		});
		//loop();
	}
	
	public void loop() {
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		while(!glfwWindowShouldClose(window)) {
			if (vertices == null || indices == null || numElements == 0) {
				try {
					Thread.sleep(50);
					continue;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glUseProgram(shaderProgram);
			glBindVertexArray(VAO);
			//glDrawElements(GL_TRIANGLES,3,GL_UNSIGNED_INT,0);
			glDrawArrays(GL_TRIANGLES,0,3);
			glfwSwapBuffers(window);
			glfwPollEvents();
			float[] translateT = keyboardThread.getTranslate();
			//System.out.println("x trans: " + translateT[0]);
			cam.translate(translateT[0], translateT[1], translateT[2]);
			this.updateTransformMatrix();
		}
	}
	
	private void updateTransformMatrix() {
		
		//System.exit(0);
		float[] matCom = combineMats(project.getProjMatFMat(),cam.getCamMat());
		int fullMatLoc = glGetUniformLocation(shaderProgram,"fullMat");
		glUniformMatrix4fv(fullMatLoc, false, matCom);
		//System.out.println("matLoc: " + fullMatLoc);
		
	}
	
	public void updateData(float[] vertices, int[] indices) {
		this.vertices = vertices;
		this.indices = indices;
		int VBOt,VAOt,EBOt;
		VBOt = glGenBuffers();
		VAOt = glGenVertexArrays();
		EBOt = glGenBuffers();
		VBO=VBOt;VAO=VAOt;EBO=EBOt;
		glBindVertexArray(VAO);
		glBindBuffer(GL_ARRAY_BUFFER,VBO);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STREAM_DRAW);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STREAM_DRAW);
		//glVertexAttribPointer(0,3,GL_FLOAT,false,24,0l);
		//glEnableVertexAttribArray(0);
		//glVertexAttribPointer(0,3,GL_FLOAT,false,24,12l);
		//glEnableVertexAttribArray(1);
		glVertexAttribPointer(0,3,GL_FLOAT,false,12,0l);
		glEnableVertexAttribArray(0);
		glBindBuffer(GL_ARRAY_BUFFER,0);
		glBindVertexArray(VAO);
		numElements = indices.length;
		//int err = glGetError();
		//System.out.println("Error: " + err);
		//glBindVertexArray(0);
	}
	
	public Camera getCamera() {
		return cam;
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
