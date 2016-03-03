package core.gizmo;

import glMath.Transform;
import glMath.vectors.Vec3;
import mesh.Mesh;
import mesh.primitives.geometry.Torus;
import shaders.ShaderProgram;
import core.Camera;
import core.SceneManager;
import core.SpatialAsset;

public class RotationGizmo extends TransformGizmo {

	private static final float WHEEL_RADIUS = 10f;
	private float wheelRadius;
	private Transform xRot, yRot, zRot;
	
	public RotationGizmo(){
		super();
		if(SceneManager.meshes.get("rot_wheel") == null){
			SceneManager.meshes.put("rot_wheel", new Torus(WHEEL_RADIUS, .5f, 40));
		}
		wheelRadius = WHEEL_RADIUS;
		xRot = new Transform().rotate(0,0,1,90); 
		yRot = new Transform(); 
		zRot = new Transform().rotate(-1,0,0,90);
	}

	@Override
	public void render(ShaderProgram program){
		super.render(program);
		program.setUniform("model", xRot.getTransform());
		program.setUniform("color", 1,0,0);
		((Mesh)SceneManager.meshes.get("rot_wheel")).render();
		program.setUniform("model", yRot.getTransform());
		program.setUniform("color", 0,1,0);
		((Mesh)SceneManager.meshes.get("rot_wheel")).render();
		program.setUniform("model", zRot.getTransform());
		program.setUniform("color", 0,0,1);
		((Mesh)SceneManager.meshes.get("rot_wheel")).render();
	}
	
	@Override
	public void bind(SpatialAsset target){
		super.bind(target);
		xRot.setTranslation(target.getPos());
		yRot.setTranslation(target.getPos());
		zRot.setTranslation(target.getPos());
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
