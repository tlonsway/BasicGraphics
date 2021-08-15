package engine;
import java.util.*;
import org.jblas.*;
public class World {
	Mesh terrain;
	Noise noise;
	ArrayList<Mesh> objects;
	int width, length, height;
	float[] vertices;
	int[] indices;
	int seed;
	public World() {
		noise = new Noise();
		//int seed = 10000;
		height = 100;
		width = 100;
		length = 100;
		seed = (int)(Math.random()*100000000);
		int xShift = 0; 
		int zShift = 0;
		terrain = new Mesh();
		//terrain = generateChunk(seed, xShift, zShift);
		for(int x = 0; x < 5; x++) {
			for(int z = 0; z < 5; z++) {
				Mesh chunk = generateChunk(seed, x*100, z*100);
				terrain.addMesh(chunk);
			}
		}
		Mesh tree = ObjectGeneration.generateTree(seed);
		System.out.println("Height: "+getHeight(0, 0));
		tree.translate(0, getHeight(0, 0), 0);
		terrain.addMesh(tree);
		//distance between each point in the grid
		float gridUnit = 2f;
		//determines how zoomed in on the perlin noise the cave will be
		double perlinScaler = 25;
		//display the points or not
		boolean pointsVisible = false;
		//terrain = generateCaves(seed, gridUnit, perlinScaler, pointsVisible);
		generateVerticeList();
		indices = new int[10];
		//terrain = new Mesh();
		System.out.println("Generated "+terrain.getPolygons().size()+" Polygons");
		Polygon xaxis = new Polygon(new float[] {-10, 0, -0.5f}, new float[] {-10, 0, 0.5f}, new float[] {10, 0, 0});
		xaxis.setColor(new int[] {255,0,0});
		terrain.addToMesh(xaxis);
		Polygon yaxis = new Polygon(new float[] {-0.5f, -10, 0}, new float[] {0.5f, -10, 0}, new float[] {0, 10, 0});
		yaxis.setColor(new int[] {0,255,0});
		terrain.addToMesh(yaxis);
		Polygon zaxis = new Polygon(new float[] {-0.5f, 0, -10}, new float[] {0.5f, 0, -10}, new float[] {0, 0, 10});
		zaxis.setColor(new int[] {0,0,255});
		terrain.addToMesh(zaxis);
		
		System.out.println("Seed: "+seed);
		objects = new ArrayList<Mesh>();
	}
	
	public float getHeight(float x, float z) {
		float y = (float)(Math.tan((noise.noise(x/250.0+seed, z/250.0+seed)+1)*(1.5/2))*60);
		//float y = (float)((Math.tan(noise.noise(x/200.0, z/200.0)*(Math.PI/2.0))/2+(Math.pow(noise.noise(x/200.0, z/200.0), 2)))*40);
		return y+(float)(noise.noise(x/30.0+seed, z/30.0+seed)*5);	
	}
	
	public void generateVerticeList() {
		ArrayList<Polygon> polys = terrain.getPolygons();
		ArrayList<Float> list = new ArrayList<Float>();
		for(Polygon p: polys) {
			for(int i = 0; i < p.getPoints().length; i++) {
				list.add(p.getPoints()[i].get(0));
				list.add(p.getPoints()[i].get(1));
				list.add(p.getPoints()[i].get(2));
				list.add(p.fColor[0]);
				list.add(p.fColor[1]);
				list.add(p.fColor[2]);
			}
		}
		vertices = new float[list.size()];
		for(int i = 0; i < list.size(); i++) {
			vertices[i] = list.get(i);
		}
	}
	
	private boolean vertexEquals(ArrayList<Float> p1, ArrayList<Float> p2) {
		boolean equals = true;
		for(Float f: p1) {
			if(p2.indexOf(f) > -1 && p2.indexOf(f) < 3) {
				equals = false;
				break;
			}
		}
		if(equals)
			return true;
		else
			return false;
	}
	private boolean containsPoint(ArrayList<ArrayList<Float>> points, ArrayList<Float> point) {
		for(ArrayList<Float> p: points) {
			if(vertexEquals(p, point)) {
				return true;
			}
		}
		return false;
	}
	public Mesh generateCaves(double seed, float gridUnit, double perlinScaler, boolean pointsVisible) {
		Mesh caves = new Mesh(); 	
		//creates a grid of open and closed points
		boolean[][][] openCavities = createOpenCavities(gridUnit, perlinScaler, seed);
		//creates list of 3d points
		ArrayList<GridPoint> pts = createPoints(openCavities, gridUnit);
		//makes points visible
		if(pointsVisible) {
			for(GridPoint p: pts) {
				//System.out.println("X: "+p[0]+" Y: "+p[1]+" Z: "+p[2]);
				Polygon poly = new Polygon(new float[] {p.getLocation()[0]+0.1f, p.getLocation()[1]+0.1f, p.getLocation()[2]+0.1f}, new float[] {p.getLocation()[0]-0.1f, p.getLocation()[1]-0.1f, p.getLocation()[2]-0.1f}, new float[] {p.getLocation()[0]-0.1f, p.getLocation()[1]-0.1f, p.getLocation()[2]+0.1f});
				poly.setColor(new int[] {255, 0, 0});
				caves.addToMesh(poly);
			}
		}
		//creates polygons from points
		for(GridPoint p: pts) {
			ArrayList<Polygon> polys = getPointPolys(pts, p, gridUnit);
			for(Polygon poly: polys) {
				if(!caves.getPolygons().contains(poly)) {
					caves.addToMesh(poly);
				}
			}
		}
		return caves;
	}
	
