package core.gizmo;

import events.keyboard.ModKey;
import events.mouse.MouseButton;
import glMath.Transform;
import glMath.VecUtil;
import glMath.vectors.Vec3;
import mesh.Arrow;
import physics.collision.CollisionDetector;
import physics.collision.Ray;
import shaders.ShaderProgram;
import windowing.Window;
import core.Camera;
import core.SpatialAsset;

public class ScaleGizmo extends TransformGizmo {
	private Arrow xaxis, yaxis, zaxis;
	
	public ScaleGizmo(Camera view){
		super(view);
		xaxis = new Arrow(10, 0,0,0, 1,0,0, 1,0,0, true);
		yaxis = new Arrow(10, 0,0,0, 0,1,0, 0,1,0, true);
		zaxis = new Arrow(10, 0,0,0, 0,0,1, 0,0,1, true);
	}

	@Override
	public void render(ShaderProgram program){
		super.render(program);
		xaxis.render(program);
		yaxis.render(program);
		zaxis.render(program);
	}
	
	@Override
	public void bind(SpatialAsset target){
		//see if we have a pre-existing target we are bound to
		if(TransformGizmo.target != null){
			//if we do then we must first undo the transforms applied to the gizmo from that object
			Transform trans = new Transform();
			//first undo the rotations applied to the gizmo
			trans.setOrientation(TransformGizmo.target.getTransform().getOrientation().conjugate());
			//apply changes
			xaxis.transform(trans);
			yaxis.transform(trans);
			zaxis.transform(trans);
			//undo the translations
			xaxis.setPos(0,0,0);
			yaxis.setPos(0,0,0);
			zaxis.setPos(0,0,0);
		}
		super.bind(target);
		//get the targets orientation and position
		Transform trans = new Transform(target.getTransform());
		trans.setScale(1);
		//transforms the axis controls to align with the object
		xaxis.transform(trans);
		yaxis.transform(trans);
		zaxis.transform(trans);
		//scale the gizmo for better interaction relative to the camera
		float scale = VecUtil.subtract(view.getEye(), target.getPos()).length()/200f;
		//set the scale of the arrows to match this factor
		xaxis.setScale(scale);
		yaxis.setScale(scale);
		zaxis.setScale(scale);
	}
	
//	@Override
//	public void setScale(float factor){
//		super.setScale(factor);
//		xaxis.setScale(factor);
//		yaxis.setScale(factor);
//		zaxis.setScale(factor);
//	}
	
	@Override
	public void onMouseMove(Window window, double xpos, double ypos, double prevX, double prevY) {
		//first check to make sure there is a target to modify
		if(this.target != null){
			Vec3 planeNormal = null;
			//check which modifier is active
			switch(activeModifier){
				case 'c'://when the center sphere is selected
					planeNormal = view.getForwardVec();
					break;
				case 'x'://when the x axis is selected
					planeNormal = Transform.zAxis;
					break;
				case 'y'://when the y axis is selected
					planeNormal = Transform.zAxis;
					break;
				case 'z'://when the z axis is selected
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
				Transform trans = new Transform();
				float scale = VecUtil.subtract(view.getEye(), target.getPos()).length()/200f;
				switch(activeModifier){
					case 'c':
						//get the difference in lengths from the current point to the previous point relative to the targets center
						trans.scale(1+curPoint.subtract(target.getPos()).length()-prevPoint.subtract(target.getPos()).length());
						break;
					case 'x'://when the x axis is selected
						trans.scale(1+curPoint.subtract(prevPoint).x, 1, 1);
						break;
					case 'y'://when the y axis is selected
						trans.scale(1, 1-curPoint.subtract(prevPoint).y, 1);
						break;
					case 'z'://when the z axis is selected
						trans.scale(1, 1, 1+curPoint.subtract(prevPoint).z);
						break;
				}
				target.transform(trans);
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
		}else if(xaxis.colliding(clickRay)){
			activeModifier = 'x';
		}else if(yaxis.colliding(clickRay)){
			activeModifier = 'y';
		}else if(zaxis.colliding(clickRay)){
			activeModifier = 'z';
		}else{
			activeModifier = 0;
		}
	}
}
