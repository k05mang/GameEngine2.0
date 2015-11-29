package textures;

import static org.lwjgl.opengl.GL45.*;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.Buffer;

public class TextureCubeMapArray extends Texture implements ArrayTexture {

	private int dimension, length;
	
	public TextureCubeMapArray(InternalFormat format, int levels, int dimension, int length) {
		super(TextureType.CUBE_MAP_ARRAY, format, levels);
		this.dimension = dimension;
		this.length = length;
		glTextureStorage3D(id, levels_samples, format.value, dimension, dimension, 6*length);
	}

	@Override
	public void bufferData(Buffer pixels, BaseFormat format, TexDataType type, int level) throws IndexOutOfBoundsException {
		// TODO Auto-generated method stub

	}

	@Override
	public void bufferData(Buffer pixels, BaseFormat format, TexDataType type, int index, int level) throws IndexOutOfBoundsException {
		// TODO Auto-generated method stub

	}

	@Override
	public void bufferData(Buffer pixels, BaseFormat format, TexDataType type, int baseIndex, int count, int level)
			throws IndexOutOfBoundsException {
		// TODO Auto-generated method stub

	}
	
	public void subImage(Buffer pixels, BaseFormat format, TexDataType type, int baseIndex, int count, int level, int xoffset, int yoffset, int width, int height) throws IndexOutOfBoundsException{
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
		return dimension;
	}

	@Override
	public int getHeight() {
		return dimension;
	}

}
