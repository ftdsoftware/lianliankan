package ftd.llk;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;

public class WelcomeView extends SurfaceView implements SurfaceHolder.Callback, OnClickListener,MyDefine{

	LLKActivity llkActivity;
	WelcomeViewDrawThread welcomeViewDrawThread = null;
	
	public WelcomeView(LLKActivity llkActivity) {
		super(llkActivity);
		this.llkActivity=llkActivity;
		getHolder().addCallback(this);
		welcomeViewDrawThread = new WelcomeViewDrawThread(this,getHolder());
		
	}

	public void onDraw(Canvas canvas) {
		canvas.drawColor(Color.WHITE);//背景白色
		
		this.setOnClickListener(this);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}
	public void surfaceCreated(SurfaceHolder holder) {
		welcomeViewDrawThread.setFlag(true);
		welcomeViewDrawThread.start();//启动刷帧线程
	}
	public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        welcomeViewDrawThread.setFlag(false);//停止刷帧线程
        while (retry) {
            try {
            	welcomeViewDrawThread.join();//等待刷帧线程结束
                retry = false;
            } 
            catch (InterruptedException e) {//不断地循环，直到等待的线程结束
            }
        }
	}
	public void onClick(View v) {
		llkActivity.myHandler.sendEmptyMessage(GAME_MENU);
	}	

}
