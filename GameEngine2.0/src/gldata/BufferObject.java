package gldata;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL45.*;
import glMath.matrices.Mat2;
import glMath.matrices.Mat3;
import glMath.matrices.Mat4;
import glMath.matrices.Matrix;
import glMath.vectors.Vec2;
import glMath.vectors.Vec3;
import glMath.vectors.Vec4;

public class BufferObject {
	private int bufferId, size;
	private BufferType bufferType;
	private boolean finished;
	private ArrayList<Byte> data;
	
	/**
	 * Creates a buffer with the given type designating the underlying GLenum type on the GPU
	 * 
	 * @param bufferType The type of buffer this BufferObject will represent
	 */
	public BufferObject(BufferType type){
		bufferId = glCreateBuffers();
		bufferType = type;
		data = new ArrayList<Byte>();
		size = 0;
		finished = false;
	}
	
	/**
	 * Binds this buffer object to the buffer type target this object was initialized with
	 */
	public void bind(){
		glBindBuffer(bufferType.type, bufferId);
	}
	
	/**
	 * Unbinds this buffer object from the context
	 */
	public void unbind(){
		glBindBuffer(bufferType.type, 0);
	}
	
	/**
	 * Gets the size of this buffer object in bytes
	 * 
	 * @return The size of this buffer object in bytes
	 */
	public int size(){
		if(finished){
			return size;
		}else{
			return data.size();
		}
	}
	
	/**
	 * Deletes this buffer object from the GPU
	 */
	public void delete(){
		glBindBuffer(bufferType.type, 0);
		glDeleteBuffers(bufferId);
		bufferId = 0;
	}
	
	/**
	 * Unlocks this buffer object to allow for new data to be added to the underlying OpenGL buffer.
	 * When the finalize function is called the old data will be deleted from the GPU and the new
	 * data will replace it.
	 */
	public void reset(){
		finished = false;
		size = 0;
	}
	
	/**
	 * Gets the type this buffer object is associated with
	 * 
	 * @return BufferType that represents the internal GPU type of this buffer
	 */
	public BufferType getType(){
		return bufferType;
	}
	
	/**
	 * Gets the GPU id handle for this buffer object
	 * 
	 * @return The buffer id handle returned by the GPu for this buffer
	 */
	public int getId(){
		return bufferId;
	}
	/**
	 * Sends the accumulated data buffer to the GPU, and initializes the buffer with the given type and usage. This function
	 * also clears out the accumulated buffer after it has been sent to the GPU to alleviate memory consumption in the application.
	 * 
	 * @param usage GLenum defining how the buffers contents will be used on the GPU
	 */
	public void flush(BufferUsage usage){
		//TODO potentially make this function backwards compatible, for now though it will only be opengl 4.5 compliant
		//check if there is any data to buffer
		if(!data.isEmpty() && !finished){
			//create the buffer and buffer the data into it to send to the GPU
			ByteBuffer dataBuffer = BufferUtils.createByteBuffer(data.size());
			for(Byte dataByte : data){
				dataBuffer.put(dataByte);
			}
			dataBuffer.flip();//move the read pointer back to the beginning
			glNamedBufferData(bufferId, dataBuffer, usage.type);
			size = data.size();//store this buffers size
			data.clear();//clear out the data store since it has been buffered to the GPU and will no longer be used
			finished = true;
		}
	}

	/**
	 * Adds a float to this buffer
	 * 
	 * @param value Float value to add
	 */
	public void add(float value){
		if (!finished) {
			int floatAsInt = Float.floatToIntBits(value);
			data.add((byte) (floatAsInt & 0xff));
			data.add((byte) ((floatAsInt >>> 8) & 0xff));
			data.add((byte) ((floatAsInt >>> 16) & 0xff));
			data.add((byte) ((floatAsInt >>> 24) & 0xff));
		}
	}
	
	/**
	 * Adds a double to this buffer
	 * 
	 * @param value Double value to add
	 */
	public void add(double value){
		if (!finished) {
			long doubleAsLong = Double.doubleToLongBits(value);
			data.add((byte) (doubleAsLong & 0xff));
			data.add((byte) ((doubleAsLong >>> 8) & 0xff));
			data.add((byte) ((doubleAsLong >>> 16) & 0xff));
			data.add((byte) ((doubleAsLong >>> 24) & 0xff));
			data.add((byte) ((doubleAsLong >>> 32) & 0xff));
			data.add((byte) ((doubleAsLong >>> 40) & 0xff));
			data.add((byte) ((doubleAsLong >>> 48) & 0xff));
			data.add((byte) ((doubleAsLong >>> 56) & 0xff));
		}
	}
	
	/**
	 * Adds a byte to this buffer
	 * 
	 * @param value Byte value to add
	 */
	public void add(byte value){
		if (!finished) {
			data.add(value);
		}
	}

	/**
	 * Adds a short to this buffer
	 * 
	 * @param value Short value to add
	 */
	public void add(short value){
		if (!finished) {
			data.add((byte) (value & 0xff));
			data.add((byte) ((value >>> 8) & 0xff));
		}
	}

