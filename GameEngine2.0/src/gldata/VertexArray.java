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

	private int vaoId, stride, highestBufferIndex;
	private HashMap<String, Integer> bufferIndices;
	private HashMap<String, BufferObject> vbos;
	private HashMap<String, IndexBuffer> ibos;
	private HashMap<String, RenderMode> renderModes;
	private String ibo;
	
	/**
	 * Constructs an empty vertex array with an associated handle on the GPU
	 */
	public VertexArray(){
		//create the vao handle on the gpu
		vaoId = glCreateVertexArrays();
		//vertex buffer mapping to different names
		vbos = new HashMap<String, BufferObject>();
		
		ibos = new HashMap<String, IndexBuffer>();
		renderModes = new HashMap<String, RenderMode>();
		ibo = null;
		stride = 0;
		
		bufferIndices = new HashMap<String, Integer>();
		highestBufferIndex = 0;
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
		return renderModes.get(ibo);
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
	 * @param name String id for the given BufferObject
	 * @param buffer BufferObject with a BufferType of Array
	 * @return True if the given BufferObject is a compatible type and was added, false otherwise
	 */
	public boolean addVertexBuffer(String name, BufferObject buffer){
		//check that the buffer type is a valid type 
		if(buffer.getType() == BufferType.ARRAY){
			vbos.put(name, buffer);
			bufferIndices.put(name, highestBufferIndex);
			highestBufferIndex++;
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Establishes the binding index for the Vertex Buffer Specified by {@code name} in this Vertex Array.
	 * This function is essential for Vertex buffers to be able to be used with the Vertex array object.
	 * 
	 * @param name Name of the Vertex buffer in this Vertex array to bind to an index in the array
	 * @return True if the name exists, false otherwise
	 */
	public boolean registerVBO(String name){
		//check to make sure the buffer being set exists
		if(vbos.get(name) != null){
			glVertexArrayVertexBuffer(vaoId, bufferIndices.get(name), vbos.get(name).getId(), 0, stride);
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Adds a given IndexBuffer to this VertexArray with the given{@code id} and RenderMode {@code mode}
	 * 
	 * @param id ID this VertexArray will use to manipulate the given IndexBuffer
	 * @param mode RenderMode the given IndexBuffer will be mapped to
	 * @param buffer IndexBuffer being added to the VertexArray
	 */
	public void addIndexBuffer(String id, RenderMode mode, IndexBuffer buffer){
		ibos.put(id, buffer);
		renderModes.put(id, mode);
	}
	
	/**
	 * Sets this VertexArray's IndexBuffer to the IndexBuffer with the given {@code id}, this will also specify 
	 * the current RenderMode for this VertexArray
	 * 
	 * @param id ID of the IndexBuffer to set as the active IndexBuffer of this VertexArray 
	 * @return True if there exists an IndexBuffer with the given id in this VertexArray, false otherwise
	 */
	public boolean setIndexBuffer(String id){
		//check to make sure the buffer being set exists
		if(ibos.get(id) != null){
			ibo = id;
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
	 */
	public void addAttrib(int index, AttribType type, boolean normalize, int divisor){
		ArrayList<AttribType> attributes = new ArrayList<AttribType>();
		type.decompose(attributes);
		//set the attribute data
		for(int curIndex = 0; curIndex <  attributes.size(); curIndex++){
			AttribType curType = attributes.get(curIndex);
			//decide what function to call based on the data type
			if(curType.isDouble()){
				glVertexArrayAttribLFormat(vaoId, index+curIndex, curType.size, curType.type, stride);
			}else if(curType.isFloat()){
				glVertexArrayAttribFormat(vaoId, index+curIndex, curType.size, curType.type, normalize, stride);
			}else{
				glVertexArrayAttribIFormat(vaoId, index+curIndex, curType.size, curType.type, stride);
			}
			stride += curType.bytes;
		}
	}
	
	/**
	 * Sets the Vertex buffer to use with the Attribute index in this array
	 * 
	 * @param attrib Index of the generic attribute array to bind
	 * @param vboName Name of the vbo in this vertex array to bind to the given attribute index
	 * @return True if the name of the vbo exists and was bound, false otherwise
	 */
	public boolean setAttribVBO(int attrib, String vboName){
		glVertexArrayAttribBinding(vaoId, attrib, bufferIndices.get(vboName));
		//check to make sure the buffer being set exists
		if(bufferIndices.get(vboName) != null){
			glVertexArrayAttribBinding(vaoId, attrib, bufferIndices.get(vboName));
			return true;
		}else{
			return false;
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
