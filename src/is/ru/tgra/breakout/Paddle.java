package is.ru.tgra.breakout;

import org.lwjgl.opengl.GL11;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Paddle extends GraphicsObject{
	private int _width;
	private int _height;
	private Vector2 _pos;
	private int SPEED = 300; //in pixels per second.
	
	public int get_width() {
		return _width;
	}
	public void set_width(int _width) {
		this._width = _width;
	}
	public int get_height() {
		return _height;
	}
	public void set_height(int _height) {
		this._height = _height;
	}
	public void move(float delta_x, float delta_y){
		this._pos.add(delta_x,delta_y);
	}
	public Vector2 get_pos() {
		return _pos;
	}
	public void set_pos(Vector2 _pos) {
		this._pos.set(_pos);
	}
	
	public Paddle() {
		this._width = 75;
		this._height = 10;
		this._pos = new Vector2(200,50);
		this._rect = new Rectangle(this._pos.x,this._pos.y,this._width,this._height);
	}
	
	public void draw() {
        Gdx.gl11.glColor4f(0f, 0f, 1f, 1f);
		Gdx.gl11.glPushMatrix();
        Gdx.gl11.glTranslatef(this._pos.x, this._pos.y, 0);
        Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
        Gdx.gl11.glPopMatrix(); 
	}
	public void onFrame(float deltatime){
    	if(Gdx.input.isKeyPressed(Keys.RIGHT)){
    		if(this._pos.x<440)
    			this.move(SPEED*deltatime, 0);
        }
        if(Gdx.input.isKeyPressed(Keys.LEFT)){
        	if(this._pos.x>0)
        		this.move(-SPEED*deltatime, 0);
        }
        this.update();
	}
	@Override
	public void update() {
		this._rect.set(this._pos.x,this._pos.y,this._width,this._height);
	}
}
