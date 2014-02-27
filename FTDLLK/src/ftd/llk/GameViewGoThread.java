package ftd.llk;

import android.util.Log;

public class GameViewGoThread extends Thread implements MyDefine{
	boolean flag = true;//ѭ�����λ
	int sleepSpan = 50;//˯�ߵĺ�����
	LLKActivity llkActivity;
	public GameViewGoThread(LLKActivity llkActivity){
		this.llkActivity = llkActivity;
	}
	public void run(){
		while(flag){
			if(llkActivity.gameView != null){
				
				//�����Ϸ��ʼ״̬�ͽ�ʱ�����50����
				if(llkActivity.gameView.GAME_STATE==llkActivity.gameView.START){
					
					//���ʱ�䵽�˾ͽ���Ϸ����
					if(llkActivity.gameView.gameTime<0){
						llkActivity.gameView.GAME_STATE=llkActivity.gameView.GAMEOVER;
					}
//					Log.d("time", ""+llkActivity.gameView.gameTime);
					
					//ʱ����һ���ʱ���ж������˶��ٸ��� ���������ĸ������������(120000-15*llkActivity.gameView.difficulty*1000)/2)
					if(llkActivity.gameView.gameTime==(120000-20*llkActivity.gameView.difficulty*1000)/2){
						//���һ��ʱ��������20�����Ӿͽ���һ����ʾ
						if(llkActivity.gameView.sumSprite<40&&llkActivity.gameView.sumSprite>=36){
							llkActivity.gameView.hint+=1;
							llkActivity.gameView.jiangliMusic.playSoundPool();
						}
						//���һ���ʱ��������24�����Ӿͽ���һ��ϴ��
						if(llkActivity.gameView.sumSprite>=32&&llkActivity.gameView.sumSprite<36){
							llkActivity.gameView.xipaisum+=1;
							llkActivity.gameView.jiangliMusic.playSoundPool();
						}
						//���һ���ʱ��������28�����Ӿͽ���һ�μ�ʱ
						if(llkActivity.gameView.sumSprite<32){
							llkActivity.gameView.addTime+=1;
							llkActivity.gameView.jiangliMusic.playSoundPool();
						}
					}
					llkActivity.gameView.gameTime=llkActivity.gameView.gameTime-50;
				}
				
				
				//���ϵ�4����ť���ز���
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
				//���ְ�ť
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
				//��ͣ����������ť�Ĵ��ز���
				if(llkActivity.gameView.GAME_STATE==llkActivity.gameView.PAUSE){
					if(llkActivity.gameView.ctn_butButton.isClick()){
						llkActivity.gameView.playMusic();
						llkActivity.gameView.GAME_STATE=llkActivity.gameView.START;
						llkActivity.gameView.sumSprite=60;
						
					}
					if(llkActivity.gameView.exit_butButton.isClick()){
						llkActivity.gameView.m_bjmusicMedia[llkActivity.gameView.musicID].stop();
						llkActivity.myHandler.sendEmptyMessage(GAME_MENU);
						//ֹͣ�����߳� �����ٴν����ʱ������������µ��߳�
						boolean retry = true;
				        while (retry) {
				            try {
				            	llkActivity.gameViewGoThread.join();//�ȴ�ˢ֡�߳̽���
				                retry = false;
				            } 
				            catch (InterruptedException e) {//���ϵ�ѭ����ֱ���ȴ����߳̽���
				            }
				        }
					}
				}
				//��������������ť�Ĵ��ز���
				if(llkActivity.gameView.GAME_STATE==llkActivity.gameView.GAMEOVER){
					if(llkActivity.gameView.ctn_butButton.isClick()){
						llkActivity.gameView.saveScore();
						llkActivity.gameView.initMap();//���³�ʼ����ͼ
						llkActivity.gameView.initData();//���³�ʼ������
						llkActivity.gameView.GAME_STATE=llkActivity.gameView.START;//״̬��Ϊ��ʼ״̬
						llkActivity.gameView.m_bjmusicMedia[15].stop();
						
						llkActivity.gameView.playMusic();
						
					}
					if(llkActivity.gameView.exit_butButton.isClick()){
						llkActivity.gameView.saveScore();
						llkActivity.gameView.m_bjmusicMedia[15].stop();
						llkActivity.gameView.m_bjmusicMedia[15]=null;
						llkActivity.myHandler.sendEmptyMessage(GAME_MENU);
						//ֹͣ�����߳� �����ٴν����ʱ������������µ��߳�
						 boolean retry = true;
					        while (retry) {
					            try {
					            	llkActivity.gameViewGoThread.join();//�ȴ�ˢ֡�߳̽���
					                retry = false;
					            } 
					            catch (InterruptedException e) {//���ϵ�ѭ����ֱ���ȴ����߳̽���
					            }
					        }
					        
					}
				}
				
				llkActivity.gameView.exit_butButton.update();
				llkActivity.gameView.ctn_butButton.update();
				
				try{
					Thread.sleep(sleepSpan);//˯��
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			
		}
	}	
	public void setFlag(boolean flag) {//����ѭ�����
    	this.flag = flag;
    }

}
