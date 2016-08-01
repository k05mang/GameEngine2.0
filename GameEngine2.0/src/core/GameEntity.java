package core;

import mesh.Mesh;
import physics.collision.CollisionMesh;

public class GameEntity {

	private Mesh mesh;
	private CollisionMesh collider;
	
	public GameEntity(Mesh mesh){
		this.mesh = mesh;
	}
	
	public GameEntity(Mesh mesh, CollisionMesh collider){
		this.mesh = mesh;
		this.collider = collider.copy();
	}
}
