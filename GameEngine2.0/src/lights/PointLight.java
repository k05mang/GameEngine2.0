package lights;

import renderers.RenderMode;
import glMath.vectors.Vec3;
import mesh.primitives.geometry.Sphere;

public class PointLight extends Light {

	private float radius;
	public final static Sphere volume = new Sphere(1, VOLUME_FINENESS, RenderMode.TRIANGLES);
	
	public PointLight(float radius, Vec3 position, Vec3 color) {
		super(position, color);
		this.radius = radius;
		trans.scale(radius, radius, radius);
	}

	public PointLight(float radius, float xpos, float ypos, float zpos, Vec3 color) {
		super(xpos, ypos, zpos, color);
		this.radius = radius;
		trans.scale(radius, radius, radius);
	}

	public PointLight(float radius, Vec3 position, float r, float g, float b) {
		super(position, r, g, b);
		this.radius = radius;
		trans.scale(radius, radius, radius);
	}

	public PointLight(float radius, float xpos, float ypos, float zpos, float r, float g, float b) {
		super(xpos, ypos, zpos, r, g, b);
		this.radius = radius;
		trans.scale(radius, radius, radius);
	}

	public float getRadius(){
		return radius;
	}
}
