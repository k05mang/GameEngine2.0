package textures;

import java.nio.Buffer;

import textures.enums.BaseFormat;
import textures.enums.TexDataType;

public interface BasicTexture {

	/**
	 * Buffers pixel data provided with the given {@code format} and {@code type} for the data to be read by the GPU. The 
	 * data is applied to the entire texture at the given level. If the texture is of type {@code TextureType.RECTANGLE} 
	 * then the level parameter is ignored. If the texture has a compressed internal format then a {@code ByteBuffer} must
	 * be provided, all other buffer types will be ignored.
	 * 
	 * @param pixels Raw pixel data to be passed to the GPU
	 * @param format Format of the pixel data
	 * @param type Type of the pixel data
	 * @param level Mipmap level of the texture to modify with the given pixel data
	 */
	public void bufferData(Buffer pixels, BaseFormat format, TexDataType type, int level);
	
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
