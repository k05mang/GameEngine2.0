package gldata;

import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL45.*;

import glMath.Vec2;
import glMath.Vec3;
import glMath.Vec4;
import glMath.Mat2;
import glMath.Mat3;
import glMath.Mat4;
import renderers.RenderMode;

public class VertexArray {

	private int vaoId, stride;
	private HashMap<String, BufferObject> vbos;
	private HashMap<RenderMode, IndexBuffer> ibos;
	private BufferObject defaultVbo;
	private IndexBuffer defaultIbo;
	private StringBuilder vbo;
	private RenderMode ibo;
	private ArrayList<VertexAttrib> attributes;
	private boolean finished;
	
	/**
	 * Constructs this vertex array object with a default vertex buffer and default index buffer under the name
	 * of "default".
	 * <p>
	 * The default index buffer for this vertex array will have a RenderMode of type TRIANGLES and an IndexType of INT.
	 */
	public VertexArray(){
		this(RenderMode.TRIANGLES, IndexBuffer.IndexType.INT);
	}
	
	/**
	 * Constructs this vertex array object with a default vertex buffer and default index buffer under the name
	 * of "default".
	 * <p>
	 * The default index buffer for this vertex array will have a RenderMode specified by the given RenderMode
	 * and an IndexType of INT.
	 * 
	 * @param defaultMode Default rendering mode for the index buffer
	 */
	public VertexArray(RenderMode defaultMode){
		this(defaultMode, IndexBuffer.IndexType.INT);
	}
	
	/**
	 * Constructs this vertex array object with a default vertex buffer and default index buffer under the name
	 * of "default".
	 * <p>
	 * The default index buffer for this vertex array will have an index type specified by the given IndexType
	 * and will have a RenderMode of TIRANGLES.
	 * 
	 * @param defaultType The IndexType to set this vertex array's default index buffer to
	 */
	public VertexArray(IndexBuffer.IndexType defaultType){
		this(RenderMode.TRIANGLES, defaultType);
	}
	
	/**
	 * Constructs this vertex array object with a default vertex buffer and default index buffer under the name
	 * of "default".
	 * <p>
	 * The index buffer will have a 
	 * 
	 * @param defaultMode Default RenderMode for this VertexArray's IndexBuffer
	 * @param defaultType Default IndexType for this VertexArray's IndexBuffer 
	 */
	public VertexArray(RenderMode defaultMode, IndexBuffer.IndexType defaultType){
		vaoId = glCreateVertexArrays();
		
		vbos = new HashMap<String, BufferObject>();
		defaultVbo = new BufferObject(BufferType.ARRAY);
		//put this vertex array's default vertex buffer as "default"
		vbos.put("default", defaultVbo);
		
		ibos = new HashMap<RenderMode, IndexBuffer>();
		defaultIbo = new IndexBuffer(defaultType);
		//put this vertex array's default index buffer as "default"
		ibos.put(defaultMode, defaultIbo);
		//set the current id for what buffer to use
		vbo = new StringBuilder("default");
		ibo = defaultMode;
		attributes = new ArrayList<VertexAttrib>();
		finished = false;
	}
	
	/**
	 * Finalizes this vertex array by uploading its default buffers to the GPU and setting attribute data
	 * 
	 * @param bufferUsage GLenum determining how the vertex buffer is to be used
	 * @param ibosUsage GLenum determining how the index buffer is to be used
	 */
	public void finalize(BufferUsage bufferUsage, BufferUsage ibosUsage){
		if(!finished){
			defaultVbo.flush(bufferUsage);
			defaultIbo.flush(bufferUsage);
			stride = 0;
			for(VertexAttrib attribData : attributes){
				glVertexArrayAttribBinding(vaoId, attribData.index, 0);
				glVertexArrayAttribFormat(vaoId, attribData.index, attribData.attribute.size, attribData.attribute.type, attribData.normalize, stride);
				stride += attribData.attribute.bytes;
			}
			glVertexArrayVertexBuffer(vaoId, 0, vbos.get(vbo.toString()).getId(), 0, stride);
			glVertexArrayElementBuffer(vaoId, ibos.get(ibo).getId());
			finished = true;
		}
	}
	
