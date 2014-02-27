package ftd.llk;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class MyBJ {

	int x = 0, y = 0;
	Bitmap m_image;

	int w, h;

	public MyBJ(Bitmap im) {
		this.m_image = im;
		w = im.getWidth();
		h = im.getHeight();
	}

	public void paint(Canvas c) {
			c.drawBitmap(m_image, x, y, null);
	}

	public int getW() {
		return w;
	}

	public int getH() {
		return h;
	}
	public void setpos(int x,int y){
		this.x=x;
		this.y=y;
	}
}
