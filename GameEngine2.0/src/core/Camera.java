package core;
import static glMath.VecUtil.dot;
import glMath.MatrixUtil;
import glMath.Quaternion;
import glMath.VecUtil;
import glMath.matrices.Mat4;
import glMath.transforms.Transform;
import glMath.vectors.Vec3;
import glMath.vectors.Vec4;
import physics.collision.Ray;

public class Camera {
	
	private float theta, phi, fovy, aspect, zNear, zFar, width, height;
	private Vec3 position, forward, right, up;
	private Mat4 projection;

	/**
	 * Creates a camera with the given vector {@code eye} position, a {@code theta} horizontal rotation
	 * and a {@code phi} vertical rotation, additionally this creates and stores a perspective projection
	 * matrix formed using the given {@code fovy}, aspect = {@code width/height}, {@code zNear}, and {@code zFar}.
	 * 
	 * @param eye Vector that will be used for this cameras starting position
	 * @param theta the camera's horizontal rotation (in degrees)
	 * @param phi the camera's vertical rotation (in degrees)
	 * @param fovy Field of View angle for the y direction 
	 * @param width Width of the rendering window
	 * @param height Height of the rendering window
	 * @param zNear The nearest z value to the view before being clipped
	 * @param zFar The farthest z value from the view before being clipped
	 */
	public Camera( Vec3 eye, float theta , float phi,
			float fovy, float width, float height, 
			float zNear, float zFar) {
		this(eye.x, eye.y, eye.z, theta, phi, fovy, width, height, zNear, zFar);
	}
	
	/**
	 * Creates a camera with the given {@code x, y, z} eye position, a {@code theta} horizontal rotation
	 * and a {@code phi} vertical rotation, additionally this creates and stores a perspective projection
	 * matrix formed using the given {@code fovy}, aspect = {@code width/height}, {@code zNear}, and {@code zFar}.
	 * 
	 * @param x the x component of the camera's location
	 * @param y the y component of the camera's location
	 * @param z the z component of the camera's location
	 * @param theta the camera's horizontal rotation (in degrees)
	 * @param phi the camera's vertical rotation (in degrees)
	 * @param fovy Field of View angle for the y direction 
	 * @param width Width of the rendering window
	 * @param height Height of the rendering window
	 * @param zNear The nearest z value to the view before being clipped
	 * @param zFar The farthest z value from the view before being clipped
	 */
	public Camera( float x, float y, float z,
			float theta , float phi,
			float fovy, 
			float width, float height, 
			float zNear, float zFar) {
		this.theta = theta;
		this.phi = phi;
		this.fovy = fovy;
		this.width = width;
		this.height = height;
		this.aspect = width/height;
		this.zNear = zNear;
		this.zFar = zFar;
		
		position = new Vec3(x, y, z);
		forward = new Vec3(0,0,-1);
		right = new Vec3(1,0,0);
		up = new Vec3(0,1,0);
		projection = MatrixUtil.getPerspective(fovy, aspect, zNear, zFar);
	}
	
//
//	/**
//	 * Creates a camera with the given x, y, z eye position, and a theta horizontal rotation
//	 * and a phi vertical rotation, additionall this creates and stores an orthographic projection
//	 * matrix formed using the given left, right, bottom, top, zNear, and zFar values.
//	 * 
//	 * @param x the x component of the camera's location
//	 * @param y the y component of the camera's location
//	 * @param z the z component of the camera's location
//	 * @param theta the camera's horizontal rotation (in degrees)
//	 * @param phi the camera's vertical rotation (in degrees)
//	 * @param left The minimum x clipping value
//	 * @param right The maximum x clipping value
//	 * @param bottom The minimum y clipping value
//	 * @param top The maximum y clipping value
//	 * @param zNear The nearest z value to the view before being clipped
//	 * @param zFar The farthest z value from the view before being clipped
//	 */
//	public Camera( float x, float y, float z,
//			float theta , float phi,
//			float left, float right, float bottom, float top, float zNear, float zFar) {
//		this.theta = theta;
//		this.phi = phi;
//		eye = new Vec3(x, y, z);
//		forward = new Vec3(0,0,-1);
//		this.right = new Vec3(1,0,0);
//		up = new Vec3(0,1,0);
//		projection = MatrixUtil.getOrtho(left, right, bottom, top, zNear, zFar);
//	}
//
//	/**
//	 * Creates a camera with the given vector eye position, and a theta horizontal rotation
//	 * and a phi vertical rotation, additionall this creates and stores an orthographic projection
//	 * matrix formed using the given left, right, bottom, top, zNear, and zFar values.
//	 * 
//	 * @param eye Vector that will be used for this cameras starting position
//	 * @param theta the camera's horizontal rotation (in degrees)
//	 * @param phi the camera's vertical rotation (in degrees)
//	 * @param left The minimum x clipping value
//	 * @param right The maximum x clipping value
//	 * @param bottom The minimum y clipping value
//	 * @param top The maximum y clipping value
//	 * @param zNear The nearest z value to the view before being clipped
//	 * @param zFar The farthest z value from the view before being clipped
//	 */
//	public Camera( Vec3 eye, float theta , float phi,
//			float left, float right, float bottom, float top, float zNear, float zFar) {
//		this.theta = theta;
//		this.phi = phi;
//		this.eye = new Vec3(eye);
//		forward = new Vec3(0,0,-1);
//		this.right = new Vec3(1,0,0);
//		up = new Vec3(0,1,0);
//		projection = MatrixUtil.getOrtho(left, right, bottom, top, zNear, zFar);
//	}

