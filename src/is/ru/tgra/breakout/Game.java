package is.ru.tgra.breakout;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.BufferUtils;

public class Game implements ApplicationListener {
    // Vertex buffer.
    private FloatBuffer vertexBuffer = null;
    private Paddle paddle;
    private Ball ball;
    
    @Override
    public void create() {
        System.out.println("Created!");
        this.paddle = new Paddle();
        this.ball = new Ball();
        this.vertexBuffer = BufferUtils.newFloatBuffer(16);
        
        float[] normalPaddle = new float[] {0,0, 0,this.paddle.get_height(),  this.paddle.get_width(),0,  this.paddle.get_width() ,this.paddle.get_height()};
        this.vertexBuffer.put(normalPaddle);
        float[] normalBall = new float[] {0,0, 0,this.ball.get_radius(), this.ball.get_radius(),0,this.ball.get_radius(),this.ball.get_radius()};
        this.vertexBuffer.put(normalBall);
        this.vertexBuffer.rewind();
        Gdx.gl11.glVertexPointer(2, GL11.GL_FLOAT, 0, this.vertexBuffer);
        
        // Enable vertex array.
        Gdx.gl11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        
        // Select clear color for the screen.
        Gdx.gl11.glClearColor(.3f, .3f, .3f, 1f);
        
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
    }
    
    private void display(){
        Gdx.gl11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        Gdx.gl11.glMatrixMode(GL11.GL_MODELVIEW);
        Gdx.gl11.glLoadIdentity();
        Gdx.gl11.glColor4f(1f, 1f, 1f, 1f);
        this.paddle.draw();
        this.ball.draw();
    }
    
    private void update(){
    	float delta = Gdx.graphics.getDeltaTime();
        this.ball.onFrame(delta);
        this.paddle.onFrame(delta);
        if(this.paddle.get_Area().overlaps(this.ball.get_Area())){
        	this.ball.get_pos().set(this.ball.get_pos().x,this.paddle.get_pos().y+10);
        	float tempX = ((this.ball.get_pos().x+this.ball.get_radius())-(this.paddle.get_pos().x+(this.paddle.get_width()/2)))/(this.paddle.get_width()/2); 
        	System.out.println(tempX);
        	//Vector2 tempvec = this.ball.get_pos().tmp().div(this.paddle.get_pos());
        	//this.ball.changeDirection(tempvec.x, -tempvec.y);
        	//this.ball.get_speed().add(tempX, 0);
        	float tempY = 1*tempX;
        	if(tempX>0)
        		tempX = -tempX;
        	this.ball.changeDirection(tempX,-tempY);
        }
        //System.out.println(this.ball.get_pos());
    }
    
    
    
    @Override
    public void render() {
        this.display();
        this.update();
    }

    @Override
    public void resize(int width, int height) {
        // Load the Project matrix. Next commands will be applied on that matrix.
        Gdx.gl11.glMatrixMode(GL11.GL_PROJECTION);
        Gdx.gl11.glLoadIdentity();

        // Set up a two-dimensional orthographic viewing region.
        Gdx.glu.gluOrtho2D(Gdx.gl11, 0, width, 0, height);

        // Set up affine transformation of x and y from world coordinates to window coordinates
        Gdx.gl11.glViewport(0, 0, width, height);

        // Set the Modelview matrix back.
        Gdx.gl11.glMatrixMode(GL11.GL_MODELVIEW_MATRIX);    
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
    }
}