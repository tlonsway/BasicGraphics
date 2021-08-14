package testing;

public class Timer implements Runnable{
	boolean x;
	public Timer() {
		x = true;
	}
	public void run(){
		while(true) {
			x = !x;
			try {
				Thread.sleep(2000);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}
