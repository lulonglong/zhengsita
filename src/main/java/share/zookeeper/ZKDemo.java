package share.zookeeper;

import com.alibaba.fastjson.JSONObject;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

public class ZKDemo {
    //zk的连接地址
    //这里的地址修改成自己服务器的地址，2181是zookeeper的端口号
    public static final String zkconnect = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
    //超时时间
    public static final int timeout = 15000;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {

        ZooKeeper zooKeeper = connect();
       // create(zooKeeper,"/zktest/a","zhangsan");
        //delete(zooKeeper,"/azktest/a");
        //setData(zooKeeper,"/zktest/a","changeafter");
        getData(zooKeeper,"/zktest");

    }

    //zk连接方法
    public static ZooKeeper connect() throws IOException {
        ZooKeeper zk = new ZooKeeper(zkconnect,timeout,null);
        System.out.println("zk连接成功");
        return zk;
    }
    //增加
    public static void create(ZooKeeper zooKeeper,String node,String data) throws InterruptedException, KeeperException {
        System.out.println("开始创建节点"+node+"节点数据为:"+data);
        List<ACL> acl = ZooDefs.Ids.OPEN_ACL_UNSAFE;
        CreateMode createMode = CreateMode.PERSISTENT;
        zooKeeper.create(node,data.getBytes(),acl,createMode);
        System.out.println("zk节点创建成功");
    }
    //删除
    public static void delete(ZooKeeper zooKeeper,String node) throws InterruptedException, KeeperException {
        //先判断节点是否存在
        Stat stat = zooKeeper.exists(node,false);

        System.out.println("开始删除节点:"+node+"原来的版本号是"+ stat.getVersion());
        //int version = -1;
        //-1表示无视版本号
        zooKeeper.delete(node,stat.getVersion());
        System.out.println("zk节点删除成功");
    }
    //修改
    public static void setData(ZooKeeper zooKeeper,String node,String data) throws InterruptedException, KeeperException {
        //先判断节点是否存在
        Stat stat = zooKeeper.exists(node,false);
        System.out.println("开始修改节点:"+node+"原来的版本号是"+ stat.getVersion());
        zooKeeper.setData(node,data.getBytes(),stat.getVersion());
        System.out.println("zk节点修改成功");
    }
    //查询
    public static void getData(ZooKeeper zooKeeper,String node) throws InterruptedException, KeeperException {
        System.out.println("开始查询节点:"+node);
        byte[] bytes =zooKeeper.getData(node,false,null);
        String data = new String(bytes);

        System.out.println("查询到的数据是:"+data);


        Stat st= zooKeeper.exists(node,null);
        System.out.println("查询到的数据是:"+ JSONObject.toJSONString(st));

    }

}