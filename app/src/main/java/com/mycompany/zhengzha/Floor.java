package com.mycompany.zhengzha;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.worldopengles.zhengzha.Game.GameRun;
import com.worldopengles.zhengzha.Game.WLoader;
import com.worldopengles.zhengzha.Geometry.ColorGeometry;
import com.worldopengles.zhengzha.Geometry.TextureGeometry;
import com.worldopengles.zhengzha.Wtool.WMesh;
import javax.vecmath.Vector3f;
import com.worldopengles.zhengzha.Wtool.ColorTool;

public class Floor
{
    //几何体
    private ColorGeometry geometry;
    
  
    public Floor(GameRun run,DynamicsWorld world,StaticPlaneShape shape){
     float width=10;
        float vertex[]={
           -width,0,-width,
           -width,0,width,
           width,0,width,
           
           -width,0,-width,
           width,0,width,
           width,0,-width
       };
        WMesh mesh=new WMesh(vertex);
        geometry=new ColorGeometry(run,mesh);
        geometry.setColor(new ColorTool(1,0,0,1));
        //惯性向量
        Vector3f localinertia = new Vector3f(0, 0, 0); 
        //计算形状的惯性,把地板质量设置为0
        shape.calculateLocalInertia(0, localinertia);
        //创建一个刚体的变换储存类,后面可以用这个提取当前的旋转和位置
        Transform transform = new Transform(); 
        //初始化变换
        transform.setIdentity(); 
        //设置刚体初始化位置
        transform.origin.set(new Vector3f(0,0,0)); 
        //创建刚体运动状态
        DefaultMotionState motionState = new DefaultMotionState(transform);
        //创建描述刚体的对象
        RigidBodyConstructionInfo info = new RigidBodyConstructionInfo
        (0,
         motionState, 
         shape,
         localinertia
         ); 
        //创建刚体，把描述对象传入进去
       RigidBody body = new RigidBody(info); 
        //设置刚体的反弹
        body.setRestitution(0.4f); 
        //设置刚体的摩擦
        body.setFriction(0.6f); 
        //把刚体加入到物理世界
        world.addRigidBody(body); 
    }
    
    //因为是静止的，这个是为了感觉有地板的存在
    //然后就画一个地板所以不需要变换
    public void draw(WLoader loader){
        geometry.genMatrix();
        geometry.translate(0,0,0);
        loader.addObject(geometry);
    }
    
}
