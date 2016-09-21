package core.gizmo;

import glMath.Transform;
import glMath.vectors.Vec3;
import mesh.Mesh;
import mesh.primitives.geometry.Sphere;
import physics.collision.Ray;
import shaders.ShaderProgram;
import core.Entity;
import core.SceneManager;
import core.SpatialAsset;

public abstract class TransformGizmo {

	protected static Entity center;
	protected static Vec3 centerColor = new Vec3(1,1,0);
	private static SpatialAsset target;
	private Object activeModifier;
	
	public TransformGizmo(){
		//add the center sphere to the list of meshes if it exists
		if(SceneManager.meshes.get("gizmo_center") == null){
			SceneManager.meshes.put("gizmo_center", new Sphere(1.5f, 20));
		}
		
		//additionally check if the center sphere Entity has been created, if not then create it
		if(center == null){
			center = new Entity(SceneManager.meshes.get("gizmo_center"), true);//have it auto generate a convex hull
		}
	}
	
	public void render(ShaderProgram program){
		//only render if a target exists
		if(target != null){
			program.setUniform("model", center.getTransform().getMatrix());
			program.setUniform("color", centerColor);
			SceneManager.meshes.get("gizmo_center").render();
		}
	}
	
	public void bind(SpatialAsset target){
		this.target = target;
		center.getTransform().setTranslation(target.getPos());
	}
	
	public void unbind(){
		target = null;
	}
	
	public void check(Ray clickRay){
		//this version will check if the sphere was intersected
	}
	
	public boolean isSelected(){
		return activeModifier != null;
	}
	
	public abstract void update(float screenX, float screenY);
}
