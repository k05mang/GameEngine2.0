package core.gizmo;

import physics.collision.CollisionDetector;
import physics.collision.Ray;
import mesh.primitives.geometry.Torus;
import shaders.ShaderProgram;
import windowing.Window;
import core.Camera;
import core.Entity;
import core.SceneManager;
import core.SpatialAsset;
import events.keyboard.ModKey;
import events.mouse.MouseButton;

public class RotationGizmo extends TransformGizmo {

	private static final float WHEEL_RADIUS = 10f;
	private float wheelRadius;
	private Entity xWheel, yWheel, zWheel;
	
	public RotationGizmo(Camera view){
		super(view);
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
	
//	@Override
//	public void setScale(float factor){
//		super.setScale(factor);
//		xWheel.getTransform().setScale(factor);
//		yWheel.getTransform().setScale(factor);
//		zWheel.getTransform().setScale(factor);
//	}
	
	@Override
	public void onMouseMove(Window window, double xpos, double ypos, double prevX, double prevY) {
		//first check to make sure there is a target to modify
		if(this.target != null){
			//check which modifier is active
			switch(activeModifier){
				case 'c'://when the center sphere is selected
					break;
				case 'x'://when the x axis is selected
					break;
				case 'y'://when the y axis is selected
					break;
				case 'z'://when the z axis is selected
					break;
			}
		}//if there isn't anything to modify then do nothing
	}
	
	@Override
	public void onMousePress(Window window, MouseButton button, boolean isRepeat, ModKey[] mods){
		//first create the ray to test collision with
		Ray clickRay = view.genRay((float)window.cursorX, (float)window.cursorY);
		//perform each collision check, starting with the center sphere
		if(CollisionDetector.intersects(clickRay, center)){
			activeModifier = 'c';
		}else if(CollisionDetector.intersects(clickRay, xWheel)){
			activeModifier = 'x';
		}else if(CollisionDetector.intersects(clickRay, yWheel)){
			activeModifier = 'y';
		}else if(CollisionDetector.intersects(clickRay, zWheel)){
			activeModifier = 'z';
		}else{
			activeModifier = 0;
		}
	}
}
