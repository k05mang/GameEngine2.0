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
	
	/**
	 * Constructs a cube map texture width the given <code>InternalFormat</code>, mipmap <code>levels</code>, and
	 * <code>dimension</code>, which determines the overall dimensions of the cube.
	 * 
	 * @param format Internal format the texture is stored
	 * @param levels Mipmap levels for the texture
	 * @param dimension Dimensions of the cube's width, height, and depth
	 */
	public TextureCubeMap(InternalFormat format, int levels, int dimension) {
		super(TextureType.CUBE_MAP, format, levels);
		this.dimension = dimension;
		glTextureStorage2D(id, levels_samples, format.value, dimension, dimension);
	}

	@Override
	public void bufferData(Buffer pixels, BaseFormat format, TexDataType type, int level) {
		subImage(pixels, format, type, level, CubeMapFace.POS_X, CubeMapFace.NEG_Z, 0, 0, dimension, dimension);
	}
	
	/**
	 * Buffers pixel data with the given <code>format</code> and <code>type</code> to the given mipmap <code>level</code>
	 * of the entire <code>face</code> of the cube map. If the texture has a compressed internal format then a <code>ByteBuffer</code> must
	 * be provided, all other buffer types will be ignored.
	 * 
	 * @param pixels Pixel data to send to the GPU
	 * @param format Format of the pixel data
	 * @param type Type of the pixel data
	 * @param level Mipmap level to modify of this texture
	 * @param face Cube map face to modify in this cube map
	 */
	public void bufferData(Buffer pixels, BaseFormat format, TexDataType type, int level, CubeMapFace face) {
		subImage(pixels, format, type, level, face, face, 0, 0, dimension, dimension);
	}
	
	/**
	 * Buffers pixel data with the given <code>format</code> and <code>type</code> to the given mipmap <code>level</code>
	 * to all the faces of the cube map defined from <code>start</code> to <code>end</code> inclusive. <code>start</code> 
	 * must be a cube map face whose layer index is less than <code>end</code>'s. If the texture has a compressed 
	 * internal format then a <code>ByteBuffer</code> must be provided, all other buffer types will be ignored.
	 * 
	 * @param pixels Pixel data to send to the GPU
	 * @param format Format of the pixel data
	 * @param type Type of the pixel data
	 * @param level Mipmap level to modify of this texture
	 * @param start Starting cube map face to modify
	 * @param end Last cube map face to modify
	 */
	public void bufferData(Buffer pixels, BaseFormat format, TexDataType type, int level, CubeMapFace start, CubeMapFace end) {
		subImage(pixels, format, type, level, start, end, 0, 0, dimension, dimension);
	}

	/**
	 * Buffers pixel data with the given <code>format</code> and <code>type</code> to the given mipmap <code>level</code>
	 * to all the faces of the cube map. <code>xoffset</code> and <code>yoffset</code> define a beginning offset point to start modifying 
	 * each cube map face, and <code>width</code> and <code>height</code> define the area of the face to modify. If the texture has a compressed 
	 * internal format then a <code>ByteBuffer</code> must be provided, all other buffer types will be ignored.
	 * 
	 * @param pixels Pixel data to send to the GPU
	 * @param format Format of the pixel data
	 * @param type Type of the pixel data
	 * @param level Mipmap level to modify of this texture
	 * @param xoffset X offset to start modifying each face of the cube map from
	 * @param yoffset Y offset to start modifying each face of the cube map from
	 * @param width Width of the area of each cube map face to modify
	 * @param height Height of the area of each cube map face to modify
	 */
	public void subImage(Buffer pixels, BaseFormat format, TexDataType type, int level, int xoffset, int yoffset, int width, int height){
		subImage(pixels, format, type, level, CubeMapFace.POS_X, CubeMapFace.NEG_Z, xoffset, yoffset, width, height);
	}

	/**
	 * Buffers pixel data with the given <code>format</code> and <code>type</code> to the given mipmap <code>level</code>
	 * to the given <code>face</code> of the cube map. <code>xoffset</code> and <code>yoffset</code> define a beginning offset point to start modifying 
	 * the cube map face, and <code>width</code> and <code>height</code> define the area of the face to modify. If the texture has a compressed 
	 * internal format then a <code>ByteBuffer</code> must be provided, all other buffer types will be ignored.
	 * 
	 * @param pixels Pixel data to send to the GPU
	 * @param format Format of the pixel data
	 * @param type Type of the pixel data
	 * @param level Mipmap level to modify of this texture
	 * @param face Cube map face to modify
	 * @param xoffset X offset to start modifying the face of the cube map from
	 * @param yoffset Y offset to start modifying the face of the cube map from
	 * @param width Width of the area of the cube map face to modify 
	 * @param height Height of the area of the cube map face to modify 
	 */
	public void subImage(Buffer pixels, BaseFormat format, TexDataType type, int level, CubeMapFace face, int xoffset, int yoffset, int width, int height){
		subImage(pixels, format, type, level, face, face, xoffset, yoffset, width, height);
	}

	/**
	 * Buffers pixel data with the given <code>format</code> and <code>type</code> to the given mipmap <code>level</code> to all the faces of the cube map 
	 * defined from <code>start</code> to <code>end</code> inclusive. <code>start</code> must be a cube map face whose layer index is less than <code>end</code>'s. 
	 * <code>xoffset</code> and <code>yoffset</code> define a beginning offset point to start modifying the cube map faces, and <code>width</code> and 
	 * <code>height</code> define the area of the face to modify. If the texture has a compressed internal format then a <code>ByteBuffer</code> must be provided, 
	 * all other buffer types will be ignored.
	 * 
	 * @param pixels Pixel data to send to the GPU
	 * @param format Format of the pixel data
	 * @param type Type of the pixel data
	 * @param level Mipmap level to modify of this texture
	 * @param start Starting cube map face to modify
	 * @param end Last cube map face to modify
	 * @param xoffset X offset to start modifying the faces of the cube map from
	 * @param yoffset Y offset to start modifying the faces of the cube map from
	 * @param width Width of the area of the cube map face to modify
	 * @param height Height of the area of the cube map face to modify
	 */
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
