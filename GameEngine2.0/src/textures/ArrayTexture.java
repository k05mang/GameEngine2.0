package textures;

import java.nio.Buffer;

import textures.enums.BaseFormat;
import textures.enums.TexDataType;

public interface ArrayTexture {
	
	/**
	 * Buffers the given pixel data to the entire area of all textures in the array at the given mipmap {@code level}. The 
	 * {@code format} and {@code type} tell the GPU how to read the pixel data.If the texture has a compressed internal format 
	 * then a {@code ByteBuffer} must be provided, all other buffer types will be ignored.
	 * 
	 * @param pixels Raw pixel data to be passed to the GPU
	 * @param format Format of the pixel data being passed to GPU
	 * @param type Type of the pixel data being passed to the GPU
	 * @param level Mipmap level of the textures to modify
	 * @throws IndexOutOfBoundsException
	 */
	public void bufferData(Buffer pixels, BaseFormat format, TexDataType type, int level) throws IndexOutOfBoundsException;
	
	/**
	 * Buffers the given pixel data to the entire texture at the given {@code index} and mipmap {@code level} of the texture array. 
	 * The {@code format} and {@code type} tell the GPU how to read the pixel data.If the texture has a compressed internal format 
	 * then a {@code ByteBuffer} must be provided, all other buffer types will be ignored.
	 * 
	 * @param pixels Raw pixel data to be passed to the GPU
	 * @param format Format of the pixel data being passed to GPU
	 * @param type Type of the pixel data being passed to the GPU
	 * @param index Index of the texture in the array to buffer the pixel data to
	 * @param level Mipmap level of the texture at the given {@code index} to buffer the data to
	 * @throws IndexOutOfBoundsException
	 */
	public void bufferData(Buffer pixels, BaseFormat format, TexDataType type, int index, int level) throws IndexOutOfBoundsException;
	
	/**
	 * Buffers the given pixel data to the entire area of all textures from {@code baseIndex} through {@code baseIndex+count} textures.
	 * The {@code format} and {@code type} tell the GPU how to read the pixel data.If the texture has a compressed internal format 
	 * then a {@code ByteBuffer} must be provided, all other buffer types will be ignored.
	 * 
	 * @param pixels Raw pixel data to be passed to the GPU
	 * @param format Format of the pixel data being passed to GPU
	 * @param type Type of the pixel data being passed to the GPU
	 * @param baseIndex Starting index of the first texture in the array to start modifying
	 * @param count Number of textures to modify starting from {@code baseIndex}
	 * @param level Mipmap level of the textures from {@code baseIndex} through {@code count} textures to modify
	 * @throws IndexOutOfBoundsException
	 */
	public void bufferData(Buffer pixels, BaseFormat format, TexDataType type, int baseIndex, int count, int level) throws IndexOutOfBoundsException;
	
	/**
	 * Gets the length of this texture array
	 * 
	 * @return Length of the texture array
	 */
	public int length();
	
	/**
	 * Gets the width of the textures in the array
	 * 
	 * @return Width of the textures in the array
	 */
	public int getWidth();
	
	/**
	 * Gets the height of the textures in the array, if the array is of one dimensional textures this will return 1
	 * 
	 * @return Height of the textures in the array or 1 if they are one dimensional textures
	 */
	public int getHeight();
}
