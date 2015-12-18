package textures.loaders;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.lwjgl.BufferUtils;

import textures.enums.BaseFormat;
import textures.enums.InternalFormat;
import textures.enums.TexDataType;

class PNG extends ImageParser{
	private int samplesPerPixel, bufferStride;
	private byte bitDepth, colorType, compression, filter, interlace;
	private byte[] palette;
	//TODO adam 7 interlacing, and ancilliary chunks
	private int 
	LEFT_OFFSET,
	HORIZ_STRIDE,
	TOP_OFFSET,
	VERT_STRIDE;
	
	
	PNG(File image) throws IOException, FileFormatException{
		super(image);
		
		LEFT_OFFSET = 0;
		HORIZ_STRIDE = 1;
		TOP_OFFSET = 0;
		VERT_STRIDE = 1;
		
		palette = null;//default it to null this way we can check if it's an indexed type that failed to provide a palette
		long pngSignature = 0L;
		pngSignature |= imageStream.readByte();
		pngSignature <<= 8;
		pngSignature |= imageStream.readByte();//P
		pngSignature <<= 8;
		pngSignature |= imageStream.readByte();//N
		pngSignature <<= 8;
		pngSignature |= imageStream.readByte();//G
		pngSignature <<= 8;
		pngSignature |= imageStream.readByte();
		pngSignature <<= 8;
		pngSignature |= imageStream.readByte();
		pngSignature <<= 8;
		pngSignature |= imageStream.readByte();
		pngSignature <<= 8;
		pngSignature |= imageStream.readByte();
		if(pngSignature != 0x89_50_4E_47_0D_0A_1A_0AL){
			throw new FileFormatException("Failed to load file: "+image.getName()+" , does not follow proper PNG formatting, no PNG identifier");
		}
		//skip null value buffer before IHDR
		imageStream.skipBytes(4);
		//parse the first chunk to extract critical data such as width, height, bit depth, ect.
		//check to make sure the next chunk is the IHDR chunk
		if(imageStream.readByte() != 'I' || imageStream.readByte() != 'H' || imageStream.readByte() != 'D' || imageStream.readByte() != 'R'){
			throw new FileFormatException("Failed to load file: "+image.getName()+" , does not follow proper PNG formatting, missing IHDR chunk");
		}
		//width and height are unsigned however a png is unlikely to have dimension of 2^32
		width = imageStream.readInt();
		height = imageStream.readInt();
		bitDepth = imageStream.readByte();
		colorType = imageStream.readByte();
		compression = imageStream.readByte();
		filter = imageStream.readByte();
		interlace = imageStream.readByte();

		System.out.println(colorType);
		System.out.println(bitDepth);
		System.out.println(interlace);
		//determine the format and type of the pixel data
		switch(colorType){
			case 0://greyscale
				format = BaseFormat.RGB;
				samplesPerPixel = 1;
				bufferStride = width*3;
				break;
			case 2://truecolor
				format = BaseFormat.RGB;
				samplesPerPixel = 3;
				bufferStride = width*3;
				break;
			case 3://indexed
				format = BaseFormat.RGB;
				samplesPerPixel = 1;
				bufferStride = width*3;
				break;
			case 4://greyscale with alpha
				format = BaseFormat.RGBA;
				samplesPerPixel = 2;
				bufferStride = width*4;
				break;
			case 6://truecolor with alpha
				format = BaseFormat.RGBA;
				samplesPerPixel = 4;
				bufferStride = width*4;
				break;
		}
		
		type = TexDataType.UBYTE;

		//skip CRC
		imageStream.skipBytes(4);
	}
	
