package core.gizmo;

import mesh.primitives.geometry.Sphere;
import physics.collision.CollisionDetector;
import physics.collision.Ray;
import shaders.ShaderProgram;
import windowing.Window;
import windowing.events.keyboard.ModKey;
import windowing.events.mouse.MouseButton;
import windowing.events.mouse.MouseListener;
import core.Camera;
import core.Entity;
import core.SpatialAsset;
import core.managers.SceneManager;
import glMath.VecUtil;
import glMath.transforms.Transform;
import glMath.vectors.Vec3;

public abstract class TransformGizmo implements MouseListener{

	protected static Entity center;
	protected static Vec3 centerColor = new Vec3(1,1,0);
	protected static SpatialAsset target;
	protected static Camera view;
	protected static ActiveControl activeController = ActiveControl.NO_CONTROLLER;//represents what controlling axis should be used in modifying the object
	protected static TransformType modifier = TransformType.TRANSLATE;
	protected static final float viewScale = 150f;//reduces the scaling factor when scaling the gizmo to make use easier at further distances
	protected static final float controllerLength = 10f;//length for the arrow controllers on certain gizmos
	static{
		//create the center controller
		SceneManager.meshes.put("gizmo_center", new Sphere(1.5f, 20));
		center = new Entity(SceneManager.meshes.get("gizmo_center"), true);
	}
	
//	protected static final char 
//	NO_CONTROLLER = 0,
//	CENTER = 'c',
//	X_AXIS = 'x',
//	Y_AXIS = 'y',
//	Z_AXIS = 'z';
	protected enum ActiveControl{
		NO_CONTROLLER,
		CENTER,
		X_AXIS,
		Y_AXIS,
		Z_AXIS;
	}
	
	/**
	 * Constructs a TranformGizmo, the gizmo is a 3D representation of the cardinal transformations of an object.
	 * This constructor sets up the gizmo sphere center, which acts as a transformation without any restriction.
	 */
	public TransformGizmo(Camera view){
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
			//center the gizmo at the target object
			center.getTransform().setTranslation(target.getPos());
			//scale the gizmo for better interaction relative to the camera
			float scale = VecUtil.subtract(view.getPos(), target.getPos()).length()/viewScale;///(view.getHeight()/view.getAspect()/5);
			//set the scale of the center to match this factor
			Vec3 scalars = center.getTransform().getScalars();
			center.transform(new Transform().scale(scale/scalars.x));
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
	 * Sets the Camera the transform gizmos will use in calculations
	 * 
	 * @param view Camera to set as the new view in calculating various transforms
	 */
	public void setCamera(Camera view){
		TransformGizmo.view = view;
	}

	public void setModifier(TransformType type){
		TransformGizmo.modifier = type;
	}
	
	public abstract boolean isSelected(Ray click);

	@Override
	public abstract void onMouseMove(Window window, double xpos, double ypos, double prevX, double prevY);
	
	@Override
	public final void onMouseRelease(Window window, MouseButton button, ModKey[] mods){
		activeController = ActiveControl.NO_CONTROLLER;
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
