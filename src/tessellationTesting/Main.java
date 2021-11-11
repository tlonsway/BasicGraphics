package tessellationTesting;
import engine.*;
public class Main {
	public static void main(String[] args) {
		int[] dims = new int[] {1920, 1080};
		TessGraphics g = new TessGraphics(dims);
		g.loop();
	}
}
