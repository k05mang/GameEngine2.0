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
import glMath.Transform;
import glMath.VecUtil;
import glMath.vectors.Vec3;

public class RotationGizmo extends TransformGizmo {

	private static final float WHEEL_RADIUS = 10f;
	private float wheelRadius;
	private Entity xyWheel, yzWheel, xzWheel;
	
	public RotationGizmo(Camera view){
		super(view);
		if(SceneManager.meshes.get("rot_wheel") == null){
			SceneManager.meshes.put("rot_wheel", new Torus(WHEEL_RADIUS, .5f, 40));
		}
		wheelRadius = WHEEL_RADIUS;
		//currently the collision meshes for the wheels are pure hulls and not decomposed for the torus wheels
		xyWheel = new Entity(SceneManager.meshes.get("rot_wheel"), true);
		xyWheel.getTransform().rotate(1,0,0,90);
		yzWheel = new Entity(SceneManager.meshes.get("rot_wheel"), true);
		yzWheel.getTransform().rotate(0,0,1,90);
		xzWheel = new Entity(SceneManager.meshes.get("rot_wheel"), true);
	}

	@Override
	public void render(ShaderProgram program){
		super.render(program);
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
	public void bind(SpatialAsset target){
		super.bind(target);
		xyWheel.getTransform().setTranslation(target.getPos());
		yzWheel.getTransform().setTranslation(target.getPos());
		xzWheel.getTransform().setTranslation(target.getPos());
		//scale the gizmo for better interaction relative to the camera
		float scale = VecUtil.subtract(view.getPos(), target.getPos()).length()/200f;
		//set the scale of the wheels to match this factor
		xyWheel.getTransform().setScale(scale);
		yzWheel.getTransform().setScale(scale);
		xzWheel.getTransform().setScale(scale);
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
		if(TransformGizmo.target != null){
			Vec3 planeNormal = null;
			//check which modifier is active
			switch(activeModifier){
				case 'c'://when the center sphere is selected
					planeNormal = view.getForwardVec();
					break;
				case 'x'://when the xy wheel is selected
					planeNormal = Transform.zAxis;
					break;
				case 'y'://when the yz wheel is selected
					planeNormal = Transform.xAxis;
					break;
				case 'z'://when the xz wheel is selected
					planeNormal = Transform.yAxis;
					break;
			}
			if(planeNormal != null){
				//get the Ray from the previous movement
				Ray preMoveRay = view.genRay((float)prevX, (float)prevY);
				//get the Ray for the current movement
				Ray moveRay = view.genRay((float)xpos, (float)ypos);
				//compute the depth along the line for the point on the line that intersects the plane perpendicular to the camera
				//d = ((p0-L0)·n)/(L·n), where n is the plane normal(camera forward), L0 ray pos, L ray direction, p0 point on the plane
				//project both rays onto the plane
				float d = VecUtil.subtract(target.getPos(), preMoveRay.getPos()).dot(planeNormal)
						/preMoveRay.getDirection().dot(planeNormal);
				Vec3 prevPoint = preMoveRay.getDirection().scale(d);
				//repeat for the move ray
				d = VecUtil.subtract(target.getPos(), moveRay.getPos()).dot(planeNormal)
						/moveRay.getDirection().dot(planeNormal);
				Vec3 curPoint = moveRay.getDirection().scale(d);
				//move the points to be relative to where they were emitted
				prevPoint.add(preMoveRay.getPos());
				curPoint.add(moveRay.getPos());
				
				//use the dot product between the vectors formed by the click points and the target center as the angle
				//and the plane normal as the axis
				curPoint.subtract(target.getPos()).normalize();
				prevPoint.subtract(target.getPos()).normalize();
				//min/max used to cap the result which should never exceed 1 or -1 but can in instances result in 1.000001 due to floating point error
				float angle = (float)(Math.acos(Math.max(Math.min(curPoint.dot(prevPoint), 1), -1))*180/Math.PI);
				//now we need to determine if the motion was clockwise or counter clockwise, this is so the angle can be made
				//positive or negative depending on the motion
				angle *= prevPoint.cross(curPoint).normalize().dot(planeNormal);//the dot product of the points cross product will either be
				//1, if aligned with the plane normal or -1 if opposite
				Transform trans = new Transform().rotate(planeNormal, angle);
				target.transform(trans);
			}
		}//if there isn't anything to modify then do nothing
	}
	
	@Override
	public void onMousePress(Window window, MouseButton button, boolean isRepeat, ModKey[] mods){
		if(button == MouseButton.LEFT){
			//first create the ray to test collision with
			Ray clickRay = view.genRay((float)window.cursorX, (float)window.cursorY);
			//perform each collision check, starting with the center sphere
			if(CollisionDetector.intersects(clickRay, center)){
				activeModifier = 'c';
			}else if(CollisionDetector.intersects(clickRay, xyWheel)){
				activeModifier = 'x';
			}else if(CollisionDetector.intersects(clickRay, yzWheel)){
				activeModifier = 'y';
			}else if(CollisionDetector.intersects(clickRay, xzWheel)){
				activeModifier = 'z';
			}else{
				activeModifier = 0;
			}
		}
	}
}
