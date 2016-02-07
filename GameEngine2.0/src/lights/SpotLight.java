package lights;

import glMath.Quaternion;
import glMath.Transform;
import glMath.VecUtil;
import glMath.vectors.Vec3;
import mesh.Mesh;
import mesh.primitives.geometry.Cone;
import shaders.ShaderProgram;

public class SpotLight extends Light {

	private float radius, length, cutoff;
	private Vec3 direction;
	private static final Vec3 centerVec = new Vec3(0,-1,0);
	public final static Cone volume = new Cone(1, 1, VOLUME_FINENESS, false);
	
	public SpotLight(Vec3 position, 
			Vec3 color, 
			Vec3 direction, 
			float radius, float length, 
			float intensity) {
		this(position.x, position.y, position.z, color.x, color.y, color.z, direction.x, direction.y, direction.z, radius, length, intensity);
	}
	
	public SpotLight(Vec3 position, 
			float r, float g, float b, 
			float dirx, float diry, float dirz, 
			float radius, float length, 
			float intensity) {
		this(position.x, position.y, position.z, r, g, b, dirx, diry, dirz, radius, length, intensity);
	}

	public SpotLight(float xpos, float ypos, float zpos, 
			Vec3 color, 
			float dirx, float diry, float dirz,
			float radius, float length, 
			float intensity) {
		this(xpos, ypos, zpos, color.x, color.y, color.z, dirx, diry, dirz, radius, length, intensity);
	}

	public SpotLight(float xpos, float ypos, float zpos, 
			float r, float g, float b, 
			Vec3 direction, 
			float radius, float length, 
			float intensity) {
		this(xpos, ypos, zpos, r, g, b, direction.x, direction.y, direction.z, radius, length, intensity);
	}
	
	public SpotLight(Vec3 position, 
			Vec3 color, 
			float dirx, float diry, float dirz, 
			float radius, float length, 
			float intensity) {
		this(position.x, position.y, position.z, color.x, color.y, color.z, dirx, diry, dirz, radius, length, intensity);
	}
	
	public SpotLight(float xpos, float ypos, float zpos,
			Vec3 color, 
			Vec3 direction, 
			float radius, float length, 
			float intensity) {
		this(xpos, ypos, zpos, color.x, color.y, color.z, direction.x, direction.y, direction.z, radius, length, intensity);
	}
	
	public SpotLight(Vec3 position, 
			float r, float g, float b, 
			Vec3 direction, 
			float radius, float length, 
			float intensity) {
		this(position.x, position.y, position.z, r, g, b, direction.x, direction.y, direction.z, radius, length, intensity);
	}

	/**
	 * Constructs a spot light that has a cone of influence
	 * @param xpos
	 * @param ypos
	 * @param zpos
	 * @param r
	 * @param g
	 * @param b
	 * @param dirx
	 * @param diry
	 * @param dirz
	 * @param radius
	 * @param length
	 * @param intensity
	 */
	public SpotLight( 
			float xpos, float ypos, float zpos, 
			float r, float g, float b, 
			float dirx, float diry, float dirz,
			float radius, float length,
			float intensity) {
		super(xpos, ypos, zpos, r, g, b, intensity);
		this.radius = Math.abs(radius);
		this.length = Math.abs(length);
		Vec3 maxVec = new Vec3(this.radius, -this.length, 0);
		//compute the cutoff such that it is retained inside the volume as a smooth circle
		//this prevents the jagged simple volume from being seen
		maxVec = VecUtil.add(maxVec, VecUtil.subtract(volumeRot.multVec(maxVec), maxVec).scale(.5f)).normalize();//maxVec+(rotVec-maxVec)/2
		//get the dot product of the vector from the middle of the light to the farthest edge of the light
		//this gets the cutoff
		cutoff = centerVec.dot(maxVec);
		
		direction = new Vec3(dirx, diry, dirz).normalize();
		Vec3 axis = centerVec.cross(direction);
		float angle = (float)(Math.acos(centerVec.dot(direction))*180/Math.PI);
		trans.rotate(angle == 180 ? VecUtil.xAxis : axis, angle);
		trans.scale(this.radius, this.length, this.radius);
	}
	
	public float getRadius(){
		return radius;
	}
	
	public float getLength(){
		return length;
	}

	@Override
	public void transform(Transform transform){
		radius *= transform.getScalars().x;
		length *= transform.getScalars().y;
		Vec3 maxVec = new Vec3(this.radius, -this.length, 0);
		//compute the cutoff such that it is retained inside the volume as a smooth circle
		//this prevents the jagged simple volume from being seen
		maxVec = VecUtil.add(maxVec, VecUtil.subtract(volumeRot.multVec(maxVec), maxVec).scale(.5f)).normalize();//maxVec+(rotVec-maxVec)/2
		//get the dot product of the vector from the middle of the light to the farthest edge of the light
		//this gets the cutoff
		cutoff = centerVec.dot(maxVec);
		Quaternion rotation = transform.getOrientation();
		direction = rotation.multVec(direction);
		
		trans.transform(transform);
	}

	@Override
	public Mesh getVolume(){
		volume.setTransform(trans);
		return volume;
	}
	
	@Override
	public void bind(ShaderProgram shader){
		shader.setUniform("sLight.pos", trans.getTranslation());
		shader.setUniform("sLight.color", color);
		shader.setUniform("sLight.intensity", intensity);
		shader.setUniform("sLight.cutOff", cutoff);
		shader.setUniform("sLight.length", length);
		shader.setUniform("sLight.direction", direction);
		shader.setUniform("isPoint", false);
		shader.setUniform("isSpot", true);
		shader.setUniform("model", trans.getTransform());
	}
}
