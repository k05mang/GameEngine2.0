package shaders;

import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.opengl.GL45.*;

public class ShaderPipeline {

	private int id;
	
	/**
	 * Creates a shader program pipeline object
	 */
	public ShaderPipeline() {
		id = glCreateProgramPipelines();
	}
	
	/**
	 * Binds the shader program pipeline to the current gl context
	 */
	public void bind(){
		glBindProgramPipeline(id);
	}
	
	/**
	 * Unbinds the shader program pipeline to the current gl context
	 */
	public void unbind(){
		glBindProgramPipeline(0);
	}
	
	/**
	 * Deletes this shader program pipeline from the gl context
	 */
	public void delete(){
		glDeleteProgramPipelines(id);
	}
	
	/**
	 * Attaches the given {@code program} to the this pipelines shader {@code stages}. The shader
	 * program is bound to the given shader stages of this pipeline object only if
	 * the program is separable and successfully linked.
	 *  
	 * @param program ShaderProgram to bind executables from
	 * @param stages Shader stages to bind the programs executables to in this pipeline object
	 */
	public void attachProgram(ShaderProgram program, ShaderStage... stages){
		if(program.isSeparable() && program.isLinked()){
			int stageBits = 0;
			for(ShaderStage stage : stages){
				stageBits |= stage.useBit;
			}
			glUseProgramStages(id, stageBits, program.getId());
		}
	}

	/**
	 * Attaches the given {@code program} to this pipeline object, the program is bound to all the 
	 * shader stages that the program object has executable code for. Additionally the program 
	 * is only bound to the pipeline object if it is separable and successfully linked.
	 * 
	 * @param program ShaderProgram to bind to the pipeline at all the shader stages it is linked for
	 */
	public void attachProgram(ShaderProgram program){
		if(program.isSeparable() && program.isLinked()){
			glUseProgramStages(id, program.getShaderStages(), program.getId());
		}
	}
	
	/**
	 * Disables the given shader stage from this pipeline object, this effectively unbinds the program executable 
	 * from the given stage of this pipeline.
	 * 
	 * @param stage Stage to disable in this pipeline object
	 */
	public void detachProgramAt(ShaderStage stage){
		glUseProgramStages(id, stage.useBit, 0);
	}
	
	/**
	 * Gets the shader program pipeline info log
	 * 
	 * @return Pipeline object info log
	 */
	public String getInfoLog(){
		return glGetProgramPipelineInfoLog(id);
	}
}
