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
import textures.enums.TexDataType;

class PNG extends ImageParser{
	private int samplesPerPixel;
	private final int PALETTE_LENGTH = 256;
	private byte bitDepth, colorType, compression, filter, interlace;
	private byte[] palette;
	//TODO work with greyscale, indexed palette, and adam 7 interlacing, and ancilliary chunks
	
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

//		System.out.println(compression);
//		System.out.println(filter);
//		System.out.println(interlace);
		//determine the format and type of the pixel data
		switch(colorType){
			case 0://greyscale
				format = BaseFormat.RGB;
				samplesPerPixel = 1;
				break;
			case 2://truecolor
				format = BaseFormat.RGB;
				samplesPerPixel = 3;
				break;
			case 3://indexed
				format = BaseFormat.RGB;
				samplesPerPixel = 1;
				break;
			case 4://greyscale with alpha
				format = BaseFormat.RGBA;
				samplesPerPixel = 2;
				break;
			case 6://truecolor with alpha
				format = BaseFormat.RGBA;
				samplesPerPixel = 4;
				break;
		}
		
		if(bitDepth <= 8){
			type = TexDataType.UBYTE;
		}else{
			type = TexDataType.USHORT;
		}

		//skip CRC
		imageStream.skipBytes(4);
//		System.out.println(format);
//		System.out.println(bitDepth);
	}
	
	public ByteBuffer parse(){
		
		//segment in parenthesis is the factor to determine the number of bytes in the array
		//anything with a bitdepth of 16 will use shorts which is 2 bytes long and anything less will be converted to bytes of type RGB
		ByteBuffer image = BufferUtils.createByteBuffer(width*height*((format == BaseFormat.RGB ? 3 : 4)*Math.max(1, bitDepth/8)));
		ArrayList<Byte> idatBuffer = new ArrayList<Byte>();
		String chunkType;
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
						//buffer the image data
						for(int curByte = 0; curByte < chunkLength; curByte++){
							idatBuffer.add(imageStream.readByte());
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
			//transfer the data from the arraylist into a primitive buffer
			byte[] rawData = new byte[idatBuffer.size()];
			for(int curData = 0; curData < idatBuffer.size(); curData++){
				rawData[curData] = idatBuffer.get(curData);
			}
			//decompress the data
			byte[] imageData = decompress(rawData);
			//process the data
			process(image, imageData);
		}catch(Exception e){//if we fail, load a default image
			e.printStackTrace();
			try{
				imageStream.close();//we can close the stream now that we are done with it
			}catch(IOException io){
				io.printStackTrace();
			}
			return getDefault(width, height, false);
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
		//loop through the height of the image for each scanline
		for(int curScanline = 0; curScanline < height; curScanline++){
			byte filterType = imageData[curScanline*(scanlineSize+1)];
			byte[] currentScanline = new byte[scanlineSize];
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
				
				//unfilter then add the unfiltered value to the scanline buffer
				byte newValue = unfilter(filterType, (short)(0xff & current), (short)(0xff & prevCurLine), (short)(0xff & curPrevLine), (short)(0xff & prevPrevLine));
				currentScanline[curByte-1] = newValue;
				image.put(((height-1)-curScanline)*scanlineSize+curByte-1, newValue);
			}
			//update previous scanline with the current scanline for the next iteration
			prevScanline = currentScanline;
		}
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
	 * Parses the palette chunk of the png and returns it as a byte array
	 * 
	 * @param chunkLength Length of the chunk
	 * @return Palette as a byte array
	 */
	private void parsePLTE(int chunkLength) throws DataFormatException, IOException{
		//check if the palette is divisible by 3 if not throw an exception
		if(chunkLength%3 != 0){
			throw new DataFormatException("PLTE chunk not properly formatted unable to load image");
		}
		palette = new byte[PALETTE_LENGTH*3];//palette will have at most 256 elements each with 3 bytes for each color rgb
		for(int curByte = 0; curByte < chunkLength; curByte++){
			//fill each of the rgb bytes for the palette index
			palette[curByte] = imageStream.readByte();
			palette[curByte+1] = imageStream.readByte();
			palette[curByte+2] = imageStream.readByte();
		}
	}
	
}
