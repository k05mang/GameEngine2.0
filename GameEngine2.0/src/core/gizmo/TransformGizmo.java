package core.gizmo;

import mesh.primitives.geometry.Sphere;
import physics.collision.CollisionDetector;
import physics.collision.Ray;
import shaders.ShaderProgram;
import windowing.Window;
import core.Entity;
import core.SceneManager;
import events.keyboard.ModKey;
import events.mouse.MouseButton;
import events.mouse.MouseListener;
import glMath.vectors.Vec3;

public abstract class TransformGizmo implements MouseListener{

	protected static Entity center;
	protected static Vec3 centerColor = new Vec3(1,1,0);
	protected static Entity target;
	protected Object activeModifier;//points to the object that is modifying values for the target, i.e. the sphere center
	
	/**
	 * Constructs a TranformGizmo, the gizmo is a 3D representation of the cardinal transformations of an object.
	 * This constructor sets up the gizmo sphere center, which acts as a transformation without any restriction.
	 */
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
	
	/**
	 * Renders the gizmo to the screen, modifying values in the ShaderProgram {@code program}. The shader program 
	 * is expected to have a model matrix variable called "model" that is of type mat4, and a color variable
	 * of type "vec3".
	 * 
	 * @param program ShaderProgram to use in rendering this gizmo
	 */
	public void render(ShaderProgram program){
		//only render if a target exists
		if(target != null){
			program.setUniform("model", center.getTransform().getMatrix());
			program.setUniform("color", centerColor);
			SceneManager.meshes.get("gizmo_center").render();
		}
	}
	
	/**
	 * Binds this gizmo to an Entity and vice versa. This sets up a relationship between the Entity and the TransformGizmo, where changes made to 
	 * either will result in visual modifications to both objects, such as scaling, rotating, and translating.
	 * 
	 * @param target Target entity to connect with this gizmo
	 */
	public void bind(Entity target){
		TransformGizmo.target = target;
		center.getTransform().setTranslation(target.getPos());
	}
	
	/**
	 * Detaches the target Entity of this gizmo, further changes to either objects will not affect the other
	 */
	public void unbind(){
		target = null;
	}
	
	/**
	 * Checks if the given Ray {@code clickRay} is colliding with this gizmo.
	 * 
	 * @param clickRay Ray to test this gizmo against
	 * 
	 * return True if the ray is colliding with any part of the gizmo, false otherwise
	 */
	public boolean isSelected(Ray clickRay){//this version will check if the sphere was intersected
		if(CollisionDetector.intersects(clickRay, center)){
			activeModifier = center;
			return true;
		}else{
			return false;
		}
	}

	@Override
	public abstract void mouseMove(Window window, double xpos, double ypos);
	
	@Override
	public void mouseRelease(Window window, MouseButton button, ModKey[] mods){
		activeModifier = null;
	}
}
