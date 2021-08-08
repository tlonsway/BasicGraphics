package engine;
import java.util.*;
public class World {
	Mesh terrain;
	ArrayList<Mesh> objects;
	public World() {
		int seed = 10000;
		terrain = generateWorld(seed);
		objects = new ArrayList<Mesh>();
	}
	public Mesh generateWorld(int seed) {
		Noise noise = new Noise();
		Mesh map = new Mesh(null);
		float[][] grid = new float[101][101];
		for(int x = 0; x < grid.length; x++) {
			for(int y = 0; y < grid[0].length; y++) {
				grid[x][y] = (float)(noise.noise(x/30.0+seed, y/30.0+seed)*10);
			}
			System.out.println();
		}
		for(int row = 0; row < grid[0].length-1; row++) {
			float[][] pts = {{0,grid[0][row],row}, {0,grid[0][row+1], row+1}, {1, grid[1][row], row}};
			Polygon poly = new Polygon(pts[0], pts[1], pts[2]);
			poly.setColor(new int[] {0,(int)(255*((pts[0][1]+10)/20.0)),0});
			map.addToMesh(poly);
			boolean up = false;
			int x = 1;
			while(x < grid.length) {
				up = !up;
				float[] p = new float[3];
				if(up) {
					p[0] = x;
					p[1] = grid[x][row+1];
					p[2] = row+1;
				}
				else {
					p[0] = x;
					p[1] = grid[x][row];
					p[2] = row;
				}
				pts = shiftPoint(pts, p);
				poly = new Polygon(pts[0], pts[1], pts[2]);
				poly.setColor(new int[] {0,(int)(255*((pts[0][1]+10)/20.0)),0});
				map.addToMesh(poly);
				if(up) {
					x++;
				}
			}
		}
		return map;
	}
	private float[][] shiftPoint(float[][] pts, float[] p){
		pts[0] = pts[1];
		pts[1] = pts[2];
		pts[2] = p;
		return pts;
	}
	public ArrayList<Polygon> getPolygons() {
		return terrain.getPolygons();
	}
}
