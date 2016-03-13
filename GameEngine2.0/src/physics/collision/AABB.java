package physics.collision;

import glMath.MatrixUtil;
import glMath.Quaternion;
import glMath.Transform;
import glMath.matrices.Mat4;
import glMath.vectors.Vec3;

/**
 * AABB is an Axis Aligned Bounding Box (AABB) used in collision detection.
 * @author Kevin Mango
 *
 */
public class AABB extends CollisionMesh{
	
	private Vec3 halfDimensions;
	
	/**
	 * Constructs an AABB with 1 as the dimension of the AABB
	 */
	public AABB(){
		this(1,1,1);
	}
	
	/**
	 * Constructs an AABB with the given dimensions stored in a vector
	 * 
	 * @param dimensions Vector containing the dimensions of the AABB
	 */
	public AABB(Vec3 dimensions){
		this(dimensions.x, dimensions.y, dimensions.z);
	}
	
	/**
	 * Constructs an AABB with the given scale as the dimensions of the AABB
	 * along each of the axis
	 * 
	 * @param scale Scale to set each dimension of the AABB to
	 */
	public AABB(float scale){
		this(scale, scale, scale);
	}
	
	/**
	 * Constructs an AABB with the given {@code width}, {@code height}, and {@code depth} as dimensions
	 * 
	 * @param width Width along the x dimension of this AABB
	 * @param height Height along the y dimension of this AABB
	 * @param depth Depth along the z dimension of this AABB
	 */
	public AABB(float width, float height, float depth){
		super();
		halfDimensions = new Vec3(width/2.0f, height/2.0f, depth/2.0f);
	}
	
	/**
	 * Constructs an AABB with the given AABB as a source to copy from
	 * 
	 * @param copy AABB to copy data from in the construction of this AABB
	 */
	public AABB(AABB copy) {
		super(copy);
		halfDimensions = new Vec3(copy.halfDimensions);
	}
	
	@Override
	public void transform(Transform transform){
		transforms.setTranslation(transform);
		transforms.setScale(transform);
	}
	
	@Override
	public void setTransform(Transform trans){
		transforms.setTranslation(trans);
		transforms.setScale(trans);
	}
	
	@Override
	public Vec3 support(Vec3 direction){
		//make the point 
		Vec3 point = new Vec3(
				Math.copySign(halfDimensions.x, direction.x),
				Math.copySign(halfDimensions.y, direction.y),
				Math.copySign(halfDimensions.z, direction.z)
				);
		//transform the point
		//translate
		point.add(transforms.getTranslation());
		//scale
		Vec3 scalars = transforms.getScalars();
		point.set(point.x*scalars.x, point.y*scalars.y, point.z*scalars.z);
		return point;
	}
}
