package Game.GameData;
import java.util.*;
import org.jblas.*;

import Game.Graphics.*;

public class World {
	Mesh terrain;
	Noise noise;
	ArrayList<Mesh> objects;
	int width, length, height;
	public float[] vertices;
	public int[] indices;
	public int seed;
	public World() {
		noise = new Noise();
		//int seed = 10000;
		height = 400;
		width = 100;
		length = 100;
		seed = (int)(Math.random()*100000000);
		terrain = generateWorld(0, 0, 25, 25);
		//determines how zoomed in on the perlin noise the cave will be
		double perlinScaler = 25;
		//display the points or not
		boolean pointsVisible = false;
		//terrain = generateCaves(seed, gridUnit, perlinScaler, pointsVisible);
		generateVerticeList();
		indices = new int[10];
		//terrain = new Mesh();
		System.out.println("Generated "+terrain.getPolygons().size()+" Polygons");
		System.out.println("Seed: "+seed);
		objects = new ArrayList<Mesh>();
	}
	public World(int seed) {
		noise = new Noise();
		//int seed = 10000;
		height = 200;
		width = 100;
		length = 100;
		this.seed = seed;
		terrain = new Mesh(true);
		for(int x = 0; x < 7; x++) {
			for(int z = 0; z < 7; z++) {
				Mesh chunk = generateChunk(seed, x*width, z*length, width, length, 1);
				terrain.addMesh(chunk);
				Mesh tree = ObjectGeneration.generateTree(seed, 5);
				tree.translate(x*100-50, getHeight(x*100-50, z*100-59), z*100-50);
				terrain.addMesh(tree);
			}
		}
		generateVerticeList();
		indices = new int[10];
		System.out.println("Generated "+terrain.getPolygons().size()+" Polygons");
		System.out.println("Seed: "+seed);
		objects = new ArrayList<Mesh>();
	}
	
	public void updateWorld(int xLoc, int yLoc) {
		long sTime = System.nanoTime();
		terrain = generateWorld(xLoc, yLoc, 20, 20);
		long eTime1 = System.nanoTime();
		generateVerticeList();
		long eTime2 = System.nanoTime();
		System.out.println("GenerateWorld took: " + (eTime1-sTime));
		System.out.println("GenerateVertex took: " + (eTime2-eTime1));
		
	}
	
	private Mesh generateWorld(int startX, int startY, int wWidth, int wLength) {
		Mesh map = new Mesh(true);
		for(int w = -1*(wWidth/2); w < wWidth/2; w++) {
			for(int l = -1*(wLength/2); l < (wLength/2); l++) {
				float res = 0.5f;
				if(Math.abs(w) > 10 || Math.abs(l) > 10) {
					res = 0.05f;
				}
				else if(Math.abs(w) > 7 || Math.abs(l) > 7) {
					res = 0.1f;
				}
				else if(Math.abs(w) > 4 || Math.abs(l) > 4) {
					res = 0.2f;
				}
				else if(Math.abs(w) > 3 || Math.abs(l) > 3) {
					res = 0.27f;
				}
				//res = (float)(1/(1+Math.min(w,l)));
				map.addMesh(generateChunk(seed, startX-(w*width), startY-(l*length), width, length, res));
			}
		}
		return map;
	}
	
