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

public class TextureCubeMapArray extends Texture implements ArrayTexture {

	private int dimension, length, numTextures;
	
	/**
	 * Constructs a texture array of {@code length} cube map textures each with the given {@code InternalFormat}, and
	 * mipmap {@code levels}. {@code dimension} defines the width, height, and depth of each cube map texture in the array.
	 * 
	 * @param format Internal format of the cube map texture to store pixel data
	 * @param levels Mipmap levels for the cube map textures
	 * @param dimension Dimension of the cube map textures
	 * @param length Number of cube map textures in the array
	 */
	public TextureCubeMapArray(InternalFormat format, int levels, int dimension, int length) {
		super(TextureType.CUBE_MAP_ARRAY, format, levels);
		this.dimension = dimension;
		this.length = length;
		numTextures = 6*length;
		glTextureStorage3D(id, levels_samples, format.value, dimension, dimension, numTextures);
	}

	@Override
	public void bufferData(Buffer pixels, BaseFormat format, TexDataType type, int level) throws IndexOutOfBoundsException {
		subImage(pixels, format, type, 0, numTextures, level, 0, 0, dimension, dimension);
	}

	@Override
	public void bufferData(Buffer pixels, BaseFormat format, TexDataType type, int index, int level) throws IndexOutOfBoundsException {
		subImage(pixels, format, type, index, 6, level, 0, 0, dimension, dimension);
	}

	@Override
	public void bufferData(Buffer pixels, BaseFormat format, TexDataType type, int baseIndex, int count, int level) throws IndexOutOfBoundsException {
		subImage(pixels, format, type, baseIndex, 6*count, level, 0, 0, dimension, dimension);
	}

	/**
	 * Buffers pixel data with the given {@code format} and {@code type} to the given mipmap {@code level}
	 * of all the faces of the cube map at {@code index} defined from {@code start} to {@code end} inclusive. {@code start} 
	 * must be a cube map face whose layer index is less than {@code end}'s. If the texture has a compressed internal format then a 
	 * {@code ByteBuffer} must be provided, all other buffer types will be ignored.
	 * 
	 * @param pixels Pixel data to send to the GPU
	 * @param format Format of the pixel data
	 * @param type Type of the pixel data
	 * @param index Index of the texture in the texture array to modify
	 * @param level Mipmap level to modify of the texture at {@code index}
	 * @param start Starting cube map face to modify of the texture at the specified {@code index}
	 * @param end Last cube map face to modify of the texture at the specified {@code index}
	 * @throws IndexOutOfBoundsException
	 */
	public void bufferData(Buffer pixels, BaseFormat format, TexDataType type, int index, int level, CubeMapFace start, CubeMapFace end) throws IndexOutOfBoundsException {
		subImage(pixels, format, type, index, level, start, end, 0, 0, dimension, dimension);
	}
	
