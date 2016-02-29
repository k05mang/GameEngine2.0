package core;

import glMath.Transform;

public interface SpatialAsset {
	
	/**
	 * Transforms this spatial objects by the given Transform
	 * 
	 * @param trans Transform to modify this spatial object
	 */
	public void transform(Transform trans);
	
	/**
	 * Sets this spatial objects Transform to the given transform
	 * 
	 * @param trans Transform to set this spatial objects Transform to
	 */
	public void setTransform(Transform trans);
	
	/**
	 * Gets this spatial objects transformation
	 * 
	 * @return Transform of this spatial object
	 */
	public Transform getTransform();
}
