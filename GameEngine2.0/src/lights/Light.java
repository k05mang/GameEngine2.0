package lights;

import glMath.Transform;
import glMath.vectors.Vec3;
import mesh.Mesh;
import shaders.ShaderProgram;

public abstract class Light {
	
	protected Vec3 color;
	protected Transform trans;
	protected float intensity, attenLinear, attenQuad;
	protected static final int VOLUME_FINENESS = 10;
	//TODO attenuation

	public Light(Vec3 position, Vec3 color, float intensity, float attenLin, float attenQuad){
		this(position.x, position.y, position.z, color.x, color.y, color.z, intensity, attenLin, attenQuad);
	}
	
	public Light(float xpos, float ypos, float zpos, Vec3 color, float intensity, float attenLin, float attenQuad){
		this(xpos, ypos, zpos, color.x, color.y, color.z, intensity, attenLin, attenQuad);
	}
	
	public Light(Vec3 position, float r, float g, float b, float intensity, float attenLin, float attenQuad){
		this(position.x, position.y, position.z, r, g, b, intensity, attenLin, attenQuad);
	}
	
	public Light(float xpos, float ypos, float zpos, float r, float g, float b, float intensity, float attenLin, float attenQuad){
		color = new Vec3(r, g, b);
		trans = new Transform();
		trans.setTranslation(xpos, ypos, zpos);
		this.intensity = intensity;
		this.attenLinear = attenLin;
		this.attenQuad = attenQuad;
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
	
	public void transform(Transform trans){
		this.trans.transform(trans);
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
	
	public void setAttenuationLinear(float linear){
		attenLinear = linear;
	}
	
	public float getAttenuationLinear(){
		return attenLinear;
	}
	
	public void setAttenuationQuadratic(float quad){
		attenQuad = quad;
	}
	
	public float getAttenuationQuadratic(){
		return attenQuad;
	}
	
	public void setUniforms(ShaderProgram shader){
		shader.setUniform("light.pos", trans.getTranslation());
		shader.setUniform("light.color", color);
		shader.setUniform("light.intensity", intensity);
		shader.setUniform("light.attenLinear", attenLinear);
		shader.setUniform("light.attenQuad", attenQuad);
		shader.setUniform("model", trans.getTransform());
	}
	
	public abstract Mesh getVolume();
}
