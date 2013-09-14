package is.ru.tgra.breakout;

import com.badlogic.gdx.math.Rectangle;

public abstract class GraphicsObject {
	protected Rectangle _rect;
	public Rectangle get_Area() {
		return this._rect;
	}
	public abstract void update();
}
