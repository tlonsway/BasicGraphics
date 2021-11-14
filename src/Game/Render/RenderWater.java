package Game.Render;


import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import Game.GameData.*;
import Game.Graphics.*;
import Game.Network.*;
import Game.Init.Setup;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glDrawArrays;
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
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.*;
import java.util.*;
import org.jblas.*;

public class RenderWater {

	GameManager manager;
	
	int VAOHQ; //high quality
	int VAOLQ; //low quality
	int numVertHQ;
	int numVertLQ;
	int shaderProgram;
	
	static final float[] iMatFlat = new float[] {1,0,0,0,0,1,0,0,0,0,1,0,0,0,0,1};
	
	public RenderWater(GameManager manager) {
		this.manager = manager;
		Shader waterVertShader = new Shader("Shaders/basicProjWithModel.vtxs",GL_VERTEX_SHADER);
		Shader waterFragShader = new Shader("Shaders/singleColor.frgs",GL_FRAGMENT_SHADER);
		shaderProgram = glCreateProgram();
		glAttachShader(shaderProgram,waterVertShader.getShader());
		glAttachShader(shaderProgram,waterFragShader.getShader());
		glLinkProgram(shaderProgram);
		glDeleteShader(waterVertShader.getShader());
		glDeleteShader(waterFragShader.getShader());
		numVertLQ = 0;
		updateVertsLQ();
		updateVertsHQ();
		updateUniforms();
	}
	
	public void render() {
		if (numVertLQ == 0 || numVertHQ == 0) {
			updateVertsLQ();
			updateVertsHQ();
		}
		glUseProgram(shaderProgram);
		glBindVertexArray(VAOHQ);
		int modelMatLocT = glGetUniformLocation(shaderProgram,"model");
		glUniformMatrix4fv(modelMatLocT, false, iMatFlat);
		glDrawArrays(GL_TRIANGLES,0,numVertHQ);
		glBindVertexArray(VAOLQ);
		for(int x=-2;x<=2;x++) {
			for(int z=-2;z<=2;z++) {
				if (x==0 && z==0) {
					continue;
				}
				GameObject go = new GameObject("temp");
				go.translate(2000f*x, 0f, 2000f*z);
				float[] goMat = go.getModelMatFlat();
				modelMatLocT = glGetUniformLocation(shaderProgram,"model");
				glUniformMatrix4fv(modelMatLocT, false, goMat);
				glDrawArrays(GL_TRIANGLES,0,numVertLQ);
			}
		}
		
	}
	
	public void updateUniforms() {
		glUseProgram(shaderProgram);
		Projection project = manager.getProjection();
		Camera cam = manager.getCamera(); 
		float[] matCom = combineMats(project.getProjMatFMat(),cam.getCamMat());
		int fullMatLoc = glGetUniformLocation(shaderProgram,"fullMat");
		glUniformMatrix4fv(fullMatLoc, false, matCom);	
		
		
	}
	
	
	private void updateVertsHQ() {
		if (manager.getWorld() != null) {
			Mesh waterMesh = manager.getWorld().getWater(0.1f);
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
			VAOHQ = VAOt;
			numVertHQ = vertices.length;	
		}
	}
	private void updateVertsLQ() {
		if (manager.getWorld() != null) {
			Mesh waterMesh = manager.getWorld().getWater(0.05f);
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
			VAOLQ = VAOt;
			numVertLQ = vertices.length;	
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