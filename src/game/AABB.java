package game;

public class AABB {
	public float minX,minY,minZ;
	public float maxX,maxY,maxZ;
	
	public AABB(float[] minP, float[] maxP) {
		minX=minP[0];minY=minP[1];minZ=minP[2];
		maxX=maxP[0];maxY=maxP[1];maxZ=maxP[2];
	}
	
	public boolean containsPoint(float[] point) {
		if (point[0] >= minX && point[0] <= maxX && point[1] >= minY && point[1] <= maxY && point[2] >= minZ && point[2] <= maxZ) {
			return true;
		}
		return false;
	}
	public boolean intersectsAABB(AABB oBox) {
		if (oBox.maxX >= this.minX && this.maxX >= oBox.minX && oBox.maxY >= this.minY && this.maxY >= oBox.minY && oBox.maxZ >= this.minZ && this.maxZ >= oBox.minZ) {
			return true;
		}
		return false;
	}
}