	/**
	 * Adds an integer to this buffer
	 * 
	 * @param value Integer value to add
	 */
	public void add(int value){
		if (!finished) {
			data.add((byte) (value & 0xff));
			data.add((byte) ((value >>> 8) & 0xff));
			data.add((byte) ((value >>> 16) & 0xff));
			data.add((byte) ((value >>> 24) & 0xff));
		}
	}

	/**
	 * Adds a Vec2 to this buffer
	 * 
	 * @param value Vec2 value to add
	 */
	public void add(Vec2 value){
		if (!finished) {
			this.add(value.x);
			this.add(value.y);
		}
	}

	/**
	 * Adds a Vec3 to this buffer
	 * 
	 * @param value Vec3 value to add
	 */
	public void add(Vec3 value){
		if (!finished) {
			this.add(value.x);
			this.add(value.y);
			this.add(value.z);
		}
	}

	/**
	 * Adds a Vec4 to this buffer
	 * 
	 * @param value Vec4 value to add
	 */
	public void add(Vec4 value){
		if (!finished) {
			this.add(value.x);
			this.add(value.y);
			this.add(value.z);
			this.add(value.w);
		}
	}

	/**
	 * Adds a Mat2 to this buffer
	 * 
	 * @param value Mat2 value to add
	 */
	public void add(Mat2 value){
		if (!finished) {
			Vec2[] vectors = value.getMatrix();
			this.add(vectors[0]);
			this.add(vectors[1]);
		}
	}

	/**
	 * Adds a Mat3 to this buffer
	 * 
	 * @param value Mat3 value to add
	 */
	public void add(Mat3 value){
		if (!finished) {
			Vec3[] vectors = value.getMatrix();
			this.add(vectors[0]);
			this.add(vectors[1]);
			this.add(vectors[2]);
		}
	}

	/**
	 * Adds a Mat4 to this buffer
	 * 
	 * @param value Mat4 value to add
	 */
	public void add(Mat4 value){
		if (!finished) {
			Vec4[] vectors = value.getMatrix();
			this.add(vectors[0]);
			this.add(vectors[1]);
			this.add(vectors[2]);
			this.add(vectors[3]);
		}
	}

	/**
	 * Sets a value in this buffer, if this was called after the buffer was flushed then this will update
	 * the buffer GPU side. If this was called before the buffer was flushed then the value is changed 
	 * in this buffers temp storage in this object instead of the GPU.
	 * 
	 * @param offset Byte offset from the beginning of the buffer to start modifying
	 * @param value Value to change in the buffer
	 * @throws IndexOutOfBoundsException
	 */
	public void set(int offset, float value) throws IndexOutOfBoundsException{
		
		//check if the offset and the range of the input will result in an index out of bounds
		//check by offsetting offset with a value of 1 less than the size of the variable to be added, this is because we are checking indexes
		if(offset+3 > (finished ? size : data.size())-1 || offset < 0){
			throw new IndexOutOfBoundsException("the area defined from offset through the size of the input value results in an insertion out of the buffers bounds");
		}else if(!finished){//check if this buffer has been flushed
			int floatAsInt = Float.floatToIntBits(value);
			data.set(offset, (byte)(floatAsInt & 0xff));
			data.set(offset+1, (byte)((floatAsInt >>> 8) & 0xff));
			data.set(offset+2, (byte)((floatAsInt >>> 16) & 0xff));
			data.set(offset+3, (byte)((floatAsInt >>> 24) & 0xff));
		}else{
			ByteBuffer dataBuffer = BufferUtils.createByteBuffer(4);
			dataBuffer.putFloat(value);
			dataBuffer.flip();
			glNamedBufferSubData(bufferId, offset, dataBuffer);
		}
	}
	
	/**
	 * Sets a value in this buffer, if this was called after the buffer was flushed then this will update
	 * the buffer GPU side. If this was called before the buffer was flushed then the value is changed 
	 * in this buffers temp storage in this object instead of the GPU.
	 * 
	 * @param offset Byte offset from the beginning of the buffer to start modifying
	 * @param value Value to change in the buffer
	 * @throws IndexOutOfBoundsException
	 */
	public void set(int offset, double value) throws IndexOutOfBoundsException{
		
		//check if the offset and the range of the input will result in an index out of bounds
		//check by offsetting offset with a value of 1 less than the size of the variable to be added, this is because we are checking indexes
		if(offset+7 > (finished ? size : data.size())-1 || offset < 0){
			throw new IndexOutOfBoundsException("the area defined from offset through the size of the input value results in an insertion out of the buffers bounds");
		}else if(!finished){//check if this buffer has been flushed
			long doubleAsLong = Double.doubleToLongBits(value);
			data.set(offset, (byte)(doubleAsLong & 0xff));
			data.set(offset+1, (byte)((doubleAsLong >>> 8) & 0xff));
			data.set(offset+2, (byte)((doubleAsLong >>> 16) & 0xff));
			data.set(offset+3, (byte)((doubleAsLong >>> 24) & 0xff));
			data.set(offset+4, (byte)((doubleAsLong >>> 32) & 0xff));
			data.set(offset+5, (byte)((doubleAsLong >>> 40) & 0xff));
			data.set(offset+6, (byte)((doubleAsLong >>> 48) & 0xff));
			data.set(offset+7, (byte)((doubleAsLong >>> 56) & 0xff));
		}else{
			ByteBuffer dataBuffer = BufferUtils.createByteBuffer(8);
			dataBuffer.putDouble(value);
			dataBuffer.flip();
			glNamedBufferSubData(bufferId, offset, dataBuffer);
		}
	}
	
