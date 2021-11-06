package Game.Graphics;

import Game.GameData.World;

public class WorldUpdateThread implements Runnable {

	Graphics g;
	World w;
	int[] playerLoc;
	
	public WorldUpdateThread(Graphics g, World w, int[] playerLoc) {
		this.g = g;
		this.w = w;
		this.playerLoc = playerLoc;
	}
	
	public void run() {
		w.updateWorld(-playerLoc[0], -playerLoc[1]);
		g.setWorldUpdateReady(w.vertices);
		//g.updateData(w.vertices, w.indices);
	}
}
