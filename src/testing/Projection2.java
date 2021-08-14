package testing;

public class Projection2 {

	public static float[] getProjectionMatSimplified(float near, float right, float top, float far) {
		/*float[] ret = new float[] {(near/right),0,0,0,
									0,(near/top),0,0,
									0,0,-(far+near)/(far-near),-(2*far*near)/(far-near),
									0,0,-1,0};*/
		float[] ret = new float[] {1f,0f,0f,0f,
				0f,1f,0f,0f,
				0f,0f,-(far)/(far-near),-1f,
				0f,0f,-(far*near)/(far-near),0f};
		return ret;
	}
}
