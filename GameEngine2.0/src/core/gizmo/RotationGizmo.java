package core.gizmo;

import glMath.Transform;
import mesh.primitives.geometry.Torus;
import shaders.ShaderProgram;
import core.Camera;
import core.Entity;
import core.SceneManager;
import core.SpatialAsset;

public class RotationGizmo extends TransformGizmo {

	private static final float WHEEL_RADIUS = 10f;
	private float wheelRadius;
	private Entity xWheel, yWheel, zWheel;
	
	public RotationGizmo(){
		super();
		if(SceneManager.meshes.get("rot_wheel") == null){
			SceneManager.meshes.put("rot_wheel", new Torus(WHEEL_RADIUS, .5f, 40));
		}
		wheelRadius = WHEEL_RADIUS;
		//null should be replaced with a suitable collisionmesh when it is created
		xWheel = new Entity(SceneManager.meshes.get("rot_wheel"), null);
		xWheel.getTransform().rotate(0,0,1,90);
		yWheel = new Entity(SceneManager.meshes.get("rot_wheel"), null);
		zWheel = new Entity(SceneManager.meshes.get("rot_wheel"), null);
		zWheel.getTransform().rotate(-1,0,0,90);
	}

	@Override
	public void render(ShaderProgram program){
		super.render(program);
		program.setUniform("model", xWheel.getTransform().getMatrix());
		program.setUniform("color", 1,0,0);
		SceneManager.meshes.get("rot_wheel").render();
		program.setUniform("model", yWheel.getTransform().getMatrix());
		program.setUniform("color", 0,1,0);
		SceneManager.meshes.get("rot_wheel").render();
		program.setUniform("model", zWheel.getTransform().getMatrix());
		program.setUniform("color", 0,0,1);
		SceneManager.meshes.get("rot_wheel").render();
	}
	
	@Override
	public void bind(SpatialAsset target){
		super.bind(target);
		xWheel.getTransform().setTranslation(target.getPos());
		yWheel.getTransform().setTranslation(target.getPos());
		zWheel.getTransform().setTranslation(target.getPos());
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void select(Camera view, float screenX, float screenY) {
		// TODO Auto-generated method stub
		
	}
}
