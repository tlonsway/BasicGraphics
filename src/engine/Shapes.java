package engine;

import org.jblas.*;

public class Shapes {

	public static Polygon[] genCube(FloatMatrix point, FloatMatrix dims, int[] color) {
		FloatMatrix p0 = point;
		FloatMatrix p1 = new FloatMatrix(new float[] {point.get(0)+dims.get(0),point.get(1),point.get(2)} );
		FloatMatrix p2 = new FloatMatrix(new float[] {point.get(0),point.get(1)+dims.get(1),point.get(2)} );
		FloatMatrix p3 = new FloatMatrix(new float[] {point.get(0)+dims.get(0),point.get(1)+dims.get(1),point.get(2)} );
		FloatMatrix p4 = new FloatMatrix(new float[] {point.get(0),point.get(1),point.get(2)+dims.get(2)} );
		FloatMatrix p5 = new FloatMatrix(new float[] {point.get(0)+dims.get(0),point.get(1),point.get(2)+dims.get(2)} );
		FloatMatrix p6 = new FloatMatrix(new float[] {point.get(0),point.get(1)+dims.get(1),point.get(2)+dims.get(2)} );
		FloatMatrix p7 = new FloatMatrix(new float[] {point.get(0)+dims.get(0),point.get(1)+dims.get(1),point.get(2)+dims.get(2)} );
		Polygon[] ret = new Polygon[12];
		//color = new int[] {(int)(Math.random()*255),(int)(Math.random()*255),(int)(Math.random()*255)};
		/*
		ret[0] = new Polygon(p0, p1, p4, color);
		ret[1] = new Polygon(p1, p3, p5, color);
		ret[2] = new Polygon(p4, p5, p6, color);
		ret[3] = new Polygon(p1, p4, p5, color);
		ret[4] = new Polygon(p3, p5, p7, color);
		ret[5] = new Polygon(p5, p6, p7, color);
		ret[6] = new Polygon(p0, p2, p6, color);
		ret[7] = new Polygon(p0, p4, p6, color);
		ret[8] = new Polygon(p0, p1, p2, color);
		ret[9] = new Polygon(p2, p3, p6, color);
		ret[10] = new Polygon(p3, p6, p7, color);
		ret[11] = new Polygon(p1, p2, p3, color);
		*/
		return ret;
	}
}
