package is.ru.tgra.breakout;

import org.lwjgl.opengl.GL11;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Brick extends GraphicsObject {
	private int _width;
	private int _height;
	private Vector2 _pos;
	private BrickDelegate _delegate;
	private int _life;
	private Vector2 _bottomLeftPoint;
	private Vector2 _bottomRightPoint;
	private Vector2 _topLeftPoint;
	private Vector2 _topRightPoint;
	private Vector2 _bottomNormal;
	private Vector2 _leftNormal;
	private Vector2 _topNormal;
	private Vector2 _rightNormal;
	
	public Vector2 get_pos() {
		return _pos;
	}
	public Brick() {
		this._width = 50;
		this._height = 25;
		this._pos = new Vector2(0,0);
		this._rect = new Rectangle(this._pos.x,this._pos.y,this._width,this._height);
	}
	public Brick(int x, int y,BrickDelegate delegate,int life){
		this._delegate = delegate;
		this._width = 50;
		this._height = 25;
		this._pos = new Vector2(x,y);
		this._rect = new Rectangle(this._pos.x,this._pos.y,this._width,this._height);
		this._life = life;
		//4 corner points
		this._bottomLeftPoint = new Vector2(x,y);
		this._bottomRightPoint = new Vector2((x+this._width),y);
		this._topLeftPoint = new Vector2(x,(y+this._height));
		this._topRightPoint = new Vector2((x+this._width),(y+this._height));
		//4 normals of the sides (I save them so that I don't have to calculate them for every collision check.)
		this._bottomNormal = this.lineNormal(this._bottomLeftPoint,this._bottomRightPoint);
		this._leftNormal = this.lineNormal(this._topLeftPoint,this._bottomLeftPoint);
		this._topNormal = this.lineNormal(this._topRightPoint,this._topLeftPoint);
		this._rightNormal = this.lineNormal(this._bottomRightPoint,this._topRightPoint);
	}
	public boolean collisionWithBall(Ball b,float delta)
	{
		float bottom = -1f;
		float left = -1f;
		float top = -1f;
		float right = -1f;
		int count = 0;
		
		//næ í sek í collision, það er collision ef það returnar á milli 0 og 1.
		bottom = this.lineCollision(this._bottomLeftPoint,this._bottomRightPoint, b, delta);
		left = this.lineCollision(this._topLeftPoint,this._bottomLeftPoint, b, delta);
		top = this.lineCollision(this._topRightPoint,this._topLeftPoint, b, delta);
		right = this.lineCollision(this._bottomRightPoint,this._topRightPoint, b, delta);
		
		//checka hvort collision hafi verið.
		if((bottom>=0f && bottom<1f)||(top>=0f && top<1f)||(right>=0f && right<1f)||(left>=0f && left<1f))
		{
			//checka hvort boltinn hafi collidað við línurnar, annars set ég breytuna sem 100f svo ég geti notað minna en virkjan seinna.
			if(!(bottom>=0f && bottom<1f))
				bottom = 100f;
			if(!(top>=0f && top<1f))
				top = 100f;
			if(!(right>=0f && right<1f))
				right = 100f;
			if(!(left>=0f && left<1f))
				left = 100f;
			
			//checka hvort það sé minni tími í bottom collision heldur en left eða right collision. Geri þetta fyrir allar gerðir collisiona.
			//Ég geri bara collisionið sem ætti að vera næst (minnsti tími í collisionið).
			if(bottom < left && bottom < right){
				b.changeDirection(1,-1);
				System.out.println("bottom");
			}
			if(top < left && top < right){
				b.changeDirection(1,-1);
				System.out.println("top");
			}
			if(right < bottom && right < top){
				b.changeDirection(-1,1);
				System.out.println("right");
			}
			if(left < bottom && left < top){
				b.changeDirection(-1,1);
				System.out.println("left");
			}
			//læt brickinn fá hit í sig.
			this.hit();
			//returna true í loopuna til að geta breakað úr iteration í gegnum bricks.
			return true;
		}
		/*
		if((bottom>=0f && bottom<1f)||(top>=0f && top<1f)||(right>=0f && right<1f)||(left>=0f && left<1f))
		{
			if(bottom>=0f && bottom<1f){
				if((left>=0f && left<1f)&&left<bottom){
					b.changeDirection(-1,1);
					System.out.println("bottomLeftCorner");
				} else if ((right>=0f && right<1f)&&right<bottom){
					b.changeDirection(-1,1);
					System.out.println("bottomRightCorner");
				} else {
					b.changeDirection(1,-1);
					System.out.println("bottom");
				}
			}
			if(top>=0f && top<1f){
				if((left>=0f && left<1f)&&left<top){
					b.changeDirection(-1,1);
					System.out.println("topLeftCorner");
				} else if ((right>=0f && right<1f)&&right<top){
					b.changeDirection(-1,1);
					System.out.println("topRightCorner");
				} else {
					b.changeDirection(1,-1);
					System.out.println("top");
				}
			}
			if(right>=0f && right<1f){
				b.changeDirection(-1,1);
				System.out.println("right");
			}
			if(left>=0f && left<1f){
				b.changeDirection(-1,1);
				System.out.println("left");
			}
			this.hit();
			return true;
		}
		*/
		return false;
		/*if(bottom || left || top || right){
			this.hit();
			return true;
		}else{
			return false;
		}*/
	}
	//distance to line tekið frá: http://stackoverflow.com/questions/849211/shortest-distance-between-a-point-and-a-line-segment
	//og endurskrifað. þetta gefur distance í öðru veldi.
	private float distanceToLine(Vector2 P1, Vector2 P2, Vector2 b){
		float d1 = P1.dst2(P2);
		if(d1 == 0) return b.dst2(P1);
		float t = ((b.x - P1.x) * (P2.x - P1.x) + (b.y - P1.y) * (P2.y - P1.y)) / d1;
		if (t < 0) return b.dst2(P1);
		if (t > 1) return b.dst2(P2);
		return b.dst2(new Vector2(P1.x + t * (P2.x - P1.x), P1.y + t * (P2.y - P1.y)));
	}
	private float lineCollision(Vector2 P1, Vector2 P2,Ball b,float delta)
	{
		Vector2 C = b.get_middle();
		float d1 = distanceToLine(P1,P2,C);
		Vector2 CnextFrame = new Vector2(C).add((b.get_speed().x*delta),(b.get_speed().y*delta));// b.get_middle().tmp().add();//miðja boltans í næsta frame.
		float d2 = distanceToLine(P1,P2,CnextFrame);
		float t = (float) (((float)b.get_radius()-d1)/(d2-d1));
		if(t>=0f && t<1f)
			System.out.println("hit at t: "+t);
		
		return t;
		//if(t>=0f && t<1f)
			//return true;
		//else
			//return false;
	}
	/*
	private boolean lineCollision(Vector2 P1, Vector2 P2,Ball b,float delta)
	{
		Vector2 C = b.get_middle();
		System.out.println("C: "+C);
		Vector2 P1C = new Vector2(C).add(P1).div(2f);//vector milli P1 og miðju boltans.
		System.out.println("P1: "+P1);
		System.out.println("P1C: "+P1C);
		Vector2 CnextFrame = new Vector2(C).add((b.get_speed().x*delta),(b.get_speed().y*delta));// b.get_middle().tmp().add();//miðja boltans í næsta frame.

		//System.out.println("b.get_middle(): "+b.get_middle());
		//System.out.println("CnextFrame: "+CnextFrame);
		Vector2 P1CnextFrame = new Vector2(CnextFrame).add(P1).div(2f);//vector milli P1 og miðju boltans í næsta frame.
		//System.out.println("CnextFrame: "+CnextFrame);
		
		//test
		float dist = new Vector2(C).dst(P1);
		System.out.println("dist: "+dist);
		//
		float d1; //distance to line
		float d2; //distance to line next frame.
		float t = 0f; //time er 0f núna, 1f í næsta frame.
		if(P1 == this._bottomLeftPoint)
		{

			System.out.println("this._bottomNormal: "+this._bottomNormal);
			System.out.println("this._leftNormal: "+this._leftNormal);
			System.out.println("this._topNormal: "+this._topNormal);
			System.out.println("this._rightNormal: "+this._rightNormal);
			d1 = P1C.dot(this._bottomNormal);
			System.out.println("d1: "+d1);
			d2 = P1CnextFrame.dot(this._bottomNormal);
			System.out.println("d2: "+d2);
			t = (float) (((float)b.get_radius()-d1)/(d2-d1));
			if(t>=0f && t<1f)
				System.out.println("hit the bottom line at t: "+t);
		} 
		else if(P1 == this._topLeftPoint)
		{
			d1 = P1C.dot(this._leftNormal);
			d2 = P1CnextFrame.dot(this._leftNormal);
			t = (float) ((b.get_radius()-d1)/(d2-d1));
			if(t>=0f && t<1f)
				System.out.println("hit the left line at t: "+t);
		} 
		else if(P1 == this._topRightPoint)
		{
			d1 = P1C.dot(this._topNormal);
			d2 = P1CnextFrame.dot(this._topNormal);
			t = (float) ((b.get_radius()-d1)/(d2-d1));
			if(t>=0f && t<1f)
				System.out.println("hit the top line at t: "+t);
		} 
		else if(P1 == this._bottomRightPoint)
		{
			d1 = P1C.dot(this._rightNormal);
			d2 = P1CnextFrame.dot(this._rightNormal);
			t = (float) ((b.get_radius()-d1)/(d2-d1));
			if(t>=0f && t<1f)
				System.out.println("hit the right line at t: "+t);
		}
		if(t>=0f && t<1f)
			return true;
		else
			return false;
	}*/
	
	private Vector2 lineNormal(Vector2 P1, Vector2 P2){
		float vx = P2.x-P1.x;
		float vy = P2.y-P1.y;
		float length = (float)Math.sqrt(vx*vx+vy*vy);
		float dx = (vx/length);
		float dy = (vy/length);
		Vector2 leftNormal = new Vector2(dy,(-dx));
		return leftNormal;
	}
	public void draw() {
		if(this._life == 1)
			Gdx.gl11.glColor4f(1f, 0f, 0f, 1f);
		else if(this._life == 2)
			Gdx.gl11.glColor4f(0f, 1f, 0f, 1f);
		else if(this._life == 3)
			Gdx.gl11.glColor4f(0f, 0f, 1f, 1f);
		else if(this._life == 4)
			Gdx.gl11.glColor4f(0f, 1f, 1f, 1f);
		else if(this._life == 5)
			Gdx.gl11.glColor4f(1f, 1f, 0f, 1f);
		Gdx.gl11.glPushMatrix();
        Gdx.gl11.glTranslatef(this._pos.x, this._pos.y, 0);
        Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 44, 4);
        Gdx.gl11.glPopMatrix(); 
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
	
	public void hit() {
		this._life--;
		if(this._life==0)
			this._delegate.dead(this);
	}
	

}