	/**
	 * Sets a value in this buffer, if this was called after the buffer was flushed then this will update
	 * the buffer GPU side. If this was called before the buffer was flushed then the value is changed 
	 * in this buffers temp storage in this object instead of the GPU.
	 * 
	 * @param offset Byte offset from the beginning of the buffer to start modifying
	 * @param value Value to change in the buffer
	 * @throws IndexOutOfBoundsException
	 */
	public void set(int offset, byte value) throws IndexOutOfBoundsException{
		
		//check if the offset and the range of the input will result in an index out of bounds
		//check by offsetting offset with a value of 1 less than the size of the variable to be added, this is because we are checking indexes
		if(offset > (finished ? size : data.size())-1 || offset < 0){//check if this buffer has been flushed
			throw new IndexOutOfBoundsException("the area defined from offset through the size of the input value results in an insertion out of the buffers bounds");
		}else if(!finished){
			data.set(offset, value);
		}else{
			ByteBuffer dataBuffer = BufferUtils.createByteBuffer(1);
			dataBuffer.put(value);
			dataBuffer.flip();
			glNamedBufferSubData(bufferId, offset, dataBuffer);
		}
	}

	/**
	 * Sets a value in this buffer, if this was called after the buffer was flushed then this will update
	 * the buffer GPU side. If this was called before the buffer was flushed then the value is changed 
	 * in this buffers temp storage in this object instead of the GPU.
	 * 
	 * @param offset Byte offset from the beginning of the buffer to start modifying
	 * @param value Value to change in the buffer
	 * @throws IndexOutOfBoundsException
	 */
	public void set(int offset, short value) throws IndexOutOfBoundsException{
		
		//check if the offset and the range of the input will result in an index out of bounds
		//check by offsetting offset with a value of 1 less than the size of the variable to be added, this is because we are checking indexes
		if(offset+1 > (finished ? size : data.size())-1 || offset < 0){
			throw new IndexOutOfBoundsException("the area defined from offset through the size of the input value results in an insertion out of the buffers bounds");
		}else if(!finished){//check if this buffer has been flushed
			data.set(offset, (byte)(value & 0xff));
			data.set(offset+1, (byte)((value >>> 8) & 0xff));
		}else{
			ByteBuffer dataBuffer = BufferUtils.createByteBuffer(2);
			dataBuffer.putShort(value);
			dataBuffer.flip();
			glNamedBufferSubData(bufferId, offset, dataBuffer);
		}
	}

	/**
	 * Sets a value in this buffer, if this was called after the buffer was flushed then this will update
	 * the buffer GPU side. If this was called before the buffer was flushed then the value is changed 
	 * in this buffers temp storage in this object instead of the GPU.
	 * 
	 * @param offset Byte offset from the beginning of the buffer to start modifying
	 * @param value Value to change in the buffer
	 * @throws IndexOutOfBoundsException
	 */
	public void set(int offset, int value) throws IndexOutOfBoundsException{
		
		//check if the offset and the range of the input will result in an index out of bounds
		//check by offsetting offset with a value of 1 less than the size of the variable to be added, this is because we are checking indexes
		if(offset+3 > (finished ? size : data.size())-1 || offset < 0){
			throw new IndexOutOfBoundsException("the area defined from offset through the size of the input value results in an insertion out of the buffers bounds");
		}else if(!finished){//check if this buffer has been flushed
			data.set(offset, (byte)(value & 0xff));
			data.set(offset+1, (byte)((value >>> 8) & 0xff));
			data.set(offset+2, (byte)((value >>> 16) & 0xff));
			data.set(offset+3, (byte)((value >>> 24) & 0xff));
		}else{
			ByteBuffer dataBuffer = BufferUtils.createByteBuffer(4);
			dataBuffer.putInt(value);
			dataBuffer.flip();
			glNamedBufferSubData(bufferId, offset, dataBuffer);
		}
	}

