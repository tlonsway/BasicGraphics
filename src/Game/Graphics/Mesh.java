package Game.Graphics;
import java.util.*;


public class Mesh {
	public float[] verts;
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
			this.polygons.get(0).setFColor(new float[] {1, 0, 0});
			this.polygons.get(1).setFColor(new float[] {1, 0, 0});
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
	public void generateVertices() {
		ArrayList<Float> list = new ArrayList<Float>();
		float[] listArr = new float[polygons.size()*27];
		int polyInc = 0;
		int listArrInc = 0;
		float[] lastPCol = new float[3];
		for(Polygon p: polygons) {
			for(int i = 0; i < p.getPoints().length; i++) {
				listArr[listArrInc] = p.getPoints()[i].get(0);
				listArr[listArrInc+1] = p.getPoints()[i].get(1);
				listArr[listArrInc+2] = p.getPoints()[i].get(2);
				float[] pCol = p.fColor[i];
				listArr[listArrInc+3] = pCol[0];
				listArr[listArrInc+4] = pCol[1];
				listArr[listArrInc+5] = pCol[2];
				float[] pNorm = p.getNorm().toArray();
				listArr[listArrInc+6] = pNorm[0];
				listArr[listArrInc+7] = pNorm[1];
				listArr[listArrInc+8] = pNorm[2];
				listArrInc+=9;
			}
			polyInc++;
		}
		verts = listArr;
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
	public Mesh clone() {
		Mesh mesh = new Mesh(true);
		for(Polygon p: polygons) {
			mesh.addToMesh(p.clone());
		}
		return mesh;
	}
}
