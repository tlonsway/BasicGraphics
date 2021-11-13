package Game.sound;
import Game.Graphics.Camera;
import Game.Graphics.Operations;

import static org.lwjgl.openal.AL10.*;
import org.jblas.*;
public class SoundListener {
	Camera cam;
	public SoundListener(Camera cam) {
		this.cam = cam;
		update();
	}
	public void update() {
		setPosition();
		setOrientation();
		setVelocity(); 
	}
	public void setPosition() {
		float[] loc = cam.getCamPos();
		alListener3f(AL_POSITION, -loc[0], -loc[1], -loc[2]);
	}
	public void setOrientation() {
		float[] rots = cam.getRotations();
		FloatMatrix dir = new FloatMatrix(new float[] {0f, 0f, -1f, 1f});
		FloatMatrix result = Operations.rotatePoint(dir, 'x', -rots[0]);
		result = Operations.rotatePoint(result, 'y', -rots[1]);
		result = Operations.rotatePoint(result, 'z', -rots[2]);
		float sum = 0;
		for(int i=0;i<3;i++) {
			float f = result.get(i);
			sum += f*f;
		}
		float mag = (float)Math.sqrt(sum);
		result = result.divi(mag);
		float[] tmp = result.data;
		float[] direction = new float[] {tmp[0], tmp[1], tmp[2], 0, 1, 0};
		alListenerfv(AL_ORIENTATION, direction);
	}
	public void setVelocity() {
		float[] vel = cam.getVelocity();
		alListener3f(AL_VELOCITY, vel[0], vel[1], vel[2]);
	}
}
