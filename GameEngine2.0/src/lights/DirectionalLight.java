package lights;

import glMath.Transform;
import glMath.vectors.Vec3;
import mesh.Mesh;
import mesh.primitives.geometry.Cube;
import shaders.ShaderProgram;

public class DirectionalLight extends Light {
	public final static Cube volume = new Cube(1, 1, 1);
	private Vec3 direction;

	/**
	 * Constructs a directional light with the given {@code direction}, {@code color}, 
	 * volume dimensions {@code vDimension}, and {@code intensity}.
	 * 
	 * @param direction Direction of the light
	 * @param color Color of the light
	 * @param vDimension Dimensions of the bounding volume of the Directional light
	 * @param intensity Intensity of the light
	 */
	public DirectionalLight(Vec3 direction, Vec3 color, Vec3 vDimension, float intensity) {
		this(direction.x, direction.y, direction.z, color.x, color.y, color.z, vDimension.x, vDimension.y, vDimension.z, intensity);
	}

	public DirectionalLight(Vec3 direction, Vec3 color, float vWidth, float vHeight, float vDepth, float intensity) {
		this(direction.x, direction.y, direction.z, color.x, color.y, color.z, vWidth, vHeight, vDepth, intensity);
	}

	public DirectionalLight(float dirx, float diry, float dirz, Vec3 color, Vec3 vDimension, float intensity) {
		this(dirx, diry, dirz, color.x, color.y, color.z, vDimension.x, vDimension.y, vDimension.z, intensity);
	}

	public DirectionalLight(Vec3 direction, float r, float g, float b, Vec3 vDimension, float intensity) {
		this(direction.x, direction.y, direction.z, r, g, b, vDimension.x, vDimension.y, vDimension.z, intensity);
	}

	public DirectionalLight(Vec3 direction, float r, float g, float b, float vWidth, float vHeight, float vDepth,float intensity) {
		this(direction.x, direction.y, direction.z, r, g, b, vWidth, vHeight, vDepth, intensity);
	}

	public DirectionalLight(float dirx, float diry, float dirz, Vec3 color, float vWidth, float vHeight, float vDepth, float intensity) {
		this(dirx, diry, dirz, color.x, color.y, color.z, vWidth, vHeight, vDepth, intensity);
	}
	
	public DirectionalLight(float dirx, float diry, float dirz, float r, float g, float b, Vec3 vDimension, float intensity) {
		this(dirx, diry, dirz, r, g, b, vDimension.x, vDimension.y, vDimension.z, intensity);
	}

	/**
	 * Constructs a directional light with the given {@code intensity}. Direction specified by {@code dirx}, {@code diry}, and {@code dirz},
	 * color specified by {@code r}, {@code g}, and {@code b}, and bounding volume specified by {@code vWidth}, {@code vHeight}, and {@code vDepth}.
	 * 
	 * @param dirx X component of the direction vector of the light
	 * @param diry Y component of the direction vector of the light
	 * @param dirz Z component of the direction vector of the light
	 * @param r Red component of the color of the light
	 * @param g Green component of the color of the light
	 * @param b Blue component of the color of the light
	 * @param vWidth Width of the light volume of this light
	 * @param vHeight Height of the light volume of this light
	 * @param vDepth Depth of the light volume of this light
	 * @param intensity Intensity of the light
	 */
	public DirectionalLight(float dirx, float diry, float dirz, float r, float g, float b, float vWidth, float vHeight, float vDepth, float intensity) {
		super(0,0,0, r, g, b, intensity);
		direction = new Vec3(dirx, diry, dirz);
		trans.scale(vWidth, vHeight, vDepth);
	}

	@Override
	public Mesh getVolume(){
		volume.setTransform(trans);
		return volume;
	}

	@Override
	public void transform(Transform transform){
		direction = transform.getOrientation().multVec(direction);
		trans.transform(transform);
	}
	
	@Override
	public void bind(ShaderProgram shader){
		shader.setUniform("dLight.direction", direction);
		shader.setUniform("dLight.color", color);
		shader.setUniform("dLight.intensity", intensity);
		shader.setUniform("isPoint", false);
		shader.setUniform("isSpot", false);
		shader.setUniform("model", trans.getTransform());
	}
}
