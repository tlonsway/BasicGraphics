package game;

import org.jblas.*;

public class AABB {
	public float minX,minY,minZ;
	public float maxX,maxY,maxZ;
	float[] lowestPoint; //this is the point on the object with the lowest height
	GameObject object;
	Camera cam;
	
	public AABB(float[] minP, float[] maxP) {
		minX=minP[0];minY=minP[1];minZ=minP[2];
		maxX=maxP[0];maxY=maxP[1];maxZ=maxP[2];
		this.object = null;
		cam = null;
	}
	
	public AABB(float[] minP, float[] maxP, GameObject attachedObject) {
		minX=minP[0];minY=minP[1];minZ=minP[2];
		maxX=maxP[0];maxY=maxP[1];maxZ=maxP[2];
		this.object = attachedObject;
		cam = null;
	}
	
	public void setObject(GameObject go) {
		object = go;
	}
	
	public void setCamera(Camera cam) {
		this.cam = cam;
	}
	
	
	
	public boolean containsPoint(float[] point) {
		if (point[0] >= minX && point[0] <= maxX && point[1] >= minY && point[1] <= maxY && point[2] >= minZ && point[2] <= maxZ) {
			return true;
		}
		return false;
	}
	public boolean intersectsAABB(AABB oBox) {
		float[] tpos = new float[0];
		if (cam != null) {
			tpos = cam.getCamPos();
			tpos = new float[] {-tpos[0],-tpos[1],-tpos[2]};
			
		} else if (object != null) {
			tpos = object.getPosition();
		} else {
			System.err.println("AABB not attached to GameObject or Camera, will not work");
			return false;
		}
		
		if (oBox.getGameObject() == null) {
			//float[] tpos = object.getPosition();
			float tx = tpos[0]; float ty = tpos[1]; float tz = tpos[2];
			//System.out.print("Coords of object bbox: MIN(" + (this.minX+tx) + "," + (this.minY+ty) + "," + (this.minZ+tz) + ") MAX(" + (this.maxX+tx) + "," + (this.maxY+ty) + "," + (this.maxZ+tz) + ")");
			//System.out.println(" Coords of other AABB: MIN(" + oBox.minX + "," + oBox.minY + "," + oBox.minZ + ") MAX(" + oBox.maxX + "," + oBox.maxY + "," + oBox.maxZ + ")");
			if (oBox.maxX >= this.minX+tx && this.maxX+tx >= oBox.minX && oBox.maxY >= this.minY+ty && this.maxY+ty >= oBox.minY && oBox.maxZ >= this.minZ+tz && this.maxZ+tz >= oBox.minZ) {
				return true;
			}
			return false;
		} else {
			//float[] tpos = object.getPosition();
			float tx = tpos[0]; float ty = tpos[1]; float tz = tpos[2];
			float[] opos = oBox.getGameObject().getPosition();
			float ox = opos[0]; float oy = opos[1]; float oz = opos[2];
			if (oBox.maxX+ox >= this.minX+tx && this.maxX+tx >= oBox.minX+ox && oBox.maxY+oy >= this.minY+ty && this.maxY+ty >= oBox.minY+oy && oBox.maxZ+oz >= this.minZ+tz && this.maxZ+tz >= oBox.minZ+oz) {
				return true;
			}
			return false;
		}
	}
	public boolean intersectsMesh(Mesh m) {
		for(Polygon p : m.getPolygons()) {
			if(this.intersectsPolygon(p)) {
				//System.out.println("MESH INTERSECTION DETECTED");
				return true;
			}
		}
		return false;
	}
	public boolean intersectsPolygon(Polygon p) {
		//this is a shortcut, approximates the polygon with an AABB, will not work for steep polygons
		FloatMatrix[] points = p.getPoints();
		float[] pMinT = new float[] {points[0].get(0),points[0].get(1),points[0].get(2)};
		float[] pMaxT = new float[] {points[0].get(0),points[0].get(1),points[0].get(2)};
		for(FloatMatrix point : points) {
			float pXT = point.get(0);
			float pYT = point.get(1);
			float pZT = point.get(2);
			if(pXT < pMinT[0]) {
				pMinT[0] = pXT;
			}
			if(pXT > pMaxT[0]) {
				pMaxT[0] = pXT;
			}
			
			if(pYT < pMinT[1]) {
				pMinT[1] = pYT;
			}
			if(pYT > pMaxT[1]) {
				pMaxT[1] = pYT;
			}
			
			if(pZT < pMinT[2]) {
				pMinT[2] = pZT;
			}
			if(pZT > pMaxT[2]) {
				pMaxT[2] = pZT;
			}
			//System.out.println("Max X-cord: " + pMaxT[0]);
		}
		AABB polyBox = new AABB(pMinT,pMaxT);
		//System.out.println("Checking intersection");
		if (this.intersectsAABB(polyBox)) {
			return true;
		}
		return false;
	}
	public float getXWidth() {
		return maxX-minX;
	}
	
	public float getYHeight() {
		return maxY-minY;
	}
	
	public float getZLength() {
		return maxZ-minZ;
	}
	public GameObject getGameObject() {
		return object;
	}
	
}
