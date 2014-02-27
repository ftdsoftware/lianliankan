package ftd.llk;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class RakingView extends SurfaceView implements SurfaceHolder.Callback,MyDefine{
	LLKActivity llkActivity=null;//llkactivity引用
	RakingViewDrawThread rakingViewDrawThread=null;
	
	Bitmap rakingBitmap;//背景图片
	SharedPreferences sp;//数据操作
	int score[]=new int [6];
	public RakingView(LLKActivity llkActivity) {
		super(llkActivity);
		this.llkActivity=llkActivity;
		getHolder().addCallback(this);	

		initRaking();//调用初始化排行榜界面函数
	}
	/**
	 * 初始化排行榜界面中用到的图片
	 */
	public void initRaking()
	{
		rakingBitmap=	MyTools.loadFromAsset("image/bjimg/raking.png");
		sp = llkActivity.getSharedPreferences("myPref",2);//第一个参数为文件名，第二个为数据操作模式
		
		if(sp!=null){
			for(int i=0;i<6;i++){
				score[i]=sp.getInt("bestSocre"+i, 0);
			}
		}

	
	}

	protected void onDraw(Canvas c) {
		//处理屏幕自适应问题，任何机型都适应，宽高比例悬殊太大的机型会有视觉差，但不影响操作
		c.save();
//				Log.d("窗口", "doDraw绘制");
		c.scale(llkActivity.size_w, llkActivity.size_h);
		//刷屏
		c.drawColor(0xff000000);
		//画背景
		c.drawBitmap(rakingBitmap, 0, 0, null);
		
		Paint p=new Paint();
		p.setColor(Color.BLACK);
		p.setTextSize(26);
		p.setTypeface(Typeface.DEFAULT_BOLD);
		for(int i=0;i<6;i++){
			c.drawText(""+score[i], 683, 107+i*43, p);	
		}
		
			
		c.restore();
		
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		rakingViewDrawThread=new RakingViewDrawThread(this, getHolder());//初始化绘制线程
		rakingViewDrawThread.setFlag(true);
		rakingViewDrawThread.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		 boolean retry = true;
	       rakingViewDrawThread.setFlag(false);//停止刷帧线程
	        while (retry) {
	            try {
	            	rakingViewDrawThread.join();//等待刷帧线程结束
	                retry = false;
	            } 
	            catch (InterruptedException e) {//不断地循环，直到等待的线程结束
	            }
	        }
	}
		
}


