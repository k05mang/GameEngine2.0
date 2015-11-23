package textures;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static org.lwjgl.opengl.GL45.*;

public class Texture1DArray extends Texture implements ArrayTexture{

	private int width, length;
	
	public Texture1DArray(InternalFormat format, int width, int levels, int length) {
		super(TextureType._1D_ARRAY, format, levels);
		glTextureStorage2D(id, levels, format.value, width, length);
	}

	@Override
	public void bufferData(ByteBuffer pixels, BaseFormat format, TexDataType type, int index, int level)
			throws IndexOutOfBoundsException {
		if(index >= length){
			throw new IndexOutOfBoundsException("Texture array index greater than array length");
		}else if(iformat.isCompressedFormat()){
			glCompressedTextureSubImage2D(id, level, 0, 0, width, index, format.value, type.value, pixels);
		}else{
			glTextureSubImage2D(id, level, 0, 0, width, index, format.value, type.value, pixels);
		}
	}

	@Override
	public void bufferData(ShortBuffer pixels, BaseFormat format, TexDataType type, int index, int level)
			throws IndexOutOfBoundsException {
		//check if the index is out of bounds 
		if(index >= length || index < 0){
			throw new IndexOutOfBoundsException("Texture array index greater than array length\nindex:"+index+"\nlength:"+length);
		}else if(!iformat.isCompressedFormat()){
			glTextureSubImage2D(id, level, 0, 0, width, index, format.value, type.value, pixels);
		}
	}

	@Override
	public void bufferData(IntBuffer pixels, BaseFormat format, TexDataType type, int index, int level)
			throws IndexOutOfBoundsException {
		//check if the index is out of bounds 
		if(index >= length || index < 0){
			throw new IndexOutOfBoundsException("Texture array index greater than array length\nindex:"+index+"\nlength:"+length);
		}else if(!iformat.isCompressedFormat()){
			glTextureSubImage2D(id, level, 0, 0, width, index, format.value, type.value, pixels);
		}
	}

	@Override
	public void bufferData(FloatBuffer pixels, BaseFormat format, TexDataType type, int index, int level)
			throws IndexOutOfBoundsException {
		//check if the index is out of bounds 
		if(index >= length || index < 0){
			throw new IndexOutOfBoundsException("Texture array index greater than array length\nindex:"+index+"\nlength:"+length);
		}else if(!iformat.isCompressedFormat()){
			glTextureSubImage2D(id, level, 0, 0, width, index, format.value, type.value, pixels);
		}
	}

	@Override
	public void bufferData(DoubleBuffer pixels, BaseFormat format, TexDataType type, int index, int level)
			throws IndexOutOfBoundsException {
		//check if the index is out of bounds 
		if(index >= length || index < 0){
			throw new IndexOutOfBoundsException("Texture array index greater than array length\nindex:"+index+"\nlength:"+length);
		}else if(!iformat.isCompressedFormat()){
			glTextureSubImage2D(id, level, 0, 0, width, index, format.value, type.value, pixels);
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
		return 0;
	}
}
