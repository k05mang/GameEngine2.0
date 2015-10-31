package glMath;

public class Transform {
	private Quaternion orientation;
	private Vec3 position, scale;
	
	/**
	 * Constructs a transform object with set to the identity
	 */
	public Transform(){
		orientation = new Quaternion();
		position = new Vec3();
		scale = new Vec3(1);
	}
	
	/**
	 * Constructs this Transform using the given Transform's data to initialize its fields
	 * 
	 * @param copy Transform to copy from
	 */
	public Transform(Transform copy){
		orientation = new Quaternion(copy.orientation);
		position = new Vec3(copy.position);
		scale = new Vec3(copy.scale);
	}
	
	/**
	 * Scales this transform
	 * 
	 * @param x Amount to scale along the x axis
	 * @param y Amount to scale along the y axis
	 * @param z Amount to scale along the z axis
	 * @return This transform object
	 */
	public Transform scale(float x, float y, float z){
		scale.set(scale.x*x, scale.y*y, scale.z*z);
		return this;
	}

	/**
	 *  Scales this transform's matrix by the given vector
	 *  
	 * @param scalars Vector containing scalars for this transform to apply
	 * @return This transform object
	 */
	public Transform scale(Vec3 scalars){
		scale.set(scale.x*scalars.x, scale.y*scalars.y, scale.z*scalars.z);
		return this;
	}

	/**
	 * Translates this transform by the given amounts
	 * 
	 * @param x Amount to translate along the x axis
	 * @param y Amount to translate along the y axis
	 * @param z Amount to translate along the z axis
	 * @return This transform object
	 */
	public Transform translate(float x, float y, float z){
		position.add(x, y, z);
		return this;
	}
	
	/**
	 * Translates this transform by the given vector
	 * 
	 * @param vector Vector to translate this transform on
	 * @return This transform object
	 */
	public Transform translate(Vec3 vector){
		position.add(vector);
		return this;
	}

	/**
	 * Rotates this transform be the given axis and angel theta
	 * 
	 * @param x X component of the axis to rotate around
	 * @param y Y component of the axis to rotate around
	 * @param z Z component of the axis to rotate around
	 * @param theta Amount to rotate around the given axis
	 * @return This transform object
	 */
	public Transform rotate(float x, float y, float z, float theta){
		orientation.set(Quaternion.fromAxisAngle(x, y, z, theta).mult(orientation));
		return this;
	} 
	
	/**
	 * Rotates this transform be the given axis and angel theta
	 * 
	 * @param axis Vector representing the axis to rotate around
	 * @param theta Amount to rotate around the given axis
	 * @return This transform object
	 */
	public Transform rotate(Vec3 axis, float theta){
		orientation.set(Quaternion.fromAxisAngle(axis, theta).mult(orientation));
		return this;
	}
	
	/**
	 * Gets this transform's transformations as a 4x4 matrix. The matrix is constructed by first scaling, then orientating,
	 * then lastly translating.
	 */
	public Mat4 getTransform(){
		return MatrixUtil.multiply(getTranslateMat(position), orientation.asMatrix(), getScaleMat(scale));
	}
	
	/**
	 * Transforms this by the given transform
	 * 
	 * @param value Transform to use in the modification of this object
	 * @return This object after trasnformation
	 */
	public Transform transform(Transform value){
		orientation.set(Quaternion.multiply(value.orientation, orientation));
		position.add(value.position);
		Vec3 scalars = value.scale;
		scale.set(scale.x*scalars.x, scale.y*scalars.y, scale.z*scalars.z);
		
		return this;
	}

	
	/**
	 * Generates a scaling matrix of type Mat4 based on the given scalars
	 * 
	 * @param x X scale
	 * @param y Y scale
	 * @param z Z scale
	 * @return A 4x4 matrix of type Mat4 representing a scaling matrix using the given scaling components
	 */
	public static Mat4 getScaleMat(float x, float y, float z){
		return new Mat4(
				new Vec4(x,0,0,0),
				new Vec4(0,y,0,0),
				new Vec4(0,0,z,0),
				new Vec4(0,0,0,1)
				);
	}
	
	/**
	 * Generates a scaling matrix of type Mat4 based on the given scalars that are stored in the vector
	 *  
	 * @param scalars Vector containing the scalars for the matrix
	 * @return A 4x4 matrix of type Mat4 representing a scaling matrix using the given scaling components
	 */
	public static Mat4 getScaleMat(Vec3 scalars){
		return new Mat4(
				new Vec4(scalars.x,0,0,0),
				new Vec4(0,scalars.y,0,0),
				new Vec4(0,0,scalars.z,0),
				new Vec4(0,0,0,1)
				);
	}
	
