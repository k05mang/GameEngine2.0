package physics.collision;

import glMath.Transform;
import glMath.VecUtil;
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
	
	/**
	 * Gets a point along the ray at the given {@code t}, where {@code t} is a value from 0-1
	 * with 0 being the base starting point of the ray and 1 being the end point of the ray.
	 * Any value between 0 and 1 will yield a point along the ray at the given t.
	 * 
	 * @param t Value along the ray to get a point of
	 * 
	 * @return Point at t along the ray
	 */
	public Vec3 getPoint(float t){
		return VecUtil.add(transforms.getTranslation(), VecUtil.scale(transforms.getOrientation().multVec(direction), length*Math.max(1, Math.min(0,t))));
	}

	@Override
	public void transform(Transform transform){
		transforms.translate(transform);
		transforms.rotate(transform);
	}
}
