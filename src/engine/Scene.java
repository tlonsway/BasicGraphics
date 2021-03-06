package engine;

import java.util.*;
import org.jblas.*;
import java.awt.*;

public class Scene {
	private ArrayList<Polygon> polygons;
	private Camera camera;
	
	public Scene(int[] screenDims) {
		polygons = new ArrayList<Polygon>();
		camera = new Camera(screenDims);
	}
	
	public void render(Graphics2D g2) {
		
		RenderingHints rh = new RenderingHints(
	             RenderingHints.KEY_COLOR_RENDERING,
	             RenderingHints.VALUE_COLOR_RENDER_SPEED);
	    g2.setRenderingHints(rh);
		
		
		FloatMatrix camMatTemp = camera.getCamMat();
		//camera.getCamPos();
		//Operations.printMat(camMatTemp);
		//ArrayList<FloatMatrix>
		//FloatMatrix[][] renderedPolys = new FloatMatrix[polygons.size()][2];
		
		
		ArrayList<Polygon> renderedPolys = new ArrayList<Polygon>();
		for(Polygon poly : polygons) {
			poly.getRendered(camera,camMatTemp);
			//renderedPolys.add(poly);
		}
		
		Collections.sort(polygons);
		
		for(Polygon poly : polygons) {
			//FloatMatrix[] polyRen = poly.getRendered(camera,camMatTemp);
			FloatMatrix[] polyRen = poly.getRenderedPoints();
			if (polyRen != null) {
				boolean onscreen = false;
				if (polyRen[0].get(0) > 0 && polyRen[0].get(0) < camera.screenDims[0] && polyRen[0].get(1) > 0 && polyRen[0].get(1) < camera.screenDims[1]) {
					onscreen = true;
				} else
				if (polyRen[1].get(0) > 0 && polyRen[1].get(0) < camera.screenDims[0] && polyRen[1].get(1) > 0 && polyRen[1].get(1) < camera.screenDims[1]) {
					onscreen = true;
				} else
				if (polyRen[2].get(0) > 0 && polyRen[2].get(0) < camera.screenDims[0] && polyRen[2].get(1) > 0 && polyRen[2].get(1) < camera.screenDims[1]) {
					onscreen = true;
				}
				
				
				/*if ((polyRen[0].get(0) < 0 || polyRen[1].get(0) < 0 || polyRen[2].get(0) < 0)) {
					onscreen = false;
					//continue;
				}
				if ((polyRen[0].get(0) > camera.screenDims[0] || polyRen[1].get(0) > camera.screenDims[0] || polyRen[2].get(0) > camera.screenDims[0])) {
					onscreen = false;
					//continue;
				}
				if ((polyRen[0].get(1) < 0 || polyRen[1].get(1) < 0 || polyRen[2].get(1) < 0)) {
					onscreen = false;
					//continue;
				}
				if ((polyRen[0].get(1) > camera.screenDims[1] || polyRen[1].get(1) > camera.screenDims[1] || polyRen[2].get(1) > camera.screenDims[1])) {
					onscreen = false;
					//continue;
				}
				*/
				if (!onscreen) {
					continue;
				}
				g2.setColor(Color.BLACK);
				g2.drawLine((int)polyRen[0].get(0), (int)polyRen[0].get(1), (int)polyRen[1].get(0), (int)polyRen[1].get(1));
				g2.drawLine((int)polyRen[1].get(0), (int)polyRen[1].get(1), (int)polyRen[2].get(0), (int)polyRen[2].get(1));
				g2.drawLine((int)polyRen[2].get(0), (int)polyRen[2].get(1), (int)polyRen[0].get(0), (int)polyRen[0].get(1));
				//g2.setColor(poly.getColorAsColor());
				int[] pxP = new int[] {(int)polyRen[0].get(0),(int)polyRen[1].get(0),(int)polyRen[2].get(0)};
				int[] pyP = new int[] {(int)polyRen[0].get(1),(int)polyRen[1].get(1),(int)polyRen[2].get(1)};
				g2.fillPolygon(pxP, pyP, 3);
			} else {
			}
		}
	}
	
	public void addPolygon(Polygon p) {
		polygons.add(p);
	}
	
	public void addCube(FloatMatrix point, FloatMatrix dims, int[] color) {
		Polygon[] cube = Shapes.genCube(point, dims, color);
		for(Polygon p : cube) {
			polygons.add(p);
		}
	}
	
	public Camera getCam() {
		return camera;
	}
}
