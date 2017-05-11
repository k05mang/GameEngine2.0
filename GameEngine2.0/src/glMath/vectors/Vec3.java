package glMath.vectors;


import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;

import org.lwjgl.BufferUtils;

/**
 * 
 * @author Kevin Mango
 *
 */
public class Vec3 implements Vector {

	public float x, y, z;
	public static final int SIZE_IN_BYTES = 12;
	public static final int SIZE_IN_FLOATS = 3;
	
	/**
	 * Default construct which initializes all vector components to 0
	 */
	public Vec3(){
		this(0,0,0);
	}
	
	/**
	 * Constructs a Vec3 objects with 3 floats
	 * 
	 * @param x X component of this vector
	 * @param y Y component of this vector
	 * @param z Z component of this vector
	 */
	public Vec3(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
		trunc();
	}
	
	/**
	 * Constructs a Vec3 object with a Vec2 for the x and y components of this vector
	 * and a float for the z
	 * 
	 * @param vect Vec2 to use in the initialization of this Vec3
	 * @param z Float to initialize the z component of this vector
	 */
	public Vec3(Vec2 vect, float z){
		this(vect.x, vect.y, z);
	}
	
	/**
	 * Constructs a Vec3 object with a Vec2 initializing the y and z components of this vector
	 * and a float initializing the x component of this vector
	 * 
	 * @param x Float to initialize the x component
	 * @param vect Vec2 used in initializing the y and z components
	 */
	public Vec3(float x, Vec2 vect){
		this(x, vect.x, vect.y);
	}
	
	/**
	 * Copy constructor for Vec3
	 * 
	 * @param copy Vector to copy from 
	 */
	public Vec3(Vec3 copy){
		this(copy.x, copy.y, copy.z);
	}
	
	/**
	 * Constructs a Vec3 with a base value which initializes all components of the vector with this value
	 * 
	 * @param initializer Value to initialize all components with
	 */
	public Vec3(float initializer){
		this(initializer, initializer, initializer);
	}
	
	/**
	 * Constructs a Vec3 with the first 3 components of the array
	 * 
	 * @param init Float array to use in the initialization
	 */
	public Vec3(float[] init){
		if(init.length >= 3){
			x = init[0];
			y = init[1];
			z = init[2];
			trunc();
		}
	}
	
	/**
	 * Computes the cross product of this vector with the given vector
	 * 
	 * @param vector Vector to compute the cross product with
	 * @return A vector which is the cross product vector orthogonal to the given vectors
	 */
	public Vec3 cross(Vec3 vector){
		return new Vec3(
				y*vector.z - z*vector.y,
				z*vector.x - x*vector.z,
				x*vector.y - y*vector.x
				);
	}
	
	@Override
	public float dot(Vector vector) {
		//determine what vector was passed to the function
		if(vector instanceof Vec2){
			Vec2 vect = (Vec2)vector;
			return x*vect.x+y*vect.y;//z and w are 0
		}else if(vector instanceof Vec3){
			Vec3 vect = (Vec3)vector;
			return x*vect.x+y*vect.y+z*vect.z;//w is 0
		}else{
			Vec4 vect = (Vec4)vector;
			return x*vect.x+y*vect.y+z*vect.z;//w doesn't exist
		}
	}

	@Override
	public float length() {
		return (float)Math.sqrt(x*x+y*y+z*z);
	}

	@Override
	public Vec3 normalize() {
		float len = (float)Math.sqrt(x*x+y*y+z*z);
		if(len != 0){
			x /= len;
			y /= len;
			z /= len;
			trunc();
		}
		return this;
	}

	@Override
	public Vec3 add(Vector vector) {
		if(vector instanceof Vec3){
			Vec3 vect = (Vec3)vector;
			x += vect.x;
			y += vect.y;
			z += vect.z;
			trunc();
		}else{
			System.err.println("Type mismatch in vector addition, vector is not of type Vec3");
		}
		return this;
	}
	
	public Vec3 add(float x, float y, float z) {
		this.x += x;
		this.y += y;
		this.z += z;
		trunc();
		return this;
	}

