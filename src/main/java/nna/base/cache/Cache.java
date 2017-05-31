package nna.base.cache;

import nna.base.bean.confbean.ConfSession;
import nna.base.bean.confbean.ConfMeta;
import nna.base.util.List;
import nna.base.util.ObjectFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * the core cache of the platform 
 * @author NNA-SHUAI
 * @create 2017-05-13 17:43
 **/

 class Cache {

    static ConcurrentHashMap<String,Integer> srvEnNmToId=new ConcurrentHashMap<String, Integer>();
    static List<ConfMeta> confMetaCache;
    static List<ObjectFactory> objectFactoryCache;
    static ConcurrentHashMap<String,ConfSession> sessions=new ConcurrentHashMap<String, ConfSession>();

    static{

    }

}
