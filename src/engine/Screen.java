package engine;

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;

public class Screen extends JPanel {
	int[] screenDims;
	Scene scene;
	
	public Screen(int[] screenDims) {
		this.screenDims = screenDims;
		this.scene = new Scene(screenDims);
		new Thread(new CameraUpdateThread(scene)).start();
	}
	
	public void redraw() {
		repaint();
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		BufferedImage im = new BufferedImage(screenDims[0],screenDims[1],BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2dT = im.createGraphics();
		scene.render(g2dT);
		g2d.drawImage(im, null, 0, 0);
	}
	
	public Scene getScene() {
		return scene;
	}
}
