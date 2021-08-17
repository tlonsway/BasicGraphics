package game;

import org.jblas.FloatMatrix;

public class ObjectGeneration {
	public static Mesh generateTree(int seed, int resolution) {
		Mesh tree;
		float trunkRadius = Noise.genfloat(seed, 0.5f, 2.0f);
		float trunkHeight = Noise.genfloat(seed, 10f, 20f);
		int numBranches = Noise.genInt(seed,  6, 6 );
		//System.out.println("trunkRadius: "+trunkRadius+" trunkHeight: "+ trunkHeight);
		tree = generateBranch(3, trunkHeight, trunkRadius, resolution, 6, new float[] {0, 0, 0}, new float[] {0,trunkHeight, 0});
		tree.translate(0, -trunkHeight-0.01f, 0);
		//System.out.println("Created a tree with "+tree.getPolygons().size()+" polygons a height of: "+trunkHeight+" and a radius of: "+trunkRadius);
		return tree;
	}
	private static Mesh generateBranch(int level, float bl, float bw, int resolution, int numBranches, float[] rotations, float[] endPoint) {
		Mesh branch = generateCylinder(bw, bl, resolution);
		branch.translate(endPoint[0], endPoint[1], endPoint[2]);
		FloatMatrix nextEndPoint = new FloatMatrix(new float[] {0, bl, 0});
		branch.rotate(endPoint, 'x', rotations[0]);
		nextEndPoint = game.Operations.rotatePoint(nextEndPoint, 'x', rotations[0]);
		branch.rotate(endPoint, 'y', rotations[1]);
		nextEndPoint = game.Operations.rotatePoint(nextEndPoint, 'y', rotations[1]);
		branch.rotate(endPoint, 'z', rotations[2]);
		nextEndPoint = game.Operations.rotatePoint(nextEndPoint, 'z', rotations[2]);
		float[] nep = new float[] {endPoint[0]+nextEndPoint.get(0), endPoint[1]+nextEndPoint.get(1), endPoint[2]+nextEndPoint.get(2)};
		if(level < 2) {
			Mesh leaves = generateLeaves();
			leaves.translate(nep[0], nep[1], nep[2]);
			branch.addMesh(leaves);
		}
		if(level != 0) {
			if(numBranches != 2) {
				numBranches--;
			}
			float bA = 0.5f;
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
				float xRotRem = (float)(Math.acos(g));
				if(xRotRem < .0001) {
					xRotRem = 0;
				}
				if(resolution > 3) {
					resolution--;
				}
				System.out.println("Level: "+level+" cA: "+rotations[0]+" Range: "+range+" height: "+height+" (p,l)A: ("+p+","+l+")"+a+" C: "+c+" G: "+g+" xRotRem: "+xRotRem+" theta: "+(angle*i)+" numBranches: "+numBranches+" branchNum: "+i+" branchL: "+bl);
				Mesh b = generateBranch(level-1, bl*0.8f, bw*0.75f, resolution, numBranches, new float[] {rotations[0]+bA-xRotRem, angle*i+rotations[1], 0}, nep);
				branch.addMesh(b);
			}
		}	
			
		return branch;
	}
	public static Mesh generateLeaves() {
		Mesh leaves = new Mesh();
		FloatMatrix p1 = new FloatMatrix(new float[] {-1.0f, 0, -1.0f});
		FloatMatrix p2 = new FloatMatrix(new float[] {-1.0f, 0, 1.0f});
		for(int i = 0; i < 4; i++) {
			Polygon p = new Polygon(new float[] {p1.get(0), p1.get(1), p1.get(2)}, new float[] {p2.get(0), p2.get(1), p2.get(2)}, new float[] {0, 0, 0});
			p.setFColor(new float[] {1, 0, 0});
			leaves.addToMesh(p);
			p1 = game.Operations.rotatePoint(p1, 'z', (float)(Math.PI/2.0f));
			p2 = game.Operations.rotatePoint(p2, 'z', (float)(Math.PI/2.0f));
		}
		return leaves;
	}
	private static Mesh generateCylinder(float radius, float height, int resolution) {
		Mesh cyl = new Mesh();
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
				pts[0][0] = (float)(Math.cos((double)(angle*r))*radius);
				pts[0][1] = 0;
				pts[0][2] = (float)(Math.sin((double)(angle*r))*radius);
				//Point two
				pts[1][0] = (float)(Math.cos((double)(angle*(r+1)))*radius);
				pts[1][1] = 0;
				pts[1][2] = (float)(Math.sin((double)(angle*(r+1)))*radius);
				//Point Three
				pts[2][0] = (float)(Math.cos((double)(angle*r+(angle/2.0)))*radius);
				pts[2][1] = height;
				pts[2][2] = (float)(Math.sin((double)(angle*r+(angle/2.0)))*radius);
			}
			
			Polygon side = new Polygon(pts[0], pts[1], pts[2]);
			if(down) {
				side.setFColor(new float[] {0.34f, 0.21f,0});
			}
			else {
				//side.setFColor(new float[] {0.34f, 0.21f,0});
				side.setFColor(new float[] {0,0,1});
			}
			down = !down;
			cyl.addToMesh(side);
		}
		return cyl;
	}
}
