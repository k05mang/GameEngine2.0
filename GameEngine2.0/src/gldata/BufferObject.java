package gldata;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL45.*;

import glMath.Vec2;
import glMath.Vec3;
import glMath.Vec4;

import glMath.Mat2;
import glMath.Mat3;
import glMath.Mat4;

public class BufferObject {
	private int bufferId, size, type;
	private boolean finished;
	private ArrayList<Byte> data;
	
	/**
	 * Creates this buffer with the given type designating this buffers type on the GPU
	 * 
	 * @param bufferType The type of buffer this BufferObject will represent
	 */
	public BufferObject(int bufferType){
		bufferId = glGenBuffers();
		type = bufferType;
		data = new ArrayList<Byte>();
		size = 0;
		finished = false;
	}
	
	/**
	 * Binds this buffer object to the buffer type target this object was initialized with
	 */
	public void bind(){
		glBindBuffer(type, bufferId);
	}
	
	/**
	 * Unbinds this buffer object from the context
	 */
	public void unbind(){
		glBindBuffer(type, 0);
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
	 * @return GLenum representing the type this buffer is used with
	 */
	public int getType(){
		return type;
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
	public void flush(int usage){
		//TODO potentially make this function backwards compatible, for now though it will only be opengl 4.5 compliant
		//check if there is any data to buffer
		if(!data.isEmpty() && !finished){
			//craete the buffer and buffer the data into it to send to the GPU
			ByteBuffer dataBuffer = BufferUtils.createByteBuffer(data.size());
			for(Byte dataByte : data){
				dataBuffer.put(dataByte);
			}
			dataBuffer.flip();//move the read pointer back to the beginning
			glNamedBufferData(bufferId, dataBuffer, usage);
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
			data.add((byte) ((floatAsInt >> 8) & 0xff));
			data.add((byte) ((floatAsInt >> 16) & 0xff));
			data.add((byte) ((floatAsInt >> 24) & 0xff));
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
			data.add((byte) ((doubleAsLong >> 8) & 0xff));
			data.add((byte) ((doubleAsLong >> 16) & 0xff));
			data.add((byte) ((doubleAsLong >> 24) & 0xff));
			data.add((byte) ((doubleAsLong >> 32) & 0xff));
			data.add((byte) ((doubleAsLong >> 40) & 0xff));
			data.add((byte) ((doubleAsLong >> 48) & 0xff));
			data.add((byte) ((doubleAsLong >> 56) & 0xff));
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
			data.add((byte) ((value >> 8) & 0xff));
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
			data.add((byte) ((value >> 8) & 0xff));
			data.add((byte) ((value >> 16) & 0xff));
			data.add((byte) ((value >> 24) & 0xff));
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
		if(offset+3 > data.size() || offset < 0){
			throw new IndexOutOfBoundsException("the area defined from offset through the size of the input value results in an insertion out of the buffers bounds");
		}else if(!finished){//check if this buffer has been flushed
			int floatAsInt = Float.floatToIntBits(value);
			data.set(offset, (byte)(floatAsInt & 0xff));
			data.set(offset+1, (byte)((floatAsInt >> 8) & 0xff));
			data.set(offset+2, (byte)((floatAsInt >> 16) & 0xff));
			data.set(offset+3, (byte)((floatAsInt >> 24) & 0xff));
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
		if(offset+7 > data.size() || offset < 0){
			throw new IndexOutOfBoundsException("the area defined from offset through the size of the input value results in an insertion out of the buffers bounds");
		}else if(!finished){//check if this buffer has been flushed
			long doubleAsLong = Double.doubleToLongBits(value);
			data.set(offset, (byte)(doubleAsLong & 0xff));
			data.set(offset+1, (byte)((doubleAsLong >> 8) & 0xff));
			data.set(offset+2, (byte)((doubleAsLong >> 16) & 0xff));
			data.set(offset+3, (byte)((doubleAsLong >> 24) & 0xff));
			data.set(offset+4, (byte)((doubleAsLong >> 32) & 0xff));
			data.set(offset+5, (byte)((doubleAsLong >> 40) & 0xff));
			data.set(offset+6, (byte)((doubleAsLong >> 48) & 0xff));
			data.set(offset+7, (byte)((doubleAsLong >> 56) & 0xff));
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
		if(offset > data.size() || offset < 0){//check if this buffer has been flushed
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
		if(offset+1 > data.size() || offset < 0){
			throw new IndexOutOfBoundsException("the area defined from offset through the size of the input value results in an insertion out of the buffers bounds");
		}else if(!finished){//check if this buffer has been flushed
			data.set(offset, (byte)(value & 0xff));
			data.set(offset+1, (byte)((value >> 8) & 0xff));
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
		if(offset+3 > data.size() || offset < 0){
			throw new IndexOutOfBoundsException("the area defined from offset through the size of the input value results in an insertion out of the buffers bounds");
		}else if(!finished){//check if this buffer has been flushed
			data.set(offset, (byte)(value & 0xff));
			data.set(offset+1, (byte)((value >> 8) & 0xff));
			data.set(offset+2, (byte)((value >> 16) & 0xff));
			data.set(offset+3, (byte)((value >> 24) & 0xff));
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
		if(offset+Vec2.SIZE_IN_BYTES-1 > data.size() || offset < 0){
			throw new IndexOutOfBoundsException("the area defined from offset through the size of the input value results in an insertion out of the buffers bounds");
		}else{
			this.set(offset, value.x);
			this.set(offset+4, value.y);
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
		if(offset+Vec3.SIZE_IN_BYTES-1 > data.size() || offset < 0){
			throw new IndexOutOfBoundsException("the area defined from offset through the size of the input value results in an insertion out of the buffers bounds");
		}else{
			this.set(offset, value.x);
			this.set(offset+4, value.y);
			this.set(offset+8, value.z);
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
		if(offset+Vec4.SIZE_IN_BYTES-1 > data.size() || offset < 0){
			throw new IndexOutOfBoundsException("the area defined from offset through the size of the input value results in an insertion out of the buffers bounds");
		}else{
			this.set(offset, value.x);
			this.set(offset+4, value.y);
			this.set(offset+8, value.z);
			this.set(offset+16, value.w);
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
	public void set(int offset, Mat2 value) throws IndexOutOfBoundsException{
		if(offset+Mat2.SIZE_IN_BYTES-1 > data.size() || offset < 0){
			throw new IndexOutOfBoundsException("the area defined from offset through the size of the input value results in an insertion out of the buffers bounds");
		}else{
			Vec2[] vectors = value.getMatrix();
			this.set(offset, vectors[0]);
			this.set(offset+Vec2.SIZE_IN_BYTES, vectors[1]);
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
	public void set(int offset, Mat3 value) throws IndexOutOfBoundsException{
		if(offset+Mat3.SIZE_IN_BYTES-1 > data.size() || offset < 0){
			throw new IndexOutOfBoundsException("the area defined from offset through the size of the input value results in an insertion out of the buffers bounds");
		}else{
			Vec3[] vectors = value.getMatrix();
			this.set(offset, vectors[0]);
			this.set(offset+Vec3.SIZE_IN_BYTES, vectors[1]);
			this.set(offset+(Vec3.SIZE_IN_BYTES*2), vectors[2]);
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
	public void set(int offset, Mat4 value) throws IndexOutOfBoundsException{
		if(offset+Mat4.SIZE_IN_BYTES-1 > data.size() || offset < 0){
			throw new IndexOutOfBoundsException("the area defined from offset through the size of the input value results in an insertion out of the buffers bounds");
		}else{
			Vec4[] vectors = value.getMatrix();
			this.set(offset, vectors[0]);
			this.set(offset+Vec4.SIZE_IN_BYTES, vectors[1]);
			this.set(offset+(Vec4.SIZE_IN_BYTES*2), vectors[2]);
			this.set(offset+(Vec4.SIZE_IN_BYTES*3), vectors[3]);
		}
	}
}
