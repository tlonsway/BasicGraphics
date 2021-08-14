package testing;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class MouseThread implements MouseListener {

    @Override
    public void mouseClicked(MouseEvent arg0) {
		int x = (int)MouseInfo.getPointerInfo().getLocation().getX();
        int y = (int)MouseInfo.getPointerInfo().getLocation().getY();
        System.out.print("(R value * 2 - 256)/256: " + (getPixelValue(x, y).getRed()*2.0-256)/256 + " ");
        System.out.print("(G value * 2 - 256)/256: " + (getPixelValue(x, y).getGreen()*2.0-256)/256 + " ");
        System.out.print("(B value * 2 - 256)/256: " + (getPixelValue(x, y).getBlue()*2.0-256)/256 + " ");
     }

     @Override
     public void mouseEntered(MouseEvent arg0) { }

     @Override
     public void mouseExited(MouseEvent arg0) { }

     @Override
     public void mousePressed(MouseEvent arg0) { }

     @Override
     public void mouseReleased(MouseEvent arg0) { }
     
	public Color getPixelValue(int x, int y) {
		try {
			Robot r = new Robot();
			return r.getPixelColor(x, y);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
