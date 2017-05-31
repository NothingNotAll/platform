package nna.base.cache;

import nna.base.bean.confbean.ConfSession;
import nna.base.bean.confbean.ConfMeta;
import nna.base.util.List;
import nna.base.util.ObjectFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * s
 *
 * @author NNA-SHUAI
 * @create 2017-05-16 19:02
 **/

public class CacheFactory {

    private CacheFactory(){}

    public static List<ConfMeta> getConfMetaCache(){
        return Cache.confMetaCache;
    }

    public static List getObjectFactoryCache() {
        return Cache.objectFactoryCache;
    }

    public static ConcurrentHashMap<String,Integer> getEnToIdCache() {
        return Cache.srvEnNmToId;
    }

    public static List initObjectFactoryCache(int count) {
        Cache.objectFactoryCache=new List<ObjectFactory>(count);
        return Cache.objectFactoryCache;
    }

    public static ConcurrentHashMap<String,ConfSession> initUserSessionCache(int count) {
        Cache.sessions=new ConcurrentHashMap<String,ConfSession>(count);
        return Cache.sessions;
    }

    public static ConcurrentHashMap<String,ConfSession> getUserSessionCache() {
        return Cache.sessions;
    }

    public static List initConfMetaCache(int count) {
        Cache.confMetaCache=new List<ConfMeta>(count);
        return Cache.confMetaCache;
    }

    public static ConcurrentHashMap<String, Integer> initEnToIdCache(int count) {
        Cache.srvEnNmToId=new ConcurrentHashMap<String, Integer>(count);
        return Cache.srvEnNmToId;
    }
}
