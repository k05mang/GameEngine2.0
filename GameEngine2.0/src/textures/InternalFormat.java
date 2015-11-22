package textures;

import static org.lwjgl.opengl.GL11.GL_R3_G3_B2;
import static org.lwjgl.opengl.GL11.GL_RGB10;
import static org.lwjgl.opengl.GL11.GL_RGB10_A2;
import static org.lwjgl.opengl.GL11.GL_RGB12;
import static org.lwjgl.opengl.GL11.GL_RGB16;
import static org.lwjgl.opengl.GL11.GL_RGB4;
import static org.lwjgl.opengl.GL11.GL_RGB5;
import static org.lwjgl.opengl.GL11.GL_RGB5_A1;
import static org.lwjgl.opengl.GL11.GL_RGB8;
import static org.lwjgl.opengl.GL11.GL_RGBA12;
import static org.lwjgl.opengl.GL11.GL_RGBA16;
import static org.lwjgl.opengl.GL11.GL_RGBA2;
import static org.lwjgl.opengl.GL11.GL_RGBA4;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL13.GL_COMPRESSED_RGB;
import static org.lwjgl.opengl.GL13.GL_COMPRESSED_RGBA;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT16;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT24;
import static org.lwjgl.opengl.GL14.GL_DEPTH_COMPONENT32;
import static org.lwjgl.opengl.GL21.GL_COMPRESSED_SRGB;
import static org.lwjgl.opengl.GL21.GL_COMPRESSED_SRGB_ALPHA;
import static org.lwjgl.opengl.GL21.GL_SRGB8;
import static org.lwjgl.opengl.GL21.GL_SRGB8_ALPHA8;
import static org.lwjgl.opengl.GL30.GL_COMPRESSED_RED;
import static org.lwjgl.opengl.GL30.GL_COMPRESSED_RED_RGTC1;
import static org.lwjgl.opengl.GL30.GL_COMPRESSED_RG;
import static org.lwjgl.opengl.GL30.GL_COMPRESSED_RG_RGTC2;
import static org.lwjgl.opengl.GL30.GL_COMPRESSED_SIGNED_RED_RGTC1;
import static org.lwjgl.opengl.GL30.GL_COMPRESSED_SIGNED_RG_RGTC2;
import static org.lwjgl.opengl.GL30.GL_DEPTH24_STENCIL8;
import static org.lwjgl.opengl.GL30.GL_DEPTH32F_STENCIL8;
import static org.lwjgl.opengl.GL30.GL_DEPTH_COMPONENT32F;
import static org.lwjgl.opengl.GL30.GL_R11F_G11F_B10F;
import static org.lwjgl.opengl.GL30.GL_R16;
import static org.lwjgl.opengl.GL30.GL_R16F;
import static org.lwjgl.opengl.GL30.GL_R16I;
import static org.lwjgl.opengl.GL30.GL_R16UI;
import static org.lwjgl.opengl.GL30.GL_R32F;
import static org.lwjgl.opengl.GL30.GL_R32I;
import static org.lwjgl.opengl.GL30.GL_R32UI;
import static org.lwjgl.opengl.GL30.GL_R8;
import static org.lwjgl.opengl.GL30.GL_R8I;
import static org.lwjgl.opengl.GL30.GL_R8UI;
import static org.lwjgl.opengl.GL30.GL_RG16;
import static org.lwjgl.opengl.GL30.GL_RG16F;
import static org.lwjgl.opengl.GL30.GL_RG16I;
import static org.lwjgl.opengl.GL30.GL_RG16UI;
import static org.lwjgl.opengl.GL30.GL_RG32F;
import static org.lwjgl.opengl.GL30.GL_RG32I;
import static org.lwjgl.opengl.GL30.GL_RG32UI;
import static org.lwjgl.opengl.GL30.GL_RG8;
import static org.lwjgl.opengl.GL30.GL_RG8I;
import static org.lwjgl.opengl.GL30.GL_RG8UI;
import static org.lwjgl.opengl.GL30.GL_RGB16F;
import static org.lwjgl.opengl.GL30.GL_RGB16I;
import static org.lwjgl.opengl.GL30.GL_RGB16UI;
import static org.lwjgl.opengl.GL30.GL_RGB32F;
import static org.lwjgl.opengl.GL30.GL_RGB32I;
import static org.lwjgl.opengl.GL30.GL_RGB32UI;
import static org.lwjgl.opengl.GL30.GL_RGB8I;
import static org.lwjgl.opengl.GL30.GL_RGB8UI;
import static org.lwjgl.opengl.GL30.GL_RGB9_E5;
import static org.lwjgl.opengl.GL30.GL_RGBA16F;
import static org.lwjgl.opengl.GL30.GL_RGBA16I;
import static org.lwjgl.opengl.GL30.GL_RGBA16UI;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL30.GL_RGBA32I;
import static org.lwjgl.opengl.GL30.GL_RGBA32UI;
import static org.lwjgl.opengl.GL30.GL_RGBA8I;
import static org.lwjgl.opengl.GL30.GL_RGBA8UI;
import static org.lwjgl.opengl.GL30.GL_STENCIL_INDEX1;
import static org.lwjgl.opengl.GL30.GL_STENCIL_INDEX16;
import static org.lwjgl.opengl.GL30.GL_STENCIL_INDEX4;
import static org.lwjgl.opengl.GL30.GL_STENCIL_INDEX8;
import static org.lwjgl.opengl.GL31.GL_R16_SNORM;
import static org.lwjgl.opengl.GL31.GL_R8_SNORM;
import static org.lwjgl.opengl.GL31.GL_RG16_SNORM;
import static org.lwjgl.opengl.GL31.GL_RG8_SNORM;
import static org.lwjgl.opengl.GL31.GL_RGB16_SNORM;
import static org.lwjgl.opengl.GL31.GL_RGB8_SNORM;
import static org.lwjgl.opengl.GL31.GL_RGBA16_SNORM;
import static org.lwjgl.opengl.GL31.GL_RGBA8_SNORM;
import static org.lwjgl.opengl.GL33.GL_RGB10_A2UI;
import static org.lwjgl.opengl.GL41.GL_RGB565;
import static org.lwjgl.opengl.GL42.GL_COMPRESSED_RGBA_BPTC_UNORM;
import static org.lwjgl.opengl.GL42.GL_COMPRESSED_RGB_BPTC_SIGNED_FLOAT;
import static org.lwjgl.opengl.GL42.GL_COMPRESSED_RGB_BPTC_UNSIGNED_FLOAT;
import static org.lwjgl.opengl.GL42.GL_COMPRESSED_SRGB_ALPHA_BPTC_UNORM;
import static org.lwjgl.opengl.GL43.GL_COMPRESSED_R11_EAC;
import static org.lwjgl.opengl.GL43.GL_COMPRESSED_RG11_EAC;
import static org.lwjgl.opengl.GL43.GL_COMPRESSED_RGB8_ETC2;
import static org.lwjgl.opengl.GL43.GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2;
import static org.lwjgl.opengl.GL43.GL_COMPRESSED_RGBA8_ETC2_EAC;
import static org.lwjgl.opengl.GL43.GL_COMPRESSED_SIGNED_R11_EAC;
import static org.lwjgl.opengl.GL43.GL_COMPRESSED_SIGNED_RG11_EAC;
import static org.lwjgl.opengl.GL43.GL_COMPRESSED_SRGB8_ALPHA8_ETC2_EAC;
import static org.lwjgl.opengl.GL43.GL_COMPRESSED_SRGB8_ETC2;
import static org.lwjgl.opengl.GL43.GL_COMPRESSED_SRGB8_PUNCHTHROUGH_ALPHA1_ETC2;

