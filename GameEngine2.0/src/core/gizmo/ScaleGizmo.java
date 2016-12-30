package core.gizmo;

import static core.gizmo.TransformType.SCALE;
import events.keyboard.ModKey;
import events.mouse.MouseButton;
import glMath.Quaternion;
import glMath.Transform;
import glMath.VecUtil;
import glMath.vectors.Vec3;
import mesh.Arrow;
import physics.collision.CollisionDetector;
import physics.collision.Ray;
import shaders.ShaderProgram;
import windowing.Window;
import core.Camera;

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
		//get the targets orientation
		Transform trans = new Transform();
		//undo the previous orientation
		//first determine what to use when calculating the inverse rotation
		//in some situations the x axis might have not changed but others have
		Quaternion rotation = Quaternion.interpolate(xaxis.getDirection(), Transform.xAxis);
		//if the angle of rotation for the quaternion is 0 then the x axis cannot be used in calculating the inverse rotation
		if(rotation.getAngle() == 0){
			//in which case use either of the other axis to test against since this means it was a rotation about the x axis
			rotation = Quaternion.interpolate(zaxis.getDirection(), Transform.zAxis);
		}
		trans.rotate(rotation);
		trans.rotate(target.getTransform());
		//transforms the axis controls to align with the object
		xaxis.transform(trans);
		yaxis.transform(trans);
		zaxis.transform(trans);
		//set the position of the axis controllers
		xaxis.setPos(target.getPos());
		yaxis.setPos(target.getPos());
		zaxis.setPos(target.getPos());
		//scale the gizmo for better interaction relative to the camera
		float scale = VecUtil.subtract(view.getPos(), target.getPos()).length()/viewScale;///(view.getHeight()/view.getAspect()/5);
		//set the scale of the arrows to match this factor
		xaxis.setScale(scale);
		yaxis.setScale(scale);
		zaxis.setScale(scale);
		
		xaxis.render(program);
		yaxis.render(program);
		zaxis.render(program);
	}
	
	@Override
	public void onMouseMove(Window window, double xpos, double ypos, double prevX, double prevY) {
		//first check to make sure there is a target to modify
		if(TransformGizmo.target != null && modifier == SCALE){
			Vec3 planeNormal = view.getForwardVec();
			//check which modifier is active
//			switch(activeController){
//				case CENTER://when the center sphere is selected
//					planeNormal = view.getForwardVec();
//					break;
//				case X_AXIS://when the x axis is selected
//					planeNormal = Transform.zAxis;
//					break;
//				case Y_AXIS://when the y axis is selected
//					planeNormal = Transform.zAxis;
//					break;
//				case Z_AXIS://when the z axis is selected
//					planeNormal = Transform.yAxis;
//					break;
//			}
			if(activeController != NO_CONTROLLER){
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
				
				Transform trans = new Transform();
				//scalar factor used to offset the amount of applied scaling when the camera is panned far out
				float scale = 1+(curPoint.subtract(target.getPos()).length()-prevPoint.subtract(target.getPos()).length())/
						VecUtil.subtract(view.getPos(), target.getPos()).length();
				switch(activeController){
					case CENTER:
						//get the difference in lengths from the current point to the previous point relative to the targets center
						trans.scale(scale);
						break;
					case X_AXIS://when the x axis is selected
						trans.scale(scale, 1, 1);
						break;
					case Y_AXIS://when the y axis is selected
						trans.scale(1, scale, 1);
						break;
					case Z_AXIS://when the z axis is selected
						trans.scale(1, 1, scale);
						break;
				}
				target.transform(trans);
			}
		}//if there isn't anything to modify then do nothing
	}
	
	@Override
	public void onMousePress(Window window, MouseButton button, boolean isRepeat, ModKey[] mods){
		if(button == MouseButton.LEFT && modifier == SCALE){
			//first create the ray to test collision with
			Ray clickRay = view.genRay((float)window.cursorX, (float)window.cursorY);
			//perform each collision check, starting with the center sphere
			if(CollisionDetector.intersects(clickRay, center)){
				activeController = CENTER;
			}else if(xaxis.colliding(clickRay)){
				activeController = X_AXIS;
			}else if(yaxis.colliding(clickRay)){
				activeController = Y_AXIS;
			}else if(zaxis.colliding(clickRay)){
				activeController = Z_AXIS;
			}else{
				activeController = NO_CONTROLLER;
			}
		}
	}
}