	/**
	 * Sets a value in this buffer, if this was called after the buffer was flushed then this will update
	 * the buffer GPU side. If this was called before the buffer was flushed then the value is changed 
	 * in this buffers temp storage in this object instead of the GPU.
	 * 
	 * @param offset Byte offset from the beginning of the buffer to start modifying
	 * @param value Value to change in the buffer
	 * @throws IndexOutOfBoundsException
	 */
	public void set(int offset, Vec2 value) throws IndexOutOfBoundsException{
		//determine if the offset would attempt to set values out of bounds
		if(offset+Vec2.SIZE_IN_BYTES-1 > (finished ? size : data.size())-1 || offset < 0){
			throw new IndexOutOfBoundsException("the area defined from offset through the size of the input value results in an insertion out of the buffers bounds");
		}else if(!finished){//check if this buffer has been flushed
			//add the x value
			int floatAsInt = Float.floatToIntBits(value.x);
			data.set(offset, (byte)(floatAsInt & 0xff));
			data.set(offset+1, (byte)((floatAsInt >>> 8) & 0xff));
			data.set(offset+2, (byte)((floatAsInt >>> 16) & 0xff));
			data.set(offset+3, (byte)((floatAsInt >>> 24) & 0xff));
			
			//add the y value
			floatAsInt = Float.floatToIntBits(value.y);
			data.set(offset+4, (byte)(floatAsInt & 0xff));
			data.set(offset+5, (byte)((floatAsInt >>> 8) & 0xff));
			data.set(offset+6, (byte)((floatAsInt >>> 16) & 0xff));
			data.set(offset+7, (byte)((floatAsInt >>> 24) & 0xff));
		}else{
			glNamedBufferSubData(bufferId, offset, value.asByteBuffer());
		}
	}

	/**
	 * Sets a value in this buffer, if this was called after the buffer was flushed then this will update
	 * the buffer GPU side. If this was called before the buffer was flushed then the value is changed 
	 * in this buffers temp storage in this object instead of the GPU.
	 * 
	 * @param offset Byte offset from the beginning of the buffer to start modifying
	 * @param value Value to change in the buffer
	 * @throws IndexOutOfBoundsException
	 */
	public void set(int offset, Vec3 value) throws IndexOutOfBoundsException{
		//determine if the offset would attempt to set values out of bounds
		if(offset+Vec3.SIZE_IN_BYTES-1 > (finished ? size : data.size())-1 || offset < 0){
			throw new IndexOutOfBoundsException("the area defined from offset through the size of the input value results in an insertion out of the buffers bounds");
		}else if(!finished){//check if this buffer has been flushed
			//add the x value
			int floatAsInt = Float.floatToIntBits(value.x);
			data.set(offset, (byte)(floatAsInt & 0xff));
			data.set(offset+1, (byte)((floatAsInt >>> 8) & 0xff));
			data.set(offset+2, (byte)((floatAsInt >>> 16) & 0xff));
			data.set(offset+3, (byte)((floatAsInt >>> 24) & 0xff));

			//add the y value
			floatAsInt = Float.floatToIntBits(value.y);
			data.set(offset+4, (byte)(floatAsInt & 0xff));
			data.set(offset+5, (byte)((floatAsInt >>> 8) & 0xff));
			data.set(offset+6, (byte)((floatAsInt >>> 16) & 0xff));
			data.set(offset+7, (byte)((floatAsInt >>> 24) & 0xff));
			
			//add the z value
			floatAsInt = Float.floatToIntBits(value.z);
			data.set(offset+8, (byte)(floatAsInt & 0xff));
			data.set(offset+9, (byte)((floatAsInt >>> 8) & 0xff));
			data.set(offset+10, (byte)((floatAsInt >>> 16) & 0xff));
			data.set(offset+11, (byte)((floatAsInt >>> 24) & 0xff));
		}else{
			glNamedBufferSubData(bufferId, offset, value.asByteBuffer());
		}
	}

	/**
	 * Sets a value in this buffer, if this was called after the buffer was flushed then this will update
	 * the buffer GPU side. If this was called before the buffer was flushed then the value is changed 
	 * in this buffers temp storage in this object instead of the GPU.
	 * 
	 * @param offset Byte offset from the beginning of the buffer to start modifying
	 * @param value Value to change in the buffer
	 * @throws IndexOutOfBoundsException
	 */
	public void set(int offset, Vec4 value) throws IndexOutOfBoundsException{
		//determine if the offset would attempt to set values out of bounds
		if(offset+Vec4.SIZE_IN_BYTES-1 > (finished ? size : data.size())-1 || offset < 0){
			throw new IndexOutOfBoundsException("the area defined from offset through the size of the input value results in an insertion out of the buffers bounds");
		}else if(!finished){//check if this buffer has been flushed
			//add the x value
			int floatAsInt = Float.floatToIntBits(value.x);
			data.set(offset, (byte)(floatAsInt & 0xff));
			data.set(offset+1, (byte)((floatAsInt >>> 8) & 0xff));
			data.set(offset+2, (byte)((floatAsInt >>> 16) & 0xff));
			data.set(offset+3, (byte)((floatAsInt >>> 24) & 0xff));

			//add the y value
			floatAsInt = Float.floatToIntBits(value.y);
			data.set(offset+4, (byte)(floatAsInt & 0xff));
			data.set(offset+5, (byte)((floatAsInt >>> 8) & 0xff));
			data.set(offset+6, (byte)((floatAsInt >>> 16) & 0xff));
			data.set(offset+7, (byte)((floatAsInt >>> 24) & 0xff));
			
			//add the z value
			floatAsInt = Float.floatToIntBits(value.z);
			data.set(offset+8, (byte)(floatAsInt & 0xff));
			data.set(offset+9, (byte)((floatAsInt >>> 8) & 0xff));
			data.set(offset+10, (byte)((floatAsInt >>> 16) & 0xff));
			data.set(offset+11, (byte)((floatAsInt >>> 24) & 0xff));

			//add the w value
			floatAsInt = Float.floatToIntBits(value.w);
			data.set(offset+12, (byte)(floatAsInt & 0xff));
			data.set(offset+13, (byte)((floatAsInt >>> 8) & 0xff));
			data.set(offset+14, (byte)((floatAsInt >>> 16) & 0xff));
			data.set(offset+15, (byte)((floatAsInt >>> 24) & 0xff));
		}else{
			glNamedBufferSubData(bufferId, offset, value.asByteBuffer());
		}
	}

