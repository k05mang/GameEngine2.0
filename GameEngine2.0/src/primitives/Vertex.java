package primitives;

import java.util.Arrays;

import glMath.*;
import gldata.VertexArray;
import gldata.BufferObject;

/**
 * Class for working with generic vertex information such as position, normal, texture coordinate, and texture space tangent and bitangent.
 * Tangent and bitangent are optional fields are not required for the function of this class. The vertex operates primarily in 3D space but 
 * theoretically could be applied to a 2D application.
 * 
 * @author Kevin Mango
 * @version 2
 *
 */
public class Vertex{

	private Vec3 pos, normal, tangent, bitangent;
	private Vec2 textCoords;

	/**
	 * Constructs a vertex object using the given float values to initialize the vertex's fields,
	 * the normal, tangent, and bitangent values will be normalized in the vertex
	 * 
	 * @param x X component of this vertex's position
	 * @param y Y component of this vertex's position
	 * @param z Z component of this vertex's position
	 * 
	 * @param normalX X component of this vertex's normal
	 * @param normalY Y component of this vertex's normal
	 * @param normalZ Z component of this vertex's normal
	 * 
	 * @param u X component of this vertex's texture coordinate
	 * @param v Y component of this vertex's texture coordinate
	 * 
	 * @param tx X component of this vertex's tangent to its texture space
	 * @param ty Y component of this vertex's tangent to its texture space
	 * @param tz Z component of this vertex's tangent to its texture space
	 * 
	 * @param btx X component of this vertex's bitangent to its texture space
	 * @param bty Y component of this vertex's bitangent to its texture space
	 * @param btz Z component of this vertex's bitangent to its texture space
	 */
	public Vertex(float x, float y, float z, float normalX, float normalY, float normalZ, float u, float v,
			float tx, float ty, float tz, float btx, float bty, float btz){
		this.pos = new Vec3(x, y, z);
		this.normal = new Vec3(normalX, normalY, normalZ);
		this.normal.normalize();
		this.textCoords = new Vec2(u, v);
		
		this.tangent = new Vec3(tx, ty, tz);
		this.tangent.normalize();
		
		this.bitangent = new Vec3(btx, bty, btz);
		this.bitangent.normalize();
	}
	
	/**
	 * Constructs a vertex object using the given vectors to initialize the vertex's fields, these vectors are copied 
	 * into their respective fields, the normal, tangent, and bitangent values will be normalized in the vertex
	 * 
	 * @param pos Position vector for this vertex
	 * @param normal Normal vector for this vertex
	 * @param textCoord Texture coordinates for this vertex
	 * @param tangent Texture space tangent for this vertex
	 * @param bitangent Texture space bitangent for this vertex
	 */
	public Vertex(Vec3 pos, Vec3 normal, Vec2 textCoord, Vec3 tangent, Vec3 bitangent){
		this(pos.x, pos.y, pos.z, normal.x, normal.y, normal.z, textCoord.x, textCoord.y,
				tangent.x, tangent.y, tangent.z, bitangent.x, bitangent.y, bitangent.z);
	}
	
	/**
	 * Constructs a vertex object using the given vectors to initialize the vertex's fields, these vectors are copied 
	 * into their respective fields, the normal, values will be normalized in the vertex
	 * <p>
	 * This will construct a vertex without a tangent and bitangent being initialized, future calls to these functions will 
	 * either return null when calling a getter function, or will instantiate a new vector for the variable if a setter
	 * or if a summing function is called.
	 * 
	 * @param pos Position vector for this vertex
	 * @param normal Normal vector for this vertex, the copy of this will be normalized in the vertex
	 * @param textCoord Texture coordinates for this vertex
	 */
	public Vertex(Vec3 pos, Vec3 normal, Vec2 textCoord){
		this(pos.x, pos.y, pos.z, normal.x, normal.y, normal.z, textCoord.x, textCoord.y);
	}
	
