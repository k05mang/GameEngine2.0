package textures;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.Buffer;

import static org.lwjgl.opengl.GL45.*;

public class Texture1DArray extends Texture implements ArrayTexture{

	private int width, length;
	
	/**
	 * Constructs a texture array of one dimensional textures. Each texture will have the given <code>InternalFormat</code>,
	 * <code>width</code>, and mipmap <code>levels</code>, <code>length</code> will define the number of textures in the array.
	 * 
	 * @param format Internal GPU formatting of the texture data
	 * @param width Width of the textures defined in the array
	 * @param levels Mipmap levels to apply to each element in the texture array
	 * @param length Number of texture elements in the array
	 */
	public Texture1DArray(InternalFormat format, int width, int levels, int length) {
		super(TextureType._1D_ARRAY, format, levels);
		this.width = width;
		this.length = length;
		glTextureStorage2D(id, levels_samples, format.value, width, length);
	}

	@Override
	public void bufferData(Buffer pixels, BaseFormat format, TexDataType type, int index, int level)
			throws IndexOutOfBoundsException {
		subImage(pixels, format, type, index, level, 0, width);
	}
	
	/**
	 * Buffers the given pixel data to the texture area defined from <code>offset</code> through <code>width</code> at the given <code>index</code>
	 * and mipmap <code>level</code> of the texture array. The <code>format</code> and <code>type</code> tell the GPU how to read the pixel data.
	 * If the texture has a compressed internal format then a <code>ByteBuffer</code> must be provided, all other buffer types will be ignored.
	 * 
	 * @param pixels Pixel data to buffer to the texture on the GPU
	 * @param format Format of the pixel data being sent
	 * @param type Type of the pixel data being sent
	 * @param index Index of the texture in the array to modify
	 * @param level Mipmap level of the texture at the specified index in the array
	 * @param offset Offset from the start of the texture to start modifying from
	 * @param width Width of the area of the texture after offset to modify
	 * @throws IndexOutOfBoundsException
	 */
	public void subImage(Buffer pixels, BaseFormat format, TexDataType type, int index, int level, int offset, int width)
			throws IndexOutOfBoundsException {
		if(index >= length || index < 0){
			throw new IndexOutOfBoundsException("Texture array index out of bounds\nindex:"+index+"\nlength:"+length);
		}else if(!iformat.isCompressedFormat()){
			if(pixels instanceof ByteBuffer){
				glTextureSubImage2D(id, level, offset, 0, width, index, format.value, type.value, (ByteBuffer)pixels);
			}else if(pixels instanceof ShortBuffer){
				glTextureSubImage2D(id, level, offset, 0, width, index, format.value, type.value, (ShortBuffer)pixels);
			}else if(pixels instanceof IntBuffer){
				glTextureSubImage2D(id, level, offset, 0, width, index, format.value, type.value, (IntBuffer)pixels);
			}else if(pixels instanceof FloatBuffer){
				glTextureSubImage2D(id, level, offset, 0, width, index, format.value, type.value, (FloatBuffer)pixels);
			}else if(pixels instanceof DoubleBuffer){
				glTextureSubImage2D(id, level, offset, 0, width, index, format.value, type.value, (DoubleBuffer)pixels);
			}
		}else{
			if(pixels instanceof ByteBuffer){
				glCompressedTextureSubImage2D(id, level, offset, 0, width, index, format.value, type.value, (ByteBuffer)pixels);
			}//else do nothing
		}
	}

	@Override
	public int length() {
		return length;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return 1;
	}
}
