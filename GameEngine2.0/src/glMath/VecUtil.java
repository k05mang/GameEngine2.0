package glMath;

import glMath.vectors.Vec2;
import glMath.vectors.Vec3;
import glMath.vectors.Vec4;
import glMath.vectors.Vector;

public abstract class VecUtil {
	/**
	 * Computes the dot product of two vectors of the same type
	 * 
	 * @param vec1 Vector to start from
	 * @param vec2 Vector to dot with
	 * @return Float representing the dot product of the given vectors, or 0 in the event of a 
	 * mismatch
	 */
	public static float dot(Vector vec1, Vector vec2){
		return vec1.dot(vec2);
	}
	
	/**
	 * Generates the cross product of the given vectors.
	 * The cross product produced by this function takes the first 2 arguments crosses them
	 * and uses the result of that cross product as the left hand value for the next value in
	 * the given set, and continues this for each element in the given arguments. 
	 * 
	 * So given the vectors {A, B, C, D}, this would return ( (A x B) x C) x D.
	 * 
	 * @param vectors Vectors of type Vec3 to be used in determining the cross product
	 * @return The vector representing the chained cross product of the given arguments
	 */
	public static Vec3 cross(Vec3... vectors){
		if(vectors.length > 1){
			Vec3 result = vectors[0].cross(vectors[1]);
			for(int curVec = 2; curVec < vectors.length; curVec++){
				result.set(result.cross(vectors[curVec]));
			}
			return result;
		}else{
			return null;
		}
	}
	
	/**
	 * Adds vectors together, returning the result of their sum
	 * 
	 * @param vectors Vectors to be added together from left to right
	 * @return The sum of the given vectors
	 */
	public static Vec2 add(Vec2... vectors){
		if(vectors.length > 0){
			Vec2 result = new Vec2(0);
			for (int curVector = 0; curVector < vectors.length; curVector++){
				result.add(vectors[curVector]);
			}
			return result;
		}else{
			return null;
		}
	}
	
	/**
	 * Adds vectors together, returning the result of their sum
	 * 
	 * @param vectors Vectors to be added together from left to right
	 * @return The sum of the given vectors
	 */
	public static Vec3 add(Vec3... vectors){
		if(vectors.length > 0){
			Vec3 result = new Vec3(0);
			for (int curVector = 0; curVector < vectors.length; curVector++){
				result.add(vectors[curVector]);
			}
			return result;
		}else{
			return null;
		}
	}

	/**
	 * Adds vectors together, returning the result of their sum
	 * 
	 * @param vectors Vectors to be added together from left to right
	 * @return The sum of the given vectors
	 */
	public static Vec4 add(Vec4... vectors){
		if(vectors.length > 0){
			Vec4 result = new Vec4(0);
			for (int curVector = 0; curVector < vectors.length; curVector++){
				result.add(vectors[curVector]);
			}
			return result;
		}else{
			return null;
		}
	}
	
	/**
	 * Subtracts vectors, returning the result of their difference
	 * 
	 * @param vectors Vectors to be subtracted from left to right
	 * @return The difference of the given vectors
	 */
	public static Vec2 subtract(Vec2... vectors){
		if(vectors.length > 1){
			Vec2 result = new Vec2(vectors[0]);
			for (int curVector = 1; curVector < vectors.length; curVector++){
				result.subtract(vectors[curVector]);
			}
			return result;
		}else{
			return vectors.length == 1 ? new Vec2(vectors[0]) : null;
		}
	}

	/**
	 * Subtracts vectors, returning the result of their difference
	 * 
	 * @param vectors Vectors to be subtracted from left to right
	 * @return The difference of the given vectors
	 */
	public static Vec3 subtract(Vec3... vectors){
		if(vectors.length > 1){
			Vec3 result = new Vec3(vectors[0]);
			for (int curVector = 1; curVector < vectors.length; curVector++){
				result.subtract(vectors[curVector]);
			}
			return result;
		}else{
			return vectors.length == 1 ? new Vec3(vectors[0]) : null;
		}
	}

	/**
	 * Subtracts vectors, returning the result of their difference
	 * 
	 * @param vectors Vectors to be subtracted from left to right
	 * @return The difference of the given vectors
	 */
	public static Vec4 subtract(Vec4... vectors){
		if(vectors.length > 1){
			Vec4 result = new Vec4(vectors[0]);
			for (int curVector = 1; curVector < vectors.length; curVector++){
				result.subtract(vectors[curVector]);
			}
			return result;
		}else{
			return vectors.length == 1 ? new Vec4(vectors[0]) : null;
		}
	}
	
	/**
	 * Computes the reflection of a vector over a given incident vector
	 * 
	 * @param incident Vector being reflected
	 * @param normal Vector to be reflected over
	 * @return The reflection of the incident vector over the given normal vector
	 */
	public static Vec2 reflect(Vec2 incident, Vec2 normal){
		Vec2 i = new Vec2(incident);
		Vec2 n = new Vec2(incident);
		n.normalize();
		return i.subtract(n.scale(2*(i.dot(n))));
	}
	
	/**
	 * Computes the reflection of a vector over a given incident vector
	 * 
	 * @param incident Vector being reflected
	 * @param normal Vector to be reflected over
	 * @return The reflection of the incident vector over the given normal vector
	 */
	public static Vec3 reflect(Vec3 incident, Vec3 normal){
		Vec3 i = new Vec3(incident);
		Vec3 n = new Vec3(incident);
		n.normalize();
		return i.subtract(n.scale(2*(i.dot(n))));
	}
	
