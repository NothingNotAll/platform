package nna.base.protocol.dispatch;


import nna.base.bean.confbean.ConfMeta;

/*
 * app 数据存储中心 线程安全
 * */
 class StoreData {

    static private ThreadLocal<ConfMeta> configThreadLocal=new ThreadLocal<ConfMeta>();

     static void setConfig(ConfMeta confMeta){
        configThreadLocal.set(confMeta);
    }
     static ConfMeta getConfig(){
        return configThreadLocal.get();
    }

}
