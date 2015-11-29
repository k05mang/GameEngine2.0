package textures;

import static org.lwjgl.opengl.GL45.*;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.Buffer;

public class TextureCubeMap extends Texture implements BasicTexture {

	private int dimension;//since the texture needs to be square only use a single dimension field
	
	public TextureCubeMap(InternalFormat format, int levels, int dimension) {
		super(TextureType.CUBE_MAP, format, levels);
		this.dimension = dimension;
		glTextureStorage2D(id, levels_samples, format.value, dimension, dimension);
	}

	@Override
	public void bufferData(Buffer pixels, BaseFormat format, TexDataType type, int level) {
		subImage(pixels, format, type, level, CubeMapFace.POS_X, CubeMapFace.NEG_Z, 0, 0, dimension, dimension);
	}
	
	public void bufferData(Buffer pixels, BaseFormat format, TexDataType type, int level, CubeMapFace face) {
		subImage(pixels, format, type, level, face, face, 0, 0, dimension, dimension);
	}
	
	public void bufferData(Buffer pixels, BaseFormat format, TexDataType type, int level, CubeMapFace start, CubeMapFace end) {
		subImage(pixels, format, type, level, start, end, 0, 0, dimension, dimension);
	}

	public void subImage(Buffer pixels, BaseFormat format, TexDataType type, int level, int xoffset, int yoffset, int width, int height){
		subImage(pixels, format, type, level, CubeMapFace.POS_X, CubeMapFace.NEG_Z, xoffset, yoffset, width, height);
	}

	public void subImage(Buffer pixels, BaseFormat format, TexDataType type, int level, CubeMapFace face, int xoffset, int yoffset, int width, int height){
		subImage(pixels, format, type, level, face, face, xoffset, yoffset, width, height);
	}

	public void subImage(Buffer pixels, BaseFormat format, TexDataType type, int level, CubeMapFace start, CubeMapFace end, int xoffset, int yoffset, int width, int height)/*throws cubemaprangeexception*/{
		if(start.layer > end.layer){
			//TODO possibly through an exception here
		}else if(!iformat.isCompressedFormat()){
			//end.layer-start.layer+1 this guarantees that at least 1 image from the offset will be modified
			if(pixels instanceof ByteBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, start.layer, width, height, end.layer-start.layer+1, format.value, type.value, (ByteBuffer)pixels);
			}else if(pixels instanceof ShortBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, start.layer, width, height, end.layer-start.layer+1, format.value, type.value, (ShortBuffer)pixels);
			}else if(pixels instanceof IntBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, start.layer, width, height, end.layer-start.layer+1, format.value, type.value, (IntBuffer)pixels);
			}else if(pixels instanceof FloatBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, start.layer, width, height, end.layer-start.layer+1, format.value, type.value, (FloatBuffer)pixels);
			}else if(pixels instanceof DoubleBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, start.layer, width, height, end.layer-start.layer+1, format.value, type.value, (DoubleBuffer)pixels);
			}
		}else{
			if(pixels instanceof ByteBuffer){
				glCompressedTextureSubImage3D(id, level, xoffset, yoffset, start.layer, width, height, end.layer-start.layer+1, format.value, type.value, (ByteBuffer)pixels);
			}//else do nothing
		}
	}
	
	@Override
	public int getWidth(){
		return dimension;
	}
	
	@Override
	public int getHeight(){
		return dimension;
	}
}