	public ByteBuffer parse() throws IOException, DataFormatException{
		ByteBuffer image = BufferUtils.createByteBuffer(height*bufferStride);
		String chunkType;
		boolean firstIdatFound = false;//indicates whether we found the first idat chunk, this is to decide where to mark the stream
		int compressedSize = 0;//total size of the compressed data stream
		ArrayList<Integer> idatSizes = new ArrayList<Integer>();//stores the various sizes of the idat chunks found while parsing
		do{
			//get chunk length
			int chunkLength = imageStream.readInt();
//			System.out.println(chunkLength);
			//get chunk type
			chunkType = new String(new byte[]{imageStream.readByte(), imageStream.readByte(), imageStream.readByte(), imageStream.readByte()});
//			System.out.println(chunkType);
			
			switch(chunkType){
				case "IDAT":
					//if we have found the first idat chunk then add this chunks data and skip the bytes for later
					if(firstIdatFound){
						compressedSize += chunkLength;
						imageStream.skipBytes(chunkLength);
						idatSizes.add(chunkLength);
					}else{//otherwise do the same tihng as above except mark the stream for reset 
						firstIdatFound = true;
						imageStream.mark(image.capacity());
						compressedSize += chunkLength;
						imageStream.skipBytes(chunkLength);
						idatSizes.add(chunkLength);
					}
					break;
				case "PLTE":
					parsePLTE(chunkLength);
					break;
				default:
					imageStream.skipBytes(chunkLength);//until implemented ignore other non critical chunks
			}
			imageStream.skipBytes(4);//for now skip the CRC
		} while(!chunkType.equals("IEND"));
		//check to make sure that an indexed type has a palette to work with
		if(colorType == 3 && palette == null){
			try{
				imageStream.close();//we can close the stream now that we are done with it
			}catch(IOException io){
				io.printStackTrace();
			}
			throw new DataFormatException("Indexed PNG does not have palette for indexed values");
		}
		//reset the input stream for re-reading
		imageStream.reset();
		byte[] imageData = new byte[compressedSize];//allocate the buffer for storing the compressed image data
		int offset = 0;//offset from the beginning of the compressed storage to buffer the next chunk into
		//for each chunk read that many bytes from the image stream and store them
		for(Integer curSize : idatSizes){
			//for each byte in the current chunk read them and store them
			for(int curByte = 0; curByte < curSize; curByte++){
				imageData[offset+curByte] = imageStream.readByte();
			}
			//update the offset
			offset += curSize;
			imageStream.skipBytes(12);//skip the crc, chunk length, and chunk type
		}
		//decompress the data
		imageData = decompress(imageData);
		//process the data
		if(interlace == 0){
			process(image, imageData);
		}else{
			processAdam7(image, imageData);
		}

		image.flip();//flip the buffer
		//attempt to close the stream
		try{
			imageStream.close();//we can close the stream now that we are done with it
		}catch(IOException io){
			io.printStackTrace();
			return image;
		}
		return image;
	}
	
	private void process(ByteBuffer image, byte[] imageData){
		//size of an unfiltered scanline
		int scanlineSize = (int)Math.ceil(width*samplesPerPixel*(bitDepth/8.0f));
		int prevIndexOffset = Math.max(1, samplesPerPixel*(bitDepth/8));//used to determine how far to offset the arrays for values on the previous pixel
		byte[] prevScanline = new byte[scanlineSize];
		byte[] currentScanline = new byte[scanlineSize];
		//loop through the height of the image for each scanline
		for(int curScanline = 0; curScanline < height; curScanline++){
			int scanlineOffset = curScanline*(scanlineSize+1);
			byte filterType = imageData[scanlineOffset];
			//shift the scanline parsing 1 byte to allow the reading of the first byte which indicates filter type
			for(int curByte = 1; curByte < scanlineSize+1; curByte++){
				//pre compute indices for filtered byte data
				int previousIndex = curByte-1-prevIndexOffset;
				//get values for unfiltering
				byte current = imageData[scanlineOffset+curByte];//current byte
				//values that are out of bounds are 0 for the unfiltering
				byte prevCurLine = previousIndex < 0 ? 0 : currentScanline[previousIndex];//byte of the same index in the previous pixel on the current line
				byte curPrevLine = prevScanline[curByte-1];//byte of the same index on the previous line
				byte prevPrevLine = previousIndex < 0 ? 0 : prevScanline[previousIndex];//byte of the same index on the previous pixel of the previous line
				
				//unfilter then add the unfiltered value to the scanline buffer, bitwise & required to preserve unsigned byte values
				byte newValue = unfilter(filterType, (short)(0xff & current), (short)(0xff & prevCurLine), (short)(0xff & curPrevLine), (short)(0xff & prevPrevLine));
				currentScanline[curByte-1] = newValue;
			}
			//process the current scanline unfiltered to expand values with small bit depths and get indexed values from the palette if needed
			processScanline(image, currentScanline, curScanline);
			//update previous scanline with the current scanline for the next iteration
			byte[] temp = prevScanline;
			prevScanline = currentScanline;
			currentScanline = temp;
		}
	}
	
