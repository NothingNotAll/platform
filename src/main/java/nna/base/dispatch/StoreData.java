package nna.base.dispatch;


import nna.MetaBean;

/*
 * app 数据存储中心 线程安全
 * */
 class StoreData {

    static private ThreadLocal<MetaBean> configThreadLocal=new ThreadLocal<MetaBean>();

    static void setConfig(MetaBean MetaBean){
        configThreadLocal.set(MetaBean);
    }
    static MetaBean getConfig(){
        return configThreadLocal.get();
    }

}