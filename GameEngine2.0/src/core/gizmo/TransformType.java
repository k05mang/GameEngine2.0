package core.gizmo;

public enum TransformType {
	
	TRANSLATE(0),
	SCALE(1),
	ROTATE(2);
	
	public final int type;
	
	private TransformType(int value){
		type = value;
	}
}
