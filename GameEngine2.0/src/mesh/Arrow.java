package mesh;

import glMath.Transform;
import glMath.VecUtil;
import glMath.vectors.Vec3;
import mesh.primitives.geometry.Cone;
import mesh.primitives.geometry.Cylinder;
import renderers.RenderMode;
import core.Resource;

public class Arrow implements Resource{
	
	private Cylinder shaft;
	private Cone tip;
	
	public Arrow(float length, Vec3 position, Vec3 direction){
		this(length, position.x, position.y, position.z, direction.x, direction.y, direction.z);
	}
	
	public Arrow(float length, float posx, float posy, float posz, float dirx, float diry, float dirz){
		Vec3 dirNorm = new Vec3(dirx, diry, dirz);
		dirNorm.normalize();
		shaft = new Cylinder(25, length, 10, RenderMode.TRIANGLES);
		tip = new Cone(50, length/10, 10, true, RenderMode.TRIANGLES);
		Transform trans = new Transform();
		//first orient the meshes
		Vec3 axis = VecUtil.yAxis.cross(dirNorm);
		float angle = (float)Math.acos(VecUtil.yAxis.dot(dirNorm));//division is to normalize the vector
		trans.rotate(axis, angle);
		//translate the cylinder
		float halfLength = (length/2);
		trans.translate(halfLength*dirNorm.x, halfLength*dirNorm.y, halfLength*dirNorm.z);
		shaft.transform(trans);
		//translate the tip
		trans.translate(halfLength*dirNorm.x, halfLength*dirNorm.y, halfLength*dirNorm.z);
		tip.transform(trans);
		
		//translate both to the position
		trans.translate(posx, posy, posz);
		shaft.transform(trans);
		tip.transform(trans);
		
	}
	
	public void render(){
		shaft.render();
		tip.render();
	}

	@Override
	public void delete() {
		shaft.delete();
		tip.delete();
	}
}