	/**
	 * Sets a value in this buffer, if this was called after the buffer was flushed then this will update
	 * the buffer GPU side. If this was called before the buffer was flushed then the value is changed 
	 * in this buffers temp storage in this object instead of the GPU.
	 * 
	 * @param offset Byte offset from the beginning of the buffer to start modifying
	 * @param value Value to change in the buffer
	 * @throws IndexOutOfBoundsException
	 */
	public void set(int offset, Matrix value) throws IndexOutOfBoundsException{
		//determine if the offset would attempt to set values out of bounds
		int size_in_bytes = 0, size_in_floats = 0;
		//determine what type the matrix is to decide the controlling variable for the loop below and the index check
		if(value instanceof Mat2){
			size_in_bytes = Mat2.SIZE_IN_BYTES;
			size_in_floats = Mat2.SIZE_IN_FLOATS;
		}else if(value instanceof Mat3){
			size_in_bytes = Mat3.SIZE_IN_BYTES;
			size_in_floats = Mat3.SIZE_IN_FLOATS;
		}else{
			size_in_bytes = Mat4.SIZE_IN_BYTES;
			size_in_floats = Mat4.SIZE_IN_FLOATS;
		}
		//check index out of bounds
		if(offset+size_in_bytes-1 > (finished ? size : data.size())-1 || offset < 0){
			throw new IndexOutOfBoundsException("the area defined from offset through the size of the input value results in an insertion out of the buffers bounds");
		}else if(!finished){//check if this buffer has been flushed
			//add the x value
			int floatAsInt = 0;
			int byteOffset = 0;
			for(int curFloat = 0; curFloat < size_in_floats; curFloat++){
				floatAsInt = Float.floatToIntBits(value.valueAt(curFloat));
				byteOffset = curFloat*4;
				data.set(offset+byteOffset, (byte)(floatAsInt & 0xff));
				data.set(offset+byteOffset+1, (byte)((floatAsInt >>> 8) & 0xff));
				data.set(offset+byteOffset+2, (byte)((floatAsInt >>> 16) & 0xff));
				data.set(offset+byteOffset+3, (byte)((floatAsInt >>> 24) & 0xff));
			}
		}else{
			glNamedBufferSubData(bufferId, offset, value.asByteBuffer());
		}
	}
	
	/**
	 * Sets values in this BufferObject to the given values from the starting {@code offset}.
	 * 
	 * @param offset Offset in bytes from the start of the buffer array to start modifying
	 * @param values Values to set the buffer to from the starting offset
	 * @throws IndexOutOfBoundsException
	 */
	public void set(int offset, byte[] values) throws IndexOutOfBoundsException{
		//determine if the offset would attempt to set values out of bounds
		if(offset+values.length-1 > (finished ? size : data.size())-1 || offset < 0){
			throw new IndexOutOfBoundsException("the area defined from offset through the size of the input values results in an insertion out of the buffers bounds");
		}else if(!finished){//check if this buffer has been flushed
			for(int curValue = 0; curValue < values.length; curValue++){
				data.set(offset+curValue, values[curValue]);
			}
		}else{
			ByteBuffer dataBuffer = BufferUtils.createByteBuffer(values.length);
			for(byte value : values){
				dataBuffer.put(value);
			}
			dataBuffer.flip();
			glNamedBufferSubData(bufferId, offset, dataBuffer);
		}
	}

	/**
	 * Sets values in this BufferObject to the given values from the starting {@code offset}.
	 * 
	 * @param offset Offset in bytes from the start of the buffer array to start modifying
	 * @param values Values to set the buffer to from the starting offset
	 * @throws IndexOutOfBoundsException
	 */
	public void set(int offset, short[] values) throws IndexOutOfBoundsException{
		//determine if the offset would attempt to set values out of bounds
		if(offset+(values.length << 1)-1 > (finished ? size : data.size())-1 || offset < 0){
			throw new IndexOutOfBoundsException("the area defined from offset through the size of the input values results in an insertion out of the buffers bounds");
		}else if(!finished){//check if this buffer has been flushed
			for(int curValue = 0; curValue < values.length; curValue++){
				data.set(offset+(curValue << 1), (byte)(values[curValue] & 0xff));
				data.set(offset+(curValue << 1)+1, (byte)((values[curValue] >>> 8) & 0xff));
			}
		}else{
			ShortBuffer dataBuffer = BufferUtils.createShortBuffer(values.length);
			for(short value : values){
				dataBuffer.put(value);
			}
			dataBuffer.flip();
			glNamedBufferSubData(bufferId, offset, dataBuffer);
		}
	}

