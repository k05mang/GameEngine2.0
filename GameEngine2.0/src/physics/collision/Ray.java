package physics.collision;

import glMath.Transform;
import glMath.vectors.Vec3;
import core.SpatialAsset;

public class Ray extends SpatialAsset {

	private Vec3 direction;
	private float length;

	public Ray(float length, Vec3 start, Vec3 direction){
		this(length, start.x, start.y, start.z, direction.x, direction.y, direction.z);
	}
	
	public Ray(float length, Vec3 start, float dirx, float diry, float dirz){
		this(length, start.x, start.y, start.z, dirx, diry, dirz);
	}

	public Ray(float length, float x, float y, float z, Vec3 direction){
		this(length, x, y, z, direction.x, direction.y, direction.z);
	}
	
	public Ray(float length, float x, float y, float z, float dirx, float diry, float dirz){
		super();
		this.length = Math.abs(length);
		transforms.translate(x, y, z);
		direction = new Vec3(dirx, diry, dirz).normalize();
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
		return transforms.getOrientation().multVec(direction);
	}

	@Override
	public void transform(Transform transform){
		transforms.translate(transform);
		transforms.rotate(transform);
	}
}
