package mesh;

import glMath.Quaternion;
import glMath.Transform;
import glMath.VecUtil;
import glMath.vectors.Vec3;
import mesh.primitives.geometry.Cone;
import mesh.primitives.geometry.Cube;
import mesh.primitives.geometry.Cylinder;
import physics.collision.CollisionDetector;
import physics.collision.CollisionMesh;
import physics.collision.Ray;
import shaders.ShaderProgram;
import core.Entity;
import core.SceneManager;

/**
 * 
 * @author Kevin Mango
 * 
 * Class used to represent vectors in a 3 dimensional space, and additionally used to create interactive transformation elements.
 *
 */
public class Arrow{
	
	private float shaftLength, tipLength;
	private final float origLength, origTipLength;
	private Vec3 direction, position, color;
	private boolean useCube;
	private Entity shaft, tip;

	/**
	 * Constructs an arrow that represents a Ray object
	 * 
	 * @param ray Ray whose data to use in the construction of this Arrow
	 */
	public Arrow(Ray ray){
		//use the rays length only if the ray is negative which means it is infinite, in which case an arbitrary value is used
		this(ray.getLength() < 0 ? 10000 : ray.getLength(), ray.getPos(), ray.getDirection(), 1,1,1);
	}
	
	/**
	 * Same as {@link #Arrow(float length, Vec3 position, Vec3 direction, Vec3 color, boolean cubeTip) Arrow} with cubeTip set to false
	 */
	public Arrow(float length, Vec3 position, Vec3 direction, Vec3 color){
		this(length, position.x, position.y, position.z, direction.x, direction.y, direction.z, color.x, color.y, color.z, false);
	}
	
	public Arrow(float length, Vec3 position, Vec3 direction, float r, float g, float b){
		this(length, position.x, position.y, position.z, direction.x, direction.y, direction.z, r, g, b, false);
	}
	
	public Arrow(float length, float posx, float posy, float posz, Vec3 direction, Vec3 color){
		this(length, posx, posy, posz, direction.x, direction.y, direction.z, color.x, color.y, color.z, false);
	}
	
	public Arrow(float length, Vec3 position, float dirx, float diry, float dirz, Vec3 color){
		this(length, position.x, position.y, position.z, dirx, diry, dirz, color.x, color.y, color.z, false);
	}
	
	public Arrow(float length, Vec3 position, float dirx, float diry, float dirz, float r, float g, float b){
		this(length, position.x, position.y, position.z, dirx, diry, dirz, r, g, b, false);
	}
	
	public Arrow(float length, float posx, float posy, float posz, Vec3 direction, float r, float g, float b){
		this(length, posx, posy, posz, direction.x, direction.y, direction.z, r, g, b, false);
	}
	
	public Arrow(float length, float posx, float posy, float posz, float dirx, float diry, float dirz, Vec3 color){
		this(length, posx, posy, posz, dirx, diry, dirz, color.x, color.y, color.z, false);
	}
	
	/**
	 * Same as {@link #Arrow(float length, float posx, float posy, float posz, float dirx, float diry, float dirz, 
	 * float r, float g, float b, boolean cubeTip) Arrow} with cubeTip set to false
	 */
	public Arrow(float length, float posx, float posy, float posz, float dirx, float diry, float dirz, float r, float g, float b){
		this(length, posx, posy, posz, dirx, diry, dirz, r, g, b, false);
	}
	
	/**
	 * Constructs an Arrow object with the given {@code length}, {@code position}, {@code direction}, and {@code color}.
	 * {@code cubeTip} is used to specify the type of tip that is used in rendering the Arrow, if true the tip will be a 
	 * cube, else a cone will be used.
	 * 
	 * @param length Length of the Arrow from the base to the tip
	 * @param position Position of the base of the Arrow
	 * @param direction Direction of the Arrow
	 * @param color Color of the Arrow
	 * @param cubeTip Boolean indicating what type of tip to use in rendering, true means a cube will be the tip, otherwise a cone
	 */
	public Arrow(float length, Vec3 position, Vec3 direction, Vec3 color, boolean cubeTip){
		this(length, position.x, position.y, position.z, direction.x, direction.y, direction.z, color.x, color.y, color.z, cubeTip);
	}
	
	public Arrow(float length, Vec3 position, Vec3 direction, float r, float g, float b, boolean cubeTip){
		this(length, position.x, position.y, position.z, direction.x, direction.y, direction.z, r, g, b, cubeTip);
	}
	
	public Arrow(float length, float posx, float posy, float posz, Vec3 direction, Vec3 color, boolean cubeTip){
		this(length, posx, posy, posz, direction.x, direction.y, direction.z, color.x, color.y, color.z, cubeTip);
	}
	
	public Arrow(float length, Vec3 position, float dirx, float diry, float dirz, Vec3 color, boolean cubeTip){
		this(length, position.x, position.y, position.z, dirx, diry, dirz, color.x, color.y, color.z, cubeTip);
	}
	
	public Arrow(float length, Vec3 position, float dirx, float diry, float dirz, float r, float g, float b, boolean cubeTip){
		this(length, position.x, position.y, position.z, dirx, diry, dirz, r, g, b, cubeTip);
	}
	