	private void processScanline(ByteBuffer image, byte[] scanline, int scanlineIndex){
		int offset = ((height-1-TOP_OFFSET)-scanlineIndex*VERT_STRIDE)*bufferStride;//current scanline to start buffering to in the image buffer
		int curPixel = LEFT_OFFSET;
		if(colorType == 0 || colorType == 4){//greyscale
			//if the bit depth is less than a byte then values need to be expanded
			if(bitDepth < 8){
				//decide the bitmask to use when getting the bits from the byte below
				//this makes sure that the left bits don't get copied when a shift occurs on middle bit values
				byte bitmask = 0;
				switch(bitDepth){
					case 1:
						bitmask = 0b0000_0001;
						break;
					case 2:
						bitmask = 0b0000_0011;
						break;
					case 4:
						bitmask = 0b0000_1111;
						break;
				}
				int bitGroups = 8/bitDepth;//number of groups of bits we need to shift and copy on the current byte
				for(int curByte = 0; curByte < scanline.length; curByte++){
					for(int bitGroup = 0; bitGroup < bitGroups; bitGroup++){
						//check if we processed all pixel bits and what remains is padding
						if(curPixel >= width){
							break;
						}
						//bitGroups-bitGroup  will give us the bit group that is to the left and the loop will move down from there
						//0010 1100
						//^^^^ ^^^^
						//|||| bitGroup
						//bitGroups-bitGroup-1
						//when bitGroup is 0 for the above
						int value = (int)(bitmask & (scanline[curByte] >>> (bitDepth*(bitGroups-bitGroup-1))));
						value = (int)(255*(value/(float)bitmask));
						expandGrey(image, offset+3*curPixel, (byte)value);
						curPixel += HORIZ_STRIDE;
					}
				}
			}else{//otheriwse we can just extend the data to 3 element values and buffer it
				//duplicate the greyscale to make it compatible with RGB
				//check if we need to buffer two bytes or 1 
				//values for calculating how the data is being read and stored
				boolean isAlpha = colorType == 4;
				int pixelStride = isAlpha ? 2 : 1;
				int bufferedPixels = isAlpha ? 4 : 3;
				
				if(bitDepth == 8){
					for(int curByte = 0; curByte < scanline.length; curByte += pixelStride){
						//RGB
						expandGrey(image, offset+bufferedPixels*curPixel, scanline[curByte]);
						
						if(isAlpha){
							expandGrey(image, offset+bufferedPixels*curPixel+1, scanline[curByte+1]);
						}
						curPixel += HORIZ_STRIDE;
					}
				}else{
					for(int curByte = 0; curByte < scanline.length; curByte += 2*pixelStride){
						int value = (int)(0xff & scanline[curByte]);
						value <<= 8;
						value += (int)(0xff & scanline[curByte+1]);
						value = (int)Math.floor((255*(value/65535.0))+ 0.5);
						//RGB
						expandGrey(image, offset+bufferedPixels*curPixel, (byte)value);
						
						if(isAlpha){
							value = (int)(0xff & scanline[curByte+2]);
							value <<= 8;
							value += (int)(0xff & scanline[curByte+3]);
							value = (int)Math.floor((255*(value/65535.0))+ 0.5);
							//alpha
							image.put(offset+bufferedPixels*curPixel+3, (byte)value);
						}
						curPixel += HORIZ_STRIDE;
					}
				}
			}
		}else if(colorType == 2 || colorType == 6){//truecolor
			//values for calculating how the data is being read and stored
			boolean isAlpha = colorType == 6;
			int pixelStride = isAlpha ? 4 : 3;
			if(bitDepth == 8){
				for(int curByte = 0; curByte < scanline.length; curByte += pixelStride){
					int pixelIndex = offset+pixelStride*curPixel;
					//loop through the values and add them
					for(int curRGBA = 0; curRGBA < pixelStride; curRGBA++){
						byte value = scanline[curByte+curRGBA];
						image.put(pixelIndex+curRGBA, (byte)value);
					}
					curPixel += HORIZ_STRIDE;
				}
			}else{
				for(int curByte = 0; curByte < scanline.length; curByte += 2*pixelStride){
					int pixelIndex = offset+pixelStride*curPixel;
					//loop through the values and add them
					for(int curRGBA = 0; curRGBA < 2*pixelStride; curRGBA+=2){
						int value = (int)(0xff & scanline[curByte+curRGBA]);
						value <<= 8;
						value += (int)(0xff & scanline[curByte+curRGBA+1]);
						value = (int)Math.floor((255*(value/65535.0))+ 0.5);
						image.put(pixelIndex+(curRGBA >> 1), (byte)value);
					}
					curPixel += HORIZ_STRIDE;
				}
			}
		}else{//indexed
			//decide the bitmask to use when getting the bits from the byte below
			//this makes sure that the left bits don't get copied when a shift occurs on middle bit values
			int bitmask = 0;
			switch(bitDepth){
				case 1:
					bitmask = 0b0000_0001;
					break;
				case 2:
					bitmask = 0b0000_0011;
					break;
				case 4:
					bitmask = 0b0000_1111;
					break;
				case 8:
					bitmask = 0b1111_1111;
					break;
			}
			int bitGroups = 8/bitDepth;//number of groups of bits we need to shift and copy on the current byte
			for(int curByte = 0; curByte < scanline.length; curByte++){
				for(int bitGroup = 0; bitGroup < bitGroups; bitGroup++){
					//check if we processed all pixel bits and what remains is padding
					if(curPixel >= width){
						break;
					}
					//bitGroups-bitGroup  will give us the bit group that is to the left and the loop will move down from there
					//0010 1100
					//^^^^ ^^^^
					//|||| bitGroup
					//bitGroups-bitGroup-1
					//when bitGroup is 0 for the above
					int index = bitmask & (scanline[curByte] >>> (bitDepth*(bitGroups-bitGroup-1)));
//					System.out.println(index);
					image.put(offset+3*curPixel, palette[3*index]);
					image.put(offset+3*curPixel+1, palette[3*index+1]);
					image.put(offset+3*curPixel+2, palette[3*index+2]);
					curPixel += HORIZ_STRIDE;
				}
			}
		}
	}
	