	@Override
	public Vec3 subtract(Vector vector) {
		if(vector instanceof Vec3){
			Vec3 vect = (Vec3)vector;
			x -= vect.x;
			y -= vect.y;
			z -= vect.z;
			trunc();
		}else{
			System.err.println("Type mismatch in vector subtraction, vector is not of type Vec3");
		}
		return this;
	}
	
	public Vec3 subtract(float x, float y, float z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		trunc();
		return this;
	}

	@Override
	public Vec3 scale(float factor) {
		x *= factor;
		y *= factor;
		z *= factor;
		trunc();
		return this;
	}
	
	@Override
	public float valueAt(int index) throws IndexOutOfBoundsException{
		switch(index){
			case 0:
				return x;
			case 1:
				return y;
			case 2:
				return z;
			default:
				throw new IndexOutOfBoundsException("Value at index: "+index+" is out of bounds for a Vec3");
		}
	}
	
	@Override
	public Vec3 set(int index, float value){
		switch(index){
			case 0:
				x = value;
				break;
			case 1:
				y = value;
				break;
			case 2:
				z = value;
				break;
			default:
				System.err.println("Index out of bounds for this vector");
				break;
		}
		trunc();
		return this;
	}
	
	@Override
	public Vec3 set(Vector vec){
		if(vec instanceof Vec3){
			Vec3 vector = (Vec3)vec;
			x = vector.x;
			y = vector.y;
			z = vector.z;
			trunc();
		}else{
			System.err.println("Vector given is not of type Vec3, failed to set values");
		}
		return this;
	}
	
	@Override
	public Vec3 set(float... values){
		switch(values.length){
			case 1:
				x = values[0];
				break;
			case 2:
				x = values[0];
				y = values[1];
				break;
			case 3:
				x = values[0];
				y = values[1];
				z = values[2];
				break;
			default:
				break;
		}
		trunc();
		return this;
	}
	
	@Override
	public void store(FloatBuffer storage){
		storage.put(x);
		storage.put(y);
		storage.put(z);
	}
	
	@Override
	public void store(ByteBuffer storage){
		storage.putFloat(x);
		storage.putFloat(y);
		storage.putFloat(z);
	}
	
	@Override
	public Vector swizzle(String type){
		type = type.trim();
		switch(type.length()){
			case 2:
				switch(type.charAt(0)){
					case 'x':
						return getPosType(type, 2);
					case 'y':
						return getPosType(type, 2);
					case 'z':
						return getPosType(type, 2);
						
					case 'r':
						return getColorType(type, 2);
					case 'g':
						return getColorType(type, 2);
					case 'b':
						return getColorType(type, 2);
						
					case 's':
						return getTextureType(type, 2);
					case 't':
						return getTextureType(type, 2);
					case 'p':
						return getTextureType(type, 2);
					default:
						System.err.println("Swizzle string must begin with either x, y, z, r, g, b, s, t, or p");
						return null;
				}
			case 3:
				switch(type.charAt(0)){
					case 'x':
						return getPosType(type, 3);
					case 'y':
						return getPosType(type, 3);
					case 'z':
						return getPosType(type, 3);
						
					case 'r':
						return getColorType(type, 3);
					case 'g':
						return getColorType(type, 3);
					case 'b':
						return getColorType(type, 3);
						
					case 's':
						return getTextureType(type, 3);
					case 't':
						return getTextureType(type, 3);
					case 'p':
						return getTextureType(type, 3);
					default:
						System.err.println("Swizzle string must begin with either x, y, z, r, g, b, s, t, or p");
						return null;
				}
			case 4:
				switch(type.charAt(0)){
					case 'x':
						return getPosType(type, 4);
					case 'y':
						return getPosType(type, 4);
					case 'z':
						return getPosType(type, 4);
						
					case 'r':
						return getColorType(type, 4);
					case 'g':
						return getColorType(type, 4);
					case 'b':
						return getColorType(type, 4);
						
					case 's':
						return getTextureType(type, 4);
					case 't':
						return getTextureType(type, 4);
					case 'p':
						return getTextureType(type, 4);
					default:
						System.err.println("Swizzle string must begin with either x, y, z, r, g, b, s, t, or p");
						return null;
				}
			default:
				System.err.println("Swizzle string must be of an appropriate length between 2 and 4");
				return null;
		}
	}
	
