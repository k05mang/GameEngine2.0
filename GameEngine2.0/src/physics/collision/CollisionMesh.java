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
	
	public abstract Vec3 support(Vec3 direction);
}
