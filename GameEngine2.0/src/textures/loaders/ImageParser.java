package textures.loaders;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

import textures.enums.BaseFormat;
import textures.enums.TexDataType;

public abstract class ImageParser {
	
	int width, height;
	BaseFormat format;
	TexDataType type;
	DataInputStream imageStream;
	
	ImageParser(File file) throws FileNotFoundException{
		imageStream = new DataInputStream(new FileInputStream(file));
		//setup default information
		format = BaseFormat.RGBA;
		type = TexDataType.UBYTE;
	}
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public BaseFormat getFormat(){
		return format;
	}
	
	public TexDataType getType(){
		return type;
	}
	
	public abstract ByteBuffer parse();
	
	static ByteBuffer getDefault(int width, int height, boolean cubeMap){
		int cubeMapFactor = cubeMap ? 6 : 1;
		ByteBuffer pixels = BufferUtils.createByteBuffer(width*height*4*cubeMapFactor);
		for(int curRow = 0; curRow < height*cubeMapFactor; curRow++){
			for(int curCol = 0; curCol < width*cubeMapFactor; curCol++){
				int colorMod = (curRow+curCol)%2;
				//use an alternating checkerboard pattern
				pixels.put((byte)(colorMod*127));
				pixels.put((byte)(colorMod*127));
				pixels.put((byte)(colorMod*127));
				pixels.put((byte)1);
			}
		}
		pixels.flip();
		return pixels;
	}
}
