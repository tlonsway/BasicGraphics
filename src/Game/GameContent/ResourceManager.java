package Game.GameContent;

import java.util.*;
import Game.GameData.*;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import Game.GameData.*;
import Game.Graphics.*;
import Game.Network.*;
import Game.Init.Setup;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glUniform3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.nio.*;
import java.util.*;
import org.jblas.*;

public class ResourceManager {
	
	GameManager manager;
	HashMap<VAOStorage,ArrayList<PhysicalResource>> resources;
	
	int shaderProgram;
	
	static final float[] iMatFlat = new float[] {1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1};
	
	static final float HQDistCutoff = 100.0f;
	static final float MQDistCutoff = 250.0f;
	static final float LQDistCutoff = 500.0f;
	
	public ResourceManager(GameManager manager) {
		this.manager = manager;
		resources = new HashMap<VAOStorage, ArrayList<PhysicalResource>>();
		Shader vertShader = new Shader("Shaders/basicProjModelLighting.vtxs",GL_VERTEX_SHADER);
		Shader fragShader = new Shader("Shaders/basicLighting.frgs",GL_FRAGMENT_SHADER);
		shaderProgram = glCreateProgram();
		glAttachShader(shaderProgram,vertShader.getShader());
		glAttachShader(shaderProgram,fragShader.getShader());
		glLinkProgram(shaderProgram);
		glUseProgram(shaderProgram);
		glDeleteShader(vertShader.getShader());
		glDeleteShader(fragShader.getShader());
	}
	
	public void addTestTrees() {
		Mesh tree1 = ObjectGeneration.generateTree(9817239, 5);
		Mesh tree1LQ = ObjectGeneration.generateTree(9817239, 2);
		Mesh tree1MQ = ObjectGeneration.generateTree(9817239, 3);
		Mesh tree1HQ = ObjectGeneration.generateTree(9817239, 9);
		VAOStorage vao1 = new VAOStorage(tree1,tree1HQ,tree1MQ,tree1LQ);
		for(int i=0;i<500;i++) {
			float posX = (float)((Math.random()*2000.0)-1000.0);
			float posZ = (float)((Math.random()*2000.0)-1000.0);
			float posY = manager.getWorld().getHeight(posX, posZ)-0.5f;
			if (posY < 10.0f) {
				i--;
				continue;
			}
			float[] pos = new float[] {posX,posY,posZ};
			float[] rot = new float[3];
			PhysicalResource pr = new Tree(vao1, tree1, pos, rot);
			//pr.setQualityVAO(vao1HQ, vao1MQ, vao1LQ);
			this.addResource(pr);
		}
		Mesh tree2 = ObjectGeneration.generateTree(3453452, 5);
		Mesh tree2LQ = ObjectGeneration.generateTree(3453452, 2);
		Mesh tree2MQ = ObjectGeneration.generateTree(3453452, 3);
		Mesh tree2HQ = ObjectGeneration.generateTree(3453452, 9);
		VAOStorage vao2 = new VAOStorage(tree2,tree2HQ,tree2MQ,tree2LQ);
		for(int i=0;i<500;i++) {
			float posX = (float)((Math.random()*2000.0)-1000.0);
			float posZ = (float)((Math.random()*2000.0)-1000.0);
			float posY = manager.getWorld().getHeight(posX, posZ)-0.5f;
			if (posY < 10.0f) {
				i--;
				continue;
			}
			float[] pos = new float[] {posX,posY,posZ};
			float[] rot = new float[3];
			PhysicalResource pr = new Tree(vao2, tree2, pos, rot);
			//pr.setQualityVAO(vao2HQ, vao2MQ, vao2LQ);
			this.addResource(pr);
		}
	}
	
	public void addRocks() {
		Mesh rock = ObjectGeneration.generateSphere(9, 13, 9,5);
		VAOStorage vao1 = new VAOStorage(rock);
		float[] pos = new float[] {0,manager.getWorld().getHeight(0,  0)+10,0};
		float[] rot = new float[3];
		PhysicalResource pr = new Tree(vao1, rock, pos, rot);
		this.addResource(pr);
	}
	
