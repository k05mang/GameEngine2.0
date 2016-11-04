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
	
	/**
	 * Constructs an Entity object with the given {@code mesh} as this Entities render Mesh. This Entity will initially have
	 * no CollisionMesh associated with it. This constructor is the equivalent of calling {@code Entity(Mesh mesh, CollisionMesh mesh)}
	 * with the value for {@code mesh} equal to {@code null}.
	 * 
	 * @param mesh Renderable mesh to be used with this Entity
	 */
	public Entity(Mesh mesh){
		this(mesh, null);
	}
	
	/**
	 * Constucts an Entity object with the given {@code mesh} as this Entities render Mesh. If the {@code autoGenHull} is true
	 * then this Entity will attempt to construct a ConvexHull to use with the given mesh. If the hull fails to generate when
	 * one if requested then the default CollisionMesh used is an AABB encapsulating the mesh. If autoGenHull is false then there
	 * will be no CollisionMesh associated with this Entity.
	 * 
	 * @param mesh Renderable mesh to be associated with this Entity
	 * @param autoGenHull Boolean requesting that the system automatically generate a CollisionMesh for the given Mesh
	 */
	public Entity(Mesh mesh, boolean autoGenHull){
		super();
		if(mesh != null){
			this.mesh = mesh;
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
	}
	
	/**
	 * Constructs an Entity with the given {@code mesh} as the renderable mesh, and the given {@code collider} as this Entities
	 * CollisionMesh. If the collider is not {@code null}, then a copy of the collider is made and used by the Entity.
	 * 
	 * @param mesh Renderable mesh to be associated with this Entity
	 * @param collider CollisionMesh to use with this Entity
	 */
	public Entity(Mesh mesh, CollisionMesh collider){
		super();
		this.mesh = mesh;
		if(mesh != null){
			this.transforms.set(this.mesh.getTransform());
		}
		if(collider != null){
			this.collider = collider.copy();
			this.collider.setTransform(this.transforms);
		}
	}
	
	/**
	 * Sets the renderable mesh object for this entity
	 * 
	 * @param mesh Mesh object to use in rendering this entity
	 */
	public void set(Mesh mesh){
		this.mesh = mesh;
		//set this Entities transformations to the new mesh if the new mesh exists
		if(mesh != null){
			this.transforms.set(this.mesh.getTransform());
			if(collider != null){
				this.collider.setTransform(this.transforms);
			}
		}
	}
	
	/**
	 * Sets the collision mesh for this given entity
	 * 
	 * @param collider {@code CollisionMesh} object to use in collision detection algorithms
	 */
	public void set(CollisionMesh collider){
		if(collider != null){
			this.collider = collider.copy();//copy the collision mesh
			this.collider.setTransform(this.transforms);//align its transformations with this Entity
		}else{
			this.collider = null;
		}
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
		//if the collision mesh was successfully generated then proceed to transform it to match the Entity
		if(collider != null){
			collider.setTransform(this.transforms);
		}
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
