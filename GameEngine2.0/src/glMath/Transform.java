package glMath;

import glMath.matrices.Mat3;
import glMath.matrices.Mat4;
import glMath.vectors.Vec2;
import glMath.vectors.Vec3;
import glMath.vectors.Vec4;

public class Transform {
	private Quaternion orientation;
	private Vec3 position, scale;
	public static final Vec3 
	xAxis = new Vec3(1,0,0),
	yAxis = new Vec3(0,1,0),
	zAxis = new Vec3(0,0,1);
	
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
	 * 
	 * @return This transform post operation
	 */
	public Transform scale(float x, float y, float z){
		scale.set(scale.x*x, scale.y*y, scale.z*z);
		return this;
	}

	/**
	 *  Scales this transform's matrix by the given vector
	 *  
	 * @param scalars Vector containing scalars for this transform to apply
	 * 
	 * @return This transform post operation
	 */
	public Transform scale(Vec3 scalars){
		scale.set(scale.x*scalars.x, scale.y*scalars.y, scale.z*scalars.z);
		return this;
	}
	
	/**
	 * Scales this transform by the given scalar, this means all axis will
	 * be scaled uniformly.
	 * 
	 * @param scalar Scalar for the transform
	 * 
	 * @return This transform post operation
	 */
	public Transform scale(float scalar){
		scale.set(scale.x*scalar, scale.y*scalar, scale.z*scalar);
		return this;
	}
	
	/**
	 * Scales this Transform by the scale of the given Transform
	 * 
	 * @param trans Transform whose scale to use as the scaling
	 * 
	 * @return This Transform post operation
	 */
	public Transform scale(Transform trans){
		scale.set(scale.x*trans.scale.x, scale.y*trans.scale.y, scale.z*trans.scale.z);
		return this;
	}

	/**
	 * Translates this transform by the given amounts
	 * 
	 * @param x Amount to translate along the x axis
	 * @param y Amount to translate along the y axis
	 * @param z Amount to translate along the z axis
	 * 
	 * @return This transform post operation
	 */
	public Transform translate(float x, float y, float z){
		position.add(x, y, z);
		return this;
	}
	
	/**
	 * Translates this transform by the given vector
	 * 
	 * @param vector Vector to translate this transform on
	 * 
	 * @return This transform post operation
	 */
	public Transform translate(Vec3 vector){
		position.add(vector);
		return this;
	}
	
	/**
	 * Translates this Transform by the given Transforms translation
	 * 
	 * @param trans Transform whose translation to use in translating this Transform
	 * 
	 * @return This Transform post operation
	 */
	public Transform translate(Transform trans){
		position.add(trans.position);
		return this;
	}

	/**
	 * Rotates this transform be the given axis and angle theta
	 * 
	 * @param x X component of the axis to rotate around
	 * @param y Y component of the axis to rotate around
	 * @param z Z component of the axis to rotate around
	 * @param theta Amount to rotate around the given axis
	 * 
	 * @return This transform post operation
	 */
	public Transform rotate(float x, float y, float z, float theta){
		return rotate(Quaternion.fromAxisAngle(x, y, z, theta));
	} 
	
	/**
	 * Rotates this transform be the given axis and angle theta
	 * 
	 * @param axis Vector representing the axis to rotate around
	 * @param theta Amount to rotate around the given axis
	 * 
	 * @return This transform post operation
	 */
	public Transform rotate(Vec3 axis, float theta){
		return rotate(Quaternion.fromAxisAngle(axis, theta));
	}
	
	/**
	 * Rotates this transform around the given {@code axis} and {@code point}. The resulting transformation will be rotated and translated as a result
	 * of this function.
	 * 
	 * @param point Point to rotate the transformation around
	 * @param axis Axis to perform the rotation around
	 * @param theta Angle to rotate around the given axis and point by
	 * 
	 * @return This Transform object post transformation
	 */
	public Transform rotate(Vec3 point, Vec3 axis, float theta){
		return rotate(point, Quaternion.fromAxisAngle(axis, theta));
	}
	
	/**
	 * Rotates this transform around the given {@code point} by the rotation defined by the given Quaternion. 
	 * The resulting transformation will be rotated and translated as a result of this function.
	 * 
	 * @param point Point to rotate the transformation around
	 * @param rotation Quaternion representing the rotation to be applied to this Transform
	 * 
	 * @return This Transform object post transformation
	 */
	public Transform rotate(Vec3 point, Quaternion rotation){
		//start with the vector from the point to the transform origin
		Vec3 relaPoint = VecUtil.subtract(position, point);
		//apply the rotation on that vector
		Vec3 postTrans = rotation.multVec(relaPoint);
		//translate from the current position to the new position
		translate(VecUtil.subtract(postTrans, relaPoint));
		//apply the rotation onto the objects orientation
		return rotate(rotation);
	}
	
