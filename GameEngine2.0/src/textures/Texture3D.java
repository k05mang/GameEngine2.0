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

public class Texture3D extends Texture implements BasicTexture {

	private int width, height, depth;
	
	/**
	 * Constructs a three dimensional texture with the given {@code InternalFormat}, mipmap {@code levels},
	 * {@code width}, {@code height}, and {@code depth}.
	 * 
	 * @param format Internal format of the texture
	 * @param levels Mipmap levels of the texture
	 * @param width Width of the texture
	 * @param height Height of the texture
	 * @param depth Depth of the texture
	 */
	public Texture3D(InternalFormat format, int levels, int width, int height, int depth) {
		super(TextureType._3D, format, levels);
		this.width = width;
		this.height = height;
		this.depth = depth;
		glTextureStorage3D(id, levels_samples, format.value, width, height, depth);
	}

	@Override
	public void bufferData(Buffer pixels, BaseFormat format, TexDataType type, int level) {
		subImage(pixels, format, type, level, 0,0,0, width, height, depth);
	}
	
	/**
	 * Buffers the given pixel data to the GPU, {@code format} and {@code type} specify how the GPU will read the pixel data.
	 * The pixel data will be applied to the given mipmap {@code level} of the texture and will start at {@code xoffset} and 
	 * {@code yoffset} from the lower left corner of the texture, and {@code zoffset} from the back of the texture. The data 
	 * will be applied to an area defined by {@code width}, {@code height}, {@code depth} from the offset point. If the 
	 * texture has a compressed internal format then a {@code ByteBuffer} must be provided, all other buffer types will be ignored.
	 * 
	 * @param pixels Pixel data to be sent to the GPU
	 * @param format Format of the pixel data being sent
	 * @param type Type of the pixel data being sent
	 * @param level Mipmap level of the texture to apply the pixel data to
	 * @param xoffset X offset to start modifying the texture from
	 * @param yoffset Y offset to start modifying the texture from 
	 * @param zoffset Z offset to start modifying the texture from
	 * @param width Width from {@code xoffset} to start modifying
	 * @param height Height from {@code Yoffset} to start modifying
	 * @param depth Depth from {@code Zoffset} to start modifying
	 */
	public void subImage(Buffer pixels, BaseFormat format, TexDataType type, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth){
		if(!iformat.isCompressedFormat()){
			if(pixels instanceof ByteBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, zoffset, width, height, depth, format.value, type.value, (ByteBuffer)pixels);
			}else if(pixels instanceof ShortBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, zoffset, width, height, depth, format.value, type.value, (ShortBuffer)pixels);
			}else if(pixels instanceof IntBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, zoffset, width, height, depth, format.value, type.value, (IntBuffer)pixels);
			}else if(pixels instanceof FloatBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, zoffset, width, height, depth, format.value, type.value, (FloatBuffer)pixels);
			}else if(pixels instanceof DoubleBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, zoffset, width, height, depth, format.value, type.value, (DoubleBuffer)pixels);
			}
		}else{
			if(pixels instanceof ByteBuffer){
				glCompressedTextureSubImage3D(id, level, xoffset, yoffset, zoffset, width, height, depth, format.value, type.value, (ByteBuffer)pixels);
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
	
	@Override
	public int getDepth(){
		return depth;
	}
}
