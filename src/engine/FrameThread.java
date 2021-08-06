package engine;

public class FrameThread implements Runnable {
	int fps;
	Screen screen;
	
	public FrameThread(Screen screen, int fps) {
		this.screen = screen;	
		this.fps = fps;
	}
	
	public void run() {
		while(true) {
			screen.redraw();
			
			try {
				Thread.sleep((int)(1000.0/(double)fps));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
