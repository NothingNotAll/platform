package nna.base.protocol.dispatch;

import nna.MetaBean;

/**
 * s
 *
 * @author NNA-SHUAI
 * @create 2017-05-15 11:05
 **/

public class ConfMetaSetFactory {
    private ConfMetaSetFactory(){}
    public static MetaBean getConfMeta(){
        return StoreData.getConfig();
    }

    public static MetaBeanWrapper getMetaBeanWrapper(){
        return new MetaBeanWrapper(StoreData.getConfig());
    }

    public static void setConfMeta(MetaBean meta){
        StoreData.setConfig(meta);
    }
}
