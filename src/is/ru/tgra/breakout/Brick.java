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
		this._bottomLeftPoint = new Vector2(x,y);
		this._bottomRightPoint = new Vector2((x+this._width),y);
		this._topLeftPoint = new Vector2(x,(y+this._height));
		this._topRightPoint = new Vector2((x+this._width),(y+this._height));
	}
	
	private float lineNormal(Vector2 P1, Vector2 P2){
		return 0.0f;
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
