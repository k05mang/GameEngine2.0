package glMath.transforms;

import glMath.Quaternion;

public interface TransformListener {
	
	public void scaled(float x, float y, float z);
	
	public void translated(float x, float y, float z);
	
	public void rotated(Quaternion rotation);
}
