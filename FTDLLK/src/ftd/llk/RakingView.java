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
	LLKActivity llkActivity=null;//llkactivity����
	RakingViewDrawThread rakingViewDrawThread=null;
	
	Bitmap rakingBitmap;//����ͼƬ
	SharedPreferences sp;//���ݲ���
	int score[]=new int [6];
	public RakingView(LLKActivity llkActivity) {
		super(llkActivity);
		this.llkActivity=llkActivity;
		getHolder().addCallback(this);	

		initRaking();//���ó�ʼ�����а���溯��
	}
	/**
	 * ��ʼ�����а�������õ���ͼƬ
	 */
	public void initRaking()
	{
		rakingBitmap=	MyTools.loadFromAsset("image/bjimg/raking.png");
		sp = llkActivity.getSharedPreferences("myPref",2);//��һ������Ϊ�ļ������ڶ���Ϊ���ݲ���ģʽ
		
		if(sp!=null){
			for(int i=0;i<6;i++){
				score[i]=sp.getInt("bestSocre"+i, 0);
			}
		}

	
	}

	protected void onDraw(Canvas c) {
		//������Ļ����Ӧ���⣬�κλ��Ͷ���Ӧ����߱�������̫��Ļ��ͻ����Ӿ������Ӱ�����
		c.save();
//				Log.d("����", "doDraw����");
		c.scale(llkActivity.size_w, llkActivity.size_h);
		//ˢ��
		c.drawColor(0xff000000);
		//������
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
		rakingViewDrawThread=new RakingViewDrawThread(this, getHolder());//��ʼ�������߳�
		rakingViewDrawThread.setFlag(true);
		rakingViewDrawThread.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		 boolean retry = true;
	       rakingViewDrawThread.setFlag(false);//ֹͣˢ֡�߳�
	        while (retry) {
	            try {
	            	rakingViewDrawThread.join();//�ȴ�ˢ֡�߳̽���
	                retry = false;
	            } 
	            catch (InterruptedException e) {//���ϵ�ѭ����ֱ���ȴ����߳̽���
	            }
	        }
	}
		
}


