package core.gizmo;

import physics.collision.Ray;
import mesh.Arrow;
import shaders.ShaderProgram;
import windowing.Window;
import core.Camera;
import core.SpatialAsset;

public class TranslationGizmo extends TransformGizmo{

	private Arrow xaxis, yaxis, zaxis;
	
	public TranslationGizmo(){
		super();
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
	}
	
	@Override
	public void setScale(float factor){
		super.setScale(factor);
		xaxis.setScale(factor);
		yaxis.setScale(factor);
		zaxis.setScale(factor);
	}
	
	@Override
	public boolean isSelected(Ray clickRay){
		//first check if the center sphere was selected
		if(super.isSelected(clickRay)){
			return true;
		}else{
			//if not then check the scalar modifiers individually
			if(xaxis.colliding(clickRay)){
				activeModifier = 'x';
				return true;
			}else if(yaxis.colliding(clickRay)){
				activeModifier = 'y';
				return true;
			}else if(zaxis.colliding(clickRay)){
				activeModifier = 'z';
				return true;
			}else{
				activeModifier = 0;
			}
		}
		return false;
	}
	
	@Override
	public void onMouseMove(Window window, double xpos, double ypos) {
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
}
