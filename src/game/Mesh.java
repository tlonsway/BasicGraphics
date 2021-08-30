package game;
import java.util.*;


public class Mesh {
	
	private ArrayList<Polygon> polygons;
	public Mesh(ArrayList<Polygon> polygons) {
		if(polygons == null) {
			this.polygons = new ArrayList<Polygon>();
		}
		else {
			this.polygons = polygons;
		}
	}
	public Mesh(boolean blank) {
		this.polygons = new ArrayList<Polygon>();
		if(!blank) {
			this.polygons.add(new Polygon(new float[] {-1, 1, 0}, new float[] {1, 1, 0},new float[] {-1, -1, 0}));
			this.polygons.add(new Polygon(new float[] {-1, -1, 0}, new float[] {1, 1, 0},new float[] {1, -1, 0}));
		}
	}
	public void addToMesh(Polygon p) {
		polygons.add(p);
	}
	public void addMesh(Mesh m) {
		for(Polygon p: m.getPolygons()) {
			polygons.add(p);
		}
	}
	public ArrayList<Polygon> getPolygons(){
		return polygons;
	}
	public void translate(float x, float y, float z) {
		for(Polygon p: polygons) {
			p.translate(x, y, z);
		}
	}
	public void rotate(float[] rotationPoint, char axis, float angle) {
		for(Polygon p: polygons) {
			p.rotate(rotationPoint, axis, angle);
		}
	}
}
