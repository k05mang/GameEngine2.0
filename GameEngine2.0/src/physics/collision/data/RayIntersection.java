package physics.collision.data;

import glMath.vectors.Vec3;
import physics.collision.CollisionMesh;
import physics.collision.Ray;

public class RayIntersection extends CollisionData {
	private float entryDepth, exitDepth;
	
	public RayIntersection(Ray ray, CollisionMesh objB, boolean areColliding, float depthEnter, float depthExit) {
		super(ray, objB, areColliding);
		entryDepth = depthEnter;
		exitDepth = depthExit;
	}
	
	@Override
	public Ray getColliderA(){
		return (Ray)objA;
	}
	
	@Override
	public CollisionMesh getColliderB(){
		return (CollisionMesh)objB;
	}
	
	public float getDepthEntered(){
		return entryDepth;
	}
	
	public float getDepthExited(){
		return exitDepth;
	}
	
	public Vec3 getPointEntered(){
		return ((Ray)objA).getPoint(entryDepth);
	}
	
	public Vec3 getPointExited(){
		return ((Ray)objA).getPoint(exitDepth);
	}
}
