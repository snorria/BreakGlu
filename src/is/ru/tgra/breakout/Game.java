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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.BufferUtils;

public class Game implements ApplicationListener {
    // Vertex buffer.
    private FloatBuffer vertexBuffer = null;
    private Paddle paddle;
    private Ball ball;
    private ArrayList<Brick> bricks;
    
    @Override
    public void create() {
        System.out.println("Created!");
        //set up world coords.
        Gdx.gl11.glMatrixMode(GL11.GL_PROJECTION);
        Gdx.gl11.glLoadIdentity();

        // Set up a two-dimensional orthographic viewing region.
        //Gdx.glu.gluOrtho2D(Gdx.gl11, 0, Gdx.graphics.getWidth(), 0, Gdx.graphics.getHeight());
        
     // Set up affine transformation of x and y from world coordinates to window coordinates
        Gdx.gl11.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


        // Set the Modelview matrix back.
        Gdx.gl11.glMatrixMode(GL11.GL_MODELVIEW_MATRIX);    
        this.paddle = new Paddle();
        this.ball = new Ball();
        this.bricks = new ArrayList<Brick>();
        this.vertexBuffer = BufferUtils.newFloatBuffer(24);
        
        float[] normalPaddle = new float[] {0,0, 0,this.paddle.get_height(),  this.paddle.get_width(),0,  this.paddle.get_width() ,this.paddle.get_height()};
        this.vertexBuffer.put(normalPaddle);
        float[] normalBall = new float[] {0,0, 0,this.ball.get_width(), this.ball.get_width(),0,this.ball.get_width(),this.ball.get_width()};
        this.vertexBuffer.put(normalBall);
        float[] normalBrick = new float[] {0,0,0,25,50,0,50,25};
        this.vertexBuffer.put(normalBrick);
        this.vertexBuffer.rewind();
        Gdx.gl11.glVertexPointer(2, GL11.GL_FLOAT, 0, this.vertexBuffer);
        
        // Enable vertex array.
        Gdx.gl11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        
        // Select clear color for the screen.
        Gdx.gl11.glClearColor(.3f, .3f, .3f, 1f);
        
        //load level
        try {
        	int x = 0;
        	int y = Gdx.graphics.getHeight();
			Scanner in = new Scanner(new FileReader("level0.txt"));
			while(in.hasNext()){
				//System.out.println(in.next());
				String temp = in.next();
				for(int i = 0; i<10;i++){
					if(temp.charAt(i) == 'x'){
						Brick newbrick = new Brick(x,y);
						bricks.add(newbrick);
					}
					x+=51;
				}
				x = 0;
				y -= 26;
				/*if(in.next() == "x"){
					System.out.println("brick:("+x+","+y+")");
				}*/
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
	        	Rectangle brickRect = temp.get_Area(); 
	        	Rectangle ballRect = this.ball.get_Area();
	        	if(brickRect.overlaps(ballRect)){
	        		if(this.ball.get_speed().x>0){
	        			//ball going to the right.
	        		}
	        		else{
	        			//ball going to the left.
	        		}
	        		if(this.ball.get_speed().y>0){
	        			//ball going up.
	        		}
	        		else{
	        			//ball going down.
	        		}
	        		if(ballRect.getY() <= temp.get_Area().getY()){
	        			this.ball.changeDirection(1,-1);
	        			System.out.println("under");
	        		} else if(ballRect.getY()+2 >= brickRect.getY() +brickRect.getHeight()){
	        			this.ball.changeDirection(1,-1);
	        			System.out.println("over");	
	        		} else if(ballRect.getX()+2 > brickRect.getX()+temp.get_Area().getWidth()){
	        			this.ball.changeDirection(-1,1);
	        			System.out.println("right");
	        		}  else if(ballRect.getX() < brickRect.getX()){
	        			this.ball.changeDirection(-1,1);
	        			System.out.println("left");
	        		}
	        		System.out.println(ballRect);
	        		System.out.println(brickRect);
	        		/*Vector2 ballToBrick = this.ball.get_pos().tmp().sub(temp.get_pos());
	        		ballToBrick.nor();
	        		Vector2 brickFacing = new Vector2(0,1).nor();
	        		
	        		float angle = (float) Math.acos(ballToBrick.dot(brickFacing));
	        		angle = angle*100;
	        		System.out.println(angle);
	        		if((angle<45.0 && angle>0.0) ||(angle>315.0 && angle <360.0)){
	        			
	        		} else if (angle<135.0 && angle>45.0){
	        			
	        		} else if (angle<225.0 && angle>135.0){
	        			
	        		} else if (angle<315.0 && angle>225.0){
	        			
	        		}*/
	        		iter.remove();
	        		
	        	}
	        }
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
        Gdx.glu.gluOrtho2D(Gdx.gl11, 0, 480, 0, 320);

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