package lights;

import glMath.Transform;
import glMath.VecUtil;
import glMath.vectors.Vec3;
import mesh.Mesh;
import mesh.primitives.geometry.Sphere;
import shaders.ShaderProgram;

public class PointLight extends Light {

	private float radius;
	public final static Sphere volume = new Sphere(1, VOLUME_FINENESS);
	
	public PointLight(Vec3 position, Vec3 color, float radius, float intensity) {
		this(position.x, position.y, position.z, color.x, color.y, color.z, radius, intensity);
	}

	public PointLight(float xpos, float ypos, float zpos, Vec3 color, float radius, float intensity) {
		this(xpos, ypos, zpos, color.x, color.y, color.z, radius, intensity);
	}

	public PointLight(Vec3 position, float r, float g, float b, float radius, float intensity) {
		this(position.x, position.y, position.z, r, g, b, radius, intensity);
	}

	public PointLight(float xpos, float ypos, float zpos, float r, float g, float b, float radius, float intensity) {
		super(xpos, ypos, zpos, r, g, b, intensity);
		//compute the new radius
		Vec3 maxVec = new Vec3(radius, 0, 0);
		//compute the radius such that it is retained inside the volume as a smooth circle
		//this prevents the jagged simple volume from being seen
		maxVec.add(VecUtil.subtract(volumeRot.multVec(maxVec), maxVec).scale(.5f));//maxVec+(rotVec-maxVec)/2
		//get the dot product of the vector from the middle of the light to the farthest edge of the light
		//this gets the cutoff
		this.radius = maxVec.length();
		
		transforms.scale(radius);
	}

	public float getRadius(){
		return radius;
	}
	
	@Override
	public void transform(Transform transform){
		radius *= transform.getScalars().x;
		transforms.transform(transform);
	}

	@Override
	public Mesh getVolume(){
		volume.setTransform(transforms);
		return volume;
	}
	
	@Override
	public void bind(ShaderProgram shader){
		shader.setUniform("pLight.pos", transforms.getTranslation());
		shader.setUniform("pLight.color", color);
		shader.setUniform("pLight.intensity", intensity);
		shader.setUniform("pLight.radius", radius);
		shader.setUniform("isPoint", true);
		shader.setUniform("isSpot", false);
		shader.setUniform("model", transforms.getMatrix());
	}
}
