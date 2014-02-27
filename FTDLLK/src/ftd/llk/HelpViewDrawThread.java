package ftd.llk;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class HelpViewDrawThread extends Thread{
	private int sleepSpan = 200;//˯�ߵĺ�����
	private boolean flag = true;//ѭ�����λ
	HelpView helpView;//�˵����������
	SurfaceHolder surfaceHolder = null;
	public HelpViewDrawThread(HelpView helpView,SurfaceHolder surfaceHolder){//������
		this.helpView = helpView;
		this.surfaceHolder = surfaceHolder;
	}
	public void run(){
		Canvas c;//����
		while(flag){
			c = null;
			try {
				// �����������������ڴ�Ҫ��Ƚϸߵ�����£����������ҪΪnull
			    c = surfaceHolder.lockCanvas(null);
			    synchronized (this.surfaceHolder) {
			    	try{
			    		helpView.onDraw(c);
			    	}
			    	catch(Exception e){}
			    }
			} finally {
			    if (c != null) {
			    	//������Ļ��ʾ����
			        surfaceHolder.unlockCanvasAndPost(c);
			    }
			}
			try{
				Thread.sleep(sleepSpan);//˯��sleepSpan����
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
    public void setFlag(boolean flag) {//����ѭ�����
    	this.flag = flag;
    }	
}