	/**
	 * Rotates this transform around the given point {@code posx, posy, posz} by the rotation defined by the given Quaternion. 
	 * The resulting transformation will be rotated and translated as a result of this function.
	 * 
	 * @param x X component of the position to rotate around
	 * @param y Y component of the position to rotate around
	 * @param z Z component of the position to rotate around
	 * @param rotation Quaternion representing the rotation to be applied to this Transform
	 * 
	 * @return This Transform object post transformation
	 */
	public Transform rotate(float x, float y, float z, Quaternion rotation){
		return rotate(new Vec3(x, y, z), rotation);
	}
	
	/**
	 * Rotates this transform around the given axis represented by {@code axisx, axisy, axisz} and {@code point}. 
	 * The resulting transformation will be rotated and translated as a result of this function.
	 * 
	 * @param point Point to rotate the transformation around
	 * @param posz Z component of the position to rotate around
	 * @param axisx X component of the axis to rotate around
	 * @param axisy Y component of the axis to rotate around
	 * @param axisz Z component of the axis to rotate around
	 * @param theta Angle to rotate around the given axis and point by
	 * 
	 * @return This Transform object post transformation
	 */
	public Transform rotate(Vec3 point, float axisx, float axisy, float axisz, float theta){
		return rotate(point, Quaternion.fromAxisAngle(new Vec3(axisx, axisy, axisz), theta));
	}
	
	/**
	 * Rotates this transform around the given {@code axis} and point {@code posx, posy, posz}. 
	 * The resulting transformation will be rotated and translated as a result of this function.
	 * 
	 * @param posx X component of the position to rotate around
	 * @param posy Y component of the position to rotate around
	 * @param posz Z component of the position to rotate around
	 * @param axis Axis to perform the rotation around
	 * @param theta Angle to rotate around the given axis and point by
	 * 
	 * @return This Transform object post transformation
	 */
	public Transform rotate(float posx, float posy, float posz, Vec3 axis, float theta){
		return rotate(new Vec3(posx, posy, posz), Quaternion.fromAxisAngle(axis, theta));
	}
	
	/**
	 * Rotates this transform around the given axis represented by {@code axisx, axisy, axisz} and point {@code posx, posy, posz}. 
	 * The resulting transformation will be rotated and translated as a result of this function.
	 * 
	 * @param posx X component of the position to rotate around
	 * @param posy Y component of the position to rotate around
	 * @param posz Z component of the position to rotate around
	 * @param axisx X component of the axis to rotate around
	 * @param axisy Y component of the axis to rotate around
	 * @param axisz Z component of the axis to rotate around
	 * @param theta Angle to rotate around the given axis and point by
	 * 
	 * @return This Transform object post transformation
	 */
	public Transform rotate(float posx, float posy, float posz, float axisx, float axisy, float axisz, float theta){
		return rotate(new Vec3(posx, posy, posz), Quaternion.fromAxisAngle(new Vec3(axisx, axisy, axisz), theta));
	}
	
	/**
	 * Rotates this Transform by the given Transforms rotation
	 * 
	 * @param trans Transform whose rotation will be used to rotate this Transform
	 * 
	 * @return This Transform post operation
	 */
	public Transform rotate(Transform trans){
		return rotate(trans.orientation);
	}
	
	/**
	 * Rotates this Transform by the given Quaternion rotation
	 * 
	 * @param rotation Quaternion containing the rotation to be applied to this Transform
	 * 
	 * @return This Transform post operation
	 */
	public Transform rotate(Quaternion rotation){
		orientation.set(Quaternion.multiply(rotation, orientation));
		return this;
	}
	
	/**
	 * Gets this transform's transformations as a 4x4 matrix. The matrix is constructed by first scaling, then orienting,
	 * then lastly translating.
	 */
	public Mat4 getMatrix(){
		return MatrixUtil.multiply(getTranslateMat(position), orientation.asMatrix(), getScaleMat(scale));
	}
	
	/**
	 * Transforms this by the given transform
	 * 
	 * @param value Transform to use in the modification of this object
	 * 
	 * @return This object after transformation
	 */
	public Transform transform(Transform value){
		orientation = Quaternion.multiply(value.orientation, orientation);
		position.add(value.position);
		scale.set(scale.x*value.scale.x, scale.y*value.scale.y, scale.z*value.scale.z);
		
		return this;
	}
	
	/**
	 * Transforms the target vector by the transformations of this Transform. The Vector is 
	 * scaled, rotated, then translated in that order.
	 * 
	 * @param target Vector to transform
	 * 
	 * @return New vector representing the {@code target} post transformation, {@code target} is unchanged
	 */
	public Vec3 transform(Vec3 target){
		Vec3 result = new Vec3(target);
		//scale the vector
		result.set(result.x*scale.x, result.y*scale.y, result.z*scale.z);
		//rotate the vector
		result = orientation.multVec(result);
		//translate the vector
		result.add(position);
		return result;
	}
	
