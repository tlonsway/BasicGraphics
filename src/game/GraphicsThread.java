package game;

public class GraphicsThread implements Runnable {
	Graphics g;
	
	public GraphicsThread() {
	}
	
	public Graphics getGraphics() {
		return g;
	}
	
	public void run() {
		g = new Graphics();
	}
}
