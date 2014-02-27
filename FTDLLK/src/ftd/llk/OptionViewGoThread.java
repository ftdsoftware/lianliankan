package ftd.llk;

import android.util.Log;


public class OptionViewGoThread extends Thread implements MyDefine{
	boolean flag = true;//循环标记位
	int sleepSpan = 50;//睡眠的毫秒数
	LLKActivity llkActivity;
	public OptionViewGoThread(LLKActivity llkActivity){
		this.llkActivity = llkActivity;
	}
	public void run(){
		while(flag){
			if(llkActivity.optionView != null){
				
				
				
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
