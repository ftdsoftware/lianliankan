package ftd.llk;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;

public class MyButton 
{
	int x = 0,y = 0;
	Bitmap m_image[];
	int w,h;
	float size_x,size_y;
	
	int type;
	int m_fram;
	
	private boolean isHendandle;//判断被点击到了
	
	private boolean isHaveClick;//允许触摸松开做判断
	
	private boolean isChang;//判断button是否变化了
		
	boolean isPlay,islarge,isclik;
	
	
	public MyButton(Bitmap im,int x,int y)
	{
		m_image = new Bitmap[1];
		this.m_image[0] = im;
		this.x = x;
		this.y = y;
		w = im.getWidth();
		h = im.getHeight();
		isHaveClick = false;
		isPlay = false;
		islarge = false;
		isChang=false;
		size_x = size_y = 1;
		type = 0;
		
	}
	
	public MyButton(String  fileName,int x,int y)
	{
		m_image = new Bitmap[1];
		this.m_image[0] = MyTools.loadFromAsset(fileName);
		this.x = x;
		this.y = y;
		w = m_image[0].getWidth();
		h = m_image[0].getHeight();
		isHaveClick = false;
		islarge = true;
		isChang=false;
		size_x = size_y = 1;
		type = 0;
			//MyTools.getRanx(50, 150)/100.f;
	}
	
	public MyButton(Bitmap  bitmap[],int x,int y)
	{
		m_image = bitmap;
		this.x = x;
		this.y = y;
		w = m_image[0].getWidth();
		h = m_image[0].getHeight();
		isHaveClick = false;
		islarge = true;
		isChang=false;
		type = 1;
		m_fram = 0;
			//MyTools.getRanx(50, 150)/100.f;
	}
	
	public void paint(Canvas c)
	{
		switch(type)
		{
		case 0:
			c.save();
			c.translate(x+w/2, y+h/2);
			c.scale(size_x, size_y);
			c.drawBitmap(m_image[0], -w/2, -h/2, null);
			c.restore();
			break;
		case 1:
			c.drawBitmap(m_image[m_fram], x, y, null);
			break;
		}
	}
	
	public void update()
	{
		if(isChang)
		{
			if(islarge)
			{
				size_x+=0.3f;
				size_y+=0.3f;
				if(size_x>=1.7f)
				{
					islarge = false;
					
				}
			}
			else
			{
				size_x-=0.3f;
				size_y-=0.3f;
				if(size_x<=0.8f)
				{
					size_x = size_y = 1f;
					islarge = true;
					isChang = false;
					
				}
			}
		}
		
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
	
	
	public boolean isClick()
	{
		boolean t_isHendandle = isHendandle;
		isHendandle = false;
		return t_isHendandle;
	}
	private boolean isColliClick(float x,float y)
	{
		return (x>=this.x && x<=this.x+this.w && y>=this.y && y<=this.y+h);
	}
	
	public boolean onTouchEvent(MotionEvent event) 
	{
		
		float t_x = event.getX()/LLKActivity.size_w;
		float t_y = event.getY()/LLKActivity.size_h;
		switch(event.getAction())
		{
		case MotionEvent.ACTION_DOWN:
			if(isColliClick(t_x,t_y))
			{
				isHaveClick = true;
				isChang=true;
				isPlay = true;
				m_fram = 1;
//				Log.d("点击","按钮");
				
			}
			break;
		case MotionEvent.ACTION_UP:
			
			
			if(isHaveClick && isColliClick(t_x,t_y))
			{
				isHendandle = true;

//				Log.d("点击松开","按钮");
				
			}
			isHaveClick = false;
			m_fram = 0;
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		}
		return true;
	}
}