	//GET HEIGHT
	public float getHeight(float x, float z) {
		double nx = x/width - 0.5; 
		double ny = z/length - 0.5;
		double aF = 0.5;
		double bF = 1;
		double cF = 8;
		double e = Math.abs((1/aF) * Noise.noise(aF*nx+seed, aF*ny+seed) + (1/bF)* Noise.noise(bF*nx+seed, bF*ny+seed) + (1/cF) *Noise.noise(cF*nx+seed, cF*ny+seed));
		e /= (1/aF) + (1/bF) + (1/cF);
		nx = (nx/25.0); 
		ny = (ny/25.0); 
		double d = Math.sqrt(nx*nx + ny*ny) / 0.2;//Math.sqrt(0.5);
		if(d > 1) {
			d = 1;
		}
		e = Math.abs((1 + e - d) / 2);
		float ret = (float)Math.pow(e, 1.80);
		return ret*height;
	}
	public ArrayList<Mesh> generateTrees(float x, float z){
		ArrayList<Mesh> trees = new ArrayList<>();
		int treesPerChunk = 10;
		
		for(int i = 0; i < treesPerChunk; i++) {
			double a = width*Math.abs(Noise.noise(seed+0.01+2*i));
			double b = length*Math.abs(Noise.noise(seed+0.01+2*i+1));
			Mesh tree = ObjectGeneration.generateTree(seed, 5);
			//tree.translate((float)a, getHeight((float)(x + a),(float)( z + b))+100, (float)b);
			tree.translate(0, 100, 0);
		}	
		return trees;
	}
	