	public Mat4 getLookAt() {
		Quaternion rot = new Quaternion(phi,theta,0);
		
		forward = rot.multVec(Transform.zAxis);
		up = rot.multVec(Transform.yAxis);
		right = up.cross(forward);
		
		return  new Mat4(
				new Vec4(right.x, up.x, forward.x, 0),
				new Vec4(right.y, up.y, forward.y, 0),
				new Vec4(right.z, up.z, forward.z, 0),
				new Vec4(-dot(right,position),-dot(up,position),-dot(forward,position),1.0f)
				);
	}
	
	public Mat4 getProjection(){
		return projection;
	}
	
	/**
	 * Increases the camera's rotation angle by the indicated amount. 
	 * 
	 * @param amt amount (in degrees) by which to increase the camera's rotation
	 */
	public void rotate( float amt ) {
		theta = (theta+amt)%360;
	}
	 
	/**
	 * Rotates the camera vertically
	 * 
	 * @param amt amount (in degrees) by which to increase the camera's rotation
	 */
	public void lookY(float amt, boolean restrict){
		if (restrict) {
			phi += (!(phi + amt < -85 || phi + amt > 85) ? amt : 0);
		}else{
			phi += amt;
		}
	}
	
	public void moveTo(float locX, float locY, float locZ){
		position.set(locX, locY, locZ);
	}
	
	public void moveTo(Vec3 position){
		position.set(position);
	}
	
	public void strafe(float amt){
		position.add(right.x*amt, right.y*amt, right.z*amt);
	}
	
	/**
	 * Moves the camera in a horizontal direction independent of where the camera is facing (walking)
	 * 
	 * @param amt how far to move 
	 */
	public void move(float amt){
		Quaternion rot = new Quaternion(0,theta,0);
		
		Vec3 dir = rot.multVec(Transform.zAxis);
		
		position.add(dir.scale(amt));
	}
	
	public void moveUpDown(float amt){
		position.set(1, position.y+amt);
	}
	
	public void fly(float amt){
		position.add(forward.x*amt, forward.y*amt, forward.z*amt);
	}
	
	public Vec3 getPos(){
		return position;
	}
	
	public Vec3 getForwardVec(){
		return forward;
	}
	
	public float getWidth(){
		return width;
	}
	
	public float getHeight(){
		return height;
	}
	
	public float getAspect(){
		return aspect;
	}
	
	public float getFov(){
		return fovy;
	}
	
	public float getZNear(){
		return zNear;
	}
	
	public float getZFar(){
		return zFar;
	}
	
	/**
	 * Gets the angle, in degrees, between where the camera is currently looking vertically relative to the xz-plane
	 * 
	 * @return Angle in degrees of the vertical rotation relative to the xz-plane
	 */
	public float getVertAngle(){
		return phi;
	}

	/**
	 * Gets the angle, in degrees, between where the camera is currently looking horizontally relative to the negative z axis
	 * 
	 * @return Angle in degrees of the horizontal rotation relative to the negative z axis
	 */
	public float getHorizAngle(){
		return theta;
	}
	
	/**
	 * Creates a Ray projected from the given normalized screen coordinates
	 * 
	 * @param x Screen space x
	 * @param y Screen space y
	 * 
	 * @return Ray projected in world coordinates
	 */
	public Ray genRay(float x, float y){
		//point projection onto near clip plane method
		//convert fovy to radians
		float radFovy = fovy*(float)Math.PI/180.0f;
		//since the entire height of the camera plane lies within the fovy angle, we only want half that to compute
		//the vector length that is centered at the origin of the near plane
		//tan(fovy/2) = opp/adj = nearHeight/zNear
		//zNear*tan(fovy/2) = nearHeight
		float nearHeight = (float)Math.tan(radFovy/2)*-zNear;
		Vec3 planeHeight = VecUtil.scale(up, nearHeight);
		//since the aspect ratio of the screen needs to be maintained we get the width by taking the height of the near plane and 
		//scaling with the aspect ratio to get the width of the near plane
		Vec3 planeWidth = VecUtil.scale(right, nearHeight*aspect);
		//get the center point of the near plane, then take the fraction of the width and height from the mouse coordinates to get
		//the points on the near plane that intersect the mouse coordinates, additionally x and y must be normalized
		Vec3 direction = VecUtil.add(VecUtil.scale(forward, -zNear), VecUtil.scale(planeWidth, -2*(x/width)+1), VecUtil.scale(planeHeight, 2*(y/height)-1));
		//multiplications applied to x and y values translate the final direction ray into fullscreen coordinates instead of the quarter screen
		//used in calculations
		return new Ray(zFar, position, direction);
	}
}