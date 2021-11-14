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
		Mesh tree = ObjectGeneration.generateTree(9817239, 5);
		VAOStorage vao = new VAOStorage(tree);
		for(int i=0;i<500;i++) {
			float posX = (float)((Math.random()*2000.0)-1000.0);
			float posZ = (float)((Math.random()*2000.0)-1000.0);
			float posY = manager.getWorld().getHeight(posX, posZ);
			if (posY < 10.0f) {
				i--;
				continue;
			}
			float[] pos = new float[] {posX,posY,posZ};
			float[] rot = new float[3];
			PhysicalResource pr = new Tree(vao, tree, pos, rot);
			this.addResource(pr);
		}
		Mesh tree2 = ObjectGeneration.generateTree(3453452, 5);
		VAOStorage vao2 = new VAOStorage(tree);
		for(int i=0;i<500;i++) {
			float posX = (float)((Math.random()*2000.0)-1000.0);
			float posZ = (float)((Math.random()*2000.0)-1000.0);
			float posY = manager.getWorld().getHeight(posX, posZ);
			if (posY < 10.0f) {
				i--;
				continue;
			}
			float[] pos = new float[] {posX,posY,posZ};
			float[] rot = new float[3];
			PhysicalResource pr = new Tree(vao2, tree2, pos, rot);
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
			int VAOT = vaoS.getVAO();
			int numVertT = vaoS.getNumVert();
			glBindVertexArray(VAOT);
			for(PhysicalResource resourceT : resources.get(vaoS)) {
				float[] modelMat = resourceT.getModelMatFlat();
				modelMatLoc = glGetUniformLocation(shaderProgram,"model");
				glUniformMatrix4fv(modelMatLoc, false, modelMat);
				glDrawArrays(GL_TRIANGLES,0,numVertT);
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
