package lights;

import glMath.Quaternion;
import glMath.Transform;
import glMath.vectors.Vec3;
import mesh.Mesh;
import shaders.ShaderProgram;

public abstract class Light {
	
	protected Vec3 color;
	protected Transform trans;
	protected float intensity, attenLinear;
	protected static final int VOLUME_FINENESS = 10;
	//rotation for the next point on the volume
	protected static final Quaternion volumeRot = Quaternion.fromAxisAngle(0, 1, 0, 360/VOLUME_FINENESS);

	public Light(Vec3 position, Vec3 color, float intensity, float attenLin){
		this(position.x, position.y, position.z, color.x, color.y, color.z, intensity, attenLin);
	}
	
	public Light(float xpos, float ypos, float zpos, Vec3 color, float intensity, float attenLin){
		this(xpos, ypos, zpos, color.x, color.y, color.z, intensity, attenLin);
	}
	
	public Light(Vec3 position, float r, float g, float b, float intensity, float attenLin){
		this(position.x, position.y, position.z, r, g, b, intensity, attenLin);
	}
	
	public Light(float xpos, float ypos, float zpos, float r, float g, float b, float intensity, float attenLin){
		color = new Vec3(r, g, b);
		trans = new Transform();
		trans.setTranslation(xpos, ypos, zpos);
		this.intensity = intensity;
		this.attenLinear = attenLin;
	}
	
	public Vec3 getPos(){
		return trans.getTranslation();
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
	
	public abstract void transform(Transform trans);
	
	public Transform getTransform(){
		return trans;
	}
	
	public float getIntensity(){
		return intensity;
	}
	
	public void setIntensity(float intensity){
		this.intensity = intensity;
	}
	
	public void setAttenuationLinear(float linear){
		attenLinear = linear;
	}
	
	public float getAttenuationLinear(){
		return attenLinear;
	}
	
	public abstract void bind(ShaderProgram shader);
	
	public abstract Mesh getVolume();
}
