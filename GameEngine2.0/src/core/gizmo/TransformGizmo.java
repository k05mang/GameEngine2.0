package core.gizmo;

import mesh.primitives.geometry.Sphere;
import glMath.Transform;
import glMath.vectors.Vec3;
import shaders.ShaderProgram;

public abstract class TransformGizmo {

	public static final Sphere center = new Sphere(1, 20);
	protected static final Vec3 centerColor = new Vec3(1,1,0);
	protected Transform sphereTrans;
	private static final float radius = 1.5f;
	
	public TransformGizmo(float x, float y, float z){
		sphereTrans = new Transform().translate(x, y, z).scale(radius);
	}
	
	public void setPos(Vec3 pos){
		setPos(pos.x, pos.y, pos.z);
	}
	
	public void setPos(float x, float y, float z){
		sphereTrans.setTranslation(x, y, z);
	}
	
	public void setScale(float scale){
		sphereTrans.setScale(radius*scale);
	}
	
	public void transform(Transform trans){
		sphereTrans.transform(trans);
	}
	
	public void render(ShaderProgram program){
		program.setUniform("model", sphereTrans.getTransform());
		program.setUniform("color", centerColor);
		center.render();
	}
}
