package lights;

import mesh.Mesh;
import mesh.primitives.geometry.Plane;
import renderers.RenderMode;
import glMath.vectors.Vec3;

public class DirectionalLight extends Light {
	public final static Plane volume = new Plane(1, 1, RenderMode.TRIANGLES, RenderMode.LINES);

	public DirectionalLight(Vec3 direction, Vec3 color, float intensity) {
		super(direction, color, intensity, 0, 0);
	}

	public DirectionalLight(float dirx, float diry, float dirz, Vec3 color, float intensity) {
		super(dirx, diry, dirz, color, intensity, 0, 0);
	}

	public DirectionalLight(Vec3 direction, float r, float g, float b, float intensity) {
		super(direction, r, g, b, intensity, 0, 0);
	}

	public DirectionalLight(float dirx, float diry, float dirz, float r, float g, float b, float intensity) {
		super(dirx, diry, dirz, r, g, b, intensity, 0, 0);
	}
	
	public Mesh getVolume(){
		volume.setTransform(trans);
		return volume;
	}
}
