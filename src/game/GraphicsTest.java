package game;

import java.util.*;

public class GraphicsTest {
	public static void main(String[] args) {
		Graphics g = new Graphics();
		//float[] vertices = new float[] {-0.1f,0.1f,-1.2f,
		//		0f,-0.1f,-1.2f,
		//		0.1f,0.1f,-1.2f};
		
		//int[] indices = new int[] {1,2,3};
		//g.updateData(vertices, indices);
		World world = new World();
		
		g.setWorld(world);
		
		float[] vert = world.vertices;
		int[] ind = world.indices;;
		g.updateData(vert, ind);
		
		//for(int i=0;i<100;i++) {
		for(int r=0;r<40;r++) {
			for(int c=0;c<40;c++) {
				//Polygon p1 = new Polygon(new float[] {5+(2*i),7,5+(2*i)},new float[] {5+(2*i),9,5+(2*i)},new float[] {5+(2*i),9,7+(2*i)});
				Polygon p1 = new Polygon(new float[] {5+(4*r),7,5+(4*c)},new float[] {5+(4*r),9,5+(4*c)},new float[] {5+(4*r),9,7+(4*c)});
				p1.setFColor(new float[] {0.9f,0.2f,0.9f});
				ArrayList<Polygon> meshPolys = new ArrayList<Polygon>();
				meshPolys.add(p1);
				Mesh m = new Mesh(meshPolys);
				GameObject go1 = new GameObject("TestPoly",world,m);
				go1.translate(0f, 150f, 0f);
				g.addGameObject(go1);
			}
		}
		//AABB polyBound = new AABB(new float[] {5,7,5}, new float[] {5,9,7});
		//GameObject go1 = new GameObject(world,m,polyBound);
		//polyBound.setObject(go1);
		
		//go1.rotate('y', (float)(Math.PI/2), false);
		g.loop();
	}	
}
