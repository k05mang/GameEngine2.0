package mesh.curve;

public enum Continuity {
	C0,//there is no smoothness between the curves
	G1,//the boundaries between curves maintain co-linear control points around the shared point
	C1,//criteria of G1 and are equidistant from the shared point (joint is mid point)
	C2;//criteria of C1 and the distance between the next point and previous point to the adjacent controls is equal to 4 times the distance between the joint and adjacent control
}
