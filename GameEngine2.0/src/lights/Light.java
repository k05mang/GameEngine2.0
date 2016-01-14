package lights;

import glMath.Transform;
import glMath.vectors.Vec3;

public abstract class Light {
	
	protected Vec3 color;
	protected Transform trans;
	protected static final int VOLUME_FINENESS = 10;
	//TODO attenuation

	public Light(Vec3 position, Vec3 color){
		this(position.x, position.y, position.z, color.x, color.y, color.z);
	}
	
	public Light(float xpos, float ypos, float zpos, Vec3 color){
		this(xpos, ypos, zpos, color.x, color.y, color.z);
	}
	
	public Light(Vec3 position, float r, float g, float b){
		this(position.x, position.y, position.z, r, g, b);
	}
	
	public Light(float xpos, float ypos, float zpos, float r, float g, float b){
		color = new Vec3(r, g, b);
		trans = new Transform();
		trans.setPos(xpos, ypos, zpos);
	}
	
	public Vec3 getPos(){
		return trans.getPosition();
	}
	
	public Vec3 getColor(){
		return color;
	}
	
	public void setColor(Vec3 newColor){
		color.set(newColor);
	}
	
	public void setColor(float r, float g, float b){
		color.set(r, g, b);
	}
	
	public void transform(Transform trans){
		trans.transform(trans);
	}
	
	public Transform getTransform(){
		return trans;
	}
}