	private boolean[][][] createOpenCavities(float gridUnit, double perlinScaler, double seed){
		boolean[][][] openCavities = new boolean[(int)(width/gridUnit)+2][(int)(height/gridUnit)+2][(int)(length/gridUnit)+2];
		for(int x = 0; x < (int)(width/gridUnit); x++) {
			for(int y = 0; y < (int)(height/gridUnit); y++) {
				for(int z = 0; z < (int)(length/gridUnit); z++) {
					double value = noise.noise(x/perlinScaler+seed, y/perlinScaler+seed, z/perlinScaler+seed);
					//if(value > 0.5) {
					//if(value<0.5 && value > 0.3) {
					if(value < 0.15 && value > 0.02) {
						openCavities[x][y][z] = true;
					}
					else {
						openCavities[x][y][z] = false;
					}
				}
			}
		}
		return openCavities;
	}
	
	private ArrayList<GridPoint> createPoints(boolean[][][] grid, float gridUnit){
		ArrayList<GridPoint> pts = new ArrayList<GridPoint>();
		for(int x = 1; x < grid.length-1; x++) {
			for(int y = 1; y < grid[x].length-1; y++) {
				for(int z = 1; z < grid[x][y].length-1; z++) {
					if(grid[x][y][z]) {
						if(!grid[x+1][y][z] || !grid[x-1][y][z]) {
							pts.add(new GridPoint(new float[] {x*gridUnit, y*gridUnit, z*gridUnit}));
						}
						else if(!grid[x][y+1][z] || !grid[x][y-1][z]) {
							pts.add(new GridPoint(new float[] {x*gridUnit, y*gridUnit, z*gridUnit}));
						}
						else if(!grid[x][y][z+1] || !grid[x][y][z-1]) {
							pts.add(new GridPoint(new float[] {x*gridUnit, y*gridUnit, z*gridUnit}));
						}
					}
				}
			}
		}
		return pts;
	}
	
	private ArrayList<Polygon> getPointPolys(ArrayList<GridPoint> pts, GridPoint p, float gridUnit){
		ArrayList<Polygon> polys = new ArrayList<Polygon>();
		ArrayList<GridPoint> closest = p.getClosest(pts, gridUnit);
		ArrayList<ArrayList<GridPoint>> neighborClosest = new ArrayList<ArrayList<GridPoint>>();
		for(GridPoint gp: closest) {
			neighborClosest.add(gp.getClosest(pts, gridUnit));
		}
		for(GridPoint gp: closest) {
			for(GridPoint ngp: neighborClosest.get(closest.indexOf(gp))) {
				if(closest.contains(ngp) && !polys.contains(new Polygon(p.getLocation(), gp.getLocation(), ngp.getLocation()))) {
					Polygon poly = new Polygon(p.getLocation(), gp.getLocation(), ngp.getLocation());
					poly.setColor(new int[] {100, 100, 100});
					polys.add(poly);
				}
			}
		}
		return polys;
	}
	
	private boolean containsPoint(GridPoint point, ArrayList<GridPoint> points) {
		for(GridPoint p: points) {
			if(p.equals(point)) {
				return true;
			}
		}
		return false;
	}
	public float getGreenColor(float y){
		return (float)((y+5)/153.0);
	}
	public float[] getRedColor(float y){
		return new float[] {(float)((y+40)/85.0),0,0};
	}
	public Mesh generateChunk(int seed, int xShift, int zShift) {
		Mesh map = new Mesh(null);
		float[][] grid = new float[width+1][length+1];
		for(int x = 0; x < grid.length; x++) {
			for(int y = 0; y < grid[0].length; y++) {
				grid[x][y] = getHeight(x+xShift, y+zShift);
			}
		}
		for(int row = 0; row < grid[0].length-1; row++) {
			float[][] pts = {{0+xShift,grid[0][row],row+zShift}, {0+xShift,grid[0][row+1], row+1+zShift}, {1+xShift, grid[1][row], row+zShift}};
			Polygon poly = new Polygon(pts[0], pts[1], pts[2]);
			poly.setFColor(new float[] {0, (float)(getGreenColor(grid[0][row])), 0} );
			map.addToMesh(poly);
			boolean up = false;
			int x = 1;
			while(x < grid.length) {
				up = !up;
				float[] p = new float[3];
				if(up) {
					p[0] = x+xShift;
					p[1] = grid[x][row+1];
					p[2] = row+1+zShift;
				}
				else {
					p[0] = x+xShift;
					p[1] = grid[x][row];
					p[2] = row+zShift;
				}
				pts = shiftPoint(pts, p);
				poly = new Polygon(pts[0], pts[1], pts[2]);
				poly.setFColor(new float[] {0, (float)(getGreenColor(p[1])), 0} );
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
