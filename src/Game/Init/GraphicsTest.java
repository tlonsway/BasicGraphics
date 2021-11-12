package Game.Init;

import java.util.*;
import Game.GameData.*;
import Game.Graphics.*;
import Game.Network.*;
public class GraphicsTest {
	public static void main(String[] args) {
		
		int[] screenDims = new int[] {1920,1080};
		
		GameManager g = new GameManager(screenDims);
		//float[] vertices = new float[] {-0.1f,0.1f,-1.2f,
		//		0f,-0.1f,-1.2f,
		//		0.1f,0.1f,-1.2f};
		
		//int[] indices = new int[] {1,2,3};
		//g.updateData(vertices, indices);
		
		System.out.println("Creating world mesh");
		World world = new World();
		
		g.setWorld(world);
		//world.setGraphics(g);
		//float[] vert = world.vertices;
		//int[] ind = world.indices;;
		//g.updateData(vert, ind);
		g.setWorldUpdateReady();
		System.out.println("World mesh complete");
		
		
		//for(int i=0;i<100;i++) {
		System.out.println("Creating trees");
		/*for(int r=0;r<10;r++) {
			for(int c=0;c<10;c++) {
				//Polygon p1 = new Polygon(new float[] {5+(2*i),7,5+(2*i)},new float[] {5+(2*i),9,5+(2*i)},new float[] {5+(2*i),9,7+(2*i)});
				//Polygon p1 = new Polygon(new float[] {5+(4*r),7,5+(4*c)},new float[] {5+(4*r),9,5+(4*c)},new float[] {5+(4*r),9,7+(4*c)});
				//p1.setFColor(new float[] {0.9f,0.2f,0.9f});
				//ArrayList<Polygon> meshPolys = new ArrayList<Polygon>();
				//meshPolys.add(p1);
				//Mesh m = new Mesh(meshPolys);
				Mesh m = ObjectGeneration.generateTree((int)(Math.random()*100000000), 8);
				GameObject go1 = new GameObject("Tree",world,m);
				//go1.disableGravity();
				go1.translate(20f+(r*50), 150f, 20f+(c*50));
				g.addGameObject(go1);
			}
		}*/
		/*float worldWid = 750;
		float worldHgt = 750;
		for(int i=0;i<100;i++) {
			Mesh m = ObjectGeneration.generateTree((int)(Math.random()*100000000), 8);
			GameObject go1 = new GameObject("Tree",world,m);
			float tXLoc = (float)Math.random()*worldWid;
			float tYLoc = (float)Math.random()*worldHgt;
			go1.translate(tXLoc, 150f, tYLoc);
			g.addGameObject(go1);
		}*/
		System.out.println("Tree generation complete");
		
		//float[] crosshair = new float[] {-0.1f,0f, 0.1f,0f,
		//								 0f,-0.1f, 0f,0.1f};
										
		//g.setUIData(crosshair);
		
		
		//AABB polyBound = new AABB(new float[] {5,7,5}, new float[] {5,9,7});
		//GameObject go1 = new GameObject(world,m,polyBound);
		//polyBound.setObject(go1);
		
		//go1.rotate('y', (float)(Math.PI/2), false);
		//g.loop();
		g.gameLoop();
	}	
}
