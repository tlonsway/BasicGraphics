package testing;

import org.jblas.*;

public class Operations {
	
	public static FloatMatrix translateMat(FloatMatrix mat, float x, float y, float z) {
		float[][] tMat = new float[][] {{1,0,0,x},{0,1,0,y},{0,0,1,z},{0,0,0,1}};
		FloatMatrix tMat2 = new FloatMatrix(tMat);
		return tMat2.mmul(mat);
	}
	
	public static FloatMatrix scaleMat(FloatMatrix mat, float x, float y, float z) {
		float[][] tMat = new float[][] {{x,0,0,0},{0,y,0,0},{0,0,z,0},{0,0,0,1}};
		FloatMatrix tMat2 = new FloatMatrix(tMat);
		return tMat2.mmul(mat);
	}
	
	public static FloatMatrix rotateMat(FloatMatrix mat, char c, float degree) {
		float[][] tMat = new float[4][4];
        if (c=='x') {
            tMat = new float[][]{{1,0,0,0},{0,(float)Math.cos(degree),-(float)(Math.sin(degree)),0},{0,(float)Math.sin(degree),(float)Math.cos(degree),0},{0,0,0,1}};           
        }
        if (c=='y') {
            tMat = new float[][]{{(float)Math.cos(degree),0,(float)Math.sin(degree),0},{0,1,0,0},{-(float)(Math.sin(degree)),0,(float)Math.cos(degree),0},{0,0,0,1}};              
        }
        if (c=='z') {
            tMat = new float[][]{{(float)Math.cos(degree),-(float)(Math.sin(degree)),0,0},{(float)Math.sin(degree),(float)Math.cos(degree),0,0},{0,0,1,0},{0,0,0,1}};              
        }
        FloatMatrix tMat2 = new FloatMatrix(tMat);
        return tMat2.mmul(mat);
	}
	
	public static void printMat(FloatMatrix mat) {
		for(int r=0;r<mat.rows;r++) {
			for(int c=0;c<mat.columns;c++) {
				System.out.print(mat.get(r,c) + " ");				
			}
			System.out.println();
		}
		System.out.println();
	}
}
