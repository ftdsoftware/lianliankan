package ftd.llk;

import android.util.Log;


public class HelpViewGoThread extends Thread implements MyDefine{
	boolean flag = true;//ѭ�����λ
	int sleepSpan = 50;//˯�ߵĺ�����
	LLKActivity llkActivity;
	public HelpViewGoThread(LLKActivity llkActivity){
		this.llkActivity = llkActivity;
	}
	public void run(){
		while(flag){
			if(llkActivity.helpView != null){
				for(int i = 0;i<llkActivity.helpView.helpbButton.length;i++)
				{
					
					
						if(llkActivity.helpView.helpbButton[i].isClick())
						{
							switch(i)
							{
						case 1:
							 llkActivity.helpView.index++;
							 
							break;
						case 0:
							 llkActivity.helpView.index--;
							 
							break;

							}
						}
						llkActivity.helpView.helpbButton[i].update();
				}
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
