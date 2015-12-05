package textures;

import static org.lwjgl.opengl.GL45.*;

public class Texture2DMS extends Texture{

	private int width, height;
	
	/**
	 * Constructs a two dimensional multisample texture with the given {@code InternalFormat}, {@code samples},
	 * {@code width} and {@code height}, and fixed sampling.
	 * 
	 * @param format Internal format of the texture
	 * @param samples Number of samples used in filtering the texture
	 * @param width Width of the texture
	 * @param height Height of the texture
	 * @param fixedSampling Whether to use a fixed sampling when taking samples
	 */
	public Texture2DMS(InternalFormat format, int samples, int width, int height, boolean fixedSampling) {
		super(TextureType._2D_MULTISAMPLE, format, samples);
		this.width = width;
		this.height = height;
		glTextureStorage2DMultisample(id, levels_samples, width, height, format.value, fixedSampling);
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
}