	/**
	 * Computes the reflection of a vector over a given incident vector
	 * 
	 * @param incident Vector being reflected
	 * @param normal Vector to be reflected over
	 * @return The reflection of the incident vector over the given normal vector
	 */
	public static Vec4 reflect(Vec4 incident, Vec4 normal){
		Vec4 i = new Vec4(incident);
		Vec4 n = new Vec4(incident);
		n.normalize();
		return i.subtract(n.scale(2*(i.dot(n))));
	}
	
	/**
	 * Scales the given vector by the given scalar factor without modifying the given vector
	 * 
	 * @param vector Vector to scale
	 * @param scalar Factor to scale the vector by
	 * @return A new vector representing the given vector scaled by the given factor
	 */
	public static Vec2 scale(Vec2 vector, float scalar){
		return new Vec2(vector).scale(scalar);
	}

	/**
	 * Scales the given vector by the given scalar factor without modifying the given vector
	 * 
	 * @param vector Vector to scale
	 * @param scalar Factor to scale the vector by
	 * @return A new vector representing the given vector scaled by the given factor
	 */
	public static Vec3 scale(Vec3 vector, float scalar){
		return new Vec3(vector).scale(scalar);
	}

	/**
	 * Scales the given vector by the given scalar factor without modifying the given vector
	 * 
	 * @param vector Vector to scale
	 * @param scalar Factor to scale the vector by
	 * @return A new vector representing the given vector scaled by the given factor
	 */
	public static Vec4 scale(Vec4 vector, float scalar){
		return new Vec4(vector).scale(scalar);
	}
	
	/**
	 * Converts the given Vector {@code vec} to a type Vec2.
	 * <br>
	 * With the higher order vectors the first two values are used to construct the Vec2, the x and y values.
	 * If the type passed to this function is a Vec2 then the vector is returned from the function as a copy.
	 * 
	 * @param vec Vector to convert
	 * 
	 * @return Vec2 containing the first two values of the passed vector
	 */
	public static Vec2 toVec2(Vector vec){
		//check if the vector given is one of the other types and convert
		if(vec instanceof Vec3){
			Vec3 vector = (Vec3)vec;
			return new Vec2(vector.x, vector.y);
		}else if(vec instanceof Vec4){
			Vec4 vector = (Vec4)vec;
			return new Vec2(vector.x, vector.y);
		}
		//otherwise return the vector
		return new Vec2((Vec2)vec);
	}
	
	/**
	 * Converts the given Vector {@code vec} to a type Vec3.
	 * <br>
	 * With Vec4 types the first three values are used to construct the Vec3, the x, y, z values.
	 * In the case of Vec2 types the vector returned will contain the first two values of the vector with z = 0.
	 * If the type passed to this function is a Vec3 then the vector is returned from the function as a copy.
	 * 
	 * @param vec Vector to convert
	 * 
	 * @return Vec3 containing the padded or trimmed values of the passed vector
	 */
	public static Vec3 toVec3(Vector vec){
		//check if the vector given is one of the other types and convert
		if(vec instanceof Vec2){
			Vec2 vector = (Vec2)vec;
			return new Vec3(vector.x, vector.y, 0.0f);
		}else if(vec instanceof Vec4){
			Vec4 vector = (Vec4)vec;
			return new Vec3(vector.x, vector.y, vector.z);
		}
		//otherwise return the vector
		return new Vec3((Vec3)vec);
	}
	
	/**
	 * Converts the given Vector {@code vec} to a type Vec4.
	 * <br>
	 * With Vec3 types the first three values are used to construct the Vec4 then w = 0.
	 * In the case of Vec2 types the vector returned will contain the first two values of the vector with z and y = 0.
	 * If the type passed to this function is a Vec4 then the vector is returned from the function as a copy.
	 * 
	 * @param vec Vector to convert
	 * 
	 * @return Vec4 containing the the padded values of the passed vector
	 */
	public static Vec4 toVec4(Vector vec){
		//check if the vector given is one of the other types and convert
		if(vec instanceof Vec2){
			Vec2 vector = (Vec2)vec;
			return new Vec4(vector.x, vector.y, 0.0f, 0.0f);
		}else if(vec instanceof Vec3){
			Vec3 vector = (Vec3)vec;
			return new Vec4(vector.x, vector.y, vector.z, 0.0f);
		}
		//otherwise return the vector
		return new Vec4((Vec4)vec);
	}
	
	/**
	 * Determines the distance the given {@code point} is from the line formed by {@code start} and {@code end}
	 * 
	 * @param start Starting point of the line formed with end
	 * @param end End point of the line formed with start
	 * @param point Point to determine distance of from the line made by start and end
	 * 
	 * @return Distance {@code point} is from the line formed by {@code start} and {@code end}
	 */
	public static float distance(Vector start, Vector end, Vector point){
		//d = |(end-start)X(point-start)|
//		      ---------------------------
//		      		 |(end-start)|
		
		Vec3 line = toVec3(end).subtract(toVec3(start));
		Vec3 relaPoint = toVec3(point).subtract(toVec3(start));
		//check if the line is 0 length
		if(line.length() == 0){
			//if it is then just return the distance between the start and point
			return relaPoint.length();
		}else{
			//otherwise use the formula above to get the distance
			return line.cross(relaPoint).length()/line.length();
		}
	}
}
