package textures;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import static org.lwjgl.opengl.GL45.*;

public class Texture1D extends Texture implements BasicTexture{
	private int width;
	
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
	 * ......If the texture's internal format is a 
	 * compressed type this version of the <code>bufferData</code> function does nothing.
	 * @param pixels
	 * @param format
	 * @param type
	 * @param level
	 * @param offset
	 * @param width
	 */
	public void subImage(ByteBuffer pixels, BaseFormat format, TexDataType type, int level, int offset, int width){
		if(iformat.isCompressedFormat()){
			glCompressedTextureSubImage1D(id, level, 0, width, format.value, type.value, pixels);
		}else{
			glTextureSubImage1D(id, level, 0, width, format.value, type.value, pixels);
		}
	}
	
	public void subImage(ShortBuffer pixels, BaseFormat format, TexDataType type, int level, int offset, int width){
		if(!iformat.isCompressedFormat()){
			glTextureSubImage1D(id, level, 0, width, format.value, type.value, pixels);
		}
	}
	
	public void subImage(IntBuffer pixels, BaseFormat format, TexDataType type, int level, int offset, int width){
		if(!iformat.isCompressedFormat()){
			glTextureSubImage1D(id, level, 0, width, format.value, type.value, pixels);
		}
	}
	
	public void subImage(FloatBuffer pixels, BaseFormat format, TexDataType type, int level, int offset, int width){
		if(!iformat.isCompressedFormat()){
			glTextureSubImage1D(id, level, 0, width, format.value, type.value, pixels);
		}
	}
	
	public void subImage(DoubleBuffer pixels, BaseFormat format, TexDataType type, int level, int offset, int width){
		if(!iformat.isCompressedFormat()){
			glTextureSubImage1D(id, level, 0, width, format.value, type.value, pixels);
		}
	}
	
}
