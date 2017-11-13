package mesh;

public enum MaterialType{
	DIFFUSE(0),
	NORMAL(1),
	SPECULAR(2),
	BUMP(3),
	COLOR(4);
	
	public final int textureUnit;
	
	private MaterialType(int type){
		textureUnit = type;
	}
};
