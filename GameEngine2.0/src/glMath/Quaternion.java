package glMath;

import glMath.matrices.Mat3;
import glMath.matrices.Mat4;
import glMath.vectors.Vec3;
import glMath.vectors.Vec4;

public class Quaternion {
	private Vec4 data;//retains this quaternions data, which consists of 4 components
	
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
		data = new Vec4(copy.getData());
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
		Vec4 multData = rhs.getData();
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
	 * @return The given vecetor rotated by the rotation represented by this quaternion
	 */
	public Vec3 multVec(Vec3 vector){
		Vec3 vecCopy = new Vec3(vector);
		vecCopy.normalize();
		Quaternion vec = new Quaternion(new Vec4(vecCopy, 0));
		return (Vec3)multiply(this, vec, conjugate()).getData().swizzle("xyz");
	}
	
	/**
	 * Sets this quaternion equal to the given quaternion without modifying the given quaternion
	 * 
	 * @param dupe Quaternion to duplicate
	 */
	public void set(Quaternion dupe){
		data.set(dupe.getData());
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
		
		float wx = data.w*data.x;
		float wy = data.w*data.y;
		float wz = data.w*data.z;
		
		return new Mat4( new Vec4(1.0f - 2.0f * (y2 + z2), 2.0f * (xy - wz), 2.0f * (xz + wy), 0.0f),
						new Vec4(2.0f * (xy + wz), 1.0f - 2.0f * (x2 + z2), 2.0f * (yz - wx), 0.0f),
						new Vec4(2.0f * (xz - wy), 2.0f * (yz + wx), 1.0f - 2.0f * (x2 + y2), 0.0f),
						new Vec4(0.0f, 0.0f, 0.0f, 1.0f));
	}
	
	/**
	 * Creates only the rotation matrix from this quaternion, this matrix is the upper 3x3 matrix of the asMatrix function,
	 * this isolates just the rotation component of the matrix
	 * 
	 * @return The rotation component of the matrix representing this quaternion
	 */
	public Mat3 asRotMatrix(){
		normalize();
		
		float x2 = data.x*data.x;
		float y2 = data.y*data.y;
		float z2 = data.z*data.z;
		
		float xy = data.x*data.y;
		float xz = data.x*data.z;
		float yz = data.y*data.z;
		
		float wx = data.w*data.x;
		float wy = data.w*data.y;
		float wz = data.w*data.z;
		
		return new Mat3( new Vec3(1.0f - 2.0f * (y2 + z2), 2.0f * (xy - wz), 2.0f * (xz + wy)),
						new Vec3(2.0f * (xy + wz), 1.0f - 2.0f * (x2 + z2), 2.0f * (yz - wx)),
						new Vec3(2.0f * (xz - wy), 2.0f * (yz + wx), 1.0f - 2.0f * (x2 + y2)));
	}
	
	/**
	 * Gets the raw data object for this quaternion
	 * 
	 * @return Main data storage for this quaternion
	 */
	public Vec4 getData(){
		return data;
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
	 * this effectively translates to (...(q1*q2)*q3)*q4)...*qn)
	 * 
	 * @param quats Quaternions to multiply
	 * @return The product of the given quaternions
	 */
	public static Quaternion multiply(Quaternion... quats){
		if(quats.length > 1){
			Quaternion result = new Quaternion(quats[0]);
			for(int curQuat = 1; curQuat < quats.length; curQuat++){
				result.set(result.mult(quats[curQuat]));// result *= quats[curQuat]
			}
			return result;
		}else{
			return quats.length == 1 ? quats[0] : null;
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
		Vec3 nAxis = new Vec3(axis);
		nAxis.normalize();
		float sinAngle = (float)Math.sin((angle*Math.PI/180)/2.0f);
		nAxis.scale(sinAngle);
		
		return new Quaternion(new Vec4(nAxis, (float)Math.cos((angle*Math.PI/180)/2.0f)));
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
}
