package mesh.curve;

import java.util.ArrayList;

import glMath.VecUtil;
import glMath.vectors.Vec3;

/**
 * Represents a composite Bezier curve object. Internally this is represented by a LinkedList
 * @author Kevin Mango
 *
 */
public class BezierPath {

	private ArrayList<BezierCurve> curves;
	private BezierNode start, end;
	private Continuity smoothness;
	private boolean isClosed;
	
	/**
	 * Constructs a BezierPath with a default smoothness of C0
	 */
	public BezierPath() {
		this(Continuity.C0);
	}
	
	/**
	 * Constructs a BezierPath with the given continuity
	 * 
	 * @param c Smoothness this Path will maintain between its curves
	 */
	public BezierPath(Continuity c){
		curves = new ArrayList<BezierCurve>();
		start = new BezierNode();
		end = start;//since there is no data both start and end are the same
		smoothness = c;
		isClosed = false;
	}
	
	/**
	 * Attempts to add the given curve to the path object, if the given {@code curve} is does not have a connection with
	 * the pre-existing curves then the add operation will fail and this function will return false.
	 * <br>
	 * In order for a curve to have a connection it must share an end point with one of the end curves in the path object.
	 * If the curve shares a point then it will be added to this path, and will be transformed to integrate with the existing
	 * curves to maintain the specified smoothness of the path.
	 * 
	 * @param curve Curve to be added to the path
	 * 
	 * @return True if the curve was successfully added, false if it could not be added to the path
	 */
	public boolean add(BezierCurve curve){
		//check if there is a previous curve to relate them to
		if(curves.size() == 0){
			start.curve = new BezierCurve(curve);//since there is no current data this is the first new curve
			curves.add(start.curve);//store the new curve object for reference when rendering
			end = new BezierNode(null, start, null);//create a new node for end
			start.next = end;//set the next node to be the end node
		}else if(!isClosed && curve.getOrder() > 1){
			//determine if the curve should be added to the front, end, or neither
			Vec3 startPoint = curve.getBezierPoint(0), endPoint = curve.getBezierPoint(1);
			//compare with the start curves start point
			if(start.curve.getBezierPoint(0).equals(endPoint)){//check if the end point matches this end
				//in this case we only need to add the curve and make it the new start
				BezierNode curStart = start;
				start = new BezierNode(curStart, null, new BezierCurve(curve));
				curStart.prev = start;
			}else if(start.curve.getBezierPoint(0).equals(startPoint)){//check if the start point matches
				//in this case we need to add the curve but reverse, this way values read consecutively along the path
				//are order in the same direction making reading the path much easier
				BezierNode curStart = start;
				start = new BezierNode(curStart, null, new BezierCurve(curve, true));
				curStart.prev = start;
			}else{//else they dont match
				//next check if the points match the last point on the end curve
				if(end.curve.getBezierPoint(1).equals(startPoint)){//check if the start point matches this end
					//in this case we only need to add the curve and make it the new end
					BezierNode curEnd = end;
					end = new BezierNode(null, curEnd, new BezierCurve(curve));
					curEnd.prev = end;
				}else if(end.curve.getBezierPoint(1).equals(endPoint)){//check if the end point matches
					//in this case we need to add the curve but reverse, this way values read consecutively along the path
					//are order in the same direction making reading the path much easier
					BezierNode curEnd = end;
					end = new BezierNode(null, curEnd, new BezierCurve(curve, true));
					curEnd.prev = end;
				}else{//else they dont match
					return false;
				}
			}
			//check if the start and end curves match, in which case the loop is closed
			if(end.curve.getBezierPoint(1).equals(start.curve.getBezierPoint(0))){
				//if they are connect the list and mark it closed
				start.prev = end;
				end.next = start;
				isClosed = true;
			}
			//smooth the path only if the curve added is not 2 points
			if(curve.getOrder() > 1){
				smooth();
			}
		}else{//the path is closed and isn't accepting new curves
			return false;
		}
		return true;
	}
	
