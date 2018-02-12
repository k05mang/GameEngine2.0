package physics.collision.data;

import java.time.LocalDateTime;

import core.SpatialAsset;

public class CollisionData {
	protected boolean areColliding;
	protected SpatialAsset objA, objB;
	protected LocalDateTime timeStamp;//tracks the time this collision data was made
	
	public CollisionData(SpatialAsset objA, SpatialAsset objB, boolean areColliding){
		this.objA = objA;
		this.objB = objB;
		this.areColliding = areColliding;
		timeStamp = LocalDateTime.now();
	}
	
	public boolean areColliding(){
		return areColliding;
	}
	
	public SpatialAsset getColliderA(){
		return objA;
	}
	
	public SpatialAsset getColliderB(){
		return objB;
	}
	
	public LocalDateTime getTimeStamp(){
		return timeStamp;
	}
}
