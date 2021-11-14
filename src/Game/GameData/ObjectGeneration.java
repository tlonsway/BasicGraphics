package Game.GameData;

import org.jblas.FloatMatrix;

import Game.Graphics.Mesh;
import Game.Graphics.Polygon;

public class ObjectGeneration {
	public static Mesh generateTree(int seed, int resolution) {
		Mesh tree = new Mesh(true);
		boolean split = Noise.genfloat(seed, 0, 1) > 0.2;
		float bA;
		int numBranchStacks;
		int levels;
		//split > 0.0
		if(!split) {
			bA = Noise.genfloat(seed, 0.7f, 0.9f);
			numBranchStacks = Noise.genInt(seed+6, 3, 5);
			levels = 3;
		}
		else {
			numBranchStacks = 1;
			bA = Noise.genfloat(seed+12, 0.4f, 0.8f);
			levels = 4;
		}
		float trunkRadius = Noise.genfloat(seed+18, 0.5f, 1.3f);
		float trunkHeight = Noise.genfloat(seed+24, 4f, 9f);
		int numBranches = Noise.genInt(seed+30, 6, 10);
		float height = 0;
		float yRot = 0;
		for(int a = 0; a < numBranchStacks; a++) {
			Mesh branch = generateBranch(levels, trunkHeight, trunkRadius, bA, resolution, numBranches, new float[] {0, 0, 0}, new float[] {0,trunkHeight, 0}, seed);
			branch.translate(0, -trunkHeight-0.02f+height, 0);
			branch.rotate(new float[] {0, 0, 0}, 'y', yRot);
			yRot += Math.PI/4.0;
			height+=trunkHeight;
			trunkHeight*=0.85;
			trunkRadius*=0.75;
			if(resolution > 3) {
				resolution--;
			}
			tree.addMesh(branch);
		}
		//System.out.println("Created a tree with "+tree.getPolygons().size()+" polygons a height of: "+trunkHeight+" and a radius of: "+trunkRadius);
		return tree;
	}
	public static float[] generateColor(int seed, String c){
		float[] color = new float[3];
		if(c.equals("brown")) {
			color[0] = Noise.genfloat(seed, 0f, 0.35f)+0.39f;
			color[1] = Noise.genfloat(seed, 0f, 0.195f)+0.234f;
			color[2] = Noise.genfloat(seed, 0f, 0.078f); 
		}
		else if(c.equals("green")) {
			color[0] = Noise.genfloat(seed, 0f, 0.117f)+0.195f;
			color[1] = Noise.genfloat(seed, 0f, 0.352f)+0.508f;
			color[2] = Noise.genfloat(seed, 0f, 0.0976f); 
		}
		return color;
	}
	
