package Game.Render;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
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
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform3fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.util.ArrayList;

import org.jblas.FloatMatrix;

import Game.GameData.*;
import Game.Graphics.Camera;
import Game.Graphics.Mesh;
import Game.Graphics.Polygon;
import Game.Graphics.Projection;
import Game.Graphics.Shader;

public class RenderClouds {
GameManager manager;
	ArrayList<float[]> cloud1Locs;
	ArrayList<float[]> cloud2Locs;
	int VAO1; 
	int VAO2;
	int numVert1;
	int numVert2;
	int shaderProgram;
	
	float wavePos = 0.0f;
	
	static final float[] iMatFlat = new float[] {1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1};
	
	public RenderClouds(GameManager manager) {
		cloud1Locs = new ArrayList<>();
		cloud2Locs = new ArrayList<>();
		generateCloudLocs();
		System.out.println("Type 1: "+cloud1Locs.size());
		System.out.println("Type 2: "+cloud2Locs.size());
		this.manager = manager;
		//Shader waterVertShader = new Shader("Shaders/basicProjWithModel.vtxs",GL_VERTEX_SHADER);
		Shader waterVertShader = new Shader("Shaders/basicProjModelLighting.vtxs",GL_VERTEX_SHADER);
		Shader waterFragShader = new Shader("Shaders/basicLighting.frgs",GL_FRAGMENT_SHADER);
		shaderProgram = glCreateProgram();
		glAttachShader(shaderProgram,waterVertShader.getShader());
		glAttachShader(shaderProgram,waterFragShader.getShader());
		glLinkProgram(shaderProgram);
		glDeleteShader(waterVertShader.getShader());
		glDeleteShader(waterFragShader.getShader());
		numVert1 = 0;
		numVert2 = 0;
		updateVerts();
	}
	
	private void moveClouds(float xShift, float zShift) {
		float[] cpt = manager.getCamera().getCamPos();
		//System.out.println("Moving trees 1");
		for(float[] loc: cloud1Locs) {
			loc[0] += xShift;
			loc[2] += zShift;
			if(Math.sqrt(Math.pow(-cpt[0]-loc[0], 2)+Math.pow(-cpt[2]-loc[2], 2)) > 2000) {
				addNewCloud(cloud1Locs, loc);
			}
		}
		//System.out.println("Moving trees 2");
		for(float[] loc: cloud2Locs) {
			loc[0] += xShift;
			loc[2] += zShift;
			if(Math.sqrt(Math.pow(-cpt[0]-loc[0], 2)+Math.pow(-cpt[2]-loc[2], 2)) > 2000) {
				addNewCloud(cloud2Locs, loc);
			}
		}
		//System.out.println("Finished moving trees");
	}
	
	public void generateCloudLocs() {
		int num1 = (int)(Math.random()*20+10);
		int num2 = (int)(Math.random()*20+10);
		for(int i = 0; i < num1; i++) {
			cloud1Locs.add(new float[] {(float)(Math.random()*4000-2000), (float)(Math.random()*60+250), (float)(Math.random()*4000-2000)});
			float[] pos = cloud1Locs.get(cloud1Locs.size()-1);
			System.out.println(pos[0]+" "+pos[1]+" "+pos[2]);
		}
		System.out.println("-----------------------");
		for(int i = 0; i < num2; i++) {
			cloud2Locs.add(new float[] {(float)(Math.random()*4000-2000), (float)(Math.random()*60+250), (float)(Math.random()*4000-2000)});
			float[] pos = cloud2Locs.get(cloud2Locs.size()-1);
			System.out.println(pos[0]+" "+pos[1]+" "+pos[2]);
		}
		System.out.println("Finished generating clouds");
	}
	
	public void addNewCloud(ArrayList<float[]> locs, float[] loc) {
		float[] camPos = manager.getCamera().getCamPos();
		loc = new float[] {-2000-camPos[0], (float)(Math.random()*60+250), (float)(Math.random()*4000-2000)-camPos[2]};
	}
	
