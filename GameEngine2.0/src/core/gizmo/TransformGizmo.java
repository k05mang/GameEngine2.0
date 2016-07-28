package core.gizmo;

import glMath.Transform;
import glMath.vectors.Vec3;
import mesh.primitives.geometry.Sphere;
import shaders.ShaderProgram;
import core.SpatialAsset;

public abstract class TransformGizmo {

	public static final Sphere center = new Sphere(1.5f, 20);
	protected static final Vec3 centerColor = new Vec3(1,1,0);
	protected Transform sphereTrans;
	private static SpatialAsset target;
	
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
	
//	public abstract void update();
//	
//	public abstract void select(Vec3 origin, float screenX, float screenY);
}
