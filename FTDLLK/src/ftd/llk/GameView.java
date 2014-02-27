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
	
	MyBJ gameBj;//游戏背景
	MyBJ pauseBj;//游戏暂停画面
	MyBJ gameoverBj;//游戏结束画面
	MyButton m_button[];//游戏中用到的按钮
	MyButton m_music[];//控制音乐的按钮
	MyButton ctn_butButton;//继续按钮
	MyButton exit_butButton;//退出按钮
	MySprite m_mySprite;//游戏中的格子
	MyBJ m_mask;//用来覆盖点击的格子
	Bitmap spriteBitmap;//游戏格子的图片
	int map[][]=new int[MAP_R][MAP_C];//游戏地图数组
	int gamelevel;//游戏关数
	int addTime;//加时次数
	int hint;//提示次数
	int xipaisum;//洗牌次数
	int score;//得分
	int tempscore;
	int bestScore;//最高分
	int difficulty;//难度
	String difString=null;

	int sumSprite=60;//记录格子数当格子数为0时进行下一关
	int clickj[]=new int[2];//记录点击行的位置只记录两次点击的位置 后面行列注意别混淆
	int clicki[]=new int[2];//记录点击列的位置
	
	int GAME_STATE;//游戏状态 有暂停、游戏中、游戏结束
	int START=1;//游戏中
	int PAUSE=2;//暂停
	int GAMEOVER=3;//游戏结束
	int gameTime;//游戏规定的时间
	
	boolean isMusic=true;
	MyPlayerSound m_hint;//消掉格子的声音
	MyPlayerMedia m_bjmusicMedia[];//背景音乐
	int musicID;
	/**
	播放第几个的音乐  现在构造函数随机一个0到16的数在每次停止之后在播放的时候数加1
	不能每次都随机因为怕随机到同一个数这样会报错因为这个media已经stop不能再start了
	*/
	MyPlayerSound nextMusic;//下一关音效
	MyPlayerSound jiangliMusic;//奖励音效
	
	SharedPreferences sp;//数据操作
	SharedPreferences.Editor editor;
	
	public GameView(LLKActivity llkActivity){
		super(llkActivity);
		this.llkActivity=llkActivity;
		getHolder().addCallback(this);	
		

		GAME_STATE=START;
		
		initGameView();//初始化游戏界面
		initMusic();//初始化音乐
	}
	
	/**
	 * 初始化游戏界面
	 */
	public void initGameView(){
		//初始化背景
		gameBj=new MyBJ(MyTools.loadFromAsset("image/bjimg/gamebj.jpg"));
		//初始化游戏暂停画面
		pauseBj=new MyBJ(MyTools.loadFromAsset("image/bjimg/pause.png"));
		pauseBj.setpos(30, 60);
		//初始化游戏结束画面
		gameoverBj=new MyBJ(MyTools.loadFromAsset("image/bjimg/gameover.png"));
		gameoverBj.setpos(30, 60);
		//按钮加载
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
		//加载游戏格子要用到的图片
		spriteBitmap=MyTools.loadFromAsset("image/gameimg/game1.png");
		//初始化格子精灵类
		m_mySprite=new MySprite(spriteBitmap);
		m_mask=new MyBJ(MyTools.loadFromAsset("image/gameimg/mask.png"));
		
		sp = llkActivity.getSharedPreferences("myPref",2);//第一个参数为文件名，第二个为数据操作模式
		editor= sp.edit();
		if(sp!=null){
			Log.d("sp", "not null");
			String music=null;
			music=sp.getString("isMusic", "");
			isMusic=new Boolean(music);
			difficulty=sp.getInt("difficulty", 0);
			bestScore=sp.getInt("bestSocre0", 0);
		}
		initData();//先初始化数据在初始化地图不然地图初始化会出错
		initMap();
		
	}
	/**
	 * 初始化音乐
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
	 * 初始化数据
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
	 * 初始化游戏数组地图
	 */
	public void initMap(){
		//初始化地图数组把1到15赋值给地图数组每隔数赋值三次tempi表示在地图数组中的位置tempi/10表示行数tempi%10表示列数
		int temp=MyTools.getRan(11);//每一关的图片都都是随机出现
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
		//对地图数组进行洗牌操作
		xipai();
		//将记录点都赋值为-1 后面要数值进行判断是否记录的点击点 -1代表没有记录
		for(int i=0;i<2;i++){
			clicki[i]=-1;
			clickj[i]=-1;
		}
	}
	/**
	 * 将地图数组洗牌打乱
	 * @param map传入的地图数组
	 * 随机生成4个数代表数组中的行和列，通过四个数得到地图数组的两个变量，在将变量进行交换 100代表洗牌的次数
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
	//用来保存登上排行榜的分数
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
	 * 画游戏界面
	 */
	protected void onDraw(Canvas c) {
		//处理屏幕自适应问题，任何机型都适应，宽高比例悬殊太大的机型会有视觉差，但不影响操作
		c.save();
//		Log.d("窗口", "doDraw绘制");
		c.scale(llkActivity.size_w, llkActivity.size_h);
		//刷屏
		c.drawColor(0xff000000);
		//画背景
		gameBj.paint(c);
		
		//画游戏信息
		Paint p=new Paint();

		p.setColor(Color.RED);
		p.setTextSize(22);
		p.setTypeface(Typeface.DEFAULT_BOLD);
		c.drawText("生命值", 13, 25, p);
		//根据时间的值得到矩形的长度
		c.drawRect(90,8, 90+(gameTime/1000)*4, 28, p);
		c.drawText("得分： "+score, 670, 25, p);
		p.setARGB(254, 47, 53, 243);
		c.drawText("洗牌： "+xipaisum, 670, 55, p);
		c.drawText("提示： "+hint, 670, 85, p);
		c.drawText("加时： "+addTime, 670, 115, p);
		
		c.drawText("难度："+difString+"  关数："+gamelevel, 13, 55, p);
		
		//画按钮
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
		case 1://游戏中
			//画游戏格子
			for(int j=0;j<MAP_R;j++){
				for(int i=0;i<MAP_C;i++){
					m_mySprite.setPos(30+i*60, 60+j*60);
					if(map[j][i]!=0){
						m_mySprite.setIndex(map[j][i]-1);
						m_mySprite.paint(c);
					}
				}
			}
			//画点击了格子上的人吃蛋糕
			if(clicki[0]!=-1&&clickj[0]!=-1){
				m_mask.setpos(30+60*clicki[0], 60+60*clickj[0]);
				m_mask.paint(c);
			}
			
			break;
		case 2://游戏暂停
			{
				pauseBj.paint(c);
				ctn_butButton.paint(c);
				exit_butButton.paint(c);
			}
			break;
		case 3://游戏结束
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
	 * 关数增加 先判断格子是否都消掉了
	 */
	public void addlevel(){
			gamelevel++;//关数加1
			if(gamelevel>11){
				gamelevel=gamelevel%11;
				gameTime=100000-(20*difficulty*1000)-gamelevel*5000;
			}
			else{
				gameTime=100000-(20*difficulty*1000);
			}
			sumSprite=60;
			//重新给地图数组赋值
			int tempi=0;
			for(int i=0;i<4;i++){
				for(int j=0;j<15;j++){
					map[tempi/10][tempi%10]=j+1+(gamelevel-1)*15;
					tempi++;
				}
			}
			//对地图数组进行洗牌操作
			xipai();
			//播放音乐
			if(isMusic){
				m_bjmusicMedia[musicID].stop();
				playMusic();
			}
	}
	/**
	 * 不同的关数消掉格子之后地图改变的方式不同
	 */
	public void changeMap(){
		switch(gamelevel){
			case 11:
				break;
			case 2:
				{//格子往小掉  循环遍历每一列，有0的地方就上下交换数据 没一列循环两次是为了消掉上下都是0的情况
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
				{//格子向右移  循环遍历每一列，有0的地方就左右交换数据 没一列循环两次是为了消掉上下都是0的情况 j用来判读是否在最边上
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
				//上半部  格子往上  循环遍历每一列，有0的地方就上下交换数据 没一列循环两次是为了消掉上下都是0的情况
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
				
				//下半部  格子往下掉  循环遍历每一列，有0的地方就上下交换数据 没一列循环两次是为了消掉上下都是0的情况
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
					//格子向右移  循环遍历每一列，有0的地方就左右交换数据 没一列循环两次是为了消掉上下都是0的情况 j用来判读是否在最边上
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
					
					//下半部  格子往下掉  循环遍历每一列，有0的地方就上下交换数据 没一列循环两次是为了消掉上下都是0的情况
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
				{//上部分
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
					//下部分
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
			{//左部分
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
				//右部分
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
			{//上部分往左
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
				//下部分往右
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
				{//左边往下
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
					//右边往上
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
				//上半部  格子往上  循环遍历每一列，有0的地方就上下交换数据 没一列循环两次是为了消掉上下都是0的情况
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
				
				//下半部  格子往下掉  循环遍历每一列，有0的地方就上下交换数据 没一列循环两次是为了消掉上下都是0的情况
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
				
				//格子向右移  循环遍历每一列，有0的地方就左右交换数据 没一列循环两次是为了消掉上下都是0的情况 j用来判读是否在最边上
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
				
				//下半部  格子往下掉  循环遍历每一列，有0的地方就上下交换数据 没一列循环两次是为了消掉上下都是0的情况
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
				//上部分
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
				//下部分
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
				//左部分
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
				//右部分
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
	 * 判断游戏是否死锁 死锁就重新洗牌 思路和hint的差不多
	 */
	public boolean clock(){
		//j，i第一个点击点的坐标 k、l第二个点击点的坐标 
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
										//同一个点不进行连线判断
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
	 * 提示方法当玩家点击提示就用来消掉一对格子
	 * 对地图数组中的点循环遍历 先从所有的点拿一个点和所有的点对比看能否符合消掉条件一直循环将所有的点都比较   消掉一个格子就break
	 * 
	 */
	public void hint(){
		//j，i第一个点击点的坐标 k、l第二个点击点的坐标 
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
								//同一个点不进行连线判断
								if(judge()&&(clicki[0]!=clicki[1]||clickj[0]!=clickj[1])){
									sumSprite=sumSprite-2;
									//将两个格子在地图数组中的数据变为0即消掉格子
									for(int p=0;p<2;p++){
										map[clickj[p]][clicki[p]]=0;	
									}
									clicki[0]=-1;
									clickj[0]=-1;
									clicki[1]=-1;
									clickj[1]=-1;
									//再判断格子是否都消掉了
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
	 * 判断两个格子的连线转弯是不是小于3个
	 * 思路是利用广度优先算法 ，先求出一个点的转三个弯可以到达的所有点并保存在一个集合 ，
	 * 再用另一个点去判断是否挨着这个集合中的某一个点
	 * @return 
	 */
	public boolean judge(){
		/**先判断两个点是否相邻**/
		if((Math.abs(clicki[0]-clicki[1])==1&&Math.abs(clickj[0]-clickj[1])==0)||
				(Math.abs(clicki[0]-clicki[1])==0&&Math.abs(clickj[0]-clickj[1])==1)){
			return true;
		}
		
		/* 不相邻就进行下面的算法*/
		
		
		int tempmap[][]=new int[8][12];//将地图数组保存到临时数组 临时数组比地图数组上下左右多一行
	     Vector lianxian1=new Vector();
		/**用来保存第一个点转三次弯可以到达位置的坐标，
		数值等于在地图数组中的排序位置 从左到右 上到下 每一个格子都有唯一的一个值**/
	     Vector lianxian2=new Vector();//用来保存第二个点击点可以连线的位置
		//初始化临时数组 都赋值为0
		for(int j=0;j<8;j++){
			for(int i=0;i<12;i++){
				tempmap[j][i]=0;
			}
		}
		//将地图数组中的值放到临时数组中 上下左右空一行出来 有值的地方赋值为1 没有值的地方为0 0表示可以通过连线1表示不可以连线
		for(int j=1;j<7;j++){
			for(int i=1;i<11;i++){
				if(map[j-1][i-1]!=0){
					tempmap[j][i]=1;
				}
			}
		}
		//clicki【0】是游戏地图数组中的坐标 比临时地图数组中的值小1
		int tempClicki1=clicki[0]+1;
		int tempClickj1=clickj[0]+1;
		lianxian1.add(tempClickj1*12+tempClicki1);
		int tempsum1=1;//用来统计有多少个格子可以连线到点击点 同时也是lianxian数组的下标
		//得到点击点上面所有的可以连线的位置 从点击点上面开始搜索
		for(int i=tempClickj1-1;i>=0;i--){
			if(tempmap[i][tempClicki1]==0){
				lianxian1.add(i*12+tempClicki1);
				tempsum1++;
			}else{
				break;
			}
		}
		//得到点击点下面所有的可以连线的位置 从点击点下面开始搜索
		for(int i=tempClickj1+1;i<8;i++){
			if(tempmap[i][tempClicki1]==0){
				lianxian1.add(i*12+tempClicki1);
				tempsum1++;
			}else{
				break;
			}
		}
		//得到点击点右面所有的可以连线的位置 
		for(int i=tempClicki1+1;i<12;i++){
			if(tempmap[tempClickj1][i]==0){
				lianxian1.add(i+tempClickj1*12);
				tempsum1++;
			}
			else{
				break;
			}
		}
		//得到点击点左面所有的可以连线的位置 
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
		//第二次连线 将第一次得到的位置数组里面的点进行连线
		for(int k=1;k<firstSum;k++){
			//vector中保存的是object类型要转换为int类型
			tempClicki1=(Integer.parseInt(String.valueOf(lianxian1.get(k))))%12 ;
			tempClickj1=(Integer.parseInt(String.valueOf(lianxian1.get(k))))/12;
			if(tempClicki1==12){
				tempClicki1=0;
			}
			if(tempClickj1==8){
				tempClickj1=0;
			}
			//得到点击点上面所有的可以连线的位置 从点击点上面开始搜索
			for(int i=tempClickj1-1;i>=0;i--){
				if(tempmap[i][tempClicki1]==0){
					lianxian1.add(i*12+tempClicki1);
					tempsum1++;
				}else{
					break;
				}
			}
			//得到点击点下面所有的可以连线的位置 从点击点下面开始搜索
			for(int i=tempClickj1+1;i<8;i++){
				if(tempmap[i][tempClicki1]==0){
					lianxian1.add(i*12+tempClicki1);
					tempsum1++;
				}else{
					break;
				}
			}
			//得到点击点右面所有的可以连线的位置 
			for(int i=tempClicki1+1;i<12;i++){
				if(tempmap[tempClickj1][i]==0){
					lianxian1.add(i+tempClickj1*12);
					tempsum1++;
				}
				else{
					break;
				}
			}
			//得到点击点左面所有的可以连线的位置 
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
		//将第二个点击点进行连线不转弯搜索
		int tempClicki2=clicki[1]+1;
		int tempClickj2=clickj[1]+1;
		lianxian2.add(tempClickj2*12+tempClicki2);
		int tempsum2=1;//用来统计有多少个格子可以连线到点击点 同时也是lianxian数组的下标
		//得到点击点上面所有的可以连线的位置 从点击点上面开始搜索
		for(int i=tempClickj2-1;i>=0;i--){
			if(tempmap[i][tempClicki2]==0){
				lianxian2.add(i*12+tempClicki2);
				tempsum2++;
			}else{
				break;
			}
		}
		//得到点击点下面所有的可以连线的位置 从点击点下面开始搜索
		for(int i=tempClickj2+1;i<8;i++){
			if(tempmap[i][tempClicki2]==0){
				lianxian2.add(i*12+tempClicki2);
				tempsum2++;
			}else{
				break;
			}
		}
		//得到点击点右面所有的可以连线的位置 
		for(int i=tempClicki2+1;i<12;i++){
			if(tempmap[tempClickj2][i]==0){
				lianxian2.add(i+tempClickj2*12);
				tempsum2++;
			}
			else{
				break;
			}
		}
		//得到点击点左面所有的可以连线的位置 
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
	 * 判断是否要消除格子
	 */
	public boolean isCut(){
		//如果两次点击的位置在地图数组中是不是同一种格子而且不是同一个位置就  注意行列放的位置
		if((map[clickj[0]][clicki[0]]==map[clickj[1]][clicki[1]])
				&&(clickj[0]!=clickj[1]||clicki[0]!=clicki[1])){
//			Log.d("judge", ""+judge());
			if(judge()){
				sumSprite=sumSprite-2;
				if(isMusic){
					//播放音效
					m_hint.playSoundPool();
				}
				//将两个格子在地图数组中的数据变为0即消掉格子
				for(int k=0;k<2;k++){
					map[clickj[k]][clicki[k]]=0;	
				}
				//再判断格子是否都消掉了
				if(sumSprite==0){
					addlevel();
					nextMusic.playSoundPool();
				}
				//改变地图
				changeMap();
				if(clock()){//判读是否死锁 死锁就重新洗牌
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
	 * 记录点击的位置，地图数组中的行列坐标
	 */
	public void recordClick(int i,int j){
//		Log.d("点击了i,j", ""+i+","+j);
		//判断有没有记录第一次的位置
		if(clickj[0]==-1){
			clickj[0]=j;
			clicki[0]=i;
//			Log.d("记录在第一个", ""+i+","+j);
		}
		else {//说明有一个格子被记录啦
//			Log.d("记录在第二个", ""+i+","+j);
			clickj[1]=j;
			clicki[1]=i;
			//判断是否要消掉格子
			if(isCut()){
				changeMap();//改变地图数组
			}
			//把记录点击位置的数据消掉
			for(int k=0;k<2;k++){
				clickj[k]=-1;
				clicki[k]=-1;
			}
		}
	}
	/**
	 * 屏幕触摸操作
	 */
	public boolean onTouchEvent(MotionEvent event) {
		float tempx=event.getX()/LLKActivity.size_w-30;
		float tempy=event.getY()/LLKActivity.size_h-60;
		
		switch(GAME_STATE){
		case 1://游戏中
			{
				//为按钮添加触控
				for(int i = 0;i<m_button.length;i++)
				{
					m_button[i].onTouchEvent(event);
				}
				if(isMusic){
					m_music[1].onTouchEvent(event);
				}else{
					m_music[0].onTouchEvent(event);
				}

				//判读是否点击了格子的区域
				if(tempx>0&&tempx<600&&tempy>0&&tempy<360&&event.getAction()==MotionEvent.ACTION_DOWN){
					//得到点击了地图数组的位置
					int tempi=(int) (tempx/60);
					int tempj=(int) (tempy/60);
					//记录点击的位置注意行列区分
					if(map[tempj][tempi]!=0){
						recordClick(tempi, tempj);
					}
					
				}
			}
			break;
		case 2://游戏暂停
			{
				ctn_butButton.onTouchEvent(event);
				exit_butButton.onTouchEvent(event);
			}
			break;
		case 3://游戏结束
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
		//开启画游戏线程
		gameViewDrawThread=new GameViewDrawThread(this,getHolder());//初始化绘制线程
		gameViewDrawThread.setFlag(true);
		gameViewDrawThread.start();

		//播放音乐
		if(isMusic&&GAME_STATE!=PAUSE){
			playMusic();		
		}

	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		
		 boolean retry = true;
	        gameViewDrawThread.setFlag(false);//停止刷帧线程
	        while (retry) {
	            try {
	            	gameViewDrawThread.join();//等待刷帧线程结束
	                retry = false;
	            } 
	            catch (InterruptedException e) {//不断地循环，直到等待的线程结束
	            }
	        }
	
	}
}