public enum InternalFormat {
//	--------------------Color formats--------------------
	R8(GL_R8),
	R8_SNORM(GL_R8_SNORM),
	R16(GL_R16),
	R16_SNORM(GL_R16_SNORM),
	RG8(GL_RG8),
	RG8_SNORM(GL_RG8_SNORM),
	RG16(GL_RG16),
	RG16_SNORM(GL_RG16_SNORM),
	R3_G3_B2(GL_R3_G3_B2),
	RGB4(GL_RGB4),
	RGB5(GL_RGB5),
	RGB565(GL_RGB565),
	RGB8(GL_RGB8),
	RGB8_SNORM(GL_RGB8_SNORM),
	RGB10(GL_RGB10),
	RGB12(GL_RGB12),
	RGB16(GL_RGB16),
	RGB16_SNORM(GL_RGB16_SNORM),
	RGBA2(GL_RGBA2),
	RGBA4(GL_RGBA4),
	RGB5_A1(GL_RGB5_A1),
	RGBA8(GL_RGBA8),
	RGBA8_SNORM(GL_RGBA8_SNORM),
	RGB10_A2(GL_RGB10_A2),
	RGB10_A2UI(GL_RGB10_A2UI),
	RGBA12(GL_RGBA12),
	RGBA16(GL_RGBA16),
	RGBA16_SNORM(GL_RGBA16_SNORM),
	SRGB8(GL_SRGB8),
	SRGB8_ALPHA8(GL_SRGB8_ALPHA8),
	R16F(GL_R16F),
	RG16F(GL_RG16F),
	RGB16F(GL_RGB16F),
	RGBA16F(GL_RGBA16F),
	R32F(GL_R32F),
	RG32F(GL_RG32F),
	RGB32F(GL_RGB32F),
	RGBA32F(GL_RGBA32F),
	R11F_G11F_B10F(GL_R11F_G11F_B10F),
	RGB9_E5(GL_RGB9_E5),
	R8I(GL_R8I),
	R8UI(GL_R8UI),
	R16I(GL_R16I),
	R16UI(GL_R16UI),
	R32I(GL_R32I),
	R32UI(GL_R32UI),
	RG8I(GL_RG8I),
	RG8UI(GL_RG8UI),
	RG16I(GL_RG16I),
	RG16UI(GL_RG16UI),
	RG32I(GL_RG32I),
	RG32UI(GL_RG32UI),
	RGB8I(GL_RGB8I),
	RGB8UI(GL_RGB8UI),
	RGB16I(GL_RGB16I),
	RGB16UI(GL_RGB16UI),
	RGB32I(GL_RGB32I),
	RGB32UI(GL_RGB32UI),
	RGBA8I(GL_RGBA8I),
	RGBA8UI(GL_RGBA8UI),
	RGBA16I(GL_RGBA16I),
	RGBA16UI(GL_RGBA16UI),
	RGBA32I(GL_RGBA32I),
	RGBA32UI(GL_RGBA32UI),
//	--------------------Depth Formats--------------------
	DEPTH16(GL_DEPTH_COMPONENT16),
	DEPTH24(GL_DEPTH_COMPONENT24),
	DEPTH32(GL_DEPTH_COMPONENT32),
	DEPTH32F(GL_DEPTH_COMPONENT32F),
	D24_S8(GL_DEPTH24_STENCIL8),
	D32F_S8(GL_DEPTH32F_STENCIL8),
	STENCIL1(GL_STENCIL_INDEX1),
	STENCIL4(GL_STENCIL_INDEX4),
	STENCIL8(GL_STENCIL_INDEX8),
	STENCIL16(GL_STENCIL_INDEX16),
//	--------------------Compressed Formats--------------------
	RED(GL_COMPRESSED_RED),
	RG(GL_COMPRESSED_RG),
	RGB(GL_COMPRESSED_RGB),
	RGBA(GL_COMPRESSED_RGBA),
	SRGB(GL_COMPRESSED_SRGB),
	SRGB_ALPHA(GL_COMPRESSED_SRGB_ALPHA),
	RED_RGTC1(GL_COMPRESSED_RED_RGTC1),
	SIGNED_RED_RGTC1(GL_COMPRESSED_SIGNED_RED_RGTC1),
	RG_RGTC2(GL_COMPRESSED_RG_RGTC2),
	SIGNED_RG_RGTC2(GL_COMPRESSED_SIGNED_RG_RGTC2),
	RGBA_BPTC_UNORM(GL_COMPRESSED_RGBA_BPTC_UNORM),
	SRGB_ALPHA_BPTC_UNORM(GL_COMPRESSED_SRGB_ALPHA_BPTC_UNORM),
	RGB_BPTC_SIGNED_FLOAT(GL_COMPRESSED_RGB_BPTC_SIGNED_FLOAT),
	RGB_BPTC_UNSIGNED_FLOAT(GL_COMPRESSED_RGB_BPTC_UNSIGNED_FLOAT),
	RGB8_ETC2(GL_COMPRESSED_RGB8_ETC2),
	SRGB8_ETC2(GL_COMPRESSED_SRGB8_ETC2),
	RGB8_PUNCHTHROUGH_ALPHA1_ETC2(GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2),
	SRGB8_PUNCHTHROUGH_ALPHA1_ETC2(GL_COMPRESSED_SRGB8_PUNCHTHROUGH_ALPHA1_ETC2),
	RGBA8_ETC2_EAC(GL_COMPRESSED_RGBA8_ETC2_EAC),
	SRGB8_ALPHA8_ETC2_EAC(GL_COMPRESSED_SRGB8_ALPHA8_ETC2_EAC),
	R11_EAC(GL_COMPRESSED_R11_EAC),
	SIGNED_R11_EAC(GL_COMPRESSED_SIGNED_R11_EAC),
	RG11_EAC(GL_COMPRESSED_RG11_EAC),
	SIGNED_RG11_EAC(GL_COMPRESSED_SIGNED_RG11_EAC);
	
