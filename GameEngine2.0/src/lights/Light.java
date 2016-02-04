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

	/**
	 * Constructs a light with the given {@code position}, or direction in the case of a point light,
	 * {@code color}, {@code intensity}, and linear attenuation
	 * 
	 * @param position Position of the light or direction in the case of directional lights
	 * @param color Color of the light
	 * @param intensity Intensity of the light
	 * @param attenLin Linear attenuation of the light
	 */
	public Light(Vec3 position, Vec3 color, float intensity, float attenLin){
		this(position.x, position.y, position.z, color.x, color.y, color.z, intensity, attenLin);
	}
	
	/**
	 * Constructs a light with the given {@code position}, or direction in the case of a point light,
	 * {@code color}, {@code intensity}, and linear attenuation
	 * 
	 * @param xpos X component for the position or direction
	 * @param ypos Y component for the position or direction
	 * @param zpos Z component for the position or direction
	 * @param color Color of the light
	 * @param intensity Intensity of the light
	 * @param attenLin Linear attenuation of the light
	 */
	public Light(float xpos, float ypos, float zpos, Vec3 color, float intensity, float attenLin){
		this(xpos, ypos, zpos, color.x, color.y, color.z, intensity, attenLin);
	}
	
	/**
	 * Constructs a light with the given {@code position}, or direction in the case of a point light,
	 * {@code color}, {@code intensity}, and linear attenuation
	 * 
	 * @param position Position or direction of the light
	 * @param r Red component of the color of the light
	 * @param g Green component of the color of the light
	 * @param b Blue component of the color of the light
	 * @param intensity Intensity of the light
	 * @param attenLin Linear attenuation of the light
	 */
	public Light(Vec3 position, float r, float g, float b, float intensity, float attenLin){
		this(position.x, position.y, position.z, r, g, b, intensity, attenLin);
	}
	
	/**
	 * Constructs a light with the given {@code position}, or direction in the case of a point light,
	 * {@code color}, {@code intensity}, and linear attenuation
	 * 
	 * @param xpos X component for the position or direction
	 * @param ypos Y component for the position or direction
	 * @param zpos Z component for the position or direction
	 * @param r Red component of the color of the light
	 * @param g Green component of the color of the light
	 * @param b Blue component of the color of the light
	 * @param intensity Intensity of the light
	 * @param attenLin Linear attenuation of the light
	 */
	public Light(float xpos, float ypos, float zpos, float r, float g, float b, float intensity, float attenLin){
		color = new Vec3(r, g, b);
		trans = new Transform();
		trans.setTranslation(xpos, ypos, zpos);
		this.intensity = intensity;
		this.attenLinear = attenLin;
	}
	
	/**
	 * Gets the position or direction of the light
	 * 
	 * @return Position or direction of the light
	 */
	public Vec3 getPos(){
		return trans.getTranslation();
	}
	
	/**
	 * Gets the color of the light
	 * 
	 * @return Color of the light
	 */
	public Vec3 getColor(){
		return color;
	}
	
	/**
	 * Sets the color of the light
	 * 
	 * @param newColor New color of the light
	 */
	public void setColor(Vec3 newColor){
		color.set(newColor);
	}
	
	/**
	 * Sets the color of the light
	 * 
	 * @param r Red component of the color of the light
	 * @param g Green component of the color of the light
	 * @param b Blue component of the color of the light
	 */
	public void setColor(float r, float g, float b){
		color.set(r, g, b);
	}
	
	/**
	 * Transforms the light
	 * 
	 * @param transform Transform that changes this light
	 */
	public abstract void transform(Transform transform);
	
	/**
	 * Gets this lights transformation
	 * 
	 * @return Trasnformation of this light
	 */
	public Transform getTransform(){
		return trans;
	}
	
	/**
	 * Gets the intensity of the light
	 * 
	 * @return Intensity of the light
	 */
	public float getIntensity(){
		return intensity;
	}
	
	/**
	 * Sets the intensity of the light
	 * 
	 * @param intensity New value for this lights intensity
	 */
	public void setIntensity(float intensity){
		this.intensity = intensity;
	}
	
	/**
	 * Sets the linear attenuation for this light
	 * 
	 * @param linear New linear attenuation for this light
	 */
	public void setAttenuationLinear(float linear){
		attenLinear = linear;
	}
	
	/**
	 * Gets the linear attenuation of this light
	 * 
	 * @return Linear attenuation for light
	 */
	public float getAttenuationLinear(){
		return attenLinear;
	}
	
	/**
	 * Binds this light to the shader program uniform.
	 * <br>
	 * The light uniform is assumed to be of the type Light 
	 * defined as such in the shader:
	 * 
	 * struct Light{
	 * 	  vec3 pos, color;
	 * 	  float intensity, attenLinear;
	 * };
	 * 
	 * with an instance name of light, additionally this method will attempt
	 * to modify a uniform mat4 with the name of model
	 * 
	 * @param shader Shader Program to set the uniforms of
	 */
	public abstract void bind(ShaderProgram shader);
	
	/**
	 * Gets the transformed mesh volume of this light
	 * 
	 * @return Mesh that represents the volume of this light
	 */
	public abstract Mesh getVolume();
}
