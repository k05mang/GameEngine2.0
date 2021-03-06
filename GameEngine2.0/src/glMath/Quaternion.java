package glMath;

import glMath.matrices.Mat3;
import glMath.matrices.Mat4;
import glMath.transforms.Transform;
import glMath.vectors.Vec3;
import glMath.vectors.Vec4;

public class Quaternion {
	private Vec4 data;//retains this quaternions data, which consists of 4 components
    private static final float DOT_THRESHOLD = 0.9995f;
	
	/**
	 * Constructs an unrotated quaternion
	 */
	public Quaternion(){
		data = new Vec4(0,0,0,1);
	}
	
	/**
	 * Constructs a quaternion, initializing the data in the object with the data given
	 * 
	 * @param x First component of quaternion
	 * @param y Second component of quaternion
	 * @param z Third component of quaternion
	 * @param w Fourth component of quaternion
	 */
	public Quaternion(float x, float y, float z, float w){
		data = new Vec4(x, y, z, w);
	}
	
	/**
	 * Constructs a quaternion, initializing the data in the object with the data given
	 * 
	 * @param data Vector to set the quaternion components to
	 */
	public Quaternion(Vec4 data){
		this.data = new Vec4(data);
	}
	
	/**
	 * Constructs a quaternion from a vector containing the yaw, pitch, and roll of a rotation
	 * 
	 * @param angles Rotation angles to set the quaternion with, read as roll, pitch, and yaw
	 */
	public Quaternion(Vec3 angles){
		Vec3 radAngles = (new Vec3(angles)).scale((float)(Math.PI/180)/2.0f);
		
		float sinr = (float)Math.sin(radAngles.x);
		float sinp = (float)Math.sin(radAngles.y);
		float siny = (float)Math.sin(radAngles.z);
		
		float cosr = (float)Math.cos(radAngles.x);
		float cosp = (float)Math.cos(radAngles.y);
		float cosy = (float)Math.cos(radAngles.z);
		
	 
		data = new Vec4(sinr*cosp*cosy - cosr*sinp*siny,
				cosr*sinp*cosy + sinr*cosp*siny,
				cosr*cosp*siny - sinr*sinp*cosy,
				cosr*cosp*cosy + sinr*sinp*siny);
		
		this.normalize();
	}
	
	/**
	 * Constructs a quaternion from values of the yaw, pitch, and roll of a rotation
	 * 
	 * @param roll X-rotation for the quaternion
	 * @param pitch Y-rotation for the quaternion
	 * @param yaw Z-rotation for the quaternion
	 */
	public Quaternion(float roll, float pitch, float yaw){
		Vec3 radAngles = (new Vec3(roll, pitch, yaw)).scale((float)(Math.PI/180)/2.0f);
		
		float sinr = (float)Math.sin(radAngles.x);
		float sinp = (float)Math.sin(radAngles.y);
		float siny = (float)Math.sin(radAngles.z);
		
		float cosr = (float)Math.cos(radAngles.x);
		float cosp = (float)Math.cos(radAngles.y);
		float cosy = (float)Math.cos(radAngles.z);
		
	 
		data = new Vec4(sinr*cosp*cosy - cosr*sinp*siny,
				cosr*sinp*cosy + sinr*cosp*siny,
				cosr*cosp*siny - sinr*sinp*cosy,
				cosr*cosp*cosy + sinr*sinp*siny);
		
		this.normalize();
	}
	
	/**
	 * Copies the value of a quaternion
	 * 
	 * @param copy Quaternion to copy
	 */
	public Quaternion(Quaternion copy){
		data = new Vec4(copy.data);
	}
	
	/**
	 * Computes the conjugate of this qauternion, which is defined to be the negation of the first 3 components of the qauternion
	 * 
	 * @return This quaternion's conjugate, this quaternion is left unchanged
	 */
	public Quaternion conjugate(){
		data.normalize();
		return new Quaternion(-data.x,-data.y,-data.z,data.w);
	}
	
