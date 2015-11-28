package textures;

import java.nio.Buffer;

public interface ArrayTexture {

	/**
	 * Buffers the given pixel data to the entire texture at the given <code>index</code> and mipmap <code>level</code> of the texture array. 
	 * The <code>format</code> and <code>type</code> tell the GPU how to read the pixel data.If the texture has a compressed internal format 
	 * then a <code>ByteBuffer</code> must be provided, all other buffer types will be ignored.
	 * 
	 * @param pixels Raw pixel data to be passed to the GPU
	 * @param format Format of the pixel data being passed to GPU
	 * @param type Type of the pixel data being passed to the GPU
	 * @param index Index of the texture in the array to buffer the pixel data to
	 * @param level Mipmap level of the texture at the given <code>index</code> to buffer the data to
	 * @throws IndexOutOfBoundsException
	 */
	public void bufferData(Buffer pixels, BaseFormat format, TexDataType type, int index, int level) throws IndexOutOfBoundsException;
	
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
