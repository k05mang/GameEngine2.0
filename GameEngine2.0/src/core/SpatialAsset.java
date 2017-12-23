package core;

import glMath.transforms.Transform;
import glMath.vectors.Vec3;

public abstract class SpatialAsset /*implements Cloneable*/{
	
	protected Transform transforms;
	
	/**
	 * Constructs a SpatialAsset
	 */
	public SpatialAsset(){
		transforms = new Transform();
	}
	
	/**
	 * Constructs a SpatialAsset with the given asset as a source to copy from
	 * 
	 * @param copy SpatialAsset whose data to copy
	 */
	public SpatialAsset(SpatialAsset copy, boolean copyListeners){
		transforms = new Transform(copy.transforms, copyListeners);
	}
	
	/**
	 * Transforms this spatial objects by the given Transform
	 * 
	 * @param trans Transform to modify this spatial object
	 */
	public void transform(Transform trans){
		transforms.transform(trans);
	}
	
	/**
	 * Sets this spatial objects Transform to the given transform
	 * 
	 * @param trans Transform to set this spatial objects Transform to
	 */
	public void setTransform(Transform trans){
		transforms.set(trans);
	}
	
	/**
	 * Gets this spatial objects transformation
	 * 
	 * @return Transform of this spatial object
	 */
	public Transform getTransform(){
		return transforms;
	}
	
	/**
	 * Gets the central position of the SpatialAsset
	 * 
	 * @return Central position of the SpatialAsset
	 */
	public Vec3 getPos(){
		return transforms.getTranslation();
	}
//	
//	@Override
//	public abstract SpatialAsset clone();
}
