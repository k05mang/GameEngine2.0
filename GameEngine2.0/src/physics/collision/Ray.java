package physics.collision;

import glMath.VecUtil;
import glMath.transforms.Transform;
import glMath.vectors.Vec3;
import core.SpatialAsset;

public class Ray extends SpatialAsset implements Cloneable{

	private Vec3 direction;
	private float length;

	/**
	 * Constructs a Ray given a {@code length}, that determines the length of the ray, 
	 * {@code start}, the starting point of the ray as an (x,y,z) vector, and {@code direction},
	 * the direction of the Ray from the {@code start} point extending a distance of {@code length}
	 * 
	 * @param length Length of the Ray
	 * @param start Starting point of the Ray
	 * @param direction Direction the Ray extends in
	 */
	public Ray(float length, Vec3 start, Vec3 direction){
		this(length, start.x, start.y, start.z, direction.x, direction.y, direction.z);
	}
	
	/**
	 * Constructs a Ray given a {@code length}, that determines the length of the ray, 
	 * {@code start}, the starting point of the ray as an (x,y,z) vector, and {@code dirx, diry, dirz},
	 * the direction of the Ray from the {@code start} point extending a distance of {@code length}
	 * 
	 * @param length Length of the Ray
	 * @param start Starting point of the Ray
	 * @param dirx X component of the direction vector of the Ray
	 * @param diry Y component of the direction vector of the Ray
	 * @param dirz Z component of the direction vector of the Ray
	 */
	public Ray(float length, Vec3 start, float dirx, float diry, float dirz){
		this(length, start.x, start.y, start.z, dirx, diry, dirz);
	}

	/**
	 * Constructs a Ray given a {@code length}, that determines the length of the ray, 
	 * {@code x, y, z}, the starting point of the ray, and {@code direction},
	 * the direction of the Ray from the {@code start} point extending a distance of {@code length}
	 * 
	 * @param length Length of the Ray
	 * @param x X component of the start point of the Ray
	 * @param y Y component of the start point of the Ray
	 * @param z Z component of the start point of the Ray
	 * @param direction Direction the Ray extends in
	 */
	public Ray(float length, float x, float y, float z, Vec3 direction){
		this(length, x, y, z, direction.x, direction.y, direction.z);
	}
	
	/**
	 * Constructs a Ray given a {@code length}, that determines the length of the ray, 
	 * {@code x, y, z}, the starting point of the ray, and {@code dirx, diry, dirz},
	 * the direction of the Ray from the {@code start} point extending a distance of {@code length}
	 * 
	 * @param length Length of the Ray
	 * @param x X component of the start point of the Ray
	 * @param y Y component of the start point of the Ray
	 * @param z Z component of the start point of the Ray
	 * @param dirx X component of the direction vector of the Ray
	 * @param diry Y component of the direction vector of the Ray
	 * @param dirz Z component of the direction vector of the Ray
	 */
	public Ray(float length, float x, float y, float z, float dirx, float diry, float dirz){
		super();
		this.length = Math.abs(length);
		transforms.translate(x, y, z);
		direction = new Vec3(dirx, diry, dirz).normalize();
	}

	/**
	 * Constructs a copy of the given Ray
	 * 
	 * @param copy Ray whose data to copy into this Ray
	 */
	public Ray(Ray copy) {
		super(copy, false);
		direction = new Vec3(copy.direction);
		length = copy.length;
	}
	
	/**
	 * Gets the length of this Ray
	 * 
	 * @return Length of the Ray
	 */
	public float getLength(){
		return length;
	}
	
	/**
	 * Sets the length of the Ray, the absolute value of {@code length} will be used in the event of a negative value
	 * 
	 * @param length New length of the Ray, taken as a magnitude, ignoring sign
	 */
	public void setLength(float length){
		this.length = Math.abs(length);
	}
	
	/**
	 * Gets the direction this Ray is extending in
	 * 
	 * @return Direction vector this Ray is currently extending in, this vector is adjusted based on transformations applied
	 * to this Ray
	 */
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
		return VecUtil.add(transforms.getTranslation(), VecUtil.scale(transforms.getOrientation().multVec(direction), length*t));
//		return VecUtil.add(transforms.getTranslation(), VecUtil.scale(transforms.getOrientation().multVec(direction), length*Math.min(1, Math.max(0,t))));
	}
	
	@Override
	public Ray clone(){
		return new Ray(this);
	}

	@Override
	public void transform(Transform transform){
		transforms.translate(transform);
		transforms.rotate(transform);
	}
}
