package engine;

public class ObjectGeneration {
	public static Mesh generateTree(int seed) {
		Noise noise = new Noise();
		Mesh tree = new Mesh();
		float trunkRadius = (float)(Math.abs(noise.noise(seed/10.0)+0.3)*2.5);
		float trunkHeight = (float)(Math.abs(noise.noise(seed/10.0+0.3f)+0.5)*9);
		float branchLength = (float)noise.noise(seed+0.6f);
		tree.addMesh(generateCylinder(trunkRadius, trunkHeight, 10));
		System.out.println("Created a tree with "+tree.getPolygons().size()+" polygons a height of: "+trunkHeight+" and a radius of: "+trunkRadius);
		return tree;
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