	private void processAdam7(ByteBuffer image, byte[] imageData){
		//perform the seven passes of the adam 7 algorithm
		int subImageOffset = 0;
		for(int curPass = 0; curPass < 7; curPass++){
			adam7Offsets(curPass);
			//calculate the width and height of the current pass subimage
			int pixelsWide = (int)Math.ceil(width/(float)HORIZ_STRIDE);
			int pixelsHigh = (int)Math.ceil(height/(float)VERT_STRIDE);
			//determine if the starting offset will eliminate the ending pixel of the scanline, since it would end up out of bounds for the image dimensions

//			if(pixelsWide*HORIZ_STRIDE+LEFT_OFFSET > width){
//				pixelsWide--;
//			}
//			if(pixelsHigh*VERT_STRIDE+TOP_OFFSET > height){
//				pixelsHigh--;
//			}
			System.out.println("Pixelsdimensions: "+pixelsWide+", "+pixelsHigh);
			//size of an unfiltered scanline, no difference to a filtered scanlines size
			int scanlineSize = (int)Math.ceil(pixelsWide*samplesPerPixel*(bitDepth/8.0f));
			byte[] prevScanline = new byte[scanlineSize];
			byte[] currentScanline = new byte[scanlineSize];
			int prevIndexOffset = Math.max(1, samplesPerPixel*(bitDepth/8));//used to determine how far to offset the arrays for values on the previous pixel
			//loop through the height of the image for each scanline
			for(int curScanline = 0; curScanline < pixelsHigh; curScanline++){
				int scanlineOffset = subImageOffset+curScanline*(scanlineSize+1);
				byte filterType = imageData[scanlineOffset];
				System.out.println(filterType);
				//shift the scanline parsing 1 byte to allow the reading of the first byte which indicates filter type
				for(int curByte = 1; curByte < scanlineSize+1; curByte++){
					//pre compute indices for filtered byte data
					int previousIndex = curByte-1-prevIndexOffset;
					//get values for unfiltering
					byte current = imageData[scanlineOffset+curByte];//current byte
					//values that are out of bounds are 0 for the unfiltering
					byte prevCurLine = previousIndex < 0 ? 0 : currentScanline[previousIndex];//byte of the same index in the previous pixel on the current line
					byte curPrevLine = prevScanline[curByte-1];//byte of the same index on the previous line
					byte prevPrevLine = previousIndex < 0 ? 0 : prevScanline[previousIndex];//byte of the same index on the previous pixel of the previous line
					
					//unfilter then add the unfiltered value to the scanline buffer, bitwise & required to preserve unsigned byte values
					byte newValue = unfilter(filterType, (short)(0xff & current), (short)(0xff & prevCurLine), (short)(0xff & curPrevLine), (short)(0xff & prevPrevLine));
					currentScanline[curByte-1] = newValue;
				}
				//process the current scanline unfiltered to expand values with small bit depths and get indexed values from the palette if needed
				processScanline(image, currentScanline, curScanline);
				//update previous scanline with the current scanline for the next iteration
				byte[] temp = prevScanline;
				prevScanline = currentScanline;
				currentScanline = temp;
			}
//			System.out.println("pass: "+curPass+" complete");
			subImageOffset += pixelsHigh*(scanlineSize+1);//plus one to account for the filter byte
//			System.out.println(subImageOffset);
		}
	}
	
