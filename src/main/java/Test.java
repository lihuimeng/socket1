import java.util.HashMap;
import java.util.Map;

public class Test {
    static int d=3;
    static{
        System.out.println("我是ClassLoaderProduce类");
    }
    public static void main(String [] args){
        /*int b=0;
        String c="hello";
       int d =  SimpleClass.a ;
       System.out.println(d+"ddd");*/
      //  SimpleClass simpleClass=new SimpleClass();
        //simpleClass.run();
        Map map = new HashMap<String,String>();
        map.put("11","ddd");
        map.put("11","dddww");
        System.out.println(map.size());
    }
}

class SimpleClass{
    static int  a=3;
    static{
        System.out.println(a+"aaa");
        a=100;
        System.out.println(a);
    }

    public SimpleClass(){
        System.out.println("对类进行加载！");
    }

    public void run(){
        System.out.println("我要跑跑跑！");
    }
}