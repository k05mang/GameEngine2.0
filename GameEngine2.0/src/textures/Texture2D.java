package textures;

import static org.lwjgl.opengl.GL45.*;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.Buffer;

public class Texture2D extends Texture implements BasicTexture {

	private int width, height;
	
	/**
	 * Constructs a two dimensional texture with the given <code>InternalFormat</code>, <code>width</code>,
	 * <code>height</code>, and mipmap <code>levels</code>.
	 * 
	 * @param format Internal format of the texture to store pixel data
	 * @param levels Number of mipmap levels for this texture
	 * @param width Width of the the texture
	 * @param height Height of the texture
	 */
	public Texture2D(InternalFormat format, int levels, int width, int height) {
		super(TextureType._2D, format, levels);
		this.width = width;
		this.height = height;
		glTextureStorage2D(id, levels_samples, format.value, width, height);
	}

	@Override
	public void bufferData(Buffer pixels, BaseFormat format, TexDataType type, int level) {
		subImage(pixels, format, type, level, 0, 0, width, height);
	}

	/**
	 * Buffers the given pixel data to the GPU, <code>format</code> and <code>type</code> specify how the GPU will read the pixel data.
	 * The pixel data will be applied to the given mipmap <code>level</code> of the texture and will start at <code>xoffset</code> and 
	 * <code>yoffset</code> from the lower left corner of the texture, and will cover an area defined by <code>width</code> and <code>height</code>
	 * from the offset point. If the texture has a compressed internal format then a <code>ByteBuffer</code> must be provided, all other buffer 
	 * types will be ignored.
	 * 
	 * @param pixels Pixel data to be sent to the GPU
	 * @param format Format of the pixel data being sent
	 * @param type Type of the pixel data being sent
	 * @param level Mipmap level of the texture to apply the pixel data to
	 * @param xoffset X offset from the lower left corner of the texture to start modifying
	 * @param yoffset Y offset from the lower left corner of the texture to start modifying
	 * @param width Width from the offset point to start modifying
	 * @param height Height from the offset point to start modifying
	 */
	public void subImage(Buffer pixels, BaseFormat format, TexDataType type, int level, int xoffset, int yoffset, int width, int height){
		if(!iformat.isCompressedFormat()){
			if(pixels instanceof ByteBuffer){
				glTextureSubImage2D(id, level, xoffset, yoffset, width, height, format.value, type.value, (ByteBuffer)pixels);
			}else if(pixels instanceof ShortBuffer){
				glTextureSubImage2D(id, level, xoffset, yoffset, width, height, format.value, type.value, (ShortBuffer)pixels);
			}else if(pixels instanceof IntBuffer){
				glTextureSubImage2D(id, level, xoffset, yoffset, width, height, format.value, type.value, (IntBuffer)pixels);
			}else if(pixels instanceof FloatBuffer){
				glTextureSubImage2D(id, level, xoffset, yoffset, width, height, format.value, type.value, (FloatBuffer)pixels);
			}else if(pixels instanceof DoubleBuffer){
				glTextureSubImage2D(id, level, xoffset, yoffset, width, height, format.value, type.value, (DoubleBuffer)pixels);
			}
		}else{
			if(pixels instanceof ByteBuffer){
				glCompressedTextureSubImage2D(id, level, xoffset, yoffset, width, height, format.value, type.value, (ByteBuffer)pixels);
			}//else do nothing
		}
	}
	
	@Override
	public int getWidth(){
		return width;
	}
	
	@Override
	public int getHeight(){
		return height;
	}
}
