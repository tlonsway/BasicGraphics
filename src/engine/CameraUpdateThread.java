package engine;

public class CameraUpdateThread implements Runnable{
	Scene scene;
	final float MOVEMENTSPEED = 0.01f;
	public CameraUpdateThread(Scene scene) {
		this.scene = scene;
	}
	public void run() {
		try {
			while(true) {
				Thread.sleep(1000/60);
				float x, y, z; x=0; y=0; z=0;
				if(scene.getCam().translationState[0]) {
					x += MOVEMENTSPEED;
				}
				if(scene.getCam().translationState[1]) {
					x -= MOVEMENTSPEED;
				}
				if(scene.getCam().translationState[2]) {
					z -= MOVEMENTSPEED;
				}
				if(scene.getCam().translationState[3]) {
					z += MOVEMENTSPEED;
				}
				if(scene.getCam().translationState[4]) {
					y += MOVEMENTSPEED;
				}
				if(scene.getCam().translationState[5]) {
					y -= MOVEMENTSPEED;
				}
				scene.getCam().translate(x, y, z);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
