package framebuffer;

import static org.lwjgl.opengl.GL43.*;

public enum FrameBufferParam {
	DEFAULT_WIDTH(GL_FRAMEBUFFER_DEFAULT_WIDTH),
	DEFAULT_HEIGHT(GL_FRAMEBUFFER_DEFAULT_HEIGHT),
	DEFAULT_LAYERS(GL_FRAMEBUFFER_DEFAULT_LAYERS), 
	DEFAULT_SAMPLES(GL_FRAMEBUFFER_DEFAULT_SAMPLES),
	FIXED_SAMPLE_LOCATIONS(GL_FRAMEBUFFER_DEFAULT_FIXED_SAMPLE_LOCATIONS);
	
	public final int value;
	
	private FrameBufferParam(int type){
		value = type;
	}
}
