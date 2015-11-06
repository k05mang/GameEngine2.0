package textures;

import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL42.*;
import static org.lwjgl.opengl.GL43.*;

public enum CompressedFormat {
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
	
	private CompressedFormat(int type){
		value = type;
	}

}
