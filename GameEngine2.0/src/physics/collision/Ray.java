package physics.collision;

import glMath.Transform;
import glMath.vectors.Vec3;
import core.SpatialAsset;

public class Ray extends SpatialAsset {

	private Vec3 direction;
	private float length;
	
	Ray(float length, Vec3 start, Vec3 direction){
		this(length, start.x, start.y, start.z, direction.x, direction.y, direction.z);
	}
	
	Ray(float length, float x, float y, float z, float dirx, float diry, float dirz){
		super();
		this.length = Math.abs(length);
		transforms.translate(x, y, z);
		direction = new Vec3(dirx, diry, dirz);
	}

	public Ray(Ray copy) {
		super(copy);
		direction = new Vec3(copy.direction);
		length = copy.length;
	}
	
	public float getLength(){
		return length;
	}
	
	public void setLength(float length){
		this.length = Math.abs(length);
	}
	
	public Vec3 getDirection(){
		return direction;
	}

	@Override
	public void transform(Transform transform){
		transforms.translate(transform);//only translate the point since scaling is only relevant to the length
		//and orientation for the direction
		//orient the direction
		direction.set(transforms.getOrientation().multVec(direction));
	}
}