	/**
	 * Buffers pixel data with the given {@code format} and {@code type} to the given mipmap {@code level}
	 * of all the faces of the cube map at {@code index} defined from {@code start} to {@code end} inclusive. {@code start} 
	 * must be a cube map face whose layer index is less than {@code end}'s. {@code xoffset} and {@code yoffset} define a 
	 * beginning offset point to start modifying each cube map face, and {@code width} and {@code height} define the area of the 
	 * face to modify. If the texture has a compressed internal format then a {@code ByteBuffer} must be provided, all other buffer types will be ignored.
	 * 
	 * @param pixels Pixel data to send to the GPU
	 * @param format Format of the pixel data
	 * @param type Type of the pixel data
	 * @param index Index of the texture in the texture array to modify
	 * @param level Mipmap level to modify of the texture at {@code index}
	 * @param start Starting cube map face to modify of the texture at the specified {@code index}
	 * @param end Last cube map face to modify of the texture at the specified {@code index}
	 * @param xoffset X offset to start modifying each face of the cube map from
	 * @param yoffset Y offset to start modifying each face of the cube map from
	 * @param width Width of the area of each cube map face to modify
	 * @param height Height of the area of each cube map face to modify
	 * @throws IndexOutOfBoundsException
	 */
	public void subImage(Buffer pixels, BaseFormat format, TexDataType type, int index, int level, CubeMapFace start, CubeMapFace end, int xoffset, int yoffset, int width, int height) 
			throws IndexOutOfBoundsException{
		if(index < 0 || index >= length){
			throw new IndexOutOfBoundsException("Texture array index out of bounds\nindex:"+index+"\nlength:"+length);
		}else if(!iformat.isCompressedFormat()){
			if(pixels instanceof ByteBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, 6*index+start.layer, width, height, end.layer-start.layer+1, format.value, type.value, (ByteBuffer)pixels);
			}else if(pixels instanceof ShortBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, 6*index+start.layer, width, height, end.layer-start.layer+1, format.value, type.value, (ShortBuffer)pixels);
			}else if(pixels instanceof IntBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, 6*index+start.layer, width, height, end.layer-start.layer+1, format.value, type.value, (IntBuffer)pixels);
			}else if(pixels instanceof FloatBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, 6*index+start.layer, width, height, end.layer-start.layer+1, format.value, type.value, (FloatBuffer)pixels);
			}else if(pixels instanceof DoubleBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, 6*index+start.layer, width, height, end.layer-start.layer+1, format.value, type.value, (DoubleBuffer)pixels);
			}
		}else{
			if(pixels instanceof ByteBuffer){
				glCompressedTextureSubImage3D(id, level, xoffset, yoffset, 6*index+start.layer, width, height, end.layer-start.layer+1, format.value, type.value, (ByteBuffer)pixels);
			}//else do nothing
		}
	}
	
	/**
	 * Buffers pixel data with the given {@code format} and {@code type} to the given mipmap {@code level} of all the faces of the cube maps from 
	 * {@code baseIndex} through {@code baseIndex+count}. {@code xoffset} and {@code yoffset} define a beginning offset point to start modifying 
	 * each cube map face, and {@code width} and {@code height} define the area of the face to modify. If the texture has a compressed internal format then a 
	 * {@code ByteBuffer} must be provided, all other buffer types will be ignored.
	 * 
	 * @param pixels Pixel data to send to the GPU
	 * @param format Format of the pixel data
	 * @param type Type of the pixel data
	 * @param index Index of the texture in the texture array to modify
	 * @param level Mipmap level to modify of the texture at {@code index}
	 * @param xoffset X offset to start modifying each face of the cube map from
	 * @param yoffset Y offset to start modifying each face of the cube map from
	 * @param width Width of the area of each cube map face to modify
	 * @param height Height of the area of each cube map face to modify
	 * @throws IndexOutOfBoundsException
	 */
	public void subImage(Buffer pixels, BaseFormat format, TexDataType type, int baseIndex, int count, int level, int xoffset, int yoffset, int width, int height) throws IndexOutOfBoundsException{
		if(baseIndex < 0 || baseIndex+count > length){
			throw new IndexOutOfBoundsException("Texture array index range out of bounds\nbaseIndex:"+baseIndex+"\nrange upper bound:"+baseIndex+count+"\nlength:"+length);
		}else if(!iformat.isCompressedFormat()){
			if(pixels instanceof ByteBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, 6*baseIndex, width, height, 6*count, format.value, type.value, (ByteBuffer)pixels);
			}else if(pixels instanceof ShortBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, 6*baseIndex, width, height, 6*count, format.value, type.value, (ShortBuffer)pixels);
			}else if(pixels instanceof IntBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, 6*baseIndex, width, height, 6*count, format.value, type.value, (IntBuffer)pixels);
			}else if(pixels instanceof FloatBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, 6*baseIndex, width, height, 6*count, format.value, type.value, (FloatBuffer)pixels);
			}else if(pixels instanceof DoubleBuffer){
				glTextureSubImage3D(id, level, xoffset, yoffset, 6*baseIndex, width, height, 6*count, format.value, type.value, (DoubleBuffer)pixels);
			}
		}else{
			if(pixels instanceof ByteBuffer){
				glCompressedTextureSubImage3D(id, level, xoffset, yoffset, 6*baseIndex, width, height, 6*count, format.value, type.value, (ByteBuffer)pixels);
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
