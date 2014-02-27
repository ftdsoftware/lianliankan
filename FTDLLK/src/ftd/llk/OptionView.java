package ftd.llk;


import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class OptionView extends SurfaceView implements SurfaceHolder.Callback,MyDefine{
	LLKActivity llkActivity=null;//llkactivity引用
	OptionViewDrawThread optionViewDrawThread=null;
	
	Bitmap optionBitmap;//背景图片
	Bitmap buttBitmap[];//五个按钮
	MyButton helpbButton[]=new MyButton[2];
	boolean isMusic;
	int difficulty;//难度
	
	SharedPreferences sp;//数据操作
	SharedPreferences.Editor editor;

	public OptionView(LLKActivity llkActivity) {
		super(llkActivity);
		this.llkActivity=llkActivity;
		getHolder().addCallback(this);	

		initOption();
	}
	/**
	 * 初始化界面中用到的图片
	 */
	public void initOption()
	{
		optionBitmap=MyTools.loadFromAsset("image/bjimg/option.png");
		buttBitmap=new Bitmap[5];

		buttBitmap[0]=MyTools.loadFromAsset("image/bjimg/easy.png");
		buttBitmap[1]=MyTools.loadFromAsset("image/bjimg/hard.png");
		buttBitmap[2]=MyTools.loadFromAsset("image/bjimg/bt.png");
		buttBitmap[3]=MyTools.loadFromAsset("image/bjimg/open.png");
		buttBitmap[4]=MyTools.loadFromAsset("image/bjimg/close.png");
		
		
		sp = llkActivity.getSharedPreferences("myPref",2);//第一个参数为文件名，第二个为数据操作模式
		editor= sp.edit();
		if(sp!=null){
			Log.d("sp", "not null");
			String music=null;
			music=sp.getString("isMusic", "");
			isMusic=new Boolean(music);
			difficulty=sp.getInt("difficulty", 0);
		}
	}
	

	protected void onDraw(Canvas c) {
		//处理屏幕自适应问题，任何机型都适应，宽高比例悬殊太大的机型会有视觉差，但不影响操作
		c.save();
//				Log.d("窗口", "doDraw绘制");
		c.scale(llkActivity.size_w, llkActivity.size_h);
		//刷屏
		c.drawColor(0xff000000);
		
		c.drawBitmap(optionBitmap, 0, 0, null);
		if(isMusic){
			c.drawBitmap(buttBitmap[3], 140, 130,null);
		}
		else{
			c.drawBitmap(buttBitmap[4], 200, 130,null);
		}
		switch(difficulty){
		case 0:
			c.drawBitmap(buttBitmap[0], 140, 220,null);
			break;
		case 1:
			c.drawBitmap(buttBitmap[1], 205, 220,null);
			break;
		case 2:
			c.drawBitmap(buttBitmap[2], 265, 220,null);
			break;
		}
					
		c.restore();
		
	}
	
	public boolean onTouchEvent(MotionEvent event) {
			if(event.getAction()==MotionEvent.ACTION_DOWN){
				float x=event.getX()/llkActivity.size_w;
				float y=event.getY()/llkActivity.size_h;
				if(x>140&&x<200&&y>130&&y<180){
					isMusic=true;
				}
				if(x<260&&x>200&&y>130&&y<180){
					isMusic=false;
				}
				if(x>140&&x<200&&y>220&&y<270){
					difficulty=0;
				}
				if(x<265&&x>200&&y>220&&y<270){
					difficulty=1;
				}
				if(x>265&&x<310&&y>220&&y<270){
					difficulty=2;
				}
			
			}

		return true;
			
	}
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		optionViewDrawThread=new OptionViewDrawThread(this, getHolder());
		optionViewDrawThread.setFlag(true);
		optionViewDrawThread.start();
		
		

	}

	public void surfaceDestroyed(SurfaceHolder holder) {

		 //添加数据
        editor.putString("isMusic", ""+isMusic);
        editor.putInt("difficulty", difficulty);
        //保存数据
        editor.commit();
       
       
        
		 boolean retry = true;
		 optionViewDrawThread.setFlag(false);//停止刷帧线程
	        while (retry) {
	            try {
	            	optionViewDrawThread.join();//等待刷帧线程结束
	                retry = false;
	            } 
	            catch (InterruptedException e) {//不断地循环，直到等待的线程结束
	            }
	        }
	}
		
}


