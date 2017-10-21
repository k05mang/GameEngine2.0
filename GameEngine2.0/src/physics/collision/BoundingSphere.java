package physics.collision;

import glMath.*;
import glMath.matrices.Mat4;
import glMath.vectors.Vec3;

public class BoundingSphere extends CollisionMesh{

	private float radius;
	
	public BoundingSphere(float radius){
		this.radius = radius;
	}
	
	public BoundingSphere(BoundingSphere copy){
		super(copy);
		this.radius = copy.radius;
	}

	@Override
	public CollisionMesh clone() {
		return new BoundingSphere(this);
	}

	@Override
	public Vec3 support(Vec3 direction) {
		Vec3 dirScaled = new Vec3(direction);
		dirScaled.normalize();
		dirScaled.scale(radius);
		return (Vec3)VecUtil.add(transforms.getTranslation(), dirScaled);
	}
	
	public float getRadius() {
		return radius;
	}
}