	private void adam7Offsets(int pass){
		switch(pass){
		//values meaning from top to bottom
		//offset from left side of the original image (stride)
		//horizontal offset between each pixel at the given pass
		//offset from the top of the original image
		//vertical between each pixel at the given pass
			case 0:
				LEFT_OFFSET = 0;
				HORIZ_STRIDE = 8;
				TOP_OFFSET = 0;
				VERT_STRIDE = 8;
				break;
			case 1:
				LEFT_OFFSET = 4;
				HORIZ_STRIDE = 8;
				TOP_OFFSET = 0;
				VERT_STRIDE = 8;
				break;
			case 2:
				LEFT_OFFSET = 0;
				HORIZ_STRIDE = 4;
				TOP_OFFSET = 4;
				VERT_STRIDE = 8;
				break;
			case 3:
				LEFT_OFFSET = 2;
				HORIZ_STRIDE = 4;
				TOP_OFFSET = 0;
				VERT_STRIDE = 4;
				break;
			case 4:
				LEFT_OFFSET = 0;
				HORIZ_STRIDE = 2;
				TOP_OFFSET = 2;
				VERT_STRIDE = 4;
				break;
			case 5:
				LEFT_OFFSET = 1;
				HORIZ_STRIDE = 2;
				TOP_OFFSET = 0;
				VERT_STRIDE = 2;
				break;
			case 6:
				LEFT_OFFSET = 0;
				HORIZ_STRIDE = 1;
				TOP_OFFSET = 1;
				VERT_STRIDE = 2;
				break;
		}
	}
	
