package game;

public class OtherPlayer {
	float[] pos;
	float[] rot;
	float[] vel;
	float[] acc;
	int ID;
	
	public OtherPlayer() {
		pos = new float[3];
		rot = new float[3];
	}
	
	public OtherPlayer(float[] playerPos, float[] playerRot) {
		this.pos = playerPos;
		this.rot = playerRot;
	}
	
	public void updateData() {
		
	}	
	
	public void updatePosition() {
		for(int i=0;i<3;i++) {
			vel[i] += acc[i];
			pos[i] += vel[i];
		}
	}
	
}
