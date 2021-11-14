package Game.GameContent;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.util.ArrayList;

import org.jblas.FloatMatrix;

import Game.Graphics.*;

public class VAOStorage {
	int VAO;
	int numVert;
	
	boolean vaoQualityEnabled = false;
	VAOQualityStorage vaoQuality;
	
	public VAOStorage(Mesh m) {
		float[] verts = getVertices(m);
		int VBOt,VAOt;
		VBOt = glGenBuffers();
		VAOt = glGenVertexArrays();
		glBindVertexArray(VAOt);
		glBindBuffer(GL_ARRAY_BUFFER,VBOt);
		glBufferData(GL_ARRAY_BUFFER, verts, GL_STREAM_DRAW);
		glVertexAttribPointer(0,3,GL_FLOAT,false,36,0l);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1,3,GL_FLOAT,false,36,12l);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(2,3,GL_FLOAT,false,36,24l);
		glEnableVertexAttribArray(2);
		glBindBuffer(GL_ARRAY_BUFFER,0);
		glBindVertexArray(VAOt);
		VAO=VAOt;
		numVert = verts.length;
	}
	
	public VAOStorage(float[] verts) {
		/* params array should store vertex attributes used. Possible attributes:
		 * position,color,norm
		 */
		int VBOt,VAOt;
		VBOt = glGenBuffers();
		VAOt = glGenVertexArrays();
		glBindVertexArray(VAOt);
		glBindBuffer(GL_ARRAY_BUFFER,VBOt);
		glBufferData(GL_ARRAY_BUFFER, verts, GL_STREAM_DRAW);
		glVertexAttribPointer(0,3,GL_FLOAT,false,36,0l);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1,3,GL_FLOAT,false,36,12l);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(2,3,GL_FLOAT,false,36,24l);
		glEnableVertexAttribArray(2);
		glBindBuffer(GL_ARRAY_BUFFER,0);
		glBindVertexArray(VAOt);
		VAO=VAOt;
		numVert = verts.length;
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
	
	public VAOStorage(int VAO, int numVert) {
		this.VAO = VAO;
		this.numVert = numVert;
	}
	
	public VAOStorage(Mesh mDefault, Mesh mHQ, Mesh mMQ, Mesh mLQ) {
		this(mDefault);
		vaoQuality = new VAOQualityStorage(mHQ, mMQ, mLQ);
		this.vaoQualityEnabled = true;
	}
	
	public int getVAO() {
		return VAO;
	}
	
	public int getNumVert() {
		return numVert;
	}
	
	public boolean isVaoQualityEnabled() {
		return vaoQualityEnabled;
	}
	
	public VAOQualityStorage getVAOQualityStorage() {
		return vaoQuality;
	}
	
	@Override
	public int hashCode() {
		return VAO;
	}
	
}
