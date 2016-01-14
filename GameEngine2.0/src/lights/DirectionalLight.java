package lights;

import mesh.primitives.geometry.Plane;
import renderers.RenderMode;
import glMath.vectors.Vec3;

public class DirectionalLight extends Light {
	public final static Plane volume = new Plane(1, 1, RenderMode.TRIANGLES);

	public DirectionalLight(Vec3 direction, Vec3 color) {
		super(direction, color);
	}

	public DirectionalLight(float dirx, float diry, float dirz, Vec3 color) {
		super(dirx, diry, dirz, color);
	}

	public DirectionalLight(Vec3 direction, float r, float g, float b) {
		super(direction, r, g, b);
	}

	public DirectionalLight(float dirx, float diry, float dirz, float r, float g, float b) {
		super(dirx, diry, dirz, r, g, b);
	}
}
