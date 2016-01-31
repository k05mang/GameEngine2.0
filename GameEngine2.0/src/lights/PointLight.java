package lights;

import renderers.RenderMode;
import glMath.Transform;
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
		this.radius = radius;
		trans.scale(radius, radius, radius);
	}

	public float getRadius(){
		return radius;
	}
	
	public void transform(Transform transform){
		radius *= transform.getScalars().x;
		trans.transform(transform);
	}
	
	public Mesh getVolume(){
		volume.setTransform(trans);
		return volume;
	}
}
