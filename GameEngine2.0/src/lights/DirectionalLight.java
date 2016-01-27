package lights;

import mesh.Mesh;
import mesh.primitives.geometry.Plane;
import renderers.RenderMode;
import shaders.ShaderProgram;
import glMath.Transform;
import glMath.VecUtil;
import glMath.vectors.Vec3;

public class DirectionalLight extends Light {
	public final static Plane volume = new Plane(1, 1, RenderMode.TRIANGLES, RenderMode.LINES);

	public DirectionalLight(Vec3 direction, Vec3 color, float intensity) {
		super(direction, color, intensity, 0);
	}

	public DirectionalLight(float dirx, float diry, float dirz, Vec3 color, float intensity) {
		super(dirx, diry, dirz, color, intensity, 0);
	}

	public DirectionalLight(Vec3 direction, float r, float g, float b, float intensity) {
		super(direction, r, g, b, intensity, 0);
	}

	public DirectionalLight(float dirx, float diry, float dirz, float r, float g, float b, float intensity) {
		super(dirx, diry, dirz, r, g, b, intensity, 0);
	}
	
	public Mesh getVolume(){
		volume.setTransform(trans);
		return volume;
	}
	
	@Override
	public void transform(Transform transform){
		
		trans.transform(transform);
	}
	
	@Override
	public void bind(ShaderProgram shader){
		shader.setUniform("dLight.direction", trans.getTranslation());
		shader.setUniform("dLight.color", color);
		shader.setUniform("dLight.intensity", intensity);
		shader.setUniform("isPoint", false);
		shader.setUniform("isSpot", false);
		shader.setUniform("model", trans.getTransform());
	}
}