	public void render() {
		glUseProgram(shaderProgram);
		//update uniform variables for lighting
		float[] cpt = manager.getCamera().getCamPos();
		int viewPosLoc = glGetUniformLocation(shaderProgram, "viewPos");
		glUniform3fv(viewPosLoc,new float[] {-cpt[0],-cpt[1],-cpt[2]});		
		int modelMatLoc = glGetUniformLocation(shaderProgram,"model");
		glUniformMatrix4fv(modelMatLoc, false, iMatFlat);
		int modelInvTranMatLoc = glGetUniformLocation(shaderProgram, "invTranMod");
		glUniformMatrix4fv(modelInvTranMatLoc,false,iMatFlat);
		glBindVertexArray(VAO1);
		System.out.println("-------------------------");
		for(float[] loc: cloud1Locs) {
			GameObject go = new GameObject("temp");
			go.translate(loc[0], loc[1], loc[2]);
			System.out.println("X: "+loc[0]+" Y: "+loc[1]+" Z: "+loc[2]);
			float[] goMat = go.getModelMatFlat();
			int modelMatLocT = glGetUniformLocation(shaderProgram,"model");
			glUniformMatrix4fv(modelMatLocT, false, goMat);
			glDrawArrays(GL_TRIANGLES,0,numVert1);
		}
		glBindVertexArray(VAO2);
		for(float[] loc: cloud2Locs) {
			GameObject go = new GameObject("temp");
			go.translate(loc[0], loc[1], loc[2]);
			float[] goMat = go.getModelMatFlat();
			int modelMatLocT = glGetUniformLocation(shaderProgram,"model");
			glUniformMatrix4fv(modelMatLocT, false, goMat);
			glDrawArrays(GL_TRIANGLES,0,numVert2);
		}
		moveClouds(0.2f, 0);
	}
	
	public void updateWavePos() {
		glUseProgram(shaderProgram);
		int wavePosLoc = glGetUniformLocation(shaderProgram,"wavePos");
		glUniform1f(wavePosLoc, wavePos);
		wavePos-=0.1f;
	}
	
	private void updateVerts() {
		if (manager.getWorld() != null) {
			int seed = manager.getWorld().seed;
			Mesh cloud1 = ObjectGeneration.generateCloud(seed, 5);
			Mesh cloud2 = ObjectGeneration.generateCloud(seed>>2, 5);
			
			float[] cloud1Verts = getVertices(cloud1);
			float[] cloud2Verts = getVertices(cloud2);
			
			int VBOt1,VAOt1, VBOt2, VAOt2;
			
			//cloud type 1
			VBOt1 = glGenBuffers();
			VAOt1 = glGenVertexArrays();
			glBindVertexArray(VAOt1);
			glBindBuffer(GL_ARRAY_BUFFER,VBOt1);
			glBufferData(GL_ARRAY_BUFFER, cloud1Verts, GL_DYNAMIC_DRAW);
			glVertexAttribPointer(0,3,GL_FLOAT,false,36,0l);
			glEnableVertexAttribArray(0);
			glVertexAttribPointer(1,3,GL_FLOAT,false,36,12l);
			glEnableVertexAttribArray(1);
			glVertexAttribPointer(2,3,GL_FLOAT,false,36,24l);
			glEnableVertexAttribArray(2);
			glBindBuffer(GL_ARRAY_BUFFER,0);
			glBindVertexArray(0);
			VAO1 = VAOt1;
			numVert1 = cloud1Verts.length;	
			
			//cloud type 2
			VBOt2 = glGenBuffers();
			VAOt2 = glGenVertexArrays();
			glBindVertexArray(VAOt2);
			glBindBuffer(GL_ARRAY_BUFFER,VBOt2);
			glBufferData(GL_ARRAY_BUFFER, cloud1Verts, GL_DYNAMIC_DRAW);
			glVertexAttribPointer(0,3,GL_FLOAT,false,36,0l);
			glEnableVertexAttribArray(0);
			glVertexAttribPointer(1,3,GL_FLOAT,false,36,12l);
			glEnableVertexAttribArray(1);
			glVertexAttribPointer(2,3,GL_FLOAT,false,36,24l);
			glEnableVertexAttribArray(2);
			glBindBuffer(GL_ARRAY_BUFFER,0);
			glBindVertexArray(0);
			VAO2 = VAOt2;
			numVert2 = cloud2Verts.length;	
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
	
	private float[] getVertices(Mesh m) {
		ArrayList<Polygon> polys = m.getPolygons();
		float[] vertices = new float[polys.size()*27];
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
				float[] normVec = p.getNorm().toArray();
				vertices[vertIn+3] = normVec[0];
				vertices[vertIn+4] = normVec[1];
				vertices[vertIn+5] = normVec[2];
				vertIn+=6;
			}
		}
		return vertices;
	}
}
