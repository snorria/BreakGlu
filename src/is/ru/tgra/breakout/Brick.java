package is.ru.tgra.breakout;

import org.lwjgl.opengl.GL11;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Brick extends GraphicsObject {
	private int _width;
	private int _height;
	private Vector2 _pos;
	
	public Vector2 get_pos() {
		return _pos;
	}
	public Brick() {
		this._width = 50;
		this._height = 25;
		this._pos = new Vector2(0,0);
		this._rect = new Rectangle(this._pos.x,this._pos.y,this._width,this._height);
	}
	public Brick(int x, int y){
		this._width = 50;
		this._height = 25;
		this._pos = new Vector2(x,y);
		this._rect = new Rectangle(this._pos.x,this._pos.y,this._width,this._height);
	}
	
	public void draw() {
        Gdx.gl11.glColor4f(1f, 0f, 1f, 1f);
		Gdx.gl11.glPushMatrix();
        Gdx.gl11.glTranslatef(this._pos.x, this._pos.y, 0);
        Gdx.gl11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 8, 4);
        Gdx.gl11.glPopMatrix(); 
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

}
