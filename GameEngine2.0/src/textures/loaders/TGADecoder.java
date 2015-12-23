package textures.loaders;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;

import textures.enums.BaseFormat;
import textures.enums.TexDataType;

public class TGADecoder extends ImageParser {

	private final int 
			COLOR_MAPPED = 1,
			TRUE_COLOR = 2,
			GREYSCALE = 3,
			RLE_COLOR_MAP = 9,
			RLE_TRUE_COLOR = 10,
			RLE_GREYSCALE = 11,//there may or may not be a 32 and 33 type based on a different specification
			
			BOTTOM_LEFT = 0,
			BOOTME_RIGHT = 1,
			TOP_LEFT = 2,
			TOP_RIGHT = 3;
	private int imageType, colorMapStart, colorMapLength, colorMapBitDepth, pixelBitDepth, imageOrigin;
	private byte[] colorMap;
	
	public TGADecoder(File file) throws IOException {
		super(file);
		//byte ordering is from least to most signif
		int idLength = 0xff & imageStream.readByte();
		boolean hasColorMap = imageStream.readByte() == 1;
		imageType = imageStream.readByte();
		//color map info
		colorMapStart = 0xff & imageStream.readByte();
		colorMapStart |= (0xff & imageStream.readByte()) << 8;

		colorMapLength = 0xff & imageStream.readByte();
		colorMapLength |= (0xff & imageStream.readByte()) << 8;
		
		colorMapBitDepth = 0xff & imageStream.readByte();

		//image info
		imageStream.skipBytes(4);//skip image origin information

		width = 0xff & imageStream.readByte();
		width |= (0xff & imageStream.readByte()) << 8;
		
		height = 0xff & imageStream.readByte();
		height |= (0xff & imageStream.readByte()) << 8;
		
		pixelBitDepth = 0xff & imageStream.readByte();
		
		//image descriptor
		byte descriptor = imageStream.readByte();//bits 0-3 are for attribute/alpha bit depth this may be used later for now don't worry about it
		imageOrigin = (0b0011_0000 & descriptor) >> 4;//gets the bits that tell how the pixels are to be read
		
		//skip the image id field
		imageStream.skipBytes(idLength);
		
		colorMap = null;
		
		if(hasColorMap){
			parseColorMap();
		}
		
		if(imageType == COLOR_MAPPED || imageType == RLE_COLOR_MAP){
			switch(colorMapBitDepth){
				case 15:
					format = BaseFormat.RGBA;
					type = TexDataType.USHORT_5_5_5_1;
					break;
				case 16:
					format = BaseFormat.RGBA;
					type = TexDataType.USHORT_5_5_5_1;
					break;
				case 24:
					format = BaseFormat.RGB;
					type = TexDataType.UBYTE;
					break;
				case 32:
					format = BaseFormat.RGBA;
					type = TexDataType.UBYTE;
					break;
			}
		}else if(imageType == TRUE_COLOR || imageType == RLE_TRUE_COLOR){
			format = BaseFormat.RGBA;
			type = TexDataType.UBYTE;
		}else{
			format = BaseFormat.RGB;
			type = TexDataType.UBYTE;
		}
	}
	
	private void parseColorMap() throws IOException{
		int bytesPerEntry = (int)Math.ceil(colorMapBitDepth/8.0f);
		colorMap = new byte[colorMapLength*bytesPerEntry];
		
		//skip the bytes before the start index
		imageStream.skipBytes(colorMapStart*bytesPerEntry);
		//add bytes from the start index to the end of the color map
		for(int curEntry = 0; curEntry < (colorMapLength-colorMapStart)*bytesPerEntry; curEntry++){
			colorMap[curEntry] = imageStream.readByte();
		}
	}

	@Override
	public ByteBuffer parse() throws IOException, DataFormatException {
		// TODO Auto-generated method stub
		return null;
	}

}
