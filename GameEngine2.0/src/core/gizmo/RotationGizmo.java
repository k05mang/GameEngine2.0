package core.gizmo;

import physics.collision.CollisionDetector;
import physics.collision.Ray;
import mesh.primitives.geometry.Torus;
import shaders.ShaderProgram;
import windowing.Window;
import windowing.events.keyboard.ModKey;
import windowing.events.mouse.MouseButton;
import core.Camera;
import core.Entity;
import core.managers.SceneManager;
import glMath.Quaternion;
import glMath.VecUtil;
import glMath.transforms.Transform;
import glMath.vectors.Vec3;
import static core.gizmo.TransformType.ROTATE;

public class RotationGizmo extends TransformGizmo {

	private static final float WHEEL_RADIUS = 10f;
	private Entity xyWheel, yzWheel, xzWheel;
	
	public RotationGizmo(Camera view){
		super(view);
		if(SceneManager.meshes.get("rot_wheel") == null){
			SceneManager.meshes.put("rot_wheel", new Torus(WHEEL_RADIUS, .5f, 40));
		}
//		wheelRadius = WHEEL_RADIUS;
		//currently the collision meshes for the wheels are pure hulls and not decomposed for the torus wheels
		xyWheel = new Entity(SceneManager.meshes.get("rot_wheel"), true);
		xyWheel.getTransform().rotate(1,0,0,90);
		yzWheel = new Entity(SceneManager.meshes.get("rot_wheel"), true);
		yzWheel.getTransform().rotate(0,0,1,90);
		xzWheel = new Entity(SceneManager.meshes.get("rot_wheel"), true);
	}
	
	@Override
	public boolean isSelected(Ray click){
		//perform each collision check, starting with the center sphere
		return CollisionDetector.intersects(click, center.getCollider()).areColliding()
				|| CollisionDetector.intersects(click, xyWheel.getCollider()).areColliding() 
				|| CollisionDetector.intersects(click, yzWheel.getCollider()).areColliding() 
				|| CollisionDetector.intersects(click, xzWheel.getCollider()).areColliding(); 
	}
	
	@Override
	public void render(ShaderProgram program){
		super.render(program);
		//set scale position for rendering
		xyWheel.getTransform().setTranslation(target.getPos());
		yzWheel.getTransform().setTranslation(target.getPos());
		xzWheel.getTransform().setTranslation(target.getPos());
		//scale the gizmo for better interaction relative to the camera
		float scale = VecUtil.subtract(view.getPos(), target.getPos()).length()/viewScale;///(view.getHeight()/view.getAspect()/5);
		//set the scale of the wheels to match this factor
		xyWheel.getTransform().setScale(scale);
		yzWheel.getTransform().setScale(scale);
		xzWheel.getTransform().setScale(scale);
		
		program.setUniform("model", xyWheel.getTransform().getMatrix());
		program.setUniform("color", 1,0,0);
		SceneManager.meshes.get("rot_wheel").render();
		program.setUniform("model", yzWheel.getTransform().getMatrix());
		program.setUniform("color", 0,1,0);
		SceneManager.meshes.get("rot_wheel").render();
		program.setUniform("model", xzWheel.getTransform().getMatrix());
		program.setUniform("color", 0,0,1);
		SceneManager.meshes.get("rot_wheel").render();
	}
	
	@Override
	public void onMouseMove(Window window, double xpos, double ypos, double prevX, double prevY) {
		//first check to make sure there is a target to modify
		if(TransformGizmo.target != null && modifier == ROTATE){
			Vec3 planeNormal = null;
			//check which modifier is active
			switch(activeController){
				case CENTER://when the center sphere is selected
					planeNormal = view.getForwardVec();
					break;
				case X_AXIS://when the xy wheel is selected
					planeNormal = Transform.zAxis;
					break;
				case Y_AXIS://when the yz wheel is selected
					planeNormal = Transform.xAxis;
					break;
				case Z_AXIS://when the xz wheel is selected
					planeNormal = Transform.yAxis;
					break;
			}
			if(planeNormal != null){
				//get the Ray from the previous movement
				Ray preMoveRay = view.genRay((float)prevX, (float)prevY);
				//get the Ray for the current movement
				Ray moveRay = view.genRay((float)xpos, (float)ypos);
				//project both rays onto the plane
				float d = CollisionDetector.depth(preMoveRay, planeNormal, target.getPos());
				Vec3 prevPoint = preMoveRay.getDirection().scale(d);
				//repeat for the move ray
				d = CollisionDetector.depth(moveRay, planeNormal, target.getPos());
				Vec3 curPoint = moveRay.getDirection().scale(d);
				//move the points to be relative to where they were emitted
				prevPoint.add(preMoveRay.getPos());
				curPoint.add(moveRay.getPos());
				
				//use the dot product between the vectors formed by the click points and the target center as the angle
				//and the plane normal as the axis
				curPoint.subtract(target.getPos()).normalize();
				prevPoint.subtract(target.getPos()).normalize();
				//create a quaternion to rotate between the previous rotation end and the new rotation end
				Transform trans = new Transform().rotate(Quaternion.interpolate(prevPoint, curPoint));
				target.transform(trans);
			}
		}//if there isn't anything to modify then do nothing
	}
	
	@Override
	public void onMousePress(Window window, MouseButton button, boolean isRepeat, ModKey[] mods){
		if(button == MouseButton.LEFT && modifier == ROTATE && target != null){
			//first create the ray to test collision with
			Ray clickRay = view.genRay((float)window.cursorX, (float)window.cursorY);
			//perform each collision check, starting with the center sphere
			if(CollisionDetector.intersects(clickRay, center.getCollider()).areColliding()){
				activeController = ActiveControl.CENTER;
			}else if(CollisionDetector.intersects(clickRay, xyWheel.getCollider()).areColliding()){
				activeController = ActiveControl.X_AXIS;
			}else if(CollisionDetector.intersects(clickRay, yzWheel.getCollider()).areColliding()){
				activeController = ActiveControl.Y_AXIS;
			}else if(CollisionDetector.intersects(clickRay, xzWheel.getCollider()).areColliding()){
				activeController = ActiveControl.Z_AXIS;
			}else{
				activeController = ActiveControl.NO_CONTROLLER;
			}
		}
	}
}
