package engine;

public class CameraUpdateThread implements Runnable{
	Scene scene;
	final float MOVEMENTSPEED = 0.04f;
	public CameraUpdateThread(Scene scene) {
		this.scene = scene;
	}
	public void run() {
		try {
			while(true) {
				Thread.sleep(1000/60);
				float x, y, z; x=0f; y=0f; z=0f;
				float shift = 1.0f;
				if(scene.getCam().translationState[6]) {
					shift = 3.0f;
				}
				if(scene.getCam().translationState[0]) {
					x += MOVEMENTSPEED*shift;
				}
				if(scene.getCam().translationState[1]) {
					x -= MOVEMENTSPEED*shift;
				}
				if(scene.getCam().translationState[2]) {
					z -= MOVEMENTSPEED*shift;
				}
				if(scene.getCam().translationState[3]) {
					z += MOVEMENTSPEED*shift;
				}
				if(scene.getCam().translationState[4]) {
					y += MOVEMENTSPEED*shift;
				}
				if(scene.getCam().translationState[5]) {
					y -= MOVEMENTSPEED*shift;
				}
				scene.getCam().translate(x, y, z);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
