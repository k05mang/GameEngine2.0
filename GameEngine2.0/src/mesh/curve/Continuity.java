package mesh.curve;

public enum Continuity {
	C0,//there is no smoothness between the curves
	C1,//the boundaries between curves maintain co-linear control points around the shared point
	C2;//the boundaries between curves maintain co-linear control points around the shared point and are equidistant from the shared point
}