	/**
	 * Constructs a vertex object using the given float values
	 * <p>
	 * This will construct a vertex without a tangent and bitangent being initialized, future calls to these functions will 
	 * either return null when calling a getter function, or will instantiate a new vector for the variable if a setter
	 * or if a summing function is called.
	 * 
	 * @param x X component of this vertex's position
	 * @param y Y component of this vertex's position
	 * @param z Z component of this vertex's position
	 * 
	 * @param normalX X component of this vertex's normal
	 * @param normalY Y component of this vertex's normal
	 * @param normalZ Z component of this vertex's normal
	 * 
	 * @param u X component of this vertex's texture coordinate
	 * @param v Y component of this vertex's texture coordinate
	 */
	public Vertex(float x, float y, float z, float normalX, float normalY, float normalZ, float u, float v){
		this.pos = new Vec3(x, y, z);
		this.normal = new Vec3(normalX, normalY, normalZ);
		this.normal.normalize();
		this.textCoords = new Vec2(u, v);
		this.tangent = null;
		this.bitangent = null;
	}
	
//	public Vertex(float x, float y, float z, Vec3 normal, Vec2 textCoord, Vec3 tangent, Vec3 bitangent){
//		this(x,y,z, normal.x, normal.y, normal.z, textCoord.x, textCoord.y,
//				tangent.x, tangent.y, tangent.z, bitangent.x, bitangent.y, bitangent.z);
//	}
//	
//	public Vertex(Vec3 pos,  Vec3 normal, float u, float v, Vec3 tangent, Vec3 bitangent){
//		this(pos.x, pos.y, pos.z, normal.x, normal.y, normal.z, u, v,
//				tangent.x, tangent.y, tangent.z, bitangent.x, bitangent.y, bitangent.z);
//	}
//	
//	public Vertex(Vec3 pos,  Vec3 normal, Vec2 textCoord, float tx, float ty, float tz, Vec3 bitangent){
//		this(pos.x, pos.y, pos.z, normal.x, normal.y, normal.z, textCoord.x, textCoord.y,
//				tx, ty, tz, bitangent.x, bitangent.y, bitangent.z);
//	}
//	
//	public Vertex(Vec3 pos,  Vec3 normal, Vec2 textCoord, Vec3 tangent, float btx, float bty, float btz){
//		this(pos.x, pos.y, pos.z, normal.x, normal.y, normal.z, textCoord.x, textCoord.y,
//				tangent.x, tangent.y, tangent.z, btx, bty, btz);
//	}
	
	/**
	 * Gets the position for this vertex
	 * 
	 * @return The position of this vertex as a 3 component vector
	 */
	public Vec3 getPos(){
		return pos;
	}

	/**
	 * Gets the normal for this vertex
	 * 
	 * @return The normal of this vertex as a 3 component vector
	 */
	public Vec3 getNormal(){
		return normal;
	}

	/**
	 * Gets the texture coordinates for this vertex
	 * 
	 * @return The texture coordinates of this vertex as a 2 component vector
	 */
	public Vec2 getUV(){
		return textCoords;
	}
	
	/**
	 * Sets the position of this vertex to the given vector
	 * 
	 * @param newPos New position of this vertex as a vector
	 */
	public void setPos(Vec3 newPos){
		pos.set(newPos);
	}
	
	/**
	 * Sets the position of this vertex to the given float values
	 * 
	 * @param x X component of the new position for this vertex
	 * @param y Y component of the new position for this vertex
	 * @param z Z component of the new position for this vertex
	 */
	public void setPos(float x, float y, float z){
		pos.set(x, y, z);
	}

	/**
	 * Sets the normal of this vertex to the given vector, after being copied the vector will
	 * be normalized
	 * 
	 * @param newNorm New normal of this vertex as a vector
	 */
	public void setNormal(Vec3 newNorm){
		normal.set(newNorm);
		normal.normalize();
	}
	
	/**
	 * Sets the normal of this vertex to the given float values, then normalizes this vertex's normal
	 * 
	 * @param nx X component of the new normal for this vertex
	 * @param ny Y component of the new normal for this vertex
	 * @param nz Z component of the new normal for this vertex
	 */
	public void setNormal(float nx, float ny, float nz){
		normal.set(nx, ny, nz);
		normal.normalize();
	}
	
	/**
	 * Sets this vertex's texture coordinates to the given vector
	 * 
	 * @param newUVs New texture coordinates of this vertex as a vector
	 */
	public void setUV(Vec2 newUVs){
		textCoords.set(newUVs);
	}
	
	/**
	 * Sets this vertex's texture coordinates to the given float values
	 * 
	 * @param u X component of the new texture coordinate
	 * @param v Y component of the new texture coordinate
	 */
	public void setUV(float u, float v){
		textCoords.set(u, v);
	}
	
	/**
	 * Gets the texture coordinate tangent for this vertex or null if one was never given
	 * 
	 * @return Vector containing the components of this vertex's texture space tangent or null if non was initialized
	 */
	public Vec3 getTangent(){
		return tangent;
	}
	
	/**
	 * Sets the tangent of this vertex, or initializes a new tangent if the vertex was constructed with one
	 * 
	 * @param tx X component of the new tangent for this vertex
	 * @param ty Y component of the new tangent for this vertex
	 * @param tz Z component of the new tangent for this vertex
	 */
	public void setTangent(float tx, float ty, float tz){
		//check if the tangent was initialized or not
		if(tangent == null){
			tangent = new Vec3(tx, ty, tz);
		}else{
			tangent.set(tx, ty, tz);
		}
		tangent.normalize();
	}
	
