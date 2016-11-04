package core.gizmo;

import glMath.Transform;
import mesh.Arrow;
import physics.collision.Ray;
import shaders.ShaderProgram;
import windowing.Window;
import core.Entity;

public class ScaleGizmo extends TransformGizmo {
	private Arrow xaxis, yaxis, zaxis;
	
	public ScaleGizmo(){
		super();
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
	public void bind(Entity target){
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
	public boolean isSelected(Ray clickRay){
		
	}
	
	@Override
	public void mouseMove(Window window, double xpos, double ypos) {
		//first check to make sure there is a target to modify
		if(this.target != null){
			//check to see if there is an active modifier, this will happen when the mouse is released
			if(activeModifier != null){
				//determine what activeModifier is being used at this moment
			}
		}//if there isn't anything to modify then do nothing
	}
}
