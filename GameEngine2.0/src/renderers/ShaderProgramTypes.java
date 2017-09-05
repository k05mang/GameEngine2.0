package renderers;

public enum ShaderProgramTypes {

	PASS_THROUGH("pass-through"),
	GEOMETRY("geometry"),
	LIGHT("light"),
	STENCIL("stencil"),
	POST_PROCESS("post-process:");
	
	public final String name;
	
	private ShaderProgramTypes (String value){
		name = value;
	}
}
