package nna.base.init;

import nna.MetaBean;
import nna.base.bean.dbbean.PlatformEntry;
import nna.base.db.DBCon;
import nna.base.util.List;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author NNA-SHUAI
 * @create 2017-05-24 20:21
 **/

public class NNAServiceInit2 {
    public static HashMap<Integer,DBCon> dbConMap=new HashMap<Integer, DBCon>();

    public void build() throws IllegalAccessException, InvocationTargetException, InstantiationException, SQLException, NoSuchMethodException, ClassNotFoundException, IOException {
        int confCacheCount=NNAServiceInit1.platformEntryMap.size();
        MetaBean.setConfMetaCache(new List<MetaBean>(confCacheCount));
        Iterator<Map.Entry<Integer,PlatformEntry>> iterator=NNAServiceInit1.platformEntryMap.entrySet().iterator();
        while(iterator.hasNext()){
            MetaBean metaBean=new MetaBean();
            Map.Entry<Integer,PlatformEntry> entry=iterator.next();
            PlatformEntry platformEntry=entry.getValue();
            metaBean.setPlatformEntry(platformEntry);

        }
    }


}