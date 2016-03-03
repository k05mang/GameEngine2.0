package physics.collision;

import glMath.MatrixUtil;
import glMath.Quaternion;
import glMath.matrices.Mat4;
import glMath.vectors.Vec3;

public class AABB extends CollisionMesh{
	
	private Vec3 halfDimensions;
	
	public AABB(){
		this(1,1,1);
	}
	
	public AABB(Vec3 dimensions){
		this(dimensions.x, dimensions.y, dimensions.z);
	}
	
	public AABB(float scale){
		this(scale, scale, scale);
	}
	
	public AABB(float width, float height, float length){
		super();
		halfDimensions = new Vec3(width/2.0f,height/2.0f,length/2.0f);
	}
	
	public AABB(AABB copy) {
		super(copy);
		halfDimensions = new Vec3(copy.halfDimensions);
	}
	
	public boolean colliding(AABB collider){
		Vec3 thisCenter = getPos();
		Vec3 colliderCenter = collider.getPos();
		//max x value of this < min x of collider or min x value of this < max x value of collider
		boolean x = thisCenter.x+halfDimensions.x > colliderCenter.x-collider.halfDimensions.x //case where this is left of collider
				&&
				thisCenter.x-halfDimensions.x < colliderCenter.x+collider.halfDimensions.x;//case where this is right of collider
				
		//max y value of this < min y of collider or min y value of this < max y value of collider
		boolean y = thisCenter.y+halfDimensions.y > colliderCenter.y-collider.halfDimensions.y //case where this is below of collider
				&&
				thisCenter.y-halfDimensions.y < colliderCenter.y+collider.halfDimensions.y;//case where this is above of collider
				
		//max z value of this < min z of collider or min z value of this < max z value of collider
		boolean z = thisCenter.z+halfDimensions.z > colliderCenter.z-collider.halfDimensions.z //case where this is behind of collider
				&&
				thisCenter.z-halfDimensions.z < colliderCenter.z+collider.halfDimensions.z;//case where this is front of collider
				
		//all three must be true for the boxes to be colliding
		return x && y && z;
	}
	
//	@Override
//	public Vec3 support(Vec3 direction){
//		
//		return (Vec3)modelMat.multVec(new Vec3(
//				Math.copySign(halfDimensions.x, direction.x),
//				Math.copySign(halfDimensions.y, direction.y),
//				Math.copySign(halfDimensions.z, direction.z)
//				)).swizzle("xyz");
//	}
}