	/**
	 * Sets this vertex's tangent to the given vector
	 * 
	 * @param newTangent Vector to set as the new tangent for this vertex
	 */
	public void setTangent(Vec3 newTangent){
		//check if the tangent was initialized or not
		if(tangent == null){
			tangent = new Vec3(newTangent);
		}else{
			tangent.set(newTangent);
		}
		this.tangent.normalize();
	}
	
	/**
	 * Adds the given vector to the tangent field of this vertex, or initializes a new tangent if the vertex
	 * was constructed without one
	 * 
	 * @param addition Vector to add to this vertex's tangent
	 */
	public void addToTangent(Vec3 addition){
		//check if the tangent was initialized or not
		if(tangent == null){
			tangent = new Vec3(addition);
		}else{
			tangent.add(addition);
		}
		tangent.normalize();
	}
	
	/**
	 * Gets this vertex's bitangent or null if the vertex was constructed without one
	 * 
	 * @return Vector containing the components of this vertex's bitangent or null
	 */
	public Vec3 getBitangent(){
		return bitangent;
	}
	
	/**
	 * Sets this vertex's bitangent with the given float values or initializes a new bitangent if this vertex was 
	 * constructed without one
	 * 
	 * @param btx X component of this vertex's new bitangent
	 * @param bty Y component of this vertex's new bitangent
	 * @param btz Z component of this vertex's new bitangent
	 */
	public void setBitangent(float btx, float bty, float btz){
		if(tangent == null){
			tangent = new Vec3(btx, bty, btz);
		}else{
			tangent.set(btx, bty, btz);
		}
		bitangent.normalize();
	}
	
	/**
	 * Sets this vertex's bitangent to the given vector or initializes a new bitangent if this vertex was 
	 * constructed without one
	 * 
	 * @param bitangent New bitangent for this vertex
	 */
	public void setBitangent(Vec3 newBitangent){
		if(tangent == null){
			tangent = new Vec3(newBitangent);
		}else{
			tangent.set(newBitangent);
		}
		this.bitangent.normalize();
	}
	
	/**
	 * Adds the given vector to the bitangent field of this vertex, or initializes a new bitangent if the vertex
	 * was constructed without one
	 * 
	 * @param addition Vector to add to this vertex's bitangent
	 */
	public void addToBitangent(Vec3 addition){
		if(bitangent == null){
			bitangent = new Vec3(addition);
		}else{
			bitangent.add(addition);
		}
		bitangent.normalize();
	}
	
	/**
	 * Adds this vertex to the given BufferObject.
	 * <p>
	 * All fields will be added to the vertex array including tangent and bitangent if applicable.
	 * <p>
	 * The fields will be added in the following order:
	 * <ul>
	 * <li>position</li>
	 * <li>normal</li>
	 * <li>texture coordinates</li>
	 * <li>tangent(if applicable)</li>
	 * <li>bitangent(if applicable)</li>
	 * </ul>
	 * 
	 * @param buffer BufferObject to add this vertex to
	 */
	public void addTo(BufferObject buffer){
		buffer.add(pos);
		buffer.add(normal);
		buffer.add(textCoords);
		if(tangent != null){
			buffer.add(tangent);
		}
		
		if(bitangent != null){
			buffer.add(bitangent);
		}
	}
	
	/**
	 * Adds this vertex to the given vertex array object.
	 * <p>
	 * All fields will be added to the vertex array including tangent and bitangent if applicable.
	 * <p>
	 * The fields will be added in the following order:
	 * <ul>
	 * <li>position</li>
	 * <li>normal</li>
	 * <li>texture coordinates</li>
	 * <li>tangent(if applicable)</li>
	 * <li>bitangent(if applicable)</li>
	 * </ul>
	 * 
	 * @param buffer Vertex array object to add this vertex to
	 */
	public void addTo(VertexArray buffer){
		buffer.add(pos);
		buffer.add(normal);
		buffer.add(textCoords);
		if(tangent != null){
			buffer.add(tangent);
		}
		
		if(bitangent != null){
			buffer.add(bitangent);
		}
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Vertex){
			Vertex vertex = (Vertex) o;
			return pos.equals(vertex.pos) && 
					normal.equals(vertex.normal) && 
					textCoords.equals(vertex.textCoords);
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		return Arrays.hashCode(new Object[]{pos, normal, textCoords});
	}
	
	/**
	 * Prints this vertex's information to the terminal window
	 */
	public void print(){
		System.out.println(pos.toString());
		System.out.println(normal.toString());
		System.out.println(textCoords.toString());
		if(tangent != null){
			System.out.println(tangent.toString());
		}
		if(bitangent != null){
			System.out.println(bitangent.toString());
		}
	}
}