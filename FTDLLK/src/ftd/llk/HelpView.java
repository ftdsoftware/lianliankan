package ftd.llk;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class HelpView extends SurfaceView implements SurfaceHolder.Callback,MyDefine{
	LLKActivity llkActivity=null;//llkactivity����
	HelpViewDrawThread helpViewDrawThread=null;
	
	Bitmap helpBitmap;//����ͼƬ
	MyBJ helpSprite;//˵������ͼƬ
	MyButton helpbButton[]=new MyButton[2];
	int index=0;
	public HelpView(LLKActivity llkActivity) {
		super(llkActivity);
		this.llkActivity=llkActivity;
		getHolder().addCallback(this);	

		initHelp();//���ó�ʼ���������溯��
	}
	/**
	 * ��ʼ�������������õ���ͼƬ
	 */
	public void initHelp()
	{
	helpBitmap=	MyTools.loadFromAsset("image/bjimg/helpbj.png");
	helpSprite=new MyBJ(MyTools.loadFromAsset("image/bjimg/help.png"));
	helpbButton[0]=new MyButton(MyTools.loadFromAsset("image/button/up.png"),50,360);
	helpbButton[1]=new MyButton(MyTools.loadFromAsset("image/button/next.png"),630,360);
	
	}

	protected void onDraw(Canvas c) {
		//������Ļ����Ӧ���⣬�κλ��Ͷ���Ӧ����߱�������̫��Ļ��ͻ����Ӿ������Ӱ�����
		c.save();
//				Log.d("����", "doDraw����");
		c.scale(llkActivity.size_w, llkActivity.size_h);
		//ˢ��
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
		helpViewDrawThread=new HelpViewDrawThread(this, getHolder());//��ʼ�������߳�
		helpViewDrawThread.setFlag(true);
		helpViewDrawThread.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		 boolean retry = true;
	       helpViewDrawThread.setFlag(false);//ֹͣˢ֡�߳�
	        while (retry) {
	            try {
	            	helpViewDrawThread.join();//�ȴ�ˢ֡�߳̽���
	                retry = false;
	            } 
	            catch (InterruptedException e) {//���ϵ�ѭ����ֱ���ȴ����߳̽���
	            }
	        }
	}
		
}


