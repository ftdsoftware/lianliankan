package ftd.llk;

import android.util.Log;


public class OptionViewGoThread extends Thread implements MyDefine{
	boolean flag = true;//ѭ�����λ
	int sleepSpan = 50;//˯�ߵĺ�����
	LLKActivity llkActivity;
	public OptionViewGoThread(LLKActivity llkActivity){
		this.llkActivity = llkActivity;
	}
	public void run(){
		while(flag){
			if(llkActivity.optionView != null){
				
				
				
				try{
					Thread.sleep(sleepSpan);//˯��
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			}
		}
}
