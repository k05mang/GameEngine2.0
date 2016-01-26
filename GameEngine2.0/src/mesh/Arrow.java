package mesh;

import glMath.Quaternion;
import glMath.Transform;
import glMath.VecUtil;
import glMath.vectors.Vec3;
import mesh.primitives.geometry.Cone;
import mesh.primitives.geometry.Cylinder;
import renderers.RenderMode;
import shaders.ShaderProgram;
import core.Resource;

public class Arrow implements Resource{
	
	private Cylinder shaft;
	private Cone tip;
	private static final int TIP_LENGTH = 10;
	private float length;
	private Vec3 direction, position;
	
	public Arrow(float length, Vec3 position, Vec3 direction){
		this(length, position.x, position.y, position.z, direction.x, direction.y, direction.z);
	}
	
	public Arrow(float length, float posx, float posy, float posz, float dirx, float diry, float dirz){
		this.length = length;
		position = new Vec3(posx, posy, posz);
		direction = new Vec3(dirx, diry, dirz);
		direction.normalize();
		shaft = new Cylinder(1, length, 10, RenderMode.TRIANGLES);
		tip = new Cone(2, TIP_LENGTH, 10, false, RenderMode.TRIANGLES);
		Transform trans = new Transform();
		//first orient the meshes
		Vec3 axis = direction.cross(VecUtil.yAxis);
		float angle = (float)(Math.acos(VecUtil.yAxis.dot(direction))*180/Math.PI);
		trans.rotate(angle == 180 ? VecUtil.xAxis : axis, angle);
		//translate the cylinder
		float halfLength = length/2;
		trans.translate(halfLength*direction.x, halfLength*direction.y, halfLength*direction.z);
		shaft.transform(trans);
		//translate the tip
		trans.translate(
				halfLength*direction.x+Math.signum(direction.x)*(TIP_LENGTH/2), 
				halfLength*direction.y+Math.signum(direction.y)*(TIP_LENGTH/2), 
				halfLength*direction.z+Math.signum(direction.z)*(TIP_LENGTH/2));
		tip.transform(trans);
		
		//translate both to the position
		Transform posTrans = new Transform().translate(position);
		shaft.transform(posTrans);
		tip.transform(posTrans);
		
	}
	
	public void render(ShaderProgram program){
		program.setUniform("model", shaft.getModelView());
		shaft.render();
		program.setUniform("model", tip.getModelView());
		tip.render();
	}
	
	public void setLength(float length){
		float scale = length/this.length;//calculate how much to scale the cylinder by
		Transform trans = new Transform().scale(1, scale, 1);
		Vec3 translation = new Vec3(direction);
		translation.scale((length-this.length)/2);
		trans.translate(translation);
		shaft.transform(trans);
		trans.setScale(1);
		trans.translate(translation);
		tip.transform(trans);
		this.length = length;
	}
	
	public float getLength(){
		return length;
	}
	
	public void translate(float x, float y, float z){
		Transform trans = new Transform().translate(x, y, z);
		shaft.transform(trans);
		tip.transform(trans);
		position.add(x, y, z);
	}
	
	public void translate(Vec3 translation){
		translate(translation.x, translation.y, translation.z);
	}
	
	public void rotate(float x, float y, float z, float angle){
		Transform orient = new Transform().rotate(x, y, z, -angle);
		//orient the models
		shaft.transform(orient);
		tip.transform(orient);
		//translate the models 
		Quaternion rotation = Quaternion.fromAxisAngle(x, y, z, angle);
		//compute the center points of the shaft and tip relative to the origin
		Vec3 shaftOrigin = VecUtil.subtract(shaft.getPos(), position);
		Vec3 tipOrigin = VecUtil.subtract(tip.getPos(), position);
		//compute the center points after rotating them
		Vec3 shaftPoint = rotation.multVec(shaftOrigin);
		Vec3 tipPoint = rotation.multVec(tipOrigin);
		//translate the shaft based on the vector from the shaft origin to the new shaft point
		Transform trans = new Transform().translate(VecUtil.subtract(shaftPoint, shaftOrigin));
		shaft.transform(trans);
		//translate the tip using the same process as the shaft
		trans.setTranslation(0, 0, 0);
		trans.translate(VecUtil.subtract(tipPoint, tipOrigin));
		tip.transform(trans);
		//store the new direction of the vector
		direction.set(shaftPoint);
		direction.normalize();
	}
	
	public void rotate(Vec3 axis, float angle){
		rotate(axis.x, axis.y, axis.z, angle);
	}

	@Override
	public void delete() {
		shaft.delete();
		tip.delete();
	}
}
