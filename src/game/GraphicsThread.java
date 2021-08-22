package game;

public class GraphicsThread implements Runnable{
	private Graphics graphic;
	public GraphicsThread(Graphics graphic) {
		this.graphic = graphic; 
	}
	public void run() {
		graphic.loop();
	}
}