	/**
	 * Expands the given greyscale value of the png file to fit into RGB values
	 * 
	 * @param image ByteBuffer where the processed image data is being stored
	 * @param offset Offset into the ByteBuffer to store the greyscale value
	 * @param value Greyscale value to store into the ByteBuffer
	 */
	private void expandGrey(ByteBuffer image, int offset, byte value){
		//R
		image.put(offset, value);
		//G
		image.put(offset+1, value);
		//B
		image.put(offset+2, value);
	}
	
	/**
	 * Decompresses the IDAT image data
	 * 
	 * @param data Data to decompress
	 * @return Byte array of the decompressed data
	 * @throws DataFormatException
	 */
	private byte[] decompress(byte[] data) throws DataFormatException{
		//initialize decompressor
		Inflater decompressor = new Inflater();
		decompressor.setInput(data);
		//create storage buffer
		byte[] uncompressed = new byte[(height*(int)Math.ceil(width*samplesPerPixel*(bitDepth/8.0f))+height) << 1];
		//decompress and close the decompressor
		int uncompressedSize = decompressor.inflate(uncompressed);
		decompressor.end();
		return Arrays.copyOfRange(uncompressed, 0, uncompressedSize);
	}
	
	/**
	 * Unfilters the image data based on the {@code type} of filter used, {@code x}, {@code a}, {@code b}, and {@code c} are shorts so that 
	 * arithmetic for unfiltering can be performed as though the data was unsigned
	 * 
	 * @param type Type of filter used
	 * @param x Current byte being processed
	 * @param a Previous pixel byte that corresponds to x in the current pixel
	 * @param b Previous pixel byte that corresponds to x in the current pixel of the previous scanline
	 * @param c Previous pixel byte that corresponds to x in the current pixel of the previous pixel of the previous scanline
	 * @return Unfiltered byte
	 */
	private byte unfilter(byte type, short x, short a, short b, short c){
		byte result = 0;
		switch(type){
			case 0://no filter
				result = (byte)x;
				break;
			case 1://sub filter
				result = (byte)(x+a);
				break;
			case 2://up filter
				result = (byte)(x+b);
				break;
			case 3://average filter
				result = (byte)(x+((a+b) >>> 1));
				break;
			case 4://Paeth filter
				int p = a + b - c;
			    int pa = Math.abs(p - a);
	    		int pb = Math.abs(p - b);
			    int pc = Math.abs(p - c);
			    if(pa <= pb && pa <= pc){
			    	result = (byte)(x+a);
			    }
			    else if(pb <= pc){
			    	result = (byte)(x+b);
			    }
			    else{
			    	result = (byte)(x+c);
			    }
				break;
		}
		return result;
	}
	
	/**
	 * Parses the palette chunk of the png and stores it in the palette array of this PNG object
	 * 
	 * @param chunkLength Length of the chunk
	 */
	private void parsePLTE(int chunkLength) throws DataFormatException, IOException{
		//check if the palette is divisible by 3 if not throw an exception
		if(chunkLength%3 != 0){
			throw new DataFormatException("PLTE chunk not properly formatted unable to load image");
		}
		palette = new byte[chunkLength];//palette will have at most 256 elements each with 3 bytes for each color rgb
		for(int curByte = 0; curByte < chunkLength; curByte+=3){
			//fill each of the rgb bytes for the palette index
			palette[curByte] = imageStream.readByte();
			palette[curByte+1] = imageStream.readByte();
			palette[curByte+2] = imageStream.readByte();
		}
	}
	
}
