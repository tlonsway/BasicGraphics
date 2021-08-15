package game;

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
	
	public static FloatMatrix rotateMat(FloatMatrix mat, char c, float degree, boolean flip) {
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
        if (!flip) {
        	return tMat2.mmul(mat);
        } else {
        	return mat.mmul(tMat2);
        }
	}
	
	public static FloatMatrix rotateMatAll(FloatMatrix mat, float degX, float degY, float degZ) {
		float sX = (float)Math.sin(degX);
		float sY = (float)Math.sin(degY);
		float sZ = (float)Math.sin(degZ);
		float cX = (float)Math.cos(degX);
		float cY = (float)Math.cos(degY);
		float cZ = (float)Math.cos(degZ);
		float[][] tMat = new float[][] {{cY*cZ,cY*sZ,-sY,0},
										{(sX*sY*cZ)-(cX*sZ),(sX*sY*sZ)+(cX*cZ),sX*cY,0},
										{(cX*sY*cZ)+(sX*sZ),(cX*sY*sZ)-(sX*cZ),cX*cY,0},
										{0,0,0,1}};
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
