package game;

public class ServerObject extends GameObject{
	private boolean hasMoved;
	public ServerObject(String name) {
		super(name);
	}
	public ServerObject(String name, World world) {
		super(name, world);
	}
	public ServerObject(String name, World world, Mesh mesh) {
		super(name, world, mesh);
	}
	public void setHasMoved(boolean move) {
		hasMoved = move;
	}
	public boolean hasMoved() {
		return hasMoved;
	}
}