	/**
	 * Sets the given smoothness of the BezierPath, new additions to the path object will reflect this change in smoothness.
	 * Existing curves will not change smoothness however.
	 * 
	 * @param c New smoothness for the curve
	 */
	public void setSmoothness(Continuity c){
		smoothness = c;
	}
	
	/**
	 * Determines if the given path object is closed by all the curves that define the path. If
	 * this returns true then this means that the path will no longer accept new curve objects.
	 * 
	 * @return True if the path is closed and connected by all its current curves, false otherwise
	 */
	public boolean isClosed(){
		return isClosed;
	}
	
	/**
	 * Smooth's the curve at the joint points of the end curves
	 */
	private void smooth(){
		switch(smoothness){
//			case C0: if it is c0 do nothing since this just means the curves are joint
//				break;
			case C1:
				ArrayList<Vec3> changeCurve = start.curve.getPoints();
				ArrayList<Vec3> bodyCurve = start.next.curve.getPoints();
				//check that the first curve and second curve can be linearized
				if(changeCurve.size() > 2 && bodyCurve.size() > 2){
					//linearize the curves at the joint point
					linearize(bodyCurve.get(0), 					//joint point
							bodyCurve.get(1), 						//body control
							changeCurve.get(changeCurve.size()-1));	//control to change
				}
				changeCurve = end.curve.getPoints();
				bodyCurve = end.prev.curve.getPoints();
				//check that the last curve curve and second to last curve can be linearized
				if(changeCurve.size() > 2 && bodyCurve.size() > 2){
					//linearize the curves at the joint point
					linearize(changeCurve.get(0), 				//joint point
							bodyCurve.get(bodyCurve.size()-1), 	//body control
							changeCurve.get(0));				//control to change
				}
				
				//lastly check if the end curves are joined in which case they also need to be linearized
				if(start.prev != null){
					
				}
				break;
			case C2:
				break;
		}
	}
	
	/**
	 * Takes 3 points from a curve and linearizes them for c1 smoothness conditions. Only the end control will
	 * have its value updated, this is to reduce the need to update the entire curve 
	 * 
	 * @param joint Point that is shared between the curves
	 * @param bodyControl Control point coming from the main body of the path
	 * @param endControl Control point coming from the end curve.
	 */
	private void linearize(Vec3 joint, Vec3 bodyControl, Vec3 endControl){
		//check if they are already colinear
		if(VecUtil.distance(joint, bodyControl, endControl) == 0){
			//this means they are already co-linear
			//but test to see how the control points line up to guarantee they aren't on the same side of the joint
			//which in turn makes sharp edges
			Vec3 relaPoint1 = VecUtil.subtract(endControl, joint);//line from the joint to the start curve control
			Vec3 relaPoint2 = VecUtil.subtract(bodyControl, joint);//line from the joint to the second curve control
			double angle = Math.acos(relaPoint1.dot(relaPoint2));
			//check if the angle is 180 or 0
			if(angle == 0){
				//if it is 0 then that means one of the control points is over extending the joint and needs to be adjusted
				//move the control point on the end curve, this preserves the rest of the curve smoothness
				//move the control point opposite the joint point the same distance it currently is from the joint
				//get a translation vector
				Vec3 adjust = VecUtil.subtract(joint, endControl);//from target control to joint
				adjust.scale(2);//translate once to reach the joint again to mirror it = adjust+adjust = 2*adjust
				//apply the translation
				
			}
		}else{
			
		}
	}
	
	private class BezierNode{
		public BezierNode next, prev;
		public BezierCurve curve;
		
		public BezierNode(){
			this(null, null, null);
		}
		
		public BezierNode(BezierNode next, BezierNode prev, BezierCurve data){
			this.next = next;
			this.prev = prev;
			curve = data;
		}
	}
}