	/**
	 * Sets values in this BufferObject to the given values from the starting {@code offset}.
	 * 
	 * @param offset Offset in bytes from the start of the buffer array to start modifying
	 * @param values Values to set the buffer to from the starting offset
	 * @throws IndexOutOfBoundsException
	 */
	public void set(int offset, int[] values) throws IndexOutOfBoundsException{
		//determine if the offset would attempt to set values out of bounds
		if(offset+(values.length << 2)-1 > (finished ? size : data.size())-1 || offset < 0){
			throw new IndexOutOfBoundsException("the area defined from offset through the size of the input values results in an insertion out of the buffers bounds");
		}else if(!finished){//check if this buffer has been flushed
			for(int curValue = 0; curValue < values.length; curValue++){
				data.set(offset+(curValue << 2), (byte)(values[curValue] & 0xff));
				data.set(offset+(curValue << 2)+1, (byte)((values[curValue] >>> 8) & 0xff));
				data.set(offset+(curValue << 2)+2, (byte)((values[curValue] >>> 16) & 0xff));
				data.set(offset+(curValue << 2)+3, (byte)((values[curValue] >>> 24) & 0xff));
			}
		}else{
			IntBuffer dataBuffer = BufferUtils.createIntBuffer(values.length);
			for(int value : values){
				dataBuffer.put(value);
			}
			dataBuffer.flip();
			glNamedBufferSubData(bufferId, offset, dataBuffer);
		}
	}

	/**
	 * Sets values in this BufferObject to the given values from the starting {@code offset}.
	 * 
	 * @param offset Offset in bytes from the start of the buffer array to start modifying
	 * @param values Values to set the buffer to from the starting offset
	 * @throws IndexOutOfBoundsException
	 */
	public void set(int offset, float[] values) throws IndexOutOfBoundsException{
		//determine if the offset would attempt to set values out of bounds
		if(offset+(values.length << 2)-1 > (finished ? size : data.size())-1 || offset < 0){
			throw new IndexOutOfBoundsException("the area defined from offset through the size of the input values results in an insertion out of the buffers bounds");
		}else if(!finished){//check if this buffer has been flushed
			int floatAsInt = 0;
			for(int curValue = 0; curValue < values.length; curValue++){
				floatAsInt = Float.floatToIntBits(values[curValue]);
				data.set(offset+(curValue << 2), (byte)(floatAsInt & 0xff));
				data.set(offset+(curValue << 2)+1, (byte)((floatAsInt >>> 8) & 0xff));
				data.set(offset+(curValue << 2)+2, (byte)((floatAsInt >>> 16) & 0xff));
				data.set(offset+(curValue << 2)+3, (byte)((floatAsInt >>> 24) & 0xff));
			}
		}else{
			FloatBuffer dataBuffer = BufferUtils.createFloatBuffer(values.length);
			for(float value : values){
				dataBuffer.put(value);
			}
			dataBuffer.flip();
			glNamedBufferSubData(bufferId, offset, dataBuffer);
		}
	}

	/**
	 * Sets values in this BufferObject to the given values from the starting {@code offset}.
	 * 
	 * @param offset Offset in bytes from the start of the buffer array to start modifying
	 * @param values Values to set the buffer to from the starting offset
	 * @throws IndexOutOfBoundsException
	 */
	public void set(int offset, double[] values) throws IndexOutOfBoundsException{
		//determine if the offset would attempt to set values out of bounds
		if(offset+(values.length << 3)-1 > (finished ? size : data.size())-1 || offset < 0){
			throw new IndexOutOfBoundsException("the area defined from offset through the size of the input values results in an insertion out of the buffers bounds");
		}else if(!finished){//check if this buffer has been flushed
			long doubleAsLong = 0;
			for(int curValue = 0; curValue < values.length; curValue++){
				doubleAsLong = Double.doubleToLongBits(values[curValue]);
				data.set(offset+(curValue << 3), (byte)(doubleAsLong & 0xff));
				data.set(offset+(curValue << 3)+1, (byte)((doubleAsLong >>> 8) & 0xff));
				data.set(offset+(curValue << 3)+2, (byte)((doubleAsLong >>> 16) & 0xff));
				data.set(offset+(curValue << 3)+3, (byte)((doubleAsLong >>> 24) & 0xff));
				data.set(offset+(curValue << 3)+4, (byte)((doubleAsLong >>> 32) & 0xff));
				data.set(offset+(curValue << 3)+5, (byte)((doubleAsLong >>> 40) & 0xff));
				data.set(offset+(curValue << 3)+6, (byte)((doubleAsLong >>> 48) & 0xff));
				data.set(offset+(curValue << 3)+7, (byte)((doubleAsLong >>> 56) & 0xff));
			}
		}else{
			DoubleBuffer dataBuffer = BufferUtils.createDoubleBuffer(values.length);
			for(double value : values){
				dataBuffer.put(value);
			}
			dataBuffer.flip();
			glNamedBufferSubData(bufferId, offset, dataBuffer);
		}
	}

