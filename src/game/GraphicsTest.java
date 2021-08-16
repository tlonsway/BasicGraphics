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
		
		Polygon p1 = new Polygon(new float[] {5,7,5},new float[] {5,9,5},new float[] {5,9,7});
		p1.setFColor(new float[] {0.9f,0.2f,0.9f});
		ArrayList<Polygon> meshPolys = new ArrayList<Polygon>();
		meshPolys.add(p1);
		Mesh m = new Mesh(meshPolys);
		//AABB polyBound = new AABB(new float[] {5,7,5}, new float[] {5,9,7});
		GameObject go1 = new GameObject("World",world,m);
		//GameObject go1 = new GameObject(world,m,polyBound);
		//polyBound.setObject(go1);
		go1.translate(0f, 70f, 0f);
		g.addGameObject(go1);
		
		
		g.loop();
	}	
}
