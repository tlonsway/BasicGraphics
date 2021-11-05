package Game.Graphics;

import Game.GameData.World;

public class GraphicsThread implements Runnable{
	private World world;
	public GraphicsThread(World world) {
		this.world = world;
	}
	public void run() {
		System.out.println("Starting Graphic Thread");
		int[] screenDims = new int[] {1920,1080};
		Graphics graphic = new Graphics(screenDims);
		graphic.setWorld(world);
		float[] vert = world.vertices;
		int[] ind = world.indices;
		graphic.updateData(vert, ind);
		graphic.loop();
	}
}
