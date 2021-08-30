package game;

public class OtherPlayer {
	float[] pos;
	float[] rot;
	float[] vel;
	float[] acc;
	int ID;
	private String userName;
	public OtherPlayer(String userName, int ID) {
		pos = new float[3];
		rot = new float[3];
		this.userName = userName;
		this.ID = ID;
	}
	
	public OtherPlayer(String userName, int ID, float[] playerPos, float[] playerRot) {
		this.pos = playerPos;
		this.rot = playerRot;
		this.userName = userName;
		this.ID = ID;
	}
	public void setPosition(float[] p) {
		pos = p;
	}
	public void updateData() {
		
	}	
	public float[] getLocation() {
		return pos;
	}
	public void setID(int id) {
		ID = id;
	}
	public void updatePosition() {
		for(int i=0;i<3;i++) {
			vel[i] += acc[i];
			pos[i] += vel[i];
		}
	}
	public String getName() {
		return userName;
	}
	
}
