package nna;


/*
 * app 数据存储中心 线程安全
 * */
 public class StoreData {

    static private ThreadLocal<MetaBean> configThreadLocal=new ThreadLocal<MetaBean>();

    public static void setConfig(MetaBean MetaBean){
        configThreadLocal.set(MetaBean);
    }
    public static MetaBean getConfig(){
        return configThreadLocal.get();
    }

}
