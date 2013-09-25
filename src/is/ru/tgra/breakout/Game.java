package is.ru.tgra.breakout;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import org.lwjgl.opengl.GL11;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.BufferUtils;

public class Game implements ApplicationListener,BallDelegate,BrickDelegate {
    // Vertex buffer.
    private FloatBuffer vertexBuffer = null;
    private Paddle paddle;
    private Ball ball;
    private ArrayList<Brick> bricks;
    private int lives;
    private int level;
    //font stuff
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    
    
    @Override
    public void create() {
        System.out.println("Created!");
        //set up world coords.
        Gdx.gl11.glMatrixMode(GL11.GL_PROJECTION);
        Gdx.gl11.glLoadIdentity();
        //font stuff
        this.spriteBatch = new SpriteBatch();
        this.font = new BitmapFont();

        // Set up a two-dimensional orthographic viewing region.
        //Gdx.glu.gluOrtho2D(Gdx.gl11, 0, Gdx.graphics.getWidth(), 0, Gdx.graphics.getHeight());
        
     // Set up affine transformation of x and y from world coordinates to window coordinates
        Gdx.gl11.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


        // Set the Modelview matrix back.
        Gdx.gl11.glMatrixMode(GL11.GL_MODELVIEW_MATRIX);
        this.vertexBuffer = BufferUtils.newFloatBuffer(96);
        
        
        float[] normalPaddle = new float[] {0,0, 0,10,  75,0,  75 ,10};
        this.vertexBuffer.put(normalPaddle);
        //circle
        float[] normalBall = new float[80];
        double pi = Math.PI;
        int s = 0;
        for (double i = 0; i<2 *pi;i += pi/20){
        	normalBall[s++] = (float)Math.cos(i);
        	normalBall[s++] = (float)Math.sin(i);
        }
        //float[] normalBall = new float[] {0,0, 0,8, 8,0,8,8};
        this.vertexBuffer.put(normalBall);
        float[] normalBrick = new float[] {0,0,0,25,50,0,50,25};
        this.vertexBuffer.put(normalBrick);
        this.vertexBuffer.rewind();
        Gdx.gl11.glVertexPointer(2, GL11.GL_FLOAT, 0, this.vertexBuffer);
        
        // Enable vertex array.
        Gdx.gl11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        
        // Select clear color for the screen.
        Gdx.gl11.glClearColor(.3f, .3f, .3f, 1f);
        
        this.startGame();
        
    }
    private void startGame(){
    	
    	this.level = 1;
    	this.lives = 3;
        this.paddle = new Paddle();
        this.ball = new Ball(this);
        this.bricks = new ArrayList<Brick>();
    	this.loadLevel();
    }
    private void loadLevel(){
    	//load level
        try {
        	int x = 0;
        	int y = 374;
			Scanner in = new Scanner(new FileReader("level"+(this.level%5)+".txt"));
			while(in.hasNext()){
				String temp = in.next();
				for(int i = 0; i<10;i++){
					if(temp.charAt(i) == '1'){
						Brick newbrick = new Brick(x,y,this,1);
						bricks.add(newbrick);
					}
					if(temp.charAt(i) == '2'){
						Brick newbrick = new Brick(x,y,this,2);
						bricks.add(newbrick);
					}
					if(temp.charAt(i) == '3'){
						Brick newbrick = new Brick(x,y,this,3);
						bricks.add(newbrick);
					}
					if(temp.charAt(i) == '4'){
						Brick newbrick = new Brick(x,y,this,4);
						bricks.add(newbrick);
					}
					if(temp.charAt(i) == '5'){
						Brick newbrick = new Brick(x,y,this,5);
						bricks.add(newbrick);
					}
					x+=51;
				}
				x = 0;
				y -= 26;
			}
			in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
        
        this.spriteBatch.begin();
		font.setColor(1.0f, 1.0f, 1.0f, 1f);
		font.draw(this.spriteBatch, String.format("Lives: %d Level: %d",this.lives,this.level), 10, 20);
		this.spriteBatch.end();

        Gdx.gl11.glVertexPointer(2, GL11.GL_FLOAT, 0, this.vertexBuffer);
        this.paddle.draw();
        this.ball.draw();
        for (Brick b : bricks) {
			b.draw();
		}
        
    }
    
    private void update(){
    	float delta = Gdx.graphics.getDeltaTime();
        this.ball.onFrame(delta);
        this.paddle.onFrame(delta);
        if(this.ball.get_pos().y < 60)
        {
	        if(this.paddle.get_Area().overlaps(this.ball.get_Area())){
	        	//tekið frá http://gamedev.stackexchange.com/questions/20456/a-more-sophisticated-ball-paddle-collision-algorithm-for-breakout
	        	float ballWidth = this.ball.get_Area().getWidth();
	        	float ballCenterX = this.ball.get_pos().x + ballWidth/2;
	        	float paddleWidth = this.paddle.get_Area().getWidth();
	        	float paddleCenterX = this.paddle.get_pos().x + paddleWidth/2;
	        	float speedX = this.ball.get_speed().x;
	        	float speedY = this.ball.get_speed().y;
	        	
	        	// Applying the Pythagorean theorem, calculate the ball's overall
	            // speed from its X and Y components.  This will always be a
	            // positive value.
	        	double speedXY = Math.sqrt(speedX*speedX + speedY*speedY);
	
	            // Calculate the position of the ball relative to the center of
	            // the paddle, and express this as a number between -1 and +1.
	            // (Note: collisions at the ends of the paddle may exceed this
	            // range, but that is fine.)
	            double posX = (ballCenterX - paddleCenterX) / (paddleWidth/2);
	
	            // Define an empirical value (tweak as needed) for controlling
	            // the amount of influence the ball's position against the paddle
	            // has on the X speed.  This number must be between 0 and 1.
	            final double influenceX = 0.75;
	
	            // Let the new X speed be proportional to the ball position on
	            // the paddle.  Also make it relative to the original speed and
	            // limit it by the influence factor defined above.
	            speedX = (float) (speedXY * posX * influenceX);
	
	            // Finally, based on the new X speed, calculate the new Y speed
	            // such that the new overall speed is the same as the old.  This
	            // is another application of the Pythagorean theorem.  The new
	            // Y speed will always be nonzero as long as the X speed is less
	            // than the original overall speed.
	            speedY = (float) (Math.sqrt(speedXY*speedXY - speedX*speedX) *
	                     (speedY > 0? -1 : 1));
	            this.ball.set_speed(new Vector2(speedX,speedY));
	
	        	
	        	this.ball.get_pos().set(this.ball.get_pos().x,this.paddle.get_pos().y+10);
	        	//float tempX = ((this.ball.get_pos().x+this.ball.get_radius())-(this.paddle.get_pos().x+(this.paddle.get_width()/2)))/(this.paddle.get_width()/2); 
	        	//System.out.println(tempX);
	        	//Vector2 tempvec = this.ball.get_pos().tmp().div(this.paddle.get_pos());
	        	//this.ball.changeDirection(tempvec.x, -tempvec.y);
	        	//this.ball.get_speed().add(tempX, 0);
	        	//float tempY = 1*tempX;
	        	//if(tempX>0)
	        		//tempX = -tempX;
	        	//this.ball.changeDirection(tempX,-tempY);
	        }
        }
        else 
        {
	        Iterator<Brick> iter = bricks.iterator();
	        while(iter.hasNext()){
	        	Brick temp = iter.next();
	        	if(temp.collisionWithBall(this.ball, delta))
	        		break;
	        }
        }
        if(Gdx.input.isKeyPressed(Keys.SPACE) && this.lives == 0){
            this.startGame();
        }
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
        //Gdx.glu.gluOrtho2D(Gdx.gl11, 0, 510, 0, 400);

        // Set up affine transformation of x and y from world coordinates to window coordinates
        Gdx.gl11.glViewport(0, 0, width, height);

        // Set the Modelview matrix back.
        Gdx.gl11.glMatrixMode(GL11.GL_MODELVIEW_MATRIX);    
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
    }
    
    public void gameOver() {
    	this.ball.get_pos().set(250, 100);
    	this.ball.get_speed().set(0, 0);
    	//this.startGame();
    }

	@Override
	public void dead(Brick b) {
		this.bricks.remove(b);
		if(this.bricks.isEmpty()){
			this.level++;
			this.loadLevel();
			this.ball = new Ball(this);
		}
	}

	@Override
	public void ballDead() {
		//GameOver
		this.lives--;
		if(this.lives>0){
			System.out.println("YOU LOST A LIFE");
			this.ball = new Ball(this);
		}
		else{
			System.out.println("YOU LOSE");
			this.gameOver();
		}
	}
	
}