package is.ru.tgra.breakout;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;

public class Main {
	public static void main (String[] args) {
        new LwjglApplication(new Game(), "Game", 500, 400, false);
	}
}