package game;


public class GraphicsTest {
	public static void main(String[] args) {
		Graphics g = new Graphics();
		//float[] vertices = new float[] {-0.1f,0.1f,-1.2f,
		//		0f,-0.1f,-1.2f,
		//		0.1f,0.1f,-1.2f};
		
		//int[] indices = new int[] {1,2,3};
		//g.updateData(vertices, indices);
		World world = new World();
		float[] vert = world.vertices;
		int[] ind = world.indices;
		World world2 = new World();
		float[] vert2 = world2.vertices;
		int[] ind2 = world2.indices;
		g.updateData(vert, ind);
		g.updateData2(vert2, ind2);
		g.loop();
	}	
}
