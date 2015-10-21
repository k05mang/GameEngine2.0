package shaders;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL43.*;

public enum ShaderType {
	COMPUTE(GL_COMPUTE_SHADER),
	VERTEX(GL_VERTEX_SHADER),
	TESS_CONTROL(GL_TESS_CONTROL_SHADER),
	TESS_EVAEL(GL_TESS_EVALUATION_SHADER),
	GEO(GL_GEOMETRY_SHADER),
	FRAG(GL_FRAGMENT_SHADER);
	
	public final int type;
	
	private ShaderType(int type){
		this.type = type;
	}
}
