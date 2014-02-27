package ftd.llk;

import android.util.Log;

public class GameViewGoThread extends Thread implements MyDefine{
	boolean flag = true;//循环标记位
	int sleepSpan = 50;//睡眠的毫秒数
	LLKActivity llkActivity;
	public GameViewGoThread(LLKActivity llkActivity){
		this.llkActivity = llkActivity;
	}
	public void run(){
		while(flag){
			if(llkActivity.gameView != null){
				
				//如果游戏开始状态就将时间减掉50毫秒
				if(llkActivity.gameView.GAME_STATE==llkActivity.gameView.START){
					
					//如果时间到了就将游戏结束
					if(llkActivity.gameView.gameTime<0){
						llkActivity.gameView.GAME_STATE=llkActivity.gameView.GAMEOVER;
					}
//					Log.d("time", ""+llkActivity.gameView.gameTime);
					
					//时间是一半的时候判断消掉了多少格子 根据消掉的格子来奖励玩家(120000-15*llkActivity.gameView.difficulty*1000)/2)
					if(llkActivity.gameView.gameTime==(120000-20*llkActivity.gameView.difficulty*1000)/2){
						//如果一半时间消掉了20个格子就奖励一次提示
						if(llkActivity.gameView.sumSprite<40&&llkActivity.gameView.sumSprite>=36){
							llkActivity.gameView.hint+=1;
							llkActivity.gameView.jiangliMusic.playSoundPool();
						}
						//如果一半的时间消掉了24个格子就奖励一次洗牌
						if(llkActivity.gameView.sumSprite>=32&&llkActivity.gameView.sumSprite<36){
							llkActivity.gameView.xipaisum+=1;
							llkActivity.gameView.jiangliMusic.playSoundPool();
						}
						//如果一半的时间消掉了28个格子就奖励一次加时
						if(llkActivity.gameView.sumSprite<32){
							llkActivity.gameView.addTime+=1;
							llkActivity.gameView.jiangliMusic.playSoundPool();
						}
					}
					llkActivity.gameView.gameTime=llkActivity.gameView.gameTime-50;
				}
				
				
				//边上的4个按钮触控操作
				for(int i = 0;i<llkActivity.gameView.m_button.length;i++)
				{
						if(llkActivity.gameView.m_button[i].isClick())
						{
							switch(i)
							{
						case 0:
							if(llkActivity.gameView.xipaisum>0){
								llkActivity.gameView.xipaisum--;
								llkActivity.gameView.xipai();
							}
							break;
						case 1:
							llkActivity.gameView.GAME_STATE=llkActivity.gameView.PAUSE;
							llkActivity.gameView.m_bjmusicMedia[llkActivity.gameView.musicID].stop();
							break;
						case 2:
							if(llkActivity.gameView.hint>0){
								llkActivity.gameView.hint--;
								llkActivity.gameView.hint();								
								llkActivity.gameView.changeMap();
							}
							break;
						case 3:
							if(llkActivity.gameView.addTime>0){
								llkActivity.gameView.addTime--;
								llkActivity.gameView.gameTime=llkActivity.gameView.gameTime+10000;
							}
							break;
							}	
						}
						llkActivity.gameView.m_button[i].update();
				}
				//音乐按钮
				for(int i = 0;i<llkActivity.gameView.m_music.length;i++)
				{
				if(llkActivity.gameView.m_music[i].isClick())
				{
					switch(i)
					{
				case 0:
					llkActivity.gameView.isMusic=true;
					llkActivity.gameView.playMusic();					
					break;
				case 1:
					llkActivity.gameView.isMusic=false;
					llkActivity.gameView.m_bjmusicMedia[llkActivity.gameView.musicID].stop();
					break;
					}	
				}
				llkActivity.gameView.m_music[i].update();
				}
				//暂停界面两个按钮的触控操作
				if(llkActivity.gameView.GAME_STATE==llkActivity.gameView.PAUSE){
					if(llkActivity.gameView.ctn_butButton.isClick()){
						llkActivity.gameView.playMusic();
						llkActivity.gameView.GAME_STATE=llkActivity.gameView.START;
						llkActivity.gameView.sumSprite=60;
						
					}
					if(llkActivity.gameView.exit_butButton.isClick()){
						llkActivity.gameView.m_bjmusicMedia[llkActivity.gameView.musicID].stop();
						llkActivity.myHandler.sendEmptyMessage(GAME_MENU);
						//停止更新线程 反正再次进入的时候会有两个更新的线程
						boolean retry = true;
				        while (retry) {
				            try {
				            	llkActivity.gameViewGoThread.join();//等待刷帧线程结束
				                retry = false;
				            } 
				            catch (InterruptedException e) {//不断地循环，直到等待的线程结束
				            }
				        }
					}
				}
				//结束界面两个按钮的触控操作
				if(llkActivity.gameView.GAME_STATE==llkActivity.gameView.GAMEOVER){
					if(llkActivity.gameView.ctn_butButton.isClick()){
						llkActivity.gameView.saveScore();
						llkActivity.gameView.initMap();//重新初始化地图
						llkActivity.gameView.initData();//重新初始化数据
						llkActivity.gameView.GAME_STATE=llkActivity.gameView.START;//状态变为开始状态
						llkActivity.gameView.m_bjmusicMedia[15].stop();
						
						llkActivity.gameView.playMusic();
						
					}
					if(llkActivity.gameView.exit_butButton.isClick()){
						llkActivity.gameView.saveScore();
						llkActivity.gameView.m_bjmusicMedia[15].stop();
						llkActivity.gameView.m_bjmusicMedia[15]=null;
						llkActivity.myHandler.sendEmptyMessage(GAME_MENU);
						//停止更新线程 反正再次进入的时候会有两个更新的线程
						 boolean retry = true;
					        while (retry) {
					            try {
					            	llkActivity.gameViewGoThread.join();//等待刷帧线程结束
					                retry = false;
					            } 
					            catch (InterruptedException e) {//不断地循环，直到等待的线程结束
					            }
					        }
					        
					}
				}
				
				llkActivity.gameView.exit_butButton.update();
				llkActivity.gameView.ctn_butButton.update();
				
				try{
					Thread.sleep(sleepSpan);//睡眠
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			
		}
	}	
	public void setFlag(boolean flag) {//设置循环标记
    	this.flag = flag;
    }

}
