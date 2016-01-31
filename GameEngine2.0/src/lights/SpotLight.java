package lights;

import glMath.Transform;
import glMath.vectors.Vec3;
import mesh.Mesh;
import mesh.primitives.geometry.Cone;
import renderers.RenderMode;

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
		Vec3 lengthVec = new Vec3(0, this.length, 0);
		Vec3 maxVec = new Vec3(this.radius, this.length, 0);
		//normalize the vectors
		lengthVec.normalize();
		maxVec.normalize();
		//get the dot product of the vector from the middle of the light to the farthest edge of the light
		//this gets the cutoff
		cutoff = lengthVec.dot(maxVec);//result is in radians
		trans.scale(this.radius, this.length, this.radius);
	}
	
	public float getRadius(){
		return radius;
	}
	
	public float getLength(){
		return length;
	}
	
	public void transform(Transform transform){
		radius *= transform.getScalars().x;
		length *= transform.getScalars().y;
		trans.transform(transform);
	}
	
	public Mesh getVolume(){
		volume.setTransform(trans);
		return volume;
	}
}
