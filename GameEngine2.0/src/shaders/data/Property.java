package shaders.data;
import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.opengl.GL44.*;

public enum Property {

	ACTIVE_VARIABLES(GL_ACTIVE_VARIABLES),
	BUFFER_BINDING(GL_BUFFER_BINDING),
	NUM_ACTIVE_VARIABLES(GL_NUM_ACTIVE_VARIABLES),
	ARRAY_SIZE(GL_ARRAY_SIZE),
	ARRAY_STRIDE(GL_ARRAY_STRIDE),
	BLOCK_INDEX(GL_BLOCK_INDEX),
	IS_ROW_MAJOR(GL_IS_ROW_MAJOR),
	MATRIX_STRIDE(GL_MATRIX_STRIDE),
	ATOMIC_COUNTER_BUFFER_INDEX(GL_ATOMIC_COUNTER_BUFFER_INDEX),
	BUFFER_DATA_SIZE(GL_BUFFER_DATA_SIZE),
	NUM_COMPATIBLE_SUBROUTINES(GL_NUM_COMPATIBLE_SUBROUTINES),
	COMPATIBLE_SUBROUTINES(GL_COMPATIBLE_SUBROUTINES),
	IS_PER_PATCH(GL_IS_PER_PATCH),
	LOCATION(GL_LOCATION),
	LOCATION_COMPONENT(GL_LOCATION_COMPONENT),
	LOCATION_INDEX(GL_LOCATION_INDEX),
	NAME_LENGTH(GL_NAME_LENGTH),
	OFFSET(GL_OFFSET),
	REFERENCED_BY_VERTEX_SHADER(GL_REFERENCED_BY_VERTEX_SHADER),
	REFERENCED_BY_TESS_CONTROL_SHADER(GL_REFERENCED_BY_TESS_CONTROL_SHADER),
	REFERENCED_BY_TESS_EVALUATION_SHADER(GL_REFERENCED_BY_TESS_EVALUATION_SHADER),
	REFERENCED_BY_GEOMETRY_SHADER(GL_REFERENCED_BY_GEOMETRY_SHADER),
	REFERENCED_BY_FRAGMENT_SHADER(GL_REFERENCED_BY_FRAGMENT_SHADER),
	REFERENCED_BY_COMPUTE_SHADER(GL_REFERENCED_BY_COMPUTE_SHADER),
	TRANSFORM_FEEDBACK_BUFFER_INDEX(GL_TRANSFORM_FEEDBACK_BUFFER_INDEX),
	TRANSFORM_FEEDBACK_BUFFER_STRIDE(GL_TRANSFORM_FEEDBACK_BUFFER_STRIDE),
	TOP_LEVEL_ARRAY_SIZE(GL_TOP_LEVEL_ARRAY_SIZE),
	TOP_LEVEL_ARRAY_STRIDE(GL_TOP_LEVEL_ARRAY_STRIDE),
	TYPE(GL_TYPE);

	public final int value;
	
	private Property(int type){
		value = type;
	}
}
