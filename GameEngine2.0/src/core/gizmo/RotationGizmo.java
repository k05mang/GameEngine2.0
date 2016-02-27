package core.gizmo;

import mesh.primitives.geometry.Torus;
import glMath.Transform;
import glMath.vectors.Vec3;
import shaders.ShaderProgram;

public class RotationGizmo extends TransformGizmo {

	private static final float WHEEL_RADIUS = 10f;
	private float wheelRadius;
	public static final Torus rotWheel = new Torus(WHEEL_RADIUS, .5f, 40);
	private Transform xRot, yRot, zRot;
	
	public RotationGizmo(){
		this(0,0,0);
	}
	
	public RotationGizmo(float x, float y, float z){
		super(x, y, z);
		wheelRadius = WHEEL_RADIUS;
		xRot = new Transform().translate(x, y, z).rotate(0,0,1,90); 
		yRot = new Transform().translate(x, y, z); 
		zRot = new Transform().translate(x, y, z).rotate(-1,0,0,90);
	}

	@Override
	public void transform(Transform trans) {
		super.transform(trans);
		xRot.transform(trans);
		yRot.transform(trans);
		zRot.transform(trans);
		wheelRadius *= trans.getScalars().x;//this is assuming the scaling is uniform
	}

	@Override
	public void setPos(Vec3 pos){
		setPos(pos.x, pos.y, pos.z);
	}

	@Override
	public void setPos(float x, float y, float z){
		super.setPos(x, y, z);
		xRot.setTranslation(x, y, z); 
		yRot.setTranslation(x, y, z); 
		zRot.setTranslation(x, y, z);
	}

	@Override
	public void setScale(float scale){
		transform(new Transform().scale(scale/(wheelRadius/WHEEL_RADIUS)));
	}

	@Override
	public void render(ShaderProgram program){
		super.render(program);
		program.setUniform("model", xRot.getTransform());
		program.setUniform("color", 1,0,0);
		rotWheel.render();
		program.setUniform("model", yRot.getTransform());
		program.setUniform("color", 0,1,0);
		rotWheel.render();
		program.setUniform("model", zRot.getTransform());
		program.setUniform("color", 0,0,1);
		rotWheel.render();
		program.setUniform("model", zRot.getTransform());
		program.setUniform("color", 0,0,1);
		rotWheel.render();
	}
}