	/**
	 * Transforms the {@code target} Transform by the inverse transformation of this transform object.
	 * The resulting Transform is transformed in place and is modified by this function.
	 * 
	 * @param target Transform to transform by the inverse transformation
	 * 
	 * @return The target Transform post inverse transformation
	 */
	public Transform inverseTransform(Transform target){
		//translate by the negative translation of this transform
		target.translate(-position.x, -position.y, -position.z);
		//rotate it by the conjugate orientation
		target.rotate(orientation.conjugate());
		//then scale, each value needs to be 1/scalar
		target.scale(1/scale.x, 1/scale.y, 1/scale.z);
		return target;
	}
	
	/**
	 * Transforms the {@code target} vector by the inverse transformation of this transform object.
	 * The resulting vector is the local space vector of this transform object.
	 * 
	 * @param target Vector to transform by the inverse transformation
	 * 
	 * @return A new vector representing the target vector post inverse transformation
	 */
	public Vec3 inverseTransform(Vec3 target){
		Vec3 result = new Vec3(target);
		//translate by the negative translation of this transform
		result.subtract(position);
		//rotate it by the conjugate orientation
		result = orientation.conjugate().multVec(result);
		//then scale, each value needs to be 1/scalar
		result.set(result.x/scale.x, result.y/scale.y, result.z/scale.z);
		return result;
	}
	
	/**
	 * Sets this transform equal to the given transform
	 * 
	 * @param trans Transform to copy into this transform
	 */
	public void set(Transform trans){
		orientation.set(trans.orientation);
		position.set(trans.position);
		scale.set(trans.scale);
	}
	
	/**
	 * Sets the scaling for this transform, this will overwrite any previous
	 * scaling data associated with this transform.
	 * 
	 * @param x X axis scale
	 * @param y Y axis scale
	 * @param z Z axis scale
	 */
	public void setScale(float x, float y, float z){
		scale.set(x, y, z);
	}
	
	/**
	 * Sets the scaling for this transform, this will overwrite any previous
	 * scaling data associated with this transform.
	 *  
	 * @param scalars Scalars to set this transforms scale to
	 */
	public void setScale(Vec3 scalars){
		scale.set(scalars);
	}
	
	/**
	 * Sets the scaling for this transform, this will overwrite any previous
	 * scaling data associated with this transform.
	 *  
	 * @param scalar Scalar to set all the scaling values to
	 */
	public void setScale(float scalar){
		scale.set(scalar, scalar, scalar);
	}
	
	/**
	 * Sets this Transforms scale to the given Transforms scale
	 * 
	 * @param trans Transform whose scale to set this Transforms scale to
	 */
	public void setScale(Transform trans){
		scale.set(trans.scale);
	}
	
	/**
	 * Sets the orientation for this transform
	 * 
	 * @param orientation Orientation to set this transform to
	 */
	public void setOrientation(Quaternion orientation){
		this.orientation.set(orientation);
	}
	
	/**
	 * Sets this Transforms rotation to the given Transforms rotation
	 * 
	 * @param trans Transform whose rotation to set this Transforms rotation to
	 */
	public void setOrientation(Transform trans){
		orientation.set(trans.orientation);
	}
	
	/**
	 * Sets the translation for this transform
	 * 
	 * @param x X translation
	 * @param y Y translation
	 * @param z Z translation
	 */
	public void setTranslation(float x, float y, float z){
		position.set(x, y, z);
	}
	
	/**
	 * Sets the translation for this transform
	 * 
	 * @param trans Translation to set this transforms translation to
	 */
	public void setTranslation(Vec3 trans){
		position.set(trans);
	}
	
	/**
	 * Sets this Transforms translation to the given Transforms translation
	 * 
	 * @param trans Transform whose translation to use in setting this Transforms translation to
	 */
	public void setTranslation(Transform trans){
		position.set(trans.position);
	}
	
	/**
	 * Gets the scalars for this transformation
	 * 
	 * @return The x, y, z scalars for this transformation
	 */
	public Vec3 getScalars(){
		return scale;
	}
	
	/**
	 * Gets the position of this transformation
	 * 
	 * @return The x, y, z position of this transformation
	 */
	public Vec3 getTranslation(){
		return position;
	}
	
	/**
	 * Gets the orientation of this transformation
	 * 
	 * @return The quaternion representing the orientation of this transformation
	 */
	public Quaternion getOrientation(){
		return orientation;
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
		return Quaternion.fromAxisAngle(x, y, z, theta).asMatrix();
	}
	
	/**
	 * Generates a 4x4 matrix representing a rotation around the axis given by the vector, by the angle theta
	 * 
	 * @param axis Vector representing the axis of rotation
	 * @param theta Angle to rotate about the axis in degrees
	 * @return A 4x4 matrix representing a rotation of theta degrees around the given axis
	 */
	public static Mat4 getRotateMat(Vec3 axis, float theta){
		return Quaternion.fromAxisAngle(axis, theta).asMatrix();
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
