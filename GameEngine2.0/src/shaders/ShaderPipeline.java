package shaders;

import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.opengl.GL45.*;

public class ShaderPipeline {

	private int id;
	
	public ShaderPipeline() {
		id = glCreateProgramPipelines();
	}
	
	public void bind(){
		glBindProgramPipeline(id);
	}
	
	public void unbind(){
		glBindProgramPipeline(0);
	}
	
	public void delete(){
		glDeleteProgramPipelines(id);
	}

	public void attachProgram(ShaderProgram program){
		glUseProgramStages(id, program.getStage().getUseBit(), program.getId());
	}
	
	public void disableStage(ShaderStage stage){
		glUseProgramStages(id, stage.getUseBit(), 0);
	}
	
	public String getInfoLog(){
		return glGetProgramPipelineInfoLog(id);
	}
}
