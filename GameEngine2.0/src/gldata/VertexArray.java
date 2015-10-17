package gldata;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL45.*;
import glMath.Vec2;
import glMath.Vec3;
import glMath.Vec4;
import glMath.Mat2;
import glMath.Mat3;
import glMath.Mat4;

public class VertexArray {

	private int vaoId;
	private BufferObject vertexBuffer, indices;
	private ArrayList<VertexAttrib> attributes;
	private boolean finished;
	
	public VertexArray(){
		vaoId = glGenVertexArrays();
		vertexBuffer = new BufferObject(GL_ARRAY_BUFFER);
		indices = new BufferObject(GL_ELEMENT_ARRAY_BUFFER);
		attributes = new ArrayList<VertexAttrib>();
		finished = false;
	}
	
	/**
	 * Finalizes this vertex array by uploading buffers to the GPU and setting attribute data
	 * 
	 * @param bufferUsage GLenum determining how the vertex buffer is to be used
	 * @param indicesUsage GLenum determining how the index buffer is to be used
	 */
	public void finalize(int bufferUsage, int indicesUsage){
		if(!finished){
			vertexBuffer.flush(bufferUsage);
			indices.flush(indicesUsage);
			int stride = 0;
			for(VertexAttrib attribData : attributes){
				glVertexArrayAttribBinding(vaoId, attribData.index, 0);
				glVertexArrayAttribFormat(vaoId, attribData.index, attribData.attribute.size, attribData.attribute.type, attribData.normalize, stride);
				stride += attribData.attribute.bytes;
			}
			glVertexArrayVertexBuffer(vaoId, 0, vertexBuffer.getId(), 0, stride);
			glVertexArrayElementBuffer(vaoId, indices.getId());
			finished = true;
		}
	}
	
	/**
	 * Deletes this vertex array and its associated buffers
	 */
	public void delete(){
		glDeleteVertexArrays(vaoId);
		vertexBuffer.delete();
		indices.delete();
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
			vertexBuffer.add(value);
		}
	}

	/**
	 * Adds the given value to this vertex array's vertex buffer for use in the attributes
	 * 
	 * @param value Value to add to the vertex buffer associated with this vertex array
	 */
	public void add(double value){
		if(!finished){
			vertexBuffer.add(value);
		}
	}

	/**
	 * Adds the given value to this vertex array's vertex buffer for use in the attributes
	 * 
	 * @param value Value to add to the vertex buffer associated with this vertex array
	 */
	public void add(byte value){
		if(!finished){
			vertexBuffer.add(value);
		}
	}

	/**
	 * Adds the given value to this vertex array's vertex buffer for use in the attributes
	 * 
	 * @param value Value to add to the vertex buffer associated with this vertex array
	 */
	public void add(short value){
		if(!finished){
			vertexBuffer.add(value);
		}
	}

	/**
	 * Adds the given value to this vertex array's vertex buffer for use in the attributes
	 * 
	 * @param value Value to add to the vertex buffer associated with this vertex array
	 */
	public void add(int value){
		if(!finished){
			vertexBuffer.add(value);
		}
	}

	/**
	 * Adds the given value to this vertex array's vertex buffer for use in the attributes
	 * 
	 * @param value Value to add to the vertex buffer associated with this vertex array
	 */
	public void add(Vec2 value){
		if(!finished){
			vertexBuffer.add(value);
		}
	}

	/**
	 * Adds the given value to this vertex array's vertex buffer for use in the attributes
	 * 
	 * @param value Value to add to the vertex buffer associated with this vertex array
	 */
	public void add(Vec3 value){
		if(!finished){
			vertexBuffer.add(value);
		}
	}

	/**
	 * Adds the given value to this vertex array's vertex buffer for use in the attributes
	 * 
	 * @param value Value to add to the vertex buffer associated with this vertex array
	 */
	public void add(Vec4 value){
		if(!finished){
			vertexBuffer.add(value);
		}
	}

	/**
	 * Adds the given value to this vertex array's vertex buffer for use in the attributes
	 * 
	 * @param value Value to add to the vertex buffer associated with this vertex array
	 */
	public void add(Mat2 value){
		if(!finished){
			vertexBuffer.add(value);
		}
	}

	/**
	 * Adds the given value to this vertex array's vertex buffer for use in the attributes
	 * 
	 * @param value Value to add to the vertex buffer associated with this vertex array
	 */
	public void add(Mat3 value){
		if(!finished){
			vertexBuffer.add(value);
		}
	}

	/**
	 * Adds the given value to this vertex array's vertex buffer for use in the attributes
	 * 
	 * @param value Value to add to the vertex buffer associated with this vertex array
	 */
	public void add(Mat4 value){
		if(!finished){
			vertexBuffer.add(value);
		}
	}

	/**
	 * Adds the given value to this vertex array's index buffer for use in indexed rendering functions
	 * 
	 * @param value Index to add to the buffer
	 */
	public void addIndex(int value){
		if(!finished){
			indices.add(value);
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
