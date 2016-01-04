package primitives;
import java.util.ArrayList;

import glMath.VecUtil;
import glMath.vectors.Vec3;

public class BezierPath {
	private ArrayList<Vec3> points;
	BezierCurve curve;
	private final int MIN_SEGMENTS = 50;
	
	/**
	 * Constructs a Bezier path or curve using the given starting points
	 * 
	 * @param p0 Start point of the curve
	 * @param p1 Mid control point of the curve
	 * @param p2 End point of the curve
	 * @param initPoints Additional points to be added to the curve during initial construction
	 */
	public BezierPath(Vec3 p0, Vec3 p1, Vec3 p2, Vec3... initPoints){
		points = new ArrayList<Vec3>(initPoints.length+3);
		points.add(p0);
		points.add(p1);
		points.add(p2);
		for(Vec3 curPoint : initPoints){
			points.add(curPoint);
		}
		curve = null;
	}
	
	/**
	 * Adds a point to the curve, this new point will become the new end point for the curve,
	 * and the previous end point will become a control point along the curve
	 * 
	 * @param point New point to add
	 */
	public void add(Vec3 point){
		points.add(point);
	}
	
	/**
	 * Adds a set of points to the bezier path
	 * 
	 * @param points Points to add to the bezier curve
	 */
	public void add(Vec3... points){
		for(Vec3 point : points){
			this.points.add(point);
		}
	}
	
	/**
	 * Adds a set of points to the bezier path
	 * 
	 * @param points Points to add to the bezier curve
	 */
	public void add(ArrayList<Vec3> points){
		for(Vec3 point : points){
			this.points.add(point);
		}
	}
	
	/**
	 * Gets a point on this curve at the given point t along the curve. 
	 * If the value of t is greater than 1 then the end point of the curve will be returned.
	 * If the value of t is less than 0 then the start point will be returned.
	 * 
	 * @param t Value between 0 and 1 that defines a point along the curve
	 * @return A point at t along the curve
	 */
	public Vec3 getBezierPoint(float t){
		if(t >= 1){
			return points.get(points.size()-1);
		}else if(t <= 0){
			return points.get(0);
		}else{
			return getPoint(t, points);
		}
	}
	
	/**
	 * Uses de Casteljau's algorithm to calculate a point on the curve at t using a subdividing recursion
	 * 
	 * @param t Point along the curve to find
	 * @param subPoints Previously computed points used in de Casteljau's algorithm
	 * @return Final computed point
	 */
	private Vec3 getPoint(float t, ArrayList<Vec3> subPoints){
		if(subPoints.size() == 1){//indicates we have calculated the point on the curve at t
			return subPoints.get(0);
		}else{
			//else continue following de Casteljau's algorithm and subdivide
			ArrayList<Vec3> newPoints = new ArrayList<Vec3>(subPoints.size()-1);
			for(int curPoint = 0; curPoint < subPoints.size()-1; curPoint++){
				newPoints.add(
						VecUtil.scale(subPoints.get(curPoint), 1-t).add( VecUtil.scale(subPoints.get(curPoint+1), t))
						);
			}
			return getPoint(t, newPoints);
		}
	}
	
	/**
	 * Increases the order of the curve by 1 while preserving the curve
	 */
	public void increaseOrder(){
		ArrayList<Vec3> newPoints = new ArrayList<Vec3>(points.size()+1);
		newPoints.add(points.get(0));
		int n = points.size()-1;
		//loop through the current control points and modify them for the new point
		for(int curPoint = 0; curPoint < n; curPoint++){
			float factor = (curPoint+1)/(float)(n+1);
			newPoints.add(
					VecUtil.scale(points.get(curPoint), factor).add( VecUtil.scale(points.get(curPoint+1), 1-factor))
					);
		}
		//add original end point
		newPoints.add(points.get(points.size()-1));
		points = newPoints;
		//update the curve that renders this path if it is available
		if(curve != null){
			curve.constructCurve(MIN_SEGMENTS+(n+1)*20);
		}
	}
	
	/**
	 * Decreases the order of the curve by 1 while attempting to preserve the curve
	 */
	public void decreaseOrder(){
		if(points.size()-1 > 2){
			
		}
	}
	
	/**
	 * Moves the control point of this curve at the given {@code pIndex}, by the given translation vector
	 * 
	 * @param pIndex Index of the control point in this curve to modify
	 * @param translation Amount to move the control point at {@code pIndex} by
	 */
	public void movePoint(int pIndex, Vec3 translation){
		movePoint(pIndex, translation.x, translation.y, translation.z);
	}
	
	/**
	 * Moves the control point of this curve at the given {@code pIndex}, by the given translation amounts
	 * 
	 * @param pIndex Index of the control point in this curve to modify
	 * @param x X amount to translate by
	 * @param y Y amount to translate by
	 * @param z Z amount to translate by
	 */
	public void movePoint(int pIndex, float x, float y, float z){
		//do a bounds check
		if(pIndex < points.size() && pIndex > 0){
			points.get(pIndex).add(x, y, z);
			//if there is a renderable curve associated with this path update it
			if(curve != null){
				curve.updateCurve();
			}
		}
	}
	
	/**
	 * Gets the points that define the curve, these points are the start and end points, and the control points of the curve
	 * 
	 * @return All defining points of the curve
	 */
	public ArrayList<Vec3> getPoints(){
		return points;
	}
	
	/**
	 * Gets the number of points that define this curve including the start and end points
	 * 
	 * @return Number of points that define the curve
	 */
	public int numPoints(){
		return points.size();
	}
}
