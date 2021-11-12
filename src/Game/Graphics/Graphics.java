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
	
	float[] playerXZ = new float[2];
	boolean worldUpdateReady = false;
	float[] newWorldVert;
	boolean updatingVAO = false; 
	
	int waterVAO;
	int waterShaderProgram;
	int waterVertexCount;
	
	boolean sunUpdate = false;
	float[] newSunPos;
	Thread dayNightThreadT;
	DayNightThread dayNightThreadDNT;
	
	//Tesselation stuff
	int[] tessVAOs;
	int tesselationShaderProgram;
	
	public Graphics(int[] screenDims) {
		this.screenDims = screenDims;
		//screenDims = new int[] {1920,1080};
		String windowTitle = "Game Window";
		window = Setup.start(screenDims, windowTitle);
		keyboardThread = new KeyboardManager();
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
		Shader tessFragShader = new Shader("Shaders/tess_fs.vtxs", GL_FRAGMENT_SHADER);
		Shader tessGeoShader = new Shader("Shaders/tess_gs.vtxs", GL_GEOMETRY_SHADER);
		shaderProgram = glCreateProgram();
		glAttachShader(shaderProgram,vertShader.getShader());
		glAttachShader(shaderProgram,fragShader.getShader());
		glLinkProgram(shaderProgram);
		glUseProgram(shaderProgram);
		
		int lightPosLoc = glGetUniformLocation(shaderProgram, "lightPos");
		int lightColLoc = glGetUniformLocation(shaderProgram, "lightColor");
		
		glUniform3fv(lightPosLoc, new float[] {-10000,6000,-10000}); //light position
		glUniform3fv(lightColLoc, new float[] {1.0f,1.0f,1.0f}); //light color
		
		Shader UIvertShader = new Shader("Shaders/UIVert.vtxs",GL_VERTEX_SHADER);
		Shader UIfragShader = new Shader("Shaders/singleColor.frgs",GL_FRAGMENT_SHADER);
		UIShaderProgram = glCreateProgram();
		glAttachShader(UIShaderProgram,UIvertShader.getShader());
		glAttachShader(UIShaderProgram,UIfragShader.getShader());
		glLinkProgram(UIShaderProgram);
		
		Shader skyVertShader = new Shader("Shaders/simpleSkyVert.vtxs",GL_VERTEX_SHADER);
		//Shader skyFragShader = new Shader("Shaders/singleColor.frgs",GL_FRAGMENT_SHADER);
		Shader skyFragShader = new Shader("Shaders/basicSunFrag.frgs",GL_FRAGMENT_SHADER);
		skyShaderProgram = glCreateProgram();
		glAttachShader(skyShaderProgram,skyVertShader.getShader());
		glAttachShader(skyShaderProgram,skyFragShader.getShader());
		glLinkProgram(skyShaderProgram);
		
		Shader tessCS = new Shader("Shaders/tcs.vtxs",GL_TESS_CONTROL_SHADER);
		Shader tessES = new Shader("Shaders/tes.vtxs",GL_TESS_EVALUATION_SHADER);
		System.out.println("Created objects");
		
		tesselationShaderProgram = glCreateProgram();
		glAttachShader(tesselationShaderProgram,vertShader.getShader());
		glAttachShader(tesselationShaderProgram,tessCS.getShader());
		glAttachShader(tesselationShaderProgram,tessES.getShader());
		glAttachShader(tesselationShaderProgram, tessGeoShader.getShader());
		glAttachShader(tesselationShaderProgram,tessFragShader.getShader());
		glLinkProgram(tesselationShaderProgram);
		
		glDeleteShader(tessCS.getShader());
		glDeleteShader(tessES.getShader());
		
		glDeleteShader(vertShader.getShader());
		glDeleteShader(fragShader.getShader());
		
		glDeleteShader(UIvertShader.getShader());
		glDeleteShader(UIfragShader.getShader());
		
		glDeleteShader(skyVertShader.getShader());
		glDeleteShader(skyFragShader.getShader());
		
		bindSkyVertices();
		bindTessVertices();
		
		Shader waterVertShader = new Shader("Shaders/basicProjWithModel.vtxs",GL_VERTEX_SHADER);
		Shader waterFragShader = new Shader("Shaders/singleColor.frgs",GL_FRAGMENT_SHADER);
		waterShaderProgram = glCreateProgram();
		glAttachShader(waterShaderProgram,waterVertShader.getShader());
		glAttachShader(waterShaderProgram,waterFragShader.getShader());
		glLinkProgram(waterShaderProgram);
		glDeleteShader(waterVertShader.getShader());
		glDeleteShader(waterFragShader.getShader());
		
		
		
		
		vertices = null;
		indices = null;
		numElements = 0;
		//glClearColor(0.0f,0.0f,0.0f,1.0f);
		glClearColor(0.275f,0.94f,0.97f,1.0f);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_MULTISAMPLE); 
		
		//xglEnable(GL_CULL_FACE); 
		glLineWidth(3.0f);
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true);
			if ( key == GLFW_KEY_SPACE && action == GLFW_PRESS) {
				cam.jump();
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
			if (key == GLFW_KEY_T && action == GLFW_PRESS) {
				Mesh m = ObjectGeneration.generateTree((int)(Math.random()*100000000), 8);
				GameObject go1 = new GameObject("Tree",world,m);
				go1.translate(-cam.getCamPos()[0], -cam.getCamPos()[1]+10f, -cam.getCamPos()[2]);
				this.addGameObject(go1);
			}
			
			keyboardThread.keyEvent(key, action);
			int hotBarSelected = keyboardThread.getHotbarKey(key, action);
			if (hotBarSelected != -1) {
				UIManager.setHotbarSlot(hotBarSelected);
			}
		});
		glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
			mouseThread.mouseMovement(xpos, ypos);
		});
		cam.translate(0f, -60f, 0f);
		
		/* temporarily removed during organization overhaul
		dayNightThreadDNT = new DayNightThread(this);
		dayNightThreadT = new Thread(dayNightThreadDNT);
		dayNightThreadT.start();
		*/
		
		//loop();
	}
	
	public void setWorldUpdateReady(float[] newVert) {
		worldUpdateReady = true;
		newWorldVert = newVert;
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
		bindWaterVertices();
		
		int loops = 0;
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
			
			int camRotYUniformLoc = glGetUniformLocation(skyShaderProgram, "camRotYU");
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
			boolean tess = false;
			if(!tess) {
				glUseProgram(waterShaderProgram);
				int modelMatLocT = glGetUniformLocation(waterShaderProgram,"model");
				glUniformMatrix4fv(modelMatLocT, false, iMatFlat);
				glBindVertexArray(waterVAO);
				glDrawArrays(GL_TRIANGLES,0,waterVertexCount);
				
				
				
				
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
			}else {
				glUseProgram(tesselationShaderProgram);
				for(int i = 0; i < tessVAOs.length; i++) {
					glBindVertexArray(tessVAOs[i]);
					glDrawArrays(GL_PATCHES,0,4);
				}
				glDisableVertexAttribArray(0);
				glBindVertexArray(0);
			}
			
			//endTimer("Draw GameObjects");
			
			
			//glDrawElements(GL_TRIANGLES,3,GL_UNSIGNED_INT,0);
			
			//glEnable(GL_BLEND);
			//glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			
			
			
			//glDisable(GL_BLEND);
			
			if (!updatingVAO) {
				glfwSwapBuffers(window);
				glfwPollEvents();
			}
			
			
			loops++;
			if (worldUpdateReady) {
				System.out.println("loops complete: " + loops);
				this.updateData(newWorldVert, new int[0]);
				System.out.println("Generated chunk");
				worldUpdateReady = false;
			}
			float[] currentXZ = new float[] {cam.getCamPos()[0],cam.getCamPos()[2]};
			long sTime = System.nanoTime();
			if (Math.sqrt((currentXZ[0]-playerXZ[0])*(currentXZ[0]-playerXZ[0])+(currentXZ[1]-playerXZ[1])*(currentXZ[1]-playerXZ[1])) > 200) {
				loops = 0;
				System.out.println("Generating new chunk");
				playerXZ = currentXZ;
				//new Thread(new WorldUpdateThread(this,world,new int[] {(int)currentXZ[0],(int)currentXZ[1]})).start();
				//world.updateWorld(-(int)currentXZ[0],-(int)currentXZ[1]);
				//this.updateData(world.vertices, world.indices);
				long eTime = System.nanoTime();
				System.out.println("Time for thread starting: " + (eTime-sTime));
			}
			
			
			
			
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
			
			glUseProgram(skyShaderProgram);
			int skyFullMatUniformLoc = glGetUniformLocation(skyShaderProgram, "fullMat");
			float[] matComT = combineMats(project.getProjMatFMat(),cam.getCamMat());
			glUniformMatrix4fv(skyFullMatUniformLoc, false, matComT);
			FloatMatrix projMat = project.getProjMatFMat();
			FloatMatrix camMat = cam.getCamMat();
			FloatMatrix sunPoint = new FloatMatrix(new float[] {-10000.0f,6000.0f,-10000.0f,1.0f});
			FloatMatrix resultTemp = projMat.mmul(camMat.mmul(sunPoint));
			float zValTemp = resultTemp.get(2);
			resultTemp.divi(resultTemp.get(2));
			//System.out.println("Result Location: " + resultTemp);
			float[] sunPositionProjected = new float[] {resultTemp.get(0),resultTemp.get(1),zValTemp};
			int skySunProjUniformLoc = glGetUniformLocation(skyShaderProgram, "sunPosition");
			glUniform3fv(skySunProjUniformLoc, sunPositionProjected);
			
			//if (sunUpdate) {
			//	sunUpdate = false;
			updateSunPosition(newSunPos);
			glUseProgram(skyShaderProgram);
			int timeUniformLoc = glGetUniformLocation(skyShaderProgram, "time");
			glUniform1i(timeUniformLoc,dayNightThreadDNT.getTime());
			int sunColorUniformLoc = glGetUniformLocation(skyShaderProgram, "sunColor");
			double timeVal = ((double)dayNightThreadDNT.getTime()/(1000.0f));
			float sunRedT = 1.0f;
			//float sunGreenT = (float)(((Math.cos(1.2*timeVal)+1.0)/2.5) + (0.4)*Math.sin(0.06*timeVal));
			float sunGreenT = (float)(Math.max(-0.0343*(1.2*timeVal-3.1415)*(1.2*timeVal-3.1415)*(1.2*timeVal-3.1415), 0.0) + Math.cos((0.5)*((1/2.5)*timeVal-3.5)));
			float sunBlueT = (float)(1.0/(100*(timeVal+0.44)));
			if (sunGreenT > 1.0f) {
				sunGreenT = 1.0f;
			}
			if (sunGreenT < 0.0f) {
				sunGreenT = 0.0f;
			}
			//System.out.print("timeVal: " + timeVal);
			//System.out.println(" Sun color: GREEN: " + sunGreenT + " BLUE: " + sunBlueT);
			glUniform3fv(sunColorUniformLoc,new float[] {sunRedT,sunGreenT,sunBlueT});
			
			glUseProgram(shaderProgram);
			int lightColLoc = glGetUniformLocation(shaderProgram, "lightColor");
			glUniform3fv(lightColLoc, new float[] {sunRedT,sunGreenT,sunBlueT}); //light color
			
			//}
			
			
			glUseProgram(waterShaderProgram);
			float[] matCom = combineMats(project.getProjMatFMat(),cam.getCamMat());
			int fullMatLoc = glGetUniformLocation(waterShaderProgram,"fullMat");
			glUniformMatrix4fv(fullMatLoc, false, matCom);
			
			//endTimer("Camera Transforms");
			
			gravity.setGameObjects(objects);
		 	gravity.run();
			
			//endTimer("Gravity Thread");
		}
		try {
			dayNightThreadDNT.close();
			((Thread)dayNightThreadT).join();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public void initSunUpdate(float[] newPos) {
		sunUpdate = true;
		newSunPos = newPos;
	}
	
	public void updateSunPosition(float[] newSunPos) {
		glUseProgram(skyShaderProgram);
		FloatMatrix projMat = project.getProjMatFMat();
		FloatMatrix camMat = cam.getCamMat();
		//FloatMatrix sunPoint = new FloatMatrix(new float[] {-10000.0f,6000.0f,-10000.0f,1.0f});
		FloatMatrix sunPoint = new FloatMatrix(newSunPos);
		FloatMatrix resultTemp = projMat.mmul(camMat.mmul(sunPoint));
		float zValTemp = resultTemp.get(2);
		resultTemp.divi(resultTemp.get(2));
		//System.out.println("Result Location: " + resultTemp);
		float[] sunPositionProjected = new float[] {resultTemp.get(0),resultTemp.get(1),zValTemp};
		int skySunProjUniformLoc = glGetUniformLocation(skyShaderProgram, "sunPosition");
		glUniform3fv(skySunProjUniformLoc, sunPositionProjected);
		glUseProgram(shaderProgram);
		int lightPosLoc = glGetUniformLocation(shaderProgram, "lightPos");
		//int lightColLoc = glGetUniformLocation(shaderProgram, "lightColor");
		glUniform3fv(lightPosLoc, newSunPos); //light position
		//glUniform3fv(lightColLoc, new float[] {1.0f,1.0f,1.0f}); //light color
	}
	
	
	public World getWorld() {
		return world;
	}
	public ArrayList<GameObject> getGameObjects(){
		return objects;
	}
	
	private void bindTessVertices() {
		int worldWidth = 20;
		int worldLength = 20;
		int chunkDim = 100;
		tessVAOs = new int[20*20];
		for(int x = 0; x < worldWidth; x++) {
			for(int y = 0; y < worldLength; y++) {
				int vbo = glGenBuffers();
				int VAO = glGenVertexArrays();
				tessVAOs[x*worldWidth+y] = VAO;
				glBindVertexArray(VAO);
				glBindBuffer(GL_ARRAY_BUFFER, VBO);
				int xLoc = x-(worldWidth/2);
				int yLoc = y-(worldLength/2);
				float[] verts = new float[] {xLoc*chunkDim, yLoc*chunkDim, xLoc*chunkDim, yLoc*chunkDim+chunkDim, xLoc*chunkDim+chunkDim, yLoc*chunkDim+chunkDim, xLoc*chunkDim+chunkDim, yLoc*chunkDim};
				glBufferData(GL_ARRAY_BUFFER, verts, GL_STATIC_DRAW);
				glVertexAttribPointer(0, 2, GL_FLOAT, false, 8, 0l);
				glPatchParameteri(GL_PATCH_VERTICES, 4);
				glBindVertexArray(0);
			}
		}
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
	
	private void bindWaterVertices() {
		//float[] verts = world.getWater();
		//float[] verts = new float[0];
		Mesh waterMesh = world.getWater();
		//GameObject go = new GameObject("ocean",world,waterMesh);
		ArrayList<Polygon> polys = waterMesh.getPolygons();
		float[] vertices = new float[polys.size()*18];
		int vertIn = 0;
		for(Polygon p : polys) {
			FloatMatrix[] polyPoints = p.getPoints();
			for(int pi2=0;pi2<3;pi2++) {
				for(int pi=0;pi<3;pi++) {
					vertices[vertIn] = polyPoints[pi2].get(pi);
					vertIn++;
				}
				float[] colT = p.fColor[pi2];
				vertices[vertIn] = colT[0];
				vertices[vertIn+1] = colT[1];
				vertices[vertIn+2] = colT[2];
				vertIn+=3;
			}
		}
		
		//float[] waterVerts = go.getVertices();
		float[] waterVerts = vertices;
		int VBOt,VAOt;
		VBOt = glGenBuffers();
		VAOt = glGenVertexArrays();
		glBindVertexArray(VAOt);
		glBindBuffer(GL_ARRAY_BUFFER,VBOt);
		glBufferData(GL_ARRAY_BUFFER, waterVerts, GL_STATIC_DRAW);
		glVertexAttribPointer(0,3,GL_FLOAT,false,24,0l);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1,3,GL_FLOAT,false,24,12l);
		glEnableVertexAttribArray(1);
		glBindBuffer(GL_ARRAY_BUFFER,0);
		glBindVertexArray(0);
		waterVAO = VAOt;
		waterVertexCount = vertices.length;
	}
	
	private void updateFPS() {
		long frameTime = System.currentTimeMillis()-fpsTime;
		double fps = 1000.0/frameTime;
		glfwSetWindowTitle(window, ""+fps);
		fpsTime = System.currentTimeMillis();
	}
	
	private void updateTransformMatrix() {
		//glUseProgram(tesselationShaderProgram);
		glUseProgram(shaderProgram);
		float[] matCom = combineMats(project.getProjMatFMat(),cam.getCamMat());
		int fullMatLoc = glGetUniformLocation(shaderProgram,"fullMat");
		//int fullMatLoc = glGetUniformLocation(tesselationShaderProgram,"fullMat");
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
		long sTime = System.nanoTime();
		this.vertices = vertices;
		this.indices = indices;
		int VBOt,VAOt,EBOt;
		VBOt = glGenBuffers();
		VAOt = glGenVertexArrays();
		EBOt = glGenBuffers();
		
		glBindVertexArray(VAOt);
		glBindBuffer(GL_ARRAY_BUFFER,VBOt);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_STREAM_DRAW);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBOt);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STREAM_DRAW);
		glVertexAttribPointer(0,3,GL_FLOAT,false,36,0l);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1,3,GL_FLOAT,false,36,12l);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(2,3,GL_FLOAT,false,36,24l);
		glEnableVertexAttribArray(2);
		
		//glVertexAttribPointer(0,3,GL_FLOAT,false,12,0l);
		//glEnableVertexAttribArray(0);
		updatingVAO = true;
		glBindBuffer(GL_ARRAY_BUFFER,0);
		glBindVertexArray(VAOt);
		VBO=VBOt;VAO=VAOt;EBO=EBOt;
		numElements = vertices.length;
		//glBindVertexArray(0);
		updatingVAO = false;
		long eTime = System.nanoTime();
		System.out.println("Buffer load time: " + (eTime-sTime)/(Math.pow(10,9)));
		
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
