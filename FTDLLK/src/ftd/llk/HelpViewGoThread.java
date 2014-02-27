package ftd.llk;

import android.util.Log;


public class HelpViewGoThread extends Thread implements MyDefine{
	boolean flag = true;//循环标记位
	int sleepSpan = 50;//睡眠的毫秒数
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
					Thread.sleep(sleepSpan);//睡眠
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			}
		}
}
