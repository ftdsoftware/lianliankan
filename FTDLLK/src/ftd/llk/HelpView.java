package ftd.llk;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class HelpView extends SurfaceView implements SurfaceHolder.Callback,MyDefine{
	LLKActivity llkActivity=null;//llkactivity引用
	HelpViewDrawThread helpViewDrawThread=null;
	
	Bitmap helpBitmap;//背景图片
	MyBJ helpSprite;//说明文字图片
	MyButton helpbButton[]=new MyButton[2];
	int index=0;
	public HelpView(LLKActivity llkActivity) {
		super(llkActivity);
		this.llkActivity=llkActivity;
		getHolder().addCallback(this);	

		initHelp();//调用初始化帮助界面函数
	}
	/**
	 * 初始化帮助界面中用到的图片
	 */
	public void initHelp()
	{
	helpBitmap=	MyTools.loadFromAsset("image/bjimg/helpbj.png");
	helpSprite=new MyBJ(MyTools.loadFromAsset("image/bjimg/help.png"));
	helpbButton[0]=new MyButton(MyTools.loadFromAsset("image/button/up.png"),50,360);
	helpbButton[1]=new MyButton(MyTools.loadFromAsset("image/button/next.png"),630,360);
	
	}

	protected void onDraw(Canvas c) {
		//处理屏幕自适应问题，任何机型都适应，宽高比例悬殊太大的机型会有视觉差，但不影响操作
		c.save();
//				Log.d("窗口", "doDraw绘制");
		c.scale(llkActivity.size_w, llkActivity.size_h);
		//刷屏
		c.drawColor(0xff000000);
		
		c.drawBitmap(helpBitmap, 0, 0, null);
		helpSprite.setpos(-index*800, 0);
		helpSprite.paint(c);
		if(index==0||index==1){
			helpbButton[1].paint(c);
		}
		if(index==1||index==2){
			helpbButton[0].paint(c);
		}
			
		c.restore();
		
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(index==0||index==1){
			helpbButton[1].onTouchEvent(event);
		}
		if(index==1||index==2){
			helpbButton[0].onTouchEvent(event);
		}
		
		return true;
	}
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		helpViewDrawThread=new HelpViewDrawThread(this, getHolder());//初始化绘制线程
		helpViewDrawThread.setFlag(true);
		helpViewDrawThread.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		 boolean retry = true;
	       helpViewDrawThread.setFlag(false);//停止刷帧线程
	        while (retry) {
	            try {
	            	helpViewDrawThread.join();//等待刷帧线程结束
	                retry = false;
	            } 
	            catch (InterruptedException e) {//不断地循环，直到等待的线程结束
	            }
	        }
	}
		
}


