package mesh.curve;
import java.util.ArrayList;
import java.util.Arrays;

import glMath.VecUtil;
import glMath.vectors.Vec3;
/**
 * Class representing the mathematical form of a bezier curve
 * 
 * @author Kevin Mango
 *
 */
public class BezierPath {
	
	private ArrayList<Vec3> points;//control points of the curve
	BezierCurve curve;//renderable curve object that is associated with this curve
	private BezierPath derivative;
	private final float T_STEP = .001f;
//	private static ArrayList<int[]> binomialLUT = new ArrayList<int[]>();
	private int n;//nth order of the curve
	private float length;
	private boolean needsUpdate = false;//variable for flagging whether there needs to be an update to the length and derivative
	//due to changes in the bezier curve
	
	/**
	 * Constructs a Bezier path or curve using the given starting points
	 * 
	 * @param initPoints Points used in the initial construction of the curve
	 */
	public BezierPath(Vec3... initPoints){
		derivative = null;
		points = new ArrayList<Vec3>(initPoints.length);
		for(Vec3 curPoint : initPoints){
			points.add(new Vec3(curPoint));
		}
		curve = null;
		n = initPoints.length-1;
		needsUpdate = true;
	}
	
	/**
	 * Constructs a Bezier path or curve using the given starting points
	 * 
	 * @param initPoints Points used in the initial construction of the curve
	 */
	public BezierPath(ArrayList<Vec3> initPoints){
		derivative = null;
		points = new ArrayList<Vec3>(initPoints.size());
		for(Vec3 curPoint : initPoints){
			points.add(new Vec3(curPoint));
		}
		curve = null;
		n = initPoints.size()-1;
		needsUpdate = true;
	}
	
	/**
	 * Adds a point to the curve, this new point will become the new end point for the curve,
	 * and the previous end point will become a control point along the curve
	 * 
	 * @param point New point to add
	 */
	public void add(Vec3 point){
		points.add(point);
		n++;//increase the order of the curve
		//if there is a renderable curve associated with this curve update it
		if(curve != null){
			curve.updateCurve();
		}
		needsUpdate = true;
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
		n += points.length;
		if(curve != null){
			curve.updateCurve();
		}
		needsUpdate = true;
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
		n += points.size();
		if(curve != null){
			curve.updateCurve();
		}
		needsUpdate = true;
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
		//clamp the range for t
		if(t >= 1){
			return points.get(points.size()-1);
		}else if(t <= 0){
			return points.get(0);
		}else{
			//if t is between 1 and 0 then call the compute method
			return getPoint(t, points);
		}
	}
	
	/**
	 * Gets the unnormalized tangent line at the given point along the Bezier path
	 * 
	 * @param t Value between 0 and 1 specifying the point along the curve to query
	 * @return Tangent vector at the point t along the curve
	 */
	public Vec3 getTangent(float t){
		update();
		return derivative.getBezierPoint(t);
	}
	
	/**
	 * Gets the normal for the point on the curve defined by {@code t}
	 * 
	 * @param t Value between 0 and 1 defining a point along the curve to retrieve the normal of
	 * @return The normal at the given point on the curve
	 */
	public Vec3 getNormal(float t){
		update();
		//clamp t first
//		float clampT = Math.max(0, Math.min(1, t));
//		//TODO: update how this works, using the cross product might not work all the time
//		if(t >= 1){
//			return getBezierPoint(t-T_STEP).cross(getTangent(t));
//		}else{
//			return getBezierPoint(t+T_STEP).cross(getTangent(t));
//		}
		return getBezierPoint(t+T_STEP).cross(getTangent(t));
	}
	
	/**
	 * Gets the length of the Bezier curve
	 * 
	 * @return The length of the bezier curve
	 */
	public float getLength(){
		update();
		return length;
	}
	
	/**
	 * Gets the distance point {@code t} is along the curve
	 * 
	 * @param t Value between 0 and 1 specifying a point along the curve
	 * @return Distance point {@code t} is along the curve
	 */
	public float getDistanceAt(float t){
		update();
		return t*length;
	}
	
	/**
	 * Splits this Bezier path at the point {@code t}, separating it into two halves.
	 * 
	 * @param t Point between 0 and 1 that the Bezier path will be split at
	 * @return List containing two Bezier paths with index 0 being the portion of the curve from the start
	 * to the split point t, and the path in index 1 being the portion from point t to the end
	 */
	public BezierPath[] split(float t){
		//bare minimum of at least n+1 paths for n points of splitting
		BezierPath[] paths = new BezierPath[2];
		
		//create lists to hold the left and right portions of the new bezier paths
		ArrayList<Vec3> leftPoints = new ArrayList<Vec3>(points.size());
		ArrayList<Vec3> rightPoints = new ArrayList<Vec3>(points.size());
		
		//this runs on a modified De Casteljau where in points from the recursive steps are stored
		//run the modified version of the algorithm
		splitHalf(t, points, leftPoints, rightPoints);
		
		//using the list of control points from the previous algorithm run, generate the new bezier paths with the left and right Arrays
		paths[0] = new BezierPath(leftPoints);
		paths[1] = new BezierPath(rightPoints);
		
		return paths;
	}
	
