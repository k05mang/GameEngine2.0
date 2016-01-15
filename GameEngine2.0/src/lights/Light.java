package lights;

import glMath.Transform;
import glMath.vectors.Vec3;
import mesh.Renderable;

public abstract class Light {
	
	protected Vec3 color;
	protected Transform trans;
	protected float intensity;
	protected static final int VOLUME_FINENESS = 10;
	//TODO attenuation

	public Light(Vec3 position, Vec3 color, float intensity){
		this(position.x, position.y, position.z, color.x, color.y, color.z, intensity);
	}
	
	public Light(float xpos, float ypos, float zpos, Vec3 color, float intensity){
		this(xpos, ypos, zpos, color.x, color.y, color.z, intensity);
	}
	
	public Light(Vec3 position, float r, float g, float b, float intensity){
		this(position.x, position.y, position.z, r, g, b, intensity);
	}
	
	public Light(float xpos, float ypos, float zpos, float r, float g, float b, float intensity){
		color = new Vec3(r, g, b);
		trans = new Transform();
		trans.setPos(xpos, ypos, zpos);
		this.intensity = intensity;
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
	
	public float getIntensity(){
		return intensity;
	}
	
	public void setIntensity(float intensity){
		this.intensity = intensity;
	}
	
	public abstract Renderable getVolume();
}
