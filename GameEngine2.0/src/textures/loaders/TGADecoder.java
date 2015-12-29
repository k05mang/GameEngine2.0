package textures.loaders;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;

import org.lwjgl.BufferUtils;

import textures.enums.BaseFormat;
import textures.enums.TexDataType;

public class TGADecoder extends ImageParser {

	private final int 
			COLOR_MAPPED = 1,
			TRUE_COLOR = 2,
			GREYSCALE = 3;
	private int imageType, colorMapStart, colorMapLength, colorMapBitDepth, pixelBitDepth, bufferStride;
	private boolean isRLE, startTop, startLeft;
	private byte[] colorMap;
	
	public TGADecoder(File file) throws IOException {
		super(file);
		//byte ordering is from least to most signif
		int idLength = 0xff & imageStream.readByte();
		boolean hasColorMap = imageStream.readByte() == 1;
		imageType = imageStream.readByte();
//		isRLE = imageType == RLE_COLOR_MAP || imageType == RLE_TRUE_COLOR || imageType == RLE_GREYSCALE;
		isRLE = imageType >> 3 == 1;
		imageType &= 0b0111;//isolate the portion that determines the base type
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
		int imageOrigin = (0b0011_0000 & descriptor) >> 4;//gets the bits that tell how the pixels are to be read
		startTop = imageOrigin >> 1 == 1;//left bit decides top or bottom
		startLeft = (imageOrigin & 0b01) == 0;//right bit decides left or right
		//skip the image id field
		imageStream.skipBytes(idLength);
		
		colorMap = null;
		
		bufferStride = width;
		
		if(hasColorMap){
			parseColorMap();
		}
		
//		System.out.println(startTop);
//		System.out.println(startLeft);
//		System.out.println(pixelBitDepth);
		
		if(imageType == COLOR_MAPPED){
			if(colorMapBitDepth < 32){
				format = BaseFormat.RGB;
				type = TexDataType.UBYTE;
				bufferStride *= 3;
			}else{
				format = BaseFormat.RGBA;
				type = TexDataType.UBYTE;
				bufferStride <<= 2;
			}
		}else if(imageType == TRUE_COLOR){
			//if the number of bytes per pixel is 3 then it's RGB otherwise RGBA
			if(pixelBitDepth < 32){
				format = BaseFormat.BGR;
				bufferStride *= 3;
			}else{
				format = BaseFormat.BGRA;
				bufferStride <<= 2;
			}
			type = TexDataType.UBYTE;
		}else{
			format = BaseFormat.RGB;
			bufferStride *= 3;
			type = TexDataType.UBYTE;
		}
	}
	
	private void parseColorMap() throws IOException{
		int bytesPerEntry = (int)Math.ceil(colorMapBitDepth/8.0f);
		colorMap = new byte[colorMapLength*bytesPerEntry];
		
		//skip the bytes before the start index
		imageStream.skipBytes(colorMapStart*bytesPerEntry);
		//add bytes from the start index to the end of the color map
		for(int curEntry = 0; curEntry < (colorMapLength-colorMapStart)*bytesPerEntry; curEntry+=bytesPerEntry){
			if(colorMapBitDepth < 24){
				short values = (short)(0xff & imageStream.readByte());
				values |= ((0xff & imageStream.readByte()) << 8);
				byte bitMask = 0b11111;
				colorMap[curEntry] = (byte)(values >> 11);//R
				colorMap[curEntry+1] = (byte)((values >> 6) & bitMask);//G
				colorMap[curEntry+2] = (byte)((values >> 1) & bitMask);//B
			}else if(colorMapBitDepth == 24){
				colorMap[curEntry] = imageStream.readByte();//R
				colorMap[curEntry+1] = imageStream.readByte();//G
				colorMap[curEntry+2] = imageStream.readByte();//B
			}else{
				colorMap[curEntry] = imageStream.readByte();//R
				colorMap[curEntry+1] = imageStream.readByte();//G
				colorMap[curEntry+2] = imageStream.readByte();//B
				colorMap[curEntry+3] = imageStream.readByte();//A
			}
		}
	}