	/**
	 * Runs through De Casteljau's algorithm to obtain the points for the split on {@code t} of this Bezier path
	 * with each point being place in the left and right array lists.
	 * 
	 * @param t Point to split at
	 * @param subList List of new points for the subdivided control points of the curve
	 * @param left List of control points for the portion of the curve from the start of this curve to {@code t}
	 * @param right List of control points for the portion of the curve from {@code t} to the end of this curve
	 */
	private void splitHalf(float t, ArrayList<Vec3> subPoints, ArrayList<Vec3> left, ArrayList<Vec3> right){
		if(subPoints.isEmpty()){
			return;//simply end the recursion
		}else if(subPoints.size() == 1){
			//add the final point t to the lists
			//add t to the end of the left list since it is the last point
			left.add(subPoints.get(0));
			//add t to the beginning of the right list since it will be the start point
			right.add(0, subPoints.get(0));
		}else{
			//else continue following de Casteljau's algorithm and subdivide
			//this will be 1 smaller than the previous list
			ArrayList<Vec3> newPoints = new ArrayList<Vec3>(subPoints.size()-1);//create a list to hold the new set of points
			
			//add the first point from the sublist to the left list
			left.add(subPoints.get(0));
			
			//add the last point from the sublist to the right list
			//insert to the front of the list since the recursion generates the right half starting with last element in its list
			//this keeps the right list and by extension the right bezier path consistent with the original curve
			right.add(0, subPoints.get(subPoints.size()-1));
			
			//create the new list of points
			for(int curPoint = 0; curPoint < subPoints.size()-1; curPoint++){
				//get the new point as the point (100-x)% from the start and x% from the end where 
				newPoints.add(
						VecUtil.scale(subPoints.get(curPoint), 1-t).
						add( 
							VecUtil.scale(subPoints.get(curPoint+1), t)
							)
						);
			}
			//recurse with the smaller point set
			splitHalf(t, newPoints, left, right);
		}
	}
	
	/**
	 * Computes the length of the curve
	 */
	private void computeLength(){
		if(points.size() < 2){
			length = 0f;
		}else if(points.size() == 2){
			length = VecUtil.subtract(points.get(0), points.get(1)).length();
		}else{
			length = 0f;
			Vec3 basePoint = new Vec3(getBezierPoint(0));
			for(float t = T_STEP; t < 1.0f; t += T_STEP){
				Vec3 nextPoint = getBezierPoint(t);
				length += VecUtil.subtract(nextPoint, basePoint).length();
				basePoint.set(nextPoint);
			}
		}
	}
	
	/**
	 * Uses 1 to calculate a point on the curve at t using a subdividing recursion
	 * 
	 * @param t Point along the curve to find
	 * @param subPoints Previously computed points used in De Casteljau's algorithm
	 * @return Final computed point
	 */
	private Vec3 getPoint(float t, ArrayList<Vec3> subPoints){
		//From http://pomax.github.io/bezierinfo/
//		1.Treat t as a ratio (which it is). t=0 is 0% along a line, t=1 is 100% along a line.
//		2.Take all lines between the curve's defining points. For an order n curve, that's n lines.
//		3.Place markers along each of these line, at distance t. So if t is 0.2, place the mark at 20% from the start, 80% from the end.
//		4.Now form lines between those points. This gives n-1 lines.
//		5.Place markers along each of these line at distance t.
//		6.Form lines between those points. This'll be n-2 lines.
//		7.Place markers, form lines, place markers, etc.
//		8.Repeat this until you have only one line left. The point t on that line coincides with the original curve point at t.
		
		//if the list is empty, this is mainly a fail safe for empty lists, though this should never happen
		if(subPoints.isEmpty()){
			return new Vec3();//return an empty vector
		}else if(subPoints.size() == 1){//indicates we have calculated the point on the curve at t
			return subPoints.get(0);
		}else{
			//else continue following de Casteljau's algorithm and subdivide
			//this will be 1 smaller than the previous list
			ArrayList<Vec3> newPoints = new ArrayList<Vec3>(subPoints.size()-1);//create a list to hold the new set of points
			
			//create the new list of points
			for(int curPoint = 0; curPoint < subPoints.size()-1; curPoint++){
				//get the new point as the point (100-x)% from the start and x% from the end where 
				newPoints.add(
						VecUtil.scale(subPoints.get(curPoint), 1-t).
						add( 
							VecUtil.scale(subPoints.get(curPoint+1), t)
							)
						);
			}
			//recurse with the smaller point set
			return getPoint(t, newPoints);
		}
	}
	
