package game;

public class MouseManager {

	double lastX, lastY;
	float[] pendingRotate;
	
	public MouseManager() {
		lastX = lastY = 0;
		pendingRotate = new float[2];
	}

	public void mouseMovement(double xpos, double ypos) {
		//System.out.println("XPOS: " + xpos + " YPOS: " + ypos);
		//System.out.println("XPOSdiff: " + (lastX-xpos) + " YPOSdiff: " + (lastY-ypos));
		pendingRotate[0] -= (Math.PI*(lastX-xpos))/(4*180);
		pendingRotate[1] -= (Math.PI*(lastY-ypos))/(4*180);
		lastX = xpos; lastY = ypos;
	}
	
	public float[] getRotation() {
		float[] tempRet = new float[] {pendingRotate[0],pendingRotate[1]};
		pendingRotate = new float[] {0,0};
		return tempRet;
	}
	
}
