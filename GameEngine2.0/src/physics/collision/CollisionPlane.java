package physics.collision;

import glMath.Transform;
import glMath.vectors.Vec2;
import glMath.vectors.Vec3;

public class CollisionPlane extends CollisionMesh{
	
	private Vec3 normal;
	private Vec2 halfDimensions;
	
	public CollisionPlane(float width, float length){
		super();
		halfDimensions = new Vec2(width/2.0f, length/2.0f);
		normal = new Vec3(0,1,0);
	}
	
	public CollisionPlane(float sideLength){
		this(sideLength, sideLength);
	}
	
	public CollisionPlane(CollisionPlane copy) {
		super(copy);
		halfDimensions = new Vec2(copy.halfDimensions);
		normal = new Vec3(copy.normal);
	}
	
	public Vec3 getNormal(){
		return transforms.getOrientation().multVec(normal);
	}
	
	public Vec2 getHalfDimensions(){
		Vec3 scalars = transforms.getScalars();
		return new Vec2(halfDimensions.x*scalars.x, halfDimensions.y*scalars.z);
	}
	
	@Override
	public void transform(Transform trans){
		super.transform(trans);//perform transformations as usual
	}

	@Override
	public Vec3 support(Vec3 direction){
		return null;
	}
}
