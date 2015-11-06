package textures;

import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;

public enum DepthFormat {
	DEPTH16(GL_DEPTH_COMPONENT16),
	DEPTH24(GL_DEPTH_COMPONENT24),
	DEPTH32(GL_DEPTH_COMPONENT32),
	DEPTH32F(GL_DEPTH_COMPONENT32F),
	D24_S8(GL_DEPTH24_STENCIL8),
	D32F_S8(GL_DEPTH32F_STENCIL8),
	STENCIL1(GL_STENCIL_INDEX1),
	STENCIL4(GL_STENCIL_INDEX4),
	STENCIL8(GL_STENCIL_INDEX8),
	STENCIL16(GL_STENCIL_INDEX16);
	
	public final int value;
	
	private DepthFormat(int type){
		value = type;
	}

}
