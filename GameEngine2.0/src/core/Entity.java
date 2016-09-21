package core;

import glMath.Transform;
import glMath.vectors.Vec3;
import mesh.Mesh;
import mesh.primitives.geometry.Cube;
import physics.collision.AABB;
import physics.collision.CollisionMesh;
import physics.collision.ConvexHull;

public class Entity extends SpatialAsset{

	private Mesh mesh;
	private CollisionMesh collider;
	
	public Entity(Mesh mesh){
		this(mesh, false);
	}
	
	public Entity(Mesh mesh, boolean autoGenHull){
		super();
		if(mesh != null){
			this.mesh = mesh;
		}else{
			this.mesh = new Cube(10);
		}
		//check if a collision mesh needs to be auto generated
		if(autoGenHull){
			CollisionMesh convexHull = ConvexHull.get(mesh);
			//check for the case in which the convex hull generation fails
			if(convexHull == null){
				this.collider = new AABB(mesh);
			}else{
				this.collider = convexHull;
			}
		}else{
			this.collider = null;
		}
		//establish a uniform transformation between all the objects and the Entity, with the mesh as the base transformation
		this.transforms.set(this.mesh.getTransform());
		this.collider.setTransform(this.mesh.getTransform());
	}
	
	public Entity(Mesh mesh, CollisionMesh collider){
		super();
		if(mesh != null){
			this.mesh = mesh;
		}else{
			this.mesh = new Cube(10);
		}
		this.collider = collider;
	}
	
	/**
	 * Sets the renderable mesh object for this entity
	 * 
	 * @param mesh Mesh object to use in rendering this entity
	 */
	public void set(Mesh mesh){
		if(mesh != null){
			this.mesh = mesh;
		}
	}
	
	/**
	 * Sets the collision mesh for this given entity
	 * 
	 * @param collider {@code CollisionMesh} object to use in collision detection algorithms
	 */
	public void set(CollisionMesh collider){
		this.collider = collider;
	}
	
	/**
	 * Determines if this entity has a valid collision mesh assigned to it
	 * 
	 * @return False if the collision mesh it null, true otherwise
	 */
	public boolean hasCollisionMesh(){
		return collider != null;
	}
	
	/**
	 * Attempts to generate a convex hull from the currently assigned mesh object
	 * 
	 * @return True if hull generation was successful, false otherwise
	 */
	public boolean genHull(){
		collider = ConvexHull.get(mesh);
		return collider != null;
	}
	
	/**
	 * Gets the CollisionMesh currently being used by this Entity, this CollisionMesh will have 
	 * the same transformations as this Entity
	 * 
	 * @return CollisionMesh or null if none was set to this Entity
	 */
	public CollisionMesh getCollider(){
		if(collider != null){
			collider.setTransform(transforms);
		}
		return collider;
	}
	
	/**
	 * Gets this Entity's renderable mesh object, this Mesh will have 
	 * the same transformations as this Entity
	 * 
	 * @return The renderable Mesh object assigned to this Entity
	 */
	public Mesh getMesh(){
		mesh.setTransform(transforms);
		return mesh;
	}
}
