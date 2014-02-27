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
		canvas.drawColor(Color.WHITE);//������ɫ
		
		this.setOnClickListener(this);
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}
	public void surfaceCreated(SurfaceHolder holder) {
		welcomeViewDrawThread.setFlag(true);
		welcomeViewDrawThread.start();//����ˢ֡�߳�
	}
	public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        welcomeViewDrawThread.setFlag(false);//ֹͣˢ֡�߳�
        while (retry) {
            try {
            	welcomeViewDrawThread.join();//�ȴ�ˢ֡�߳̽���
                retry = false;
            } 
            catch (InterruptedException e) {//���ϵ�ѭ����ֱ���ȴ����߳̽���
            }
        }
	}
	public void onClick(View v) {
		llkActivity.myHandler.sendEmptyMessage(GAME_MENU);
	}	

}
