package Game.Graphics;

public class UIManager {

	int[] screenDims;
	
	int currentHotbarSlot = 0; //index 0->(numHotbarSlots-1)
	
	static final float crossHairDim = 0.03f;
	static final float hotBarPos = -0.75f;
	static final float hotBarHeight = 0.12f;
	static final float hotBarWidth = 0.5f;
	static final float[] hotbarColor = new float[] {0.0f,0.0f,0.0f,1.0f};
	static final int numHotbarSlots = 10;
	boolean generated;
	float[] vertices;
	
	
	public UIManager(int[] screenDims) {
		this.screenDims = screenDims;
		this.vertices = new float[0];
		regenerate();
	}
	
	public void setHotbarSlot(int n) {
		currentHotbarSlot = n;
		regenerate();
	}
	
	public void regenerate() {
		float[] vertTemp = new float[0];
		//create the vertices for the crosshair
		float crosshairHoriz = crossHairDim*((float)screenDims[1]/(float)screenDims[0]);
		float[] crosshair = new float[] {-crosshairHoriz,0f, 0.0f,0.0f,0.0f,crosshairHoriz,0f, 0.0f,0.0f,0.0f,
										 0f,-crossHairDim, 0f, 0.0f,0.0f,0.0f, crossHairDim, 0.0f,0.0f,0.0f};
		vertTemp = combineVertArr(vertTemp,crosshair);
		//create the vertices for the hotbar
		float[] hbc = hotbarColor;
		float[] hotbar = new float[] {-hotBarWidth, hotBarPos-hotBarHeight, hbc[0],hbc[1],hbc[2],hotBarWidth, hotBarPos-hotBarHeight,hbc[0],hbc[1],hbc[2],
									  -hotBarWidth, hotBarPos, hbc[0],hbc[1],hbc[2],hotBarWidth, hotBarPos,hbc[0],hbc[1],hbc[2]};
		
		float[] hotBarVertLines = new float[(numHotbarSlots+1)*10];
		int incT = 0;
		float distBetweenLines = (float)hotBarWidth/((float)numHotbarSlots/2.0f);
		for(int i=0;i<=numHotbarSlots;i++) {
			float tHorzPos = -hotBarWidth+(distBetweenLines*i);
			hotBarVertLines[incT] = tHorzPos;
			hotBarVertLines[incT+1] = hotBarPos-hotBarHeight;
			hotBarVertLines[incT+2] = hbc[0];
			hotBarVertLines[incT+3] = hbc[1];
			hotBarVertLines[incT+4] = hbc[2];
			hotBarVertLines[incT+5] = tHorzPos;
			hotBarVertLines[incT+6] = hotBarPos;
			hotBarVertLines[incT+7] = hbc[0];
			hotBarVertLines[incT+8] = hbc[1];
			hotBarVertLines[incT+9] = hbc[2];
			incT+=10;
		}
		hotbar = combineVertArr(hotbar,hotBarVertLines);
		vertTemp = combineVertArr(vertTemp,hotbar);
		
		this.vertices = vertTemp;
		generated = true;
	}
	
	
	public float[] getUIVertices() {
		return this.vertices;
	}
	
	private float[] combineVertArr(float[] arr1, float[] arr2) {
		float[] ret = new float[arr1.length + arr2.length];
		int inc = 0;
		for(int i=0;i<arr1.length;i++) {
			ret[inc] = arr1[i];
			inc++;
		}
		for(int i=0;i<arr2.length;i++) {
			ret[inc] = arr2[i];
			inc++;
		}
		return ret;
	}
	
}
