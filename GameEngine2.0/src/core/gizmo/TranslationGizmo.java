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

public class TranslationGizmo extends TransformGizmo{

	private Arrow xaxis, yaxis, zaxis;
	
	public TranslationGizmo(Camera view){
		super(view);
		xaxis = new Arrow(10, 0,0,0, 1,0,0, 1,0,0);
		yaxis = new Arrow(10, 0,0,0, 0,1,0, 0,1,0);
		zaxis = new Arrow(10, 0,0,0, 0,0,1, 0,0,1);
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
		super.bind(target);
		xaxis.setPos(target.getPos());
		yaxis.setPos(target.getPos());
		zaxis.setPos(target.getPos());
		//scale the gizmo for better interaction relative to the camera
		float scale = VecUtil.subtract(view.getPos(), target.getPos()).length()/200f;
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
		if(TransformGizmo.target != null){
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
				//move the points to be relative to where they were emitted
				prevPoint.add(preMoveRay.getPos());
				curPoint.add(moveRay.getPos());
				//adjust values based on whether we are restricting to a line
				switch(activeModifier){
					case 'x'://when the x axis is selected
						prevPoint.y = 0;
						curPoint.y = 0;
						break;
					case 'y'://when the y axis is selected
						prevPoint.x = 0;
						curPoint.x = 0;
						break;
					case 'z'://when the z axis is selected
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
		if(button == MouseButton.LEFT){
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
}