	public Arrow(float length, float posx, float posy, float posz, Vec3 direction, float r, float g, float b, boolean cubeTip){
		this(length, posx, posy, posz, direction.x, direction.y, direction.z, r, g, b, cubeTip);
	}
	
	public Arrow(float length, float posx, float posy, float posz, float dirx, float diry, float dirz, Vec3 color, boolean cubeTip){
		this(length, posx, posy, posz, dirx, diry, dirz, color.x, color.y, color.z, cubeTip);
	}
	
	/**
	 * Constructs an Arrow object with the given {@code length}, position given by {@code posx}, {@code posy}, and {@code posz}.
	 * Direction given by {@code dirx}, {@code diry}, and {@code dirz} relative to the base position of the Arrow. And color 
	 * specified by {@code r}, {@code g}, and {@code b}. {@code cubeTip} is used to specify the type of tip that is used in 
	 * rendering the Arrow, if true the tip will be a cube, else a cone will be used.
	 * 
	 * @param length Length of the Arrow from base to tip
	 * @param posx X value of the position of the Arrow
	 * @param posy Y value of the position of the Arrow
	 * @param posz Z value of the position of the Arrow
	 * @param dirx X value of the direction of the Arrow 
	 * @param diry Y value of the direction of the Arrow 
	 * @param dirz Z value of the direction of the Arrow 
	 * @param r Red component of the Arrow's color
	 * @param g Green component of the Arrow's color
	 * @param b Blue component of the Arrow's color
	 * @param cubeTip Boolean indicating what type of tip to use in rendering, true means a cube will be the tip, otherwise a cone
	 */
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
		shaft = new Entity(SceneManager.meshes.get("arrow_shaft"), true);
		useCube = cubeTip;
		tip = new Entity(useCube ? SceneManager.meshes.get("arrow_cube") : SceneManager.meshes.get("arrow_cone"), true);
		
		origTipLength = useCube ? 2 : 2.5f;
		tipLength = origTipLength;
		//subtract tip length from the length of the shaft so that the arrow will span the entire length
		origLength = Math.max(length, 3)-origTipLength;
		shaftLength = origLength;
		
		color = new Vec3(r, g, b);
		position = new Vec3(posx, posy, posz);
		direction = new Vec3(dirx, diry, dirz).normalize();
		//create the initial transforms for the shaft and tip
		Transform shaftTrans = new Transform().scale(.5f, shaftLength, .5f);
		float xzScale = useCube ? tipLength : 1;
		Transform tipTrans = new Transform().scale(xzScale, tipLength, xzScale);
		//first orient the meshes
		Vec3 axis = Transform.yAxis.cross(direction);
		float angle = (float)(Math.acos(Transform.yAxis.dot(direction))*180/Math.PI);
		shaftTrans.rotate(angle == 180 ? Transform.xAxis : axis, angle);
		tipTrans.rotate(angle == 180 ? Transform.xAxis : axis, angle);
		//translate the cylinder
		float halfLength = shaftLength/2;
		shaftTrans.translate(halfLength*direction.x, halfLength*direction.y, halfLength*direction.z);
		//translate the tip
		tipTrans.translate(
				(shaftLength+tipLength/2)*direction.x, 
				(shaftLength+tipLength/2)*direction.y, 
				(shaftLength+tipLength/2)*direction.z);
		
		//translate both to the position
		shaftTrans.translate(position);
		tipTrans.translate(position);
		
