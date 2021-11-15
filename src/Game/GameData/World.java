package Game.GameData;
import java.util.*;
import org.jblas.*;

import Game.Graphics.*;

public class World {
	ArrayList<Mesh> terrain;
	Noise noise;
	Mesh tree;
	Graphics g;
	ArrayList<Mesh> objects;
	HashMap<String, Mesh> chunkBuf;
	HashMap<String, float[]> chunkVertBuf;
	int width, length, height;
	public float[] vertices;
	public int[] indices;
	public int seed;
	public World() {
		this.g = g;
		chunkBuf = new HashMap<>();
		chunkVertBuf = new HashMap<>();
		noise = new Noise();
		//int seed = 10000;
		height = 300;
		width = 100;
		length = 100;
		seed = (int)(Math.random()*100000000);
		terrain = generateWorld(0, 0, 20, 20);
		//determines how zoomed in on the perlin noise the cave will be
		double perlinScaler = 25;
		//display the points or not
		boolean pointsVisible = false;
		//terrain = generateCaves(seed, gridUnit, perlinScaler, pointsVisible);
		generateVerticeList(0, 0);
		indices = new int[10];
		//terrain = new Mesh();
		//System.out.println("Generated "+terrain.getPolygons().size()+" Polygons");
		System.out.println("Seed: "+seed);
		objects = new ArrayList<Mesh>();
		
		
	}
	public void setGraphics(Graphics g) {
		this.g = g;
		tree = ObjectGeneration.generateTree(seed, 3);
		for(int x = -2; x < 2; x++) {
			for(int y = -2; y < 2; y++) {
				generateTrees(x*width, y*length); 
			}
		}
	}
	
	public World(int seed) {
		noise = new Noise();
		//int seed = 10000;
		height = 200;
		width = 100;
		length = 100;
		this.seed = seed;
		terrain = new ArrayList<>();
		for(int x = 0; x < 7; x++) {
			for(int z = 0; z < 7; z++) {
				Mesh chunk = generateChunk(seed, x*width, z*length, width, length, 1);
				terrain.add(chunk);
				Mesh tree = ObjectGeneration.generateTree(seed, 5);
				tree.translate(x*100-50, getHeight(x*100-50, z*100-59), z*100-50);
				terrain.add(tree);
			}
		}
		generateVerticeList(0, 0);
		indices = new int[10];
		
		System.out.println("Seed: "+seed);
		objects = new ArrayList<Mesh>();
	}
	
	public void updateWorld(int xLoc, int yLoc) {
		long sTime = System.nanoTime();
		xLoc+=50;
		yLoc+=50;
		xLoc = xLoc-(xLoc%100);
		yLoc = yLoc-(yLoc%100);
		terrain = generateWorld(xLoc, yLoc, 20, 20);
		long eTime1 = System.nanoTime();
		generateVerticeList(xLoc, yLoc);
		long eTime2 = System.nanoTime();
		System.out.println("GenerateWorld took: " + (eTime1-sTime));
		System.out.println("GenerateVertex took: " + (eTime2-eTime1));
	}
	
