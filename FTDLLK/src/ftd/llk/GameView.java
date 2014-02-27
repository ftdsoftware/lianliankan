package ftd.llk;

import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

import android.R.integer;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView  extends SurfaceView implements SurfaceHolder.Callback,MyDefine{
	LLKActivity llkActivity;
	GameViewDrawThread gameViewDrawThread;
	
	MyBJ gameBj;//��Ϸ����
	MyBJ pauseBj;//��Ϸ��ͣ����
	MyBJ gameoverBj;//��Ϸ��������
	MyButton m_button[];//��Ϸ���õ��İ�ť
	MyButton m_music[];//�������ֵİ�ť
	MyButton ctn_butButton;//������ť
	MyButton exit_butButton;//�˳���ť
	MySprite m_mySprite;//��Ϸ�еĸ���
	MyBJ m_mask;//�������ǵ���ĸ���
	Bitmap spriteBitmap;//��Ϸ���ӵ�ͼƬ
	int map[][]=new int[MAP_R][MAP_C];//��Ϸ��ͼ����
	int gamelevel;//��Ϸ����
	int addTime;//��ʱ����
	int hint;//��ʾ����
	int xipaisum;//ϴ�ƴ���
	int score;//�÷�
	int tempscore;
	int bestScore;//��߷�
	int difficulty;//�Ѷ�
	String difString=null;

	int sumSprite=60;//��¼��������������Ϊ0ʱ������һ��
	int clickj[]=new int[2];//��¼����е�λ��ֻ��¼���ε����λ�� ��������ע������
	int clicki[]=new int[2];//��¼����е�λ��
	
	int GAME_STATE;//��Ϸ״̬ ����ͣ����Ϸ�С���Ϸ����
	int START=1;//��Ϸ��
	int PAUSE=2;//��ͣ
	int GAMEOVER=3;//��Ϸ����
	int gameTime;//��Ϸ�涨��ʱ��
	
	boolean isMusic=true;
	MyPlayerSound m_hint;//�������ӵ�����
	MyPlayerMedia m_bjmusicMedia[];//��������
	int musicID;
	/**
	���ŵڼ���������  ���ڹ��캯�����һ��0��16������ÿ��ֹ֮ͣ���ڲ��ŵ�ʱ������1
	����ÿ�ζ������Ϊ�������ͬһ���������ᱨ����Ϊ���media�Ѿ�stop������start��
	*/
	MyPlayerSound nextMusic;//��һ����Ч
	MyPlayerSound jiangliMusic;//������Ч
	
	SharedPreferences sp;//���ݲ���
	SharedPreferences.Editor editor;
	
	public GameView(LLKActivity llkActivity){
		super(llkActivity);
		this.llkActivity=llkActivity;
		getHolder().addCallback(this);	
		

		GAME_STATE=START;
		
		initGameView();//��ʼ����Ϸ����
		initMusic();//��ʼ������
	}
	
	/**
	 * ��ʼ����Ϸ����
	 */
	public void initGameView(){
		//��ʼ������
		gameBj=new MyBJ(MyTools.loadFromAsset("image/bjimg/gamebj.jpg"));
		//��ʼ����Ϸ��ͣ����
		pauseBj=new MyBJ(MyTools.loadFromAsset("image/bjimg/pause.png"));
		pauseBj.setpos(30, 60);
		//��ʼ����Ϸ��������
		gameoverBj=new MyBJ(MyTools.loadFromAsset("image/bjimg/gameover.png"));
		gameoverBj.setpos(30, 60);
		//��ť����
		m_music=new MyButton[2];
		m_music[0]=new MyButton("image/button/musicopen.png", 660, 370);
		m_music[1]=new MyButton("image/button/musicclose.png", 660, 370);
		m_button = new MyButton[4];
		m_button[0] = new  MyButton("image/button/gamebutton"+0+".png",0,0);
		m_button[0].setPos(660, 120);
		m_button[1] = new  MyButton("image/button/gamebutton"+1+".png",0,0);
		m_button[1].setPos(660, 180);
		m_button[2] = new  MyButton("image/button/gamebutton"+2+".png",0,0);
		m_button[2].setPos(660, 250);
		m_button[3] = new  MyButton("image/button/gamebutton"+3+".png",0,0);
		m_button[3].setPos(660, 310);
		ctn_butButton=new  MyButton("image/button/continue.png",200,250);
		exit_butButton=new  MyButton("image/button/exit.png",370,250);
		//������Ϸ����Ҫ�õ���ͼƬ
		spriteBitmap=MyTools.loadFromAsset("image/gameimg/game1.png");
		//��ʼ�����Ӿ�����
		m_mySprite=new MySprite(spriteBitmap);
		m_mask=new MyBJ(MyTools.loadFromAsset("image/gameimg/mask.png"));
		
		sp = llkActivity.getSharedPreferences("myPref",2);//��һ������Ϊ�ļ������ڶ���Ϊ���ݲ���ģʽ
		editor= sp.edit();
		if(sp!=null){
			Log.d("sp", "not null");
			String music=null;
			music=sp.getString("isMusic", "");
			isMusic=new Boolean(music);
			difficulty=sp.getInt("difficulty", 0);
			bestScore=sp.getInt("bestSocre0", 0);
		}
		initData();//�ȳ�ʼ�������ڳ�ʼ����ͼ��Ȼ��ͼ��ʼ�������
		initMap();
		
	}
	/**
	 * ��ʼ������
	 */
	public void initMusic(){
		m_hint=new MyPlayerSound("music/hit.mp3", 1);
		m_bjmusicMedia=new MyPlayerMedia[16];
		for(int i=1;i<10;i++){
			m_bjmusicMedia[i-1]=new MyPlayerMedia("music/midi/10"+i+".mid");
		}
		for(int i=10;i<17;i++){
			m_bjmusicMedia[i-1]=new MyPlayerMedia("music/midi/1"+i+".mid");
		}
		nextMusic=new MyPlayerSound("music/next.mp3", 1);
		jiangliMusic=new MyPlayerSound("music/jiangli.mp3", 1);
	}
	/**
	 * ��ʼ������
	 */
	public void  initData(){
		addTime=3;
		xipaisum=3; 
		score=0;
		hint=3;
		gamelevel=1;
		sumSprite=60;
		 musicID=MyTools.getRan(17);
		
		switch(difficulty){
		case 0:
			difString="Easy";
			break;
		case 1:
			difString="Hard";
			break;
		case 2:
			difString="BT";
			break;
		}
		gameTime=100000-(20*difficulty*1000);
		
		
	}
	/**
	 * ��ʼ����Ϸ�����ͼ
	 */
	public void initMap(){
		//��ʼ����ͼ�����1��15��ֵ����ͼ����ÿ������ֵ����tempi��ʾ�ڵ�ͼ�����е�λ��tempi/10��ʾ����tempi%10��ʾ����
		int temp=MyTools.getRan(11);//ÿһ�ص�ͼƬ�������������
		int tempi=0;
		if(difficulty==0){
			for(int i=0;i<4;i++){
				for(int j=0;j<15;j++){
					map[tempi/10][tempi%10]=j+1+temp*15;//
					tempi++;
				}
			}
		}
		else if(difficulty==1){
			for(int i=0;i<4;i++){
				for(int j=0;j<15;j++){
					map[tempi/10][tempi%10]=MyTools.getRan(1,165);//
					tempi++;
				}
			}
		}
		else if(difficulty==2){
			for(int i=0;i<4;i++){
				for(int j=0;j<15;j++){
					map[tempi/10][tempi%10]=MyTools.getRan(1,165);//
					tempi++;
				}
			}
		}
		//�Ե�ͼ�������ϴ�Ʋ���
		xipai();
		//����¼�㶼��ֵΪ-1 ����Ҫ��ֵ�����ж��Ƿ��¼�ĵ���� -1����û�м�¼
		for(int i=0;i<2;i++){
			clicki[i]=-1;
			clickj[i]=-1;
		}
	}
	/**
	 * ����ͼ����ϴ�ƴ���
	 * @param map����ĵ�ͼ����
	 * �������4�������������е��к��У�ͨ���ĸ����õ���ͼ����������������ڽ��������н��� 100����ϴ�ƵĴ���
	 */
	public void xipai(){
		int tempi1;
		int tempi2;
		int tempj1;
		int tempj2;
		int tempmap;
		for(int i=0;i<300;i++){
			 tempi1=MyTools.getRan(6);
			 tempi2=MyTools.getRan(6);
			 tempj1=MyTools.getRan(10);
			 tempj2=MyTools.getRan(10);
			 tempmap=map[tempi1][tempj1];
			 map[tempi1][tempj1]=map[tempi2][tempj2];
			 map[tempi2][tempj2]=tempmap;
		}
	}
	public void playMusic(){
		musicID=(musicID+1)%16;
		 Log.d("id", ""+musicID);
		if(isMusic){
			try {
					m_bjmusicMedia[musicID].start();
				
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	//��������������а�ķ���
	public void saveScore(){
		int score[]=new int [7];
		if(sp!=null){
			for(int i=0;i<6;i++){
				score[i]=sp.getInt("bestSocre"+i, 0);
			}
		}
		score[6]=this.score;
		Arrays.sort(score); 
		int j=0;
		for(int i=6;i>0;i--){
			 editor.putInt("bestSocre"+j, score[i]);
			 editor.commit();
			 j++;
		}
	}

	/**
	 * ����Ϸ����
	 */
	protected void onDraw(Canvas c) {
		//������Ļ����Ӧ���⣬�κλ��Ͷ���Ӧ����߱�������̫��Ļ��ͻ����Ӿ������Ӱ�����
		c.save();
//		Log.d("����", "doDraw����");
		c.scale(llkActivity.size_w, llkActivity.size_h);
		//ˢ��
		c.drawColor(0xff000000);
		//������
		gameBj.paint(c);
		
		//����Ϸ��Ϣ
		Paint p=new Paint();

		p.setColor(Color.RED);
		p.setTextSize(22);
		p.setTypeface(Typeface.DEFAULT_BOLD);
		c.drawText("����ֵ", 13, 25, p);
		//����ʱ���ֵ�õ����εĳ���
		c.drawRect(90,8, 90+(gameTime/1000)*4, 28, p);
		c.drawText("�÷֣� "+score, 670, 25, p);
		p.setARGB(254, 47, 53, 243);
		c.drawText("ϴ�ƣ� "+xipaisum, 670, 55, p);
		c.drawText("��ʾ�� "+hint, 670, 85, p);
		c.drawText("��ʱ�� "+addTime, 670, 115, p);
		
		c.drawText("�Ѷȣ�"+difString+"  ������"+gamelevel, 13, 55, p);
		
		//����ť
		if(m_button!=null)
			for(int i = 0,j = m_button.length;i<j;i++)
		{
			if(m_button[i]!=null)
				m_button[i].paint(c);
		}
		if(m_music!=null){
			if(isMusic){
				m_music[1].paint(c);
			}else{
				m_music[0].paint(c);
			}
		}
		switch(GAME_STATE){
		case 1://��Ϸ��
			//����Ϸ����
			for(int j=0;j<MAP_R;j++){
				for(int i=0;i<MAP_C;i++){
					m_mySprite.setPos(30+i*60, 60+j*60);
					if(map[j][i]!=0){
						m_mySprite.setIndex(map[j][i]-1);
						m_mySprite.paint(c);
					}
				}
			}
			//������˸����ϵ��˳Ե���
			if(clicki[0]!=-1&&clickj[0]!=-1){
				m_mask.setpos(30+60*clicki[0], 60+60*clickj[0]);
				m_mask.paint(c);
			}
			
			break;
		case 2://��Ϸ��ͣ
			{
				pauseBj.paint(c);
				ctn_butButton.paint(c);
				exit_butButton.paint(c);
			}
			break;
		case 3://��Ϸ����
			{
				gameoverBj.paint(c);
				if(score>bestScore){
					c.drawText(""+score, 310, 136, p);
				}
				else{
					c.drawText(""+bestScore, 310, 136, p);
				}
				c.drawText(""+score, 285, 177, p);
				ctn_butButton.paint(c);
				exit_butButton.paint(c);
				if(isMusic){
					m_bjmusicMedia[musicID].stop();
					try {
						m_bjmusicMedia[15].start();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
					
			}
			break;
			
		}
			
		c.restore();
	}
	/**
	 * �������� ���жϸ����Ƿ�������
	 */
	public void addlevel(){
			gamelevel++;//������1
			if(gamelevel>11){
				gamelevel=gamelevel%11;
				gameTime=100000-(20*difficulty*1000)-gamelevel*5000;
			}
			else{
				gameTime=100000-(20*difficulty*1000);
			}
			sumSprite=60;
			//���¸���ͼ���鸳ֵ
			int tempi=0;
			for(int i=0;i<4;i++){
				for(int j=0;j<15;j++){
					map[tempi/10][tempi%10]=j+1+(gamelevel-1)*15;
					tempi++;
				}
			}
			//�Ե�ͼ�������ϴ�Ʋ���
			xipai();
			//��������
			if(isMusic){
				m_bjmusicMedia[musicID].stop();
				playMusic();
			}
	}
	/**
	 * ��ͬ�Ĺ�����������֮���ͼ�ı�ķ�ʽ��ͬ
	 */
	public void changeMap(){
		switch(gamelevel){
			case 11:
				break;
			case 2:
				{//������С��  ѭ������ÿһ�У���0�ĵط������½������� ûһ��ѭ��������Ϊ���������¶���0�����
					for(int i=0;i<10;i++){
						for(int k=0;k<2;k++){
							for(int j=5;j>0;j--){
								if(map[j][i]==0){
									map[j][i]=map[j-1][i];
									map[j-1][i]=0;
								}
							}	
						}
					}
				}
				break;
			case 3:
				{//����������  ѭ������ÿһ�У���0�ĵط������ҽ������� ûһ��ѭ��������Ϊ���������¶���0����� j�����ж��Ƿ��������
					for(int i=0;i<6;i++){
						for(int k=0;k<2;k++){
							for(int j=9;j>0;j--){
								if(map[i][j]==0){
									map[i][j]=map[i][j-1];
									map[i][j-1]=0;
								}
							}	
						}
					}
				}
				break;
			case 4:
			{
				//�ϰ벿  ��������  ѭ������ÿһ�У���0�ĵط������½������� ûһ��ѭ��������Ϊ���������¶���0�����
				for(int i=0;i<10;i++){
					for(int k=0;k<2;k++){
						for(int j=0;j<2;j++){
							if(map[j][i]==0){
								map[j][i]=map[j+1][i];
								map[j+1][i]=0;
							}
						}	
					}
				}
				
				//�°벿  �������µ�  ѭ������ÿһ�У���0�ĵط������½������� ûһ��ѭ��������Ϊ���������¶���0�����
				for(int i=0;i<10;i++){
					for(int k=0;k<2;k++){
						for(int j=5;j>3;j--){
							if(map[j][i]==0){
								map[j][i]=map[j-1][i];
								map[j-1][i]=0;
							}
						}	
					}
				}
			}
			break;
			case 5:
				{
					//����������  ѭ������ÿһ�У���0�ĵط������ҽ������� ûһ��ѭ��������Ϊ���������¶���0����� j�����ж��Ƿ��������
					for(int i=0;i<6;i++){
						for(int k=0;k<2;k++){
							for(int j=9;j>5;j--){
								if(map[i][j]==0){
									map[i][j]=map[i][j-1];
									map[i][j-1]=0;
								}
							}	
						}
					}
					
					//�°벿  �������µ�  ѭ������ÿһ�У���0�ĵط������½������� ûһ��ѭ��������Ϊ���������¶���0�����
					for(int i=0;i<6;i++){
						for(int k=0;k<2;k++){
							for(int j=0;j<5;j++){
								if(map[i][j]==0){
									map[i][j]=map[i][j+1];
									map[i][j+1]=0;
								}
							}	
						}
					}
				}
				break;
			case 6:
				{//�ϲ���
					for(int i=0;i<10;i++){
						for(int k=0;k<2;k++){
							for(int j=2;j>0;j--){
								if(map[j][i]==0){
									map[j][i]=map[j-1][i];
									map[j-1][i]=0;
								}
							}	
						}
					}
					//�²���
					for(int i=0;i<9;i++){
						for(int k=0;k<2;k++){
							for(int j=3;j<5;j++){
								if(map[j][i]==0){
									map[j][i]=map[j+1][i];
									map[j+1][i]=0;
								}
							}	
						}
					}
				}
				break;
			case 7:
			{//�󲿷�
				for(int i=0;i<6;i++){
					for(int k=0;k<2;k++){
						for(int j=4;j>0;j--){
							if(map[i][j]==0){
								map[i][j]=map[i][j-1];
								map[i][j-1]=0;
							}
						}	
					}
				}
				//�Ҳ���
				for(int i=0;i<6;i++){
					for(int k=0;k<2;k++){
						for(int j=5;j<9;j++){
							if(map[i][j]==0){
								map[i][j]=map[i][j+1];
								map[i][j+1]=0;
							}
						}	
					}
				}
			}
			break;
			case 8:
			{//�ϲ�������
				for(int i=0;i<3;i++){
					for(int k=0;k<2;k++){
						for(int j=0;j<9;j++){
							if(map[i][j]==0){
								map[i][j]=map[i][j+1];
								map[i][j+1]=0;
							}
						}	
					}
				}
				//�²�������
				for(int i=3;i<6;i++){
					for(int k=0;k<2;k++){
						for(int j=9;j>0;j--){
							if(map[i][j]==0){
								map[i][j]=map[i][j-1];
								map[i][j-1]=0;
							}
						}	
					}
				}
			}
			break;
			case 9:
				{//�������
					for(int i=0;i<5;i++){
						for(int k=0;k<2;k++){
							for(int j=5;j>0;j--){
								if(map[j][i]==0){
									map[j][i]=map[j-1][i];
									map[j-1][i]=0;
								}
							}	
						}
					}
					//�ұ�����
					for(int i=5;i<10;i++){
						for(int k=0;k<2;k++){
							for(int j=0;j<5;j++){
								if(map[j][i]==0){
									map[j][i]=map[j+1][i];
									map[j+1][i]=0;
								}
							}	
						}
					}
				}
				break;
			case 10:
			{
				//�ϰ벿  ��������  ѭ������ÿһ�У���0�ĵط������½������� ûһ��ѭ��������Ϊ���������¶���0�����
				for(int i=0;i<10;i++){
					for(int k=0;k<2;k++){
						for(int j=0;j<2;j++){
							if(map[j][i]==0){
								map[j][i]=map[j+1][i];
								map[j+1][i]=0;
							}
						}	
					}
				}
				
				//�°벿  �������µ�  ѭ������ÿһ�У���0�ĵط������½������� ûһ��ѭ��������Ϊ���������¶���0�����
				for(int i=0;i<10;i++){
					for(int k=0;k<2;k++){
						for(int j=5;j>3;j--){
							if(map[j][i]==0){
								map[j][i]=map[j-1][i];
								map[j-1][i]=0;
							}
						}	
					}
				}
				
				//����������  ѭ������ÿһ�У���0�ĵط������ҽ������� ûһ��ѭ��������Ϊ���������¶���0����� j�����ж��Ƿ��������
				for(int i=0;i<6;i++){
					for(int k=0;k<2;k++){
						for(int j=9;j>5;j--){
							if(map[i][j]==0){
								map[i][j]=map[i][j-1];
								map[i][j-1]=0;
							}
						}	
					}
				}
				
				//�°벿  �������µ�  ѭ������ÿһ�У���0�ĵط������½������� ûһ��ѭ��������Ϊ���������¶���0�����
				for(int i=0;i<6;i++){
					for(int k=0;k<2;k++){
						for(int j=0;j<5;j++){
							if(map[i][j]==0){
								map[i][j]=map[i][j+1];
								map[i][j+1]=0;
							}
						}	
					}
				}
				
			}
				break;
			case 1:
			{
				//�ϲ���
				for(int i=0;i<9;i++){
					for(int k=0;k<2;k++){
						for(int j=2;j>0;j--){
							if(map[j][i]==0){
								map[j][i]=map[j-1][i];
								map[j-1][i]=0;
							}
						}	
					}
				}
				//�²���
				for(int i=0;i<9;i++){
					for(int k=0;k<2;k++){
						for(int j=3;j<5;j++){
							if(map[j][i]==0){
								map[j][i]=map[j+1][i];
								map[j+1][i]=0;
							}
						}	
					}
				}
				//�󲿷�
				for(int i=0;i<6;i++){
					for(int k=0;k<2;k++){
						for(int j=4;j>0;j--){
							if(map[i][j]==0){
								map[i][j]=map[i][j-1];
								map[i][j-1]=0;
							}
						}	
					}
				}
				//�Ҳ���
				for(int i=0;i<6;i++){
					for(int k=0;k<2;k++){
						for(int j=5;j<9;j++){
							if(map[i][j]==0){
								map[i][j]=map[i][j+1];
								map[i][j+1]=0;
							}
						}	
					}
				}
			}
				break;
		}
	}
	/**
	 * �ж���Ϸ�Ƿ����� ����������ϴ�� ˼·��hint�Ĳ��
	 */
	public boolean clock(){
		//j��i��һ������������ k��l�ڶ������������� 
				for(int j=0;j<MAP_R;j++){
					for(int i=0;i<MAP_C;i++){
						if(map[j][i]!=0){
							for(int k=0;k<MAP_R;k++){
								for(int l=0;l<MAP_C;l++){
									if(map[k][l]!=0&&(map[j][i]==map[k][l])){
										clicki[0]=i;
										clicki[1]=l;
										clickj[0]=j;
										clickj[1]=k;
										//ͬһ���㲻���������ж�
										if(judge()&&(clicki[0]!=clicki[1]||clickj[0]!=clickj[1])){
											
											return false;
										}
									}
								}
							}
						}
					}
				}
				return true;
	}
	/**
	 * ��ʾ��������ҵ����ʾ����������һ�Ը���
	 * �Ե�ͼ�����еĵ�ѭ������ �ȴ����еĵ���һ��������еĵ�Աȿ��ܷ������������һֱѭ�������еĵ㶼�Ƚ�   ����һ�����Ӿ�break
	 * 
	 */
	public void hint(){
		//j��i��һ������������ k��l�ڶ������������� 
		for(int j=0;j<MAP_R;j++){
			for(int i=0;i<MAP_C;i++){
				if(map[j][i]!=0){
					for(int k=0;k<MAP_R;k++){
						for(int l=0;l<MAP_C;l++){
							if(map[k][l]!=0&&(map[j][i]==map[k][l])){
								clicki[0]=i;
								clicki[1]=l;
								clickj[0]=j;
								clickj[1]=k;
								//ͬһ���㲻���������ж�
								if(judge()&&(clicki[0]!=clicki[1]||clickj[0]!=clickj[1])){
									sumSprite=sumSprite-2;
									//�����������ڵ�ͼ�����е����ݱ�Ϊ0����������
									for(int p=0;p<2;p++){
										map[clickj[p]][clicki[p]]=0;	
									}
									clicki[0]=-1;
									clickj[0]=-1;
									clicki[1]=-1;
									clickj[1]=-1;
									//���жϸ����Ƿ�������
									if(sumSprite==0){
										addlevel();
										nextMusic.playSoundPool();
									}
									m_hint.playSoundPool();
									score=score+1;
									return;
								}
							}
						}
					}
				}
			}
		}
	}
	/**
	 * �ж��������ӵ�����ת���ǲ���С��3��
	 * ˼·�����ù�������㷨 �������һ�����ת��������Ե�������е㲢������һ������ ��
	 * ������һ����ȥ�ж��Ƿ�����������е�ĳһ����
	 * @return 
	 */
	public boolean judge(){
		/**���ж��������Ƿ�����**/
		if((Math.abs(clicki[0]-clicki[1])==1&&Math.abs(clickj[0]-clickj[1])==0)||
				(Math.abs(clicki[0]-clicki[1])==0&&Math.abs(clickj[0]-clickj[1])==1)){
			return true;
		}
		
		/* �����ھͽ���������㷨*/
		
		
		int tempmap[][]=new int[8][12];//����ͼ���鱣�浽��ʱ���� ��ʱ����ȵ�ͼ�����������Ҷ�һ��
	     Vector lianxian1=new Vector();
		/**���������һ����ת��������Ե���λ�õ����꣬
		��ֵ�����ڵ�ͼ�����е�����λ�� ������ �ϵ��� ÿһ�����Ӷ���Ψһ��һ��ֵ**/
	     Vector lianxian2=new Vector();//��������ڶ��������������ߵ�λ��
		//��ʼ����ʱ���� ����ֵΪ0
		for(int j=0;j<8;j++){
			for(int i=0;i<12;i++){
				tempmap[j][i]=0;
			}
		}
		//����ͼ�����е�ֵ�ŵ���ʱ������ �������ҿ�һ�г��� ��ֵ�ĵط���ֵΪ1 û��ֵ�ĵط�Ϊ0 0��ʾ����ͨ������1��ʾ����������
		for(int j=1;j<7;j++){
			for(int i=1;i<11;i++){
				if(map[j-1][i-1]!=0){
					tempmap[j][i]=1;
				}
			}
		}
		//clicki��0������Ϸ��ͼ�����е����� ����ʱ��ͼ�����е�ֵС1
		int tempClicki1=clicki[0]+1;
		int tempClickj1=clickj[0]+1;
		lianxian1.add(tempClickj1*12+tempClicki1);
		int tempsum1=1;//����ͳ���ж��ٸ����ӿ������ߵ������ ͬʱҲ��lianxian������±�
		//�õ�������������еĿ������ߵ�λ�� �ӵ�������濪ʼ����
		for(int i=tempClickj1-1;i>=0;i--){
			if(tempmap[i][tempClicki1]==0){
				lianxian1.add(i*12+tempClicki1);
				tempsum1++;
			}else{
				break;
			}
		}
		//�õ�������������еĿ������ߵ�λ�� �ӵ�������濪ʼ����
		for(int i=tempClickj1+1;i<8;i++){
			if(tempmap[i][tempClicki1]==0){
				lianxian1.add(i*12+tempClicki1);
				tempsum1++;
			}else{
				break;
			}
		}
		//�õ�������������еĿ������ߵ�λ�� 
		for(int i=tempClicki1+1;i<12;i++){
			if(tempmap[tempClickj1][i]==0){
				lianxian1.add(i+tempClickj1*12);
				tempsum1++;
			}
			else{
				break;
			}
		}
		//�õ�������������еĿ������ߵ�λ�� 
		for(int i=tempClicki1-1;i>=0;i--){
			if(tempmap[tempClickj1][i]==0){
				lianxian1.add(i+tempClickj1*12);
				tempsum1++;
			}
			else{
				break;
			}
		}
		int firstSum=tempsum1;
		//�ڶ������� ����һ�εõ���λ����������ĵ��������
		for(int k=1;k<firstSum;k++){
			//vector�б������object����Ҫת��Ϊint����
			tempClicki1=(Integer.parseInt(String.valueOf(lianxian1.get(k))))%12 ;
			tempClickj1=(Integer.parseInt(String.valueOf(lianxian1.get(k))))/12;
			if(tempClicki1==12){
				tempClicki1=0;
			}
			if(tempClickj1==8){
				tempClickj1=0;
			}
			//�õ�������������еĿ������ߵ�λ�� �ӵ�������濪ʼ����
			for(int i=tempClickj1-1;i>=0;i--){
				if(tempmap[i][tempClicki1]==0){
					lianxian1.add(i*12+tempClicki1);
					tempsum1++;
				}else{
					break;
				}
			}
			//�õ�������������еĿ������ߵ�λ�� �ӵ�������濪ʼ����
			for(int i=tempClickj1+1;i<8;i++){
				if(tempmap[i][tempClicki1]==0){
					lianxian1.add(i*12+tempClicki1);
					tempsum1++;
				}else{
					break;
				}
			}
			//�õ�������������еĿ������ߵ�λ�� 
			for(int i=tempClicki1+1;i<12;i++){
				if(tempmap[tempClickj1][i]==0){
					lianxian1.add(i+tempClickj1*12);
					tempsum1++;
				}
				else{
					break;
				}
			}
			//�õ�������������еĿ������ߵ�λ�� 
			for(int i=tempClicki1-1;i>=0;i--){
				if(tempmap[tempClickj1][i]==0){
					lianxian1.add(i+tempClickj1*12);
					tempsum1++;
				}
				else{
					break;
				}
			}
		}
		//���ڶ��������������߲�ת������
		int tempClicki2=clicki[1]+1;
		int tempClickj2=clickj[1]+1;
		lianxian2.add(tempClickj2*12+tempClicki2);
		int tempsum2=1;//����ͳ���ж��ٸ����ӿ������ߵ������ ͬʱҲ��lianxian������±�
		//�õ�������������еĿ������ߵ�λ�� �ӵ�������濪ʼ����
		for(int i=tempClickj2-1;i>=0;i--){
			if(tempmap[i][tempClicki2]==0){
				lianxian2.add(i*12+tempClicki2);
				tempsum2++;
			}else{
				break;
			}
		}
		//�õ�������������еĿ������ߵ�λ�� �ӵ�������濪ʼ����
		for(int i=tempClickj2+1;i<8;i++){
			if(tempmap[i][tempClicki2]==0){
				lianxian2.add(i*12+tempClicki2);
				tempsum2++;
			}else{
				break;
			}
		}
		//�õ�������������еĿ������ߵ�λ�� 
		for(int i=tempClicki2+1;i<12;i++){
			if(tempmap[tempClickj2][i]==0){
				lianxian2.add(i+tempClickj2*12);
				tempsum2++;
			}
			else{
				break;
			}
		}
		//�õ�������������еĿ������ߵ�λ�� 
		for(int i=tempClicki2-1;i>=0;i--){
			if(tempmap[tempClickj2][i]==0){
				lianxian2.add(i+tempClickj2*12);
				tempsum2++;
			}
			else{
				break;
			}
		}
		//
		for(int i=0;i<tempsum1;i++){
			for(int j=0;j<tempsum2;j++){
				if(lianxian1.get(i)==lianxian2.get(j)){
					return true;
				}
			}
		}
		return false;
	}


	/**
	 * �ж��Ƿ�Ҫ��������
	 */
	public boolean isCut(){
		//������ε����λ���ڵ�ͼ�������ǲ���ͬһ�ָ��Ӷ��Ҳ���ͬһ��λ�þ�  ע�����зŵ�λ��
		if((map[clickj[0]][clicki[0]]==map[clickj[1]][clicki[1]])
				&&(clickj[0]!=clickj[1]||clicki[0]!=clicki[1])){
//			Log.d("judge", ""+judge());
			if(judge()){
				sumSprite=sumSprite-2;
				if(isMusic){
					//������Ч
					m_hint.playSoundPool();
				}
				//�����������ڵ�ͼ�����е����ݱ�Ϊ0����������
				for(int k=0;k<2;k++){
					map[clickj[k]][clicki[k]]=0;	
				}
				//���жϸ����Ƿ�������
				if(sumSprite==0){
					addlevel();
					nextMusic.playSoundPool();
				}
				//�ı��ͼ
				changeMap();
				if(clock()){//�ж��Ƿ����� ����������ϴ��
					xipai();
				}
				if(difficulty==0){
					score=score+1;
				}
				else if(difficulty==1){
					score=score+2;
				}
				else if(difficulty==2){
					score=score+3;
				}
				return true;
			}
			
		}
		return false;
	}
	/**
	 * ��¼�����λ�ã���ͼ�����е���������
	 */
	public void recordClick(int i,int j){
//		Log.d("�����i,j", ""+i+","+j);
		//�ж���û�м�¼��һ�ε�λ��
		if(clickj[0]==-1){
			clickj[0]=j;
			clicki[0]=i;
//			Log.d("��¼�ڵ�һ��", ""+i+","+j);
		}
		else {//˵����һ�����ӱ���¼��
//			Log.d("��¼�ڵڶ���", ""+i+","+j);
			clickj[1]=j;
			clicki[1]=i;
			//�ж��Ƿ�Ҫ��������
			if(isCut()){
				changeMap();//�ı��ͼ����
			}
			//�Ѽ�¼���λ�õ���������
			for(int k=0;k<2;k++){
				clickj[k]=-1;
				clicki[k]=-1;
			}
		}
	}
	/**
	 * ��Ļ��������
	 */
	public boolean onTouchEvent(MotionEvent event) {
		float tempx=event.getX()/LLKActivity.size_w-30;
		float tempy=event.getY()/LLKActivity.size_h-60;
		
		switch(GAME_STATE){
		case 1://��Ϸ��
			{
				//Ϊ��ť��Ӵ���
				for(int i = 0;i<m_button.length;i++)
				{
					m_button[i].onTouchEvent(event);
				}
				if(isMusic){
					m_music[1].onTouchEvent(event);
				}else{
					m_music[0].onTouchEvent(event);
				}

				//�ж��Ƿ����˸��ӵ�����
				if(tempx>0&&tempx<600&&tempy>0&&tempy<360&&event.getAction()==MotionEvent.ACTION_DOWN){
					//�õ�����˵�ͼ�����λ��
					int tempi=(int) (tempx/60);
					int tempj=(int) (tempy/60);
					//��¼�����λ��ע����������
					if(map[tempj][tempi]!=0){
						recordClick(tempi, tempj);
					}
					
				}
			}
			break;
		case 2://��Ϸ��ͣ
			{
				ctn_butButton.onTouchEvent(event);
				exit_butButton.onTouchEvent(event);
			}
			break;
		case 3://��Ϸ����
			{
				ctn_butButton.onTouchEvent(event);
				exit_butButton.onTouchEvent(event);
			}
			break;
			
		}
		return true;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		//��������Ϸ�߳�
		gameViewDrawThread=new GameViewDrawThread(this,getHolder());//��ʼ�������߳�
		gameViewDrawThread.setFlag(true);
		gameViewDrawThread.start();

		//��������
		if(isMusic&&GAME_STATE!=PAUSE){
			playMusic();		
		}

	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		
		 boolean retry = true;
	        gameViewDrawThread.setFlag(false);//ֹͣˢ֡�߳�
	        while (retry) {
	            try {
	            	gameViewDrawThread.join();//�ȴ�ˢ֡�߳̽���
	                retry = false;
	            } 
	            catch (InterruptedException e) {//���ϵ�ѭ����ֱ���ȴ����߳̽���
	            }
	        }
	
	}
}
