package Game.GameContent;

import Game.Graphics.*;
import org.jblas.*;

public abstract class PhysicalResource {
	
	VAOStorage vao;
	String type;
	AABB bounds;
	FloatMatrix modelMat;
	float[] flatModelMat;
	int health = 0;
	
	public PhysicalResource(VAOStorage vao, AABB bounds, FloatMatrix modelMat) {
		this.vao = vao;
		this.bounds = bounds;
		this.setModelMat(modelMat);
	}
	
	public VAOStorage getVAOStorage() {
		return vao;
	}
	
	public FloatMatrix getModelMat() {
		return modelMat;
	}
	
	public float[] getModelMatFlat() {
		return flatModelMat;
	}
	
	public void setModelMat(FloatMatrix newMat) {
		modelMat = newMat;
		float[] ret = new float[16];
		int t = 0;
		for(int r=0;r<4;r++) {
			for(int c=0;c<4;c++) {
				ret[t] = modelMat.get(c,r);
				t++;
			}
		}
		flatModelMat = ret;
	}
	
}
