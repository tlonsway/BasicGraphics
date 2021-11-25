package Game.Graphics;

import org.jblas.*;
import org.jblas.Geometry.*;

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
	
	public static FloatMatrix rotatePoint(FloatMatrix point, char c, float degree) {
		if (point.getRows() == 3) {
			point = new FloatMatrix(new float[] {point.get(0),point.get(1),point.get(2),1});
		}
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
        return tMat2.mmul(point);
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
	
	public static FloatMatrix crossProd(FloatMatrix vec1, FloatMatrix vec2) {
		float[] v1 = vec1.data;
		float[] v2 = vec2.data;
		float iVal = (v1[1]*v2[2])-(v2[1]*v1[2]);
		float jVal = (v1[0]*v2[2])-(v2[0]*v1[2]);
		float kVal = (v1[0]*v2[1])-(v2[0]*v1[1]);
		return new FloatMatrix(new float[] {iVal,jVal,kVal});
	}
	
	public static FloatMatrix lookAt(FloatMatrix eye, FloatMatrix at, FloatMatrix up) {
		//System.out.println("Position: " + position + ", Target: " + target);
		FloatMatrix Z = at.rsub(eye);
		//FloatMatrix Z = eye.rsub(at);
		Z = Geometry.normalize(Z);
		FloatMatrix Y = up;
		FloatMatrix X = crossProd(Y,Z);
		X = Geometry.normalize(X);
		Y = crossProd(Z,X);
		
		//Y = Geometry.normalize(Y);
		float[][] ret = new float[][] {{X.get(0),X.get(1),X.get(2),-X.dot(eye)},
									   {Y.get(0),Y.get(1),Y.get(2),-Y.dot(eye)},
									   {Z.get(0),Z.get(1),Z.get(2),-Z.dot(eye)},
									   {0,0,0,1}};
		/*float[][] ret = new float[][] {{X.get(0),Y.get(0),Z.get(0),0},
									   {X.get(1),Y.get(1),Z.get(1),0},
									   {X.get(2),Y.get(2),Z.get(2),0},
									   {-X.dot(eye),-Y.dot(eye),-Z.dot(eye),1}};*/
		
		return new FloatMatrix(ret);
	}
	
	/*
	public static void main(String[] args) {
		//test look at method
		FloatMatrix pos = new FloatMatrix(new float[] {5,5,5});
		FloatMatrix target = new FloatMatrix(new float[] {0,0,1});
		FloatMatrix up = new FloatMatrix(new float[] {0,1,0});
		FloatMatrix transform = lookAt(pos,target,up);
		System.out.println(transform);
	}
	*/
	
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
