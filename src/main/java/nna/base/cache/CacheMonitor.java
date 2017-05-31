package nna.base.cache;

/**
 * @author NNA-SHUAI
 * @create 2017-05-27 11:21
 **/

public class CacheMonitor {
    private static Cache cache;

    public CacheMonitor(Cache cache){
        this.cache=cache;
    }

    public static Cache getCache() {
        return cache;
    }

    public static void setCache(Cache cache) {
        CacheMonitor.cache = cache;
    }
}
