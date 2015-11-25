package textures;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public interface BasicTexture {

	/**
	 * Buffers pixel data provided with the given <code>format</code> and <code>type</code> for the data to be read by the GPU. The 
	 * data is applied to the entire texture at the given level. If the texture is of type <code>TextureType.RECTANGLE</code> 
	 * then the level parameter is ignored.
	 * 
	 * @param pixels Raw pixel data to be passed to the GPU
	 * @param format Format of the pixel data
	 * @param type Type of the pixel data
	 * @param level Mipmap level of the texture to modify with the given pixel data
	 */
	public void bufferData(ByteBuffer pixels, BaseFormat format, TexDataType type, int level);
	
	/**
	 * Buffers pixel data provided with the given <code>format</code> and <code>type</code> for the data to be read by the GPU. The 
	 * data is applied to the entire texture at the given level.  If the texture is of type <code>TextureType.RECTANGLE</code> 
	 * then the level parameter is ignored. If the texture's internal format is a compressed type this function does nothing, 
	 * to buffer data to a compressed format type use @link{#bufferData(ByteBuffer, BaseFormat, TexDataType, int) bufferData}.
	 * 
	 * @param pixels Raw pixel data to be passed to the GPU
	 * @param format Format of the pixel data
	 * @param type Type of the pixel data
	 * @param level Mipmap level of the texture to modify with the given pixel data
	 */
	public void bufferData(ShortBuffer pixels, BaseFormat format, TexDataType type, int level);
	
	/**
	 * Buffers pixel data provided with the given <code>format</code> and <code>type</code> for the data to be read by the GPU. The 
	 * data is applied to the entire texture at the given level.  If the texture is of type <code>TextureType.RECTANGLE</code> 
	 * then the level parameter is ignored. If the texture's internal format is a compressed type this function does nothing, 
	 * to buffer data to a compressed format type use @link{#bufferData(ByteBuffer, BaseFormat, TexDataType, int) bufferData}.
	 * 
	 * @param pixels Raw pixel data to be passed to the GPU
	 * @param format Format of the pixel data
	 * @param type Type of the pixel data
	 * @param level Mipmap level of the texture to modify with the given pixel data
	 */
	public void bufferData(IntBuffer pixels, BaseFormat format, TexDataType type, int level);
	
	/**
	 * Buffers pixel data provided with the given <code>format</code> and <code>type</code> for the data to be read by the GPU. The 
	 * data is applied to the entire texture at the given level.  If the texture is of type <code>TextureType.RECTANGLE</code> 
	 * then the level parameter is ignored. If the texture's internal format is a compressed type this function does nothing, 
	 * to buffer data to a compressed format type use @link{#bufferData(ByteBuffer, BaseFormat, TexDataType, int) bufferData}.
	 * 
	 * @param pixels Raw pixel data to be passed to the GPU
	 * @param format Format of the pixel data
	 * @param type Type of the pixel data
	 * @param level Mipmap level of the texture to modify with the given pixel data
	 */
	public void bufferData(FloatBuffer pixels, BaseFormat format, TexDataType type, int level);
	
	/**
	 * Buffers pixel data provided with the given <code>format</code> and <code>type</code> for the data to be read by the GPU. The 
	 * data is applied to the entire texture at the given level.  If the texture is of type <code>TextureType.RECTANGLE</code> 
	 * then the level parameter is ignored. If the texture's internal format is a compressed type this function does nothing, 
	 * to buffer data to a compressed format type use @link{#bufferData(ByteBuffer, BaseFormat, TexDataType, int) bufferData}.
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
		return 1;
	}
	
	/**
	 * Gets the height of the texture, if the texture is one dimensional then this will return 1.
	 * 
	 * @return The texture height or 1 if the texture has no height
	 */
	default int getHeight(){
		return 1;
	}
	
	/**
	 * Gets the depth of the texture, if the texture is one or two dimensional then this will return 1.
	 * 
	 * @return The texture depth or 1 if the texture has no depth
	 */
	default int getDepth(){
		return 1;
	}
}
