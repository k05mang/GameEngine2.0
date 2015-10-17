package primitives;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import glMath.*;

/**
 * class for storing and retrieving 3-dimensional coordinates
 * 
 * @author Kevin Mango
 * @version 1
 *
 */
public class Vertex{

	private Vec3 pos, normal, tangent, bitangent;
	private Vec2 textCoords;

	/**
	 * Constructor for a vertex object
	 * 
	 * @param x world x-coordinate
	 * @param y world y-coordinate
	 * @param z world z-coordinate
	 * 
	 * @param normalX x component of the normal vector for this vertex
	 * @param normalY y component of the normal vector for this vertex
	 * @param normalZ z component of the normal vector for this vertex
	 * 
	 * @param u U-coordinate of this vertex's texture
	 * @param v V-coordinate of this vertex's texture
	 */
	public Vertex(float x, float y, float z, float normalX, float normalY, float normalZ, float u, float v,
			float tx, float ty, float tz, float btx, float bty, float btz){
		this.pos = new Vec3(x, y, z);
		this.normal = new Vec3(normalX, normalY, normalZ);
		this.normal.normalize();
		this.textCoords = new Vec2(u, v);
		this.tangent = new Vec3(tx, ty, tz);
		this.bitangent = new Vec3(btx, bty, btz);
	}
	
	public Vertex(Vec3 pos, Vec3 normal, Vec2 textCoord, Vec3 tangent, Vec3 bitangent){
		this(pos.x, pos.y, pos.z, normal.x, normal.y, normal.z, textCoord.x, textCoord.y,
				tangent.x, tangent.y, tangent.z, bitangent.x, bitangent.y, bitangent.z);
	}
	
	public Vertex(float x, float y, float z, Vec3 normal, Vec2 textCoord, Vec3 tangent, Vec3 bitangent){
		this(x,y,z, normal.x, normal.y, normal.z, textCoord.x, textCoord.y,
				tangent.x, tangent.y, tangent.z, bitangent.x, bitangent.y, bitangent.z);
	}
	
	public Vertex(Vec3 pos, float normalX, float normalY, float normalZ, Vec2 textCoord, Vec3 tangent, Vec3 bitangent){
		this(pos.x, pos.y, pos.z, normalX, normalY, normalZ, textCoord.x, textCoord.y,
				tangent.x, tangent.y, tangent.z, bitangent.x, bitangent.y, bitangent.z);
	}
	
	public Vertex(Vec3 pos,  Vec3 normal, float u, float v, Vec3 tangent, Vec3 bitangent){
		this(pos.x, pos.y, pos.z, normal.x, normal.y, normal.z, u, v,
				tangent.x, tangent.y, tangent.z, bitangent.x, bitangent.y, bitangent.z);
	}
	
	public Vertex(Vec3 pos,  Vec3 normal, Vec2 textCoord, float tx, float ty, float tz, Vec3 bitangent){
		this(pos.x, pos.y, pos.z, normal.x, normal.y, normal.z, textCoord.x, textCoord.y,
				tx, ty, tz, bitangent.x, bitangent.y, bitangent.z);
	}
	
	public Vertex(Vec3 pos,  Vec3 normal, Vec2 textCoord, Vec3 tangent, float btx, float bty, float btz){
		this(pos.x, pos.y, pos.z, normal.x, normal.y, normal.z, textCoord.x, textCoord.y,
				tangent.x, tangent.y, tangent.z, btx, bty, btz);
	}
	
	public Vec3 getPos(){
		return pos;
	}
	
	public Vec3 getNormal(){
		return normal;
	}
	
	public Vec2 getUV(){
		return textCoords;
	}
	
	public void setPos(Vec3 newPos){
		pos.set(newPos);
	}
	
	public void setPos(float x, float y, float z){
		pos.set(x, y, z);
	}
	
	public void setNormal(Vec3 newNorm){
		normal.set(newNorm);
	}
	
	public void setNormal(float nx, float ny, float nz){
		normal.set(nx, ny, nz);
	}
	
	public void setUV(Vec2 newUVs){
		textCoords.set(newUVs);
	}
	
	public void setUV(float u, float v){
		textCoords.set(u, v);
	}
	
	public Vec3 getTangent(){
		return tangent;
	}
	
	public void setTangent(float tx, float ty, float tz){
		tangent.set(tx, ty, tz);
		tangent.normalize();
	}
	
	public void setTangent(Vec3 tangent){
		this.tangent.set(tangent);
		this.tangent.normalize();
	}
	
//	public void addToTangent(Vec3 newTangent){
//		tangent.add(newTangent);
//		tangent.normalize();
//	}
	
	public Vec3 getBitangent(){
		return bitangent;
	}
	
	public void setBitangent(float btx, float bty, float btz){
		bitangent.set(btx, bty, btz);
		bitangent.normalize();
	}
	
	public void setBitangent(Vec3 bitangent){
		this.bitangent.set(bitangent);
		this.bitangent.normalize();
	}
	
//	public void addToBitangent(Vec3 newBitangent){
//		bitangent.add(newBitangent);
//		bitangent.normalize();
//	}
	
	public void store(ByteBuffer buffer){
		pos.store(buffer);
		normal.store(buffer);
		textCoords.store(buffer);
//		tangent.store(buffer);
//		bitangent.store(buffer);
	}
	
	public void write(DataOutputStream stream) throws IOException{
		pos.write(stream);
		normal.write(stream);
		textCoords.write(stream);
		tangent.write(stream);
		bitangent.write(stream);
	}
	
	@Override
	public boolean equals(Object o){
		if(o!= null && o instanceof Vertex){
			Vertex equality = (Vertex) o;
			return pos.equals(equality.pos) && 
//					normal.equals(equality.normal);// && 
					textCoords.equals(equality.textCoords);
		}
		return false;
	}
	
	@Override
	public int hashCode(){
		int result = pos.hashCode();
//		result = 37*result+normal.hashCode();
		result = 37*result+textCoords.hashCode();
		return result;
	}
	
	public void print(){
		System.out.println(pos.toString()+"\n"+normal.toString()+"\n"+textCoords.toString()+"\n");
	}
}