package mesh;

import glMath.Quaternion;
import glMath.Transform;
import glMath.VecUtil;
import glMath.vectors.Vec3;
import mesh.primitives.geometry.Cone;
import mesh.primitives.geometry.Cube;
import mesh.primitives.geometry.Cylinder;
import renderers.RenderMode;
import shaders.ShaderProgram;
import core.Resource;
import core.SceneManager;

public class Arrow{
	
	private Transform shaftTrans, tipTrans;
	private float length, tipLength;
	private final float origLength, origTipLength;
	private Vec3 direction, position, color;
	private boolean useCube;
	private Mesh shaft, tip;
	
	public Arrow(float length, Vec3 position, Vec3 direction, Vec3 color){
		this(length, position.x, position.y, position.z, direction.x, direction.y, direction.z, color.x, color.y, color.z, false);
	}
	
	public Arrow(float length, float posx, float posy, float posz, float dirx, float diry, float dirz, float r, float g, float b){
		this(length, posx, posy, posz, dirx, diry, dirz, r, g, b, false);
	}
	
	public Arrow(float length, Vec3 position, Vec3 direction, Vec3 color, boolean cubeTip){
		this(length, position.x, position.y, position.z, direction.x, direction.y, direction.z, color.x, color.y, color.z);
	}
	
	public Arrow(float length, float posx, float posy, float posz, float dirx, float diry, float dirz, float r, float g, float b, boolean cubeTip){
		//add meshes to the meshes manager if they don't exist already
		//check if the shaft was added
		if(SceneManager.meshes.get("arrow_shaft") == null){
			SceneManager.meshes.put("arrow_shaft", new Cylinder(1, 1, 10));
		}
		//check if the pointed tip was added
		if(SceneManager.meshes.get("arrow_cone") == null){
			SceneManager.meshes.put("arrow_cone", new Cone(1, 1, 10, false));
		}
		//check if the cube tip was added
		if(SceneManager.meshes.get("arrow_cube") == null){
			SceneManager.meshes.put("arrow_cube", new Cube(1));
		}
		shaft = (Mesh)SceneManager.meshes.get("arrow_shaft");
		useCube = cubeTip;
		tip = useCube ? (Mesh)SceneManager.meshes.get("arrow_cube") : (Mesh)SceneManager.meshes.get("arrow_cone");
		this.length = length;
		origLength = length;
		origTipLength = useCube ? 2 : 2.5f;
		tipLength = origTipLength;
		color = new Vec3(r, g, b);
		position = new Vec3(posx, posy, posz);
		direction = new Vec3(dirx, diry, dirz);
		direction.normalize();
		//create the initial transforms for the shaft and tip
		shaftTrans = new Transform().scale(.5f, length, .5f);
		float xzScale = useCube ? tipLength : 1;
		tipTrans = new Transform().scale(xzScale, tipLength, xzScale);
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
	
	public void transform(Transform trans){
		shaftTrans.transform(trans);
		tipTrans.transform(trans);
		//translate
		position.add(trans.getTranslation());

		//scale
		float newLength = trans.getScalars().y*length;
		float newTipLength = trans.getScalars().y*tipLength;
		//translate the meshes to maintain the position
		shaftTrans.translate((newLength-length)/2*direction.x, (newLength-length)/2*direction.y, (newLength-length)/2*direction.z);
		tipTrans.translate(
				(newLength-length+(newTipLength-tipLength)/2)*direction.x, 
				(newLength-length+(newTipLength-tipLength)/2)*direction.y, 
				(newLength-length+(newTipLength-tipLength)/2)*direction.z
				);
		length = newLength;
		tipLength = newTipLength;
		
		//rotate
		//translate the models 
		Quaternion rotation = trans.getOrientation();
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
	
	public void setScale(float scale){
		float newLength = scale*origLength;
		float newTipLength = scale*origTipLength;
		//translate the meshes to maintain the position
		shaftTrans.translate((newLength-length)/2*direction.x, (newLength-length)/2*direction.y, (newLength-length)/2*direction.z);
		tipTrans.translate(
				(newLength-length+(newTipLength-tipLength)/2)*direction.x, 
				(newLength-length+(newTipLength-tipLength)/2)*direction.y, 
				(newLength-length+(newTipLength-tipLength)/2)*direction.z
				);
		shaftTrans.setScale(.5f, origLength, .5f);
		float xzScale = useCube ? origTipLength : 1;
		tipTrans.setScale(xzScale, origTipLength, xzScale);
		shaftTrans.scale(scale);
		tipTrans.scale(scale);
		length = newLength;
		tipLength = newTipLength;
	}
	
	public void setPos(Vec3 pos){
		setPos(pos.x, pos.y, pos.z);
	}
	
	public void setPos(float x, float y, float z){
		shaftTrans.translate(x-position.x, y-position.y, z-position.z);
		tipTrans.translate(x-position.x, y-position.y, z-position.z);
		position.set(x, y, z);
	}
	
	public Vec3 getPos(){
		return position;
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
}
