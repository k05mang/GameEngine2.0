package core.gizmo;

import glMath.Transform;
import glMath.vectors.Vec3;
import mesh.Arrow;
import shaders.ShaderProgram;
import core.Camera;
import core.SpatialAsset;

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
	public void bind(SpatialAsset target){
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
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void select(Camera view, float screenX, float screenY) {
		// TODO Auto-generated method stub

	}

}
