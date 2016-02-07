package shaders;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.opengl.GL43.*;

public enum ShaderStage {
	COMPUTE(GL_COMPUTE_SHADER, GL_COMPUTE_SHADER_BIT),
	VERTEX(GL_VERTEX_SHADER, GL_VERTEX_SHADER_BIT),
	TESS_CONTROL(GL_TESS_CONTROL_SHADER, GL_TESS_CONTROL_SHADER_BIT),
	TESS_EVAL(GL_TESS_EVALUATION_SHADER, GL_TESS_EVALUATION_SHADER_BIT),
	GEO(GL_GEOMETRY_SHADER, GL_GEOMETRY_SHADER_BIT),
	FRAG(GL_FRAGMENT_SHADER, GL_FRAGMENT_SHADER_BIT);
	
	public final int type, useBit;
	
	private ShaderStage(int type, int useBit){
		this.type = type;
		this.useBit = useBit;
	}
}
