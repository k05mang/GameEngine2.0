package textures;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
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
		glTextureStorage1D(id, levels, format.value, width);
	}

	@Override
	public void bufferData(ByteBuffer pixels, BaseFormat format, TexDataType type, int level) {
		if(iformat.isCompressedFormat()){
			glCompressedTextureSubImage1D(id, level, 0, width, format.value, type.value, pixels);
		}else{
			glTextureSubImage1D(id, level, 0, width, format.value, type.value, pixels);
		}
	}

	@Override
	public void bufferData(ShortBuffer pixels, BaseFormat format, TexDataType type, int level) {
		if(!iformat.isCompressedFormat()){
			glTextureSubImage1D(id, level, 0, width, format.value, type.value, pixels);
		}
	}

	@Override
	public void bufferData(IntBuffer pixels, BaseFormat format, TexDataType type, int level) {
		if(!iformat.isCompressedFormat()){
			glTextureSubImage1D(id, level, 0, width, format.value, type.value, pixels);
		}
	}

	@Override
	public void bufferData(FloatBuffer pixels, BaseFormat format, TexDataType type, int level) {
		if(!iformat.isCompressedFormat()){
			glTextureSubImage1D(id, level, 0, width, format.value, type.value, pixels);
		}
	}

	@Override
	public void bufferData(DoubleBuffer pixels, BaseFormat format, TexDataType type, int level) {
		if(!iformat.isCompressedFormat()){
			glTextureSubImage1D(id, level, 0, width, format.value, type.value, pixels);
		}
	}

	@Override
	public int getWidth(){
		return width;
	}
	
	/**
	 * Buffers pixel data provided with the given <code>format</code> and <code>type</code> for the data to be read by the GPU. The pixel data is applied to the subimage of
	 * the texture defined by <code>offset</code> and <code>width</code>.
	 * 
	 * @param pixels Pixel data to pass to the GPU
	 * @param format Format of the pixel data
	 * @param type Type of the pixel data
	 * @param level Mipamp level to modify with the given pixel data
	 * @param offset Offset from the beginning of the texture to start modifying with the given data
	 * @param width Width from the offset to modify with the given data
	 */
	public void subImage(ByteBuffer pixels, BaseFormat format, TexDataType type, int level, int offset, int width){
		if(iformat.isCompressedFormat()){
			glCompressedTextureSubImage1D(id, level, 0, width, format.value, type.value, pixels);
		}else{
			glTextureSubImage1D(id, level, 0, width, format.value, type.value, pixels);
		}
	}

	/**
	 * Buffers pixel data provided with the given <code>format</code> and <code>type</code> for the data to be read by the GPU. The pixel data is applied to the subimage of
	 * the texture defined by <code>offset</code> and <code>width</code>. If the texture's internal format is a compressed type function does nothing,
	 * to buffer data to a compressed format type use @link{#subImage(ByteBuffer, BaseFormat, TexDataType, int, int, int) bufferData}.
	 * 
	 * @param pixels Pixel data to pass to the GPU
	 * @param format Format of the pixel data
	 * @param type Type of the pixel data
	 * @param level Mipamp level to modify with the given pixel data
	 * @param offset Offset from the beginning of the texture to start modifying with the given data
	 * @param width Width from the offset to modify with the given data
	 */
	public void subImage(ShortBuffer pixels, BaseFormat format, TexDataType type, int level, int offset, int width){
		if(!iformat.isCompressedFormat()){
			glTextureSubImage1D(id, level, 0, width, format.value, type.value, pixels);
		}
	}

	/**
	 * Buffers pixel data provided with the given <code>format</code> and <code>type</code> for the data to be read by the GPU. The pixel data is applied to the subimage of
	 * the texture defined by <code>offset</code> and <code>width</code>. If the texture's internal format is a compressed type function does nothing,
	 * to buffer data to a compressed format type use @link{#subImage(ByteBuffer, BaseFormat, TexDataType, int, int, int) bufferData}.
	 * 
	 * @param pixels Pixel data to pass to the GPU
	 * @param format Format of the pixel data
	 * @param type Type of the pixel data
	 * @param level Mipamp level to modify with the given pixel data
	 * @param offset Offset from the beginning of the texture to start modifying with the given data
	 * @param width Width from the offset to modify with the given data
	 */
	public void subImage(IntBuffer pixels, BaseFormat format, TexDataType type, int level, int offset, int width){
		if(!iformat.isCompressedFormat()){
			glTextureSubImage1D(id, level, 0, width, format.value, type.value, pixels);
		}
	}

	/**
	 * Buffers pixel data provided with the given <code>format</code> and <code>type</code> for the data to be read by the GPU. The pixel data is applied to the subimage of
	 * the texture defined by <code>offset</code> and <code>width</code>. If the texture's internal format is a compressed type function does nothing,
	 * to buffer data to a compressed format type use @link{#subImage(ByteBuffer, BaseFormat, TexDataType, int, int, int) bufferData}.
	 * 
	 * @param pixels Pixel data to pass to the GPU
	 * @param format Format of the pixel data
	 * @param type Type of the pixel data
	 * @param level Mipamp level to modify with the given pixel data
	 * @param offset Offset from the beginning of the texture to start modifying with the given data
	 * @param width Width from the offset to modify with the given data
	 */
	public void subImage(FloatBuffer pixels, BaseFormat format, TexDataType type, int level, int offset, int width){
		if(!iformat.isCompressedFormat()){
			glTextureSubImage1D(id, level, 0, width, format.value, type.value, pixels);
		}
	}

	/**
	 * Buffers pixel data provided with the given <code>format</code> and <code>type</code> for the data to be read by the GPU. The pixel data is applied to the subimage of
	 * the texture defined by <code>offset</code> and <code>width</code>. If the texture's internal format is a compressed type function does nothing,
	 * to buffer data to a compressed format type use @link{#subImage(ByteBuffer, BaseFormat, TexDataType, int, int, int) bufferData}.
	 * 
	 * @param pixels Pixel data to pass to the GPU
	 * @param format Format of the pixel data
	 * @param type Type of the pixel data
	 * @param level Mipamp level to modify with the given pixel data
	 * @param offset Offset from the beginning of the texture to start modifying with the given data
	 * @param width Width from the offset to modify with the given data
	 */
	public void subImage(DoubleBuffer pixels, BaseFormat format, TexDataType type, int level, int offset, int width){
		if(!iformat.isCompressedFormat()){
			glTextureSubImage1D(id, level, 0, width, format.value, type.value, pixels);
		}
	}
	
}
