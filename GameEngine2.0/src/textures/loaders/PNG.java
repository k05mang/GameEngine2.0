package textures.loaders;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.lwjgl.BufferUtils;

import textures.enums.BaseFormat;
import textures.enums.InternalFormat;
import textures.enums.TexDataType;

class PNG extends ImageParser{
	private int samplesPerPixel, bufferStride;
	private final int PALETTE_LENGTH = 256;
	private byte bitDepth, colorType, compression, filter, interlace;
	private byte[] palette;
	//TODO adam 7 interlacing, and ancilliary chunks
	
	PNG(File image) throws IOException, FileFormatException{
		super(image);
		palette = null;//default it to null this way we can check if it's an indexed type that failed to provide a palette
		imageStream.skipBytes(1);//skip the first byte since it has no relevance with modern systems
		//get the next 3 bytes that spell out PNG
		if(imageStream.readByte() != 0x50 || imageStream.readByte() != 0x4E || imageStream.readByte() != 0x47){
			throw new FileFormatException("Failed to load file: "+image.getName()+" , does not follow proper PNG formatting, no PNG identifier");
		}
		
		//skip the DOS line ending data
		imageStream.skipBytes(8);
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
	
	public ByteBuffer parse(){
		
		//segment in parenthesis is the factor to determine the number of bytes in the array
		//anything with a bitdepth of 16 will use shorts which is 2 bytes long and anything less will be converted to bytes of type RGB
		ByteBuffer image = BufferUtils.createByteBuffer(height*bufferStride);
		String chunkType;
		boolean firstIdatFound = false;//indicates whether we found the first idat chunk, this is to decide where to mark the stream
		int compressedSize = 0;//total size of the compressed data stream
		ArrayList<Integer> idatSizes = new ArrayList<Integer>();//stores the various sizes of the idat chunks found while parsing 
		try{
			do{
				//get chunk length
				int chunkLength = imageStream.readInt();
//				System.out.println(chunkLength);
				//get chunk type
				chunkType = new String(new byte[]{imageStream.readByte(), imageStream.readByte(), imageStream.readByte(), imageStream.readByte()});
//				System.out.println(chunkType);
				
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
				return getDefault(width, height, false, format == BaseFormat.RGBA);
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
			process(image, imageData);
		}catch(Exception e){//if we fail, load a default image
			e.printStackTrace();
			try{
				imageStream.close();//we can close the stream now that we are done with it
			}catch(IOException io){
				io.printStackTrace();
			}
			return getDefault(width, height, false, format == BaseFormat.RGBA);
		}
		image.flip();//flip the buffer
		//attempt to close the stream
		try{
			imageStream.close();//we can close the stream now that we are done with it
		}catch(IOException io){
			io.printStackTrace();
		}
		return image;
	}
	
	private void process(ByteBuffer image, byte[] imageData){
		//size of an unfiltered scanline
		int scanlineSize = (int)Math.ceil(width*samplesPerPixel*(bitDepth/8.0f));
		byte[] prevScanline = new byte[scanlineSize];
		byte[] currentScanline = new byte[scanlineSize];
		//loop through the height of the image for each scanline
		for(int curScanline = 0; curScanline < height; curScanline++){
			byte filterType = imageData[curScanline*(scanlineSize+1)];
			//shift the scanline parsing 1 byte to allow the reading of the first byte which indicates filter type
			for(int curByte = 1; curByte < scanlineSize+1; curByte++){
				//pre compute indices for filtered byte data
				int prevIndexOffset = Math.max(1, samplesPerPixel*(bitDepth/8));//used to determine how far to offset the arrays for values on the previous pixel
				int previousIndex = curByte-1-prevIndexOffset;
				//get values for unfiltering
				byte current = imageData[curScanline*(scanlineSize+1)+curByte];//current byte
				//values that are out of bounds are 0 for the unfiltering
				byte prevCurLine = previousIndex < 0 ? 0 : currentScanline[previousIndex];//byte of the same index in the previous pixel on the current line
				byte curPrevLine = prevScanline[curByte-1];//byte of the same index on the previous line
				byte prevPrevLine = previousIndex < 0 ? 0 : prevScanline[previousIndex];//byte of the same index on the previous pixel of the previous line
				
				//unfilter then add the unfiltered value to the scanline buffer, bitwise & required to preserve unsigned byte values
				byte newValue = unfilter(filterType, (short)(0xff & current), (short)(0xff & prevCurLine), (short)(0xff & curPrevLine), (short)(0xff & prevPrevLine));
				currentScanline[curByte-1] = newValue;
			}
			//process the current scanline unfiltered to expand values with small bit depths and get indexed values from the palette if needed
			processScanline(image, currentScanline, curScanline);//TODO make this operate on single byte values above to reduce the need to loop through the scanline again
			//update previous scanline with the current scanline for the next iteration
			byte[] temp = prevScanline;
			prevScanline = currentScanline;
			currentScanline = temp;
		}
	}
	
	private void processScanline(ByteBuffer image, byte[] scanline, int scanlineIndex){
		int offset = ((height-1)-scanlineIndex)*bufferStride;//current scanline to start buffering to in the image buffer
		switch(colorType){
			case 0://greyscale - bit range 1-16
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
					int curPixel = 0;
					for(int curByte = 0; curByte < scanline.length; curByte++){
						int bitGroups = 8/bitDepth;//number of groups of bits we need to shift and copy on the current byte
						for(int bitGroup = 0; bitGroup < bitGroups; bitGroup++){
							//check if we processed all pixel bits and what remains is padding
							if(curPixel == width){
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
							curPixel++;
						}
					}
				}else{//otheriwse we can just extend the data to 3 element values and buffer it
					//duplicate the greyscale to make it compatible with RGB
					//check if we need to buffer two bytes or 1 
					if(bitDepth/8 == 1){
						for(int curByte = 0; curByte < scanline.length; curByte++){
							byte greyVal = scanline[curByte];
							//RGB
							expandGrey(image, offset+3*curByte, greyVal);
						}
					}else{
						for(int curByte = 0; curByte < scanline.length; curByte+=2){
							int value = (int)(0xff & scanline[curByte]);
							value <<= 8;
							value += (int)(0xff & scanline[curByte+1]);
							value = (int)Math.floor((255*(value/65535.0))+ 0.5);
							//RGB
							expandGrey(image, offset+3*(curByte >> 1), (byte)value);
						}
					}
				}
				break;
			case 2://truecolor - bit range 8-16
				if(bitDepth == 8){
					image.position(offset);
					image.put(scanline, 0, scanline.length);
				}else{
					for(int curByte = 0; curByte < scanline.length; curByte+=2){
						int value = (int)(0xff & scanline[curByte]);
						value <<= 8;
						value += (int)(0xff & scanline[curByte+1]);
						value = (int)Math.floor((255*(value/65535.0))+ 0.5);
						image.put(offset+(curByte >> 1), (byte)value);
					}
				}
				break;
			case 3://indexed - bit range 1-8
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
				int curPixel = 0;
				for(int curByte = 0; curByte < scanline.length; curByte++){
					int bitGroups = 8/bitDepth;//number of groups of bits we need to shift and copy on the current byte
					for(int bitGroup = 0; bitGroup < bitGroups; bitGroup++){
						//check if we processed all pixel bits and what remains is padding
						if(curPixel == width){
							break;
						}
						//bitGroups-bitGroup  will give us the bit group that is to the left and the loop will move down from there
						//0010 1100
						//^^^^ ^^^^
						//|||| bitGroup
						//bitGroups-bitGroup-1
						//when bitGroup is 0 for the above
						int index = bitmask & (scanline[curByte] >>> (bitDepth*(bitGroups-bitGroup-1)));
						image.put(offset+3*curPixel, palette[3*index]);
						image.put(offset+3*curPixel+1, palette[3*index+1]);
						image.put(offset+3*curPixel+2, palette[3*index+2]);
						curPixel++;
					}
				}
				break;
			case 4://greyscale with alpha - bit range 8-16
				//duplicate the greyscale to make it compatible with RGB
				//check if we need to buffer two bytes or 1 
				if(bitDepth/8 == 1){
					for(int curByte = 0; curByte < scanline.length; curByte+=2){
						byte greyVal = scanline[curByte];
						byte alpha = scanline[curByte+1];
						//RGB
						expandGrey(image, offset+4*(curByte >> 1), greyVal);
					}
				}else{
					for(int curByte = 0; curByte < scanline.length; curByte+=4){
						int value = (int)(0xff & scanline[curByte]);
						value <<= 8;
						value += (int)(0xff & scanline[curByte+1]);
						value = (int)Math.floor((255*(value/65535.0))+ 0.5);
						//RGB
						expandGrey(image, offset+curByte, (byte)value);
						
						//do that same transformation for the alpha
						value = (int)(0xff & scanline[curByte+2]);
						value <<= 8;
						value += (int)(0xff & scanline[curByte+3]);
						value = (int)Math.floor((255*(value/65535.0))+ 0.5);
						
						image.put(offset+curByte+3, (byte)value);
					}
				}
				break;
			case 6://truecolor with alpha - bit range 8-16
				if(bitDepth == 8){
					image.position(offset);
					image.put(scanline, 0, scanline.length);
				}else{
					for(int curByte = 0; curByte < scanline.length; curByte+=2){
						int value = (int)(0xff & scanline[curByte]);
						value <<= 8;
						value += (int)(0xff & scanline[curByte+1]);
						value = (int)Math.floor((255*(value/65535.0))+ 0.5);
						image.put(offset+(curByte >> 1), (byte)value);
					}
				}
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
		byte[] uncompressed = new byte[height*(int)Math.ceil(width*samplesPerPixel*(bitDepth/8.0f))+height];
		//decompress and close the decompressor
		decompressor.inflate(uncompressed);
		decompressor.end();
		return uncompressed;
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
		palette = new byte[PALETTE_LENGTH*3];//palette will have at most 256 elements each with 3 bytes for each color rgb
		for(int curByte = 0; curByte < chunkLength; curByte+=3){
			//fill each of the rgb bytes for the palette index
			palette[curByte] = imageStream.readByte();
			palette[curByte+1] = imageStream.readByte();
			palette[curByte+2] = imageStream.readByte();
		}
	}
	
}