	/**
	 * Generates a scaling matrix of type Mat3 based on the given scalars
	 * 
	 * @param x X scale
	 * @param y Y scale
	 * @return A 3x3 matrix of type Mat3 representing a scaling matrix using the given scaling components
	 */
	public static Mat3 getScaleMat(float x, float y){
		return new Mat3(
				new Vec3(x,0,0),
				new Vec3(0,y,0),
				new Vec3(0,0,1)
				);
	}
	
	/**
	 * Generates a scaling matrix of type Mat3 based on the given scalars that are stored in the vector
	 * 
	 * @param scalars Vector containing the scalars for this matrix
	 * @return A 3x3 matrix of type Mat3 representing a scaling matrix using the given scaling components
	 */
	public static Mat3 getScaleMat(Vec2 scalars){
		return new Mat3(
				new Vec3(scalars.x,0,0),
				new Vec3(0,scalars.y,0),
				new Vec3(0,0,1)
				);
	}
	
	/**
	 * Generates a 4x4 translation matrix
	 * 
	 * @param x X component to translate by
	 * @param y Y component to translate by
	 * @param z Z component to translate by
	 * @return A 4x4 matrix representing a translation by the given components 
	 */
	public static Mat4 getTranslateMat(float x, float y, float z){
		return new Mat4(
				new Mat3(1),
				new Vec3(x, y, z)
				);
	}
	
	/**
	 * Generates a 4x4 translation matrix
	 * 
	 * @param vector Vector representing a direction and magnitude of translation in a 3 dimensional space
	 * @return A 4x4 matrix representing a translation by the given vector
	 */
	public static Mat4 getTranslateMat(Vec3 vector){
		return new Mat4(
				new Mat3(1),
				vector
				);
	}
	
	/**
	 * Generates a 3x3 translation matrix
	 * 
	 * @param x X component to translate by
	 * @param y Y component to translate by
	 * @return A 3x3 matrix representing a translation by the given components
	 */
	public static Mat3 getTranslateMat(float x, float y){
		return new Mat3(
				new Vec3(1,0,0),
				new Vec3(0,1,0),
				new Vec3(x, y, 1)
				);
	}
	
	/**
	 * Generates a 3x3 translation matrix
	 * 
	 * @param vector Vector representing a direction and magnitude of translation in a 2 dimensional space
	 * @return A 3x3 matrix representing a translation by the given vector
	 */
	public static Mat3 getTranslateMat(Vec2 vector){
		return new Mat3(
				new Vec3(1,0,0),
				new Vec3(0,1,0),
				new Vec3(vector.x, vector.y, 1)
				);
	}
	
	/**
	 * Generates a 4x4 matrix representing a rotation around the axis given by the components x, y, z, by the angle theta
	 * 
	 * @param x X component of the axis of rotation
	 * @param y Y component of the axis of rotation
	 * @param z Z component of the axis of rotation
	 * @param theta Angle of rotation in degrees
	 * @return A 4x4 matrix representing a rotation of theta degrees around the given axis
	 */
	public static Mat4 getRotateMat(float x, float y, float z, float theta){
		Vec3 axis = new Vec3(x, y, z);
		axis.normalize();
		return Quaternion.fromAxisAngle(axis, theta).asMatrix();
	}
	
	/**
	 * Generates a 4x4 matrix representing a rotation around the axis given by the vector, by the angle theta
	 * 
	 * @param axis Vector representing the axis of rotation
	 * @param theta Angle to rotate about the axis in degrees
	 * @return A 4x4 matrix representing a rotation of theta degrees around the given axis
	 */
	public static Mat4 getRotateMat(Vec3 axis, float theta){
		Vec3 nAxis = new Vec3(axis);
		nAxis.normalize();
		return Quaternion.fromAxisAngle(nAxis, theta).asMatrix();
	}
	
	/**
	 * Generates a 3x3 matrix representing a rotation around the z axis 
	 * 
	 * @param theta Angle of rotation, in degrees,  around the z axis
	 * @return A 3x3 matrix representing a rotation of theta degrees around the z axis
	 */
	public static Mat3 getRotateMat(float theta){
		float cos = (float)Math.cos(theta*Math.PI/180.0f);
		float sin = (float)Math.sin(theta*Math.PI/180.0f);
		
		return new Mat3(
				new Vec3(cos, sin, 0),
				new Vec3(-sin, cos, 0),
				new Vec3(0,0,1)
				);
	}
}
