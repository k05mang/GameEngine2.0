package gldata;

import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL45.*;
import glMath.matrices.Mat2;
import glMath.matrices.Mat3;
import glMath.matrices.Mat4;
import glMath.vectors.Vec2;
import glMath.vectors.Vec3;
import glMath.vectors.Vec4;
import renderers.RenderMode;

public class VertexArray {

	private int vaoId, stride;
	private HashMap<String, BufferObject> vbos;
	private HashMap<RenderMode, IndexBuffer> ibos;
	private RenderMode ibo;
	
	/**
	 * Constructs an empty vertex array with an associated handle on the GPU
	 */
	public VertexArray(){
		//create the vao handle on the gpu
		vaoId = glCreateVertexArrays();
		//vertex buffer mapping to different names
		vbos = new HashMap<String, BufferObject>();
		
		ibos = new HashMap<RenderMode, IndexBuffer>();
		ibo = null;
		stride = 0;
	}
	
	/**
	 * Deletes this vertex array, any externally attached buffers are not deleted
	 */
	public void delete(){
		glBindVertexArray(0);
		glDeleteVertexArrays(vaoId);
	}
	
	/**
	 * Gets the RenderMode that the vertex array currently is setup for
	 * 
	 * @return RenderMode this vertex array is currently setup for 
	 */
	public RenderMode getRenderMode(){
		return ibo;
	}
	
	/**
	 * Gets the indexing type for the index buffer currently being used by the vertex array
	 * 
	 * @return IndexType of the currently bound index buffer
	 */
	public IndexBuffer.IndexType getIndexType(){
		return ibos.get(ibo).getType();
	}
	
	/**
	 * Gets the number of indices for the currently bound index buffer
	 * 
	 * @return Number of indices in the currently bound index buffer
	 */
	public int getNumIndices(){
		return ibos.get(ibo).numElements();
	}
	
