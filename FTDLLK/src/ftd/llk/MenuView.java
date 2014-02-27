package ftd.llk;

import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MenuView extends SurfaceView implements SurfaceHolder.Callback,MyDefine{
	LLKActivity llkActivity;//llkactivity引用
	MenuViewDrawThread menuViewDrawThread;//绘制线程
	
	
	MyButton m_button[];//菜单界面用到的按钮均可以此表示
	MyBJ bj_img;
	public MenuView(LLKActivity llkActivity)
	{	
		
		super(llkActivity);
		this.llkActivity=llkActivity;
		getHolder().addCallback(this);	

		
		initMenu();//初始化菜单
		

	}
	/**
	 * 初始化菜单中用到的图片
	 */
	public void initMenu()
	{
		//按钮加载
		m_button = new MyButton[5];
		for(int i = 0;i<m_button.length;i++)
		{
			m_button[i] = new  MyButton("image/button/"+i+".png",0,0);
			m_button[i].setPos((ON_SCREEN_WIDTH-m_button[i].getW())/2, 65+i*70);
		}
		bj_img=new MyBJ(MyTools.loadFromAsset("image/bjimg/menubj.jpg"));
	}
	public void onDraw(Canvas c) {
		//处理屏幕自适应问题，任何机型都适应，宽高比例悬殊太大的机型会有视觉差，但不影响操作
		c.save();
//		Log.d("窗口", "doDraw绘制");
		c.scale(llkActivity.size_w, llkActivity.size_h);
		//刷屏
		c.drawColor(0xff000000);
		//画背景
		bj_img.paint(c);
		//开始画菜单按钮
			if(m_button!=null)
				for(int i = 0,j = m_button.length;i<j;i++)
			{
				if(m_button[i]!=null)
					m_button[i].paint(c);
			}
			
		c.restore();
	}
	/**
	 * 屏幕触摸操作
	 */
	public boolean onTouchEvent(MotionEvent event) {
		
		
		//添加触控
		for(int i = 0;i<m_button.length;i++)
		{
			m_button[i].onTouchEvent(event);
		}
		return true;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	public void surfaceCreated(SurfaceHolder holder) {
		menuViewDrawThread = new MenuViewDrawThread(this, getHolder());//初始化绘制线程
		menuViewDrawThread.setFlag(true);
		menuViewDrawThread.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		
		 boolean retry = true;
	        menuViewDrawThread.setFlag(false);//停止刷帧线程
	        while (retry) {
	            try {
	            	menuViewDrawThread.join();//等待刷帧线程结束
	                retry = false;
	            } 
	            catch (InterruptedException e) {//不断地循环，直到等待的线程结束
	            }
	        }
	}

}