	/**
	 * Multiplies two quaternions together
	 * 
	 * @param rhs Value to multiply this quaternion with
	 * @return The product of this qauternion and the given quaternion, i.e. this*rhs
	 */
	public Quaternion mult(Quaternion rhs){
		Vec4 multData = rhs.data;
		return new Quaternion(data.w*multData.x + data.x*multData.w + data.y*multData.z - data.z*multData.y,
				                data.w*multData.y + data.y*multData.w + data.z*multData.x - data.x*multData.z,
				                data.w*multData.z + data.z*multData.w + data.x*multData.y - data.y*multData.x,
				                data.w*multData.w - data.x*multData.x - data.y*multData.y - data.z*multData.z);
	}
	
	/**
	 * TODO figure out what this did
	 * @param vector
	 * @return
	 */
	public Quaternion addVector(Vec3 vector){
		Quaternion newRotation = new Quaternion(vector.x, vector.y, vector.z, 0);
//		newRotation.set(newRotation.mult(this));
		newRotation.set(this.mult(newRotation));
		data.x += newRotation.data.x*.5;
		data.y += newRotation.data.y*.5;
		data.z += newRotation.data.z*.5;
		data.w += newRotation.data.w*.5;
//		this.normalize();
		return this;
	}
	
	/**
	 * TODO figure out what this did
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Quaternion addVector(float x, float y, float z){
		Quaternion newRotation = new Quaternion(x, y, z, 0);
		newRotation.set(newRotation.mult(this));
		data.x += newRotation.data.x*.5;
		data.y += newRotation.data.y*.5;
		data.z += newRotation.data.z*.5;
		data.w += newRotation.data.w*.5;
		return this;
	}
	
	/**
	 * Normalizes this quaternion
	 */
	public void normalize(){
		data.normalize();
	}
	
	/**
	 * Multiply the given vector by this quaternion, effectively this would be the equivalent of taking the matrix
	 * that represents this quaternion's rotations and rotating the given vector
	 * 
	 * @param vector Vector to rotate
	 * @return The given vector rotated by the rotation represented by this quaternion
	 */
	public Vec3 multVec(Vec3 vector){
		Quaternion vec = new Quaternion(new Vec4(vector, 0));
		return (Vec3)multiply(this, vec, conjugate()).data.swizzle("xyz");
	}
	
	/**
	 * Sets this quaternion equal to the given quaternion without modifying the given quaternion
	 * 
	 * @param dupe Quaternion to duplicate
	 */
	public void set(Quaternion dupe){
		data.set(dupe.data);
	}
	
	/**
	 * Sets this quaternion based on a yaw, pitch, and roll
	 * 
	 * @param roll X-rotation for the quaternion
	 * @param pitch Y-rotation for the quaternion
	 * @param yaw Z-rotation for the quaternion
	 */
	public void set(float roll, float pitch, float yaw){
		Vec3 radAngles = (new Vec3(roll, pitch, yaw)).scale((float)(Math.PI/180)/2.0f);
		
		float sinr = (float)Math.sin(radAngles.x);
		float sinp = (float)Math.sin(radAngles.y);
		float siny = (float)Math.sin(radAngles.z);
		
		float cosr = (float)Math.cos(radAngles.x);
		float cosp = (float)Math.cos(radAngles.y);
		float cosy = (float)Math.cos(radAngles.z);
		
	 
		data.set(sinr*cosp*cosy - cosr*sinp*siny,
				cosr*sinp*cosy + sinr*cosp*siny,
				cosr*cosp*siny - sinr*sinp*cosy,
				cosr*cosp*cosy + sinr*sinp*siny);
		
		this.normalize();
	}
	
	/**
	 * Sets this quaternion components with the given values
	 * 
	 * @param x First component of quaternion
	 * @param y Second component of quaternion
	 * @param z Third component of quaternion
	 * @param w Fourth component of quaternion
	 */
	public void set(float x, float y, float z, float w){
		data.set(x,y,z,w);
	}
	
	/**
	 * Creates the matrix representation of this quaternion as a 4x4 matrix
	 * 
	 * @return This quaternion as a 4x4 rotation matrix
	 */
	public Mat4 asMatrix(){
		normalize();
		
		float x2 = data.x*data.x;
		float y2 = data.y*data.y;
		float z2 = data.z*data.z;
		
		float xy = data.x*data.y;
		float xz = data.x*data.z;
		float yz = data.y*data.z;
		
		float wx = data.w*-data.x;
		float wy = data.w*-data.y;
		float wz = data.w*-data.z;
		
		return new Mat4( 1.0f - 2.0f * (y2 + z2), 2.0f * (xy - wz), 2.0f * (xz + wy), 0.0f,
						2.0f * (xy + wz), 1.0f - 2.0f * (x2 + z2), 2.0f * (yz - wx), 0.0f,
						2.0f * (xz - wy), 2.0f * (yz + wx), 1.0f - 2.0f * (x2 + y2), 0.0f,
						0.0f, 0.0f, 0.0f, 1.0f
		);
	}
	