	//OLD CODE: this code was used as a more accurate and base result for the other implementation
	/**
	 * Extends the look up table used in the polynomial evaluation of a Bezier curve
	 */
//	private void extendLUT(){
//		//add the inital value to the LUT if it is empty
//		if(binomialLUT.isEmpty()){
//			binomialLUT.add(new int[]{1});
//			binomialLUT.add(new int[]{1,1});
//			binomialLUT.add(new int[]{1,2,1});
//		}
//		int prevLUTSize = 0;
//		//determine how many new LUT we need based on the new order of the curve
//		for(int prevLUT = binomialLUT.size()-1; prevLUT < n; prevLUT++){
//			prevLUTSize = binomialLUT.get(prevLUT).length;
//			int[] newLut = new int[prevLUTSize+1];
//			//the end values will always be 1
//			newLut[0] = 1;
//			newLut[newLut.length-1] = 1;
//			
//			//compute the new values
//			for(int curValue = 1; curValue < prevLUTSize; curValue++){
//				newLut[curValue] = binomialLUT.get(prevLUT)[curValue-1]+binomialLUT.get(prevLUT)[curValue];
//			}
//			//add the new LUT
//			binomialLUT.add(newLut);
//		}
//	}
	
	/**
	 * Computes the derivative of the Bezier curve
	 */
	private void computeDerivative(){
		if(derivative == null){
			derivative = new BezierPath();
		}
		//compute the new points for what we already have stored in the derivative
		for(int curPoint = 0; curPoint < derivative.numPoints(); curPoint++){
			derivative.points.set(curPoint, 
					VecUtil.subtract(points.get(curPoint+1), points.get(curPoint)).scale(n)
					);
		}
		//compute new points for added points to this curve if any were added
		for(int newPoint = derivative.numPoints(); newPoint < points.size()-1; newPoint++){
			derivative.points.add(
					VecUtil.subtract(points.get(newPoint+1), points.get(newPoint)).scale(n)
					);
			derivative.n++;
		}
		
//		if(derivative.curve != null){
//			derivative.curve.updateCurve();
//		}
	}
	
	/**
	 * Sets the value of the control point specified by {@code index} to the given {@code value}
	 * 
	 * @param index Index of the control point defining the curve to set it's value
	 * @param value Value to set the control at {@code index} to
	 */
	public void setpoint(int index, Vec3 value){
		setPoint(index, value.x, value.y, value.z);
	}
	
	/**
	 * Sets the value of the control point specified by {@code index} to the given {@code x,y,z} values
	 * 
	 * @param index Index of the control point defining the curve to set it's value
	 * @param x X value to set the control point to
	 * @param y Y value to set the control point to
	 * @param z Z value to set the control point to
	 */
	public void setPoint(int index, float x, float y, float z){
		//check that the index passed is in bounds 
		if(index > -1 && index < points.size()){
			points.get(index).set(x,y,z);
			//if there is a curve to update then update it
			if(curve != null){
				curve.updateCurve();
			}
		}
	}
	
	/**
	 * Increases the order of the curve by 1 while preserving the curve
	 */
	public void increaseOrder(){
		ArrayList<Vec3> newPoints = new ArrayList<Vec3>(points.size()+1);
		newPoints.add(points.get(0));
		//loop through the current control points and modify them for the new point
		for(int curPoint = 0; curPoint < n; curPoint++){
			//get the factor to determine the amount of each curve point that should be used
			float factor = (curPoint+1)/(float)(n+1);
			newPoints.add(
					VecUtil.scale(points.get(curPoint), factor).add( 
							points.get(curPoint+1).x*(1-factor), 
							points.get(curPoint+1).y*(1-factor), 
							points.get(curPoint+1).z*(1-factor)
							)
					);
		}
		//add original end point
		newPoints.add(points.get(points.size()-1));
		points = newPoints;
		//update the curve that renders this path if it is available
		if(curve != null){
			curve.constructCurve();
		}
		n++;
		computeDerivative();
		computeLength();
	}
	
	/**
	 * Decreases the order of the curve by 1 while attempting to preserve the curve
	 */
	public void decreaseOrder(){
		//cap the curve degradation to be up to 3 points 
		if(points.size()-1 > 2){
			//TODO: implement order reduction
		}
	}
	
	/**
	 * Moves the control point of this curve at the given {@code pIndex}, by the given translation vector
	 * 
	 * @param pIndex Index of the control point in this curve to modify
	 * @param translation Amount to move the control point at {@code pIndex} by
	 */
	public void translatePoint(int pIndex, Vec3 translation){
		translatePoint(pIndex, translation.x, translation.y, translation.z);
	}
	
	/**
	 * Moves the control point of this curve at the given {@code pIndex}, by the given translation amounts
	 * 
	 * @param pIndex Index of the control point in this curve to modify
	 * @param x X amount to translate by
	 * @param y Y amount to translate by
	 * @param z Z amount to translate by
	 */
	public void translatePoint(int pIndex, float x, float y, float z){
		//do a bounds check
		if(pIndex < points.size() && pIndex > 0){
			points.get(pIndex).add(x, y, z);
			//if there is a renderable curve associated with this path update it
			if(curve != null){
				curve.updateCurve();
			}
			computeDerivative();
			computeLength();
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
	
	/**
	 * Gets the order of the Bezier curve
	 * 
	 * @return Nth order of the Bezier curve
	 */
	public int getOrder(){
		return n;
	}
	
	private void update(){
		//check if we need to update the derivative to be able to get accurate data
		if(needsUpdate){
			computeDerivative();
			computeLength();
			needsUpdate = false;
		}
	}
}
