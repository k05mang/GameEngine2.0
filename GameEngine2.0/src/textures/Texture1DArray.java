package textures;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.opengl.GL45.*;

public class Texture1DArray extends Texture implements ArrayTexture{

	private int width, length;
	
	/**
	 * Constructs a texture array of one dimensional textures. Each texture will have the given <code>InternalFormat</code>,
	 * <code>width</code>, and mipmap <code>levels</code>, <code>length</code> will define the number of textures in the array.
	 * 
	 * @param format Internal GPU formatting of the texture data
	 * @param width Width of the textures defined in the array
	 * @param levels Mipmap levels to apply to each element in the texture array
	 * @param length Number of texture elements in the array
	 */
	public Texture1DArray(InternalFormat format, int width, int levels, int length) {
		super(TextureType._1D_ARRAY, format, levels);
		this.width = width;
		this.length = length;
		glTextureStorage2D(id, levels, format.value, width, length);
	}

	@Override
	public void bufferData(ByteBuffer pixels, BaseFormat format, TexDataType type, int index, int level)
			throws IndexOutOfBoundsException {
		if(index >= length || index < 0){
			throw new IndexOutOfBoundsException("Texture array index greater than array length");
		}else if(iformat.isCompressedFormat()){
			glCompressedTextureSubImage2D(id, level, 0, 0, width, index, format.value, type.value, pixels);
		}else{
			glTextureSubImage2D(id, level, 0, 0, width, index, format.value, type.value, pixels);
		}
	}

	@Override
	public void bufferData(ShortBuffer pixels, BaseFormat format, TexDataType type, int index, int level)
			throws IndexOutOfBoundsException {
		//check if the index is out of bounds 
		if(index >= length || index < 0){
			throw new IndexOutOfBoundsException("Texture array index greater than array length\nindex:"+index+"\nlength:"+length);
		}else if(!iformat.isCompressedFormat()){
			glTextureSubImage2D(id, level, 0, 0, width, index, format.value, type.value, pixels);
		}
	}

	@Override
	public void bufferData(IntBuffer pixels, BaseFormat format, TexDataType type, int index, int level)
			throws IndexOutOfBoundsException {
		//check if the index is out of bounds 
		if(index >= length || index < 0){
			throw new IndexOutOfBoundsException("Texture array index greater than array length\nindex:"+index+"\nlength:"+length);
		}else if(!iformat.isCompressedFormat()){
			glTextureSubImage2D(id, level, 0, 0, width, index, format.value, type.value, pixels);
		}
	}

	@Override
	public void bufferData(FloatBuffer pixels, BaseFormat format, TexDataType type, int index, int level)
			throws IndexOutOfBoundsException {
		//check if the index is out of bounds 
		if(index >= length || index < 0){
			throw new IndexOutOfBoundsException("Texture array index greater than array length\nindex:"+index+"\nlength:"+length);
		}else if(!iformat.isCompressedFormat()){
			glTextureSubImage2D(id, level, 0, 0, width, index, format.value, type.value, pixels);
		}
	}

	@Override
	public void bufferData(DoubleBuffer pixels, BaseFormat format, TexDataType type, int index, int level)
			throws IndexOutOfBoundsException {
		//check if the index is out of bounds 
		if(index >= length || index < 0){
			throw new IndexOutOfBoundsException("Texture array index greater than array length\nindex:"+index+"\nlength:"+length);
		}else if(!iformat.isCompressedFormat()){
			glTextureSubImage2D(id, level, 0, 0, width, index, format.value, type.value, pixels);
		}
	}
	
	/**
	 * Buffers the given pixel data to the texture area defined from <code>offset</code> through <code>width</code> at the given <code>index</code>
	 * and mipmap <code>level</code> of the texture array. The <code>format</code> and <code>type</code> tell the GPU how to read the pixel data
	 * 
	 * @param pixels Pixel data to buffer to the texture on the GPU
	 * @param format Format of the pixel data being sent
	 * @param type Type of the pixel data being sent
	 * @param index Index of the texture in the array to modify
	 * @param level Mipmap level of the texture at the specified index in the array
	 * @param offset Offset from the start of the texture to start modifying from
	 * @param width Width of the area of the texture after offset to modify
	 * @throws IndexOutOfBoundsException
	 */
	public void subImage(ByteBuffer pixels, BaseFormat format, TexDataType type, int index, int level, int offset, int width)
			throws IndexOutOfBoundsException {
		if(index >= length || index < 0){
			throw new IndexOutOfBoundsException("Texture array index greater than array length");
		}else if(iformat.isCompressedFormat()){
			glCompressedTextureSubImage2D(id, level, offset, 0, width, index, format.value, type.value, pixels);
		}else{
			glTextureSubImage2D(id, level, offset, 0, width, index, format.value, type.value, pixels);
		}
	}

