package game;

public class UIManager {

	int[] screenDims;
	
	static final float crossHairDim = 0.03f;
	
	public UIManager(int[] screenDims) {
		this.screenDims = screenDims;
	}
	
	public float[] getUIVertices() {
		float[] crossHairColor = new float[] {0.0f,0.0f,0.0f,1.0f};
		
		float crosshairHoriz = crossHairDim*((float)screenDims[1]/(float)screenDims[0]);
		float[] crosshair = new float[] {-crosshairHoriz,0f, crosshairHoriz,0f,
										 0f,-crossHairDim, 0f,crossHairDim};
		return crosshair;
	}
	
}