	public void addTestFerns() {
		Mesh fern1 = ObjectGeneration.generateFern(3453426);
		VAOStorage vao1 = new VAOStorage(fern1);
		for(int i=0;i<10000;i++) {
			float posX = (float)((Math.random()*2000.0)-1000.0);
			float posZ = (float)((Math.random()*2000.0)-1000.0);
			float posY = manager.getWorld().getHeight(posX, posZ);
			if (posY < 10.0f || posY > 80.0f) {
				i--;
				continue;
			}
			float[] pos = new float[] {posX,posY,posZ};
			float[] rot = new float[3];
			PhysicalResource pr = new Tree(vao1, fern1, pos, rot);
			this.addResource(pr);
		}
	}
	public void render() {
		Camera cam = manager.getCamera();
		glEnable(GL_DEPTH_TEST);
		glUseProgram(shaderProgram);
		float[] cpt = cam.getCamPos();
		int viewPosLoc = glGetUniformLocation(shaderProgram, "viewPos");
		glUniform3fv(viewPosLoc,new float[] {-cpt[0],-cpt[1],-cpt[2]});
		int modelInvTranMatLoc = glGetUniformLocation(shaderProgram, "invTranMod");
		glUniformMatrix4fv(modelInvTranMatLoc,false,iMatFlat);
		int modelMatLoc = glGetUniformLocation(shaderProgram,"model");
		glUniformMatrix4fv(modelMatLoc, false, iMatFlat);
		updateSun();
		for(VAOStorage vaoS : resources.keySet()) {
			if (!vaoS.isVaoQualityEnabled()) {
				int VAOT = vaoS.getVAO();
				int numVertT = vaoS.getNumVert();
				glBindVertexArray(VAOT);
				for(PhysicalResource resourceT : resources.get(vaoS)) {
					if (resourceT.getHealth() <= 0) {
						resources.get(vaoS).remove(resourceT);
					}
					if (resourceT.distanceToXZ(cam) < 400.0f) {
						float[] modelMat = resourceT.getModelMatFlat();
						modelMatLoc = glGetUniformLocation(shaderProgram,"model");
						glUniformMatrix4fv(modelMatLoc, false, modelMat);
						glDrawArrays(GL_TRIANGLES,0,numVertT);
					}
				}
			} else {
				int VAOHQ = vaoS.getVAOQualityStorage().getVAOHQ().getVAO();
				int numVertHQ = vaoS.getVAOQualityStorage().getVAOHQ().getNumVert();
				int VAOMQ = vaoS.getVAOQualityStorage().getVAOMQ().getVAO();
				int numVertMQ = vaoS.getVAOQualityStorage().getVAOMQ().getNumVert();
				int VAOLQ = vaoS.getVAOQualityStorage().getVAOLQ().getVAO();
				int numVertLQ = vaoS.getVAOQualityStorage().getVAOLQ().getNumVert();
				glBindVertexArray(VAOHQ);
				for(PhysicalResource resourceT : resources.get(vaoS)) {
					if (resourceT.getHealth() <= 0) {
						resources.get(vaoS).remove(resourceT);
					}
					if (resourceT.distanceToXZ(cam) < 100.0f) {
						float[] modelMat = resourceT.getModelMatFlat();
						modelMatLoc = glGetUniformLocation(shaderProgram,"model");
						glUniformMatrix4fv(modelMatLoc, false, modelMat);
						glDrawArrays(GL_TRIANGLES,0,numVertHQ);
					}
				}
				glBindVertexArray(VAOMQ);
				for(PhysicalResource resourceT : resources.get(vaoS)) {
					
					float dist = resourceT.distanceToXZ(cam);
					if (dist >= 100.0f && dist < 300.0f) {
						float[] modelMat = resourceT.getModelMatFlat();
						modelMatLoc = glGetUniformLocation(shaderProgram,"model");
						glUniformMatrix4fv(modelMatLoc, false, modelMat);
						glDrawArrays(GL_TRIANGLES,0,numVertMQ);
					}
				}
				glBindVertexArray(VAOLQ);
				for(PhysicalResource resourceT : resources.get(vaoS)) {
					float dist = resourceT.distanceToXZ(cam);
					if (dist >= 300.0f && dist < 500.0f) {
						float[] modelMat = resourceT.getModelMatFlat();
						modelMatLoc = glGetUniformLocation(shaderProgram,"model");
						glUniformMatrix4fv(modelMatLoc, false, modelMat);
						glDrawArrays(GL_TRIANGLES,0,numVertLQ);
					}
				}
			}
		}	
	}
	
	public void updateTransformMatrix() {
		Projection project = manager.getProjection();
		Camera cam = manager.getCamera();
		glUseProgram(shaderProgram);
		float[] matCom = combineMats(project.getProjMatFMat(),cam.getCamMat());
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
	
	public void addResource(PhysicalResource pr) {
		VAOStorage prVAO = pr.getVAOStorage();
		if (resources.containsKey(prVAO)) {
			resources.get(prVAO).add(pr);
		} else {
			ArrayList<PhysicalResource> tempArrL = new ArrayList<PhysicalResource>();
			tempArrL.add(pr);
			resources.put(prVAO,tempArrL);
		}
	}
	
	public void leftClick() {
		float[] minePoint = manager.getCamera().getPointInFront(1.0f);
		for(VAOStorage vaoS : resources.keySet()) {
			for(PhysicalResource resourceT : resources.get(vaoS)) {
				if (resourceT.getBounds().containsPoint(minePoint)) {
					System.out.println("Hit a " + resourceT.getType());
					//resourceT.modifyHealth(-20);
				}
			}
		}
		
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
