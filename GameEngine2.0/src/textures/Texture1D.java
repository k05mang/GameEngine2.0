package textures;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.Buffer;

import static org.lwjgl.opengl.GL45.*;

public class Texture1D extends Texture implements BasicTexture{
	private int width;
	
	/**
	 * Constructs a texture object on the GPU with the given <code>InternalFormat</code>, mipmap <code>levels</code>, and
	 * <code>width</code>.
	 * 
	 * @param format Internal GPU formatting of the texture data
	 * @param levels Number of mipmap levels to apply to the texture
	 * @param width Width of the texture
	 */
	public Texture1D(InternalFormat format, int levels, int width) {
		super(TextureType._1D, format, levels);
		this.width = width;
		glTextureStorage1D(id, levels_samples, format.value, width);
	}

	@Override
	public void bufferData(Buffer pixels, BaseFormat format, TexDataType type, int level) {
		subImage(pixels, format, type, level, 0, width);
	}

	@Override
	public int getWidth(){
		return width;
	}
	
	/**
	 * Buffers pixel data provided with the given <code>format</code> and <code>type</code> for the data to be read by the GPU. The pixel data is applied to the subimage of
	 * the texture defined by <code>offset</code> and <code>width</code>. If the texture has a compressed internal format then a <code>ByteBuffer</code> must
	 * be provided, all other buffer types will be ignored.
	 * 
	 * @param pixels Pixel data to pass to the GPU
	 * @param format Format of the pixel data
	 * @param type Type of the pixel data
	 * @param level Mipamp level to modify with the given pixel data
	 * @param offset Offset from the beginning of the texture to start modifying with the given data
	 * @param width Width from the offset to modify with the given data
	 */
	public void subImage(Buffer pixels, BaseFormat format, TexDataType type, int level, int offset, int width){
		//check what type of buffer has been passed and call the function, casting the buffer to the proper type
		if(!iformat.isCompressedFormat()){
			if(pixels instanceof ByteBuffer){
				glTextureSubImage1D(id, level, offset, width, format.value, type.value, (ByteBuffer)pixels);
			}else if(pixels instanceof ShortBuffer){
				glTextureSubImage1D(id, level, offset, width, format.value, type.value, (ShortBuffer)pixels);
			}else if(pixels instanceof IntBuffer){
				glTextureSubImage1D(id, level, offset, width, format.value, type.value, (IntBuffer)pixels);
			}else if(pixels instanceof FloatBuffer){
				glTextureSubImage1D(id, level, offset, width, format.value, type.value, (FloatBuffer)pixels);
			}else if(pixels instanceof DoubleBuffer){
				glTextureSubImage1D(id, level, offset, width, format.value, type.value, (DoubleBuffer)pixels);
			}
		}else{
			if(pixels instanceof ByteBuffer){
				glCompressedTextureSubImage1D(id, level, offset, width, format.value, type.value, (ByteBuffer)pixels);
			}//else do nothing
		}
	}
}
