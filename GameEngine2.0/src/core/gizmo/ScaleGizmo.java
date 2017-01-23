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
	private float origClickDist;//value to retain the original distance that the first click occurred, this distance will be the scale of 1.0
	
	public ScaleGizmo(Camera view){
		super(view);
		xaxis = new Arrow(controllerLength, 0,0,0, 1,0,0, 1,0,0, true);
		yaxis = new Arrow(controllerLength, 0,0,0, 0,1,0, 0,1,0, true);
		zaxis = new Arrow(controllerLength, 0,0,0, 0,0,1, 0,0,1, true);
	}

	@Override
	public void render(ShaderProgram program){
		super.render(program);
		//get the targets orientation
		Transform trans = new Transform();
		//undo the previous orientation
		trans.rotate(xaxis.getTransform().getOrientation().conjugate());
		//apply the target transform
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
			if(activeController != NO_CONTROLLER){
				//get the Ray from the previous movement
				Ray preMoveRay = view.genRay((float)prevX, (float)prevY);
				//get the Ray for the current movement
				Ray moveRay = view.genRay((float)xpos, (float)ypos);
				//project both rays onto the plane
				float d = CollisionDetector.depth(preMoveRay, view.getForwardVec(), target.getPos());
				Vec3 prevPoint = preMoveRay.getDirection().scale(d);
				//repeat for the move ray
				d = CollisionDetector.depth(moveRay, view.getForwardVec(), target.getPos());
				Vec3 curPoint = moveRay.getDirection().scale(d);
				//move the points to be relative to where they were emitted
				prevPoint.add(preMoveRay.getPos());
				curPoint.add(moveRay.getPos());
				
				Transform trans = new Transform();
				//scale factor for the mouse movement
				float scale = curPoint.subtract(target.getPos()).length()/origClickDist;
				float previousScale = prevPoint.subtract(target.getPos()).length()/origClickDist;//used to undo previous scaling factor
				scale /= previousScale;//scale*(1/previousScale) = scale/previousScale
				
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
		if(button == MouseButton.LEFT && modifier == SCALE && target != null){
			//first create the ray to test collision with
			Ray clickRay = view.genRay((float)window.cursorX, (float)window.cursorY);
			//set the original distance for the first click
			//first get the point where the click ray intersects the view plane
			Vec3 clickPoint = VecUtil.scale(clickRay.getDirection(), CollisionDetector.depth(clickRay, view.getForwardVec(), target.getPos()));
			clickPoint.add(clickRay.getPos());
			//then get the distance of this point from the targets position
			origClickDist = VecUtil.subtract(clickPoint, target.getPos()).length();
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
