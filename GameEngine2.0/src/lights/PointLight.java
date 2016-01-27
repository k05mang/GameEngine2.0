package lights;

import renderers.RenderMode;
import shaders.ShaderProgram;
import glMath.Transform;
import glMath.VecUtil;
import glMath.vectors.Vec3;
import mesh.Mesh;
import mesh.primitives.geometry.Sphere;

public class PointLight extends Light {

	private float radius;
	public final static Sphere volume = new Sphere(1, VOLUME_FINENESS, RenderMode.TRIANGLES, RenderMode.LINES);
	
	public PointLight(float radius, Vec3 position, Vec3 color, float intensity, float attenLin) {
		this(radius, position.x, position.y, position.z, color.x, color.y, color.z, intensity, attenLin);
	}

	public PointLight(float radius, float xpos, float ypos, float zpos, Vec3 color, float intensity, float attenLin) {
		this(radius, xpos, ypos, zpos, color.x, color.y, color.z, intensity, attenLin);
	}

	public PointLight(float radius, Vec3 position, float r, float g, float b, float intensity, float attenLin) {
		this(radius, position.x, position.y, position.z, r, g, b, intensity, attenLin);
	}

	public PointLight(float radius, float xpos, float ypos, float zpos, float r, float g, float b, float intensity, float attenLin) {
		super(xpos, ypos, zpos, r, g, b, intensity, attenLin);
		//compute the new radius
		Vec3 maxVec = new Vec3(radius, 0, 0);
		//compute the radius such that it is retained inside the volume as a smooth circle
		//this prevents the jagged simple volume from being seen
		maxVec.add(VecUtil.subtract(volumeRot.multVec(maxVec), maxVec).scale(.5f));//maxVec+(rotVec-maxVec)/2
		//get the dot product of the vector from the middle of the light to the farthest edge of the light
		//this gets the cutoff
		this.radius = maxVec.length();
		
		trans.scale(radius);
	}

	public float getRadius(){
		return radius;
	}
	
	public Mesh getVolume(){
		volume.setTransform(trans);
		return volume;
	}
	
	@Override
	public void transform(Transform transform){
		Vec3 scale = transform.getScalars();
		//compute the new radius, using the acutal radius in this light will cause it to deteriorate since
		//the radius being used in the calculation is the radius of the sphere mesh
		Vec3 maxVec = new Vec3(trans.getScalars().x*scale.x, 0, 0);
		//compute the radius such that it is retained inside the volume as a smooth circle
		//this prevents the jagged simple volume from being seen
		maxVec.add(VecUtil.subtract(volumeRot.multVec(maxVec), maxVec).scale(.5f));//maxVec+(rotVec-maxVec)/2
		//get the dot product of the vector from the middle of the light to the farthest edge of the light
		this.radius = maxVec.length();
		//lastly transform the volume mesh
		trans.transform(transform);
	}
	
	@Override
	public void bind(ShaderProgram shader){
		shader.setUniform("pLight.pos", trans.getTranslation());
		shader.setUniform("pLight.color", color);
		shader.setUniform("pLight.intensity", intensity);
		shader.setUniform("pLight.radius", radius);
		shader.setUniform("pLight.attenLinear", attenLinear);
		shader.setUniform("isPoint", true);
		shader.setUniform("isSpot", false);
		shader.setUniform("model", trans.getTransform());
	}
}
