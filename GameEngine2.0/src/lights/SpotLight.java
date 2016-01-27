package lights;

import glMath.Quaternion;
import glMath.Transform;
import glMath.VecUtil;
import glMath.vectors.Vec3;
import mesh.Mesh;
import mesh.primitives.geometry.Cone;
import renderers.RenderMode;
import shaders.ShaderProgram;

public class SpotLight extends Light {

	private float radius, length, cutoff;
	public final static Cone volume = new Cone(1, 1, VOLUME_FINENESS, false, RenderMode.TRIANGLES, RenderMode.LINES);
	
	public SpotLight(float radius, float length, Vec3 position, Vec3 color, float intensity, float attenLin) {
		this(radius, length, position.x, position.y, position.z, color.x, color.y, color.z, intensity, attenLin);
	}

	public SpotLight(float radius, float length, float xpos, float ypos, float zpos, Vec3 color, float intensity, float attenLin) {
		this(radius, length, xpos, ypos, zpos, color.x, color.y, color.z, intensity, attenLin);
	}

	public SpotLight(float radius, float length, Vec3 position, float r, float g, float b, float intensity, float attenLin) {
		this(radius, length, position.x, position.y, position.z, r, g, b, intensity, attenLin);
	}

	public SpotLight(float radius, float length, float xpos, float ypos, float zpos, float r, float g, float b, float intensity, float attenLin) {
		super(xpos, ypos, zpos, r, g, b, intensity, attenLin);
		this.radius = Math.abs(radius);
		this.length = Math.abs(length);
		Vec3 lengthVec = new Vec3(0, 1, 0);
		Vec3 maxVec = new Vec3(this.radius, 1, 0);
		//rotation for the next point on the volume
		Quaternion volumeRot = Quaternion.fromAxisAngle(0, 1, 0, 360/VOLUME_FINENESS);
		//compute the cutoff such that it is retained inside the volume as a smooth circle
		//this prevents the jagged simple volume from being seen
		maxVec = VecUtil.add(maxVec, VecUtil.subtract(volumeRot.multVec(maxVec), maxVec).scale(.5f)).normalize();//maxVec+(rotVec-maxVec)/2
		//normalize the vector
		maxVec.normalize();
		//get the dot product of the vector from the middle of the light to the farthest edge of the light
		//this gets the cutoff
		cutoff = lengthVec.dot(maxVec);
		trans.scale(this.radius, this.length, this.radius);
	}
	
	public float getRadius(){
		return radius;
	}
	
	public float getLength(){
		return length;
	}
	
	public Mesh getVolume(){
		volume.setTransform(trans);
		return volume;
	}
	
	@Override
	public void transform(Transform transform){
		Vec3 scale = transform.getScalars();
		//compute new radius and length
		radius *= scale.x;
		length *= scale.y;
		//compute the new cutoff
		Vec3 lengthVec = new Vec3(0, 1, 0);
		Vec3 maxVec = new Vec3(this.radius, 1, 0);
		//compute the cutoff such that it is retained inside the volume as a smooth circle
		//this prevents the jagged simple volume from being seen
		maxVec = VecUtil.add(maxVec, VecUtil.subtract(volumeRot.multVec(maxVec), maxVec).scale(.5f)).normalize();//maxVec+(rotVec-maxVec)/2
		//normalize the vector
		maxVec.normalize();
		//get the dot product of the vector from the middle of the light to the farthest edge of the light
		//this gets the cutoff
		cutoff = lengthVec.dot(maxVec);
		
		//lastly transform the volume mesh
		trans.transform(transform);
	}
	
	@Override
	public void bind(ShaderProgram shader){
		shader.setUniform("sLight.pos", trans.getTranslation());
		shader.setUniform("sLight.color", color);
		shader.setUniform("sLight.intensity", intensity);
		shader.setUniform("sLight.cutOff", cutoff);
		shader.setUniform("sLight.attenLinear", attenLinear);
		shader.setUniform("isPoint", false);
		shader.setUniform("isSpot", true);
		shader.setUniform("model", trans.getTransform());
	}
}
