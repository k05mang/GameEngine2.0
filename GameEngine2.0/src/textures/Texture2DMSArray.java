package textures;

import static org.lwjgl.opengl.GL45.*;

public class Texture2DMSArray extends Texture {

	private int width, height, length;
	
	/**
	 * Constructs an array of <code>length</code> two dimensional multisample textures with the given <code>InternalFormat</code>, <code>samples</code>,
	 * <code>width</code> and <code>height</code>, and fixed sampling for each texture in the array.
	 *  
	 * @param format Internal format of the textures in the array
	 * @param samples Number of samples to used for the textures in the array
	 * @param width Width of the textures in the array
	 * @param height Height of the textures in the array
	 * @param length Number of textures in the array
	 * @param fixedSampling Whether to use fixed sampling for each of the textures in the array
	 */
	public Texture2DMSArray(InternalFormat format, int samples, int width, int height, int length, boolean fixedSampling) {
		super(TextureType._2D_MULTISAMPLE_ARRAY, format, samples);
		this.width = width;
		this.height = height;
		this.length = length;
		glTextureStorage3DMultisample(id, levels_samples, format.value, width, height, length, fixedSampling);
	}
	
	/**
	 * Gets the width of the texture
	 * 
	 * @return Width of the texture
	 */
	public int getWidth(){
		return width;
	}
	
	/**
	 * Gets the height of the texture
	 * 
	 * @return Height of the texture
	 */
	public int getHeight(){
		return height;
	}
	
	/**
	 * Gets the length of the texture array
	 * 
	 * @return Length of this array
	 */
	public int length(){
		return length;
	}
}
