package shaders;
import static org.lwjgl.opengl.GL41.*;

public enum ProgramParam {

	SEPARABLE(GL_PROGRAM_SEPARABLE),
	BINARY_RETRIEVABLE(GL_PROGRAM_BINARY_RETRIEVABLE_HINT);
	
	public final int value;
	
	private ProgramParam(int type){
		value = type;
	}
}
