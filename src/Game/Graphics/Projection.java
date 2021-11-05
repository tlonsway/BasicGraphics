package Game.Graphics;

import org.jblas.FloatMatrix;

public class Projection {
	
	private FloatMatrix projectionMat;
	
	public Projection() {
		float fov,aspect,near,far;
		fov = 45; aspect = 1; near = 0.1f; far = 10f;
		float[][] tProjMat = new float[][] {{(float)Math.atan((fov/2)),0,0,0},
								            {0,(float)Math.atan((fov)/2),0,0},
								            {0,0,-((far+near)/(far-near)),-((2*(far*near))/(far-near))},
								            {0,0,-1,0}};
		projectionMat = new FloatMatrix(tProjMat);
	}
	
	public Projection(float fov, float near, float far, int screenDims[]) {
		float aspect = (float)screenDims[0]/(float)screenDims[1];
		float top = (float)Math.tan(((Math.PI/180)*fov)/2)*near;
		float bottom = -top;
		float right = top*aspect;
		float left = -top*aspect;
		float n,r,l,t,b,f;
		n=near;r=right;l=left;t=top;b=bottom;f=far;
		float[][] tProjMat = new float[][] {{(2*n)/(r-l),0,(r+l)/(r-l),0},
											{0,(2*n)/(t-b),(t+b)/(t-b),0},
											{0,0,-(f+n)/(f-n),-(2*f*n)/(f-n)},
											{0,0,-1,0}};
		projectionMat = new FloatMatrix(tProjMat);
	}
	
	public float[] getProjMat() {
		float[] ret = new float[16];
		int t=0;
		for(int r=0;r<4;r++) {
			for(int c=0;c<4;c++) {
				ret[t] = projectionMat.get(r,c);
				t++;
			}
		}
		return ret;
	}
	
	public FloatMatrix getProjMatFMat() {
		return projectionMat;
	}
}