	public Mesh getWater() {
		
		Mesh map = new Mesh(null);
		float resolution = 0.2f;
		int chunkW = 2000;
		

		int chunkL = 2000;
		int width = (int)(chunkW*(resolution));
		int length = (int)(chunkL*(resolution));
		float[][] grid = new float[width+1][length+1];
		for(int x = 0; x < grid.length; x++) {
			for(int y = 0; y < grid[0].length; y++) {
				grid[x][y] = 3.5f;
			}
		}
		float pR = 1.0f/resolution;
		int xShift = -1000;
		int zShift = -1000;
		for(int row = 0; row < grid[0].length-1; row++) {
			float[][] pts = {{xShift,grid[0][row],(row*pR)+zShift}, {xShift,grid[0][row+1], ((row+1)*pR)+zShift}, {pR+xShift, grid[1][row], (row*pR)+zShift}};
			Polygon poly = new Polygon(pts[0], pts[1], pts[2]);
			float blueNum = 0.8f+((float)Math.random()*0.1f-0.05f);
			float[] col = new float[] {0.15f,0.3f,blueNum};
			poly.setFColor(col);
			//poly.setColN(0, getLandColor(pts[0][1]));
			//poly.setColN(1, getLandColor(pts[1][1]));
			//poly.setColN(2, getLandColor(pts[2][1]));
			map.addToMesh(poly);
			boolean up = false;
			int x = 1;
			while(x < grid.length) {
				up = !up;
				float[] p = new float[3];
				if(up) {
					p[0] = x*pR+xShift;
					p[1] = grid[x][row+1];
					p[2] = (row+1)*pR+zShift;
				}
				else {
					p[0] = x*pR+xShift;
					p[1] = grid[x][row];
					p[2] = row*pR+zShift;
				}
				pts = shiftPoint(pts, p);
				if(up) {
					poly = new Polygon(pts[1], pts[2], pts[0]);
					float blueNum2 = 0.8f+((float)Math.random()*0.1f-0.05f);
					float[] col2 = new float[] {0.15f,0.3f,blueNum2};
					poly.setFColors(new float[][] {col2, col2, col2});
				}else {
					poly = new Polygon(pts[0], pts[1], pts[2]);
					float blueNum2 = 0.8f+((float)Math.random()*0.1f-0.05f);
					float[] col2 = new float[] {0.15f,0.3f,blueNum2};
					poly.setFColors(new float[][] {col2, col2, col2});
				}
				/*
				if(row == grid[0].length-2) {
                    poly.setFColors(new float[][] {{0f, 0f, 0f},{0f, 0f, 0f},{0f, 0f, 0f}});
                }
				if(x == grid.length-1) {
					poly.setFColors(new float[][] {{0f, 0f, 0f},{0f, 0f, 0f},{0f, 0f, 0f}});
				}*/
				map.addToMesh(poly);
				if(up) {
					x++;
				}
			}
		}
		return map;
		//float[] waterVert = new float
	}
	public void generateVerticeList() {
		ArrayList<Polygon> polys = terrain.getPolygons();
		ArrayList<Float> list = new ArrayList<Float>();
		float[] listArr = new float[polys.size()*27];
		int polyInc = 0;
		int listArrInc = 0;
		float[] lastPCol = new float[3];
		for(Polygon p: polys) {
			for(int i = 0; i < p.getPoints().length; i++) {
				/*list.add(p.getPoints()[i].get(0));
				list.add(p.getPoints()[i].get(1));
				list.add(p.getPoints()[i].get(2));
				float[] pCol = p.fColor[i];
				//float[] pCol = p.getColN(i);
				list.add(pCol[0]);
				list.add(pCol[1]);
				list.add(pCol[2]);
				
				float[] pNorm = p.getNorm().toArray();
				list.add(pNorm[0]);
				list.add(pNorm[1]);
				list.add(pNorm[2]);*/
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
		}/*
		vertices = new float[list.size()];
		for(int i = 0; i < list.size(); i++) {
			vertices[i] = list.get(i);
		}*/
		vertices = listArr;
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
		Mesh caves = new Mesh(true); 	
		//creates a grid of open and closed points
		boolean[][][] openCavities = createOpenCavities(gridUnit, perlinScaler, seed);
		//creates list of 3d points
		ArrayList<GridPoint> pts = createPoints(openCavities, gridUnit);
		//makes points visible
		if(pointsVisible) {
			for(GridPoint p: pts) {
				//System.out.println("X: "+p[0]+" Y: "+p[1]+" Z: "+p[2]);
				Polygon poly = new Polygon(new float[] {p.getLocation()[0]+0.1f, p.getLocation()[1]+0.1f, p.getLocation()[2]+0.1f}, new float[] {p.getLocation()[0]-0.1f, p.getLocation()[1]-0.1f, p.getLocation()[2]-0.1f}, new float[] {p.getLocation()[0]-0.1f, p.getLocation()[1]-0.1f, p.getLocation()[2]+0.1f});
				poly.setFColor(new float[] {1f, 0, 0});
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
	//0.2, 0.6, 0.2 
	private float[] getLandColor(float yHeight) {
		float heightPer = yHeight/height;
		float[][] colors = new float[][] {new float[] {0.949f, 0.729f, 0}, new float[] {0.2f, 0.6f, 0.2f}, new float[] {0.1569f, 0.1569f, 0.1569f}, new float[] {1, 1, 1}};
		float[][] ranges = new float[][] {new float[] {-5f, 0.02f}, new float[] {0.01f, 0.8f}, new float[] {0.5f, 1.7f}, new float[] {1.5f, 5f}};
		float[] color = new float[3];
		
		ArrayList<Integer> rangesIncluded = new ArrayList<Integer>();
		for(int i = 0; i < ranges.length; i++) {
			if(heightPer >= ranges[i][0] && heightPer <= ranges[i][1]) {
				rangesIncluded.add(i);
			}
		}
		if(rangesIncluded.size()>1) {
			float rangePer = (heightPer-ranges[rangesIncluded.get(1)][0])/(ranges[rangesIncluded.get(0)][1]-ranges[rangesIncluded.get(1)][0]);
			
			float r = blendColorValue(colors[rangesIncluded.get(1)][0], colors[rangesIncluded.get(0)][0], 1-rangePer);
			float b = blendColorValue(colors[rangesIncluded.get(1)][1], colors[rangesIncluded.get(0)][1], 1-rangePer);
			float g = blendColorValue(colors[rangesIncluded.get(1)][2], colors[rangesIncluded.get(0)][2], 1-rangePer);
			//System.out.println("Range 0: "+rangesIncluded.get(0)+" Bot Per: "+botPer+" Ranges 1: "+rangesIncluded.get(1)+" Top Per: "+topPer+" R: "+r+" G: "+g+" B: "+b);
			color[0] = r;
			color[1] = b;
			color[2] = g;
		}
		else {
			if(rangesIncluded.size() < 1 && yHeight < 0) {
				rangesIncluded.add(0);
			}
			else if(rangesIncluded.size() < 1 && yHeight > 0) {
				rangesIncluded.add(ranges.length-1);
			}
			//float alter = (float)Math.random()*.025f-.0125f;
			color[0] = colors[rangesIncluded.get(0)][0];//+alter;
			color[1] = colors[rangesIncluded.get(0)][1];//+alter;
			color[2] = colors[rangesIncluded.get(0)][2];//+alter;
		}
		return color;
	}
	private float blendColorValue(float a, float b, float ratio) {
		return (float)Math.sqrt((1 - ratio) * Math.pow(a, 2) + ratio * Math.pow(b, 2));
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
					poly.setFColor(new float[] {0.39f, 0.39f, 0.39f});
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
		
		return (float)((y+100)/153.0);
	}
	public float[] getRedColor(float y){
		return new float[] {(float)((y+40)/85.0),0,0};
	}
	//Resolution is the number of polygons per one unit distance in game
	public Mesh generateChunk(int seed, int xShift, int zShift, int chunkW, int chunkL, float resolution) {
		Mesh map = new Mesh(null);
		int width = (int)(chunkW*(resolution));
		int length = (int)(chunkL*(resolution));
		float[][] grid = new float[width+1][length+1];
		for(int x = 0; x < grid.length; x++) {
			for(int y = 0; y < grid[0].length; y++) {
				grid[x][y] = getHeight(((float)(x)/width)*chunkW+xShift, ((float)(y)/length)*chunkL+zShift);
			}
		}
		float pR = 1.0f/resolution;
		for(int row = 0; row < grid[0].length-1; row++) {
			float[][] pts = {{xShift,grid[0][row],(row*pR)+zShift}, {xShift,grid[0][row+1], ((row+1)*pR)+zShift}, {pR+xShift, grid[1][row], (row*pR)+zShift}};
			Polygon poly = new Polygon(pts[0], pts[1], pts[2]);
			poly.setFColor(getLandColor(grid[0][row]));
			//poly.setColN(0, getLandColor(pts[0][1]));
			//poly.setColN(1, getLandColor(pts[1][1]));
			//poly.setColN(2, getLandColor(pts[2][1]));
			map.addToMesh(poly);
			boolean up = false;
			int x = 1;
			while(x < grid.length) {
				up = !up;
				float[] p = new float[3];
				if(up) {
					p[0] = x*pR+xShift;
					p[1] = grid[x][row+1];
					p[2] = (row+1)*pR+zShift;
				}
				else {
					p[0] = x*pR+xShift;
					p[1] = grid[x][row];
					p[2] = row*pR+zShift;
				}
				pts = shiftPoint(pts, p);
				if(up) {
					poly = new Polygon(pts[1], pts[2], pts[0]);
					
					poly.setFColors(new float[][] {getLandColor(pts[1][1]), getLandColor(pts[2][1]), getLandColor(pts[0][1])});
				}else {
					poly = new Polygon(pts[0], pts[1], pts[2]);
					poly.setFColors(new float[][] {getLandColor(pts[0][1]), getLandColor(pts[1][1]), getLandColor(pts[2][1])});
				}
				/*
				if(row == grid[0].length-2) {
                    poly.setFColors(new float[][] {{0f, 0f, 0f},{0f, 0f, 0f},{0f, 0f, 0f}});
                }
				if(x == grid.length-1) {
					poly.setFColors(new float[][] {{0f, 0f, 0f},{0f, 0f, 0f},{0f, 0f, 0f}});
				}*/
				map.addToMesh(poly);
				if(up) {
					x++;
				}
			}
		}
		
		if(resolution == 1 && chunkW > 30 && chunkL > 30) {
			System.out.println("Generating trees!!!!");
			ArrayList<Mesh> trees = generateTrees(xShift, zShift);
			for(Mesh tree: trees) {
				map.addMesh(tree);
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
