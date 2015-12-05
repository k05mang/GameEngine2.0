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
	
	/**
	 * Constructs a texture array of {@code length} two dimensional textures each with the given 
	 * {@code InternalFormat}, mipmap {@code levels}, {@code width} and {@code height}.
	 * 
	 * @param format Internal format of the textures in the texture array
	 * @param levels Mipmap levels for each of the textures in the array
	 * @param width Width of the textures in the array
	 * @param height Height of the textures in the array
	 * @param length Number of textures in the array
	 */
	public Texture2DArray(InternalFormat format, int levels, int width, int height, int length) {
		super(TextureType._2D_ARRAY, format, levels);
		this.width = width;
		this.height = height;
		this.length = length;
		glTextureStorage3D(id, levels_samples, format.value, width, height, length);
	}

	@Override
	public void bufferData(Buffer pixels, BaseFormat format, TexDataType type, int level){
		subImage(pixels, format, type, level, 0, length, 0, 0, width, height);
	}
	
	@Override
	public void bufferData(Buffer pixels, BaseFormat format, TexDataType type, int index, int level) throws IndexOutOfBoundsException {
		subImage(pixels, format, type, level, index, 1, 0, 0, width, height);
	}
	
	@Override
	public void bufferData(Buffer pixels, BaseFormat format, TexDataType type, int baseIndex, int count, int level) throws IndexOutOfBoundsException {
		subImage(pixels, format, type, level, baseIndex, count, 0, 0, width, height);
	}
	
	/**
	 * Buffers the given pixel data to the GPU, {@code format} and {@code type} specify how the GPU will read the pixel data.
	 * The pixel data will be applied to the given mipmap {@code level} of all textures in the texture array and will start 
	 * at {@code xoffset} and {@code yoffset} from the lower left corner of the texture, and will cover an area defined by 
	 * {@code width} and {@code height} from the offset point. If the texture has a compressed internal format then a 
	 * {@code ByteBuffer} must be provided, all other buffer types will be ignored.
	 * 
	 * @param pixels Buffer containing the image data to send to the GPU
	 * @param format Format of the pixel data being sent
	 * @param type Type of the pixel data being sent
	 * @param level Mipmap level of the texture at the given index in the array to modify
	 * @param xoffset X offset from the lower left corner of the texture to start modifying
	 * @param yoffset Y offset from the lower left corner of the texture to start modifying
	 * @param width Width of the subimage to modify
	 * @param height Height of the subimage to modify
	 */
	public void subImage(Buffer pixels, BaseFormat format, TexDataType type, int level, int xoffset, int yoffset, int width, int height){
		subImage(pixels, format, type, level, 0, length, xoffset, yoffset, width, height);
	}
	
	/**
	 * Buffers the given pixel data to the GPU, {@code format} and {@code type} specify how the GPU will read the pixel data.
	 * The pixel data will be applied to the given mipmap {@code level} of the texture at {@code index} of the texture array 
	 * and will start at {@code xoffset} and {@code yoffset} from the lower left corner of the texture, and will cover an area 
	 * defined by {@code width} and {@code height} from the offset point. If the texture has a compressed internal format then a 
	 * {@code ByteBuffer} must be provided, all other buffer types will be ignored.
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
		if(index > length || index < 0){
			throw new IndexOutOfBoundsException("Texture array index out of bounds\nindex:"+index+"\nlength:"+length);
		}else{
			subImage(pixels, format, type, index, 1, level, xoffset, yoffset, width, height);
		}
	}
	
	/**
	 * Buffers the given pixel data to the GPU, {@code format} and {@code type} specify how the GPU will read the pixel data.
	 * The pixel data will be applied to the given mipmap {@code level} at {@code baseIndex} through {@code baseIndex+count} 
	 * of the texture array and will start at {@code xoffset} and {@code yoffset} from the lower left corner of the texture, and 
	 * will cover an area defined by {@code width} and {@code height} from the offset point. If the texture has a compressed internal 
	 * format then a {@code ByteBuffer} must be provided, all other buffer types will be ignored.
	 * 
	 * @param pixels Buffer containing the image data to send to the GPU
	 * @param format Format of the pixel data being sent
	 * @param type Type of the pixel data being sent
	 * @param baseIndex Index to start modify textures in the array
	 * @param count Number of textures from {@code baseIndex} to start modifying
	 * @param level Mipmap level of the texture at the given index in the array to modify
	 * @param xoffset X offset from the lower left corner of the texture to start modifying
	 * @param yoffset Y offset from the lower left corner of the texture to start modifying
	 * @param width Width of the subimage to modify
	 * @param height Height of the subimage to modify
	 * @throws IndexOutOfBoundsException
	 */
	public void subImage(Buffer pixels, BaseFormat format, TexDataType type, int baseIndex, int count, int level, int xoffset, int yoffset, int width, int height) throws IndexOutOfBoundsException {
		if(baseIndex < 0 || baseIndex+count > length){
			throw new IndexOutOfBoundsException("Texture array index range out of bounds\nbaseIndex:"+baseIndex+"\nrange upper bound:"+baseIndex+count+"\nlength:"+length);
		}else if(!iformat.isCompressedFormat()){
			if(pixels instanceof ByteBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, baseIndex, width, height, count, format.value, type.value, (ByteBuffer)pixels);
			}else if(pixels instanceof ShortBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, baseIndex, width, height, count, format.value, type.value, (ShortBuffer)pixels);
			}else if(pixels instanceof IntBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, baseIndex, width, height, count, format.value, type.value, (IntBuffer)pixels);
			}else if(pixels instanceof FloatBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, baseIndex, width, height, count, format.value, type.value, (FloatBuffer)pixels);
			}else if(pixels instanceof DoubleBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, baseIndex, width, height, count, format.value, type.value, (DoubleBuffer)pixels);
			}
		}else{
			if(pixels instanceof ByteBuffer){
				glCompressedTextureSubImage3D(id, level, xoffset, yoffset, baseIndex, width, height, count, format.value, type.value, (ByteBuffer)pixels);
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
