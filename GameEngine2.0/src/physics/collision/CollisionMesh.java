package physics.collision;

import glMath.vectors.Vec3;
import core.SpatialAsset;

public abstract class CollisionMesh extends SpatialAsset{
	
	/**
	 * Constructs a CollisionMesh
	 */
	public CollisionMesh(){
		super();
	}
	
	/**
	 * Constructs a CollisionMesh with the given CollisionMesh as a source for data
	 * 
	 * @param copy CollisionMesh whose data to copy in the construction of this CollisionMesh
	 */
	public CollisionMesh(CollisionMesh copy){
		super(copy);
	}
	
	/**
	 * Creates a copy of this instance of the CollisionMesh
	 * 
	 * @return New instance that is an identical copy of this CollisionMesh
	 */
	public abstract CollisionMesh copy();
	
	/**
	 * Gets the farthest point on the mesh in the given direction.
	 * 
	 * @param direction Direction to sample a point from on the mesh
	 * 
	 * @return Point on the mesh in the direction the given vector
	 */
	public abstract Vec3 support(Vec3 direction);
}
