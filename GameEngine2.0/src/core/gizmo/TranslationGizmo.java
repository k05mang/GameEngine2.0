package core.gizmo;

import glMath.Transform;
import glMath.vectors.Vec3;
import mesh.Arrow;
import mesh.primitives.geometry.Sphere;
import shaders.ShaderProgram;

public class TranslationGizmo extends TransformGizmo{

	private Arrow xaxis, yaxis, zaxis;
	
	public TranslationGizmo(){
		this(0,0,0);
	}
	
	public TranslationGizmo(float x, float y, float z){
		super(x, y, z);
		xaxis = new Arrow(10, x, y, z, 1,0,0, 1,0,0);
		yaxis = new Arrow(10, x, y, z, 0,1,0, 0,1,0);
		zaxis = new Arrow(10, x, y, z, 0,0,1, .24f,.4f,.61f);
	}

	@Override
	public void setScale(float scale){
		super.setScale(scale);
		xaxis.setScale(scale);
		yaxis.setScale(scale);
		zaxis.setScale(scale);
	}

	@Override
	public void setPos(Vec3 pos){
		setPos(pos.x, pos.y, pos.z);
	}

	@Override
	public void setPos(float x, float y, float z){
		super.setPos(x, y, z);
		xaxis.setPos(x, y, z);
		yaxis.setPos(x, y, z);
		zaxis.setPos(x, y, z);
	}

	@Override
	public void transform(Transform trans){
		super.transform(trans);
		xaxis.transform(trans);
		yaxis.transform(trans);
		zaxis.transform(trans);
	}

	@Override
	public void render(ShaderProgram program){
		super.render(program);
		xaxis.render(program);
		yaxis.render(program);
		zaxis.render(program);
	}
}
