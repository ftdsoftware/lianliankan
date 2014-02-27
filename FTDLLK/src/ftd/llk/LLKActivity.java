package ftd.llk;


import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

public class LLKActivity extends Activity implements MyDefine{

	public static LLKActivity APP = null;
	private static float screenW,screenH;
	public static float size_w,size_h;
	
	public int LLK_STATE;//整个游戏的状态 游戏中、帮助、设置、欢迎等
	
	WelcomeView welcomeView = null;//欢迎界面
	WelcomeViewGoThread welcomeViewGoThread = null;//欢迎界面中的移动线程
	MenuView menuView = null;
	MenuViewGoThread menuViewGoThread = null;//菜单界面中的移动线程
	GameView gameView = null;
	GameViewGoThread gameViewGoThread;
	HelpView helpView=null;
	HelpViewGoThread helpViewGoThread;
	OptionView optionView;
	OptionViewGoThread optionViewGoThread;
	RakingView rakingView;
	
	
	boolean isSound = true;//是否播放声音
	MediaPlayer hitSound;//消掉格子的声音
	MediaPlayer backSound;//背景音乐
	MediaPlayer winSound;//胜利的音乐
	MediaPlayer startSound;//开始和菜单时的音乐
	
	MySprite mySprite;//精灵

	
	Handler myHandler = new Handler(){//用来更新UI线程中的控件
        public void handleMessage(Message msg) {
        	LLK_STATE=msg.what;
        	
        	if(msg.what == GAME_LOGO){//收到WelcomeViewGoThread/Welcome发来的消息
        		//welcomeViewGoThread.setFlag(false);
        		if(welcomeView != null){
        			welcomeView = null;  
        		}
        		initAndToMenuView();
        	}
        	else if(msg.what == GAME_MENU){//收到MenuView发来的消息
        		if(menuView != null){
        			menuView = null;  
        		}
        		initAndToMenuView();
        		
        	}   
        	else if(msg.what == GAME_START){
        		if(gameView != null){
        			gameView = null;  
        		}
        		initAndToGameView();
        	} 
        	else if(msg.what == GAME_RAKING){
        		if(rakingView!=null)
        			rakingView=null;
        		initAndToRakingView();
        	} 
        	else if(msg.what == GAME_OPTION){
        		if(optionView!=null){
        			optionView=null;
        		}
        		initOption();
        	} 
        	else if(msg.what==GAME_HELP){
        		Log.d("help", "continue");
        		if(helpView!=null){
        			helpView=null;
        		}
        		initAndToHelpView();
        	}

        }
	}; 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        APP=this;
	 	setNoTitle();
        setFullScreen();
        reGetWH();
        //设置横竖屏<android:screenOrientation="portrait">
        setScreenHorizontal();			//设置横

//		initAndToWelcomeView();			//初始化欢迎界面
        initAndToMenuView();
        
    }
    //对存储的数据进行操作
    public void sharprefrece(){
//		sp = getSharedPreferences("myPref",MODE_WORLD_WRITEABLE);//第一个参数为文件名，第二个为数据操作模式
//		editor= sp.edit();
//
//        editor.commit();
	}
    /**
     * 初始化欢迎界面
     */
    public void initAndToWelcomeView(){
    	
    }
    /**
     * 初始化排行榜界面
     */
    public void initAndToRakingView(){
    	rakingView=new RakingView(this);
    	this.setContentView(rakingView);
    }
    /**
     * 初始化设置界面
     */
    public void initOption(){
    	optionView=new OptionView(this);
    	this.setContentView(optionView);
//    	optionViewGoThread=new OptionViewGoThread(this);
//    	optionViewGoThread.start();
    }
    /**
     * 初始化菜单界面
     */
    public void initAndToMenuView(){
    	menuView = new MenuView(this);
    	this.setContentView(menuView);
    	menuViewGoThread = new MenuViewGoThread(this);
    	menuViewGoThread.start();

    }
    /**
     * 初始化游戏界面
     */
    public void initAndToGameView(){
		gameView=new GameView(this);
		this.setContentView(gameView);
		gameViewGoThread=new GameViewGoThread(this);
		gameViewGoThread.start();
    }
    /**
     * 初始化帮助界面
     */
    public void initAndToHelpView(){
		helpView=new HelpView(this);
		this.setContentView(helpView);
    	helpViewGoThread = new HelpViewGoThread(this);
    	helpViewGoThread.start();
    }
    /**
     * 覆盖返回键方法
     */
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
	     // 按下键盘上返回按钮  
	     if (keyCode == KeyEvent.KEYCODE_BACK) {  
	    	 if(LLK_STATE==GAME_HELP)
	    		 APP.myHandler.sendEmptyMessage(GAME_MENU);
	    	 else if(LLK_STATE==GAME_START){
	    		 gameView.m_bjmusicMedia[gameView.musicID].stop();
	    		 gameView.GAME_STATE=gameView.PAUSE; 
	    	 }
	    	 else if(LLK_STATE==GAME_HELP)
	    		 APP.myHandler.sendEmptyMessage(GAME_MENU);
	    	 else if(LLK_STATE==GAME_MENU)
	    		 dialog();
	    	 else if(LLK_STATE==GAME_OPTION)
	    		 APP.myHandler.sendEmptyMessage(GAME_MENU);
	    	 else if(LLK_STATE==GAME_RAKING)
	    		 APP.myHandler.sendEmptyMessage(GAME_MENU);
	    	 
	     } 
		return false;     
	 } 
	protected void dialog() {
	       Builder builder = new Builder(LLKActivity.this);
	       builder.setMessage("确认退出吗？");
	       builder.setTitle("提示");
	       builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	         dialog.dismiss();
	         APP.finish();
	        }
	       });
	       builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	         dialog.dismiss();
	        }
	       });
	       AlertDialog dialog=builder.create();
	       dialog.show();
	}
    /**
     * 设置为全屏模式
     */
    public void setFullScreen()
    {
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	
    }
    
    public void setNoTitle()
    {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);		
    }
    
    /**
     * 获取屏幕方向
     */
    public int getScreenDirection()
    {
    	return getRequestedOrientation();
    }
    
    /**
     * 设置屏幕显示方向
     * @param direction 取值说明
     *  ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,//横屏
     *  ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,//竖屏
     */
        public void setScreenDirection(int direction)
        {
        	setRequestedOrientation(direction);
        }
    
    /**
     * 设置横向屏幕
     */
    public boolean setScreenHorizontal()
    {
    	if(this.getScreenDirection() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
    	{
    		setScreenDirection(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    		return true;
    	}
    	return false;
    }
    
    /**
     * 设置竖向屏幕
     */
    public boolean setScreenVertical()
    {
    	if(this.getScreenDirection() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    	{
    		setScreenDirection(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    		return true;
    	}
    	return false;
    }
    
    /**
     * 获取屏幕宽高，并保存；
     */
    public void reGetWH()
    {
        Display display = getWindowManager().getDefaultDisplay();
        screenW = display.getWidth();
        screenH = display.getHeight();
        size_w = screenW/ON_SCREEN_WIDTH;
        size_h = screenH/ON_SCREEN_HIGHT;
       
    }
    

    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	
    	if(gameView!=null&&gameView.isMusic){
    		gameView.m_bjmusicMedia[gameView.musicID].stop();
    		gameView.GAME_STATE=gameView.PAUSE;
	        } 
    	if(gameView!=null&&gameView.GAME_STATE==gameView.GAMEOVER){
    		gameView.m_bjmusicMedia[15].stop();	
    	}
    }


}