	/**
	 * Adds the given BufferObject to this VertexArray's list of BufferObjects that are usable as
	 * vertex buffers in this array
	 * 
	 * @param name String key naming this BufferObject for easy switching of vertex buffers
	 * @param buffer Finalized BufferObject compatible with this VertexArray
	 * @return True if the given BufferObject is a compatible type and was added, false otherwise
	 */
	public boolean addVertexBuffer(String name, BufferObject buffer){
		//check that the buffer type is a valid type 
		if(buffer.getType() == BufferType.ARRAY){
			vbos.put(name, buffer);
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Sets the VertexBuffer of this VertexArray to the given name of a BufferObject stored in this VertexArray
	 * 
	 * @param name Name of the BufferObject to use as the vertex buffer for this VertexArray
	 * @param bindingIndex Index to bind the vertex buffer object to on the gpu
	 * @return True if the BufferObject with the given name exists in this VertexArray
	 */
	public boolean setVertexBuffer(String name, int bindingIndex){
		//check to make sure the buffer being set exists
		if(vbos.get(name) != null){
			glVertexArrayVertexBuffer(vaoId, bindingIndex, vbos.get(name).getId(), 0, stride);
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Adds a given IndexBuffer to this VertexArray's IndexBuffers
	 * 
	 * @param mode The type of RenderMode this IndexBuffer is compatible with, this will override existing IndexBuffers
	 * in this VertexArray if one already exists with the specified RenderMode
	 * @param buffer IndexBuffer to add to this VertexArray
	 */
	public void addIndexBuffer(RenderMode mode, IndexBuffer buffer){
		ibos.put(mode, buffer);
	}
	
	/**
	 * Sets this VertexArray's IndexBuffer to the given value, this will specify the RenderMode used in renderers
	 * 
	 * @param mode RenderMode to make active for this VertexArray by binding an IndexBuffer compatible with the given RenderMode
	 * that was previously added and stored in this VertexArray
	 * @return True if there exists an IndexBuffer with the given RenderMode in this VertexArray, false otherwise
	 */
	public boolean setIndexBuffer(RenderMode mode){
		//check to make sure the buffer being set exists
		if(ibos.get(mode) != null){
			ibo = mode;
			glVertexArrayElementBuffer(vaoId, ibos.get(ibo).getId());
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Adds an attribute definition for this vertex array
	 * 
	 * @param index Specifies the index of the attribute to set
	 * @param type The glsl attribute type that will define how the attribute will behave
	 * @param normalize Indicates whether the data being sent to the attribute should be normalized
	 * @param divisor Attribute divisor that decides the frequency of updating the attribute from the vertex buffer
	 * @param bufferIndex Index of the vertex buffer binding the attribute will read from
	 */
	public void addAttrib(int index, AttribType type, boolean normalize, int divisor, int bufferIndex){

		ArrayList<AttribType> attributes = new ArrayList<AttribType>();
		//check if the attribute is a matrix or double vector greater than 2 and decompose it into simpler types so that they can be passed to the GPU
		switch (type) {
			case DVEC3:
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DOUBLE);
				break;
			case DVEC4:
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				break;
			case MAT2:
				attributes.add(AttribType.VEC2);
				attributes.add(AttribType.VEC2);
				break;
			case MAT2x3:
				attributes.add(AttribType.VEC3);
				attributes.add(AttribType.VEC3);
				break;
			case MAT2x4:
				attributes.add(AttribType.VEC4);
				attributes.add(AttribType.VEC4);
				break;

			case MAT3:
				attributes.add(AttribType.VEC3);
				attributes.add(AttribType.VEC3);
				attributes.add(AttribType.VEC3);
				break;
			case MAT3x2:
				attributes.add(AttribType.VEC2);
				attributes.add(AttribType.VEC2);
				attributes.add(AttribType.VEC2);
				break;
			case MAT3x4:
				attributes.add(AttribType.VEC3);
				attributes.add(AttribType.VEC3);
				attributes.add(AttribType.VEC3);
				break;

			case MAT4:
				attributes.add(AttribType.VEC4);
				attributes.add(AttribType.VEC4);
				attributes.add(AttribType.VEC4);
				attributes.add(AttribType.VEC4);
				break;
			case MAT4x2:
				attributes.add(AttribType.VEC2);
				attributes.add(AttribType.VEC2);
				attributes.add(AttribType.VEC2);
				attributes.add(AttribType.VEC2);
				break;
			case MAT4x3:
				attributes.add(AttribType.VEC3);
				attributes.add(AttribType.VEC3);
				attributes.add(AttribType.VEC3);
				attributes.add(AttribType.VEC3);
				break;

			case DMAT2:
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				break;
			case DMAT2x3:
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DOUBLE);
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DOUBLE);
				break;
			case DMAT2x4:
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				break;

			case DMAT3:
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DOUBLE);

				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DOUBLE);

				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DOUBLE);
				break;
			case DMAT3x2:
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				break;
			case DMAT3x4:
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				break;

			case DMAT4:
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				break;
			case DMAT4x2:
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DVEC2);
				break;
			case DMAT4x3:
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DOUBLE);
				
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DOUBLE);
				
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DOUBLE);
				
				attributes.add(AttribType.DVEC2);
				attributes.add(AttribType.DOUBLE);
				break;
			default:
				attributes.add(type);
				break;
		}
		//set the attribute data
		for(int curIndex = 0; curIndex <  attributes.size(); curIndex++){
			AttribType curType = attributes.get(curIndex);
			glVertexArrayAttribBinding(vaoId, index+curIndex, bufferIndex);
			glVertexArrayAttribFormat(vaoId, index+curIndex, curType.size, curType.type, normalize, stride);
			stride += curType.bytes;
		}
	}
	
	/**
	 * Binds this vertex array to the context
	 */
	public void bind(){
		glBindVertexArray(vaoId);
	}
	
	/**
	 * Unbinds this vertex array from the context
	 */
	public void unbind(){
		glBindVertexArray(0);
	}
	
	/**
	 * Enables a generic vertex array attribute for this vertex array object
	 * 
	 * @param attrib Index of the attribute array to enable
	 */
	public void enableAttribute(int attrib){
		glEnableVertexArrayAttrib(vaoId, attrib);
	}

	/**
	 * Disables a generic vertex array attribute for this vertex array object
	 * 
	 * @param attrib Index of the attribute array to disable
	 */
	public void disableAttribute(int attrib){
		glDisableVertexArrayAttrib(vaoId, attrib);
	}
}
