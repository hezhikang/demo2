package com.mycompany.zhengzha;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.dynamics.DynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.worldopengles.zhengzha.Game.GameRun;
import com.worldopengles.zhengzha.Game.WLoader;
import com.worldopengles.zhengzha.Geometry.TextureGeometry;
import com.worldopengles.zhengzha.Wtool.MeshType;
import com.worldopengles.zhengzha.Wtool.WMesh;
import javax.vecmath.Vector3f;
import javax.vecmath.Quat4f;
import java.security.acl.Group;
import com.worldopengles.zhengzha.Game.Texture;

public class Block
{
    //定义一个箱子类
    //初始位置
    private float x,y,z;
    //形状
    private BoxShape shape;
    //质量
    private float 质量;
    //边的一变
    private float 宽;
    //几何体
    private TextureGeometry geometry;
    //物理世界
    private DynamicsWorld world;
    //刚体
    private RigidBody BlockBody;
    public Block(GameRun run,DynamicsWorld world,float x, float y, float z, BoxShape shape, float 质量, float 宽)
    {
        this.world=world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.shape = shape;
        this.质量 = 质量;
        this.宽 = 宽;
        WMesh mesh=MeshType.getCube(宽);
        geometry=new TextureGeometry(run,mesh);
        int texture=Texture.getAssetsPathTextureId(run.getContext(),"block.png",Texture.TEXTURE_TYPE_NEAREST);
        geometry.setTexture(texture);
        //惯性向量
         Vector3f localinertia = new Vector3f(0, 0, 0); 
         //计算形状的惯性
         shape.calculateLocalInertia(质量, localinertia);
         //创建一个刚体的变换储存类,后面可以用这个提取当前的旋转和位置
         Transform transform = new Transform(); 
         //初始化变换
         transform.setIdentity(); 
         //设置刚体初始化位置
         transform.origin.set(new Vector3f(x,y,z)); 
         //创建刚体运动状态
         DefaultMotionState motionState = new DefaultMotionState(transform);
        //创建描述刚体的对象
         RigidBodyConstructionInfo info = new RigidBodyConstructionInfo
         (质量,
         motionState, 
         shape,
         localinertia
         ); 
        //创建刚体，把描述对象传入进去
         BlockBody = new RigidBody(info); 
         //设置刚体的反弹
         BlockBody.setRestitution(0.4f); 
         //设置刚体的摩擦
         BlockBody.setFriction(0.6f); 
         //把刚体加入到物理世界
         world.addRigidBody(BlockBody); 
         
        
    }
    
    //给刚体一个力
    public void li(Vector3f vec){
        //如果刚体没有碰撞了它就会进行睡眠
        //所以我们给她力之前就先叫醒他
        BlockBody.forceActivationState(RigidBody.DISABLE_DEACTIVATION);
        //只有不休眠的时候才能给力
        BlockBody.setLinearVelocity(vec);
        //让他旋转
        BlockBody.setAngularVelocity(vec);
    }
    
    
    public void draw(WLoader loader){
        //获取刚体在物理世界的变换
       Transform transform=BlockBody.getMotionState()
       .getWorldTransform(new Transform());
        //重置几何体旋转矩阵
        geometry.genMatrix();
        //获取刚体的平移，然后设置几何体的平移
        geometry.translate(
        transform.origin.x,
        transform.origin.y,
        transform.origin.z
        );
        //获取当前旋转的四元数
        Quat4f siyuan=transform.getRotation(new Quat4f());
        if(siyuan.x!=0||siyuan.y!=0||siyuan.z!=0){
        //把四元数转换为角度和旋转轴向量
        float xuan[]=zhuan(siyuan);
        //让几何体旋转
        geometry.rotate(xuan[0],xuan[1],xuan[2],xuan[3]);
        }
        //把几何体加入到渲染列表
        loader.addObject(geometry);
    }
    
    //四元数转换为角度和旋转轴的向量
    public static float[] zhuan(Quat4f siyuan)
    {   
        double dd=Math.acos(siyuan.w);
        float nx=(float) (siyuan.x/Math.sin(dd));
        float ny=(float) (siyuan.y/Math.sin(dd));
        float nz=(float) (siyuan.z/Math.sin(dd));
        return new float[]{(float) Math.toDegrees(dd*2),nx,ny,nz};
    }

    
    
}
