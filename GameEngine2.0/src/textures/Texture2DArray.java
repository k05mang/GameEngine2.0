package textures;

import static org.lwjgl.opengl.GL45.*;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.Buffer;

public class Texture2DArray extends Texture implements ArrayTexture {

	private int width, height, length;
	
	public Texture2DArray(InternalFormat format, int levels, int width, int height, int length) {
		super(TextureType._2D_ARRAY, format, levels);
		this.width = width;
		this.height = height;
		this.length = length;
		glTextureStorage3D(id, levels_samples, format.value, width, height, length);
	}

	@Override
	public void bufferData(Buffer pixels, BaseFormat format, TexDataType type, int index, int level) throws IndexOutOfBoundsException {
		subImage(pixels, format, type, index, level, 0, 0, width, height);
	}
	
	/**
	 * Buffers the given pixel data to the GPU, <code>format</code> and <code>type</code> specify how the GPU will read the pixel data.
	 * The pixel data will be applied to the given mipmap <code>level</code> and <code>index</code> of the texture array and will start 
	 * at <code>xoffset</code> and <code>yoffset</code> from the lower left corner of the texture, and will cover an area defined by 
	 * <code>width</code> and <code>height</code> from the offset point. If the texture has a compressed internal format then a 
	 * <code>ByteBuffer</code> must be provided, all other buffer types will be ignored.
	 * 
	 * @param pixels Buffer containing the image data to send to the GPU
	 * @param format Format of the pixel data being sent
	 * @param type Type of the pixel data being sent
	 * @param index Index of the texture in the array to modify
	 * @param level Mipmap level of the texture at the given index in the array to modify
	 * @param xoffset X offset from the lower left corner of the texture to start modifying
	 * @param yoffset Y offset from the lower left corner of the texture to start modifying
	 * @param width Width of the subimage to modify
	 * @param height Height of the subimage to modify
	 * @throws IndexOutOfBoundsException
	 */
	public void subImage(Buffer pixels, BaseFormat format, TexDataType type, int index, int level, int xoffset, int yoffset, int width, int height) throws IndexOutOfBoundsException {
		if(index >= length || index < 0){
			throw new IndexOutOfBoundsException("Texture array index out of bounds\nindex:"+index+"\nlength:"+length);
		}else if(!iformat.isCompressedFormat()){
			if(pixels instanceof ByteBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, 0, width, height, index, format.value, type.value, (ByteBuffer)pixels);
			}else if(pixels instanceof ShortBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, 0, width, height, index, format.value, type.value, (ShortBuffer)pixels);
			}else if(pixels instanceof IntBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, 0, width, height, index, format.value, type.value, (IntBuffer)pixels);
			}else if(pixels instanceof FloatBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, 0, width, height, index, format.value, type.value, (FloatBuffer)pixels);
			}else if(pixels instanceof DoubleBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, 0, width, height, index, format.value, type.value, (DoubleBuffer)pixels);
			}
		}else{
			if(pixels instanceof ByteBuffer){
				glCompressedTextureSubImage3D(id, level, xoffset, yoffset, 0, width, height, index, format.value, type.value, (ByteBuffer)pixels);
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
		return height;
	}
}