	/**
	 * Sets values in this BufferObject to the given values from the starting {@code offset}.
	 * 
	 * @param offset Offset in bytes from the start of the buffer array to start modifying
	 * @param values Values to set the buffer to from the starting offset
	 * @throws IndexOutOfBoundsException
	 */
	public void set(int offset, Vec2[] values){
		//determine if the offset would attempt to set values out of bounds
		if(offset+(values.length << 2)-1 > (finished ? size : data.size())-1 || offset < 0){
			throw new IndexOutOfBoundsException("the area defined from offset through the size of the input values results in an insertion out of the buffers bounds");
		}else if(!finished){//check if this buffer has been flushed
			int floatAsInt = 0;
			for(int curValue = 0; curValue < values.length; curValue++){
				floatAsInt = Float.floatToIntBits(values[curValue].x);
				data.set(offset+(curValue << 2), (byte)(floatAsInt & 0xff));
				data.set(offset+(curValue << 2)+1, (byte)((floatAsInt >>> 8) & 0xff));
				data.set(offset+(curValue << 2)+2, (byte)((floatAsInt >>> 16) & 0xff));
				data.set(offset+(curValue << 2)+3, (byte)((floatAsInt >>> 24) & 0xff));

				//add the y value
				floatAsInt = Float.floatToIntBits(values[curValue].y);
				data.set(offset+(curValue << 2)+4, (byte)(floatAsInt & 0xff));
				data.set(offset+(curValue << 2)+5, (byte)((floatAsInt >>> 8) & 0xff));
				data.set(offset+(curValue << 2)+6, (byte)((floatAsInt >>> 16) & 0xff));
				data.set(offset+(curValue << 2)+7, (byte)((floatAsInt >>> 24) & 0xff));
			}
		}else{
			FloatBuffer dataBuffer = BufferUtils.createFloatBuffer(values.length << 1);
			for(Vec2 value : values){
				dataBuffer.put(value.x);
				dataBuffer.put(value.y);
			}
			dataBuffer.flip();
			glNamedBufferSubData(bufferId, offset, dataBuffer);
		}
	}

	/**
	 * Sets values in this BufferObject to the given values from the starting {@code offset}.
	 * 
	 * @param offset Offset in bytes from the start of the buffer array to start modifying
	 * @param values Values to set the buffer to from the starting offset
	 * @throws IndexOutOfBoundsException
	 */
	public void set(int offset, Vec3[] values){
		//determine if the offset would attempt to set values out of bounds
		if(offset+values.length*12-1 > (finished ? size : data.size())-1 || offset < 0){
			throw new IndexOutOfBoundsException("the area defined from offset through the size of the input values results in an insertion out of the buffers bounds");
		}else if(!finished){//check if this buffer has been flushed
			int floatAsInt = 0;
			for(int curValue = 0; curValue < values.length; curValue++){
				floatAsInt = Float.floatToIntBits(values[curValue].x);
				data.set(offset+curValue*12, (byte)(floatAsInt & 0xff));
				data.set(offset+curValue*12+1, (byte)((floatAsInt >>> 8) & 0xff));
				data.set(offset+curValue*12+2, (byte)((floatAsInt >>> 16) & 0xff));
				data.set(offset+curValue*12+3, (byte)((floatAsInt >>> 24) & 0xff));

				//add the y value
				floatAsInt = Float.floatToIntBits(values[curValue].y);
				data.set(offset+curValue*12+4, (byte)(floatAsInt & 0xff));
				data.set(offset+curValue*12+5, (byte)((floatAsInt >>> 8) & 0xff));
				data.set(offset+curValue*12+6, (byte)((floatAsInt >>> 16) & 0xff));
				data.set(offset+curValue*12+7, (byte)((floatAsInt >>> 24) & 0xff));
				
				//add the z value
				floatAsInt = Float.floatToIntBits(values[curValue].z);
				data.set(offset+curValue*12+8, (byte)(floatAsInt & 0xff));
				data.set(offset+curValue*12+9, (byte)((floatAsInt >>> 8) & 0xff));
				data.set(offset+curValue*12+10, (byte)((floatAsInt >>> 16) & 0xff));
				data.set(offset+curValue*12+11, (byte)((floatAsInt >>> 24) & 0xff));
			}
		}else{
			FloatBuffer dataBuffer = BufferUtils.createFloatBuffer(values.length*3);
			for(Vec3 value : values){
				dataBuffer.put(value.x);
				dataBuffer.put(value.y);
				dataBuffer.put(value.z);
			}
			dataBuffer.flip();
			glNamedBufferSubData(bufferId, offset, dataBuffer);
		}
	}

