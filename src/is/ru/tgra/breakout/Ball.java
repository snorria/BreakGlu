package is.ru.tgra.breakout;

import org.lwjgl.opengl.GL11;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Ball extends GraphicsObject{
	private int _width;
	private Vector2 _pos;
	private Vector2 _speed; //in pixels per second.
	private BallDelegate _delegate;
	
	public int get_width() {
		return _width;
	}

	public void set_width(int _width) {
		this._width = _width;
	}

	public void move(float delta_x, float delta_y){
		this._pos.add(delta_x,delta_y);
	}
	public void changeDirection(float x, float y){
		this._speed.mul(x,y);
	}
	public Vector2 get_speed() {
		return _speed;
	}
	/*public void add_speed(Vector2 _speed) {
		this._speed.set(_speed);
	}*/
	public Vector2 get_pos() {
		return _pos;
	}
	public void set_pos(Vector2 _pos) {
		this._pos.set(_pos);
	}
	public void set_speed(Vector2 _speed) {
		this._speed.set(_speed);
	}
	public double get_radius()
	{
		return _width/2.0;
	}
	
	public Ball() {
		this._width = 8;
		this._pos = new Vector2(250,100);
		this._speed = new Vector2(150,150);
		this._rect = new Rectangle(this._pos.x,this._pos.y,this._width,this._width);
	}
	public Ball(BallDelegate delegate){
		this._delegate = delegate;
		this._width = 8;
		this._pos = new Vector2(250,100);
		this._speed = new Vector2(150,150);
		this._rect = new Rectangle(this._pos.x,this._pos.y,this._width,this._width);
	}
	
	public void draw() {
        Gdx.gl11.glColor4f(1f, 0f, 0f, 1f);
		Gdx.gl11.glPushMatrix();
        Gdx.gl11.glTranslatef(this._pos.x, this._pos.y, 0);
        Gdx.gl11.glScalef(5.0f, 5.0f, 5.0f);
        Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_FAN, 4, 40);
        Gdx.gl11.glPopMatrix();
        
        
	}
	public void onFrame(float deltatime){
        this._pos.add(this._speed.x*deltatime,this._speed.y*deltatime);
        if(this._pos.x > 510){
        	this._speed.mul(-1,1);
        	this._pos.sub(this._width, 0);
        }
        if(this._pos.x < 0){
        	this._speed.mul(-1,1);
        	this._pos.add(this._width, 0);
        }
        if(this._pos.y > 400){
        	this._speed.mul(1,-1);
        	this._pos.sub(0, this._width);
        }
        if(this._pos.y < 0){
        	//this._speed.mul(1,-1);
        	//this._pos.add(0, this._width);
        	this._delegate.ballDead();
        }
        this.update();
	}

	@Override
	public void update() {
		this._rect.set(this._pos.x,this._pos.y,this._width,this._width);
	}


	
	
}