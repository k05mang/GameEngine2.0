package textures;

import static org.lwjgl.opengl.GL45.*;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.Buffer;

import textures.enums.BaseFormat;
import textures.enums.InternalFormat;
import textures.enums.TexDataType;
import textures.enums.TextureType;

public class TextureRectangle extends Texture implements BasicTexture {

	private int width, height;
	
	/**
	 * Constructs a rectangular texture with the given {@code InternalFormat}, {@code width}, and {@code height}.
	 * Since Rectanle texture types cannot have mipmaps the default level will be 0 and calling {@code genMipMaps} does
	 * nothing.
	 * 
	 * @param format Internal format for the given texture to store it's pixel data as
	 * @param width Width of the texture
	 * @param height Height of the texture
	 */
	public TextureRectangle(InternalFormat format, int width, int height) {
		super(TextureType.RECTANGLE, format, 1);
		this.width = width;
		this.height = height;
		glTextureStorage2D(id, 1, format.value, width, height);
	}

	@Override
	public void bufferData(Buffer pixels, BaseFormat format, TexDataType type, int level) {
		subImage(pixels, format, type, 0, 0, width, height);
	}
	
	/**
	 * Buffers the given pixel data to the GPU, {@code format} and {@code type} specify how the GPU will read the pixel data.
	 * The pixel data will start at {@code xoffset} and {@code yoffset} from the lower left corner of the texture, and will 
	 * cover an area defined by {@code width} and {@code height} from the offset point. If the texture has a compressed internal 
	 * format then a {@code ByteBuffer} must be provided, all other buffer types will be ignored.
	 * 
	 * @param pixels Pixel data to be sent to the GPU
	 * @param format Format of the pixel data being sent
	 * @param type Type of the pixel data being sent
	 * @param xoffset X offset from the lower left corner of the texture to start modifying
	 * @param yoffset Y offset from the lower left corner of the texture to start modifying
	 * @param width Width from the offset point to start modifying
	 * @param height Height from the offset point to start modifying
	 */
	public void subImage(Buffer pixels, BaseFormat format, TexDataType type, int xoffset, int yoffset, int width, int height){
		if(!iformat.isCompressedFormat()){
			if(pixels instanceof ByteBuffer){
				glTextureSubImage2D(id, 0, xoffset, yoffset, width, height, format.value, type.value, (ByteBuffer)pixels);
			}else if(pixels instanceof ShortBuffer){
				glTextureSubImage2D(id, 0, xoffset, yoffset, width, height, format.value, type.value, (ShortBuffer)pixels);
			}else if(pixels instanceof IntBuffer){
				glTextureSubImage2D(id, 0, xoffset, yoffset, width, height, format.value, type.value, (IntBuffer)pixels);
			}else if(pixels instanceof FloatBuffer){
				glTextureSubImage2D(id, 0, xoffset, yoffset, width, height, format.value, type.value, (FloatBuffer)pixels);
			}else if(pixels instanceof DoubleBuffer){
				glTextureSubImage2D(id, 0, xoffset, yoffset, width, height, format.value, type.value, (DoubleBuffer)pixels);
			}
		}else{
			if(pixels instanceof ByteBuffer){
				glCompressedTextureSubImage2D(id, 0, xoffset, yoffset, width, height, format.value, type.value, (ByteBuffer)pixels);
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