	/**
	 * Sets values in this BufferObject to the given values from the starting {@code offset}.
	 * 
	 * @param offset Offset in bytes from the start of the buffer array to start modifying
	 * @param values Values to set the buffer to from the starting offset
	 * @throws IndexOutOfBoundsException
	 */
	public void set(int offset, Vec4[] values){
		if(offset+(values.length << 4)-1 > (finished ? size : data.size())-1 || offset < 0){
			throw new IndexOutOfBoundsException("the area defined from offset through the size of the input values results in an insertion out of the buffers bounds");
		}else if(!finished){//check if this buffer has been flushed
			int floatAsInt = 0;
			for(int curValue = 0; curValue < values.length; curValue++){
				floatAsInt = Float.floatToIntBits(values[curValue].x);
				data.set(offset+(curValue << 4), (byte)(floatAsInt & 0xff));
				data.set(offset+(curValue << 4)+1, (byte)((floatAsInt >>> 8) & 0xff));
				data.set(offset+(curValue << 4)+2, (byte)((floatAsInt >>> 16) & 0xff));
				data.set(offset+(curValue << 4)+3, (byte)((floatAsInt >>> 24) & 0xff));

				//add the y value
				floatAsInt = Float.floatToIntBits(values[curValue].y);
				data.set(offset+(curValue << 4)+4, (byte)(floatAsInt & 0xff));
				data.set(offset+(curValue << 4)+5, (byte)((floatAsInt >>> 8) & 0xff));
				data.set(offset+(curValue << 4)+6, (byte)((floatAsInt >>> 16) & 0xff));
				data.set(offset+(curValue << 4)+7, (byte)((floatAsInt >>> 24) & 0xff));
				
				//add the z value
				floatAsInt = Float.floatToIntBits(values[curValue].z);
				data.set(offset+(curValue << 4)+8, (byte)(floatAsInt & 0xff));
				data.set(offset+(curValue << 4)+9, (byte)((floatAsInt >>> 8) & 0xff));
				data.set(offset+(curValue << 4)+10, (byte)((floatAsInt >>> 16) & 0xff));
				data.set(offset+(curValue << 4)+11, (byte)((floatAsInt >>> 24) & 0xff));
//
//				//add the w value
				floatAsInt = Float.floatToIntBits(values[curValue].w);
				data.set(offset+(curValue << 4)+12, (byte)(floatAsInt & 0xff));
				data.set(offset+(curValue << 4)+13, (byte)((floatAsInt >>> 8) & 0xff));
				data.set(offset+(curValue << 4)+14, (byte)((floatAsInt >>> 16) & 0xff));
				data.set(offset+(curValue << 4)+15, (byte)((floatAsInt >>> 24) & 0xff));
			}
		}else{
			FloatBuffer dataBuffer = BufferUtils.createFloatBuffer(values.length << 2);
			for(Vec4 value : values){
				dataBuffer.put(value.x);
				dataBuffer.put(value.y);
				dataBuffer.put(value.z);
				dataBuffer.put(value.w);
			}
			dataBuffer.flip();
			glNamedBufferSubData(bufferId, offset, dataBuffer);
		}
	}

	/**
	 * Sets values in this BufferObject to the given values from the starting {@code offset}.
	 * 
	 * @param offset Offset in bytes from the start of the buffer array to start modifying
	 * @param values Values to set the buffer to from the starting offset
	 * @throws IndexOutOfBoundsException
	 */
	public void set(int offset, Matrix[] values){
		int size_in_bytes = 0, size_in_floats = 0;
		//determine what type the matrix is to decide the controlling variable for the loop below and the index check
		if(values instanceof Mat2[]){
			size_in_bytes = Mat2.SIZE_IN_BYTES;
			size_in_floats = Mat2.SIZE_IN_FLOATS;
		}else if(values instanceof Mat3[]){
			size_in_bytes = Mat3.SIZE_IN_BYTES;
			size_in_floats = Mat3.SIZE_IN_FLOATS;
		}else{
			size_in_bytes = Mat4.SIZE_IN_BYTES;
			size_in_floats = Mat4.SIZE_IN_FLOATS;
		}
		//check index out of bounds
		if(offset+size_in_bytes*values.length-1 > (finished ? size : data.size())-1 || offset < 0){
			throw new IndexOutOfBoundsException("the area defined from offset through the size of the input value results in an insertion out of the buffers bounds");
		}else if(!finished){//check if this buffer has been flushed
			//add the x value
			int floatAsInt = 0;
			int byteOffset = 0;
			Matrix value;
			for(int curValue = 0; curValue < values.length; curValue++){
				for(int curFloat = 0; curFloat < size_in_floats; curFloat++){
					value = values[curValue];
					floatAsInt = Float.floatToIntBits(value.valueAt(curFloat));
					byteOffset = curValue*size_in_bytes+curFloat << 2;
					data.set(offset+byteOffset, (byte)(floatAsInt & 0xff));
					data.set(offset+byteOffset+1, (byte)((floatAsInt >>> 8) & 0xff));
					data.set(offset+byteOffset+2, (byte)((floatAsInt >>> 16) & 0xff));
					data.set(offset+byteOffset+3, (byte)((floatAsInt >>> 24) & 0xff));
				}
			}
		}else{
			FloatBuffer dataBuffer = BufferUtils.createFloatBuffer(values.length*size_in_floats);
			for(Matrix value : values){
				for(int curFloat = 0; curFloat < size_in_floats; curFloat++){
					dataBuffer.put(value.valueAt(curFloat));
				}
			}
			dataBuffer.flip();
			glNamedBufferSubData(bufferId, offset, dataBuffer);
		}
	}
}
