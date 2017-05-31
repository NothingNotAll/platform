package nna.base.protocol.dispatch;

import nna.base.bean.confbean.ConfMeta;

/**
 * s
 *
 * @author NNA-SHUAI
 * @create 2017-05-15 11:05
 **/

public class ConfMetaSetFactory {
    private ConfMetaSetFactory(){}
    public static ConfMeta getConfMeta(){
        return StoreData.getConfig();
    }
    public static void setConfMeta(ConfMeta intimeConfMeta){
        StoreData.setConfig(intimeConfMeta);
    }
}
