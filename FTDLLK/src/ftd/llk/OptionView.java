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
	LLKActivity llkActivity=null;//llkactivity����
	OptionViewDrawThread optionViewDrawThread=null;
	
	Bitmap optionBitmap;//����ͼƬ
	Bitmap buttBitmap[];//�����ť
	MyButton helpbButton[]=new MyButton[2];
	boolean isMusic;
	int difficulty;//�Ѷ�
	
	SharedPreferences sp;//���ݲ���
	SharedPreferences.Editor editor;

	public OptionView(LLKActivity llkActivity) {
		super(llkActivity);
		this.llkActivity=llkActivity;
		getHolder().addCallback(this);	

		initOption();
	}
	/**
	 * ��ʼ���������õ���ͼƬ
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
		
		
		sp = llkActivity.getSharedPreferences("myPref",2);//��һ������Ϊ�ļ������ڶ���Ϊ���ݲ���ģʽ
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
		//������Ļ����Ӧ���⣬�κλ��Ͷ���Ӧ����߱�������̫��Ļ��ͻ����Ӿ������Ӱ�����
		c.save();
//				Log.d("����", "doDraw����");
		c.scale(llkActivity.size_w, llkActivity.size_h);
		//ˢ��
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

		 //�������
        editor.putString("isMusic", ""+isMusic);
        editor.putInt("difficulty", difficulty);
        //��������
        editor.commit();
       
       
        
		 boolean retry = true;
		 optionViewDrawThread.setFlag(false);//ֹͣˢ֡�߳�
	        while (retry) {
	            try {
	            	optionViewDrawThread.join();//�ȴ�ˢ֡�߳̽���
	                retry = false;
	            } 
	            catch (InterruptedException e) {//���ϵ�ѭ����ֱ���ȴ����߳̽���
	            }
	        }
	}
		
}