	public final int value;
	
	private InternalFormat(int type){
		value = type;
	}
	
	/**
	 * Determines whether the current format is a color format
	 * 
	 * @return True if it is a color format false otherwise
	 */
	public boolean isColorFormat(){
		if(
			value == GL_R8 || 
			value == GL_R8_SNORM || 
			value == GL_R16 || 
			value == GL_R16_SNORM || 
			value == GL_RG8 || 
			value == GL_RG8_SNORM || 
			value == GL_RG16 || 
			value == GL_RG16_SNORM || 
			value == GL_R3_G3_B2 || 
			value == GL_RGB4 || 
			value == GL_RGB5 || 
			value == GL_RGB565 || 
			value == GL_RGB8 || 
			value == GL_RGB8_SNORM || 
			value == GL_RGB10 || 
			value == GL_RGB12 || 
			value == GL_RGB16 || 
			value == GL_RGB16_SNORM || 
			value == GL_RGBA2 || 
			value == GL_RGBA4 || 
			value == GL_RGB5_A1 || 
			value == GL_RGBA8 || 
			value == GL_RGBA8_SNORM || 
			value == GL_RGB10_A2 || 
			value == GL_RGB10_A2UI || 
			value == GL_RGBA12 || 
			value == GL_RGBA16 || 
			value == GL_RGBA16_SNORM || 
			value == GL_SRGB8 || 
			value == GL_SRGB8_ALPHA8 || 
			value == GL_R16F || 
			value == GL_RG16F || 
			value == GL_RGB16F || 
			value == GL_RGBA16F || 
			value == GL_R32F || 
			value == GL_RG32F || 
			value == GL_RGB32F || 
			value == GL_RGBA32F || 
			value == GL_R11F_G11F_B10F || 
			value == GL_RGB9_E5 || 
			value == GL_R8I || 
			value == GL_R8UI || 
			value == GL_R16I || 
			value == GL_R16UI || 
			value == GL_R32I || 
			value == GL_R32UI || 
			value == GL_RG8I || 
			value == GL_RG8UI || 
			value == GL_RG16I || 
			value == GL_RG16UI || 
			value == GL_RG32I || 
			value == GL_RG32UI || 
			value == GL_RGB8I || 
			value == GL_RGB8UI || 
			value == GL_RGB16I || 
			value == GL_RGB16UI || 
			value == GL_RGB32I || 
			value == GL_RGB32UI || 
			value == GL_RGBA8I || 
			value == GL_RGBA8UI || 
			value == GL_RGBA16I || 
			value == GL_RGBA16UI || 
			value == GL_RGBA32I || 
			value == GL_RGBA32UI		
		){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Determines whether the current format is a depth format
	 * 
	 * @return True if it is a depth format false otherwise
	 */
	public boolean isDepthFormat(){
		if(
			value == GL_DEPTH_COMPONENT16 || 
			value == GL_DEPTH_COMPONENT24 || 
			value == GL_DEPTH_COMPONENT32 || 
			value == GL_DEPTH_COMPONENT32F || 
			value == GL_DEPTH24_STENCIL8 || 
			value == GL_DEPTH32F_STENCIL8 || 
			value == GL_STENCIL_INDEX1 || 
			value == GL_STENCIL_INDEX4 || 
			value == GL_STENCIL_INDEX8 || 
			value == GL_STENCIL_INDEX16		
		){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Determines whether the current format is a compressed format
	 * 
	 * @return True if it is a compressed format false otherwise
	 */
	public boolean isCompressedFormat(){
		if(
			value == GL_COMPRESSED_RED || 
			value == GL_COMPRESSED_RG || 
			value == GL_COMPRESSED_RGB || 
			value == GL_COMPRESSED_RGBA || 
			value == GL_COMPRESSED_SRGB || 
			value == GL_COMPRESSED_SRGB_ALPHA || 
			value == GL_COMPRESSED_RED_RGTC1 || 
			value == GL_COMPRESSED_SIGNED_RED_RGTC1 || 
			value == GL_COMPRESSED_RG_RGTC2 || 
			value == GL_COMPRESSED_SIGNED_RG_RGTC2 || 
			value == GL_COMPRESSED_RGBA_BPTC_UNORM || 
			value == GL_COMPRESSED_SRGB_ALPHA_BPTC_UNORM || 
			value == GL_COMPRESSED_RGB_BPTC_SIGNED_FLOAT || 
			value == GL_COMPRESSED_RGB_BPTC_UNSIGNED_FLOAT || 
			value == GL_COMPRESSED_RGB8_ETC2 || 
			value == GL_COMPRESSED_SRGB8_ETC2 || 
			value == GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2 || 
			value == GL_COMPRESSED_SRGB8_PUNCHTHROUGH_ALPHA1_ETC2 || 
			value == GL_COMPRESSED_RGBA8_ETC2_EAC || 
			value == GL_COMPRESSED_SRGB8_ALPHA8_ETC2_EAC || 
			value == GL_COMPRESSED_R11_EAC || 
			value == GL_COMPRESSED_SIGNED_R11_EAC || 
			value == GL_COMPRESSED_RG11_EAC || 
			value == GL_COMPRESSED_SIGNED_RG11_EAC		
		){
			return true;
		}else{
			return false;
		}
	}
}
