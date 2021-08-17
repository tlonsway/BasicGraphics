package game;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

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
	MouseManager mouseThread;
	GravityThread gravity;
	World world;
	ArrayList<GameObject> objects;
	
	static final float[] iMatFlat = new float[] {1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1};
	
	long timeTemp;
	long fpsTime;
	
	boolean UIActive;
	int UIVAO;
	int UINumElements;
	int UIShaderProgram;
	
	public Graphics() {
		screenDims = new int[] {1920,1080};
		String windowTitle = "Game Window";
		window = Setup.start(screenDims, windowTitle);
		keyboardThread = new KeyboardManager(this);
		mouseThread = new MouseManager();
		gravity = new GravityThread();
		objects = new ArrayList<GameObject>();
		fpsTime = System.currentTimeMillis();
		UIActive = false;
		init();
	}
	
	public void init() {
		cam = new Camera(screenDims);
		gravity.setCamera(cam);
		project = new Projection(70f, 0.1f, 500f, screenDims);
		//vertShader = new Shader("Shaders/basicProjection.vtxs",GL_VERTEX_SHADER);
		vertShader = new Shader("Shaders/basicProjWithModel.vtxs",GL_VERTEX_SHADER);
		fragShader = new Shader("Shaders/singleColor.frgs",GL_FRAGMENT_SHADER);
		shaderProgram = glCreateProgram();
		glAttachShader(shaderProgram,vertShader.getShader());
		glAttachShader(shaderProgram,fragShader.getShader());
		glLinkProgram(shaderProgram);
		
		Shader UIvertShader = new Shader("Shaders/UIVert.vtxs",GL_VERTEX_SHADER);
		Shader UIfragShader = new Shader("Shaders/singleColor.frgs",GL_FRAGMENT_SHADER);
		UIShaderProgram = glCreateProgram();
		glAttachShader(UIShaderProgram,UIvertShader.getShader());
		glAttachShader(UIShaderProgram,UIfragShader.getShader());
		glLinkProgram(UIShaderProgram);
		
		glUseProgram(shaderProgram);
		glDeleteShader(vertShader.getShader());
		glDeleteShader(fragShader.getShader());
		
		glDeleteShader(UIvertShader.getShader());
		glDeleteShader(UIfragShader.getShader());
		
		
		vertices = null;
		indices = null;
		numElements = 0;
		//glClearColor(0.0f,0.0f,0.0f,1.0f);
		glClearColor(0.275f,0.94f,0.97f,1.0f);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_MULTISAMPLE); 
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true);
			if ( key == GLFW_KEY_SPACE && action == GLFW_PRESS) {
				cam.jump();
			}
			if (key == GLFW_KEY_0 && action == GLFW_PRESS) {
				for(GameObject go : objects) {
					go.rotate('z', (float)(Math.PI/8), false);
				}
			}
			keyboardThread.keyEvent(key, action);
		});
		glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
			mouseThread.mouseMovement(xpos, ypos);
		});
		cam.translate(0f, -60f, 0f);
		//loop();
	}
	
	private void startTimer() {
		timeTemp = System.nanoTime();
	}
	
	private void endTimer(String title) {
		System.out.println("TIME [" + title + "]: " + (System.nanoTime()-timeTemp));
		startTimer();
	}
	
	public void loop() {
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		while(!glfwWindowShouldClose(window)) {
			updateFPS();
			
			if (UIActive) {
				glUseProgram(UIShaderProgram);
				glBindVertexArray(UIVAO);
				glDrawArrays(GL_LINES,0,2);
			}
			
			//startTimer();
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
			
			int modelMatLoc = glGetUniformLocation(shaderProgram,"model");
			glUniformMatrix4fv(modelMatLoc, false, iMatFlat);
			
			int transFloatLoc = glGetUniformLocation(shaderProgram,"transP");
			glUniform1f(transFloatLoc, 1f);
			
			//endTimer("LoopInit");
			
			glBindVertexArray(VAO);
			glDrawArrays(GL_TRIANGLES,0,numElements);
			
			//endTimer("Draw Ground Mesh");
			
			int objID = 0;
			
			boolean displayBounds = true;
			for(GameObject go : objects) {
				
				if (displayBounds) {
					modelMatLoc = glGetUniformLocation(shaderProgram,"model");
					glUniformMatrix4fv(modelMatLoc, false, iMatFlat);
					
					
					glPolygonMode( GL_FRONT_AND_BACK, GL_LINE );
					
					int VBOt,VAOt;
					VBOt = glGenBuffers();
					VAOt = glGenVertexArrays();
					glBindVertexArray(VAOt);
					glBindBuffer(GL_ARRAY_BUFFER,VBOt);
					float[] vert = go.getBounds().getVertices();
					glBufferData(GL_ARRAY_BUFFER, vert, GL_STREAM_DRAW);
					glVertexAttribPointer(0,3,GL_FLOAT,false,24,0l);
					glEnableVertexAttribArray(0);
					glVertexAttribPointer(1,3,GL_FLOAT,false,24,12l);
					glEnableVertexAttribArray(1);
					glBindBuffer(GL_ARRAY_BUFFER,0);
					glDrawArrays(GL_TRIANGLES,0,vert.length);
					
					
					glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
				}

				
				//startTimer();
				//System.out.println("Rendering Object ID " + objID);
				modelMatLoc = glGetUniformLocation(shaderProgram,"model");
				glUniformMatrix4fv(modelMatLoc, false, go.getModelMatFlat());
				//endTimer("Uniform Updating");
				//System.out.println("Object Model Matrix: " );
				//for(float f : go.getModelMatFlat()) {
				//	System.out.print(f + " ");
				//}
				//System.out.println();
				glBindVertexArray(go.getVAO());
				//endTimer("Bind VAO");
				//int numE = go.getVertices().length;
				int numE = go.vertT.length;
				//endTimer("Get Arr Len");
				glDrawArrays(GL_TRIANGLES,0,numE);
				//endTimer("Draw Call");
				objID++;
				//if (cam.bounds.intersectsAABB(go.getBounds())) {
					//System.out.println("CAMERA INTERSECTING OBJECT");
				//}
			}
			
			//endTimer("Draw GameObjects");
			
			
			//glDrawElements(GL_TRIANGLES,3,GL_UNSIGNED_INT,0);
			
			
			glfwSwapBuffers(window);
			glfwPollEvents();
			//apply camera/game updates
			
			//endTimer("Swap Buffers");
			
			float[] camPos = cam.getCamPos();
			//System.out.println("Camera Position: (" + camPos[0] + "," + camPos[1] + "," + camPos[2]);
			float[] translateT = keyboardThread.getTranslate();
			cam.translate(translateT[0], translateT[1], translateT[2]);
			float[] rotateT = mouseThread.getRotation();
			cam.rotate('x', rotateT[1], false);
			cam.rotate('y', rotateT[0], true);
			this.updateTransformMatrix();
			
			//endTimer("Camera Transforms");
			
			gravity.setGameObjects(objects);
			gravity.run();
			
			//endTimer("Gravity Thread");
		}
	}
	
	private void updateFPS() {
		long frameTime = System.currentTimeMillis()-fpsTime;
		double fps = 1000.0/frameTime;
		glfwSetWindowTitle(window, ""+fps);
		fpsTime = System.currentTimeMillis();
	}
	
	private void updateTransformMatrix() {
		float[] matCom = combineMats(project.getProjMatFMat(),cam.getCamMat());
		int fullMatLoc = glGetUniformLocation(shaderProgram,"fullMat");
		glUniformMatrix4fv(fullMatLoc, false, matCom);
		
	}
	
	public void setWorld(World w) {
		this.world = w;
		cam.setWorld(world);
	}
	
	public void setGameObjects(ArrayList<GameObject> newObjs) {
		this.objects = newObjs;
		for(GameObject go : objects) {
			//process buffers, add the VAO to gameobject
			processGameObject(go);
		}
	}
	
	public void addGameObject(GameObject go) {
		this.objects.add(go);
		processGameObject(go);
	}
	
	public void addGameObjects(ArrayList<GameObject> newObjs) {
		for(GameObject go : newObjs) {
			this.objects.add(go);
			processGameObject(go);
		}
	}
	
	private void processGameObject(GameObject go) {
		float[] tempVert = go.getVertices();
		go.vertT = tempVert;
		int[] tempInd = go.getIndices();
		int VBOt,VAOt,EBOt;
		VBOt = glGenBuffers();
		VAOt = glGenVertexArrays();
		EBOt = glGenBuffers();
		glBindVertexArray(VAOt);
		glBindBuffer(GL_ARRAY_BUFFER,VBOt);
		glBufferData(GL_ARRAY_BUFFER, tempVert, GL_STREAM_DRAW);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBOt);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, tempInd, GL_STREAM_DRAW);
		glVertexAttribPointer(0,3,GL_FLOAT,false,24,0l);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1,3,GL_FLOAT,false,24,12l);
		glEnableVertexAttribArray(1);
		glBindBuffer(GL_ARRAY_BUFFER,0);
		glBindVertexArray(0);
		go.setVAO(VAOt);
	}
	
	public void setUIData(float[] vertices) {
		UIActive = true;
		UINumElements = vertices.length;
		int VBOt,VAOt;
		VBOt = glGenBuffers();
		VAOt = glGenVertexArrays();
		glBindVertexArray(VAOt);
		glBindBuffer(GL_ARRAY_BUFFER,VBOt);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
		glVertexAttribPointer(0,2,GL_FLOAT,false,8,0l);
		glEnableVertexAttribArray(0);
		//glVertexAttribPointer(1,3,GL_FLOAT,false,24,12l);
		//glEnableVertexAttribArray(1);
		UIVAO = VAOt;
		glBindBuffer(GL_ARRAY_BUFFER,0);
		glBindVertexArray(0);
		
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
		glVertexAttribPointer(0,3,GL_FLOAT,false,24,0l);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1,3,GL_FLOAT,false,24,12l);
		glEnableVertexAttribArray(1);
		//glVertexAttribPointer(0,3,GL_FLOAT,false,12,0l);
		//glEnableVertexAttribArray(0);
		glBindBuffer(GL_ARRAY_BUFFER,0);
		glBindVertexArray(VAO);
		numElements = vertices.length;
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