	/**
	 * Deletes this vertex array and its associated buffers, other buffers passed to this vertex array are not deleted
	 */
	public void delete(){
		glBindVertexArray(0);
		glDeleteVertexArrays(vaoId);
		defaultVbo.delete();
		defaultIbo.delete();
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
	
	public boolean addVertexBuffer(String name, BufferObject buffer){
		//check to make sure the buffer being added doesn't have the name of default which is reserved
		//additionally check that the buffer type is a valid type 
		if(buffer.getType() == BufferType.ARRAY){
			vbos.put(name, buffer);
			return true;
		}else{
			return false;
		}
	}
	
	public boolean setVertexBuffer(String name){
		//check to make sure the buffer being set exists
		if(vbos.get(name) != null){
			vbo.replace(0, vbo.length(), name);
			vbo.trimToSize();
			glVertexArrayVertexBuffer(vaoId, 0, vbos.get(vbo.toString()).getId(), 0, stride);
			return true;
		}else{
			return false;
		}
	}
	
	public void addIndexBuffer(RenderMode mode, IndexBuffer buffer){
		ibos.put(mode, buffer);
	}
	
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
	 */
	public void addAttrib(int index, AttribType type, boolean normalize, int divisor){
		//check if we have finalized this vertex array
		if (!finished) {
			//check if the attribute is a matrix or double vector greater than 2 and decompose it into simpler types so that they can be passed to the GPU
			switch (type) {
				case DVEC3:
					attributes.add(new VertexAttrib(index, AttribType.DVEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+1, AttribType.DOUBLE, normalize, divisor));
					break;
				case DVEC4:
					attributes.add(new VertexAttrib(index, AttribType.DVEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+1, AttribType.DVEC2, normalize, divisor));
					break;
				case MAT2:
					attributes.add(new VertexAttrib(index, AttribType.VEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+1, AttribType.VEC2, normalize, divisor));
					break;
				case MAT2x3:
					attributes.add(new VertexAttrib(index, AttribType.VEC3, normalize, divisor));
					attributes.add(new VertexAttrib(index+1, AttribType.VEC3, normalize, divisor));
					break;
				case MAT2x4:
					attributes.add(new VertexAttrib(index, AttribType.VEC4, normalize, divisor));
					attributes.add(new VertexAttrib(index+1, AttribType.VEC4, normalize, divisor));
					break;
	
				case MAT3:
					attributes.add(new VertexAttrib(index, AttribType.VEC3, normalize, divisor));
					attributes.add(new VertexAttrib(index+1, AttribType.VEC3, normalize, divisor));
					attributes.add(new VertexAttrib(index+2, AttribType.VEC3, normalize, divisor));
					break;
				case MAT3x2:
					attributes.add(new VertexAttrib(index, AttribType.VEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+1, AttribType.VEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+2, AttribType.VEC2, normalize, divisor));
					break;
				case MAT3x4:
					attributes.add(new VertexAttrib(index, AttribType.VEC3, normalize, divisor));
					attributes.add(new VertexAttrib(index+1, AttribType.VEC3, normalize, divisor));
					attributes.add(new VertexAttrib(index+2, AttribType.VEC3, normalize, divisor));
					break;
	
				case MAT4:
					attributes.add(new VertexAttrib(index, AttribType.VEC4, normalize, divisor));
					attributes.add(new VertexAttrib(index+1, AttribType.VEC4, normalize, divisor));
					attributes.add(new VertexAttrib(index+2, AttribType.VEC4, normalize, divisor));
					attributes.add(new VertexAttrib(index+3, AttribType.VEC4, normalize, divisor));
					break;
				case MAT4x2:
					attributes.add(new VertexAttrib(index, AttribType.VEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+1, AttribType.VEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+2, AttribType.VEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+3, AttribType.VEC2, normalize, divisor));
					break;
				case MAT4x3:
					attributes.add(new VertexAttrib(index, AttribType.VEC3, normalize, divisor));
					attributes.add(new VertexAttrib(index+1, AttribType.VEC3, normalize, divisor));
					attributes.add(new VertexAttrib(index+2, AttribType.VEC3, normalize, divisor));
					attributes.add(new VertexAttrib(index+3, AttribType.VEC3, normalize, divisor));
					break;
	
				case DMAT2:
					attributes.add(new VertexAttrib(index, AttribType.DVEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+1, AttribType.DVEC2, normalize, divisor));
					break;
				case DMAT2x3:
					attributes.add(new VertexAttrib(index, AttribType.DVEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+1, AttribType.DOUBLE, normalize, divisor));
					attributes.add(new VertexAttrib(index+2, AttribType.DVEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+3, AttribType.DOUBLE, normalize, divisor));
					break;
				case DMAT2x4:
					attributes.add(new VertexAttrib(index, AttribType.DVEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+1, AttribType.DVEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+2, AttribType.DVEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+3, AttribType.DVEC2, normalize, divisor));
					break;
	
				case DMAT3:
					attributes.add(new VertexAttrib(index, AttribType.DVEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+1, AttribType.DOUBLE, normalize, divisor));

					attributes.add(new VertexAttrib(index+2, AttribType.DVEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+3, AttribType.DOUBLE, normalize, divisor));

					attributes.add(new VertexAttrib(index+4, AttribType.DVEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+5, AttribType.DOUBLE, normalize, divisor));
					break;
				case DMAT3x2:
					attributes.add(new VertexAttrib(index, AttribType.DVEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+1, AttribType.DVEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+2, AttribType.DVEC2, normalize, divisor));
					break;
				case DMAT3x4:
					attributes.add(new VertexAttrib(index, AttribType.DVEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+1, AttribType.DVEC2, normalize, divisor));
					
					attributes.add(new VertexAttrib(index+2, AttribType.DVEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+3, AttribType.DVEC2, normalize, divisor));
					
					attributes.add(new VertexAttrib(index+4, AttribType.DVEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+5, AttribType.DVEC2, normalize, divisor));
					break;
	
				case DMAT4:
					attributes.add(new VertexAttrib(index, AttribType.DVEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+1, AttribType.DVEC2, normalize, divisor));
					
					attributes.add(new VertexAttrib(index+2, AttribType.DVEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+3, AttribType.DVEC2, normalize, divisor));
					
					attributes.add(new VertexAttrib(index+4, AttribType.DVEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+5, AttribType.DVEC2, normalize, divisor));
					
					attributes.add(new VertexAttrib(index+6, AttribType.DVEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+7, AttribType.DVEC2, normalize, divisor));
					break;
				case DMAT4x2:
					attributes.add(new VertexAttrib(index, AttribType.DVEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+1, AttribType.DVEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+2, AttribType.DVEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+3, AttribType.DVEC2, normalize, divisor));
					break;
				case DMAT4x3:
					attributes.add(new VertexAttrib(index, AttribType.DVEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+1, AttribType.DOUBLE, normalize, divisor));
					
					attributes.add(new VertexAttrib(index+2, AttribType.DVEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+3, AttribType.DOUBLE, normalize, divisor));
					
					attributes.add(new VertexAttrib(index+4, AttribType.DVEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+5, AttribType.DOUBLE, normalize, divisor));
					
					attributes.add(new VertexAttrib(index+6, AttribType.DVEC2, normalize, divisor));
					attributes.add(new VertexAttrib(index+7, AttribType.DOUBLE, normalize, divisor));
					break;
				default://if it is just a basic type then add it normally
					attributes.add(new VertexAttrib(index, type, normalize, divisor));
					break;
			}
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

	/**
	 * Adds the given value to this vertex array's vertex buffer for use in the attributes
	 * 
	 * @param value Value to add to the vertex buffer associated with this vertex array
	 */
	public void add(float value){
		if(!finished){
			defaultVbo.add(value);
		}
	}

	/**
	 * Adds the given value to this vertex array's vertex buffer for use in the attributes
	 * 
	 * @param value Value to add to the vertex buffer associated with this vertex array
	 */
	public void add(double value){
		if(!finished){
			defaultVbo.add(value);
		}
	}

	/**
	 * Adds the given value to this vertex array's vertex buffer for use in the attributes
	 * 
	 * @param value Value to add to the vertex buffer associated with this vertex array
	 */
	public void add(byte value){
		if(!finished){
			defaultVbo.add(value);
		}
	}

	/**
	 * Adds the given value to this vertex array's vertex buffer for use in the attributes
	 * 
	 * @param value Value to add to the vertex buffer associated with this vertex array
	 */
	public void add(short value){
		if(!finished){
			defaultVbo.add(value);
		}
	}

	/**
	 * Adds the given value to this vertex array's vertex buffer for use in the attributes
	 * 
	 * @param value Value to add to the vertex buffer associated with this vertex array
	 */
	public void add(int value){
		if(!finished){
			defaultVbo.add(value);
		}
	}

	/**
	 * Adds the given value to this vertex array's vertex buffer for use in the attributes
	 * 
	 * @param value Value to add to the vertex buffer associated with this vertex array
	 */
	public void add(Vec2 value){
		if(!finished){
			defaultVbo.add(value);
		}
	}

	/**
	 * Adds the given value to this vertex array's vertex buffer for use in the attributes
	 * 
	 * @param value Value to add to the vertex buffer associated with this vertex array
	 */
	public void add(Vec3 value){
		if(!finished){
			defaultVbo.add(value);
		}
	}

	/**
	 * Adds the given value to this vertex array's vertex buffer for use in the attributes
	 * 
	 * @param value Value to add to the vertex buffer associated with this vertex array
	 */
	public void add(Vec4 value){
		if(!finished){
			defaultVbo.add(value);
		}
	}

	/**
	 * Adds the given value to this vertex array's vertex buffer for use in the attributes
	 * 
	 * @param value Value to add to the vertex buffer associated with this vertex array
	 */
	public void add(Mat2 value){
		if(!finished){
			defaultVbo.add(value);
		}
	}

	/**
	 * Adds the given value to this vertex array's vertex buffer for use in the attributes
	 * 
	 * @param value Value to add to the vertex buffer associated with this vertex array
	 */
	public void add(Mat3 value){
		if(!finished){
			defaultVbo.add(value);
		}
	}

	/**
	 * Adds the given value to this vertex array's vertex buffer for use in the attributes
	 * 
	 * @param value Value to add to the vertex buffer associated with this vertex array
	 */
	public void add(Mat4 value){
		if(!finished){
			defaultVbo.add(value);
		}
	}

	/**
	 * Adds the given value to this vertex array's index buffer for use in indexed rendering functions
	 * 
	 * @param value Index to add to the buffer
	 */
	public void addIndex(byte value){
		if(!finished){
			defaultIbo.add(value);
		}
	}

	/**
	 * Adds the given value to this vertex array's index buffer for use in indexed rendering functions
	 * 
	 * @param value Index to add to the buffer
	 */
	public void addIndex(short value){
		if(!finished){
			defaultIbo.add(value);
		}
	}

	/**
	 * Adds the given value to this vertex array's index buffer for use in indexed rendering functions
	 * 
	 * @param value Index to add to the buffer
	 */
	public void addIndex(int value){
		if(!finished){
			defaultIbo.add(value);
		}
	}
	

	protected class VertexAttrib {
	
		public AttribType attribute;
		public boolean normalize;
		public int index, divisor;
		
		public VertexAttrib(int index, AttribType type, boolean normalize, int divisor){
			this.attribute = type;
			this.normalize = normalize;
			this.divisor = divisor;
			this.index = index;
		}
	}
}
