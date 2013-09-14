package is.ru.tgra.breakout;

import org.lwjgl.opengl.GL11;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Ball extends GraphicsObject{
	private int _radius;
	private Vector2 _pos;
	private Vector2 _speed; //in pixels per second.
	
	public int get_radius() {
		return _radius;
	}

	public void set_radius(int _radius) {
		this._radius = _radius;
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
	
	public Ball() {
		this._radius = 8;
		this._pos = new Vector2(200,300);
		this._speed = new Vector2(150,150);
		this._rect = new Rectangle(this._pos.x,this._pos.y,this._radius*2,this._radius*2);
	}
	
	public void draw() {
		Gdx.gl11.glPushMatrix();
        Gdx.gl11.glTranslatef(this._pos.x, this._pos.y, 0);
        Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 4, 4);
        Gdx.gl11.glPopMatrix(); 
	}
	public void onFrame(float deltatime){
        this._pos.add(this._speed.x*deltatime,this._speed.y*deltatime);
        if(this._pos.x > Gdx.graphics.getWidth()-this._radius*2)
        	this._speed.mul(-1,1);
        if(this._pos.x < 0)
        	this._speed.mul(-1,1);
        if(this._pos.y > Gdx.graphics.getHeight()-this._radius*2)
        	this._speed.mul(1,-1);
        if(this._pos.y < 0)
        	this._speed.mul(1,-1);
        this.update();
	}

	@Override
	public void update() {
		this._rect.set(this._pos.x,this._pos.y,this._radius*2,this._radius*2);
	}


	
	
}