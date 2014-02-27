package ftd.llk;

import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MenuView extends SurfaceView implements SurfaceHolder.Callback,MyDefine{
	LLKActivity llkActivity;//llkactivity����
	MenuViewDrawThread menuViewDrawThread;//�����߳�
	
	
	MyButton m_button[];//�˵������õ��İ�ť�����Դ˱�ʾ
	MyBJ bj_img;
	public MenuView(LLKActivity llkActivity)
	{	
		
		super(llkActivity);
		this.llkActivity=llkActivity;
		getHolder().addCallback(this);	

		
		initMenu();//��ʼ���˵�
		

	}
	/**
	 * ��ʼ���˵����õ���ͼƬ
	 */
	public void initMenu()
	{
		//��ť����
		m_button = new MyButton[5];
		for(int i = 0;i<m_button.length;i++)
		{
			m_button[i] = new  MyButton("image/button/"+i+".png",0,0);
			m_button[i].setPos((ON_SCREEN_WIDTH-m_button[i].getW())/2, 65+i*70);
		}
		bj_img=new MyBJ(MyTools.loadFromAsset("image/bjimg/menubj.jpg"));
	}
	public void onDraw(Canvas c) {
		//������Ļ����Ӧ���⣬�κλ��Ͷ���Ӧ����߱�������̫��Ļ��ͻ����Ӿ������Ӱ�����
		c.save();
//		Log.d("����", "doDraw����");
		c.scale(llkActivity.size_w, llkActivity.size_h);
		//ˢ��
		c.drawColor(0xff000000);
		//������
		bj_img.paint(c);
		//��ʼ���˵���ť
			if(m_button!=null)
				for(int i = 0,j = m_button.length;i<j;i++)
			{
				if(m_button[i]!=null)
					m_button[i].paint(c);
			}
			
		c.restore();
	}
	/**
	 * ��Ļ��������
	 */
	public boolean onTouchEvent(MotionEvent event) {
		
		
		//��Ӵ���
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
		menuViewDrawThread = new MenuViewDrawThread(this, getHolder());//��ʼ�������߳�
		menuViewDrawThread.setFlag(true);
		menuViewDrawThread.start();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		
		 boolean retry = true;
	        menuViewDrawThread.setFlag(false);//ֹͣˢ֡�߳�
	        while (retry) {
	            try {
	            	menuViewDrawThread.join();//�ȴ�ˢ֡�߳̽���
	                retry = false;
	            } 
	            catch (InterruptedException e) {//���ϵ�ѭ����ֱ���ȴ����߳̽���
	            }
	        }
	}

}
