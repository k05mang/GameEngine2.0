package core.gizmo;

import glMath.VecUtil;
import glMath.transforms.Transform;
import glMath.vectors.Vec3;
import mesh.Arrow;
import physics.collision.CollisionDetector;
import physics.collision.Ray;
import shaders.ShaderProgram;
import windowing.Window;
import windowing.events.keyboard.ModKey;
import windowing.events.mouse.MouseButton;
import core.Camera;
import core.gizmo.TransformGizmo.ActiveControl;

import static core.gizmo.TransformType.TRANSLATE;

public class TranslationGizmo extends TransformGizmo{

	private Arrow xaxis, yaxis, zaxis;
	
	public TranslationGizmo(Camera view){
		super(view);
		xaxis = new Arrow(controllerLength, 0,0,0, 1,0,0, 1,0,0);
		yaxis = new Arrow(controllerLength, 0,0,0, 0,1,0, 0,1,0);
		zaxis = new Arrow(controllerLength, 0,0,0, 0,0,1, 0,0,1);
	}

	@Override
	public void render(ShaderProgram program){
		super.render(program);

		//set position and scale
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
	public boolean isSelected(Ray click){
		//perform each collision check, starting with the center sphere
		return CollisionDetector.intersects(click, center.getCollider()).areColliding() 
				|| xaxis.colliding(click) 
				|| yaxis.colliding(click) 
				|| zaxis.colliding(click);
	}
	
	@Override
	public void onMouseMove(Window window, double xpos, double ypos, double prevX, double prevY) {
		//first check to make sure there is a target to modify
		if(TransformGizmo.target != null && modifier == TRANSLATE){
			Vec3 planeNormal = null;
			//check which modifier is active
			switch(activeController){
				case CENTER://when the center sphere is selected
					planeNormal = view.getForwardVec();
					break;
				case X_AXIS://when the x axis is selected
					planeNormal = Transform.zAxis;
					break;
				case Y_AXIS://when the y axis is selected
					planeNormal = Transform.zAxis;
					break;
				case Z_AXIS://when the z axis is selected
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
				//adjust values based on whether we are restricting to a line
				switch(activeController){
					case X_AXIS://when the x axis is selected
						prevPoint.y = 0;
						curPoint.y = 0;
						break;
					case Y_AXIS://when the y axis is selected
						prevPoint.x = 0;
						curPoint.x = 0;
						break;
					case Z_AXIS://when the z axis is selected
						prevPoint.x = 0;
						curPoint.x = 0;
						break;
				}
				//then subtract the two click positions to get the resulting translation
				//apply the translation to the appropriate objects
				Transform trans = new Transform().translate(curPoint.subtract(prevPoint));
				target.transform(trans);
				center.transform(trans);
				xaxis.transform(trans);
				yaxis.transform(trans);
				zaxis.transform(trans);
			}
		}//if there isn't anything to modify then do nothing
	}
	
	@Override
	public void onMousePress(Window window, MouseButton button, boolean isRepeat, ModKey[] mods){
		if(button == MouseButton.LEFT && modifier == TRANSLATE && target != null){
			//first create the ray to test collision with
			Ray clickRay = view.genRay((float)window.cursorX, (float)window.cursorY);
			//perform each collision check, starting with the center sphere
			if(CollisionDetector.intersects(clickRay, center.getCollider()).areColliding()){
				activeController = ActiveControl.CENTER;
			}else if(xaxis.colliding(clickRay)){
				activeController = ActiveControl.X_AXIS;
			}else if(yaxis.colliding(clickRay)){
				activeController = ActiveControl.Y_AXIS;
			}else if(zaxis.colliding(clickRay)){
				activeController = ActiveControl.Z_AXIS;
			}else{
				activeController = ActiveControl.NO_CONTROLLER;
			}
		}
	}
}
