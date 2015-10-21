package gldata;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL11.*;

import renderers.RenderMode;

public class IndexBuffer{
	private RenderMode renderMode;
	private IndexType type;
	private BufferObject buffer;
	
	/**
	 * Constructs an index buffer with the specified rendering mode and the specified data type
	 * 
	 * @param mode Mode to render this index buffer as with opengl rendering calls
	 * @param type Type to store the buffers data as for calls to opengl rendering calls
	 */
	public IndexBuffer(RenderMode mode, IndexType type){
		renderMode = mode;
		this.type = type;
		buffer = new BufferObject(BufferType.ELEMENT_ARRAY);
	}
	
	/**
	 * Binds this index buffer to the opengl context under the target GL_ELEMENT_ARRAY
	 */
	public void bind(){
		buffer.bind();
	}
	
	/**
	 * Unbinds this index buffer from the opengl context
	 */
	public void unbind(){
		buffer.unbind();
	}
	
	/**
	 * Gets the size of this index buffer in bytes
	 * 
	 * @return The number of bytes contained in this index buffer
	 */
	public int size(){
		return buffer.size();
	}
	
	/**
	 * Gets the number of elements this index buffer holds
	 * 
	 * @return The number of elements contained in this index buffer
	 */
	public int numElements(){
		switch(type){
			case BYTE:
				return buffer.size();
			case SHORT:
				return buffer.size()/2;
			case INT:
				return buffer.size()/4;
		}
		return 0;
	}
	
	/**
	 * Deletes this index buffer's internal buffer storage from the GPU
	 */
	public void delete(){
		buffer.delete();
	}
	
	/**
	 * Uploads this index buffer's internal buffer to the GPu for use in rendering calls
	 * 
	 * @param usage How the buffer is to be used on the GPU
	 */
	public void flush(BufferUsage usage){
		buffer.flush(usage);
	}
	
	/**
	 * Resets this index buffer to start taking new data from the add functions, additionally this method resets index
	 * data type for this buffer
	 * 
	 * @param type New IndexType for this buffer to retain data as
	 */
	public void reset(IndexType type){
		this.type = type;
		buffer.reset();
	}
	
	/**
	 * Gets the buffer id this index buffer uses to interact it's data with the GPU
	 * 
	 * @return This index buffer's id handle from the GPU
	 */
	public int getId(){
		return buffer.getId();
	}
	
	/**
	 * Gets the type of data this index buffer works with, this could be a type of byte, short, or int
	 * 
	 * @return This buffer's IndexType containing the GLenum value indicating the type
	 */
	public IndexType getType(){
		return type;
	}
	
	/**
	 * Gets the rendering mode for this buffer
	 * 
	 * @return 
	 */
	public RenderMode getRenderMode(){
		return renderMode;
	}
	
	/**
	 * Sets the rendering mode for this buffer
	 * 
	 * @param mode RenderMode enum specifying how this buffer should be rendered
	 */
	public void setRenderMode(RenderMode mode){
		renderMode = mode;
	}
	

	/**
	 * Adds a new value to this index buffer, if the buffer has already been flushed then this value is ignored until the buffer
	 * is reset. Additionally if the value passed here is different from the type specified for this buffer the value is
	 * converted to the proper type
	 * 
	 * @param value Value to add
	 */
	public void add(byte value){
		switch(type){
			case BYTE:
				buffer.add(value);
				break;
			case SHORT:
				buffer.add((short)value);
				break;
			case INT:
				buffer.add((int)value);
				break;
		}
	}

	/**
	 * Adds a new value to this index buffer, if the buffer has already been flushed then this value is ignored until the buffer
	 * is reset. Additionally if the value passed here is different from the type specified for this buffer the value is
	 * converted to the proper type
	 * 
	 * @param value Value to add
	 */
	public void add(short value){
		switch(type){
			case BYTE:
				buffer.add((byte)value);
				break;
			case SHORT:
				buffer.add(value);
				break;
			case INT:
				buffer.add((int)value);
				break;
		}
	}
	
	/**
	 * Adds a new value to this index buffer, if the buffer has already been flushed then this value is ignored until the buffer
	 * is reset. Additionally if the value passed here is different from the type specified for this buffer the value is
	 * converted to the proper type
	 * 
	 * @param value Value to add
	 */
	public void add(int value){
		switch(type){
			case BYTE:
				buffer.add((byte)value);
				break;
			case SHORT:
				buffer.add((short)value);
				break;
			case INT:
				buffer.add(value);
				break;
		}
	}

	/**
	 * Sets the value of this buffer at the specified index, if the this type does not match the type specified for this buffer
	 * the value is converted to the proper type
	 * 
	 * @param offset Index of the element to change, NOT in machine units
	 * @param value Value to set the index to of this buffer
	 * @throws IndexOutOfBoundsException
	 */
	public void set(int offset, byte value) throws IndexOutOfBoundsException{
		switch(type){
			case BYTE:
				buffer.set(offset, value);
				break;
			case SHORT:
				buffer.set(offset << 1, (short)value);
				break;
			case INT:
				buffer.set(offset << 2, (int)value);
				break;
		}
	}

	/**
	 * Sets the value of this buffer at the specified index, if the this type does not match the type specified for this buffer
	 * the value is converted to the proper type
	 * 
	 * @param offset Index of the element to change, NOT in machine units
	 * @param value Value to set the index to of this buffer
	 * @throws IndexOutOfBoundsException
	 */
	public void set(int offset, short value) throws IndexOutOfBoundsException{
		switch(type){
			case BYTE:
				buffer.set(offset, (byte)value);
				break;
			case SHORT:
				buffer.set(offset << 1, value);
				break;
			case INT:
				buffer.set(offset << 2, (int)value);
				break;
		}
	}
	
	/**
	 * Sets the value of this buffer at the specified index, if the this type does not match the type specified for this buffer
	 * the value is converted to the proper type
	 * 
	 * @param offset Index of the element to change, NOT in machine units
	 * @param value Value to set the index to of this buffer
	 * @throws IndexOutOfBoundsException
	 */
	public void set(int offset, int value) throws IndexOutOfBoundsException{
		switch(type){
			case BYTE:
				buffer.set(offset, (byte)value);
				break;
			case SHORT:
				buffer.set(offset << 1, (short)value);
				break;
			case INT:
				buffer.set(offset << 2, value);
				break;
		}
	}
	
	
	public enum IndexType {
		BYTE(GL_UNSIGNED_BYTE), 
		SHORT(GL_UNSIGNED_SHORT), 
		INT(GL_UNSIGNED_INT);
		
		public final int enumType;
		
		private IndexType(int type){
			enumType = type;
		}
	}
}
