package ftd.llk;


public class MenuViewGoThread extends Thread implements MyDefine{
	boolean flag = true;//循环标记位
	int sleepSpan = 50;//睡眠的毫秒数
	LLKActivity llkActivity;
	public MenuViewGoThread(LLKActivity llkActivity){
		this.llkActivity = llkActivity;
	}
	public void run(){
		while(flag){
			if(llkActivity.menuView != null){
				for(int i = 0;i<llkActivity.menuView.m_button.length;i++)
				{
					
					
						if(llkActivity.menuView.m_button[i].isClick())
						{
							switch(i)
							{
						case 0:
							llkActivity.myHandler.sendEmptyMessage(GAME_START);
							break;
						case 1:
							llkActivity.myHandler.sendEmptyMessage(GAME_RAKING);
							break;
						case 2:
							llkActivity.myHandler.sendEmptyMessage(GAME_OPTION);
							break;
						case 3:
							llkActivity.myHandler.sendEmptyMessage(GAME_HELP);
							break;
						case 4:
							if(llkActivity!=null)
								llkActivity.finish();
							break;
							}
							
						}
						llkActivity.menuView.m_button[i].update();
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
