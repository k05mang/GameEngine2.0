package core.gizmo;

import mesh.Arrow;
import shaders.ShaderProgram;
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
	public void update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void select(Camera view, float screenX, float screenY) {
		// TODO Auto-generated method stub
		
	}
}
