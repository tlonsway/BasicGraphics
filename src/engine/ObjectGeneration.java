package engine;

import org.jblas.*;
public class ObjectGeneration {
	public static Mesh generateTree(int seed, int resolution) {
		Noise noise = new Noise();
		Mesh tree;
		float trunkRadius = (float)(Math.abs(noise.noise(seed/10.0)+0.3)*2);
		float trunkHeight = (float)(Math.abs(noise.noise(seed/10.0+0.3f)*5+13));
		float branchLength = (float)((Math.abs(noise.noise(seed/10.0+0.6f))+0.3)*4);
		float branchAngle = (float)(Math.abs(noise.noise(seed/10.0+0.6f))+0.3); 
		int numBranches = (int)((noise.noise(seed/10.0+0.9f)+1)*4)+2;
		tree = generateBranch(3, trunkHeight, trunkRadius, resolution, 6, 0.8f, new float[] {0, 0, 0}, new float[] {0,trunkHeight, 0});
		//tree.addMesh(generateCylinder(trunkRadius, trunkHeight, resolution));
		//System.out.println("Created a tree with "+tree.getPolygons().size()+" polygons a height of: "+trunkHeight+" a radius of: "+trunkRadius+" and "+numBranches+" branches");
		return tree;
	}
	private static Mesh generateBranch(int level, float bl, float bw, int resolution, int numBranches, float branchAngle, float[] rotations, float[] endPoint) {
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
		if(level != 0) {
			if(numBranches != 2) {
				numBranches--;
			}
			float angle = (float)((Math.PI*2)/numBranches);
			for(int i = 0; i < numBranches; i++) {
				//nextVector = game.Operations.rotatePoint(nextVector, 'y', 0.8f);
				Mesh b = generateBranch(level-1, bl*0.8f, bw*0.75f, resolution, numBranches, branchAngle*0.9f,new float[] {rotations[0]+0.3f, (angle*i), 0}, nep );
				branch.addMesh(b);
			}
			
		}	
		return branch;
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
				side.setFColor(new float[] {0.4375f, 0.25f, 0.015625f});
			}
			else {
				side.setFColor(new float[] {0.34f, 0.21f,0});
			}
			down = !down;
			cyl.addToMesh(side);
		}
		return cyl;
	}
}
