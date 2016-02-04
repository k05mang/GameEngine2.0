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

public class Arrow{
	
	public static final Cylinder shaft = new Cylinder(1, 1, 10);
	public static final Cone tip = new Cone(1, 1, 10, false);
	private Transform shaftTrans, tipTrans;
	private float length, tipLength;
	private Vec3 direction, position, color;
	
	public Arrow(float length, Vec3 position, Vec3 direction, Vec3 color){
		this(length, position.x, position.y, position.z, direction.x, direction.y, direction.z, color.x, color.y, color.z);
	}
	
	public Arrow(float length, float posx, float posy, float posz, float dirx, float diry, float dirz, float r, float g, float b){
		this.length = length;
		tipLength = 2.5f;
		color = new Vec3(r, g, b);
		position = new Vec3(posx, posy, posz);
		direction = new Vec3(dirx, diry, dirz);
		direction.normalize();
		//create the initial transforms for the shaft and tip
		shaftTrans = new Transform().scale(.5f,length/2, .5f);
		tipTrans = new Transform().scale(1,tipLength, 1);
		//first orient the meshes
		Vec3 axis = VecUtil.yAxis.cross(direction);
		float angle = (float)(Math.acos(VecUtil.yAxis.dot(direction))*180/Math.PI);
		shaftTrans.rotate(angle == 180 ? VecUtil.xAxis : axis, angle);
		tipTrans.rotate(angle == 180 ? VecUtil.xAxis : axis, angle);
		//translate the cylinder
		float halfLength = length/2;
		shaftTrans.translate(halfLength*direction.x, halfLength*direction.y, halfLength*direction.z);
		//translate the tip
		tipTrans.translate(
				length*direction.x+Math.signum(direction.x)*(tipLength/2), 
				length*direction.y+Math.signum(direction.y)*(tipLength/2), 
				length*direction.z+Math.signum(direction.z)*(tipLength/2));
		
		//translate both to the position
		shaftTrans.translate(position);
		tipTrans.translate(position);
		
	}
	
	public void render(ShaderProgram program){
		program.setUniform("model", shaftTrans.getTransform());
		program.setUniform("color", color);
		shaft.render();
		program.setUniform("model", tipTrans.getTransform());
		program.setUniform("color", color);
		tip.render();
	}
	
	public void setLength(float length){
		float scale = length/this.length;//calculate how much to scale the cylinder by
		shaftTrans.scale(1, scale, 1);
		Vec3 translation = new Vec3(direction);
		translation.scale((length-this.length)/2);
		shaftTrans.translate(translation);
		tipTrans.translate(translation.scale(2));
		this.length = length;
	}
	
	public float getLength(){
		return length;
	}
	
	public void translate(float x, float y, float z){
		shaftTrans.translate(x, y, z);
		tipTrans.translate(x, y, z);
		position.add(x, y, z);
	}
	
	public void translate(Vec3 translation){
		translate(translation.x, translation.y, translation.z);
	}
	
	public void rotate(float x, float y, float z, float angle){
		//orient the models
		shaftTrans.rotate(x, y, z, angle);
		tipTrans.rotate(x, y, z, angle);
		//translate the models 
		Quaternion rotation = Quaternion.fromAxisAngle(x, y, z, angle);
		//compute the center points of the shaft and tip relative to the origin
		Vec3 shaftOrigin = VecUtil.subtract(shaftTrans.getTranslation(), position);
		Vec3 tipOrigin = VecUtil.subtract(tipTrans.getTranslation(), position);
		//compute the center points after rotating them
		Vec3 shaftPoint = rotation.multVec(shaftOrigin);
		Vec3 tipPoint = rotation.multVec(tipOrigin);
		//translate the shaft based on the vector from the shaft origin to the new shaft point
		shaftTrans.translate(VecUtil.subtract(shaftPoint, shaftOrigin));
		//translate the tip using the same process as the shaft
		tipTrans.translate(VecUtil.subtract(tipPoint, tipOrigin));
		//store the new direction of the vector
		direction.set(shaftPoint);
		direction.normalize();
	}
	
	public void setPos(Vec3 pos){
		setPos(pos.x, pos.y, pos.z);
	}
	
	public void setPos(float x, float y, float z){
		translate(x-position.x, y-position.y, z-position.z);
	}
	
	public Vec3 getPos(){
		return position;
	}
	
	public void rotate(Vec3 axis, float angle){
		rotate(axis.x, axis.y, axis.z, angle);
	}
	
	public void scale(float scale){
		//perform the uniform scaling
		shaftTrans.scale(scale);
		tipTrans.scale(scale);
		float newLength = scale*length;
		float newTipLength = scale*tipLength;
		//translate the meshes to maintain the position
		shaftTrans.translate((newLength-length)/2*direction.x, (newLength-length)/2*direction.y, (newLength-length)/2*direction.z);
		tipTrans.translate(
				(newLength-length+(newTipLength-tipLength)/2)*direction.x, 
				(newLength-length+(newTipLength-tipLength)/2)*direction.y, 
				(newLength-length+(newTipLength-tipLength)/2)*direction.z
				);
		length = newLength;
		tipLength = newTipLength;
	}
	
	public void setColor(float r, float g, float b){
		color.set(r, g, b);
	}
	
	public void setColor(Vec3 color){
		setColor(color.x, color.y, color.z);
	}
	
	public Vec3 getColor(){
		return color;
	}

	public static void delete() {
		shaft.delete();
		tip.delete();
	}
}
