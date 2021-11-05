package Game.Graphics;

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
	public ArrayList<GameObject> objectQueue;
	UIManager UIManager;
	
	static final float[] iMatFlat = new float[] {1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1};
	
	long timeTemp;
	long fpsTime;
	boolean drawBounds;
	
	boolean UIActive;
	int UIVAO;
	int UINumElements;
	int UIShaderProgram;
	
	static final float[] skyVert = new float[] {-1.0f,1.0f,-1.0f,-1.0f,1.0f,1.0f,
												-1.0f,-1.0f,1.0f,-1.0f,1.0f,1.0f};
	int skyVAO;
	int skyShaderProgram;
	
	
	public Graphics(int[] screenDims) {
		this.screenDims = screenDims;
		//screenDims = new int[] {1920,1080};
		String windowTitle = "Game Window";
		window = Setup.start(screenDims, windowTitle);
		keyboardThread = new KeyboardManager(this);
		mouseThread = new MouseManager();
		gravity = new GravityThread();
		objects = new ArrayList<GameObject>();
		objectQueue = new ArrayList<GameObject>();
		fpsTime = System.currentTimeMillis();
		drawBounds = false;
		UIActive = false;
		UIManager = new UIManager(screenDims);
		init();
	}
	
	public void init() {
		cam = new Camera(screenDims);
		gravity.setCamera(cam);
		project = new Projection(70f, 0.1f, 10000f, screenDims);
		//vertShader = new Shader("Shaders/basicProjection.vtxs",GL_VERTEX_SHADER);
		//vertShader = new Shader("Shaders/basicProjWithModel.vtxs",GL_VERTEX_SHADER);
		//fragShader = new Shader("Shaders/singleColor.frgs",GL_FRAGMENT_SHADER);
		vertShader = new Shader("Shaders/basicProjModelLighting.vtxs",GL_VERTEX_SHADER);
		fragShader = new Shader("Shaders/basicLighting.frgs",GL_FRAGMENT_SHADER);
		
		shaderProgram = glCreateProgram();
		glAttachShader(shaderProgram,vertShader.getShader());
		glAttachShader(shaderProgram,fragShader.getShader());
		glLinkProgram(shaderProgram);
		glUseProgram(shaderProgram);
		
		int lightPosLoc = glGetUniformLocation(shaderProgram, "lightPos");
		int lightColLoc = glGetUniformLocation(shaderProgram, "lightColor");
		
		glUniform3fv(lightPosLoc, new float[] {-1000,6000,-10000}); //light position
		glUniform3fv(lightColLoc, new float[] {1.0f,1.0f,1.0f}); //light color
		
		Shader UIvertShader = new Shader("Shaders/UIVert.vtxs",GL_VERTEX_SHADER);
		Shader UIfragShader = new Shader("Shaders/singleColor.frgs",GL_FRAGMENT_SHADER);
		UIShaderProgram = glCreateProgram();
		glAttachShader(UIShaderProgram,UIvertShader.getShader());
		glAttachShader(UIShaderProgram,UIfragShader.getShader());
		glLinkProgram(UIShaderProgram);
		
		Shader skyVertShader = new Shader("Shaders/simpleSkyVert.vtxs",GL_VERTEX_SHADER);
		Shader skyFragShader = new Shader("Shaders/singleColor.frgs",GL_FRAGMENT_SHADER);
		skyShaderProgram = glCreateProgram();
		glAttachShader(skyShaderProgram,skyVertShader.getShader());
		glAttachShader(skyShaderProgram,skyFragShader.getShader());
		glLinkProgram(skyShaderProgram);
		
		glDeleteShader(vertShader.getShader());
		glDeleteShader(fragShader.getShader());
		
		glDeleteShader(UIvertShader.getShader());
		glDeleteShader(UIfragShader.getShader());
		
		glDeleteShader(skyVertShader.getShader());
		glDeleteShader(skyFragShader.getShader());
		
		bindSkyVertices();
		
		vertices = null;
		indices = null;
		numElements = 0;
		//glClearColor(0.0f,0.0f,0.0f,1.0f);
		glClearColor(0.275f,0.94f,0.97f,1.0f);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_MULTISAMPLE); 
		//xglEnable(GL_CULL_FACE); 
		glLineWidth(3.f);
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
			if (key == GLFW_KEY_B && action == GLFW_PRESS) {
				drawBounds = !drawBounds;
			}
			if (key == GLFW_KEY_Z && action == GLFW_PRESS) {
				Mesh arrowMesh = new Mesh(true);
				Polygon arrowP = new Polygon(new float[] {0f,0.2f,0f},new float[] {0f,-0.2f,0f},new float[] {0f,0f,1f});
				arrowP.setFColor(new float[] {1.0f,0.0f,0.0f,1.0f});
				arrowMesh.addToMesh(arrowP);
				GameObject arrow = new GameObject("Arrow", world, arrowMesh);
				float[] camPos = cam.getCamPos();
				arrow.setPosition(new float[] {-camPos[0],-camPos[1],-camPos[2]});
				float[] camRot = cam.getRotations();
				arrow.setRotation(new float[] {-camRot[0],camRot[1],-camRot[2]});
				float[] arrowVelocityVector = new float[] {0.0f,0.0f,-1.0f};
				FloatMatrix rotArrVec = Operations.rotatePoint(new FloatMatrix(arrowVelocityVector), 'x', -camRot[0]);
				rotArrVec = Operations.rotatePoint(rotArrVec, 'y', -camRot[1]);
				rotArrVec = Operations.rotatePoint(rotArrVec, 'z', -camRot[2]);
				float[] arrVelVec = new float[] {rotArrVec.get(0),rotArrVec.get(1),rotArrVec.get(2)};
				arrow.setVelocity(arrVelVec);
				addGameObject(arrow);
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
			//Will add in spawnable object like people
			if(objectQueue.size() > 0) {
				addGameObject(objectQueue.get(0));
				objectQueue.remove(0);
			}
			
			
			
			this.setUIData(UIManager.getUIVertices());
			
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			
			glUseProgram(skyShaderProgram);
			
			int camRotYUniformLoc = glGetUniformLocation(skyShaderProgram, "camRotY");
			//System.out.println("Uniform Loc: " + camRotYUniformLoc);
			glUniform1f(camRotYUniformLoc, -(float)Math.sin(cam.getRotations()[0]));
			//System.out.println("IN VALUE: " + -(float)Math.sin(cam.getRotations()[0]));
			//System.out.println("CamRotY: " + cam.getRotations()[0] + " sin(camRotY): " + Math.sin(cam.getRotations()[0]));
			
			glDisable(GL_DEPTH_TEST);
			
			glBindVertexArray(skyVAO);
			glDrawArrays(GL_TRIANGLES,0,6);
			glEnable(GL_DEPTH_TEST);
			
			if (UIActive) {
				glUseProgram(UIShaderProgram);
				glBindVertexArray(UIVAO);
				glDrawArrays(GL_LINES,0,UINumElements);
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
			
			glUseProgram(shaderProgram);
			
			float[] cpt = cam.getCamPos();
			//int lightPosLoc = glGetUniformLocation(shaderProgram, "lightPos");
			//glUniform3fv(lightPosLoc,new float[] {-cpt[0],-cpt[1],-cpt[2]});
			
			int viewPosLoc = glGetUniformLocation(shaderProgram, "viewPos");
			glUniform3fv(viewPosLoc,new float[] {-cpt[0],-cpt[1],-cpt[2]});
			
			int modelMatLoc = glGetUniformLocation(shaderProgram,"model");
			glUniformMatrix4fv(modelMatLoc, false, iMatFlat);
			
			
			
			//int transFloatLoc = glGetUniformLocation(shaderProgram,"transP");
			//glUniform1f(transFloatLoc, 1f);
			
			//endTimer("LoopInit");
			
			int modelInvTranMatLoc = glGetUniformLocation(shaderProgram, "invTranMod");
			//FloatMatrix invTranModFM = Solve.pinv(go.getModelMat().transpose());
			//float[] flatMat = invTranModFM.data;
			glUniformMatrix4fv(modelInvTranMatLoc,false,iMatFlat);
			
			glBindVertexArray(VAO);
			glDrawArrays(GL_TRIANGLES,0,numElements);
			
			//endTimer("Draw Ground Mesh");
			
			int objID = 0;
		
			
			for(GameObject go : objects) {
				
				if (drawBounds) {
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
				
				modelInvTranMatLoc = glGetUniformLocation(shaderProgram, "invTranMod");
				FloatMatrix invTranModFM = Solve.pinv(go.getModelMat().transpose());
				float[] flatMat = invTranModFM.data;
				glUniformMatrix4fv(modelInvTranMatLoc,false,flatMat);
				
				//endTimer("Uniform Updating");
				//System.out.println("Object Model Matrix: " );
				//for(float f : go.getModelMatFlat()) {
				//	System.out.print(f + " ");
				//}
				//System.out.println();
				glBindVertexArray(go.getVAO());
				//endTimer("Bind VAO");
				//int numE = go.getVertices().length;
				if(go.vertT!=null) {
					int numE = go.vertT.length;
					//endTimer("Get Arr Len");
					glDrawArrays(GL_TRIANGLES,0,numE);
				}
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
	public World getWorld() {
		return world;
	}
	public ArrayList<GameObject> getGameObjects(){
		return objects;
	}
	private void bindSkyVertices() {
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
		glVertexAttribPointer(0,3,GL_FLOAT,false,36,0l);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1,3,GL_FLOAT,false,36,12l);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(2,3,GL_FLOAT,false,36,24l);
		glEnableVertexAttribArray(2);
		
		glBindBuffer(GL_ARRAY_BUFFER,0);
		glBindVertexArray(0);
		go.setVAO(VAOt);
	}
	
	public void setUIData(float[] UIvertices) {
		UIActive = true;
		UINumElements = UIvertices.length;
		int VBOt,VAOt;
		VBOt = glGenBuffers();
		VAOt = glGenVertexArrays();
		glBindVertexArray(VAOt);
		glBindBuffer(GL_ARRAY_BUFFER,VBOt);
		glBufferData(GL_ARRAY_BUFFER, UIvertices, GL_STATIC_DRAW);
		glVertexAttribPointer(0,2,GL_FLOAT,false,20,0l);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1,3,GL_FLOAT,false,20,8l);
		glEnableVertexAttribArray(1);
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
		glVertexAttribPointer(0,3,GL_FLOAT,false,36,0l);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1,3,GL_FLOAT,false,36,12l);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(2,3,GL_FLOAT,false,36,24l);
		glEnableVertexAttribArray(2);
		
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
