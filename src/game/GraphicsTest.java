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
		int[] ind = world.indices;;
		g.updateData(vert, ind);
		g.loop();
	}	
}
