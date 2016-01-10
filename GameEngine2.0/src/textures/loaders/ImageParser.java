package textures.loaders;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;

import org.lwjgl.BufferUtils;

import textures.enums.BaseFormat;
import textures.enums.TexDataType;

public abstract class ImageParser {
	
	protected int width, height;
	protected BaseFormat format;
	protected TexDataType type;
	protected DataInputStream imageStream;
	
	/**
	 * Constructs an instance of an ImageParser using the given {@code file} as the image target
	 * 
	 * @param file File to parse into raw image data for use with OpenGL
	 * @throws FileNotFoundException
	 */
	ImageParser(File file) throws IOException{
		imageStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		//setup default information
		format = BaseFormat.RGBA;
		type = TexDataType.UBYTE;
	}

	/**
	 * Width, in pixels, of the image being processed by an instance of ImageParser
	 * 
	 * @return Width of this image as an integer
	 */
	public int getWidth(){
		return width;
	}
	
	/**
	 * Height, in pixels, of the image being processed by an instance of ImageParser
	 * 
	 * @return Height of this image as an integer
	 */
	public int getHeight(){
		return height;
	}
	
	/**
	 * Gets the format of the image data that results from a call to @link{#parse() parse}
	 * 
	 * @return BaseFormat of the data that will result from a call to @link{#parse() parse}
	 */
	public BaseFormat getFormat(){
		return format;
	}
	
	/**
	 * Gets the texture data type that will result from a call to @link{#parse() parse}
	 * 
	 * @return TexDataType of the data that will result from a call to @link{#parse() parse}
	 */
	public TexDataType getType(){
		return type;
	}
	
	/**
	 * Parses the image file and extracts the image from the file, storing it into a ByteBuffer that is
	 * compatible with OpenGL. No texture flipping is needed with the ByteBuffer generated from this
	 * function. This function should only be called once per object instance, consecutive calls to this function on the same
	 * instance of an object will result in errors.
	 * 
	 * @return ByteBuffer with the raw image data that is compliant with OpenGL textures
	 * @throws IOException
	 * @throws DataFormatException
	 */
	public abstract ByteBuffer parse() throws IOException, DataFormatException;
	
	/**
	 * Gets a default image constructed procedurally with the given dimensions and compatibility
	 * with cube maps. The image data resulting from a call to this function will be a black and white checkerboard image
	 * 
	 * @param width Width of the procedural image to create
	 * @param height Height of the procedural image to create
	 * @param cubeMap Whether the image should have 6 images to fill the space of a cube map
	 * @param isRGBA Whether the image should be RGBA compliant, meaning the final image will have an alpha channel 
	 * @return ByteBuffer containing the checkerboard image of the given {@code width} and {@code height}
	 */
	static ByteBuffer getDefault(int width, int height, boolean cubeMap, boolean isRGBA){
		int cubeMapFactor = cubeMap ? 6 : 1;
		ByteBuffer pixels = BufferUtils.createByteBuffer(width*height*(isRGBA ? 4 : 3)*cubeMapFactor);
		//loop control for generating cube map faces if required otherwise only 1 image is generated
		for(int cubeFace = 0; cubeFace < cubeMapFactor; cubeFace++){
			for(int curRow = 0; curRow < height; curRow++){
				for(int curCol = 0; curCol < width; curCol++){
					float colorMod = (curRow+curCol)%2 == 0 ? .35f : 1f;
					//use an alternating checkerboard pattern
					pixels.put((byte)(colorMod*175));
					pixels.put((byte)(colorMod*175));
					pixels.put((byte)(colorMod*175));
					if(isRGBA){
						pixels.put((byte)255);
					}
				}
			}
		}
		pixels.flip();
		return pixels;
	}
}
