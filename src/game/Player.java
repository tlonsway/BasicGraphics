package game;

import org.jblas.*;
import java.util.*;

public class Player extends GameObject {

	public Player(World world) {
		super("Player",world);
		Polygon[] playerCube = Shapes.genCube(new FloatMatrix(new float[] {-1,-1,-1}),new FloatMatrix(new float[] {2,2,2}),new int[] {255,0,0});
		ArrayList<Polygon> playerCubeList = new ArrayList<Polygon>();
		for(Polygon p : playerCube) {
			playerCubeList.add(p);
		}
		Mesh m = new Mesh(playerCubeList);
		setMesh(m);
	}
	
}