		shaft.setTransform(shaftTrans);
		tip.setTransform(tipTrans);
	}
	
	/**
	 * Renders the Arrow by first setting the "model" and "color" variables of the given ShaderProgram. "color"
	 * is expected to be a vec3 uniform and will be set the value of this Arrows color. "model" is expected to
	 * be a mat4 uniform that will be used to transform the meshes of the Arrow during rendering.
	 * 
	 * @param program ShaderProgram whose uniform variables to modify for rendering this Arrow
	 */
	public void render(ShaderProgram program){
		program.setUniform("color", color);
		program.setUniform("model", shaft.getTransform().getMatrix());
		shaft.getMesh().render();
		program.setUniform("model", tip.getTransform().getMatrix());
		tip.getMesh().render();
	}
	
	/**
	 * Transforms the Arrow with the given Transform {@code trans}, all transformations are applied relative to the base
	 * of the Arrow.
	 * 
	 * @param trans Transform to modify this Arrow with
	 */
	public void transform(Transform trans){
		Transform shaftTrans = new Transform().transform(trans);
		Transform tipTrans = new Transform().transform(trans);
		//translate
		position.add(trans.getTranslation());

		//scale
		//these are the differences between the old lengths and the new
		float newLength = (trans.getScalars().y-1)*shaftLength;
		float newTipLength = (trans.getScalars().y-1)*tipLength;
		//translate the meshes to maintain the position
		shaftTrans.translate(newLength/2*direction.x, newLength/2*direction.y, newLength/2*direction.z);
		tipTrans.translate(
				(newLength+newTipLength/2)*direction.x, 
				(newLength+newTipLength/2)*direction.y, 
				(newLength+newTipLength/2)*direction.z
				);
		//get the entire new length
		shaftLength = newLength+shaftLength;
		tipLength = newTipLength+tipLength;
		
		//rotate
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
		direction.set(shaftPoint).normalize();
		
		shaft.transform(shaftTrans);
		tip.transform(tipTrans);
	}
	
	/**
	 * Sets the length of the Arrow to the given length, the length only changes the length of the shaft of the Arrow. The
	 * tip retains it's length after the length of the Arrow has been changed, however the full length of the Arrow from the
	 * base to the tip of the tip will be equal to the length passed to this function.
	 * 
	 * @param length The new length to set this Arrow to 
	 */
	public void setLength(float length){
		float scale = (length-tipLength)/shaftLength;//calculate how much to scale the cylinder by
		shaft.getTransform().scale(1, scale, 1);//scale the shaft
		Vec3 translation = new Vec3(direction);//copy the direction vector for translating the pieces of the arrow
		translation.scale((length-tipLength-shaftLength)/2);//scale direction copy for translation of cylinder
		shaft.getTransform().translate(translation);//translate the shaft
		tip.getTransform().translate(translation.scale(2));//translate the tip after scaling the translation vector
		shaftLength = length-tipLength;
	}
	
	/**
	 * Gets the length of the Arrow, the length is defined as the span between the starting point of the Arrow to the tip of the Arrow
	 * 
	 * @return The length of the Arrow from the base to the tip
	 */
	public float getLength(){
		return shaftLength+tipLength;
	}
	
	/**
	 * Sets the scaling for this Arrow, the Arrow's positiion will be maintained, meaning that any scaling is done relative to the
	 * position of the Arrow
	 * 
	 * @param scale Scale to set this Arrow to
	 */
	public void setScale(float scale){
		float newLength = scale*origLength;
		float newTipLength = scale*origTipLength;
		//translate the meshes to maintain the position
		Transform shaftTrans = new Transform().translate((newLength-shaftLength)/2*direction.x, (newLength-shaftLength)/2*direction.y, (newLength-shaftLength)/2*direction.z);
		Transform tipTrans = new Transform().translate(
				(newLength-shaftLength+newTipLength-tipLength)*direction.x, 
				(newLength-shaftLength+newTipLength-tipLength)*direction.y, 
				(newLength-shaftLength+newTipLength-tipLength)*direction.z
				);
		shaftTrans.setScale(.5f, origLength, .5f);
		float xzScale = useCube ? origTipLength : 1;
		tipTrans.setScale(xzScale, origTipLength, xzScale);
		shaftTrans.scale(scale);
		tipTrans.scale(scale);
		shaftLength = newLength;
		tipLength = newTipLength;

		shaft.setTransform(shaftTrans);
		tip.setTransform(tipTrans);
	}
	
	/**
	 * Sets the position of this Arrow with the given vector
	 * 
	 * @param pos Vector representing the new position of this Arrow
	 */
	public void setPos(Vec3 pos){
		setPos(pos.x, pos.y, pos.z);
	}
	
	/**
	 * Sets the position this Arrow is being emitted from
	 * 
	 * @param x X value to set this Arrows position to
	 * @param y Y value to set this Arrows position to
	 * @param z Z value to set this Arrows position to
	 */
	public void setPos(float x, float y, float z){
		Transform trans = new Transform().translate(x-position.x, y-position.y, z-position.z);
		shaft.transform(trans);
		tip.transform(trans);
		position.set(x, y, z);
	}
	
	/**
	 * Gets the position this Arrow is emitted from
	 * 
	 * @return Position this Arrow is being emitted from
	 */
	public Vec3 getPos(){
		return position;
	}
	
	/**
	 * Sets the color to use when rendering this Arrow
	 * 
	 * @param r Red component of this Arrows color
	 * @param g Green component of this Arrows color
	 * @param b Blue component of this Arrows color
	 */
	public void setColor(float r, float g, float b){
		color.set(r, g, b);
	}
	
	/**
	 * Sets the color used to render this arrow
	 * 
	 * @param color Vector containing the RGB components of the color to use when rendering this Arrow
	 */
	public void setColor(Vec3 color){
		setColor(color.x, color.y, color.z);
	}
	
	/**
	 * Gets the color that is used when rendering the Arrow
	 * 
	 * @return Vector containing the RGB components of the color of this Arrow
	 */
	public Vec3 getColor(){
		return color;
	}
	
	public boolean colliding(CollisionMesh mesh){
		return CollisionDetector.intersects(mesh, shaft) || CollisionDetector.intersects(mesh, tip);
	}
	
	public boolean colliding(Ray ray){
		return CollisionDetector.intersects(ray, shaft) || CollisionDetector.intersects(ray, tip);
	}
	
	public boolean colliding(Arrow arrow){
		return CollisionDetector.intersects(arrow.shaft, shaft) || CollisionDetector.intersects(arrow.tip, tip) ||
				CollisionDetector.intersects(arrow.tip, shaft) || CollisionDetector.intersects(arrow.shaft, tip);
	}
}
