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
	}
	public boolean collisionWithBall(Ball b,float delta)
	{
		float bottom = -1f;
		float left = -1f;
		float top = -1f;
		float right = -1f;
		
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
			if(bottom <= left && bottom < right){
				b.changeDirection(1,-1);
				System.out.println("bottom");
			}
			if(top <= left && top < right){
				b.changeDirection(1,-1);
				System.out.println("top");
			}
			if(right <= bottom && right < top){
				b.changeDirection(-1,1);
				System.out.println("right");
			}
			if(left <= bottom && left < top){
				b.changeDirection(-1,1);
				System.out.println("left");
			}
			//læt brickinn fá hit í sig.
			this.hit();
			//returna true í loopuna til að geta breakað úr iteration í gegnum bricks.
			return true;
		}
		return false;
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
		float t = (float) (((float)(b.get_radius()*b.get_radius())-d1)/(d2-d1));
		if(t>=0f && t<1f)
			System.out.println("hit at t: "+t);
		
		return t;
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