	/**
	 * Creates only the rotation matrix from this quaternion, this matrix is the upper 3x3 matrix of the asMatrix function,
	 * this isolates just the rotation component of the matrix
	 * 
	 * @return The rotation component of the matrix representing this quaternion
	 */
	public Mat3 asMat3(){
		normalize();
		
		float x2 = data.x*data.x;
		float y2 = data.y*data.y;
		float z2 = data.z*data.z;
		
		float xy = data.x*data.y;
		float xz = data.x*data.z;
		float yz = data.y*data.z;
		
		float wx = data.w*-data.x;
		float wy = data.w*-data.y;
		float wz = data.w*-data.z;
		
		return new Mat3( 1.0f - 2.0f * (y2 + z2), 2.0f * (xy - wz), 2.0f * (xz + wy),
						2.0f * (xy + wz), 1.0f - 2.0f * (x2 + z2), 2.0f * (yz - wx),
						2.0f * (xz - wy), 2.0f * (yz + wx), 1.0f - 2.0f * (x2 + y2)
						);
	}
	
	/**
	 * Gets the axis represented by this quaternion
	 * 
	 * @return The axis represented by this quaternion
	 */
	public Vec3 getAxis(){
		Vec3 axis = (Vec3)data.swizzle("xyz");
		axis.normalize();
		return axis;
	}
	
	/**
	 * Gets the angle that this quaternion is rotated by, around it's axis
	 * 
	 * @return Gets the angle of rotation for this quaternion
	 */
	public float getAngle(){
		return (float)Math.acos(data.w)*2*(float)(180/Math.PI);
	}
	
	/**
	 * Prints this quaternion to the terminal
	 */
	public void print(){
		data.print();
	}
	
	/**
	 * Multiplies n Quaternions starting with the left most given quaternion and multiplying down the list of inputs
	 * this effectively translates to ((q1*q2)*q3)*q4)...*qn)
	 * 
	 * @param quats Quaternions to multiply
	 * @return The product of the given quaternions
	 */
	public static Quaternion multiply(Quaternion... quats){
		if(quats.length > 0){
			Quaternion result = new Quaternion(quats[0]);
			for(int curQuat = 1; curQuat < quats.length; curQuat++){
				result.set(result.mult(quats[curQuat]));// result *= quats[curQuat]
			}
			return result;
		}else{
			return null;
		}
	}
	
	 /**
	  * Generates a quatenrion given an axis of rotation and a rotation angle
	  * 
	  * @param axis Axis to rotate around
	  * @param angle Angle to rotate by
	  * @return Quaternion representing the rotation of the given angle around the given axis
	  */
	public static Quaternion fromAxisAngle(Vec3 axis, float angle){
		return fromAxisAngle(axis.x, axis.y, axis.z, angle);
	}
	
	 /**
	  * Generates a quatenrion given an axis of rotation and a rotation angle
	  * 
	  * @param x X component of the axis to rotate around
	  * @param y Y component of the axis to rotate around
	  * @param z Z component of the axis to rotate around
	  * @param angle Angle to rotate by
	  * @return Quaternion representing the rotation of the given angle around the given axis
	  */
	public static Quaternion fromAxisAngle(float x, float y, float z, float angle){
		Vec3 nAxis = new Vec3(x, y, z);
		nAxis.normalize();
		float sinAngle = (float)Math.sin((angle*Math.PI/180)/2.0f);
		nAxis.scale(sinAngle);
		
		return new Quaternion(new Vec4(nAxis, (float)Math.cos((angle*Math.PI/180)/2.0f)));
	}
	
