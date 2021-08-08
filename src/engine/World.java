package engine;
import java.util.*;
public class World {
	Mesh terrain;
	ArrayList<Mesh> objects;
	int width, length, height;
	public World() {
		//int seed = 10000;
		height = 40;
		width = 40;
		length = 40;
		int seed = (int)(Math.random()*100000000);
		//terrain = generateWorld(seed);
		terrain = generateCaves(seed);
		objects = new ArrayList<Mesh>();
		
	}
	public Mesh generateCaves(int seed) {
		Noise noise = new Noise();
		Mesh caves = new Mesh();
		boolean[][][] openCavities = new boolean[width+2][height+2][length+2];
		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				for(int z = 0; z < length; z++) {
					if(noise.noise(x/5.0+seed, y/5.0+seed, z/5.0+seed)>0.5) {
						openCavities[x][y][z] = true;
					}
					else {
						openCavities[x][y][z] = false;
					}
				}
			}
		}
		ArrayList<float[]> pts = new ArrayList<float[]>();
		for(int y = 1; y < height+1; y++) {
			for(int z = 1; z < length+1; z++) {
				for(int x = 1; x < width+1; x++) {
					if(openCavities[x][y][z]) {
						if(!openCavities[x-1][y][z] && !pts.contains(new float[] {x-0.5f, y, z})) {
							pts.add(new float[] {x-0.5f, y, z});
						}
						if(!openCavities[x+1][y][z] && !pts.contains(new float[] {x+0.5f, y, z})) {
							pts.add(new float[] {x+0.5f, y, z});
						}
						if(!openCavities[x][y][z-1] && !pts.contains(new float[] {x, y, z-0.5f})) {
							pts.add(new float[] {x, y, z-0.5f});
						}
						if(!openCavities[x][y][z+1] && !pts.contains(new float[] {x, y, z+0.5f})) {
							pts.add(new float[] {x, y, z+0.5f});
						}
						if(!openCavities[x][y+1][z] && !pts.contains(new float[] {x, y+0.5f, z})) {
							pts.add(new float[] {x, y+0.5f, z});
						}
						if(!openCavities[x][y-1][z] && !pts.contains(new float[] {x, y-0.5f, z})) {
							pts.add(new float[] {x, y-0.5f, z});
						}
					}
				}
			}
		}
		for(float[] p: pts) {
			System.out.println("X: "+p[0]+" Y: "+p[1]+" Z: "+p[2]);
			Polygon poly = new Polygon(new float[] {p[0]+0.1f, p[1]+0.1f, p[2]+0.1f}, new float[] {p[0]-0.1f, p[1]-0.1f, p[2]-0.1f}, new float[] {p[0]-0.1f, p[1]-0.1f, p[2]+0.1f});
			poly.setColor(new int[] {255, 0, 0});
			caves.addToMesh(poly);
		}
		for(float[] p: pts) {
			if(containsPoint(new float[] {p[0]+1, p[1], p[2]}, pts)) {
				//System.out.println("found point to the right");
				if(containsPoint(new float[] {p[0]+0.5f, p[1]+0.5f, p[2]+0.5f}, pts)) {
					Polygon poly = new Polygon(new float[] {p[0], p[1], p[2]}, new float[] {p[0]+1, p[1], p[2]}, new float[] {p[0]+0.5f,p[1]+0.5f, p[2]+0.5f});
					poly.setColor(new int[] {60, 60, 60});
					caves.addToMesh(poly);
					System.out.println("Added a polygon 1");
				}
				if(containsPoint(new float[] {p[0]+0.5f, p[1]+0.5f, p[2]}, pts)) {
					Polygon poly = new Polygon(new float[] {p[0], p[1], p[2]}, new float[] {p[0]+1, p[1], p[2]}, new float[] {p[0]+0.5f,p[1]+0.5f, p[2]});
					poly.setColor(new int[] {60, 60, 60});
					caves.addToMesh(poly);
					System.out.println("Added a polygon 2");
				}
			}
		}
		return caves;
		
	}
	private boolean containsPoint(float[] point, ArrayList<float[]> points) {
		for(float[] p: points) {
			if(p[0] == point[0] && p[1] == point[1] && p[2] == point[2]) {
				return true;
			}
		}
		return false;
	}
	public Mesh generateWorld(int seed) {
		Noise noise = new Noise();
		Mesh map = new Mesh(null);
		float[][] grid = new float[width+1][length+1];
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
