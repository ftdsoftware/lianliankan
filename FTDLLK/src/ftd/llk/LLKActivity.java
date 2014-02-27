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
	
	public int LLK_STATE;//������Ϸ��״̬ ��Ϸ�С����������á���ӭ��
	
	WelcomeView welcomeView = null;//��ӭ����
	WelcomeViewGoThread welcomeViewGoThread = null;//��ӭ�����е��ƶ��߳�
	MenuView menuView = null;
	MenuViewGoThread menuViewGoThread = null;//�˵������е��ƶ��߳�
	GameView gameView = null;
	GameViewGoThread gameViewGoThread;
	HelpView helpView=null;
	HelpViewGoThread helpViewGoThread;
	OptionView optionView;
	OptionViewGoThread optionViewGoThread;
	RakingView rakingView;
	
	
	boolean isSound = true;//�Ƿ񲥷�����
	MediaPlayer hitSound;//�������ӵ�����
	MediaPlayer backSound;//��������
	MediaPlayer winSound;//ʤ��������
	MediaPlayer startSound;//��ʼ�Ͳ˵�ʱ������
	
	MySprite mySprite;//����

	
	Handler myHandler = new Handler(){//��������UI�߳��еĿؼ�
        public void handleMessage(Message msg) {
        	LLK_STATE=msg.what;
        	
        	if(msg.what == GAME_LOGO){//�յ�WelcomeViewGoThread/Welcome��������Ϣ
        		//welcomeViewGoThread.setFlag(false);
        		if(welcomeView != null){
        			welcomeView = null;  
        		}
        		initAndToMenuView();
        	}
        	else if(msg.what == GAME_MENU){//�յ�MenuView��������Ϣ
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
        //���ú�����<android:screenOrientation="portrait">
        setScreenHorizontal();			//���ú�

//		initAndToWelcomeView();			//��ʼ����ӭ����
        initAndToMenuView();
        
    }
    //�Դ洢�����ݽ��в���
    public void sharprefrece(){
//		sp = getSharedPreferences("myPref",MODE_WORLD_WRITEABLE);//��һ������Ϊ�ļ������ڶ���Ϊ���ݲ���ģʽ
//		editor= sp.edit();
//
//        editor.commit();
	}
    /**
     * ��ʼ����ӭ����
     */
    public void initAndToWelcomeView(){
    	
    }
    /**
     * ��ʼ�����а����
     */
    public void initAndToRakingView(){
    	rakingView=new RakingView(this);
    	this.setContentView(rakingView);
    }
    /**
     * ��ʼ�����ý���
     */
    public void initOption(){
    	optionView=new OptionView(this);
    	this.setContentView(optionView);
//    	optionViewGoThread=new OptionViewGoThread(this);
//    	optionViewGoThread.start();
    }
    /**
     * ��ʼ���˵�����
     */
    public void initAndToMenuView(){
    	menuView = new MenuView(this);
    	this.setContentView(menuView);
    	menuViewGoThread = new MenuViewGoThread(this);
    	menuViewGoThread.start();

    }
    /**
     * ��ʼ����Ϸ����
     */
    public void initAndToGameView(){
		gameView=new GameView(this);
		this.setContentView(gameView);
		gameViewGoThread=new GameViewGoThread(this);
		gameViewGoThread.start();
    }
    /**
     * ��ʼ����������
     */
    public void initAndToHelpView(){
		helpView=new HelpView(this);
		this.setContentView(helpView);
    	helpViewGoThread = new HelpViewGoThread(this);
    	helpViewGoThread.start();
    }
    /**
     * ���Ƿ��ؼ�����
     */
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
	     // ���¼����Ϸ��ذ�ť  
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
	       builder.setMessage("ȷ���˳���");
	       builder.setTitle("��ʾ");
	       builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	         dialog.dismiss();
	         APP.finish();
	        }
	       });
	       builder.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	         dialog.dismiss();
	        }
	       });
	       AlertDialog dialog=builder.create();
	       dialog.show();
	}
    /**
     * ����Ϊȫ��ģʽ
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
     * ��ȡ��Ļ����
     */
    public int getScreenDirection()
    {
    	return getRequestedOrientation();
    }
    
    /**
     * ������Ļ��ʾ����
     * @param direction ȡֵ˵��
     *  ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,//����
     *  ActivityInfo.SCREEN_ORIENTATION_PORTRAIT,//����
     */
        public void setScreenDirection(int direction)
        {
        	setRequestedOrientation(direction);
        }
    
    /**
     * ���ú�����Ļ
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
     * ����������Ļ
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
     * ��ȡ��Ļ��ߣ������棻
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
