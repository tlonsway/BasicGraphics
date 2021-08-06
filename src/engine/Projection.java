package engine;

import org.jblas.FloatMatrix;

public class Projection {
	
	private FloatMatrix projectionMat;
	
	public Projection() {
		float fov,aspect,near,far;
		fov = 120; aspect = 1; near = 0; far = 1000;
		float[][] tProjMat = new float[][] {{(float)Math.atan((fov/2)),0,0,0},
								            {0,(float)Math.atan((fov)/2),0,0},
								            {0,0,-((far+near)/(far-near)),-((2*(far*near))/(far-near))},
								            {0,0,-1,0}};
		projectionMat = new FloatMatrix(tProjMat);
	}
								            
	public Projection(float fov, float aspect, float near, float far) {
        float[][] tProjMat = new float[][] {{(float)Math.atan((fov/2)),0,0,0},
        									{0,(float)Math.atan((fov)/2),0,0},
								            {0,0,-((far+near)/(far-near)),-((2*(far*near))/(far-near))},
								            {0,0,-1,0}};
		projectionMat = new FloatMatrix(tProjMat);
	}
	
	
	
	public FloatMatrix project(FloatMatrix vec) {
		FloatMatrix vec2 = new FloatMatrix(new float[] {vec.get(0),vec.get(1),vec.get(2),1});
		FloatMatrix res1 = projectionMat.mmul(vec2);
        float x = -1*(res1.get(0)/res1.get(3));
        float y = -1*(res1.get(1)/res1.get(3));
        x = (x+1)/2;
        y = (y+1)/2;
		return new FloatMatrix(new float[] {x,y});
	}
}