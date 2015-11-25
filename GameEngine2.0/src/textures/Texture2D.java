package textures;

import static org.lwjgl.opengl.GL45.*;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class Texture2D extends Texture implements BasicTexture {

	private int width, height;
	
	public Texture2D(InternalFormat format, int levels, int width, int height) {
		super(TextureType._2D, format, levels);
		this.width = width;
		this.height = height;
		glTextureStorage2D(id, levels, format.value, width, height);
	}

	@Override
	public void bufferData(ByteBuffer pixels, BaseFormat format, TexDataType type, int level) {
		if(iformat.isCompressedFormat()){
			glCompressedTextureSubImage2D(id, level, 0, 0, width, height, format.value, type.value, pixels);
		}else{
			glTextureSubImage2D(id, level, 0, 0, width, height, format.value, type.value, pixels);
		}
	}

	@Override
	public void bufferData(ShortBuffer pixels, BaseFormat format, TexDataType type, int level) {
		if(!iformat.isCompressedFormat()){
			glTextureSubImage2D(id, level, 0, 0, width, height, format.value, type.value, pixels);
		}
	}

	@Override
	public void bufferData(IntBuffer pixels, BaseFormat format, TexDataType type, int level) {
		if(!iformat.isCompressedFormat()){
			glTextureSubImage2D(id, level, 0, 0, width, height, format.value, type.value, pixels);
		}
	}

	@Override
	public void bufferData(FloatBuffer pixels, BaseFormat format, TexDataType type, int level) {
		if(!iformat.isCompressedFormat()){
			glTextureSubImage2D(id, level, 0, 0, width, height, format.value, type.value, pixels);
		}
	}

	@Override
	public void bufferData(DoubleBuffer pixels, BaseFormat format, TexDataType type, int level) {
		if(!iformat.isCompressedFormat()){
			glTextureSubImage2D(id, level, 0, 0, width, height, format.value, type.value, pixels);
		}
	}

	/**
	 * Buffers the given pixel data to the GPU, <code>format</code> and <code>type</code> specify how the GPU will read the pixel data.
	 * The pixel data will be applied to the given mipmap <code>level</code> of the texture and will start <code>xoffset</code> and 
	 * <code>yoffset</code> from the lower left corner of the texture, and will cover an area defined by <code>width</code> and <code>height</code>
	 * from the offset point.
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
	public void subImage(ByteBuffer pixels, BaseFormat format, TexDataType type, int level, int xoffset, int yoffset, int width, int height){
		if(iformat.isCompressedFormat()){
			glCompressedTextureSubImage2D(id, level, xoffset, yoffset, width, height, format.value, type.value, pixels);
		}else{
			glTextureSubImage2D(id, level, xoffset, yoffset, width, height, format.value, type.value, pixels);
		}
	}

	/**
	 * Buffers the given pixel data to the GPU, <code>format</code> and <code>type</code> specify how the GPU will read the pixel data.
	 * The pixel data will be applied to the given mipmap <code>level</code> of the texture and will start <code>xoffset</code> and 
	 * <code>yoffset</code> from the lower left corner of the texture, and will cover an area defined by <code>width</code> and <code>height</code>
	 * from the offset point. If the internal format of the texture is a compressed type then this function will do nothing, instead 
	 * @link{#subImage(ByteBuffer, BaseFormat, TexDataType, int, int, int, int, int) subImage} should be used.
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
	public void subImage(ShortBuffer pixels, BaseFormat format, TexDataType type, int level, int xoffset, int yoffset, int width, int height){
		if(!iformat.isCompressedFormat()){
			glTextureSubImage2D(id, level, xoffset, yoffset, width, height, format.value, type.value, pixels);
		}
	}

	/**
	 * Buffers the given pixel data to the GPU, <code>format</code> and <code>type</code> specify how the GPU will read the pixel data.
	 * The pixel data will be applied to the given mipmap <code>level</code> of the texture and will start <code>xoffset</code> and 
	 * <code>yoffset</code> from the lower left corner of the texture, and will cover an area defined by <code>width</code> and <code>height</code>
	 * from the offset point. If the internal format of the texture is a compressed type then this function will do nothing, instead 
	 * @link{#subImage(ByteBuffer, BaseFormat, TexDataType, int, int, int, int, int) subImage} should be used.
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
	public void subImage(IntBuffer pixels, BaseFormat format, TexDataType type, int level, int xoffset, int yoffset, int width, int height){
		if(!iformat.isCompressedFormat()){
			glTextureSubImage2D(id, level, xoffset, yoffset, width, height, format.value, type.value, pixels);
		}
	}

	/**
	 * Buffers the given pixel data to the GPU, <code>format</code> and <code>type</code> specify how the GPU will read the pixel data.
	 * The pixel data will be applied to the given mipmap <code>level</code> of the texture and will start <code>xoffset</code> and 
	 * <code>yoffset</code> from the lower left corner of the texture, and will cover an area defined by <code>width</code> and <code>height</code>
	 * from the offset point. If the internal format of the texture is a compressed type then this function will do nothing, instead 
	 * @link{#subImage(ByteBuffer, BaseFormat, TexDataType, int, int, int, int, int) subImage} should be used.
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
	public void subImage(FloatBuffer pixels, BaseFormat format, TexDataType type, int level, int xoffset, int yoffset, int width, int height){
		if(!iformat.isCompressedFormat()){
			glTextureSubImage2D(id, level, xoffset, yoffset, width, height, format.value, type.value, pixels);
		}
	}

	/**
	 * Buffers the given pixel data to the GPU, <code>format</code> and <code>type</code> specify how the GPU will read the pixel data.
	 * The pixel data will be applied to the given mipmap <code>level</code> of the texture and will start <code>xoffset</code> and 
	 * <code>yoffset</code> from the lower left corner of the texture, and will cover an area defined by <code>width</code> and <code>height</code>
	 * from the offset point. If the internal format of the texture is a compressed type then this function will do nothing, instead 
	 * @link{#subImage(ByteBuffer, BaseFormat, TexDataType, int, int, int, int, int) subImage} should be used.
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
	public void subImage(DoubleBuffer pixels, BaseFormat format, TexDataType type, int level, int xoffset, int yoffset, int width, int height){
		if(!iformat.isCompressedFormat()){
			glTextureSubImage2D(id, level, xoffset, yoffset, width, height, format.value, type.value, pixels);
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
