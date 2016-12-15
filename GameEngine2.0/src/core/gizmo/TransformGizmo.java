package core.gizmo;

import mesh.primitives.geometry.Sphere;
import physics.collision.CollisionDetector;
import physics.collision.Ray;
import shaders.ShaderProgram;
import windowing.Window;
import core.Camera;
import core.Entity;
import core.SceneManager;
import core.SpatialAsset;
import events.keyboard.ModKey;
import events.mouse.MouseButton;
import events.mouse.MouseListener;
import glMath.Transform;
import glMath.vectors.Vec3;

public abstract class TransformGizmo implements MouseListener{

	protected static Entity center;
	protected static Vec3 centerColor = new Vec3(1,1,0);
	protected static SpatialAsset target;
	protected static Camera view;
	protected char activeModifier;//points to a character that represents what modifier is active for the object, 'c' will mean the center point and 0 null
	
	/**
	 * Constructs a TranformGizmo, the gizmo is a 3D representation of the cardinal transformations of an object.
	 * This constructor sets up the gizmo sphere center, which acts as a transformation without any restriction.
	 */
	public TransformGizmo(Camera view){
		//add the center sphere to the list of meshes if it exists
		if(SceneManager.meshes.get("gizmo_center") == null){
			SceneManager.meshes.put("gizmo_center", new Sphere(1.5f, 20));
		}
		
		//additionally check if the center sphere Entity has been created, if not then create it
		if(center == null){
			center = new Entity(SceneManager.meshes.get("gizmo_center"), true);//have it auto generate a convex hull
		}
		
		TransformGizmo.view = view;
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
	public void bind(SpatialAsset target){
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
	 * Determines whether this transformation gizmo is currently bound to an object
	 * 
	 * @return True if bound to an object, false otherwise
	 */
	public boolean isBound(){
		return target != null;
	}
	
	/**
	 * Uniformly sets the scales of the transformation gizmo to the given scalar {@code factor}
	 * 
	 * @param factor Floating point number that represents the factor to scale the gizmo
	 */
	public void setScale(float factor){
		center.transform(new Transform().scale(factor));
	}

	@Override
	public abstract void onMouseMove(Window window, double xpos, double ypos, double prevX, double prevY);
	
	@Override
	public final void onMouseRelease(Window window, MouseButton button, ModKey[] mods){
		activeModifier = 0;
	}
	
	@Override
	public void onMousePress(Window window, MouseButton button, boolean isRepeat, ModKey[] mods){
		
	}
	
	@Override
	public final void onMouseEnter(Window window, boolean enter){
		
	}
	
	@Override
	public final void onMouseScroll(Window window, double xoffset, double yoffset){
		
	}
}
