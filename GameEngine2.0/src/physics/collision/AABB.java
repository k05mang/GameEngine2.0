package physics.collision;

import glMath.transforms.Transform;
import glMath.vectors.Vec3;
import mesh.Geometry;
import mesh.Mesh;

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
	
	public AABB(Mesh mesh){
		this(mesh.getGeometry());
	}
	
	public AABB(Geometry mesh){
		super();
		halfDimensions = new Vec3(
				(mesh.getVertex(mesh.getMinMaxIndex(Geometry.MAX_X)).getPos().x-mesh.getVertex(mesh.getMinMaxIndex(Geometry.MIN_X)).getPos().x)/2.0f, 
				(mesh.getVertex(mesh.getMinMaxIndex(Geometry.MAX_Y)).getPos().y-mesh.getVertex(mesh.getMinMaxIndex(Geometry.MIN_Y)).getPos().y)/2.0f, 
				(mesh.getVertex(mesh.getMinMaxIndex(Geometry.MAX_Z)).getPos().z-mesh.getVertex(mesh.getMinMaxIndex(Geometry.MIN_Z)).getPos().z)/2.0f);
		//translate the AABB to the geometric center of the mesh, this way it is aligned to the vertex data
		transforms.translate(mesh.getGeometricCenter());
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
		halfDimensions = new Vec3(Math.abs(width)/2.0f, Math.abs(height)/2.0f, Math.abs(depth)/2.0f);
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
	
	/**
	 * Gets the current half dimensions of the bounding box
	 * 
	 * @return A Vec3 containing the half dimensions of the bounding box, each vector component 
	 * corresponds to the dimension along that axis
	 */
	public Vec3 getHalfDimensions(){
		Vec3 scalars = transforms.getScalars();
		return new Vec3(halfDimensions.x*scalars.x, halfDimensions.y*scalars.y, halfDimensions.z*scalars.z);
	}
	
	@Override
	public void transform(Transform transform){
		transforms.translate(transform);
		transforms.scale(transform);
	}
	
	@Override
	public void setTransform(Transform trans){
		transforms.setTranslation(trans);
		transforms.setScale(trans);
	}
	
	@Override
	public CollisionMesh clone(){
		return new AABB(this);
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
		//scale
		Vec3 scalars = transforms.getScalars();
		point.set(point.x*scalars.x, point.y*scalars.y, point.z*scalars.z);
		//translate
		point.add(transforms.getTranslation());
		return point;
	}
}
