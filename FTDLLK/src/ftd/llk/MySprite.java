package ftd.llk;


import android.graphics.Bitmap;
import android.graphics.Canvas;


public class MySprite 
{
	int x = 0,y = 0;//画的位置
	Bitmap m_image;	//图片
	int w,h;//画的宽高

	int r,c;//在大图中的行列
	
	public MySprite(Bitmap im)
	{
		this.m_image = im;
		w = im.getWidth()/6;
		h = im.getHeight()/(5*5+3);
	}

	public void paint(Canvas c)
	{
		MyTools.drawClip(c, m_image, x, y, this.c*w, this.r*h , w, h);
	}
	
	public void setIndex(int index){
		this.r=index/6;
		this.c=index%6;
		
	}
	
	public void setPos(int x,int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public int getW()
	{
		return w;
	}
	public int getH()
	{
		return h;
	}

}
