package core.gizmo;

import events.keyboard.ModKey;
import events.mouse.MouseButton;
import glMath.Transform;
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
		if(target != null){
			//if we do then we must first undo the transforms applied to the gizmo from that object
			Transform trans = new Transform();
			//first undo the rotations applied to the gizmo
			trans.setOrientation(target.getTransform().getOrientation().conjugate());
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
	}
	
	@Override
	public void setScale(float factor){
		super.setScale(factor);
		xaxis.setScale(factor);
		yaxis.setScale(factor);
		zaxis.setScale(factor);
	}
	
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