	private static Mesh generateBranch(int level, float bl, float bw, float bA, int resolution, int numBranches, float[] rotations, float[] endPoint, int seed) {
		Mesh branch = generateCylinder(bw, bl, resolution, seed, "brown");
		branch.translate(endPoint[0], endPoint[1], endPoint[2]);
		FloatMatrix nextEndPoint = new FloatMatrix(new float[] {0, bl, 0});
		branch.rotate(endPoint, 'x', rotations[0]);
		nextEndPoint = Game.Graphics.Operations.rotatePoint(nextEndPoint, 'x', rotations[0]);
		branch.rotate(endPoint, 'y', rotations[1]);
		nextEndPoint = Game.Graphics.Operations.rotatePoint(nextEndPoint, 'y', rotations[1]);
		branch.rotate(endPoint, 'z', rotations[2]);
		nextEndPoint = Game.Graphics.Operations.rotatePoint(nextEndPoint, 'z', rotations[2]);
		float[] nep = new float[] {endPoint[0]+nextEndPoint.get(0), endPoint[1]+nextEndPoint.get(1), endPoint[2]+nextEndPoint.get(2)};
		if(level < 1) {
			boolean z = false;
			if(level%2 == 0) {
				z=true;
			}
			Mesh leaves = generateLeaves(z, seed);
			leaves.translate(nep[0], nep[1], nep[2]);
			branch.addMesh(leaves);
		}
		if(level != 0) {
			if(numBranches != 2) {
				numBranches--;
			}
			
			float angle = (float)(Math.PI*2.0/numBranches);
			float radius = (float)(Math.sin(bA)*bl);
			float range = (float)(2*radius*Math.cos((Math.PI/2.0)-rotations[0]));
			if(range < 0.000000001) {
				range = 0;
			}
			for(int i = 0; i < numBranches; i++) {
				float height = (float)((range)*((Math.cos((i*angle)+Math.PI)/2.0f)+0.5f));
				float c = (float)(height/Math.cos((Math.PI/2.0)-rotations[0]));
				float w = (float)(Math.sqrt(Math.pow(c, 2)-Math.pow(height, 2)));
				float z = (float)(Math.cos(Math.PI-rotations[0]-bA));
				float y = (float)(Math.sqrt(Math.pow(bl, 2)-Math.pow(z, 2)));
				float l = (float)(z-w);
				float p = (float)(y + height);
				float a = (float)(Math.sqrt(Math.pow(l, 2)+Math.pow(p, 2)));
				float g = (float)((Math.pow(a, 2)+Math.pow(bl, 2)-Math.pow(c, 2))/(2*a*bl));
				float xRotRem = (float)(Math.PI*Math.acos(g));
				if(xRotRem < .0001) {
					xRotRem = 0;
				}
				if(resolution > 3) {
					resolution--;
				}
				float yRot;
				float theta = (float)(i*angle);
				if(range!=0) {
					float theta2 = theta;
				if(theta2 > Math.PI) {
					}
					yRot = (float)(theta2*(1-height/range));
				}
				else {
					yRot = (float)(i*angle);
				}
				if(theta > Math.PI && range!=0) {
					yRot*=-1;
				}
				//System.out.println("Level: "+level+" cA: "+rotations[0]+" Range: "+range+" height: "+height+" (p,l)A: ("+p+","+l+")"+a+" C: "+c+" G: "+g+" xRotRem: "+xRotRem+" RotPer: "+yRot+" theta: "+(angle*i)+" numBranches: "+numBranches+" branchNum: "+i+" branchL: "+bl);
				Mesh b = generateBranch(level-1, bl*0.8f, bw*0.75f, bA, resolution, numBranches, new float[] {rotations[0]+bA-xRotRem, yRot+rotations[1], 0}, nep, seed);
				branch.addMesh(b);
			}
		}	
		return branch;
	}
	public static Mesh generateLeaves(boolean zAxis, int seed) {
		Mesh leaves = new Mesh(true);
		FloatMatrix p1 = new FloatMatrix(new float[] {0, -1.0f, -1.0f});
		FloatMatrix p2 = new FloatMatrix(new float[] {0, -1.0f, 1.0f});
		for(int i = 0; i < 4; i++) {
			Polygon p = new Polygon(new float[] {p1.get(0), p1.get(1), p1.get(2)}, new float[] {p2.get(0), p2.get(1), p2.get(2)}, new float[] {0, 0, 0});
			p.setFColor(generateColor(seed, "green"));
			leaves.addToMesh(p);
			p1 = Game.Graphics.Operations.rotatePoint(p1, 'z', (float)(Math.PI/2.0f));
			p2 = Game.Graphics.Operations.rotatePoint(p2, 'z', (float)(Math.PI/2.0f));
		}
		p1 = Game.Graphics.Operations.rotatePoint(p1, 'y', (float)(Math.PI/2.0f));
		p2 = Game.Graphics.Operations.rotatePoint(p2, 'y', (float)(Math.PI/2.0f));
		Polygon p = new Polygon(new float[] {p1.get(0), p1.get(1), p1.get(2)}, new float[] {p2.get(0), p2.get(1), p2.get(2)}, new float[] {0, 0, 0});
		p.setFColor(generateColor(seed, "green"));
		leaves.addToMesh(p);
		p1 = Game.Graphics.Operations.rotatePoint(p1, 'z', (float)(Math.PI));
		p2 = Game.Graphics.Operations.rotatePoint(p2, 'z', (float)(Math.PI));
		p1 = Game.Graphics.Operations.rotatePoint(p1, 'y', (float)(Math.PI));
		p2 = Game.Graphics.Operations.rotatePoint(p2, 'y', (float)(Math.PI));
		p = new Polygon(new float[] {p1.get(0), p1.get(1), p1.get(2)}, new float[] {p2.get(0), p2.get(1), p2.get(2)}, new float[] {0, 0, 0});
		//p.setFColor(new float[] {0.4f, 0.54f, 0.24f});
		p.setFColor(generateColor(seed, "green"));
		leaves.addToMesh(p);
		return leaves;
	}
	public static Mesh generateFern(int seed) {
		Mesh bush = new Mesh(true);
		int circles = (int)(Noise.genfloat(seed, 3f, 5)+0.5);
		int leaves = (int)(Noise.genfloat(seed+3, 5, 10)+0.5);
		float height = Noise.genfloat(seed+6, 1f, 3f);
		float angle = Noise.genfloat(seed+9, 0.05f,  0.6f);
		float width = Noise.genfloat(seed+12, 0.05f, 0.2f);
		Polygon start = new Polygon(new float[] {0,0,-width}, new float[] {0, 0, width}, new float[] {0,height, 0});
		start.rotate(new float[] {0,0,0}, 'z', (float)((Math.PI/2.0)-angle));
		for(int i = 0; i < circles; i++) {
			if(i == circles-1) {
				leaves = 3;
			}
			start.rotate(new float[] {0, 0, 0}, 'z', (float)(((Math.PI/2.0)-angle)/circles)*-1f);
			if(i%2 == 0) {
				//start.setFColor(new float[] {.16f, 0.42f, 0.07f});
				start.setFColor(generateColor(seed, "green"));
			}
			else {
				//start.setFColor(new float[] {.18f, 0.49f, 0.07f});
				start.setFColor(generateColor(seed+10, "green"));
			}
			for(int a = 0; a < leaves; a++) {
				Polygon leaf = start.clone();
				leaf.rotate(new float[] {0,0,0}, 'y', (float)(Math.PI*2.0/leaves)*a);
				bush.addToMesh(leaf);
			}
		}
		return bush;
	}
	private static Mesh generateCylinder(float radius, float height, int resolution, int seed, String color) {
		Mesh cyl = new Mesh(true);
		boolean down = false;
		int r = 0;
		float angle = (float)((Math.PI*2)/resolution);
		while(r < resolution) {
			float[][] pts = new float[3][3];
			if(down) {
				//Point one
				pts[0][0] = (float)(Math.cos((double)(angle*r+(angle/2.0)))*radius);
				pts[0][1] = height;
				pts[0][2] = (float)(Math.sin((double)(angle*r+(angle/2.0)))*radius);
				//Point two
				pts[1][0] = (float)(Math.cos((double)(angle*(r+1)+(angle/2.0)))*radius);
				pts[1][1] = height;
				pts[1][2] = (float)(Math.sin((double)(angle*(r+1)+(angle/2.0)))*radius);
				//Point Three
				pts[2][0] = (float)(Math.cos((double)(angle*(r+1)))*radius);
				pts[2][1] = 0;
				pts[2][2] = (float)(Math.sin((double)(angle*(r+1)))*radius);
				r++;
			}
			else {
				//Point one
				pts[2][0] = (float)(Math.cos((double)(angle*r))*radius);
				pts[2][1] = 0;
				pts[2][2] = (float)(Math.sin((double)(angle*r))*radius);
				//Point two
				pts[1][0] = (float)(Math.cos((double)(angle*(r+1)))*radius);
				pts[1][1] = 0;
				pts[1][2] = (float)(Math.sin((double)(angle*(r+1)))*radius);
				//Point Three
				pts[0][0] = (float)(Math.cos((double)(angle*r+(angle/2.0)))*radius);
				pts[0][1] = height;
				pts[0][2] = (float)(Math.sin((double)(angle*r+(angle/2.0)))*radius);
			}
			
			Polygon side = new Polygon(pts[0], pts[1], pts[2]);
			if(down) {
				side.setFColor(generateColor(seed, color));
			}
			else {
				side.setFColor(generateColor(seed, color));
				//side.setFColor(new float[] {0,0,1});
			}
			down = !down;
			cyl.addToMesh(side);
		}
		return cyl;
	}
}