	private Vector getPosType(String type, int size){
		Vector result = null;
		switch(size){
			case 2:
				result = new Vec2();
				break;
			case 3:
				result = new Vec3();
				break;
			case 4:
				result = new Vec4();
				break;
		}
		
		for (int index = 0; index < size; index++) {
			char symbol = type.charAt(index);

			switch (symbol) {
				case 'x':
					result.set(index, x);
					break;
				case 'y':
					result.set(index, y);
					break;
				case 'z':
					result.set(index, z);
					break;
				default:
					System.err.println("A given swizzle value is not applicable to this vector");
					return null;
			}
		}
		return result;
	}
	
	private Vector getColorType(String type, int size){
		Vector result = null;
		switch(size){
			case 2:
				result = new Vec2();
				break;
			case 3:
				result = new Vec3();
				break;
			case 4:
				result = new Vec4();
				break;
		}
		
		for (int index = 0; index < size; index++) {
			char symbol = type.charAt(index);

			switch (symbol) {
				case 'r':
					result.set(index, x);
					break;
				case 'g':
					result.set(index, y);
					break;
				case 'b':
					result.set(index, z);
					break;
				default:
					System.err.println("A given swizzle value is not applicable to this vector");
					return null;
			}
		}
		return result;
	}
	
	private Vector getTextureType(String type, int size){
		Vector result = null;
		switch(size){
			case 2:
				result = new Vec2();
				break;
			case 3:
				result = new Vec3();
				break;
			case 4:
				result = new Vec4();
				break;
		}
		
		for (int index = 0; index < size; index++) {
			char symbol = type.charAt(index);

			switch (symbol) {
				case 's':
					result.set(index, x);
					break;
				case 't':
					result.set(index, y);
					break;
				case 'p':
					result.set(index, z);
					break;
				default:
					System.err.println("A given swizzle value is not applicable to this vector");
					return null;
			}
		}
		return result;
	}
	
	@Override
	public Vec3 proj(Vector onto){
		if(onto instanceof Vec3){
			Vec3 target = new Vec3((Vec3)onto);
			return target.scale(dot(target)/target.dot(target));
		}else{
			System.err.println("Error in vector projection, incompatible types");
			return null;
		}
	}
	
	@Override
	public float comp(Vector onto){
		if(onto instanceof Vec3){
			return dot(onto)/onto.length();
		}else{
			System.err.println("Error in vector projection, incompatible types");
			return 0;
		}
	}
	
	@Override
	public boolean equals(Object vect){
		if(vect instanceof Vec3){
			Vec3 vec = (Vec3)vect;
			return vec.x == x && vec.y == y && vec.z == z;
		}else{
			return false;
		}
	}
	
	@Override
	public int hashCode(){
		return Arrays.hashCode(new Float[]{x, y, z});
	}

	@Override
	public FloatBuffer asFloatBuffer(){
		FloatBuffer storage = BufferUtils.createFloatBuffer(SIZE_IN_FLOATS);
		this.store(storage);
		return (FloatBuffer)storage.flip();
	}
	
	@Override
	public ByteBuffer asByteBuffer(){
		ByteBuffer storage = BufferUtils.createByteBuffer(SIZE_IN_BYTES);
		this.store(storage);
		return (ByteBuffer)storage.flip();
	}
	
	@Override
	public String toString(){
		return "("+x+","+y+","+z+")";
	}
	
	@Override
	public void print(){
		System.out.println(toString());
	}
	
	@Override
	public boolean isZero(){
		return x == 0 && y == 0 && z == 0;
	}
	
	@Override
	public Vec3 inverse(){
		return (new Vec3(this)).scale(-1);
	}
	
	@Override
	public void trunc(){
		if(x < ROUND_VALUE && x > -ROUND_VALUE){
			x = 0.0f;
		}
		
		if(y < ROUND_VALUE && y > -ROUND_VALUE){
			y = 0.0f;
		}
		
		if(z < ROUND_VALUE && z > -ROUND_VALUE){
			z = 0.0f;
		}
	}

	@Override
	public void write(DataOutputStream stream) throws IOException {
		stream.writeFloat(this.x);
		stream.writeFloat(this.y);
		stream.writeFloat(this.z);
	}
}
