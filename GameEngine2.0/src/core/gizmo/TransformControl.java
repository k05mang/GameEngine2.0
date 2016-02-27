package core.gizmo;

import glMath.Transform;
import glMath.vectors.Vec3;
import mesh.Arrow;
import mesh.primitives.geometry.Cube;
import mesh.primitives.geometry.Torus;
import renderers.RenderMode;
import shaders.ShaderProgram;

public class TransformControl {
	private Arrow xAxis, yAxis, zAxis;
	private static final float WHEEL_RADIUS = 10f;
	private float wheelRadius;
	public static final Torus rotWheel = new Torus(WHEEL_RADIUS, .5f, 40);
	public static final Cube scale = new Cube(1);
	private Transform xRot, yRot, zRot, cube;
	
	public TransformControl(Vec3 position){
		this(position.x, position.y, position.z);
	}
	
	public TransformControl(float x, float y, float z){
		wheelRadius = WHEEL_RADIUS;
		xRot = new Transform().translate(x, y, z).rotate(0,0,1,90); 
		yRot = new Transform().translate(x, y, z); 
		zRot = new Transform().translate(x, y, z).rotate(-1,0,0,90);
		cube = new Transform().translate(x, y, z).scale(WHEEL_RADIUS/5);

		//+WHEEL_RADIUS to place them on the rotation wheels
		xAxis = new Arrow(wheelRadius*1.5f, x+wheelRadius, y, z, 1,0,0, 1,0,.3f); 
		yAxis = new Arrow(wheelRadius*1.5f, x, y+wheelRadius, z, 0,1,0, .3f,1,0); 
		zAxis = new Arrow(wheelRadius*1.5f, x, y, z+wheelRadius, 0,0,1, .3f,0,1);
	}
	
	public void translate(float x, float y, float z){
		xRot.translate(x, y, z); 
		yRot.translate(x, y, z); 
		zRot.translate(x, y, z);
		cube.translate(x, y, z);
		
		Transform trans = new Transform().translate(x, y, z);
		xAxis.transform(trans); 
		yAxis.transform(trans); 
		zAxis.transform(trans);
	}
	
	public void translate(Vec3 trans){
		translate(trans.x, trans.y, trans.z);
	}
	
	public void setPos(float x, float y, float z){
		xRot.setTranslation(x, y, z); 
		yRot.setTranslation(x, y, z); 
		zRot.setTranslation(x, y, z);
		cube.setTranslation(x, y, z);
		
		//+wheelRadius to place them on the rotation wheels
		xAxis.setPos(x+wheelRadius, y, z); 
		yAxis.setPos(x, y+wheelRadius, z); 
		zAxis.setPos(x, y, z+wheelRadius);
	}
	
	public void setPos(Vec3 pos){
		setPos(pos.x, pos.y, pos.z);
	}
	
	public Vec3 getPos(){
		return cube.getTranslation();
	}
	
	public void scale(float scale){
		xRot.scale(scale); 
		yRot.scale(scale); 
		zRot.scale(scale);
		cube.scale(scale);

		Transform trans = new Transform().scale(scale);
		
		float newWheelRadius = scale*wheelRadius;
		trans.setTranslation(newWheelRadius-wheelRadius, 0,0); 
		xAxis.transform(trans); 
		trans.setTranslation(0, newWheelRadius-wheelRadius, 0); 
		yAxis.transform(trans); 
		trans.setTranslation(0, 0, newWheelRadius-wheelRadius);
		zAxis.transform(trans);
		
		wheelRadius = newWheelRadius;
	}
	
	public void setScale(float scale){
		scale(scale/(wheelRadius/WHEEL_RADIUS));
	}
	
	public void render(ShaderProgram shader){
		xAxis.render(shader); 
		yAxis.render(shader); 
		zAxis.render(shader);

		shader.setUniform("model", xRot.getTransform());
		shader.setUniform("color", 1,0,0);
		rotWheel.render();
		shader.setUniform("model", yRot.getTransform());
		shader.setUniform("color", 0,1,0);
		rotWheel.render();
		shader.setUniform("model", zRot.getTransform());
		shader.setUniform("color", 0,0,1);
		rotWheel.render();
		shader.setUniform("model", cube.getTransform());
		shader.setUniform("color", 1,0,1);
		scale.render();
	}
}
