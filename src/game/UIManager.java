package game;

public class UIManager {

	int[] screenDims;
	
	public UIManager(int[] screenDims) {
		this.screenDims = screenDims;
	}
	
	public float[] getUIVertices() {
		float[] crossHairColor = new float[] {0.0f,0.0f,0.0f,1.0f};
		float[] crosshair = new float[] {-0.1f,0f, 0.1f,0f,
										 0f,-0.1f, 0f,0.1f};
		return crosshair;
	}
	
}