	/**
	 * Buffers the given pixel data to the texture area defined from <code>offset</code> through <code>width</code> at the given <code>index</code>
	 * and mipmap <code>level</code> of the texture array. The <code>format</code> and <code>type</code> tell the GPU how to read the pixel data. 
	 * If the internal format of the textures in the texture array are a compressed format type then this function will do nothing, to buffer data
	 * to a compressed format type use @link{#subImage(ByteBuffer, BaseFormat, TexDataType, int, int, int, int) subImage}.
	 * 
	 * @param pixels Pixel data to buffer to the texture on the GPU
	 * @param format Format of the pixel data being sent
	 * @param type Type of the pixel data being sent
	 * @param index Index of the texture in the array to modify
	 * @param level Mipmap level of the texture at the specified index in the array
	 * @param offset Offset from the start of the texture to start modifying from
	 * @param width Width of the area of the texture after offset to modify
	 * @throws IndexOutOfBoundsException
	 */
	public void subImage(ShortBuffer pixels, BaseFormat format, TexDataType type, int index, int level, int offset, int width)
			throws IndexOutOfBoundsException {
		//check if the index is out of bounds 
		if(index >= length || index < 0){
			throw new IndexOutOfBoundsException("Texture array index greater than array length\nindex:"+index+"\nlength:"+length);
		}else if(!iformat.isCompressedFormat()){
			glTextureSubImage2D(id, level, offset, 0, width, index, format.value, type.value, pixels);
		}
	}

	/**
	 * Buffers the given pixel data to the texture area defined from <code>offset</code> through <code>width</code> at the given <code>index</code>
	 * and mipmap <code>level</code> of the texture array. The <code>format</code> and <code>type</code> tell the GPU how to read the pixel data. 
	 * If the internal format of the textures in the texture array are a compressed format type then this function will do nothing, to buffer data
	 * to a compressed format type use @link{#subImage(ByteBuffer, BaseFormat, TexDataType, int, int, int, int) subImage}.
	 * 
	 * @param pixels Pixel data to buffer to the texture on the GPU
	 * @param format Format of the pixel data being sent
	 * @param type Type of the pixel data being sent
	 * @param index Index of the texture in the array to modify
	 * @param level Mipmap level of the texture at the specified index in the array
	 * @param offset Offset from the start of the texture to start modifying from
	 * @param width Width of the area of the texture after offset to modify
	 * @throws IndexOutOfBoundsException
	 */
	public void subImage(IntBuffer pixels, BaseFormat format, TexDataType type, int index, int level, int offset, int width)
			throws IndexOutOfBoundsException {
		//check if the index is out of bounds 
		if(index >= length || index < 0){
			throw new IndexOutOfBoundsException("Texture array index greater than array length\nindex:"+index+"\nlength:"+length);
		}else if(!iformat.isCompressedFormat()){
			glTextureSubImage2D(id, level, offset, 0, width, index, format.value, type.value, pixels);
		}
	}

	/**
	 * Buffers the given pixel data to the texture area defined from <code>offset</code> through <code>width</code> at the given <code>index</code>
	 * and mipmap <code>level</code> of the texture array. The <code>format</code> and <code>type</code> tell the GPU how to read the pixel data. 
	 * If the internal format of the textures in the texture array are a compressed format type then this function will do nothing, to buffer data
	 * to a compressed format type use @link{#subImage(ByteBuffer, BaseFormat, TexDataType, int, int, int, int) subImage}.
	 * 
	 * @param pixels Pixel data to buffer to the texture on the GPU
	 * @param format Format of the pixel data being sent
	 * @param type Type of the pixel data being sent
	 * @param index Index of the texture in the array to modify
	 * @param level Mipmap level of the texture at the specified index in the array
	 * @param offset Offset from the start of the texture to start modifying from
	 * @param width Width of the area of the texture after offset to modify
	 * @throws IndexOutOfBoundsException
	 */
	public void subImage(FloatBuffer pixels, BaseFormat format, TexDataType type, int index, int level, int offset, int width)
			throws IndexOutOfBoundsException {
		//check if the index is out of bounds 
		if(index >= length || index < 0){
			throw new IndexOutOfBoundsException("Texture array index greater than array length\nindex:"+index+"\nlength:"+length);
		}else if(!iformat.isCompressedFormat()){
			glTextureSubImage2D(id, level, offset, 0, width, index, format.value, type.value, pixels);
		}
	}

	/**
	 * Buffers the given pixel data to the texture area defined from <code>offset</code> through <code>width</code> at the given <code>index</code>
	 * and mipmap <code>level</code> of the texture array. The <code>format</code> and <code>type</code> tell the GPU how to read the pixel data. 
	 * If the internal format of the textures in the texture array are a compressed format type then this function will do nothing, to buffer data
	 * to a compressed format type use @link{#subImage(ByteBuffer, BaseFormat, TexDataType, int, int, int, int) subImage}.
	 * 
	 * @param pixels Pixel data to buffer to the texture on the GPU
	 * @param format Format of the pixel data being sent
	 * @param type Type of the pixel data being sent
	 * @param index Index of the texture in the array to modify
	 * @param level Mipmap level of the texture at the specified index in the array
	 * @param offset Offset from the start of the texture to start modifying from
	 * @param width Width of the area of the texture after offset to modify
	 * @throws IndexOutOfBoundsException
	 */
	public void subImage(DoubleBuffer pixels, BaseFormat format, TexDataType type, int index, int level, int offset, int width)
			throws IndexOutOfBoundsException {
		//check if the index is out of bounds 
		if(index >= length || index < 0){
			throw new IndexOutOfBoundsException("Texture array index greater than array length\nindex:"+index+"\nlength:"+length);
		}else if(!iformat.isCompressedFormat()){
			glTextureSubImage2D(id, level, offset, 0, width, index, format.value, type.value, pixels);
		}
	}

	@Override
	public int length() {
		return length;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return 1;
	}
}
