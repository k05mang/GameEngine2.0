package textures.loaders;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

import textures.enums.BaseFormat;
import textures.enums.TexDataType;

abstract class PNGParser {
	public static TextureData parse(File file) throws IOException, FileFormatException{
		DataInputStream imageFile = new DataInputStream(new FileInputStream(file));
		imageFile.skipBytes(1);//skip the first byte since it has no relevance with modern systems
		//get the next 3 bytes that spell out PNG
		if(imageFile.readByte() != 0x50 || imageFile.readByte() != 0x4E || imageFile.readByte() != 0x47){
			throw new FileFormatException("Failed to load file: "+file.getName()+" , does not follow proper PNG formatting, no PNG identifier");
		}
		
		//skip the DOS line ending data
		imageFile.skipBytes(4);
		
		//parse the first chunk to extract critical data such as width, height, bit depth, ect.
		//check to make sure the next chunk is the IHDR chunk
		if(imageFile.readInt() != 0x73_72_68_82){
			throw new FileFormatException("Failed to load file: "+file.getName()+" , does not follow proper PNG formatting, missing IHDR chunk");
		}
		//width and height are unsigned however a png is unlikely to have dimension of 2^32
		int width = imageFile.readInt();
		int height = imageFile.readInt();
		byte bitDepth = imageFile.readByte();
		byte colorType = imageFile.readByte();
		byte compression = imageFile.readByte();
		byte filter = imageFile.readByte();
		byte interlace = imageFile.readByte();
		int bytesPerPixel = 0;
		
		//determine the format and type of the pixel data
		BaseFormat format;
		TexDataType type;
		
		switch(colorType){
			case 0://greyscale
				format = BaseFormat.RED;
				bytesPerPixel = 1;
				break;
			case 2://truecolor
				format = BaseFormat.RGB;
				bytesPerPixel = 3;
				break;
			case 3://indexed
				format = BaseFormat.RGB;
				bytesPerPixel = 3;
				break;
			case 4://greyscale with alpha
				format = BaseFormat.RG;
				bytesPerPixel = 1;
				break;
			case 6://truecolor with alpha
				format = BaseFormat.RGBA;
				bytesPerPixel = 4;
				break;
		}
		
		if(bitDepth <= 8){
			type = TexDataType.UBYTE;
		}else{
			type = TexDataType.USHORT;
		}
		
		ByteBuffer image = BufferUtils.createByteBuffer(width*height*bytesPerPixel);
		
		while(imageFile.){
			
		}
		
		return null;
	}
	
	private static void parseChunk(DataInputStream input, ByteBuffer imageStore){
		
	}
	
	private static void parseIDAT(DataInputStream input, ByteBuffer imageStore){
		
	}
}