	@Override
	public ByteBuffer parse() throws IOException, DataFormatException {
		ByteBuffer image = BufferUtils.createByteBuffer(height*bufferStride);
		int bytesPerPixel = (int)Math.ceil(pixelBitDepth/8.0f);
		byte[] pixelBuffer = new byte[bytesPerPixel];
		int overflowOffset = 0;//offset in case the RLE line wraps based on allowances from the old specification, this offset is in pixels not bytes
		int valuesPerFinalPixel = 0;
		switch(imageType){
			case COLOR_MAPPED:
				if(colorMapBitDepth < 32){
					valuesPerFinalPixel = 3;
				}else{
					valuesPerFinalPixel = 4;
				}
				break;
			case TRUE_COLOR:
				if(pixelBitDepth < 32){
					valuesPerFinalPixel = 3;
				}else{
					valuesPerFinalPixel = 4;
				}
				break;
			case GREYSCALE:
				valuesPerFinalPixel = 3;
				break;
		}
		int horizOffset, vertOffset, offset;
		//go through each scanline of the image data
		for(int curScanline = 0; curScanline < height; curScanline++){
			//calculate offsets based on the origin of the image
			vertOffset = (startTop ? height-1-curScanline : curScanline)*bufferStride;
			for(int curPixel = 0; curPixel < width; curPixel++){
				//determine what type of image we are working with and decide how to handle the data
				if(isRLE){
					//determine if the packet is true RLE or raw
					byte count = imageStream.readByte();
					if((count & 0b1000_0000) == 0){//indicates raw packet
						//trim the first flag bit
						count &= 0b0111_1111;
						count++;//since the value of count is 1 less than the actual amount
						
						//buffer count pixels of data
						for(int curRLE = 0; curRLE < count; curRLE++){
							horizOffset = (startTop ? width-1-(curRLE+overflowOffset) : curRLE+overflowOffset)*valuesPerFinalPixel;
							//buffer the bytes for the pixel
							for(int curByte = 0; curByte < bytesPerPixel; curByte++){
								pixelBuffer[curByte] = imageStream.readByte();
							}
							//calculate the final offset
							offset = vertOffset+horizOffset;
							processPixel(image, pixelBuffer, offset);
						}
						curPixel += count+1;//update the scanline inner loop
						//adjust overflowoffset
						if(overflowOffset+count > width){
							overflowOffset = (overflowOffset+count)%width;
						}else{
							overflowOffset = 0;
						}
					}else{//rle packet
						//trim the first flag bit
						count &= 0b0111_1111;
						count++;//since the value of count is 1 less than the actual amount
						//buffer the bytes for the next count pixels
						for(int curByte = 0; curByte < bytesPerPixel; curByte++){
							pixelBuffer[curByte] = imageStream.readByte();
						}
						
						//buffer count pixels of data
						for(int curRLE = 0; curRLE < count; curRLE++){
							horizOffset = (startTop ? width-1-(curRLE+overflowOffset) : curRLE+overflowOffset)*valuesPerFinalPixel;
							
							//calculate the final offset
							offset = vertOffset+horizOffset;
							processPixel(image, pixelBuffer, offset);
						}
						curPixel += count+1;//update the scanline inner loop
						//adjust overflowoffset
						if(overflowOffset+count > width){
							overflowOffset = (overflowOffset+count)%width;
						}else{
							overflowOffset = 0;
						}
					}
				}else{
					//calculate the horizontal offset
					horizOffset = (startTop ? width-1-curPixel : curPixel)*valuesPerFinalPixel;
					
					//calculate the final offset
					offset = vertOffset+horizOffset;
					//buffer the bytes for the current pixel
					for(int curByte = 0; curByte < bytesPerPixel; curByte++){
						pixelBuffer[curByte] = imageStream.readByte();
					}
					processPixel(image, pixelBuffer, offset);
				}
			}
		}
		image.flip();
		return image;
	}
	
	private void processPixel(ByteBuffer image, byte[] pixel, int offset){
		//color order is the way it is since lower order bytes are first and the RGB values are presented from highest byte to lowest
		switch(imageType){
			case COLOR_MAPPED:
				int index = 0;
				for(int curByte = pixel.length-1; curByte > -1; curByte--){
					index |= pixel[curByte];
					index <<= 8;
				}
				if(colorMapBitDepth < 32){
					image.put(offset, colorMap[3*index]);//R
					image.put(offset+1, colorMap[3*index+1]);//G
					image.put(offset+2, colorMap[3*index+2]);//B
				}else{
					image.put(offset, colorMap[4*index]);//R
					image.put(offset+1, colorMap[4*index+1]);//G
					image.put(offset+2, colorMap[4*index+2]);//B
					image.put(offset+3, colorMap[4*index+3]);//A
				}
				break;
			case TRUE_COLOR:
				if(colorMapBitDepth < 32){
					image.put(offset, pixel[0]);//B
					image.put(offset+1, pixel[1]);//G
					image.put(offset+2, pixel[2]);//R
				}else{
					//pixel offsets a result of the ordering in the image data
					image.put(offset, pixel[0]);//B
					image.put(offset+1, pixel[1]);//G
					image.put(offset+2, pixel[2]);//R
					image.put(offset+3, pixel[3]);//A
				}
				break;
			case GREYSCALE:
				int value = 0;
				for(int curByte = pixel.length-1; curByte > -1; curByte--){
					value |= pixel[curByte];
					value <<= 8;
				}
				byte finalValue = (byte)(255*(value/(float)(1 << pixelBitDepth)));
				image.put(offset, finalValue);
				image.put(offset+1, finalValue);
				image.put(offset+2, finalValue);
				break;
		}
	}
}
