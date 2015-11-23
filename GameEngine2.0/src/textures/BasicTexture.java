package textures;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public interface BasicTexture {

	/**
	 * Buffers pixel data provided with the given format and type for the data to be read by the GPU. The 
	 * data is applied to the entire texture at the given level.
	 * 
	 * @param pixels Raw pixel data to be passed to the GPU
	 * @param format Format of the pixel data
	 * @param type Type of the pixel data
	 * @param level Mipmap level of the texture to modify with the given pixel data
	 */
	public void bufferData(ByteBuffer pixels, BaseFormat format, TexDataType type, int level);
	
	/**
	 * Buffers pixel data provided with the given format and type for the data to be read by the GPU. The 
	 * data is applied to the entire texture at the given level. If the texture's internal format is a 
	 * compressed type this function does nothing, to buffer data to a compressed format type use 
	 * @link{#bufferData(ByteBuffer, BaseFormat, TexDataType, int) bufferData}.
	 * 
	 * @param pixels Raw pixel data to be passed to the GPU
	 * @param format Format of the pixel data
	 * @param type Type of the pixel data
	 * @param level Mipmap level of the texture to modify with the given pixel data
	 */
	public void bufferData(ShortBuffer pixels, BaseFormat format, TexDataType type, int level);
	
	/**
	 * Buffers pixel data provided with the given format and type for the data to be read by the GPU. The 
	 * data is applied to the entire texture at the given level. If the texture's internal format is a 
	 * compressed type this function does nothing, to buffer data to a compressed format type use 
	 * @link{#bufferData(ByteBuffer, BaseFormat, TexDataType, int) bufferData}.
	 * 
	 * @param pixels Raw pixel data to be passed to the GPU
	 * @param format Format of the pixel data
	 * @param type Type of the pixel data
	 * @param level Mipmap level of the texture to modify with the given pixel data
	 */
	public void bufferData(IntBuffer pixels, BaseFormat format, TexDataType type, int level);
	
	/**
	 * Buffers pixel data provided with the given format and type for the data to be read by the GPU. The 
	 * data is applied to the entire texture at the given level. If the texture's internal format is a 
	 * compressed type this function does nothing, to buffer data to a compressed format type use 
	 * @link{#bufferData(ByteBuffer, BaseFormat, TexDataType, int) bufferData}.
	 * 
	 * @param pixels Raw pixel data to be passed to the GPU
	 * @param format Format of the pixel data
	 * @param type Type of the pixel data
	 * @param level Mipmap level of the texture to modify with the given pixel data
	 */
	public void bufferData(FloatBuffer pixels, BaseFormat format, TexDataType type, int level);
	
	/**
	 * Buffers pixel data provided with the given format and type for the data to be read by the GPU. The 
	 * data is applied to the entire texture at the given level. If the texture's internal format is a 
	 * compressed type this function does nothing, to buffer data to a compressed format type use 
	 * @link{#bufferData(ByteBuffer, BaseFormat, TexDataType, int) bufferData}.
	 * 
	 * @param pixels Raw pixel data to be passed to the GPU
	 * @param format Format of the pixel data
	 * @param type Type of the pixel data
	 * @param level Mipmap level of the texture to modify with the given pixel data
	 */
	public void bufferData(DoubleBuffer pixels, BaseFormat format, TexDataType type, int level);
	
	/**
	 * Gets the width of the texture 
	 * 
	 * @return Width of the texture
	 */
	default int getWidth(){
		return 0;
	}
	
	/**
	 * Gets the height of the texture, if the texture is one dimensional then this will return 0.
	 * 
	 * @return The texture height or 0 if the texture has no height
	 */
	default int getHeight(){
		return 0;
	}
	
	/**
	 * Gets the depth of the texture, if the texture is one or two dimensional then this will return 0.
	 * 
	 * @return The texture depth or 0 if the texture has no depth
	 */
	default int getDepth(){
		return 0;
	}
	
//	public void subImage(ByteBuffer pixels, BaseFormat format, TexDataType type, int level);
//	public void subImage(ShortBuffer pixels, BaseFormat format, TexDataType type, int level);
//	public void subImage(IntBuffer pixels, BaseFormat format, TexDataType type, int level);
//	public void subImage(FloatBuffer pixels, BaseFormat format, TexDataType type, int level);
//	public void subImage(DoubleBuffer pixels, BaseFormat format, TexDataType type, int level);
}