	private ArrayList<Mesh> generateWorld(int startX, int startY, int wWidth, int wLength) {
		ArrayList<Mesh> map = new ArrayList<>();
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
					res = 0.25f;
				}
				//res = (float)(1/(1+Math.min(w,l)));
				map.add(generateChunk(seed, startX-(w*width), startY-(l*length), width, length, res));
			}
		}
		System.out.println("Terrain size: " + map.size());
		return map;
	}
	
	//GET HEIGHT
	public float getHeight(float x, float z) {
		double ret = processNoise(Noise.noise(x/300.0+seed+0.1, z/300.0+seed+0.1), 3.2, -0.6, 0.6) + 0.07*processNoise(Noise.noise(x/40.0+seed+0.1, z/40.0+seed+0.1), 0, -0.3, 0.9);
		
		double nx = x/width - 0.5; 
		double ny = z/length - 0.5;
		double d = Math.sqrt(nx*nx + ny*ny) / 10;//Math.sqrt(0.5);
		if(d > 1) {
			d = 1;
		}
		ret = 0.5-d+ret;
		ret = (ret-0.3)*height;
		return (float)ret;
	}
	
	public double processNoise(double noise,double a,double b,double c) {
		noise = (noise+1)/2;
		double d = 0.2;
		double val = a*Math.pow(noise-d, 3)+b*Math.pow(noise-d, 2)+c*(noise-d)+d;
		return val;
	}
	
	public Mesh generateTrees(float x, float z){
		Mesh trees = new Mesh(true);
		Mesh tree = ObjectGeneration.generateTree(seed,5);
		int treesPerChunk = 2;
		for(int i = 0; i < treesPerChunk; i++) {
			double a = width*Math.abs(Noise.noise(seed+0.5+2*i));
			double b = width*Math.abs(Noise.noise(seed+0.5+2*i+1));
			double h = getHeight((float)(a+x), (float)(b+z))+11;
			if(h > height*0.09 && h < height*0.58) {
				GameObject go = new GameObject("tree", this, tree);
				go.setPosition(new float[] {(float)(a+x), (float)h, (float)(b+z)});
				g.addGameObject(go);
			}
		}	
		return trees;
	}
	
	public Mesh getWater(float quality) {
		
		Mesh map = new Mesh(null);
		//float resolution = 0.2f;
		float resolution = quality;
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
	public void generateVerticeList(double startX, double startZ) {
		int sum = 0;
		for(Mesh m: terrain) {
			sum+=m.getPolygons().size();
		}
		int inc = 0;
		System.out.println("Terrain: "+terrain.size()+" Polygons: "+sum);
		float[] ver = new float[sum*27];
		for(int x = -10; x < 10;x++) {
			for(int z = -10; z < 10;z++) {
				Mesh m = terrain.get((x+10)*20+(z+10));
				m.generateVertices();
				for(int i = 0; i < m.verts.length; i++) {
					ver[inc] = m.verts[i];
					inc++;
				}
			}
		}		
		vertices = ver;
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
	//0.2, 0.6, 0.2 
	private float[] getLandColor(float yHeight) {
		float heightPer = yHeight/height;
		float[][] colors = new float[][] {new float[] {0.949f, 0.729f, 0}, new float[] {0.2f, 0.6f, 0.2f}, new float[] {0.1569f, 0.1569f, 0.1569f}, new float[] {1, 1, 1}};
		float[][] ranges = new float[][] {new float[] {-5f, 0.05f}, new float[] {0.04f, 0.6f}, new float[] {0.58f, 0.87f}, new float[] {0.85f, 5f}};
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
		
		
		float redAlt = ((float)Math.random()*0.02f-0.01f);
		float greenAlt = ((float)Math.random()*0.02f-0.01f);
		float blueAlt = ((float)Math.random()*0.02f-0.01f);
		color[0] += redAlt;
		color[1] += greenAlt;
		color[2] += blueAlt;
		
		
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
		
		if(resolution == 1 &&  chunkW > 30 &&chunkBuf.containsKey(xShift+":"+zShift)) {
			//System.out.println("World gen key: "+xShift+":"+zShift);
			return chunkBuf.get(xShift+":"+zShift);
		}
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
					poly = new Polygon(pts[1], pts[0], pts[2]);
				}
				
				
				if(up) {
					//poly.setFColors(new float[][] {getLandColor(pts[1][1]), getLandColor(pts[2][1]), getLandColor(pts[0][1])});
					poly.setFColors(new float[][] {getLandColor(pts[1][1]), getLandColor(pts[0][1]), getLandColor(pts[2][1])});
					float[] redCol = new float[] {1.0f,0.0f,0.0f};
					//poly.setFColors(new float[][] {redCol,redCol,redCol});
				}else {
					poly = new Polygon(pts[0], pts[1], pts[2]);
					poly.setFColors(new float[][] {getLandColor(pts[0][1]), getLandColor(pts[1][1]), getLandColor(pts[2][1])});
					//float[] redCol = new float[] {1.0f,0.0f,0.0f};
					//poly.setFColors(new float[][] {redCol,redCol,redCol});
				}
				map.addToMesh(poly);
				if(up) {
					x++;
				}
			}
		}
		map.generateVertices();
		if(resolution == 1 && chunkW > 30 && chunkL > 30) {
			chunkBuf.put(xShift+":"+zShift, map);
			
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
		ArrayList<Polygon> polys = new ArrayList<>();
		for(Mesh m: terrain) {
			for(Polygon p: m.getPolygons()) {
				polys.add(p);
			}
		}
		return polys;
	}
}
