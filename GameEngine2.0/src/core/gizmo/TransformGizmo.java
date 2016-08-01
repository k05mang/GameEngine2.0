package core.gizmo;

import glMath.Transform;
import glMath.vectors.Vec3;
import mesh.primitives.geometry.Sphere;
import physics.collision.Ray;
import shaders.ShaderProgram;
import core.SpatialAsset;

public abstract class TransformGizmo {

	public static final Sphere center = new Sphere(1.5f, 20);
	protected static final Vec3 centerColor = new Vec3(1,1,0);
	protected Transform sphereTrans;
	private static SpatialAsset target;
	private Object activeModifier;
	
	public TransformGizmo(){
		sphereTrans = new Transform();
	}
	
	public void render(ShaderProgram program){
		//only render if a target exists
		if(target != null){
			program.setUniform("model", sphereTrans.getTransform());
			program.setUniform("color", centerColor);
			center.render();
		}
	}
	
	public void bind(SpatialAsset target){
		this.target = target;
		sphereTrans.setTranslation(target.getPos());
	}
	
	public void unbind(){
		target = null;
	}
	
	public void check(Ray clickRay){
		//this version will check if the sphere was intersected
	}
	
	public boolean isSelected(){
		return activeModifier != null;
	}
	
	public abstract void update(float screenX, float screenY);
}
