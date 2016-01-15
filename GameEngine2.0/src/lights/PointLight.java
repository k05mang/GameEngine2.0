package lights;

import renderers.RenderMode;
import glMath.vectors.Vec3;
import mesh.Renderable;
import mesh.primitives.geometry.Sphere;

public class PointLight extends Light {

	private float radius;
	public final static Sphere volume = new Sphere(1, VOLUME_FINENESS, RenderMode.TRIANGLES);
	
	public PointLight(float radius, Vec3 position, Vec3 color, float intensity) {
		this(radius, position.x, position.y, position.z, color.x, color.y, color.z, intensity);
	}

	public PointLight(float radius, float xpos, float ypos, float zpos, Vec3 color, float intensity) {
		this(radius, xpos, ypos, zpos, color.x, color.y, color.z, intensity);
	}

	public PointLight(float radius, Vec3 position, float r, float g, float b, float intensity) {
		this(radius, position.x, position.y, position.z, r, g, b, intensity);
	}

	public PointLight(float radius, float xpos, float ypos, float zpos, float r, float g, float b, float intensity) {
		super(xpos, ypos, zpos, r, g, b, intensity);
		this.radius = radius;
		trans.scale(radius, radius, radius);
	}

	public float getRadius(){
		return radius;
	}
	
	public Renderable getVolume(){
		volume.setTransform(trans);
		return volume;
	}
}