	/**
	 * Generates a quaternion that can be used to transform the {@code start} to the 
	 * vector {@code end} through a rotation around some axis
	 * 
	 * @param start Vector to start the rotation from
	 * @param end Vector to end the rotation to
	 * 
	 * @return Quaternion rotation around some axis that can transform the {@code start} vector to the {@code end} vector
	 */
	public static Quaternion interpolate(Vec3 start, Vec3 end){
		//First make normalized copies of the input vectors
		Vec3 startNorm = VecUtil.normalize(start);//new Vec3(start).normalize();
		Vec3 endNorm = VecUtil.normalize(end);
		
		Vec3 axis = startNorm.cross(endNorm);//get the axis of rotation
		//due to floating point errors dot product can produce -/+1.000001 resulting in NaN results from acos
		//thus the max/min are needed
		float angle = (float)(Math.acos(Math.min(1, Math.max(-1, startNorm.dot(endNorm))))*180/Math.PI);//get the angle of rotation
		//check if the axis is the zero vector
		if(axis.isZero()){
			//if it is then that means the rotation is either 180 degrees or 0 degrees
			//startNorm by generating a new axis using one of the cardinal axis
			//we first need to determine which axis to use since the inputs could be one of the cardinal axis
			if(startNorm.equals(Transform.xAxis) || endNorm.equals(Transform.xAxis) ||
					startNorm.equals(Transform.yAxis) || endNorm.equals(Transform.yAxis)){
				//for the x or y axis we can just use the z axis as the rotation axis
				return Quaternion.fromAxisAngle(Transform.zAxis, angle);
			}else{
				//otherwise we will cross the startNorm with the y axis to get a rotation axis, this works for when the inputs are the z axis
				return Quaternion.fromAxisAngle(startNorm.cross(Transform.yAxis), angle);
			}
		}else{
			//if not then the Quaternion generation can proceed as normal
			return Quaternion.fromAxisAngle(axis, angle);
		}
	}
	
	/**
	 * Spherical interpolation between two quaternions taken from
	 * https://en.wikipedia.org/wiki/Slerp
	 * 
	 * @param start Quaternion to start from
	 * @param end Quaternion to transform to
	 * @param t Amount, from 0-1 denoting a percent, to transform the quaternion 
	 * along the interpolation path from {@code start} to {@code end}
	 * 
	 * @return Quaternion representing the transformation from the start quaterion to the end quaternion
	 * by a percent amount denoted by {@code t}
	 */
	public static Quaternion slerp(Quaternion start, Quaternion end, float t) {
		Vec4 startQ = VecUtil.normalize(start.data);//new Vec4(start.data);
		Vec4 endQ = VecUtil.normalize(end.data);//new Vec4(end.data);
	    // Only unit quaternions are valid rotations.
	    // Normalize to avoid undefined behavior.
//		startQ.normalize();
//		endQ.normalize();

	    // Compute the cosine of the angle between the two vectors.
	    float dot = startQ.dot(endQ);
	    if (Math.abs(dot) > DOT_THRESHOLD) {
	        // If the inputs are too close for comfort, linearly interpolate
	        // and normalize the result.

	        //Quaternion result = v0 + t*(v1 � v0);
	        Quaternion result = new Quaternion(VecUtil.subtract(endQ, startQ).scale(t).add(startQ));
	        result.normalize();
	        return result;
	    }

	    // If the dot product is negative, the quaternions
	    // have opposite handed-ness and slerp won't take
	    // the shorter path. Fix by reversing one quaternion.
	    if (dot < 0.0f) {
	        endQ.scale(-1);
	        dot = -dot;
	    }  

	    //Clamp(dot, -1, 1);
	    dot = Math.max(1, Math.min(-1,  dot));//Stay within domain of acos()
	    double theta_0 = Math.acos(dot);  // theta_0 = angle between input vectors
	    double theta = theta_0*t;    // theta = angle between v0 and result 

	    Vec4 v2 = new Vec4(endQ.subtract(startQ.scale(dot)));
	    //Quaternion v2 = v1 � v0*dot;
	    v2.normalize();              // { start, v2 } is now an orthonormal basis

	    //start*cos(theta) + v2*sin(theta);
	    Vec4 left = startQ.scale((float)Math.cos(theta));
	    Vec4 right = v2.scale((float)Math.sin(theta));
	    return new Quaternion(left.add(right));
	}
}
