package com.mycompany.zhengzha;


import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.CollisionConfiguration;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.worldopengles.zhengzha.Game.DrawRenderer;
import com.worldopengles.zhengzha.Game.FpsCameraTool;
import com.worldopengles.zhengzha.Game.GameRun;
import com.worldopengles.zhengzha.Game.WLoader;
import com.worldopengles.zhengzha.Game.WorldTool;
import com.worldopengles.zhengzha.Wtool.ColorTool;
import javax.vecmath.Vector3f;
import com.bulletphysics.dynamics.DynamicsWorld;

public class MainActivity extends Activity
{
	
	//渲染画面的控件
	private GameRun gamerun;
	//渲染器
	private DrawRenderer renderer;
	//世界配置
	private WorldTool word;
	private Button btS,btX,btZ,btY,jia;
	//第一人称摄像头
	private FpsCameraTool fpsCamera;
	private float 速度=0.2f;
    //物理世界
    private DynamicsWorld world;
    //定义一个长方体形状
	private BoxShape box;
    //定义一个静止的平面形状
    private StaticPlaneShape floorShape;
    private Floor floor;
    private Block block;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
	
		setContentView(R.layout.main);
		btS=(Button)findViewById(R.id.s);
		btX=(Button)findViewById(R.id.x);
		btZ=(Button)findViewById(R.id.z);
		btY=(Button)findViewById(R.id.y);
		jia=(Button)findViewById(R.id.jia);
		
					//新建一个世界工具对象
		word=new WorldTool(this);
		//初始化渲染器
		init();
		gamerun=(GameRun)findViewById(R.id.game);
		//为渲染器设置世界配置
		gamerun.setWordTool(word);
		//为渲染控件设置渲染器
		gamerun.setDrawRenderer(renderer);
  
		btS.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					//前进
					fpsCamera.moveSX(速度);
				}
				
			
		});

        final Vector3f vec=new Vector3f(0,5,0);
        jia.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View p1)
                {
                  
                 block.li(vec);
                }


            });
        
		btX.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					//退后
					fpsCamera.moveSX(-速度);
				}


			});
			
		btZ.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					//右边移动
					fpsCamera.moveZY(速度);
				}


			});
		
		btY.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					//左边移动
					fpsCamera.moveZY(-速度);
				}


			});
			
			
		gamerun.setOnTouchListener(new OnTouchListener(){
				private float PreviousY;
				private float PreviousX;
				@Override
				public boolean onTouch(View p1, MotionEvent e)
				{
					float y = e.getY();
					float x = e.getX();
					switch (e.getAction()) {
						
                        
						case MotionEvent.ACTION_MOVE:
							float dy = y - PreviousY;
							float dx = x - PreviousX;
                            //视角上下移动
							fpsCamera.moveLookY(-dy*速度);
							//视角左右移动
							fpsCamera.moveLookX(dx*速度);
					}
					PreviousY = y;
					PreviousX = x;


					return true;
				}
 

			});
			
	}
   
	private void init()
	{
		
		
		renderer = new DrawRenderer(){

            @Override
            public void StopClock()
            {
                // TODO: Implement this method
            }
            
 
			@Override
			public void SurfaceChanged(int width, int heigth)
			{
				// TODO: Implement this method
			}
			
       
			
			@Override
			public void draw(int error,WLoader loader)
			{
                fpsCamera.updateCamera();
             
              //画地板
              floor.draw(loader);
              //画立方体
              block.draw(loader);
					}

                    
            
			@Override
			public void SurfaceCreated()
			{
                word.setBackground(new ColorTool(1,1,1,1));
                fpsCamera=new FpsCameraTool(0,0,0,0,0,1);
                //初始化物理世界
                initWorld();
                //建立地板
               floor=new Floor(gamerun,world,floorShape);
               //建立一个箱子
               //初始位置在0,5,0
               //质量为1
               //边是1，所以是0.5f
               block=new Block(gamerun,world,0,5,0,box,1,0.5f);
               //创建一个线程来不断模拟物理世界
                new Thread(new Runnable(){

                        @Override
                        public void run()
                        {
                            while(true){
                                //模拟
                                //第二参数数字越大模拟越真实
                                //但速度越慢
                                //第一个参数不知道如果把60改成30速度会快，就像快进
                                world.stepSimulation(1/60.0f,3);
                                try
                                {
                                    Thread.sleep(20);
                                }
                                catch (InterruptedException e)
                                {}
                            }
                        }
                        
                   
               }).start();
			}


		};
	
}


public void initWorld(){
//碰撞配置类
    CollisionConfiguration coll = new DefaultCollisionConfiguration();        
    //碰撞检测器，把配置传入进去
    CollisionDispatcher dis = new CollisionDispatcher(coll);       
    //模拟的世界的大小，超过边界就不会去模拟
    Vector3f worldMin = new Vector3f(-10000, -10000, -10000);
    Vector3f worldMax = new Vector3f(10000, 10000, 10000);
    //模拟的数量
    int maxproxies = 1024;
    //扫描类，把世界的大小和模拟数量传入进去
    AxisSweep3 axis=new AxisSweep3(worldMin, worldMax, maxproxies);
    //求解器?? 
    SequentialImpulseConstraintSolver sol = new SequentialImpulseConstraintSolver();
    //创建一个世界
    world = new DiscreteDynamicsWorld(dis, axis, sol,coll);
    //设置重力方向，下降，速度为10，这是向量
    world.setGravity(new Vector3f(0, -10, 0));
    //长方形的形状定义，长宽高的/2
   box=new BoxShape(new Vector3f(0.5f,0.5f,0.5f));
   //设置平面的参数
   //第一个参数是法线量，第二个是y的坐标，和我那个平面阴影差不多一个意思
   floorShape=new StaticPlaneShape(new Vector3f(0,1,0),0);
   
}


}
